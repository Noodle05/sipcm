package com.mycallstation.sip.util;

import java.net.InetAddress;

public interface IpTablesProcessor {

	public boolean postBlockRequest(InetAddress ip);

	public boolean postUnblockIp(InetAddress ip);

	public boolean postRemoveAll();

	public void process();
}
