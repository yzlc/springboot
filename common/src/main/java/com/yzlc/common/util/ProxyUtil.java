package com.yzlc.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author yzlc
 */
public class ProxyUtil {
    private static final Logger log = LoggerFactory.getLogger(ProxyUtil.class);
    public List<String> ips;
    private static final String ipFile = "ips";

    private ProxyUtil() {
        try {
            ips = getIp();
        } catch (Exception e) {
            log.error("获取ip失败", e);
        }
    }

    private static final ProxyUtil instance = new ProxyUtil();

    public static ProxyUtil getInstance() {
        return instance;
    }

    private List<String> getIp() throws Exception {
        List<String> list = new ArrayList<>();
        File file = new File(ipFile);
        if (file.exists()) {
            list = (List<String>) FileUtil.deserialize(file.getName());
            if (!list.isEmpty()) {
                log.info("ip地址已存在");
                return list;
            }
        }
        int page = 0;
        while (true) {
            log.debug("处理第" + ++page + "页。。。");
            String line = HttpUtil.get("https://www.xicidaili.com/nn/" + (page));
            List<String> ip = RegexUtil.getIp(line);
            list.addAll(ip);
            if (ip.isEmpty())
                break;
            Thread.sleep(4000);
        }
        if (list.isEmpty()) {
            log.error("从网站获取ip失败");
            return new ArrayList<>();
        }
        FileUtil.serialize(list, ipFile);
        log.debug("获取到的ip为：" + Arrays.toString(list.toArray()));
        return list;
    }

    public static void main(String[] args) {
        ProxyUtil.getInstance();
    }
}
