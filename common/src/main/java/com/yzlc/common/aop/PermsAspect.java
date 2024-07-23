package com.yzlc.common.aop;

import com.yzlc.common.annotation.Perms;
import com.yzlc.common.enums.Logical;
import com.yzlc.common.model.User;
import com.yzlc.common.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.util.Objects;
import java.util.Set;

@Aspect
@Component
@Slf4j
@Order(1)
public class PermsAspect {
    @Pointcut("@annotation(com.yzlc.common.annotation.Perms)")
    public void permsPointCut() {

    }

    @Around(value = "permsPointCut()")
    public Object aroundAdvice(ProceedingJoinPoint pjp) throws Throwable {
        log.info("权限校验开始");
        RequestAttributes ra = RequestContextHolder.getRequestAttributes();
        // 防止不是http请求的方法，例如：scheduled
        if (ra == null) return pjp.proceed();

        User user = new User();//TODO 登录信息
        Perms perm = ((MethodSignature) pjp.getSignature()).getMethod().getAnnotation(Perms.class);
        Set<String> perms = (Objects.nonNull(user) && Objects.nonNull(user.getPerms())) ? JsonUtil.json2pojo(user.getPerms(), Set.class) : null;
        if (Objects.isNull(perm.value()) && (Objects.isNull(perms) || perms.isEmpty() || noPerm(perm,perms))) {
            log.info("权限校验不通过:需要[" + perm + "],持有" + perms);
            return "401";//TODO
        }
        log.info("权限校验通过");
        return pjp.proceed();
    }

    public boolean noPerm(Perms perm,Set<String> perms){
        boolean logicalOr = perm.logical() == Logical.OR;
        for (String permValue : perm.value()) {
            if (logicalOr && perms.contains(permValue)) return false;
            if (!logicalOr && !perms.contains(permValue)) return true;
        }
        return logicalOr;
    }
}