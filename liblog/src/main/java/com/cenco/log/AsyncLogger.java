package com.cenco.log;

import android.os.Environment;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author rainking
 */
public class AsyncLogger {

    private static long FILE_MAX = 4096 * 4096;
    private static long FILE_CPM_MAX = 4096 * 4096;
    private final static int LOG_TYPE = 1;
    private final static int maxDay = 30;

    private String logPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/log";
    private String suffix = ".txt";

    public void setLogPath(String logPath) {
        this.logPath = logPath;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    private WriteThread mThread = null;
    private static volatile AsyncLogger instance = null;

    public static AsyncLogger getInstance() {
        if (instance == null) {
            synchronized (AsyncLogger.class) {
                if (instance == null) {
                    instance = new AsyncLogger();
                }
            }
        }
        return instance;
    }



    private AsyncLogger() {
        mThread = new WriteThread();
        mThread.start();
    }

    public void Log(String str) {
        Log(str, LOG_TYPE);
    }


    public synchronized void Log(String str, int level) {
        mThread.enqueue(new Msg(str, level));
    }


//    public static void deleteTimeoutLog(){
//
//        Thread thread = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                String logPath = CommonHelper.WORK_DIR_LOG;
//                File file = new File(logPath);
//                File[] files = file.listFiles();
//                if (files == null || files.length == 0) {
//                    return;
//                }
//                Date today = new Date();
//                for (File f : files) {
//                    if (!f.isDirectory()){
//                        continue;
//                    }
//                    String name = f.getName();
//                    Log.i("xlog","name:"+name);
//                    Date date = DateUtil.getDate(name, DateUtil.FORMAT_YMD2);
//                    if (date==null){
//                        continue;
//                    }
//
//                    int dis = Math.abs(DateUtil.dayDiff(today,date));
//                    if (dis > maxDay) {
//                        boolean b = FileUtils.deleteDir(f);
//                        Log.i("xlog","刪除 " + f.getAbsolutePath() + "--->" + b);
//                    }
//                }
//
//            }
//        });
//        thread.start();
//    }

    /**
     * 线程保持常在,不工作时休眠,需要工作时再唤醒就可.
     */
    public class WriteThread extends Thread {
        private volatile boolean isRunning = false;
        private Object lock = new Object();
        private ConcurrentLinkedQueue<Msg> mQueue = new ConcurrentLinkedQueue<Msg>();

        public WriteThread() {
            isRunning = true;
        }


        public void enqueue(Msg msg) {
            mQueue.add(msg);
            if (isRunning() == false) {
                awake();
            }
        }

        public boolean isRunning() {
            return isRunning;
        }

        public void awake() {
            synchronized (lock) {
                lock.notify();
            }
        }

        @Override
        public void run() {
            while (true) {
                synchronized (lock) {
                    isRunning = true;
                    while (!mQueue.isEmpty()) {
                        Msg msg = mQueue.poll();
                        String parentPath = getDayFilePath();
                        recordStringByDate(parentPath, msg.getMsg(),msg.level, FILE_MAX);
                    }
                    isRunning = false;
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {

                    }
                }

            }
        }

        public void recordStringLog(String filePath, String text, long max) {
            File file = new File(filePath);
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            try {
                FileWriter filerWriter = new FileWriter(file, true);
                BufferedWriter bufWriter = new BufferedWriter(filerWriter);
                bufWriter.write(text);
                bufWriter.newLine();
                bufWriter.close();
                filerWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (file.length() > max) {
                String backFile = filePath + ".log";
                File fileLogBackup = new File(backFile);
                if (fileLogBackup.exists()) {
                    fileLogBackup.delete();
                }

                file.renameTo(fileLogBackup);
            }
        }


        public void recordStringByDate(String parentPath, String text,int level, long max) {
            if (TextUtils.isEmpty(parentPath)) {
                return;
            }
            File folder = new File(parentPath);
            if (!folder.exists()) {
                folder.mkdirs();
            }

            String fileName = "log";
            if (level==Level.CRASH){
                fileName = "Crash";
            }
            File file = getLogFile(parentPath, fileName, suffix, max);
            try {
                FileWriter filerWriter = new FileWriter(file, true);
                BufferedWriter bufWriter = new BufferedWriter(filerWriter);
                bufWriter.write(text);
                bufWriter.newLine();
                bufWriter.close();
                filerWriter.close();
            } catch (IOException e) {
                e.printStackTrace();

            }

        }


        private File getLogFile(String folder, String fileName, String suffix, long maxFileSize) {
            int newFileCount = 0;
            File newFile;
            File existingFile = null;

            newFile = new File(folder, String.format("%s_%s%s", fileName, newFileCount, suffix));
            while (newFile.exists()) {
                existingFile = newFile;
                newFileCount++;
                newFile = new File(folder, String.format("%s_%s%s", fileName, newFileCount, suffix));
            }

            if (existingFile != null) {
                if (existingFile.length() >= maxFileSize) {
                    return newFile;
                }
                return existingFile;
            }

            return newFile;
        }
    }

    @NonNull
    private String getDayFilePath() {
        String day = DateUtil.getDateString(DateUtil.FORMAT_YMD2);
        if (TextUtils.isEmpty(day)) {
            day = "all";
        }
        if (logPath != null && logPath.endsWith("/")) {
            logPath = logPath.substring(0, logPath.length() - 1);
        }

        return logPath + "/" + day + "/";
    }

    class Msg {
        private String msg;
        private int level;

        public Msg(String msg, int type) {
            this.msg = msg;
            this.level = type;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

        public int getLevel() {
            return level;
        }

        public void setLevel(int level) {
            this.level = level;
        }
    }



}

