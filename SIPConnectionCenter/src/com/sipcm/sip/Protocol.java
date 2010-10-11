/**
 * 
 */
package com.sipcm.sip;

/**
 * @author wgao
 * 
 */
public enum Protocol {
	UDP("_sip._udp"), TCP("_sip._tcp"), SCTP("_sip._sctp"), TLS("_sip._tls"), SIPS(
			"_sips._tcp", 5061);

	private String dnsQueryPrefix;
	private int defaultPort;

	private Protocol(String dnsQueryPrefix) {
		this(dnsQueryPrefix, 5060);
	}

	private Protocol(String dnsQueryPrefix, int defaultPort) {
		this.dnsQueryPrefix = dnsQueryPrefix;
		this.defaultPort = defaultPort;
	}

	public String getDnsQueryPrefix() {
		return dnsQueryPrefix;
	}

	public boolean isDefaultPort(int port) {
		return port == defaultPort;
	}
}
