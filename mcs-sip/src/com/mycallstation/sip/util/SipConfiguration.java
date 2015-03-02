/**
 * 
 */
package com.mycallstation.sip.util;

import java.util.Arrays;

import org.springframework.stereotype.Component;

import com.mycallstation.common.BaseConfiguration;
import com.mycallstation.util.PhoneNumberUtil;

/**
 * @author Wei Gao
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

	public static final String IPTABLES_COMMAND_LISTALL_BLOCKING = "firewall.iptables.commands.list.all";
	public static final String IPTABLES_COMMAND_LISTONE_BLOCKING = "firewall.iptables.commands.list.ip";
	public static final String IPTABLES_COMMAND_BLOCK_ONE = "firewall.iptables.commands.block.ip";
	public static final String IPTABLES_COMMAND_UNBLOCK_ONE = "firewall.iptables.commands.unblock.ip";

	public static final String FIREWALLD_COMMAND_BLOCK_ONE = "firewall.firewalld.commands.block.ip";
    public static final String FIREWALLD_COMMAND_UNBLOCK_ONE = "firewall.firewalld.commands.unblock.ip";
    public static final String FIREWALLD_COMMAND_UNBLOCK_ALL = "firewall.firewalld.commands.unblock.all";

	public static final String USE_STUN = "sip.useStun";
	public static final String STUN_SERVER = "sip.stun.server";
	public static final String STUN_PORT = "sip.stun.port";

	public static final String GV_TIMEOUT = "com.mycallstation.googlevoice.timeout";
	public static final String GV_GENERIC_CALLBACK_NUMBER = "com.mycallstation.googlevoice.generic.callback.number";

	public static final String SIP_MIN_EXPIRESTIME = "sip.expirestime.min";
	public static final String SIP_MAX_EXPIRESTIME = "sip.expirestime.max";
	public static final String SIP_REFUSE_BRIEF_REGISTER_REQUEST = "sip.refuse.brief.register.request";

	public static final String SIP_CLIENT_REGISTER_EXPIRES = "sip.client.register.expires";
	public static final String SIP_CLIENT_REGISTER_ALLOW_METHODS = "sip.client.register.allow.methods";
	public static final String SIP_CILENT_REGISTER_RENEW_EXPIRES = "sip.client.register.renew.before.expires";

	public static final String KEEP_ALIVE_TASK_INTERVAL = "phonenumber.keepalive.task.interval";
	public static final String KEEP_ALIVE_TIMEOUT = "phonenumber.keepalive.timeout";
	public static final String KEEP_ALIVE_CONCURRENCY = "phonenumber.keepalive.concurrency";
	public static final String KEEP_ALIVE_ONLINE_ONLY = "phonenumber.keepalive.onlineonly";
	public static final String KEEP_ALIVE_CHAT_TIME = "phonenumber.keepalive.chattime";
	public static final String KEEP_ALIVE_GOOGLEVOICE_TIMEOUT = "phonenumber.keepalive.googlevoicetimeout";

	public static final String MGCP_PEER_ADDRESS = "";
	public static final String MGCP_PEER_PORT = "";
	public static final String MGCP_STACK_NAME = "";
	public static final String MGCP_LOCAL_ADDRESS = "";
	public static final String MGCP_LOCAL_PORT = "";

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

	public String getIpTablesCommandListAll() {
		return appConfig.getString(IPTABLES_COMMAND_LISTALL_BLOCKING);
	}

	public String getIpTablesCommandListOne() {
		return appConfig.getString(IPTABLES_COMMAND_LISTONE_BLOCKING);
	}

	public String getIpTablesCommandBlockOne() {
		return appConfig.getString(IPTABLES_COMMAND_BLOCK_ONE);
	}

	public String getIpTablesCommandUnblockOne() {
		return appConfig.getString(IPTABLES_COMMAND_UNBLOCK_ONE);
	}

    public String getFirewalldCommandBlockOne() {
        return appConfig.getString(FIREWALLD_COMMAND_BLOCK_ONE);
    }

    public String getFirewalldCommandUnblockOne() {
        return appConfig.getString(FIREWALLD_COMMAND_UNBLOCK_ONE);
    }

    public String getFirewalldCommandUnblockAll() {
        return appConfig.getString(FIREWALLD_COMMAND_UNBLOCK_ALL);
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

	public String[] getGoogleVoiceGenericCallbackNumber() {
		String str = appConfig.getString(GV_GENERIC_CALLBACK_NUMBER, "");
		String[] s = null;
		if (str != null && str.length() > 0) {
			s = str.split("\\s*,\\s*");
			if (s.length > 0) {
				for (int i = 0; i < s.length; i++) {
					s[i] = PhoneNumberUtil.getCanonicalizedPhoneNumber(s[i]);
				}
			}
			Arrays.sort(s);
		}
		return s;
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

	public long getKeepAliveTaskInterval() {
		return appConfig.getLong(KEEP_ALIVE_TASK_INTERVAL, 3600000L);
	}

	public int getKeepAliveTimeout() {
		return appConfig.getInt(KEEP_ALIVE_TIMEOUT, 25);
	}

	public int getKeepAliveConcurrency() {
		return appConfig.getInt(KEEP_ALIVE_CONCURRENCY, 5);
	}

	public boolean isKeepAliveOnlineOnly() {
		return appConfig.getBoolean(KEEP_ALIVE_ONLINE_ONLY, true);
	}

	public int getKeepAliveChatTime() {
		return appConfig.getInt(KEEP_ALIVE_CHAT_TIME, 5);
	}

	public int getKeepAliveGoogleVoiceTimeout() {
		return appConfig.getInt(KEEP_ALIVE_GOOGLEVOICE_TIMEOUT, 30);
	}

	public int getMGCPPeerPort() {
		return appConfig.getInt(MGCP_PEER_PORT, 2049);
	}

	public String getMGCPPeerIP() {
		return appConfig.getString(MGCP_PEER_ADDRESS, "127.0.0.1");
	}

	public String getMGCPStackName() {
		return appConfig.getString(MGCP_STACK_NAME, "MediaServer");
	}

	public String getMGCPLocalAddress() {
		return appConfig.getString(MGCP_LOCAL_ADDRESS, "127.0.0.1");
	}

	public int getMGCPLocalPort() {
		return appConfig.getInt(MGCP_LOCAL_PORT, 2050);
	}
}
