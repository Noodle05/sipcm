/**
 * 
 */
package com.mycallstation.sip.util;

import java.io.IOException;
import java.net.InetAddress;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
public class IpTablesRuleProcessor implements FirewallRuleProcessor {
    private static final Logger logger = LoggerFactory
            .getLogger(IpTablesRuleProcessor.class);

    private static final String IP_ELEMENT = "(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)";
    private static final String IPADDR = IP_ELEMENT + "\\." + IP_ELEMENT
            + "\\." + IP_ELEMENT + "\\." + IP_ELEMENT;
    private static final Pattern rule = Pattern.compile("(\\d+)\\:(" + IPADDR
            + ")");

    private String listAllBlockingCommand;
    private String listOneBlockingCommand;
    private String unblockOneCommand;
    private String blockOneCommand;

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
            listAllBlockingCommand = appConfig.getIpTablesCommandListAll();
            listOneBlockingCommand = appConfig.getIpTablesCommandListOne();
            blockOneCommand = appConfig.getIpTablesCommandBlockOne();
            unblockOneCommand = appConfig.getIpTablesCommandUnblockOne();
        }
    }

    @Override
    public void removeAllBlockIp() throws JSchException, IOException {
        removeBlockIp(null);
    }

    @Override
    public void removeBlockIp(InetAddress ip) throws JSchException, IOException {
        Map<Integer, InetAddress> rules = getRuleNumberByIp(null);
        if (rules != null) {
            if (rules.size() > 1) {
                SortedMap<Integer, InetAddress> sortedRules = new TreeMap<Integer, InetAddress>(
                        new Comparator<Integer>() {
                            @Override
                            public int compare(Integer o1, Integer o2) {
                                return o2 - o1;
                            }
                        });
                sortedRules.putAll(rules);
                rules = sortedRules;
            }
            for (Entry<Integer, InetAddress> entry : rules.entrySet()) {
                Integer ruleNumber = entry.getKey();
                InetAddress i = entry.getValue();
                if (logger.isTraceEnabled()) {
                    logger.trace("Find rule: {} which block ip: \"{}\"",
                            ruleNumber, i.getHostAddress());
                }
                removeRule(ruleNumber);
            }
        }
    }

    private void removeRule(int ruleNumber) throws JSchException, IOException {
        String command = MessageFormat.format(unblockOneCommand, ruleNumber);
        if (logger.isTraceEnabled()) {
            logger.trace("Issue command to firewall: \"{}\"", command);
        }
        SshExecuteResult result = sshExecutor.executeCommand(command);
        if (result.getExitStatus() == 0) {
            if (logger.isInfoEnabled()) {
                logger.info("Rule {} had been removed.", ruleNumber);
            }
        } else {
            if (logger.isWarnEnabled()) {
                logger.warn("SshExecutor return error code: {}, Error output:",
                        result.getExitStatus());
                for (String s : result.getError()) {
                    logger.warn("\t{}", s);
                }
            }
        }
    }

    @Override
    public void blockIp(InetAddress ip) throws JSchException, IOException {
        Map<Integer, InetAddress> existingRule = getRuleNumberByIp(ip);
        if (existingRule == null || existingRule.isEmpty()) {
            String command = MessageFormat.format(blockOneCommand,
                    ip.getHostAddress());
            if (logger.isTraceEnabled()) {
                logger.trace("Issue command to firewall: \"{}\"", command);
            }
            SshExecuteResult result = sshExecutor.executeCommand(command);
            if (result.getExitStatus() == 0) {
                if (logger.isInfoEnabled()) {
                    logger.info("Rule had been added to block ip:\"{}\".",
                            ip.getHostAddress());
                }
            } else {
                if (logger.isWarnEnabled()) {
                    logger.warn(
                            "SshExecutor return error code: {}, Error output:",
                            result.getExitStatus());
                    for (String s : result.getError()) {
                        logger.warn("\t{}", s);
                    }
                }
            }
        } else {
            if (logger.isDebugEnabled()) {
                logger.debug("Block rule for ip \"{}\" already exists.", ip);
            }
        }
    }

    private Map<Integer, InetAddress> getRuleNumberByIp(InetAddress ip)
            throws JSchException, IOException {
        String command;
        if (ip == null) {
            command = listAllBlockingCommand;
        } else {
            command = MessageFormat.format(listOneBlockingCommand,
                    ip.getHostAddress());
        }
        SshExecuteResult result = sshExecutor.executeCommand(command);
        if (result.getExitStatus() == 0) {
            Collection<String> output = result.getOutput();
            return parseOutput(output);
        } else {
            if (logger.isWarnEnabled()) {
                logger.warn("SshExecutor return error code: {}, Error output:",
                        result.getExitStatus());
                for (String s : result.getError()) {
                    logger.warn("\t{}", s);
                }
            }
            return null;
        }
    }

    private Map<Integer, InetAddress> parseOutput(Collection<String> strs) {
        Map<Integer, InetAddress> result = new HashMap<Integer, InetAddress>(
                strs.size());
        for (String str : strs) {
            try {
                Matcher m = rule.matcher(str);
                if (m.matches()) {
                    String numStr = m.group(1);
                    String ipStr = m.group(2);
                    int num = Integer.parseInt(numStr);
                    InetAddress ip = InetAddress.getByName(ipStr);
                    result.put(num, ip);
                } else {
                    if (logger.isErrorEnabled()) {
                        logger.error("I do not recorganize this line \"{}\".",
                                str);
                    }
                }
            } catch (Exception e) {
                if (logger.isErrorEnabled()) {
                    logger.error("Error happened? Noway. String: + \"" + str
                            + "\"", e);
                }
            }
        }
        return result;
    }
}
