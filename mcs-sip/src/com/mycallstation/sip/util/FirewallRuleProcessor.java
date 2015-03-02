package com.mycallstation.sip.util;

import java.net.InetAddress;

public interface FirewallRuleProcessor {
    public void removeAllBlockIp() throws Exception;

    public void blockIp(InetAddress ip) throws Exception;

    public void removeBlockIp(InetAddress ip) throws Exception;
}
