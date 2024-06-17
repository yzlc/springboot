package com.yzlc.common.util;

import lombok.extern.slf4j.Slf4j;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Base64;

/**
 * linux需要安装Tesseract环境
 * <dependency>
 *             <groupId>net.sourceforge.tess4j</groupId>
 *             <artifactId>tess4j</artifactId>
 *             <version>4.5.2</version>
 *         </dependency>
 */
@Slf4j
public class ImgRecognitionUtil {
    public static String verifyCode(String base64Image) {
        try {
            // 将base64字符串解码为字节数组
            byte[] imageBytes = Base64.getDecoder().decode(base64Image);
            // 将字节数组转换为BufferedImage
            ByteArrayInputStream bis = new ByteArrayInputStream(imageBytes);
            BufferedImage image = ImageIO.read(bis);
            // 使用Tesseract进行验证码识别
            ITesseract tesseract = new Tesseract();
            // 设置Tesseract的训练数据目录
            tesseract.setDatapath("tessdata");
            // 识别图片
            String text = tesseract.doOCR(image);
            return text.length() > 2 ? text.substring(0, text.length() - 1) : text;
        } catch (IOException | TesseractException e) {
            log.error("",e);
            return null;
        }
    }

    public static void main(String[] args) throws Exception {
        String base64Image = "/9j/4AAQSkZJRgABAgAAAQABAAD/2wBDAAgGBgcGBQgHBwcJCQgKDBQNDAsLDBkSEw8UHRofHh0aHBwgJC4nICIsIxwcKDcpLDAxNDQ0Hyc5PTgyPC4zNDL/2wBDAQkJCQwLDBgNDRgyIRwhMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjL/wAARCAAoADwDASIAAhEBAxEB/8QAHwAAAQUBAQEBAQEAAAAAAAAAAAECAwQFBgcICQoL/8QAtRAAAgEDAwIEAwUFBAQAAAF9AQIDAAQRBRIhMUEGE1FhByJxFDKBkaEII0KxwRVS0fAkM2JyggkKFhcYGRolJicoKSo0NTY3ODk6Q0RFRkdISUpTVFVWV1hZWmNkZWZnaGlqc3R1dnd4eXqDhIWGh4iJipKTlJWWl5iZmqKjpKWmp6ipqrKztLW2t7i5usLDxMXGx8jJytLT1NXW19jZ2uHi4+Tl5ufo6erx8vP09fb3+Pn6/8QAHwEAAwEBAQEBAQEBAQAAAAAAAAECAwQFBgcICQoL/8QAtREAAgECBAQDBAcFBAQAAQJ3AAECAxEEBSExBhJBUQdhcRMiMoEIFEKRobHBCSMzUvAVYnLRChYkNOEl8RcYGRomJygpKjU2Nzg5OkNERUZHSElKU1RVVldYWVpjZGVmZ2hpanN0dXZ3eHl6goOEhYaHiImKkpOUlZaXmJmaoqOkpaanqKmqsrO0tba3uLm6wsPExcbHyMnK0tPU1dbX2Nna4uPk5ebn6Onq8vP09fb3+Pn6/9oADAMBAAIRAxEAPwD1SmLJlgrqVb36H6H/ACafVSe/sYj5d1c28TEZMczqp/ImtW0tWVexP5hT/WgKP74PH/1v881JVOC+t5nKW13DckDJRJFZgPXr06dfzqOe/s7MgG6hhPXypGC8euDgj/PBpc0bXuF1uaFFV/t1usbPLIIQmN3mnbtz0zn8Pzogv7O6cpb3cEzgZKxyBjj14p8yTtcLosUVVuruCzZWmuYYg/RZXC7sf3cn/PtU8U0c8SyROrowyGU5FF1ewXH1WurG2uyrzW8Mrp93zUDDHpzVmq981wthObRC1xsIjAx97seeOOtErWd1cHsY+i2UL3WoX9vHbxiSQJAIgNoVOCQQBwxBzx279ayw1rZafe2V5pd1fSRSO8lyiFlZiucsx+6QCM9cdec10um2S2Wm29opCywIAxXJG48seeoJz/8AWxxjaeusaNZy2MVhHOY2PlSeaFDbuhIJz/IHGM5HPHKm4xjpbe+l9+lv62t1MXGyRLPaTHwjHb2u6eQxoYpGIPylgSMsBgYyOQBgfhT9N/0S+SJ9KjsJplPlFdrhsZJQsoBBwM85GPerFpa32jaFbW9tFHdzRE+Ym/ZuBJJ2k+hI69QDUcFrqF5fw3c9qlikCsEjWYuzE4z0+UL+uR3qktYuzvZaW0++36jS1Xcr3MEaeIppbuzF+00KmOFcOYlHBwrYG0nHPBznAPJqha6C+qG4u7S6m0yB5TttgjfLwOTyBz1wMgdM8Vfb+0tN1m5uIraO7F2EBTf5ZDKMcE8Ef4jpUDeH9T1Oea9uL1tPkmfP2eLLhQAAMkMBnA/z0GU6d3blb1fl87/pf8iXHXb+vU6f7PB/zxj/AO+RR5Cdt4HoHYAfrRRXoHQHlAD5SwYcgkk/z7U1lE6lWG1wMH8f5g/5wRRRQA+Nj9xz84HX+970+iigBskaSrtdQw96rrFLCoRWmZR0K7en40UUAf/Z";
        String captchaText = verifyCode(base64Image);
        System.out.println("识别结果：" + captchaText);
    }
}
