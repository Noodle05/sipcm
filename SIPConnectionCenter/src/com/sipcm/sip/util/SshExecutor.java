/**
 * 
 */
package com.sipcm.sip.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.concurrent.Future;

import javax.annotation.PreDestroy;
import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

/**
 * @author wgao
 * 
 */
@Component("sshExecutor")
@Scope("prototype")
public class SshExecutor {
	private static final Logger logger = LoggerFactory
			.getLogger(SshExecutor.class);

	@Resource(name = "global.scheduler")
	private TaskScheduler scheduler;

	private final Runnable disconnectTask;
	private final JSch jsch;
	private volatile Session jschSession;
	private volatile Future<Void> disconnectTaskFuture;

	private String host;
	private String username;
	private String knownHosts;
	private String privateKey;
	private String passwordPhrase;
	private int disconnectDelay;
	private boolean initialized;

	public SshExecutor() {
		disconnectTask = new DisconnectTask();

		jsch = new JSch();
	}

	private void checkInitialize() {
		if (!initialized) {
			throw new BeanInitializationException(
					"SshExecutor not initialize yet.");
		}
	}

	public void init(String host, String username, String knownHosts,
			String privateKey, String passwordPhrase, int disconnectDelay) {
		this.host = host;
		this.username = username;
		this.knownHosts = knownHosts;
		this.privateKey = privateKey;
		this.passwordPhrase = passwordPhrase;
		this.disconnectDelay = disconnectDelay;
		try {
			jsch.addIdentity(this.privateKey, this.passwordPhrase);
			jsch.setKnownHosts(this.knownHosts);
		} catch (JSchException e) {
			throw new BeanInitializationException(
					"Error happened when initial ssh client.", e);
		}
		initialized = true;
	}

	@PreDestroy
	public void destroy() {
		if (initialized) {
			cancelDisconnectSessionTask();
			if (jschSession != null && jschSession.isConnected()) {
				jschSession.disconnect();
				jschSession = null;
			}
		}
	}

	private void connectToHost() throws JSchException {
		cancelDisconnectSessionTask();
		if (jschSession == null) {
			jschSession = jsch.getSession(username, host);
		}
		if (!jschSession.isConnected()) {
			if (logger.isDebugEnabled()) {
				logger.debug("Connecting to \"{}\" as user: \"{}\"", host,
						username);
			}
			jschSession.connect();
		}
	}

	public SshExecuteResult executeCommand(String command)
			throws JSchException, IOException {
		checkInitialize();
		if (logger.isDebugEnabled()) {
			logger.debug("Executing command: \"{}\"", command);
		}
		connectToHost();
		try {
			ChannelExec channel = (ChannelExec) jschSession.openChannel("exec");
			channel.setCommand(command);
			InputStream is = channel.getInputStream();
			InputStream es = channel.getErrStream();
			BufferedReader ir = new BufferedReader(new InputStreamReader(is));
			BufferedReader er = new BufferedReader(new InputStreamReader(es));

			channel.connect();
			try {
				Collection<String> output = new ArrayList<String>();
				Collection<String> error = new ArrayList<String>();
				String tmp;
				while (true) {
					while ((tmp = ir.readLine()) != null) {
						output.add(tmp);
						if (logger.isTraceEnabled()) {
							logger.trace("Server return output line: \"{}\".",
									tmp);
						}
					}
					while ((tmp = er.readLine()) != null) {
						error.add(tmp);
						if (logger.isTraceEnabled()) {
							logger.trace("Server return error line: \"{}\".",
									tmp);
						}
					}
					if (channel.isClosed()) {
						if (logger.isTraceEnabled()) {
							logger.trace("Server return exit status: {}",
									channel.getExitStatus());
						}
						SshExecuteResult result = new SshExecuteResult(
								channel.getExitStatus(), output, error);
						return result;
					}
					try {
						Thread.sleep(1000L);
					} catch (Exception ee) {
					}
				}
			} finally {
				try {
					ir.close();
					er.close();
				} catch (IOException e) {
					if (logger.isWarnEnabled()) {
						logger.warn(
								"Error happened when close output/error input stream.",
								e);
					}
				} finally {
					channel.disconnect();
				}
			}
		} finally {
			disconnectFromHost();
		}
	}

	@SuppressWarnings("unchecked")
	private void disconnectFromHost() {
		cancelDisconnectSessionTask();
		Calendar c = Calendar.getInstance();
		c.add(Calendar.SECOND, disconnectDelay);
		if (logger.isTraceEnabled()) {
			logger.trace("Schedule disconnect task at \"{}\".", c.getTime());
		}
		disconnectTaskFuture = scheduler.schedule(disconnectTask, c.getTime());
	}

	private void cancelDisconnectSessionTask() {
		if (disconnectTaskFuture != null
				&& !(disconnectTaskFuture.isCancelled() || disconnectTaskFuture
						.isDone())) {
			if (logger.isTraceEnabled()) {
				logger.trace("Cancel disconnect task.");
			}
			disconnectTaskFuture.cancel(false);
		}
	}

	private class DisconnectTask implements Runnable {
		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Runnable#run()
		 */
		@Override
		public void run() {
			if (logger.isDebugEnabled()) {
				logger.debug("Disconnecting from host: \"{}\"", host);
			}
			jschSession.disconnect();
			jschSession = null;
		}
	}
}
