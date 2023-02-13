package com.yzlc.common.aop;


import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.core.NamedThreadLocal;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import com.yzlc.common.annotation.Stopwatch;

/**
 * 记录日志
 *
 * @author yzlc
 */
@Aspect
@Component
@Scope
@Order(7)
public class StopwatchAspect {
    private static final Logger LOG = LoggerFactory.getLogger(StopwatchAspect.class);

    private static final ThreadLocal<com.google.common.base.Stopwatch> STOPWATCH =
            new NamedThreadLocal<>("stopwatch");

    @Before("within(xyz.yzlc..*) && @annotation(stopwatch)")
    public void before(JoinPoint joinPoint, Stopwatch stopwatch) {
        STOPWATCH.set(com.google.common.base.Stopwatch.createStarted());
    }

    @AfterReturning("within(xyz.yzlc..*) && @annotation(stopwatch)")
    public void afterReturning(JoinPoint joinPoint, Stopwatch stopwatch) {
        LOG.info(log(joinPoint, stopwatch));
    }

    @AfterThrowing(pointcut = "within(xyz.yzlc..*) && @annotation(stopwatch)", throwing = "e")
    public void afterThrowing(JoinPoint joinPoint, Stopwatch stopwatch, Exception e) {
        LOG.error(log(joinPoint, stopwatch) + " Exception", e);
    }

    private String log(JoinPoint joinPoint, Stopwatch stopwatch) {
        String value = stopwatch.value();
        if (!"".equals(value)) value += " ";
        Signature signature = joinPoint.getSignature();
        String typeName = signature.getDeclaringTypeName();
        String s = typeName.substring(typeName.lastIndexOf(".") + 1) + "#" + signature.getName();
        return value + s + " Finished in " + STOPWATCH.get().stop();
    }
}
