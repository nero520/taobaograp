/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.my.util;

/**
 *
 * @author dell
 */
public class ProxyUtil {
    public static void setProxy() {
        System.getProperties().setProperty("proxySet", "true");
        System.getProperties().setProperty("proxyHost", "10.0.0.135");
        System.getProperties().setProperty("proxyPort", "8090");
    }
}
