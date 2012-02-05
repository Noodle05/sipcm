/**
 * 
 */
package com.mycallstation.sip.gvverification;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.annotation.Resource;

import org.apache.http.client.ClientProtocolException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.mycallstation.common.AuthenticationException;
import com.mycallstation.dataaccess.business.UserSipProfileService;
import com.mycallstation.dataaccess.model.UserSipProfile;
import com.mycallstation.googlevoice.GoogleVoiceManager;
import com.mycallstation.googlevoice.GoogleVoiceSession;
import com.mycallstation.googlevoice.HttpResponseException;
import com.mycallstation.googlevoice.setting.Phone;

/**
 * @author Wei Gao
 * 
 */
@Component("gvVerficiationManager")
public class GVVerificationManager {
	private static final Logger logger = LoggerFactory
			.getLogger(GVVerificationManager.class);

	@Resource(name = "userSipProfileService")
	private UserSipProfileService userSipProfileService;

	@Resource(name = "googleVoiceManager")
	private GoogleVoiceManager googleVoiceManager;

	private final Map<UserSipProfile, GVVerficationHolder> workingItem;
	private final Lock workingItemReadLock;
	private final Lock workingItemWriteLock;
	private final Random random;

	public GVVerificationManager() {
		workingItem = new HashMap<UserSipProfile, GVVerficationHolder>();
		ReadWriteLock rwl = new ReentrantReadWriteLock();
		workingItemReadLock = rwl.readLock();
		workingItemWriteLock = rwl.writeLock();
		random = new Random();
	}

	public void verifyAccount(Long userId, String username, String password,
			String gvNumber, Phone phone) throws ClientProtocolException,
			SecurityException, IllegalArgumentException, IOException,
			HttpResponseException, AuthenticationException,
			NoSuchMethodException, InstantiationException,
			IllegalAccessException, InvocationTargetException {
		if (userId == null) {
			throw new NullPointerException("User id cannot be null.");
		}
		if (username == null) {
			throw new NullPointerException("Account cannot be null.");
		}
		if (password == null) {
			throw new NullPointerException("Password cannot be null.");
		}
		if (gvNumber == null) {
			throw new NullPointerException(
					"Google Voice number cannot be null.");
		}
		if (phone == null) {
			throw new NullPointerException("Phone cannot be null.");
		}
		if (logger.isDebugEnabled()) {
			logger.debug(
					"Verify account for user id: {}, google voice account: {}, google voice number: {}, verify phone number: {}",
					new Object[] { userId, username, gvNumber,
							phone.getPhoneNumber() });
		}
		UserSipProfile userSipProfile = userSipProfileService
				.getEntityById(userId);
		GVVerficationHolder gvHolder = null;
		if (userSipProfile == null) {
			if (logger.isWarnEnabled()) {
				logger.warn("Cannot find user sip profile by id: {}", userId);
			}
			throw new IllegalArgumentException(
					"Cannot find user sip profile by user id: " + userId);
		}
		GoogleVoiceSession gvSession = googleVoiceManager
				.getGoogleVoiceSession(username, password);
		workingItemWriteLock.lock();
		try {
			if (!workingItem.containsKey(userSipProfile)) {
				int verifyCode = getRandomNumber();
				gvHolder = new GVVerficationHolder(gvSession, gvNumber, phone,
						verifyCode);
				workingItem.put(userSipProfile, gvHolder);
			} else {
				if (logger.isDebugEnabled()) {
					logger.debug("User {} already in verification, cannot do it again.");
				}
				throw new IllegalStateException(
						"User id "
								+ userId
								+ " already in process of verifying google voice phone.");
			}
		} finally {
			workingItemWriteLock.unlock();
		}
		gvSession.verifyForwarding(phone, gvHolder.getVerifyCode());
	}

	public void cancelVerifyAccount(Long userId) {

	}

	public GVVerficationHolder checkInVerify(UserSipProfile userSipProfile) {
		workingItemReadLock.lock();
		try {
			return workingItem.get(userSipProfile);
		} finally {
			workingItemReadLock.unlock();
		}
	}

	private int getRandomNumber() {
		return random.nextInt(100);
	}
}
