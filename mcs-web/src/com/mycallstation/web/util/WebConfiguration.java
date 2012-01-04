/**
 * 
 */
package com.mycallstation.web.util;

import org.springframework.stereotype.Component;

import com.mycallstation.common.BaseConfiguration;
import com.mycallstation.constant.ActiveMethod;

/**
 * @author wgao
 * 
 */
@Component("systemConfiguration")
public class WebConfiguration extends BaseConfiguration {
	public static final String USERNAME_LENGTH_MIN = "username.length.min";
	public static final String USERNAME_LENGTH_MAX = "username.length.max";
	public static final String USERNAME_BLACKLIST = "username.blacklist";

	public static final String REGISTER_BY_INVITE_ONLY = "register.by.invite.only";
	public static final String USERNAME_PATTERN = "register.username.pattern";
	public static final String EMAIL_PATTERN = "register.email.pattern";
	public static final String ACTIVE_METHOD = "register.active.method";
	public static final String ACTIVE_EXPIRES = "register.active.expires";
	public static final String ADMIN_EMAIL = "global.admin.email";
	public static final String FROM_EMAIL = "global.from.email";
	public static final String ADMIN_EMAIL_PERSONAL = "global.admin.email.personal";
	public static final String FROM_EMAIL_PERSONAL = "global.from.email.personal";

	public boolean isRegisterByInviteOnly() {
		return appConfig.getBoolean(REGISTER_BY_INVITE_ONLY, true);
	}

	public int getUsernameLengthMin() {
		return appConfig.getInt(USERNAME_LENGTH_MIN, 6);
	}

	public int getUsernameLengthMax() {
		return appConfig.getInt(USERNAME_LENGTH_MAX, 32);
	}

	public String getUsernamePattern() {
		return appConfig.getString(USERNAME_PATTERN,
				"^\\p{Alpha}[\\w|\\.]{5,31}$");
	}

	public String[] getUsernameBlackList() {
		return appConfig.getStringArray(USERNAME_BLACKLIST);
	}

	public String getEmailPattern() {
		return appConfig.getString(EMAIL_PATTERN,
				"^[^@]+@[^@^\\.]+\\.[^@^\\.]+$");
	}

	public ActiveMethod getActiveMethod() {
		String t = appConfig.getString(ACTIVE_METHOD, "SELF");
		try {
			return ActiveMethod.valueOf(t);
		} catch (Exception e) {
			return ActiveMethod.SELF;
		}
	}

	public String getAdminEmail() {
		return appConfig.getString(ADMIN_EMAIL);
	}

	public String getAdminEmailPersonal() {
		return appConfig.getString(ADMIN_EMAIL_PERSONAL);
	}

	public String getFromEmail() {
		return appConfig.getString(FROM_EMAIL);
	}

	public String getFromEmailPersonal() {
		return appConfig.getString(FROM_EMAIL_PERSONAL);
	}

	public int getActiveExpires() {
		return appConfig.getInt(ACTIVE_EXPIRES, 72);
	}
}
