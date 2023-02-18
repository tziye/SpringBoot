package com.common.util;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;

public class RequestIpUtil {

    static String LOCAL_ADDR_IPV4 = "127.0.0.1";
    static String LOCAL_ADDR_IPV6 = "0:0:0:0:0:0:0:1";
    static String UNKNOWN = "unknown";
    static String SPLIT = ",";

    /**
     * 获取请求IP
     */
    public static String getRequestIp(HttpServletRequest request) {
        String ipAddress;
        try {
            ipAddress = request.getHeader("x-forwarded-for");
            if (ipAddress == null || ipAddress.length() == 0 || UNKNOWN.equalsIgnoreCase(ipAddress)) {
                ipAddress = request.getHeader("Proxy-Client-IP");
            }
            if (ipAddress == null || ipAddress.length() == 0 || UNKNOWN.equalsIgnoreCase(ipAddress)) {
                ipAddress = request.getHeader("WL-Proxy-Client-IP");
            }
            if (ipAddress == null || ipAddress.length() == 0 || UNKNOWN.equalsIgnoreCase(ipAddress)) {
                ipAddress = request.getRemoteAddr();
                if (LOCAL_ADDR_IPV4.equals(ipAddress) || LOCAL_ADDR_IPV6.equals(ipAddress)) {
                    // 根据网卡取本机配置的IP
                    InetAddress inetAddress = InetAddress.getLocalHost();
                    ipAddress = inetAddress.getHostAddress();
                }
            }
            // 对于通过多个代理的情况，第一个IP为客户端真实IP，多个IP按照','分割
            if (ipAddress != null && ipAddress.length() > 15 && ipAddress.contains(SPLIT)) {
                ipAddress = ipAddress.substring(0, ipAddress.indexOf(SPLIT));
            }
        } catch (Exception e) {
            ipAddress = "";
        }
        return ipAddress;
    }

}
