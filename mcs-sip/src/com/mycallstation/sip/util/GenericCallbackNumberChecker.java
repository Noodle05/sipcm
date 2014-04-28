/**
 *
 */
package com.mycallstation.sip.util;

import java.util.Arrays;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

/**
 * @author Wei Gao
 *
 */
@Component("genericCallbackNumberChecker")
public class GenericCallbackNumberChecker {
    @Resource(name = "systemConfiguration")
    protected SipConfiguration appConfig;

    private volatile String[] genericCallbackNumbers;
    private volatile boolean genericCallbackNumbersInitialized = false;

    private void initGVCallbackNumber() {
        if (!genericCallbackNumbersInitialized) {
            synchronized (this) {
                if (!genericCallbackNumbersInitialized) {
                    genericCallbackNumbers = appConfig
                            .getGoogleVoiceGenericCallbackNumber();
                    genericCallbackNumbersInitialized = true;
                }
            }
        }
    }

    public boolean checkCallbackNumber(String number) {
        initGVCallbackNumber();
        if (genericCallbackNumbers != null) {
            if (Arrays.binarySearch(genericCallbackNumbers, number) >= 0) {
                return true;
            }
        }
        return false;
    }
}
