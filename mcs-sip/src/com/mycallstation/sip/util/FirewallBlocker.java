/**
 * 
 */
package com.mycallstation.sip.util;

import java.net.InetAddress;
import java.util.Iterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.stereotype.Component;

import com.mycallstation.sip.events.BlockIpEvent;
import com.mycallstation.sip.events.BlockIpEventListener;

/**
 * @author Wei Gao
 * 
 */
@Component("sipDosBlockEventListener")
public class FirewallBlocker implements BlockIpEventListener {
    private static final Logger logger = LoggerFactory
            .getLogger(FirewallBlocker.class);

    @Resource(name = "ipTablesBlockProcessor")
    private FirewallRuleProcessor processor;

    @Resource(name = "systemConfiguration")
    private SipConfiguration appConfig;

    @Resource(name = "globalExecutor")
    private AsyncTaskExecutor taskExecutor;

    private final BlockingQueue<Request> requests;

    private boolean firewallEnabled = false;
    private Runnable worker;
    private Future<?> _future;
    private volatile boolean keepWorking;

    public FirewallBlocker() {
        requests = new LinkedBlockingQueue<Request>();
        worker = new Worker();
    }

    @PostConstruct
    public void init() {
        if (appConfig.isFirewallEnabled()) {
            firewallEnabled = true;
        } else {
            firewallEnabled = false;
        }
        keepWorking = true;
        _future = taskExecutor.submit(worker);
        postRemoveAll();
    }

    @PreDestroy
    public void destroy() {
        keepWorking = false;
        requests.offer(new EndingRequest());
        try {
            _future.get();
        } catch (InterruptedException | ExecutionException e) {
            logger.error("Error happened during firewall blocker work.", e);
        }
    }

    private void postRemoveAll() {
        if (logger.isTraceEnabled()) {
            logger.trace("Remove all existing requests.");
        }
        requests.clear();
        requests.offer(new Request(RequestType.REMOVEALL, null));
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.mycallstation.sip.events.BlockIpEventListener#blockIp(com.mycallstation
     * .sip.events .BlockIpEventObject)
     */
    @Override
    public void blockIp(BlockIpEvent event) {
        if (firewallEnabled) {
            InetAddress ip = event.getIp();
            postBlockRequest(ip);
        }
    }

    private void postBlockRequest(InetAddress ip) {
        if (ip != null) {
            Iterator<Request> ite = requests.iterator();
            while (ite.hasNext()) {
                Request r = ite.next();
                if (ip.equals(r.ip)) {
                    if (logger.isTraceEnabled()) {
                        logger.trace(
                                "Find existing request for ip \"{}\", remove it.",
                                ip);
                    }
                    ite.remove();
                }
            }
            requests.offer(new Request(RequestType.BLOCK, ip));
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.mycallstation.sip.events.BlockIpEventListener#unblockIp(com.mycallstation
     * .sip.events .BlockIpEventObject)
     */
    @Override
    public void unblockIp(BlockIpEvent event) {
        if (firewallEnabled) {
            InetAddress ip = event.getIp();
            postUnblockIp(ip);
        }
    }

    private void postUnblockIp(InetAddress ip) {
        if (ip != null) {
            Iterator<Request> ite = requests.iterator();
            while (ite.hasNext()) {
                Request r = ite.next();
                if (ip.equals(r.ip)) {
                    if (logger.isTraceEnabled()) {
                        logger.trace(
                                "Find existing request for ip \"{}\", remove it.",
                                ip);
                    }
                    ite.remove();
                }
            }
            requests.offer(new Request(RequestType.UNBLOCK, ip));
        }
    }

    private void processRequest() {
        while (keepWorking) {
            Request request = null;
            try {
                request = requests.take();
                if (request instanceof EndingRequest) {
                    break;
                }
                if (logger.isTraceEnabled()) {
                    logger.trace("Processing request: \"{}\"", request);
                }
                switch (request.requestType) {
                case REMOVEALL:
                    processor.removeAllBlockIp();
                    break;
                case BLOCK:
                    processor.blockIp(request.ip);
                    break;
                case UNBLOCK:
                    processor.removeBlockIp(request.ip);
                    break;
                }
            } catch (InterruptedException e) {
                logger.info("Firewall Blocker thread interrupted.");
                break;
            } catch (Exception e) {
                logger.warn(
                        "Error happened when process firewall block request: {}",
                        request, e);
            }
        }
    }

    private class Worker implements Runnable {
        @Override
        public void run() {
            processRequest();
        }
    }

    private enum RequestType {
        BLOCK, UNBLOCK, REMOVEALL;
    }

    private class Request {
        private RequestType requestType;
        private InetAddress ip;

        private Request(RequestType requestType, InetAddress ip) {
            this.requestType = requestType;
            this.ip = ip;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb = sb.append("Request[type=").append(requestType);
            if (ip != null) {
                sb = sb.append(",ip=").append(ip.getHostAddress());
            }
            sb = sb.append("]");
            return sb.toString();
        }
    }

    private final class EndingRequest extends Request {
        private EndingRequest() {
            super(null, null);
        }
    }
}
