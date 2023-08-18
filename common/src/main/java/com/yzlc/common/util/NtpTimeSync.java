package com.yzlc.common.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.net.ntp.NTPUDPClient;
import org.apache.commons.net.ntp.TimeInfo;

import java.net.InetAddress;
import java.util.Date;

@Slf4j
public class NtpTimeSync {
    public static void main(String[] args) {
        sync();
    }
    public static void sync() {
        String ntpTime = getNtpTime();
        WindowsTimeUtil.modify(ntpTime);
        getNtpTime();
    }

    public static String getNtpTime(){
        String ntpServer = "ntp.ntsc.ac.cn"; // NTP 服务器地址//ntp.ntsc.ac.cn//ntp.aliyun.com
        NTPUDPClient client = new NTPUDPClient();
        String adjustTime;
        try {
            client.open();
            client.setDefaultTimeout(1000);
            client.setSoTimeout(1000);
            InetAddress hostAddr = InetAddress.getByName(ntpServer);
            TimeInfo info = client.getTime(hostAddr);
            System.out.println(info.getReturnTime());
            System.out.println(System.currentTimeMillis());
            info.computeDetails(); // 计算时间差

            long offsetValue = info.getOffset();
            long delayValue = info.getDelay();

            log.info("NTP服务器地址: " + ntpServer);
            log.info("本地时差（毫秒）: " + offsetValue);
            log.info("网络延迟（毫秒）: " + delayValue);

            long currentTimeMillis = System.currentTimeMillis();
            long adjustedTimeMillis = currentTimeMillis + offsetValue;
            adjustTime = DateFormatUtils.format(new Date(adjustedTimeMillis),"yyyy-MM-dd HH:mm:ss");
            log.info("调整前的时间: " + DateFormatUtils.format(new Date(currentTimeMillis),"yyyy-MM-dd HH:mm:ss"));
            log.info("调整后的时间: " + adjustTime);
        } catch (Exception e) {
            log.error("",e);
            adjustTime = getNtpTime();
        } finally {
            client.close();
        }
        return adjustTime;
    }
}
