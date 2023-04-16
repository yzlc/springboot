package com.yzlc.common.util;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

@Slf4j
public class ZipUtils {
    /**
     * 压缩文件或文件夹到当前目录的zip文件
     *
     * @param sourcePath 要压缩的文件或文件夹路径
     * @throws IOException
     */
    public static void zip(String sourcePath) throws IOException {
        zip(sourcePath,sourcePath+".zip");
    }
    /**
     * 压缩文件或文件夹到指定的zip文件
     *
     * @param sourcePath 要压缩的文件或文件夹路径
     * @param zipPath    压缩后的zip文件路径
     * @throws IOException
     */
    public static void zip(String sourcePath, String zipPath) throws IOException {
        FileOutputStream fos = new FileOutputStream(zipPath);
        ZipOutputStream zos = new ZipOutputStream(fos);
        File sourceFile = new File(sourcePath);
        String baseDir = "";
        if (sourceFile.isFile()) {
            compressFile(sourceFile, zos, baseDir);
        } else {
            compressDir(sourceFile, zos, baseDir);
        }
        zos.close();
        fos.close();
    }

    /**
     * 压缩单个文件
     *
     * @param sourceFile 要压缩的文件
     * @param zos        ZipOutputStream对象
     * @param baseDir    压缩文件中的路径
     * @throws IOException
     */
    private static void compressFile(File sourceFile, ZipOutputStream zos, String baseDir) throws IOException {
        if (!sourceFile.exists()) {
            return;
        }
        byte[] buffer = new byte[1024];
        FileInputStream fis = new FileInputStream(sourceFile);
        zos.putNextEntry(new ZipEntry(baseDir + sourceFile.getName()));
        int length;
        while ((length = fis.read(buffer)) > 0) {
            zos.write(buffer, 0, length);
        }
        zos.closeEntry();
        fis.close();
    }

    /**
     * 压缩目录
     *
     * @param sourceDir 要压缩的目录
     * @param zos       ZipOutputStream对象
     * @param baseDir   压缩文件中的路径
     * @throws IOException
     */
    private static void compressDir(File sourceDir, ZipOutputStream zos, String baseDir) throws IOException {
        if (!sourceDir.exists()) {
            return;
        }
        File[] files = sourceDir.listFiles();
        if (files == null || files.length == 0) {
            zos.putNextEntry(new ZipEntry(baseDir + sourceDir.getName() + "/"));
            zos.closeEntry();
            return;
        }
        for (File file : files) {
            if (file.isFile()) {
                compressFile(file, zos, baseDir + sourceDir.getName() + "/");
            } else {
                compressDir(file, zos, baseDir + sourceDir.getName() + "/");
            }
        }
    }

    /**
     * 解压指定zip文件到当前目录
     *
     * @param zipPath  要解压的zip文件路径
     * @throws IOException
     */
    public static void unzip(String zipPath) throws IOException {
        unzip(zipPath,".");
    }

    /**
     * 解压指定zip文件到指定目录
     *
     * @param zipPath  要解压的zip文件路径
     * @param destPath 解压后的目录路径
     * @throws IOException
     */
    public static void unzip(String zipPath, String destPath) throws IOException {
        File destDir = new File(destPath);
        if (!destDir.exists()) {
            destDir.mkdirs();
        }
        ZipInputStream zis = new ZipInputStream(new FileInputStream(zipPath));
        ZipEntry entry;
        while ((entry = zis.getNextEntry()) != null) {
            String entryName = entry.getName();
            File file = new File(destDir, entryName);
            if (entry.isDirectory()) {
                file.mkdirs();
            } else {
                File parent = file.getParentFile();
                if (!parent.exists()) {
                    parent.mkdirs();
                }
                OutputStream os = new FileOutputStream(file);
                byte[] buffer = new byte[1024];
                int length;
                while ((length = zis.read(buffer)) > 0) {
                    os.write(buffer, 0, length);
                }
                os.close();
            }
            zis.closeEntry();
        }
        zis.close();
    }
}
