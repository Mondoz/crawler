package com.hiekn.sfe4j.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * 
 * 配置信息
 * 
 * @author pzn
 * @version 1.0
 * @since 1.7
 */
public class ConstResource {

    static Properties props = new Properties();

    static {
        InputStream in = null;
        try {
            in = ConstResource.class.getClassLoader().getResourceAsStream("sfe4j.properties");
            props.load(in);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            if (null != in) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    /**
     *  处理线程数
     */
    public static final int TASK_THREAD_NUMS = Integer.parseInt(get("task_thread_nums", "4"));
    /**
     *  空闲时 线程睡眠时间
     */
    public static final int TASK_THREAD_IDLE_SLEEP_MILLIS = Integer.parseInt(get("task_thread_idle_sleep_millis", "60000"));
    /**
     *  完成一个任务 后 睡眠时间
     */
    public static final int TASK_THREAD_NORMAL_SLEEP_MILLIS = Integer.parseInt(get("task_thread_normal_sleep_millis", "1000"));
    /**
     *  一个任务中  批量处理  每批处理完成后睡眠时间
     */
    public static final int TASK_THREAD_BATCH_SLEEP_MILLIS = Integer.parseInt(get("task_thread_batch_sleep_millis", "-1"));
    /**
     *  批量处理的大小
     */
    public static final int TASK_BATCH_SIZE = Integer.parseInt(get("task_batch_size", "100"));
    
    
    //
    public static final String DRIVER_CLASS = get("driver_class", "");
    public static final String JDBC_URL = get("jdbc_url", "");
    public static final String JDBC_USER = get("jdbc_user", "");
    public static final String JDBC_PASSWORD = get("jdbc_password", "");
    
    /**
     * 自定义格式器的路径
     */
    public static final String FILVER_CUSTOM_PATH = get("filver_custom_path", "");
    
    // get property
    public static String get(String key, String defaultVal) { return props.getProperty(key, defaultVal); }

}
