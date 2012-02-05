/**
 * 
 */
package com.mycallstation.googlevoice.message;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mycallstation.googlevoice.result.CallResult;
import com.mycallstation.googlevoice.util.Utility;

/**
 * @author Wei Gao
 * 
 */
public class CheckMessageResult extends CallResult {
	private static final long serialVersionUID = 6831091780819592679L;

	private static final Logger logger = LoggerFactory
			.getLogger(CheckMessageResult.class);

	public static final Pattern resultPattern = Pattern.compile(
			"^\\s*\"data\"\\:\\s*\\{\"unreadCounts\"\\:(\\{.*\\})\\}\\s*$",
			Pattern.DOTALL + Pattern.CASE_INSENSITIVE);

	private final MessageInfo messageInfo;

	public CheckMessageResult(String string) {
		super(string);
		if (success) {
			Matcher m = resultPattern.matcher(rawData);
			if (m.matches()) {
				String str = m.group(1);
				if (str != null) {
					if (logger.isTraceEnabled()) {
						logger.trace("Unread count string: {}", str);
					}
					messageInfo = Utility.getGson().fromJson(str,
							MessageInfo.class);
				} else {
					if (logger.isTraceEnabled()) {
						logger.trace("Unread count is empty.");
					}
					messageInfo = null;
				}
			} else {
				if (logger.isTraceEnabled()) {
					logger.trace("Cannot find Unread count.");
				}
				messageInfo = null;
			}
		} else {
			if (logger.isTraceEnabled()) {
				logger.trace("Result is failed.");
			}
			messageInfo = null;
		}
	}

	public MessageInfo getMessageInfo() {
		return messageInfo;
	}
}
