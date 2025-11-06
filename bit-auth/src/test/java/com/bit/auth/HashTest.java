package com.bit.auth;

import common.utils.HashUtils;
import org.junit.jupiter.api.Test;

/**
 * @Datetime: 2025年06月05日14:12
 * @Author: Eleven也想AC
 * @Description: 测试Hash
 */
public class HashTest {

    @Test
    public void testHash() {
        String input = "X8Ml0Zvb7P8pEeCfNCPmcClo3sg99f10pYr1c4sRHD2fP5ysgYMASsP4S/nCr78uUjdfDYfrIvQmaElZbb4cstX47swJgWLHHo9rV25kpC0FYzAdtvOjGS0Ze1y0X+4BBjOyB3OUhX18znQQfhUQddi+xTQnl6VaQPcoSI889ThnvF2+PCuAWVY9VA3uiMVT3BPOpwnimjG0zrqzpnSRPzdFJITOYhjhXblFiprQiQ3uGBizHlKsh59S0b2QydM0bTO7GlPPobgLoUSGCxtS9Y2Nb4Fc/BCxoEGY/CFl34p8CSW6oHF2qtnOzJMFthPrAha1pB0i8ONf/v3Mh8+/NylKxyf2bJTglTS9TuwNq9Gp5OXWiZdVCK5pFpfUdtJSRxev3vtq54nGnVNUeIevWIxkX5+k44SKsYi2mw6QvsVaIfIOzdaM";
        String hash = HashUtils.calculateBase64SHA256(input);
        System.out.println("SHA-256 Hash: " + hash);
        long now = System.currentTimeMillis();
        System.out.println(now);
    }
}
