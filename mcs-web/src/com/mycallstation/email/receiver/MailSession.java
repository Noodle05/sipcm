/**
 * 
 */
package com.mycallstation.email.receiver;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.mail.Folder;
import javax.mail.FolderNotFoundException;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.event.MessageCountEvent;
import javax.mail.event.MessageCountListener;
import javax.mail.search.AndTerm;
import javax.mail.search.FromStringTerm;
import javax.mail.search.SearchTerm;
import javax.mail.search.SubjectTerm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.sun.mail.iap.ProtocolException;
import com.sun.mail.imap.IMAPFolder;
import com.sun.mail.imap.protocol.IMAPProtocol;

/**
 * @author wgao
 * 
 */
@Component("mailSession")
@Scope("prototype")
public class MailSession {
	private static final Logger logger = LoggerFactory
			.getLogger(MailSession.class);
	private String host;
	private String protocol;

	private String account;
	private String password;
	private volatile IMAPFolder waitingFolder;
	private volatile boolean stop = false;
	private volatile CountDownLatch endSignal;
	private final AtomicBoolean waiting = new AtomicBoolean(false);

	public void searchEmailBySenderAndSubjet(String sender, String subject,
			MessageOperation operation, String... folders)
			throws MessagingException, IOException {
		if (operation == null) {
			throw new NullPointerException("Operation cannot ben null.");
		}
		if (sender == null && subject == null) {
			throw new IllegalArgumentException(
					"Sender and subject cannot both be null.");
		}
		if (logger.isDebugEnabled()) {
			if (folders != null) {
				logger.debug(
						"Search email with sender: {}, subject: {} in folder(s): {}",
						new Object[] { sender, subject,
								Arrays.toString(folders) });
			} else {
				logger.debug(
						"Search email with sender: {}, subject: {} in all folders.",
						sender, subject);
			}
		}
		Properties props = new Properties();
		Session session = Session.getDefaultInstance(props, null);

		Store store = session.getStore(protocol);
		store.connect(host, account, password);
		try {
			List<Folder> fs;
			if (folders == null) {
				if (logger.isTraceEnabled()) {
					logger.trace("Getting all folder list.");
				}
				Folder[] flds = store.getDefaultFolder().list();
				fs = getAllFolders(flds);
				if (logger.isTraceEnabled()) {
					logger.trace("Folder list. {}",
							Arrays.toString(fs.toArray(new Folder[fs.size()])));
				}
			} else {
				fs = new ArrayList<Folder>(folders.length);
				for (String f : folders) {
					fs.add(store.getFolder(f));
				}
			}
			SearchTerm st = null;
			if (sender != null) {
				st = new FromStringTerm(sender);
			}
			if (subject != null) {
				if (st != null) {
					st = new AndTerm(st, new SubjectTerm(subject));
				} else {
					st = new SubjectTerm(subject);
				}
			}
			for (Folder folder : fs) {
				if (folder.exists()) {
					try {
						folder.open(Folder.READ_ONLY);
						Message[] messages = folder.search(st);
						if (messages != null && messages.length > 0) {
							if (logger.isTraceEnabled()) {
								logger.trace(
										"Found {} messages in folder {}, calling operation...",
										messages.length, folder);
							}
							try {
								operation.execute(messages);
							} catch (Throwable e) {
								if (logger.isWarnEnabled()) {
									logger.warn(
											"Error happened when process messages for search result of folder; "
													+ folder.getName(), e);
								}
							}
						} else {
							if (logger.isTraceEnabled()) {
								logger.trace("No message found in folder: {}",
										folder);
							}
						}
					} catch (MessagingException e) {
						if (logger.isDebugEnabled()) {
							logger.debug(
									"Error happened when search in folder: "
											+ folder.getName()
											+ ", contiue to next.", e);
						}
					} finally {
						try {
							folder.close(false);
						} catch (Throwable e) {
							if (logger.isDebugEnabled()) {
								logger.debug(
										"Error happened when close folder "
												+ folder + ", ignore it.", e);
							}
						}
					}
				} else {
					if (logger.isDebugEnabled()) {
						logger.debug("Folder {} doesn't exists, skip it.",
								folder);
					}
				}
			}
		} finally {
			try {
				store.close();
			} catch (Throwable e) {
				if (logger.isDebugEnabled()) {
					logger.debug("Error happened when close store, ignore it.",
							e);
				}
			}
		}
	};

