/**
 * 
 */
package com.mycallstation.communication;

import javax.annotation.Resource;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Queue;
import javax.jms.Session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Component;

import com.mycallstation.events.Operation;
import com.mycallstation.events.ServiceEvent;

/**
 * @author wgao
 * 
 */
@Component("userEventMessageSender")
public class UserEventMessageSender {
	private static final Logger logger = LoggerFactory
			.getLogger(UserEventMessageSender.class);
	@Resource(name = "jmsTemplate")
	private JmsTemplate jmsTemplate;

	@Resource(name = "serviceEventQueue")
	private Queue queue;

	public void sendUserCreatedEvent(Long[] useIds) {
		sendUserServiceEvent(Operation.CREATED, useIds);
	}

	public void sendUserModifiedEvent(Long[] useIds) {
		sendUserServiceEvent(Operation.MODIFIED, useIds);
	}

	public void sendUserDeletedEvent(Long[] useIds) {
		sendUserServiceEvent(Operation.DELETED, useIds);
	}

	private void sendUserServiceEvent(final Operation operation,
			final Long[] userIds) {
		if (userIds != null && userIds.length > 0) {
			try {
				jmsTemplate.send(queue, new MessageCreator() {
					@Override
					public Message createMessage(Session session)
							throws JMSException {
						ServiceEvent event = new ServiceEvent(operation,
								userIds);
						return session.createObjectMessage(event);
					}
				});
			} catch (Exception e) {
				if (logger.isWarnEnabled()) {
					logger.warn("Cannot send event to remote end, probably broker is down.");
					if (logger.isDebugEnabled()) {
						logger.debug("Exception stack: ", e);
					}
				}
			}
		}
	}
}
