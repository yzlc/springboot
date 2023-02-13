package com.yzlc.common.util;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author yzlc
 */
public class ResourceUtil {
    private final URL root;
    private static final ResourceUtil instance = new ResourceUtil();

    public static ResourceUtil getInstance() {
        return instance;
    }

    private ResourceUtil() {
        root = ResourceUtil.class.getClassLoader().getResource("");
    }

    public String get(String address) {
        return root.getPath() + address;
    }

    public URL getUrl(String address) throws MalformedURLException {
        return new URL(root + address);
    }
}
