/**
 * 
 */
package com.mycallstation.sip.nat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author wgao
 * 
 */
public class NATKeepAliveTask {
	private static final Logger logger = LoggerFactory
			.getLogger(NATKeepAliveTask.class);

	// private byte[] buf = new byte[] { (byte) 0x00, (byte) 0x00, (byte) 0x00,
	// (byte) 0x00 };

	// @Resource(name = "sip.LocationService")
	// private LocationService locationService;

	// @Scheduled(fixedRate = 30000L)
	public void ping() {
		if (logger.isDebugEnabled()) {
			logger.debug("Start ping remote ends.");
		}
		// try {
		// Collection<Binding> remotes = locationService.getAllRemoteEnd();
		// Map<SocketAddress, Collection<SocketAddress>> pingMap = new
		// HashMap<SocketAddress, Collection<SocketAddress>>();
		// for (Binding binding : remotes) {
		// URI uri = binding.getRemoteEnd().getURI();
		// if (uri.isSipURI()) {
		// final SipURI sipURI = (SipURI) uri;
		// SocketAddress laddr = binding.getLaddr();
		// Collection<SocketAddress> raddrs = pingMap.get(laddr);
		// if (raddrs == null) {
		// raddrs = new HashSet<SocketAddress>();
		// pingMap.put(laddr, raddrs);
		// }
		// SocketAddress raddr = new InetSocketAddress(
		// sipURI.getHost(), sipURI.getPort());
		// raddrs.add(raddr);
		// }
		// }
		// DatagramPacket packet;
		// DatagramSocket socket = null;
		// for (Entry<SocketAddress, Collection<SocketAddress>> entry : pingMap
		// .entrySet()) {
		// SocketAddress laddr = entry.getKey();
		// try {
		// if (logger.isTraceEnabled()) {
		// logger.trace("Ping remote from local socket: {}", laddr);
		// }
		// socket = new DatagramSocket(laddr);
		// for (SocketAddress raddr : entry.getValue()) {
		// try {
		// if (logger.isTraceEnabled()) {
		// logger.trace(
		// "Sending ping UDP packet for remote socket: {}",
		// raddr);
		// }
		// packet = new DatagramPacket(buf, buf.length, raddr);
		// socket.send(packet);
		// } catch (SocketException e) {
		// if (logger.isErrorEnabled()) {
		// logger.error(
		// "Cannot create packet for remote socket: "
		// + raddr, e);
		// }
		// } catch (IOException e) {
		// if (logger.isErrorEnabled()) {
		// logger.error(
		// "Error happened when sending ping packet to remote socket: "
		// + raddr, e);
		// }
		// }
		// }
		// } catch (SocketException e) {
		// if (logger.isErrorEnabled()) {
		// logger.error(
		// "Cannot create UDP socket for local socket for {}"
		// + laddr, e);
		// }
		// } finally {
		// if (socket != null) {
		// socket.close();
		// }
		// }
		// }
		// } catch (Throwable e) {
		// if (logger.isErrorEnabled()) {
		// logger.error("Error happened in NAT keep alive task.", e);
		// }
		// } finally {
		// if (logger.isDebugEnabled()) {
		// logger.debug("Ping remote ends done.");
		// }
		// }
	}
}
