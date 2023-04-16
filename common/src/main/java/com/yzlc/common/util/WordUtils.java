package com.yzlc.common.util;

import org.apache.poi.xwpf.usermodel.*;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class WordUtils {
    public static void replace(String filePath, Map<String, String> replacements) throws IOException {
        XWPFDocument document = new XWPFDocument(new FileInputStream(filePath));
        // 处理段落中的文本替换
        for (XWPFParagraph paragraph : document.getParagraphs()) {
            List<XWPFRun> runs = paragraph.getRuns();
            for (XWPFRun run : runs) {
                String text = run.getText(0);
                if (text != null) {
                    for (Map.Entry<String, String> entry : replacements.entrySet()) {
                        text = text.replace(entry.getKey(), entry.getValue());
                    }
                    run.setText(text, 0);
                }
            }
        }
        // 处理表格中的文本替换
        for (XWPFTable table : document.getTables()) {
            for (XWPFTableRow row : table.getRows()) {
                for (XWPFTableCell cell : row.getTableCells()) {
                    for (XWPFParagraph paragraph : cell.getParagraphs()) {
                        for (XWPFRun run : paragraph.getRuns()) {
                            String text = run.getText(0);
                            if (text != null) {
                                for (Map.Entry<String, String> entry : replacements.entrySet()) {
                                    String searchText = entry.getKey();
                                    String replaceText = entry.getValue();
                                    if (text.contains(searchText)) {
                                        text = text.replace(searchText, replaceText);
                                        run.setText(text, 0);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        FileOutputStream fileOut = new FileOutputStream(filePath);
        document.write(fileOut);
        fileOut.close();
        document.close();
    }
}
