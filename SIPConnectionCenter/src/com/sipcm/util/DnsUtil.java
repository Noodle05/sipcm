/**
 * 
 */
package com.sipcm.util;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedSet;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.xbill.DNS.Address;
import org.xbill.DNS.Lookup;
import org.xbill.DNS.Record;
import org.xbill.DNS.SRVRecord;
import org.xbill.DNS.TextParseException;
import org.xbill.DNS.Type;

import com.sipcm.sip.IpPort;
import com.sipcm.sip.Protocol;

/**
 * @author wgao
 * 
 */
@Component("dnsUtil")
public class DnsUtil {
	public static final Logger logger = LoggerFactory.getLogger(DnsUtil.class);

	public Map<Protocol, List<IpPort>> lookupSipSrv(String domainName)
			throws TextParseException {
		Map<Protocol, List<IpPort>> result = new EnumMap<Protocol, List<IpPort>>(
				Protocol.class);
		for (Protocol p : Protocol.values()) {
			Lookup lookup = new Lookup(
					p.getDnsQueryPrefix() + "." + domainName, Type.SRV);
			Record[] rs = lookup.run();
			if (rs != null) {
				SortedSet<SRVRecord> srs = new TreeSet<SRVRecord>(
						new Comparator<SRVRecord>() {
							@Override
							public int compare(SRVRecord o1, SRVRecord o2) {
								return o1.getPriority() - o2.getPriority();
							}
						});
				for (Record r : rs) {
					if (r instanceof SRVRecord) {
						final SRVRecord sr = (SRVRecord) r;
						srs.add(sr);
					}
				}
				if (!srs.isEmpty()) {
					List<IpPort> ips = new ArrayList<IpPort>(srs.size());
					for (SRVRecord r : srs) {
						try {
							InetAddress i = Address.getByName(r.getTarget()
									.toString());
							int port = r.getPort();
							IpPort ip = new IpPort(i, port);
							if (!ips.contains(ip)) {
								ips.add(ip);
							}
						} catch (UnknownHostException e) {
							if (logger.isWarnEnabled()) {
								logger.warn(
										"Error happened when lookup ip address for: \"{}\"",
										r.getTarget());
							}
						}
					}
					if (!ips.isEmpty()) {
						result.put(p, ips);
					}
				}
			}
		}
		return result;
	}

	private static void lookup(DnsUtil a, String domain)
			throws TextParseException {
		System.out.println("Domain: " + domain);
		Map<Protocol, List<IpPort>> m = a.lookupSipSrv(domain);
		for (Entry<Protocol, List<IpPort>> e : m.entrySet()) {
			System.out.println("Protocol: " + e.getKey());
			for (IpPort ip : e.getValue()) {
				System.out.println("\t" + ip);
			}
		}
	}

	public static void main(String[] args) {
		DnsUtil a = new DnsUtil();
		try {
			lookup(a, "gizmo5.com");
			lookup(a, "sipgate.com");
			lookup(a, "nonoh.net");
			lookup(a, "localphone.com");
			lookup(a, "ipkall.com");
			lookup(a, "ooma.com");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
