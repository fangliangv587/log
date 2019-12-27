package com.cenco.lib.log;


import android.text.TextUtils;
import android.util.Log;

import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.DiskLogAdapter;
import com.orhanobut.logger.FormatStrategy;
import com.orhanobut.logger.Logger;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Arrays;
import java.util.Date;
import java.util.List;


/**
 * Created by Administrator on 2018/3/5.
 * <p>
 * the default action is that don't save the log to the sdcard ,you can alter the param of save to save all logs{@link # },
 * the default global tag is {@link #commontag}
 */

public class LogUtils {

    private static String commontag = "";
    private static boolean isInit = false;
    public static boolean debug = true;
    private static List<String> filters;

    /*是否全局保存*/

    public static int saveLevel = Level.ERROR;

    public static void init(String tag, int level, String logPath, int days,boolean merge,String suffix) {
        if (isInit) {
            return;
        }

        saveLevel = level;

        if (tag == null) {
            tag = commontag;
        }

        if (tag != null) {
            commontag = tag;
        }


        //输出到控制台
        FormatStrategy strategy = SimpleFormatStrategy.newBuilder()
                .tag(tag)
                .build();
        Logger.addLogAdapter(new AndroidLogAdapter(strategy));


        //保存到sd卡
        if (logPath == null) {
            logPath = Utils.getDefaultLogFilePath();
        }
        FormatStrategy formatStrategy = TxtFormatStrategy.newBuilder()
                .tag(tag)
                .suffix(suffix)
                .merge(merge)
                .logPath(logPath)
                .build();
        Logger.addLogAdapter(new DiskLogAdapter(formatStrategy));

        //异常捕获
        CrashHandler.getInstance().init();

        isInit = true;

        //删除过期log
        deleteTimeOutLog(logPath, days);
        //anr检查
        //checkAnrLog(logPath);
    }


    public static void init() {
        init(null);
    }

    public static void init(String generalTag) {
        init(generalTag, Level.ERROR);
    }

    public static void init(String generalTag, int level) {
        init(generalTag, level, Utils.getDefaultLogFilePath(), 10,true,".txt");
    }


    private static boolean printLog() {
        return isInit && debug;
    }

    public static void logs(int level, String mes) {
        logs(level, null, mes);
    }

    public static void logs(int level, String tag, String mes) {

        if (filters != null && filters.contains(tag)) {
            return;
        }

        if (!printLog()) {
            String tag1 = getFormatTag(tag);
            log(level, tag1, mes);
            return;
        }

        if (level < saveLevel) {
            String tag1 = getFormatTag(tag);
            log(level, tag1, mes);
            return;
        }
        logger(level, tag, mes);
    }

    private static void log(int level, String tag, String mes) {
        switch (level) {
            case Level.VERBOSE:
                Log.v(tag, mes);
                break;
            case Level.DEBUG:
                Log.d(tag, mes);
                break;
            case Level.INFO:
                Log.i(tag, mes);
                break;
            case Level.WARN:
                Log.w(tag, mes);
                break;
            case Level.ERROR:
            case Level.CRASH:
                Log.e(tag, mes);
                break;
            default:
                Log.d(tag, mes);
                break;
        }
    }


    private static void logger(int level, String tag, String mes) {

        if (!TextUtils.isEmpty(tag)) {
            Logger.t(tag);
        }

        switch (level) {
            case Level.VERBOSE:
                Logger.v(mes);
                break;
            case Level.DEBUG:
                Logger.d(mes);
                break;
            case Level.INFO:
                Logger.i(mes);
                break;
            case Level.WARN:
                Logger.w(mes);
                break;
            case Level.ERROR:
            case Level.CRASH:
                Logger.e(mes);
                break;
            default:
                Logger.d(mes);
                break;
        }
    }


    public static void v(String tag, String mes) {
        logs(Level.VERBOSE, tag, mes);
    }

