package com.yzlc.common.util;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class XMLSearchTool {
    public static void main(String[] args) {
        String directoryPath = ""; // 替换为目标目录的路径
        String keyword = ""; // 替换为要匹配的关键字
        keyword = ""; // 替换为要匹配的关键字
        searchXMLFiles(directoryPath, keyword);
    }

    public static void searchXMLFiles(String directoryPath, String keyword) {
        File directory = new File(directoryPath);
        if (!directory.exists()) {
            System.out.println("目录不存在：" + directoryPath);
            return;
        }

        File[] files = directory.listFiles();
        if (files == null) {
            System.out.println("目录为空：" + directoryPath);
            return;
        }

        for (File file : files) {
            if (file.getName().equals(".idea") || file.getName().equals("target") || file.getName().equals(".svn"))
                continue;
            if (file.isDirectory()) {
                searchXMLFiles(file.getAbsolutePath(), keyword);
            } else {
                if (file.getName().endsWith("Mapper.xml")) {
                    searchXMLFile(file, keyword);
                }
            }
        }
    }

    public static void searchXMLFile(File file, String keyword) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new FileInputStream(file));

            NodeList nodeList = document.getElementsByTagName("*");
            boolean noSameFile = true;
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                if (!Objects.equals(node.getNodeName(), "select")
                        && !Objects.equals(node.getNodeName(), "insert")
                        && !Objects.equals(node.getNodeName(), "update")
                        && !Objects.equals(node.getNodeName(), "delete")
                ) continue;
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    String nodeText = node.getTextContent();
                    String patternString = "\\b" + keyword + "\\b";

                    // 忽略大小写的匹配模式
                    Pattern pattern = Pattern.compile(patternString, Pattern.CASE_INSENSITIVE);
                    Matcher matcher = pattern.matcher(nodeText);

                    // 查找匹配的单词
                    if (matcher.find()) {
                        if (noSameFile) {
                            noSameFile = false;
                            System.out.println(" - " + file.getName());
                        }
                        //System.out.println("节点：" + node.getNodeName());
                        System.out.println("\t - " + node.getAttributes().getNamedItem("id").getNodeValue());
                        //System.out.println("内容：" + nodeText);
                    }
                    /*if (nodeText.contains(keyword)) {
                        System.out.println("文件：" + file.getAbsolutePath());
                        System.out.println("节点：" + node.getNodeName());
                        System.out.println("id属性：" + node.getAttributes().getNamedItem("id").getNodeValue());
                        //System.out.println("内容：" + nodeText);
                        System.out.println("-----------------------------");
                    }*/
                }
            }
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
    }
}