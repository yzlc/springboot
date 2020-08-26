package xyz.yzlc.common.util;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @author yzlc
 */
public class UrlUtil {
    /**
     * 获取图片URL
     *
     * @param address 图片地址
     * @return URL
     * @throws IOException
     */
    static URL getUrl(String address) throws IOException {
        URL url;
        if (address.startsWith("https")) {
            url = new URL(address);
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
        } else if (address.startsWith("http")) {
            url = new URL(address);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
        } else {
            url = ResourceUtil.getInstance().getUrl(address);
        }
        return url;
    }
}
