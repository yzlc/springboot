package xyz.yzlc.common.util;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Objects;

/**
 * <dependency>
 * <groupId>commons-net</groupId>
 * <artifactId>commons-net</artifactId>
 * <version>3.3</version>
 * </dependency>
 */

public class FtpUtil {

    private static final Logger LOG = LoggerFactory.getLogger(FtpUtil.class);

    private final String host;

    private final int port;

    private final String username;

    private final String password;

    private final FTPClient client = new FTPClient();

    public FtpUtil(String host, int port, String username, String password) throws IOException {
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
        login();
    }

/**
     * 连接ftp服务器
     *
     * @throws IOException
     */

    private void login() throws IOException {
        client.setControlEncoding(String.valueOf(StandardCharsets.UTF_8));
        client.connect(host, port); // 连接ftp服务器
        client.login(username, password); // 登录ftp服务器
        int replyCode = client.getReplyCode(); // 是否成功登录服务器
        if (!FTPReply.isPositiveCompletion(replyCode)) {
            throw new IOException("ftp server [" + host + ":" + port + "] login failed");
        }
    }

/**
     * 关闭连接
     */

    public void logout() {
        if (client.isConnected()) {
            try {
                client.disconnect();
            } catch (IOException e) {
                LOG.info("", e);
            }
        }
    }

/**
     * 文件上传
     *
     * @param ftpDir   ftp目录
     * @param fileName 文件名
     * @param is       InputStream
     * @return true - success
     * @throws IOException
     */

    public boolean upload(String ftpDir, String fileName, InputStream is) throws IOException {
        client.enterLocalPassiveMode();
        client.setFileType(FTP.BINARY_FILE_TYPE);

        //判断FPT目标文件夹时候存在不存在则创建
        if (!client.changeWorkingDirectory(ftpDir)) {
            client.makeDirectory(ftpDir);
            client.changeWorkingDirectory(ftpDir);
        }

        return client.storeFile(fileName, is);
    }

/**
     * 下载FTP下指定文件
     *
     * @param ftpDir   FTP目录
     * @param fileName 文件名
     * @param dir      下载目录
     * @return true - success
     * @throws IOException
     */

    public boolean download(String ftpDir, String fileName, String dir) throws IOException {
        // 默认失败
        boolean flag;
        // 跳转到文件目录
        client.changeWorkingDirectory(ftpDir);
        // 获取目录下文件集合
        client.enterLocalPassiveMode();
        FTPFile file = Arrays.stream(client.listFiles()).
                filter(f -> Objects.equals(f.getName(), fileName)).
                findFirst().orElse(null);

        if (Objects.isNull(file))
            throw new FileNotFoundException();

        Path path = FileUtil.create(dir, fileName);
        try (OutputStream out = new FileOutputStream(path.toFile())) {
            // 绑定输出流下载文件,需要设置编码集，不然可能出现文件为空的情况
            flag = client.retrieveFile(new String(file.getName().getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1), out);
            // 下载成功删除文件
            //client.deleteFile(new String(fileName.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1));
        }
        return flag;
    }
}
