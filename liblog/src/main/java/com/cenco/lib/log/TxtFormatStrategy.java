package com.cenco.lib.log;

import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.text.TextUtils;

import com.orhanobut.logger.FormatStrategy;
import com.orhanobut.logger.LogStrategy;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.orhanobut.logger.Logger.ASSERT;
import static com.orhanobut.logger.Logger.DEBUG;
import static com.orhanobut.logger.Logger.ERROR;
import static com.orhanobut.logger.Logger.INFO;
import static com.orhanobut.logger.Logger.VERBOSE;
import static com.orhanobut.logger.Logger.WARN;


/**
 * CSV formatted file logging for Android.
 * Writes to CSV the following data:
 * epoch timestamp, ISO8601 timestamp (human-readable), log level, tag, log message.
 */
public class TxtFormatStrategy implements FormatStrategy {

    private static final String NEW_LINE = "\r\n";

    private final Date date;
    private final SimpleDateFormat dateFormat;
    private final LogStrategy logStrategy;
    private final String tag;

    private TxtFormatStrategy(Builder builder) {
        date = builder.date;
        dateFormat = builder.dateFormat;
        logStrategy = builder.logStrategy;
        tag = builder.tag;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    @Override public void log(int priority, String onceOnlyTag, String message) {
        String tag = formatTag(onceOnlyTag);

        date.setTime(System.currentTimeMillis());

        StringBuilder builder = new StringBuilder();

        // human-readable date/time
        builder.append(dateFormat.format(date));

        // level
        builder.append(" ");
        builder.append(logLevel(priority));

        // tag
        builder.append("/");
        builder.append(tag);


        builder.append(" : ");
        builder.append(message);

        // new line
        builder.append(NEW_LINE);

        logStrategy.log(priority, tag, builder.toString());
    }

    private String formatTag(String tag) {
        if (!TextUtils.isEmpty(tag) && !TextUtils.equals(this.tag, tag)) {
            return this.tag + "-" + tag;
        }
        return this.tag;
    }

    public static final class Builder {
        private static final int MAX_BYTES = 500 * 1024; // 500K averages to a 4000 lines per file

        Date date;
        SimpleDateFormat dateFormat;
        LogStrategy logStrategy;
        String tag = "PRETTY_LOGGER";
        String logPath;
        boolean merge;
        String suffix;

        private Builder() {
        }

        public Builder merge(boolean m) {
            merge = m;
            return this;
        }
        public Builder suffix(String m) {
            suffix = m;
            return this;
        }

        public Builder date(Date val) {
            date = val;
            return this;
        }

        public Builder logPath(String path){
            logPath = path;
            return this;
        }

        public Builder dateFormat(SimpleDateFormat val) {
            dateFormat = val;
            return this;
        }

        public Builder logStrategy(LogStrategy val) {
            logStrategy = val;
            return this;
        }

        public Builder tag(String tag) {
            this.tag = tag;
            return this;
        }

        public TxtFormatStrategy build() {
            if (date == null) {
                date = new Date();
            }
            if (dateFormat == null) {
                dateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss.SSS");
            }
            if (logStrategy == null) {

                String folder = logPath;
                if (TextUtils.isEmpty(folder)){
                    String diskPath = Environment.getExternalStorageDirectory().getAbsolutePath();
                    folder = diskPath + File.separatorChar + "logger";
                }

                HandlerThread ht = new HandlerThread("AndroidFileLogger" );
                ht.start();
                Handler handler = new DiskLogStrategy.WriteHandler(ht.getLooper(),folder,merge,suffix);
                logStrategy = new DiskLogStrategy(handler);
            }
            return new TxtFormatStrategy(this);
        }
    }


    static String logLevel(int value) {
        switch (value) {
            case VERBOSE:
                return "VERBOSE";
            case DEBUG:
                return "DEBUG";
            case INFO:
                return "INFO";
            case WARN:
                return "WARN";
            case ERROR:
                return "ERROR";
            case ASSERT:
                return "ASSERT";
            default:
                return "UNKNOWN";
        }
    }
}

