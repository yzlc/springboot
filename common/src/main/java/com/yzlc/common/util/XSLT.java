package com.yzlc.common.util;

import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

public class XSLT {
    public static void main(String[] args) throws IOException, URISyntaxException, TransformerException {
        transform("xslt/input.xml","xslt/transform.xsl","xslt/output.html");
    }

    public static void transform(String inputPath,String xlstPath,String outputPath) throws TransformerException {
        TransformerFactory factory = TransformerFactory.newInstance();
        Source xslt = new StreamSource(new File(xlstPath));
        Transformer transformer = factory.newTransformer(xslt);

        Source text = new StreamSource(new File(inputPath));
        transformer.transform(text, new StreamResult(new File(outputPath)));
    }
}
