package com.yzlc.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @author yzlc
 */
public class FileUtil {
    private static final Logger LOG = LoggerFactory.getLogger(FileUtil.class);

    static void serialize(Object o, String file) throws IOException {
        try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(file))) {
            objectOutputStream.writeObject(o);
        }
    }

    static Object deserialize(String file) throws IOException, ClassNotFoundException {
        try (ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(file))) {
            return objectInputStream.readObject();
        }
    }

    /**
     * 读取所有行,逗号拼接
     *
     * @param first 目录
     * @param more  first/文件
     * @return
     * @throws IOException
     */
    public static String read(String first, String... more) throws IOException {
        return Files.lines(Paths.get(first, more))
                .collect(Collectors.joining(","));
    }

    /**
     * 写文件
     *
     * @param str   内容
     * @param first 目录
     * @param more  first/文件
     * @throws IOException
     */
    public static void write(String str, String first, String... more) throws IOException {
        Path file = create(first, more);
        Files.write(file, str.getBytes(StandardCharsets.UTF_8),
                StandardOpenOption.CREATE);
    }

    /**
     * 创建目录/文件
     *
     * @param first 目录
     * @param more  first/文件
     * @return filePath
     * @throws IOException
     */
    public static Path create(String first, String... more) throws IOException {
        Path dir = Paths.get(first);
        if (Files.notExists(dir))
            Files.createDirectories(dir);

        Path file = Paths.get(first, more);
        if (Files.notExists(file))
            Files.createFile(file);
        return file;
    }

    public static void delete(Path path) throws IOException {
        Files.walk(path).sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
    }

    /**
     * 压缩文件夹
     *
     * @param srcDir   文件夹路径
     * @param destFile 压缩包路径
     * @throws IOException
     */
    public static void zip(final Path srcDir, final Path destFile) throws IOException {
        Path zipFile = Files.createFile(destFile);

        try (ZipOutputStream zipOutputStream = new ZipOutputStream(Files.newOutputStream(zipFile));
             Stream<Path> paths = Files.walk(srcDir)) {
            paths.filter(path -> !Files.isDirectory(path))
                    .forEach(path -> {
                        ZipEntry zipEntry = new ZipEntry(srcDir.relativize(path).toString());
                        try {
                            zipOutputStream.putNextEntry(zipEntry);
                            Files.copy(path, zipOutputStream);
                            zipOutputStream.closeEntry();
                        } catch (IOException e) {
                            LOG.error("", e);
                        }
                    });
        }
    }

    /**
     * 压缩文件夹
     *
     * @param srcDir 文件夹路径
     * @throws IOException
     */
    public static void zip(final Path srcDir) throws IOException {
        Path parent = srcDir.getParent();
        Path zip = Paths.get(srcDir.getFileName().toString() + ".zip");
        Path dest = Objects.isNull(parent) ? zip : parent.resolve(zip);
        zip(srcDir, dest);
    }

    /**
     * 查所有文件
     *
     * @param path 目录
     * @param key  关键字
     * @return
     * @throws IOException
     */
    public static List<String> find(Path path, String... key) throws IOException {
        try (Stream<Path> paths = Files.walk(path, 99)) {
            return paths.map(Path::toString).filter(f -> Arrays.stream(key).anyMatch(f::contains)).collect(Collectors.toList());
        }
    }

    /**
     * 搜索文件内容
     *
     * @param path  目录
     * @param depth 上下行数
     * @param key   关键字
     * @return
     * @throws IOException
     */
    public static List<String> search(Path path, int depth, String key) throws IOException {
        List<String> lines = Files.lines(path).collect(Collectors.toList());
        List<Integer> keyRow = IntStream.range(0, lines.size()).filter(i -> lines.get(i).contains(key)).boxed().collect(Collectors.toList());
        int prevEnd = 0;
        List<String> res = new ArrayList<>();
        for (int row : keyRow) {
            for (int i = row - depth; i < row + depth && i < lines.size(); i++) {
                if (i <= prevEnd) continue;
                res.add(lines.get(i));
            }
            prevEnd = row + depth - 1;
        }
        return res;
    }

    /**
     * 替换目录和文件名称
     *
     * @param directory   目录
     * @param target      替换符 例如：${date}
     * @param replacement 替换内容 例如：20230415
     */
    public static void replace(File directory, String target, String replacement) {
        if (directory.isDirectory()) {
            // 处理文件夹名称
            String originalName = directory.getName();
            String newName = originalName.replace(target, replacement);
            if (!newName.equals(originalName)) {
                File newDirectory = new File(directory.getParentFile(), newName);
                directory.renameTo(newDirectory);
                directory = newDirectory;
            }

            // 处理文件名称
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        replace(file, target, replacement);
                    } else {
                        String originalFileName = file.getName();
                        String newFileName = originalFileName.replace(target, replacement);
                        if (!newFileName.equals(originalFileName)) {
                            File newFile = new File(directory, newFileName);
                            file.renameTo(newFile);
                        }
                    }
                }
            }
        }
    }

    public static void main(String[] args) throws IOException {
        String dir = ".";
        List<String> paths = find(Paths.get(dir), ".java", "pom.xml", ".yml", ".properties");
        for (String path : paths) {
            List<String> res = search(Paths.get(path), 3, "svn");
            if (res.isEmpty()) continue;
            System.out.println(path);
            System.out.println(String.join("\n", res));
            System.out.println();
        }
    }
}
