package com.yzlc.common.util;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.io.IOUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author yzlc
 */
public class ExcelUtil {
    private static final Logger LOG = LoggerFactory.getLogger(ExcelUtil.class);
    private static final DataFormatter formatter = new DataFormatter();

    private static final String HEADER_NAME = "Content-disposition";

    private static String encoding(String fileName) throws UnsupportedEncodingException {
        return "attachment; filename=" + new String(fileName.getBytes("GB2312"), StandardCharsets.ISO_8859_1);
    }

    /**
     * 导入excel，返回对象集合
     *
     * @param workbook Excel - xlsx
     * @param fields   标题对应的字段名称
     * @param cls      返回类型
     * @param <T>      返回对象
     * @return 集合
     */
    public static <T> Set<T> imports(XSSFWorkbook workbook, List<String> fields, Class<T> cls) {
        Set<T> set = new HashSet<>();
        try {
            for (Sheet sheet : workbook) {
                sheet.removeRow(sheet.getRow(0));
                for (Row row : sheet) {
                    T t = cls.newInstance();
                    for (int i = 0; i < fields.size(); i++) {
                        String cellValue = formatter.formatCellValue(row.getCell(i));
                        /*if (RegexUtils.containsHtml(cellValue))
                            throw new IllegalArgumentException(cellValue);*/
                        BeanUtils.setProperty(t, fields.get(i), cellValue);
                    }
                    set.add(t);
                }
            }
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
            LOG.error("", e);
        }
        return set;
    }

    /**
     * 创建sheet<br>
     * 标题加粗，标题列设置为“文本”格式
     *
     * @param workbook  workbook
     * @param sheetName sheetName
     * @param titles    标题
     */
    public static <T> XSSFSheet createSheet(XSSFWorkbook workbook, String sheetName, List<String> titles, List<T> list, List<String> fields) {
        XSSFSheet sheet = createSheet(workbook, sheetName, titles);
        fillSheet(sheet, list, fields);
        return sheet;
    }

    /**
     * 创建sheet<br>
     * 标题加粗，标题列设置为“文本”格式
     *
     * @param workbook  workbook
     * @param sheetName sheetName
     * @param titles    标题
     */
    public static XSSFSheet createSheet(XSSFWorkbook workbook, String sheetName, List<String> titles) {
        XSSFSheet sheet = workbook.createSheet(sheetName);
        setTitle(workbook, sheet, titles);
        return sheet;
    }

    /**
     * 填充sheet
     *
     * @param sheet  sheet
     * @param list   数据
     * @param fields 标题对应字段顺序
     */
    public static <T> void fillSheet(int rowIndex, int columnIndex, XSSFSheet sheet, List<T> list, List<String> fields) {
        try {
            for (int i = 0; i < list.size(); i++) {
                XSSFRow row = sheet.createRow(i + rowIndex);
                T o = list.get(i);
                for (int j = 0; j < fields.size(); j++) {
                    row.createCell(j + columnIndex).setCellValue(BeanUtils.getProperty(o, fields.get(j)));
                }
            }
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            LOG.error("", e);
        }
    }

    /**
     * 填充sheet
     *
     * @param sheet  sheet
     * @param list   数据
     * @param fields 标题对应字段顺序
     */
    public static <T> void fillSheet(int rowIndex, XSSFSheet sheet, List<T> list, List<String> fields) {
        fillSheet(rowIndex, 0, sheet, list, fields);
    }

    /**
     * 填充sheet
     *
     * @param sheet  sheet
     * @param list   数据
     * @param fields 标题对应字段顺序
     */
    public static <T> void fillSheet(XSSFSheet sheet, List<T> list, List<String> fields) {
        fillSheet(sheet.getLastRowNum() + 1, 0, sheet, list, fields);
    }

    /**
     * 导出空白excel
     *
     * @param resp     resp
     * @param fileName 文件名
     * @throws IOException IOException
     */
    public static void exportBlank(HttpServletResponse resp, String fileName) throws IOException {
        resp.reset();
        resp.setHeader(HEADER_NAME, encoding(fileName));
        Workbook workbook = new XSSFWorkbook();
        workbook.createSheet();
        try (OutputStream os = new BufferedOutputStream(resp.getOutputStream())) {
            workbook.write(os);
        }
    }

