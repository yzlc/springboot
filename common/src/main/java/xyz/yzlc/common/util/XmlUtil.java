package xyz.yzlc.common.util;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.*;

/**
 * xmlUtil<br>
 * <ul>
 *     <li>@XmlRootElement(name = "")：根节点</li>
 *     <li>@XmlAccessorType(XmlAccessType.FIELD)：根据字段名称生成节点</li>
 *     <li>@XmlType(propOrder = {})：节点排序</li>
 * </ul>
 */
public class XmlUtil {
    /**
     * 将对象直接转换成String类型的 XML输出
     *
     * @param obj
     * @return
     */
    public static String toXml(Object obj) throws JAXBException, IOException {
        String xml;
        // 创建输出流
        try (StringWriter sw = new StringWriter()) {
            // 利用jdk中自带的转换类实现
            JAXBContext context = JAXBContext.newInstance(obj.getClass());

            Marshaller marshaller = context.createMarshaller();
            // 格式化xml输出的格式
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,
                    Boolean.TRUE);
            // 将对象转换成输出流形式的xml
            marshaller.marshal(obj, sw);
            xml = sw.toString();
        }
        return xml;
    }

    /**
     * 将对象根据路径转换成xml文件
     *
     * @param obj
     * @param path
     * @return
     */
    public static void toXml(Object obj, String path) throws JAXBException, IOException {
        // 利用jdk中自带的转换类实现
        JAXBContext context = JAXBContext.newInstance(obj.getClass());
        Marshaller marshaller = context.createMarshaller();
        // 格式化xml输出的格式
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,
                Boolean.TRUE);
        // 将对象转换成输出流形式的xml
        // 创建输出流
        try (FileWriter fw = new FileWriter(path)) {
            marshaller.marshal(obj, fw);
        }
    }

    /**
     * 将String类型的xml转换成对象
     */
    public static Object strToObj(Class clazz, String xmlStr) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(clazz);
        // 进行将Xml转成对象的核心接口
        Unmarshaller unmarshaller = context.createUnmarshaller();
        try (StringReader sr = new StringReader(xmlStr)) {
            return unmarshaller.unmarshal(sr);
        }
    }

    /**
     * 将file类型的xml转换成对象
     */
    public static Object fileToObj(Class clazz, String xmlPath) throws JAXBException, IOException {
        JAXBContext context = JAXBContext.newInstance(clazz);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        try (FileReader fr = new FileReader(xmlPath)) {
            return unmarshaller.unmarshal(fr);
        }
    }
}
