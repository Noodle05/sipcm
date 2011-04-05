/**
 * 
 */
package com.mycallstation.sip.util;

import javax.annotation.Resource;

import org.apache.commons.configuration.Configuration;
import org.jasypt.util.text.StrongTextEncryptor;
import org.jasypt.util.text.TextEncryptor;
import org.springframework.stereotype.Component;

import com.mycallstation.common.BaseConfiguration;
import com.mycallstation.util.CodecTool;

/**
 * @author wgao
 * 
 */
@Component("systemConfiguration")
public class SipConfiguration extends BaseConfiguration {

	public static final String PROCESS_PUBLIC_IP = "sip.publicIp.process";

	public static final String SIP_DOS_PROTECT_INTERVAL = "sip.dos.protect.interval";
	public static final String SIP_DOS_PROTECT_MAX_REQUESTS = "sip.dos.protect.max.requests";
	public static final String SIP_DOS_PROTECT_BLOCK_TIME = "sip.dos.protect.block.time";

	public static final String FIREWALL_ENABLED = "firewall.enable";
	public static final String FIREWALL_HOST = "firewall.host";
	public static final String FIREWALL_PORT = "firewall.port";
	public static final String FIREWALL_USER = "firewall.user";
	public static final String KNOWN_HOSTS = "firewall.known_hosts";
	public static final String PRIVATE_KEY = "firewall.private_key";
	public static final String FIREWALL_PASSPHRASE = "firewall.password_phrase";
	public static final String SSH_DISCONNECT_DELAY = "firewall.ssh.disconnect.delay";

	public static final String FIREWALL_COMMAND_LISTALL_BLOCKING = "firewall.commands.list.all";
	public static final String FIREWALL_COMMAND_LISTONE_BLOCKING = "firewall.commands.list.ip";
	public static final String FIREWALL_COMMAND_BLOCK_ONE = "firewall.commands.block.ip";
	public static final String FIREWALL_COMMAND_UNBLOCK_ONE = "firewall.commands.unblock.ip";

	public static final String USE_STUN = "sip.useStun";
	public static final String STUN_SERVER = "sip.stun.server";
	public static final String STUN_PORT = "sip.stun.port";

	public static final String GV_TIMEOUT = "com.mycallstation.googlevoice.timeout";

	public static final String SIP_MIN_EXPIRESTIME = "sip.expirestime.min";
	public static final String SIP_MAX_EXPIRESTIME = "sip.expirestime.max";
	public static final String SIP_REFUSE_BRIEF_REGISTER_REQUEST = "sip.refuse.brief.register.request";

	public static final String SIP_CLIENT_REGISTER_EXPIRES = "sip.client.register.expires";
	public static final String SIP_CLIENT_REGISTER_ALLOW_METHODS = "sip.client.register.allow.methods";
	public static final String SIP_CILENT_REGISTER_RENEW_EXPIRES = "sip.client.register.renew.before.expires";

	@Resource(name = "applicationConfiguration")
	private Configuration appConfig;

	private final TextEncryptor textEncryptor;

	public SipConfiguration() {
		StrongTextEncryptor encryptor = new StrongTextEncryptor();
		encryptor.setPassword(CodecTool.PASSWORD);
		textEncryptor = encryptor;
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

	public int getFirewallPort() {
		return appConfig.getInt(FIREWALL_PORT, 0);
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

	public String getFirewallCommandListAll() {
		return appConfig.getString(FIREWALL_COMMAND_LISTALL_BLOCKING);
	}

	public String getFirewallCommandListOne() {
		return appConfig.getString(FIREWALL_COMMAND_LISTONE_BLOCKING);
	}

	public String getFirewallCommandBlockOne() {
		return appConfig.getString(FIREWALL_COMMAND_BLOCK_ONE);
	}

	public String getFirewallCommandUnblockOne() {
		return appConfig.getString(FIREWALL_COMMAND_UNBLOCK_ONE);
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

	public int getSipClientRenewBeforeExpires() {
		return appConfig.getInt(SIP_CILENT_REGISTER_RENEW_EXPIRES, 60);
	}

	public boolean isRefuseBriefRegister() {
		return appConfig.getBoolean(SIP_REFUSE_BRIEF_REGISTER_REQUEST, true);
	}
}
