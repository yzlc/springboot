package com.yzlc.common.aop;

import com.yzlc.common.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * <dependency>
 *             <groupId>org.springframework.boot</groupId>
 *             <artifactId>spring-boot-starter-aop</artifactId>
 *         </dependency>
 */
@Aspect
@Component
@Slf4j
public class LogAspect {
    @Pointcut("execution(* com.yzlc.common.controller..*(..))")
    public void requestServer() {
    }

    @Before("requestServer()")
    public void doBefore(JoinPoint joinPoint) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        assert attributes != null;
        HttpServletRequest request = attributes.getRequest();
        try {
            log.info("request start》》》|IP:{}|URL：{}|header:{}|param:{}", request.getRemoteAddr(),
                    request.getRequestURL().toString(),
                    JsonUtil.obj2json(this.getHeader(request)), JsonUtil.obj2json(this.getRequestParams(joinPoint)));
        } catch (Exception e) {
            log.info("全局请求日志打印异常:", e);
        }
    }

    @Around("requestServer()")
    public Object doAround(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        Object result = proceedingJoinPoint.proceed();
        stopWatch.stop();
        try {
            log.info("response end》》》|result:{}|elapsedTime:{}", JsonUtil.obj2json(result), stopWatch.getTime());
        } catch (Exception e) {
            log.info("全局响应日志打印异常:", e);
        }
        return result;
    }

    @After("requestServer()")
    public void doAfter(JoinPoint joinPoint) {
    }

    /**
     * 获取入参
     *
     * @param joinPoint
     * @return
     */
    private Map<String, Object> getRequestParams(JoinPoint joinPoint) {
        Map<String, Object> requestParams = new HashMap<>();
        //参数名
        String[] paramNames = ((MethodSignature) joinPoint.getSignature()).getParameterNames();
        //参数值
        Object[] paramValues = joinPoint.getArgs();
        for (int i = 0; i < paramNames.length; i++) {
            Object value = paramValues[i];
            //如果是文件对象
            if (value instanceof MultipartFile) {
                MultipartFile file = (MultipartFile) value;
                value = file.getOriginalFilename();
            }
            requestParams.put(paramNames[i], value);
        }
        return requestParams;
    }

    private Map<String, Object> getHeader(HttpServletRequest request) {
        Map<String, Object> headerParams = new HashMap<>(1);
        // 获取所有请求头名称并遍历
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            String headerValue = request.getHeader(headerName);
            headerParams.put(headerName, headerValue);
        }
        return headerParams;
    }
}