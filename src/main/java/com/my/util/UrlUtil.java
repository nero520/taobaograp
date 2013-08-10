/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.my.util;

import java.util.HashMap;
import java.util.Map;
import org.jsoup.helper.StringUtil;

/**
 *
 * @author dell
 */
public class UrlUtil {

    public static Map<String, String> parseUrl(String url) {
        if (!StringUtil.isBlank(url)) {
            if (url.indexOf("?") != -1) {
                String queryPara = url.split("\\?")[1];
                if (queryPara.indexOf("&") != -1) {
                    Map<String, String> map = new HashMap<>();
                    String kvPara[] = queryPara.split("&");
                    for (String para : kvPara) {
                        if (para.indexOf("=") != -1) {
                            String kv[] = para.split("=");
                            map.put(kv[0], kv[1]);
                        }
                    }
                    return map;
                }
            }
        }
        return null;
    }
}