    /**
     * 导出excel<br>
     * 标题加粗，标题列设置为“文本”格式
     *
     * @param resp     resp
     * @param fileName 文件名
     * @throws IOException IOException
     */
    public static void export(HttpServletResponse resp, String fileName, XSSFWorkbook workbook) throws IOException {
        resp.reset();
        resp.setHeader(HEADER_NAME, encoding(fileName));

        try (OutputStream os = new BufferedOutputStream(resp.getOutputStream())) {
            workbook.write(os);
        }
    }

    /**
     * 导出excel<br>
     * 标题加粗，标题列设置为“文本”格式
     *
     * @param fileName 文件名
     * @throws IOException IOException
     */
    public static void export(String fileName, XSSFWorkbook workbook) throws IOException {
        try (OutputStream os = new FileOutputStream(fileName)) {
            workbook.write(os);
        }
    }

    /**
     * 导出excel<br>
     * 标题加粗，标题列设置为“文本”格式
     *
     * @param resp     resp
     * @param fileName 文件名
     * @param titles   表头
     * @throws IOException IOException
     */
    public static void export(HttpServletResponse resp, String fileName, List<String> titles) throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet();

        setTitle(workbook, sheet, titles);

        export(resp, fileName, workbook);
    }

    /***
     * 设置标题<br>
     * 1.默认把标题列设置为“文本”格式<br>
     * 2.默认加粗标题
     * @param workbook Excel - xlsx
     * @param sheet sheet
     * @param titles 标题
     */
    private static void setTitle(XSSFWorkbook workbook, XSSFSheet sheet, List<String> titles) {
        CellStyle defaultStyle = workbook.createCellStyle();
        defaultStyle.setDataFormat(workbook.createDataFormat().getFormat("@"));

        XSSFCellStyle titleStyle = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        titleStyle.setFont(font);

        XSSFRow row = sheet.createRow(0);
        for (int i = 0; i < titles.size(); i++) {
            XSSFCell cell = row.createCell(i);
            cell.setCellValue(titles.get(i));
            cell.setCellStyle(titleStyle);
            sheet.setDefaultColumnStyle(i, defaultStyle);
        }
    }

    /**
     * workbook转InputStream
     *
     * @param wb Workbook
     * @return InputStream
     */
    public static InputStream workbook2Stream(Workbook wb) throws IOException {
        InputStream in = null;
        try (ByteArrayOutputStream out = new ByteArrayOutputStream();) {
            //创建临时文件
            wb.write(out);
            byte[] bookByteAry = out.toByteArray();
            in = new ByteArrayInputStream(bookByteAry);
        } catch (IOException e) {
            LOG.error("", e);
        } finally {
            IOUtils.closeQuietly(in);
        }
        return in;
    }

    /**
     * 实体类转excel
     *
     * @param inPath 路径
     * @throws IOException IOException
     */
    public static void beanToExcel(String inPath) throws IOException {
        XSSFWorkbook wb = new XSSFWorkbook();
        Sheet sheet = wb.createSheet();
        //属性注释
        String anno = "";
        //属性计数
        int num = 0;

        File infile = new File(inPath);

        try (InputStreamReader reader = new InputStreamReader(new FileInputStream(infile));
             BufferedReader br = new BufferedReader(reader)) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.startsWith("*") && !line.endsWith("*/")) anno = line;
                else if (line.startsWith("private")) {
                    //将当前代码拆分成数组
                    String[] obj = line.replaceFirst(";", "").split(" ");
                    if (obj.length == 3) {
                        //3.添加行
                        Row row = sheet.createRow(num);
                        row.createCell(0).setCellValue(obj[2]);
                        row.createCell(1).setCellValue(obj[1]);
                        row.createCell(2).setCellValue(anno.substring(1));
                        num++;
                    }
                }
            }
        }
        String p = inPath.substring(inPath.lastIndexOf(File.separator) + 1, inPath.lastIndexOf('.'));
        try (OutputStream os = new FileOutputStream(p + ".xlsx")) {
            wb.write(os);
        }
    }
}
