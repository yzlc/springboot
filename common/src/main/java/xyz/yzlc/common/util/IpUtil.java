package xyz.yzlc.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.*;
import java.util.Enumeration;

/**
 * @author yzlc
 */
public class IpUtil {
    public final static Logger log = LoggerFactory.getLogger(IpUtil.class);

    /**
     * 获取本机IP地址
     *
     * @return 本机IP地址
     */
    public static String getLocalIp() {
        String ip = "";
        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface ni = networkInterfaces.nextElement();
                Enumeration<InetAddress> nias = ni.getInetAddresses();
                while (nias.hasMoreElements()) {
                    InetAddress ia = nias.nextElement();
                    if (!ia.isLinkLocalAddress() && !ia.isLoopbackAddress() && ia instanceof Inet4Address) {
                        ip = ia.getHostAddress();
                    }
                }
            }
        } catch (SocketException e) {
            log.error("获取本地IP异常：", e);
        }
        return ip;
    }

    public static void main(String[] args) {
        System.out.println(getLocalIp());
    }
}
