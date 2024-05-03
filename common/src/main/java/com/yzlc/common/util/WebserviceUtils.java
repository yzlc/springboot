package com.yzlc.common.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;


@Component
@Slf4j
public class WebserviceUtils {
    /**
     * 请求超时5秒
     */
    private static final int socketTimeout = 5000;
    /**
     * 传输超时30秒
     */
    private static final int connectTimeout = 30000;

    private static String post(String url, String param) throws IOException {
        String result = null;
        try (CloseableHttpClient closeableHttpClient = HttpClientBuilder.create().build()) {

            HttpPost httpPost = new HttpPost(url);
            // 设置请求和传输超时时间
            RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(socketTimeout)
                    .setConnectTimeout(connectTimeout).build();
            httpPost.setConfig(requestConfig);
            CloseableHttpResponse response = null;
            try {
                //httpPost.setHeader("Content-Type", "application/soap+xml;charset=UTF-8");
                httpPost.setHeader("Content-Type", "text/xml;charset=UTF-8");
                StringEntity data = new StringEntity(param, StandardCharsets.UTF_8);
                httpPost.setEntity(data);
                response = closeableHttpClient.execute(httpPost);
                HttpEntity httpEntity = response.getEntity();
                if (httpEntity != null) {
                    result = EntityUtils.toString(httpEntity, "UTF-8");
                }
            } catch (Exception e) {
                log.error("", e);
            } finally {
                if (response != null) {
                    EntityUtils.consume(response.getEntity());
                }
            }
        }
        return result;
    }

    public static void main(String[] args) throws IOException {
        String url = "http://www.webxml.com.cn/WebServices/WeatherWebService.asmx?wsdl";
        String param = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:web=\"http://WebXml.com.cn/\">\n" +
                "   <soapenv:Body>\n" +
                "      <web:getSupportCity>\n" +
                "         <!--Optional:-->\n" +
                "         <web:byProvinceName>北京</web:byProvinceName>\n" +
                "      </web:getSupportCity>\n" +
                "   </soapenv:Body>\n" +
                "</soapenv:Envelope>";
        String post = post(url, param);
        System.out.println(post);
    }
}
