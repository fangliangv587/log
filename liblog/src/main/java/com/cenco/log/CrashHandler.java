package com.cenco.log;

import android.annotation.SuppressLint;
import android.os.SystemClock;

import com.cenco.lib.log.Level;
import com.cenco.lib.log.LogUtils;

import java.lang.Thread.UncaughtExceptionHandler;


/** 
 * <h3>全局捕获异常</h3> 
 * <br> 
 * 当程序发生Uncaught异常的时候,有该类来接管程序,并记录错误日志 
 *  
 */  
@SuppressLint("SimpleDateFormat")  
public class CrashHandler implements UncaughtExceptionHandler {  
  
    // 系统默认的UncaughtException处理类
    private UncaughtExceptionHandler mDefaultHandler;
  
    private static CrashHandler instance = new CrashHandler();



    /** 保证只有一个CrashHandler实例 */  
    private CrashHandler() {  
    }  
  
    /** 获取CrashHandler实例 ,单例模式 */  
    public static CrashHandler getInstance() {
        return instance;  
    }  
  
    /** 
     * 初始化 
     *  
     */
    public void init() {
        // 获取系统默认的UncaughtException处理器
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();  
        // 设置该CrashHandler为程序的默认处理器  
        Thread.setDefaultUncaughtExceptionHandler(this);  
    }
  
    /** 
     * 当UncaughtException发生时会转入该函数来处理 
     */  
    @Override  
    public void uncaughtException(Thread thread, Throwable ex) {

        handleException(ex);

        if (mDefaultHandler != null) {
            SystemClock.sleep(2000);
            mDefaultHandler.uncaughtException(thread, ex);  
        }

    }  
  
    /** 
     * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成. 
     *  
     * @param ex 
     * @return true:如果处理了该异常信息; 否则返回false. 
     */  
    private boolean handleException(Throwable ex) {

        String log = XLogUtils.getExceptionMessage(ex);
        XLogUtils.logs(XLevel.CRASH,"CrashHandler","发生异常\n"+log);
        return true;
    }




}  