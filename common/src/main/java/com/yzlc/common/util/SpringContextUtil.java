package com.yzlc.common.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;

/**
 * 配置<bean id="springContextUtil" class="util.SpringContextUtil"></bean>
 */
public class SpringContextUtil {
    private ApplicationContext applicationContext;

    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public Object getBean(String name) throws BeansException {
        return applicationContext.getBean(name);
    }
}