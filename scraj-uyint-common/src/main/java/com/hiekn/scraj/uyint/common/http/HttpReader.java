package com.hiekn.scraj.uyint.common.http;

import java.io.Closeable;

import org.apache.log4j.Logger;

/**
 * 
 *
 *
 *
 * @author pzn
 * @since 1.7
 * @version 1.0
 */
public interface HttpReader extends Closeable {

    /* logger */
    Logger LOGGER = Logger.getLogger(HttpReader.class);
    
    /* static final variables */
    String HTTP_GET = "get";
    String HTTP_POST = "post";
    
    String GB_K = "gbk";
    String GB_2312 = "gb2312";
    String GB_18030 = "gb18030";
    String UTF_8 = "utf-8";
    String ISO_8859_1 = "iso-8859-1";
    /* static final variables */
    
    /**
     *
     * read url source code
     *
     * @param url
     * @return
     */
    String readSource(String url);

    /**
     *
     * read url source code with charset
     *
     * @param url
     * @param charset
     * @return
     */
    String readSource(String url, String charset);

    /**
     *
     * read url source code with charset and cookie
     *
     * @param url
     * @param charset
     * @param cookie
     * @return
     */
    String readSource(String url, String charset, String cookie);
    
    /**
     * 
     * read url source code with charset, requestType, params, cookies and filter.
     * 
     * @param url
     * @param charset
     * @param cookie
     * @param requestType 
     * @param params
     * @return
     */
    String readSource(String url, String charset, String cookie, String requestType, String params);
    
    /**
     *
     * release http resource.
     *
     */
    void close();

}
