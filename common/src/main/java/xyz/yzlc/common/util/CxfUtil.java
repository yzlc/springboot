package xyz.yzlc.common.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.endpoint.Endpoint;
import org.apache.cxf.jaxws.endpoint.dynamic.JaxWsDynamicClientFactory;
import org.apache.cxf.service.model.BindingInfo;
import org.apache.cxf.service.model.BindingOperationInfo;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transports.http.configuration.HTTPClientPolicy;

import javax.xml.namespace.QName;
import java.util.Arrays;
import java.util.Objects;

/**
 * Cxf调用webservice接口
 * <!-- CXF webservice -->
 * <dependency>
 *      <groupId>org.apache.cxf</groupId>
 *      <artifactId>cxf-spring-boot-starter-jaxws</artifactId>
 *      <version>3.2.1</version>
 * </dependency>
 * <!-- CXF webservice -->
 *
 */
@Slf4j
public class CxfUtil {
    private static final ClassLoader cl = Thread.currentThread().getContextClassLoader();

    /**
     * 调用webservice接口
     *
     * @param url        地址
     * @param operation  方法名
     * @param params 参数
     * @return 返回结果
     * @throws Exception 调用失败
     */
   public static Object[] invoke(String url, String operation, Object... params) throws Exception {
        if (StringUtils.isEmpty(url) || StringUtils.isEmpty(operation))
            throw new IllegalArgumentException("args is empty");
        long begin = System.currentTimeMillis();
        log.info("request url: " + url + " operation: " + operation + " params: " + Arrays.deepToString(params));
        Thread.currentThread().setContextClassLoader(cl);

        JaxWsDynamicClientFactory dcf = JaxWsDynamicClientFactory.newInstance();

        try (Client client = dcf.createClient(url)) {
            //超时设置
            HTTPConduit conduit = (HTTPConduit) client.getConduit();
            HTTPClientPolicy policy = new HTTPClientPolicy();
            policy.setConnectionTimeout(600000L);
            policy.setAllowChunking(false);
            policy.setReceiveTimeout(10 * 1000);
            conduit.setClient(policy);
            // 处理webService接口和实现类namespace不同的情况，
            // CXF动态客户端在处理此问题时，
            // 会报No operation was found with the name的异常
            Endpoint endpoint = client.getEndpoint();
            QName opName = new QName(endpoint.getService().getName().getNamespaceURI(), operation);
            BindingInfo bindingInfo = endpoint.getEndpointInfo().getBinding();
            if (Objects.isNull(bindingInfo.getOperation(opName))) {
                opName = bindingInfo.getOperations().stream()
                        .filter(operationInfo -> Objects.equals(operation, operationInfo.getName().getLocalPart()))
                        .findFirst().orElse(new BindingOperationInfo()).getName();
            }
            Object[] invoke = client.invoke(opName, params);
            log.info("response " + (System.currentTimeMillis() - begin)/1000.0 + "s：" + Arrays.deepToString(invoke));
            return invoke;
        }
    }
}