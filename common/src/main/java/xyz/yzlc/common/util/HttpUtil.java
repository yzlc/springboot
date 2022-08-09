package xyz.yzlc.common.util;

import org.apache.commons.text.StringEscapeUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpHeaders;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * <dependency>
 * <groupId>org.apache.httpcomponents</groupId>
 * <artifactId>httpclient</artifactId>
 * </dependency>
 */

public class HttpUtil {

    private static final Logger LOG = LoggerFactory.getLogger(HttpUtil.class);

    private static final String USER_AGENT_VALUE = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/69.0.3497.100 Safari/537.36";

    public static final String APPLICATION_JSON_VALUE = "application/json";

    // 超时设置
    private static final RequestConfig requestConfig = RequestConfig.custom()
            .setConnectTimeout(5000)
            .setConnectionRequestTimeout(5000)
            .setSocketTimeout(60*1000)
            .build();

    // 编码设置
    private static final ConnectionConfig connectionConfig = ConnectionConfig.custom()
            .setMalformedInputAction(CodingErrorAction.IGNORE)
            .setUnmappableInputAction(CodingErrorAction.IGNORE)
            .setCharset(StandardCharsets.UTF_8)
            .build();

    private static HttpClientBuilder defaultBuilder() {
        List<Header> headers = new ArrayList<>();
        Header header = new BasicHeader(HttpHeaders.USER_AGENT, USER_AGENT_VALUE);
        headers.add(header);
        return HttpClients.custom().setDefaultConnectionConfig(connectionConfig).setDefaultHeaders(headers).setDefaultRequestConfig(requestConfig);
    }

    /**
     * 发送get请求
     *
     * @param url 地址
     * @return 返回结果
     */

    public static String get(String url) throws IOException {
        return get(url, Collections.emptyMap());
    }

    /**
     * 发送get请求
     *
     * @param url 地址
     * @return 返回结果
     */

    public static String get(String url, Map<String, String> headers) throws IOException {
        HttpGet get = new HttpGet(url);
        return send(get, headers);
    }

    /**
     * 发送post请求
     *
     * @param url  地址
     * @param json json参数
     * @return 返回结果
     */

    public static String post(String url, String json) throws IOException {
        return post(url, json, Collections.emptyMap());
    }

    /**
     * 发送post请求
     *
     * @param url  地址
     * @param json json参数
     * @return 返回结果
     */

    public static String post(String url, String json, Map<String, String> headers) throws IOException {
        StringEntity entity = new StringEntity(json, StandardCharsets.UTF_8);
        entity.setContentType(APPLICATION_JSON_VALUE);
        HttpPost post = new HttpPost(url);
        post.setEntity(entity);
        return send(post, headers);
    }

    /**
     * 发送请求
     *
     * @param req 请求
     * @return 返回结果
     */
    private static String send(HttpUriRequest req, Map<String, String> headers) throws IOException {
        LOG.info("request: " + req);
        if (!headers.isEmpty()) {
            headers.forEach(req::addHeader);
            LOG.info("headers: " + headers);
        }

        if (req instanceof HttpEntityEnclosingRequest)
            LOG.info("content: " + EntityUtils.toString(((HttpEntityEnclosingRequest) req).getEntity()));

        long begin = System.currentTimeMillis();
        try (CloseableHttpClient client = defaultBuilder().build(); CloseableHttpResponse response = client.execute(req)) {
            HttpEntity entity = response.getEntity();
            String result = EntityUtils.toString(entity);
            LOG.info("response(" + (System.currentTimeMillis() - begin) / 1000.0 + "s): " + StringEscapeUtils.unescapeJava(result));
            return result;
        }
    }
}
