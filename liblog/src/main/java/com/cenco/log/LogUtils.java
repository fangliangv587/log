package com.cenco.log;


import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;



import java.io.File;
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
    private volatile static boolean isInit = false;
    public  static boolean debug = true;
    /*日志保存路径*/
    private static String logPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/log";
    /*日志最多保存天数*/
    private static int maxDay = 30;
    /*tag 过滤集，在过滤集内的tag即不打印，也不保存本地*/
    private static List<String> filters;
    /*是否全局保存*/
    private static int saveLevel = Level.ERROR;

    public static void init(String tag, int level, String logPath, int days,String suffix) {
        if (isInit) {
            return;
        }
        isInit = true;
        saveLevel = level;
        commontag = tag;

        //异常捕获
        CrashHandler.getInstance().init();

        //日志保存设置
        AsyncLogger.getInstance().setLogPath(logPath);
        AsyncLogger.getInstance().setSuffix(suffix);

        //删除过期log
        deleteTimeOutLog(logPath, days);
    }


    public static void init() {
        init(null);
    }

    public static void init(String generalTag) {
        init(generalTag, Level.ERROR);
    }

    public static void init(String generalTag, int level) {
        init(generalTag, level, logPath, maxDay,".txt");
    }
    public static void init(String generalTag, int level,String logPath) {
        init(generalTag, level, logPath, maxDay,".txt");
    }



    private static boolean printLog() {
        return isInit && debug;
    }

    public static void filters(String... filter) {
        filters = Arrays.asList(filter);
    }

    private static void logs(int level, String mes) {
        logs(level, null, mes);
    }

    public static void logs(int level, String tag, String mes) {

        if (!isInit){
            return;
        }

        if (filters != null && filters.contains(tag)) {
            return;
        }

        if (printLog()) {
            String tag1 = getFormatTag(tag);
            log(level, tag1, mes);
        }

        if (level >= saveLevel) {
            AsyncLogger.getInstance().Log(mes,level);
        }

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
        String mes = getExceptionMessage(throwable);
        logs(Level.ERROR, tag, mes);
    }

    public static void e(String tag, String message, Throwable throwable) {
        String mes = getExceptionMessage(throwable);
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
        String log = getExceptionMessage(throwable);
        e(log);
    }

    /**
     * 格式化异常信息
     * @param throwable
     * @return
     */
    public static String getExceptionMessage(Throwable throwable) {
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

    /**
     * 拼接tag
     * @param tag
     * @return
     */
    private static String getFormatTag(String tag) {

        if (TextUtils.isEmpty(tag) && TextUtils.isEmpty(commontag)){
            return "notag";
        }
        if (!TextUtils.isEmpty(tag)) {
            if (TextUtils.isEmpty(commontag)) {
                return tag;
            } else {
                return commontag + "-" + tag;
            }
        }

        return commontag;
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


}
