package com.yzlc.common.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.yzlc.common.model.http.HttpStatusEnum;
import com.yzlc.common.model.http.Response;
import com.yzlc.common.util.HttpUtil;
import com.yzlc.common.util.ImgRecognitionUtil;
import com.yzlc.common.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicStampedReference;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 抓包登录缓存
 */
@Slf4j
public class PacketSniffer {
    private static final Lock lock = new ReentrantLock();
    private static final AtomicStampedReference<Boolean> isLogin = new AtomicStampedReference<>(false, 0);
    private static final Map<String, String> head = new HashMap<>();

    public static void main(String[] args) throws Exception {
        long begin = System.currentTimeMillis();
        // 启动多个线程模拟不同的查询操作
        for (int i = 0; i < 100; i++) {
            new Thread(() -> {
                try {
                    query();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
            Thread.sleep((long) (Math.random() * 100));
        }
        System.out.println(System.currentTimeMillis() - begin);
    }

    public static Object verifyCode() throws Exception {
        log.info("verifyCode begin");
        int tryCount = 3;
        Object result = null;
        boolean ok = false;
        for (int i = 0; i < tryCount; i++) {
            try {
                String respJson = HttpUtil.get("url", head);
                Response response = JsonUtil.json2pojo(respJson, new TypeReference<Response>() {
                });
                String verifyCode = ImgRecognitionUtil.verifyCode(String.valueOf(response.getData()));
                if (StringUtils.isNumeric(verifyCode) && verifyCode.length() == 4) {
                    result = response.getData();
                    ok = true;
                    break;
                }
            } catch (Exception e) {
                log.error("", e);
            }
        }
        log.info("verifyCode end");
        if (!ok) throw new RuntimeException("获取验证码失败");
        return result;
    }

    public static void login() throws Exception {
        if (isLogin.getReference()) return;
        // 尝试获取登录锁，只有一个线程能够成功获取锁并进行登录
        if (lock.tryLock()) {
            try {
                Object verifyCode = verifyCode();

                log.info("login begin");
                String loginRespJson = HttpUtil.post("url", JsonUtil.obj2json(verifyCode), head);
                Response response = JsonUtil.json2pojo(loginRespJson, new TypeReference<Response>() {
                });
                if (Objects.equals(response.getCode(), HttpStatusEnum.OK.getCode())) {
                    head.put("session", String.valueOf(response.getData()));
                    isLogin.set(true, isLogin.getStamp() + 1);
                    log.info("登录成功");
                } else {
                    log.info("登录失败");
                }
                log.info("login end");
            } finally {
                // 释放登录锁
                lock.unlock();
            }
        } else {
            // 其他线程等待登录完成，直到有线程释放登录锁
            lock.lock();
            lock.unlock(); // 立即释放锁
        }
    }

    public static void query() throws Exception {
        login();
        log.info("query begin");
        int stamp = isLogin.getStamp();
        String respJson = HttpUtil.post("url", "reqJson", head);
        Response response = JsonUtil.json2pojo(respJson, new TypeReference<Response>() {
        });
        if (Objects.equals(response.getData(), "登录失效")) {
            //通过版本号控制只处理一次登录失效
            if (isLogin.compareAndSet(true, false, stamp, stamp + 1)) {
                log.info("检测到登录失效");
            }
        }
        log.info("query end");
    }
}
