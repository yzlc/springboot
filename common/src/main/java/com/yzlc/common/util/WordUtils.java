package com.yzlc.common.util;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class WordUtils {
    public static void replace(String filePath, Map<String, String> replacements) throws IOException {
        XWPFDocument document = new XWPFDocument(new FileInputStream(filePath));
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
        FileOutputStream fileOut = new FileOutputStream(filePath);
        document.write(fileOut);
        fileOut.close();
        document.close();
    }
}