    public static void d(String tag, String mes) {
        logs(Level.DEBUG, tag, mes);
    }

    public static void i(String tag, String mes) {
        logs(Level.INFO, tag, mes);
    }

    public static void w(String tag, String mes) {
        logs(Level.WARN, tag, mes);
    }

    public static void e(String tag, String mes) {
        logs(Level.ERROR, tag, mes);
    }

    public static void e(String tag, Throwable throwable) {
        String mes = getExceptionLog(throwable);
        logs(Level.ERROR, tag, mes);
    }

    public static void e(String tag, String message, Throwable throwable) {
        String mes = getExceptionLog(throwable);
        mes = message + "\n" + mes;
        logs(Level.ERROR, tag, mes);
    }


    public static void v(String mes) {
        v(null, mes);
    }

    public static void d(String mes) {
        d(null, mes);
    }

    public static void i(String mes) {
        i(null, mes);
    }

    public static void w(String mes) {
        w(null, mes);
    }

    public static void e(String mes) {
        e(null, mes);
    }

    public static void e(Throwable throwable) {
        String log = getExceptionLog(throwable);
        e(log);
    }

    public static String getExceptionLog(Throwable throwable) {
        if (throwable == null) {
            return "throwable is null obj !";
        }
        Writer writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        throwable.printStackTrace(printWriter);
        Throwable cause = throwable.getCause();
        while (cause != null) {
            cause.printStackTrace(printWriter);
            cause = cause.getCause();
        }
        printWriter.close();
        String result = writer.toString();
        return result;
    }

    public static String getFormatTag(String tag) {
        if (!TextUtils.isEmpty(tag) && !TextUtils.equals(commontag, tag)) {
            if (TextUtils.isEmpty(commontag)) {
                return tag;
            } else {
                return commontag + "-" + tag;
            }
        }

        return commontag;
    }


    public static void filters(String... filter) {
        filters = Arrays.asList(filter);
    }

    /**
     * 删除超期文件
     *
     * @param days
     */
    private static void deleteTimeOutLog(final String logPath, final int days) {

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {

                File file = new File(logPath);
                File[] files = file.listFiles();
                if (files == null || files.length == 0) {
                    return;
                }
                Date today = new Date();
                for (File f : files) {
                    String name = f.getName();
                    Date date = Utils.getDate(name);
                    if (date == null) {
                        continue;
                    }
                    int dis = Math.abs(Utils.dayDiff(date, today));
                    if (dis > days) {
                        boolean b = Utils.deleteDir(f);
                        LogUtils.i("刪除 " + f.getAbsolutePath() + "--->" + b);
                    }
                }

            }
        });
        thread.start();

    }

    private static void checkAnrLog(final String logPath) {
        final String anrPath = logPath.endsWith("/") ? logPath + "anr" : logPath + "/anr";

        Thread anrThread = new Thread(new Runnable() {
            @Override
            public void run() {
                int delayTime = 20 * 1000;
                String path = "/data/anr/traces.txt";
                while (true) {
                    try {
                        Thread.sleep(delayTime);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    File file = new File(anrPath);
                    if (!file.exists()) {
                        file.mkdirs();
                    }
                    File[] files = file.listFiles();
                    if (files==null ){
                        continue;
                    }
                    File anrFile = new File(path);
                    if (!anrFile.exists()){
                        continue;
                    }
                    long anrLastModified = anrFile.lastModified();
                    String lastDate = Utils.getFullDateString(new Date(anrLastModified));
                    boolean contains = false;
                    for (int i=0;i<files.length;i++){
                        String name = files[i].getName();
                        if (name.contains(lastDate)){
                            contains = true;
                            break;
                        }
                    }

                    if (contains){
                        continue;
                    }
                    String filePath = anrPath+"/"+lastDate+".txt";
                    Log.i("libLog","copy目标路径:"+filePath);
                    Utils.copyFile(path,filePath);

                }
            }
        });
        anrThread.start();
    }

}
