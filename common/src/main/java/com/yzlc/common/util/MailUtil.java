package com.yzlc.common.util;


import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.IOException;
import java.util.Properties;

public class MailUtil {
    private static final String MAIL_SMTP_HOST = PropertiesUtils.getProperty("mail.smtp.host");
    private static final String MAIL_SMTP_PORT = PropertiesUtils.getProperty("mail.smtp.port");
    private static final String MAIL_SIGNATURE = PropertiesUtils.getProperty("mail.signature");
    private static final String USERNAME = PropertiesUtils.getProperty("mail.userName");
    private static final String PASSWORD = PropertiesUtils.getProperty("mail.password");

    public static void sendEmail(String[] to, String subject,
                                 String message, String[] attachFiles,
                                 String[] cc) throws MessagingException, IOException {
        // 设置邮件服务器属性
        Properties properties = new Properties();
        properties.put("mail.smtp.host", MAIL_SMTP_HOST);
        properties.put("mail.smtp.port", MAIL_SMTP_PORT);
        properties.put("mail.smtp.auth", "true");
        //properties.put("mail.smtp.starttls.enable", "true");

        // 创建一个新的Session实例
        Authenticator auth = new Authenticator() {
            public PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(USERNAME, PASSWORD);
            }
        };
        Session session = Session.getInstance(properties, auth);

        // 创建一个新的Message实例
        Message msg = new MimeMessage(session);

        // 设置发件人
        msg.setFrom(new InternetAddress(USERNAME));

        // 设置收件人
        InternetAddress[] toAddresses = new InternetAddress[to.length];
        for (int i = 0; i < to.length; i++) {
            toAddresses[i] = new InternetAddress(to[i]);
        }
        msg.setRecipients(Message.RecipientType.TO, toAddresses);

        // 设置抄送人
        if (cc != null && cc.length > 0) {
            InternetAddress[] ccAddresses = new InternetAddress[cc.length];
            for (int i = 0; i < cc.length; i++) {
                ccAddresses[i] = new InternetAddress(cc[i]);
            }
            msg.setRecipients(Message.RecipientType.CC, ccAddresses);
        }

        // 设置邮件主题
        msg.setSubject(subject);

        // 创建消息部分
        BodyPart messageBodyPart = new MimeBodyPart();

        // 消息内容
        messageBodyPart.setText(message + "\n\n" + MAIL_SIGNATURE);

        // 创建多部分消息
        Multipart multipart = new MimeMultipart();

        // 设置文本消息部分
        multipart.addBodyPart(messageBodyPart);

        // 附件部分
        if (attachFiles != null && attachFiles.length > 0) {
            for (String filePath : attachFiles) {
                MimeBodyPart attachPart = new MimeBodyPart();

                attachPart.attachFile(filePath);


                multipart.addBodyPart(attachPart);
            }
        }

        // 发送完整消息
        msg.setContent(multipart);

        // 发送邮件
        Transport.send(msg);
    }
}
