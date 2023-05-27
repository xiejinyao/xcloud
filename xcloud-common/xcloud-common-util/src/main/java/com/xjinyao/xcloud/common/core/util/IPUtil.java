package com.xjinyao.xcloud.common.core.util;

import lombok.extern.slf4j.Slf4j;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.Enumeration;

/**
 * @author 谢进伟
 * @description IP地址工具
 * @createDate 2021/4/25 9:24
 */
@Slf4j
public class IPUtil {

    /**
     * 获取本机地址
     *
     * @return
     */
    public static String getLocalIpAddress() {
        String result = null;
        InetAddress ip = null;
        try {
            Enumeration<NetworkInterface> allNetInterfaces = NetworkInterface.getNetworkInterfaces();
            while (allNetInterfaces.hasMoreElements()) {
                NetworkInterface netInterface = allNetInterfaces.nextElement();
                boolean notParse = netInterface.isLoopback()
                        || netInterface.isVirtual()
                        || !netInterface.isUp()
                        || netInterface.isPointToPoint();
                if (!notParse) {
                    Enumeration<InetAddress> addresses = netInterface.getInetAddresses();
                    while (addresses.hasMoreElements()) {
                        ip = addresses.nextElement();
                        if (ip instanceof Inet4Address && ip.isSiteLocalAddress()) {
                            result = ip.getHostAddress();
                            break;
                        }
                    }
                }
            }
        } catch (Exception e1) {
            log.error("IP地址获取失败!", e1);
        }
        if (ip == null) {
            try {
                result = InetAddress.getLocalHost().getHostAddress();
            } catch (UnknownHostException e) {
                throw new RuntimeException(e);
            }
        }

        return StringUtils.defaultString(result, "0.0.0.0");
    }
}
