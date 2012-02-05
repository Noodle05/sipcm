/**
 * 
 */
package com.mycallstation.googlevoice.result;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Wei Gao
 * 
 */
public class CheckIllegalSharingResult extends CallResult {
	private static final long serialVersionUID = 969478788977311322L;

	private static final Logger logger = LoggerFactory
			.getLogger(CheckIllegalSharingResult.class);

	public static final Pattern resultPattern = Pattern
			.compile(
					"^\\s*\"needReclaim\"\\:\\s*(false|true)\\s*\"reclaimCheckResult\"\\:\\s*(\\d+)\\s*$",
					Pattern.DOTALL + Pattern.CASE_INSENSITIVE);

	private final boolean needReclaim;
	private final int reclaimCheckResult;

	public CheckIllegalSharingResult(String string) {
		super(string);
		if (success) {
			Matcher m = resultPattern.matcher(rawData);
			if (m.matches()) {
				if ("true".equalsIgnoreCase(m.group(1))) {
					needReclaim = true;
				} else {
					needReclaim = false;
				}
				reclaimCheckResult = Integer.parseInt(m.group(2));
			} else {
				needReclaim = false;
				reclaimCheckResult = 0;
			}
		} else {
			if (logger.isTraceEnabled()) {
				logger.trace("Result is failed.");
			}
			needReclaim = false;
			reclaimCheckResult = 0;
		}
	}

	/**
	 * @return the needReclaim
	 */
	public boolean isNeedReclaim() {
		return needReclaim;
	}

	/**
	 * @return the reclaimCheckResult
	 */
	public int getReclaimCheckResult() {
		return reclaimCheckResult;
	}

}
