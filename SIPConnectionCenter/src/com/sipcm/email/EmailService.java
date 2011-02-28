/**
 * 
 */
package com.sipcm.email;

import java.util.Collection;
import java.util.Locale;

import javax.activation.DataSource;
import javax.annotation.Resource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.velocity.app.VelocityEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Component;
import org.springframework.ui.velocity.VelocityEngineUtils;

/**
 * @author Jack
 * 
 */
@Component("globalEmailService")
public class EmailService {
	private static Logger logger = LoggerFactory.getLogger(EmailService.class);

	@Resource(name = "globalMailSender")
	private JavaMailSender mailSender;

	@Resource(name = "globalVelocityEngine")
	private VelocityEngine velocityEngine;

	void sendEmail(final EmailBean emailBean) throws Exception {
		if (logger.isTraceEnabled()) {
			logger.trace("Sending email \"{}\"", emailBean);
		}
		try {
			if (mailSender != null) {
				MimeMessagePreparator preparator = new MimeMessagePreparator() {
					public void prepare(MimeMessage mimeMessage)
							throws MessagingException {
						prepareEmail(mimeMessage, emailBean);
					}
				};

				mailSender.send(preparator);
				if (logger.isTraceEnabled()) {
					logger.trace("Email \"{}\" send out", emailBean);
				}
			}
		} finally {
			emailBean.clearAttachments();
		}
	}

	private void prepareEmail(MimeMessage mimeMessage, EmailBean emailBean)
			throws MessagingException {
		if (logger.isTraceEnabled()) {
			logger.trace("Preparing email");
		}
		if (emailBean == null) {
			if (logger.isDebugEnabled()) {
				logger.debug("Try to send mail with emailBean value null");
			}
			throw new MessagingException("No email bean specified");
		}
		if (CollectionUtils.isEmpty(emailBean.getToAddress())) {
			if (logger.isDebugEnabled()) {
				logger.debug("Can not found to address from emailBean");
			}
			throw new MessagingException("No to address specified");
		}
		if (StringUtils.isEmpty(emailBean.getSubject())) {
			if (logger.isDebugEnabled()) {
				logger.debug("Email subject is empty");
			}
			throw new MessagingException("No subject specified");
		}
		if (StringUtils.isEmpty(emailBean.getBody())) {
			if (logger.isDebugEnabled()) {
				logger.debug("Email body is empty");
			}
			throw new MessagingException("No body specified");
		}

		MimeMessageHelper helper;
		if (StringUtils.isNotEmpty(emailBean.getCharSet())) {
			helper = new MimeMessageHelper(mimeMessage, true,
					emailBean.getCharSet());
		} else {
			helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
		}
		for (String address : emailBean.getToAddress()) {
			helper.addTo(address);
		}

		if (CollectionUtils.isNotEmpty(emailBean.getCcAddress())) {
			for (String address : emailBean.getCcAddress()) {
				helper.addCc(address);
			}
		}
		if (CollectionUtils.isNotEmpty(emailBean.getBccAddress())) {
			for (String address : emailBean.getBccAddress()) {
				helper.addBcc(address);
			}
		}
		helper.setFrom(emailBean.getFromAddress());
		if (StringUtils.isNotEmpty(emailBean.getReplyAddress())) {
			helper.setReplyTo(emailBean.getReplyAddress());
		}
		helper.setSubject(emailBean.getSubject());
		if (emailBean.getPriority() != null) {
			helper.setPriority(emailBean.getPriority().getValue());
		}
		String text;
		if (emailBean.isTemplate()) {
			String temp = null;
			Locale locale = emailBean.getLocale();
			if (locale != null) {
				String base = emailBean.getBody();
				String ext = null;
				int index = base.lastIndexOf('.');
				if (index >= 0) {
					ext = base.substring(index);
					base = base.substring(0, index);
				} else {
					ext = "";
				}
				if (locale.getCountry() != null
						&& locale.getCountry().length() > 0) {
					String s = base + "_" + locale.getLanguage() + "_"
							+ locale.getCountry() + ext;
					if (velocityEngine.resourceExists(s)) {
						temp = s;
					}
				}
				if (temp == null && locale.getLanguage() != null
						&& locale.getLanguage().length() > 0) {
					String s = base + "_" + locale.getLanguage() + ext;
					if (velocityEngine.resourceExists(s)) {
						temp = s;
					}
				}
			}
			if (temp == null) {
				temp = emailBean.getBody();
			}
			text = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine,
					temp, "UTF-8", emailBean.getParams());
		} else {
			text = emailBean.getBody();
		}
		helper.setText(text, emailBean.isHtmlEncoded());

		Collection<DataSource> attachments = emailBean.getAttachments();
		if (attachments != null) {
			for (DataSource ds : attachments) {
				helper.addAttachment(ds.getName(), ds);
			}
		}
		if (logger.isTraceEnabled()) {
			logger.trace("Prepare email done");
		}
	}
}