	public void waitingForNewEmailBySenderAndSubject(final String folderName,
			final String sender, final String subject,
			final MessageOperation operation) throws MessagingException {
		if (folderName == null) {
			throw new NullPointerException("folder name cannot be null.");
		}
		if (operation == null) {
			throw new NullPointerException("Operation cannot be null.");
		}
		if (sender == null && subject == null) {
			throw new IllegalArgumentException(
					"Sender and subject cannot both be null.");
		}
		if (waiting.compareAndSet(false, true)) {
			endSignal = new CountDownLatch(1);
			stop = false;
			try {
				if (logger.isDebugEnabled()) {
					logger.debug(
							"Waiting for email from {}, with subject {} in folder {}",
							new Object[] { sender, subject, folderName });
				}
				Properties props = new Properties();
				Session session = Session.getDefaultInstance(props, null);

				Store store = session.getStore(protocol);
				store.connect(host, account, password);

				SearchTerm st = null;
				if (sender != null) {
					st = new FromStringTerm(sender);
				}
				if (subject != null) {
					if (st != null) {
						st = new AndTerm(st, new SubjectTerm(subject));
					} else {
						st = new SubjectTerm(subject);
					}
				}

				final SearchTerm searchTerm = st;

				MessageCountListener listener = new MessageCountListener() {
					@Override
					public void messagesRemoved(MessageCountEvent event) {
					}

					@Override
					public void messagesAdded(MessageCountEvent event) {
						if (logger.isTraceEnabled()) {
							logger.trace("Get new message.");
						}
						try {
							Collection<Message> msgs = Arrays.asList(event
									.getMessages());
							Iterator<Message> ite = msgs.iterator();
							while (ite.hasNext()) {
								Message msg = ite.next();
								if (!msg.match(searchTerm)) {
									if (logger.isTraceEnabled()) {
										logger.trace("This message doesn't match, remove it.");
									}
									ite.remove();
								}
							}
							if (!msgs.isEmpty()) {
								if (logger.isTraceEnabled()) {
									logger.trace(
											"Get {} messages match search condition, call operation.",
											msgs.size());
								}
								operation.execute(msgs.toArray(new Message[msgs
										.size()]));
							} else {
								if (logger.isTraceEnabled()) {
									logger.trace("No message match search term.");
								}
							}
						} catch (Throwable e) {
							if (logger.isWarnEnabled()) {
								logger.warn(
										"Error happened when handling message been added.",
										e);
							}
						}
					}
				};
				try {
					Folder folder = store.getFolder(folderName);
					if (folder instanceof IMAPFolder) {
						final IMAPFolder imapFolder = (IMAPFolder) folder;
						imapFolder.open(Folder.READ_ONLY);
						try {
							imapFolder.addMessageCountListener(listener);
							waitingFolder = imapFolder;
							while (!stop) {
								if (logger.isTraceEnabled()) {
									logger.trace("Wait... for push message.");
								}
								imapFolder.idle();
								if (logger.isTraceEnabled()) {
									logger.trace("Wake up.");
								}
							}
							waitingFolder = null;
						} finally {
							try {
								imapFolder.close(false);
							} catch (Throwable e) {
								if (logger.isDebugEnabled()) {
									logger.debug(
											"Error happened when close folder "
													+ imapFolder
													+ ", ignore it.", e);
								}
							}
						}
					} else {
						throw new FolderNotFoundException(folder,
								"Folder is not IMAP folder.");
					}
				} finally {
					try {
						store.close();
					} catch (Throwable e) {
						if (logger.isDebugEnabled()) {
							logger.debug(
									"Error happened when close store, ignore it.",
									e);
						}
					}
				}
			} finally {
				endSignal.countDown();
				waiting.set(false);
			}
		} else {
			if (logger.isDebugEnabled()) {
				logger.debug("Already waiting on folder.");
			}
		}
	}

	public void stopWaiting() throws InterruptedException {
		if (logger.isDebugEnabled()) {
			logger.debug("Stop waiting thread.");
		}
		if (waiting.get()) {
			stop = true;
			if (waitingFolder != null) {
				if (logger.isTraceEnabled()) {
					logger.trace(
							"Folder {} still open for waiting, send a NOOP command to terminate IDLE.",
							waitingFolder);
				}
				try {
					waitingFolder
							.doCommandIgnoreFailure(new IMAPFolder.ProtocolCommand() {
								@Override
								public Object doCommand(IMAPProtocol p)
										throws ProtocolException {
									p.simpleCommand("NOOP", null);
									return null;
								}
							});
				} catch (Throwable e) {
					if (logger.isDebugEnabled()) {
						logger.debug(
								"Error happened when sending NOOP command to folder "
										+ waitingFolder + ", just ignore it.",
								e);
					}
				}
			}
			if (endSignal != null) {
				if (logger.isTraceEnabled()) {
					logger.trace("Waiting for waiting thread exist.");
				}
				endSignal.await();
			}
		}
	}

	private List<Folder> getAllFolders(Folder... folders)
			throws MessagingException {
		List<Folder> ret = new ArrayList<Folder>(1);
		for (Folder folder : folders) {
			ret.add(folder);
			Folder[] fs = folder.list();
			ret.addAll(getAllFolders(fs));
		}
		return ret;
	}

	/**
	 * @return the host
	 */
	public String getHost() {
		return host;
	}

	/**
	 * @param host
	 *            the host to set
	 */
	public void setHost(String host) {
		this.host = host;
	}

	/**
	 * @return the protocol
	 */
	public String getProtocol() {
		return protocol;
	}

	/**
	 * @param protocol
	 *            the protocol to set
	 */
	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	/**
	 * @return the account
	 */
	public String getAccount() {
		return account;
	}

	/**
	 * @param account
	 *            the account to set
	 */
	public void setAccount(String account) {
		this.account = account;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param password
	 *            the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}
}
