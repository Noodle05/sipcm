/**
 * 
 */
package com.mycallstation.sip.util;

import java.io.IOException;
import java.net.InetAddress;
import java.text.MessageFormat;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;

import com.jcraft.jsch.JSchException;

/**
 * @author Wei Gao
 *
 */
@DependsOn("sshExecutor")
public class FirewalldProcessor implements FirewallRuleProcessor {
    private static final Logger logger = LoggerFactory
            .getLogger(FirewalldProcessor.class);

    private String blockOneCommand;
    private String unblockOneCommand;
    private String unblockAllCommand;

    @Resource(name = "systemConfiguration")
    private SipConfiguration appConfig;

    @Resource(name = "sshExecutor")
    private SshExecutor sshExecutor;

    @PostConstruct
    public void init() {
        if (appConfig.isFirewallEnabled()) {
            sshExecutor.init(appConfig.getFirewallHost(),
                    appConfig.getFirewallPort(), appConfig.getFirewallUser(),
                    appConfig.getKnownHostsFile(),
                    appConfig.getPrivateKeyFile(),
                    appConfig.getPasswordPhrase(),
                    appConfig.getSshDisconnectDelay());
            blockOneCommand = appConfig.getFirewalldCommandBlockOne();
            unblockOneCommand = appConfig.getFirewalldCommandUnblockOne();
            unblockAllCommand = appConfig.getFirewalldCommandUnblockAll();
        }
    }

    @Override
    public void removeBlockIp(InetAddress ip) throws JSchException, IOException {
        String command = MessageFormat.format(unblockOneCommand,
                ip.getHostAddress());
        if (issueCommand(command)) {
            logger.info("Rule {} had been removed.", ip.getHostAddress());
        } else {
            logger.warn("Rule {} had not been removed.", ip.getHostAddress());
        }
    }

    @Override
    public void blockIp(InetAddress ip) throws JSchException, IOException {
        String command = MessageFormat.format(blockOneCommand,
                ip.getHostAddress());
        if (issueCommand(command)) {
            logger.info("Rule {} had been added.", ip.getHostAddress());
        } else {
            logger.warn("Rule {} had not been added.", ip.getHostAddress());
        }
    }

    @Override
    public void removeAllBlockIp() throws JSchException, IOException {
        String command = unblockAllCommand;
        if (issueCommand(command)) {
            logger.info("All blocking rule been removed.");
        } else {
            logger.warn("All blocking rule not been removed.");
        }
    }

    private boolean issueCommand(String cmd) throws JSchException, IOException {
        if (logger.isTraceEnabled()) {
            logger.trace("Issue command to firewall: \"{}\"", cmd);
        }
        SshExecuteResult result = sshExecutor.executeCommand(cmd);
        if (result.getExitStatus() == 0) {
            return true;
        } else {
            if (logger.isWarnEnabled()) {
                logger.warn("SshExecutor return error code: {}, Error output:",
                        result.getExitStatus());
                for (String s : result.getError()) {
                    logger.warn("\t{}", s);
                }
            }
            return false;
        }
    }
}
