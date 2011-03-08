/**
 * 
 */
package com.sipcm.common;

import javax.annotation.Resource;

import org.apache.commons.configuration.Configuration;
import org.jasypt.util.text.StrongTextEncryptor;
import org.jasypt.util.text.TextEncryptor;
import org.springframework.stereotype.Component;

import com.sipcm.util.CodecTool;

/**
 * @author wgao
 * 
 */
@Component("systemConfiguration")
public class SystemConfiguration {
	public static final String DOMAIN_NAME = "domainname";

	public static final String REALM_NAME = "sip.server.realm";
	public static final String MAX_HTTP_CLIENT_TOTAL_CONNECTIONS = "com.sip.http.client.maxConnections";
	public static final String PROCESS_PUBLIC_IP = "sip.publicIp.process";

	public static final String USERNAME_LENGTH_MIN = "username.length.min";
	public static final String USERNAME_LENGTH_MAX = "username.length.max";

	public static final String SIP_DOS_PROTECT_INTERVAL = "sip.dos.protect.interval";
	public static final String SIP_DOS_PROTECT_MAX_REQUESTS = "sip.dos.protect.max.requests";
	public static final String SIP_DOS_PROTECT_BLOCK_TIME = "sip.dos.protect.block.time";

	public static final String FIREWALL_ENABLED = "firewall.enable";
	public static final String FIREWALL_HOST = "firewall.host";
	public static final String FIREWALL_USER = "firewall.user";
	public static final String KNOWN_HOSTS = "firewall.known_hosts";
	public static final String PRIVATE_KEY = "firewall.private_key";
	public static final String FIREWALL_PASSPHRASE = "firewall.password_phrase";
	public static final String SSH_DISCONNECT_DELAY = "firewall.ssh.disconnect.delay";

	public static final String USE_STUN = "sip.useStun";
	public static final String STUN_SERVER = "sip.stun.server";
	public static final String STUN_PORT = "sip.stun.port";

	public static final String USERNAME_PATTERN = "register.username.pattern";
	public static final String EMAIL_PATTERN = "register.email.pattern";
	public static final String ACTIVE_METHOD = "register.active.method";
	public static final String ACTIVE_EXPIRES = "register.active.expires";
	public static final String ADMIN_EMAIL = "global.admin.email";

	public static final String GV_TIMEOUT = "com.sipcm.googlevoice.timeout";

	public static final String SIP_MIN_EXPIRESTIME = "sip.expirestime.min";
	public static final String SIP_MAX_EXPIRESTIME = "sip.expirestime.max";

	public static final String SIP_CLIENT_REGISTER_EXPIRES = "sip.client.register.expires";
	public static final String SIP_CLIENT_REGISTER_ALLOW_METHODS = "sip.client.register.allow.methods";
	public static final String SIP_CLIENT_REGISTER_MINIMUM_INTERVAL = "sip.client.register.interval.minimum";

	@Resource(name = "applicationConfiguration")
	private Configuration appConfig;

	private final TextEncryptor textEncryptor;

	public SystemConfiguration() {
		StrongTextEncryptor encryptor = new StrongTextEncryptor();
		encryptor.setPassword(CodecTool.PASSWORD);
		textEncryptor = encryptor;
	}

	public String getDomain() {
		return appConfig.getString(DOMAIN_NAME);
	}

	public String getRealmName() {
		return appConfig.getString(REALM_NAME);
	}

	public int getMaxHttpClientTotalConnections() {
		return appConfig.getInt(MAX_HTTP_CLIENT_TOTAL_CONNECTIONS, 50);
	}

	public long getDosProtectInterval() {
		return appConfig.getLong(SIP_DOS_PROTECT_INTERVAL, 60L);
	}

	public int getDosProtectMaximumRequests() {
		return appConfig.getInt(SIP_DOS_PROTECT_MAX_REQUESTS, 10);
	}

	public long getDosProtectBlockTime() {
		return appConfig.getLong(SIP_DOS_PROTECT_BLOCK_TIME, 3600L);
	}

	public boolean isProcessPublicIp() {
		return appConfig.getBoolean(PROCESS_PUBLIC_IP, true);
	}

	public String getFirewallHost() {
		return appConfig.getString(FIREWALL_HOST);
	}

	public String getFirewallUser() {
		return appConfig.getString(FIREWALL_USER);
	}

	public String getKnownHostsFile() {
		return appConfig.getString(KNOWN_HOSTS);
	}

	public String getPrivateKeyFile() {
		return appConfig.getString(PRIVATE_KEY);
	}

	public boolean isFirewallEnabled() {
		return appConfig.getBoolean(FIREWALL_ENABLED);
	}

	public String getPasswordPhrase() {
		String password = appConfig.getString(FIREWALL_PASSPHRASE);
		if (password != null) {
			password = textEncryptor.decrypt(password);
		}
		return password;
	}

	public int getSshDisconnectDelay() {
		return appConfig.getInt(SSH_DISCONNECT_DELAY, 60);
	}

	public boolean isUseStun() {
		return appConfig.getBoolean(USE_STUN, true);
	}

	public String getStunServerAddress() {
		return appConfig.getString(STUN_SERVER, "stun.counterpath.com");
	}

	public int getStunServerPort() {
		return appConfig.getInt(STUN_PORT, 3478);
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

	public int getActiveExpires() {
		return appConfig.getInt(ACTIVE_EXPIRES, 72);
	}

	public int getGoogleVoiceCallTimeout() {
		return appConfig.getInt(GV_TIMEOUT, 60);
	}

	public int getSipMinExpires() {
		return appConfig.getInt(SIP_MIN_EXPIRESTIME, 300);
	}

	public int getSipMaxExpires() {
		return appConfig.getInt(SIP_MAX_EXPIRESTIME, 3600);
	}

	public int getSipClientRegisterExpries() {
		return appConfig.getInt(SIP_CLIENT_REGISTER_EXPIRES, 3600);
	}

	public String getSipClientAllowMethods() {
		String ret = null;
		String[] methods = appConfig
				.getStringArray(SIP_CLIENT_REGISTER_ALLOW_METHODS);
		if (methods != null && methods.length > 0) {
			for (String m : methods) {
				if (ret == null) {
					ret = m;
				} else {
					ret = ret + "," + m;
				}
			}
		}
		return ret;
	}

	public int getSipClientMinimumRenewInterval() {
		return appConfig.getInt(SIP_CLIENT_REGISTER_MINIMUM_INTERVAL, 300);
	}
}
