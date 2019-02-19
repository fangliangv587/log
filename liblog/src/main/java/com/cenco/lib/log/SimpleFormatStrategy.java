package com.cenco.lib.log;

import android.text.TextUtils;
import android.util.Log;

import com.orhanobut.logger.FormatStrategy;

/**
 * Created by Administrator on 2018/3/5.
 */

public class SimpleFormatStrategy implements FormatStrategy {

    private final String tag;

    private SimpleFormatStrategy(Builder builder) {
        tag = builder.tag;
    }

    @Override
    public void log(int priority, String tag, String message) {
        String tags = formatTag(tag);
        Log.println(priority, tags, message);
    }

    private String formatTag(String tag) {
        if (!TextUtils.isEmpty(tag) && !TextUtils.equals(this.tag, tag)) {
            return this.tag + "-" + tag;
        }
        return this.tag;
    }
    public static Builder newBuilder() {
        return new Builder();
    }


    public static class Builder {

        String tag = "PRETTY_LOGGER";

        private Builder() {
        }

        public Builder tag(String tag) {
            this.tag = tag;
            return this;
        }

        public SimpleFormatStrategy build() {

            return new SimpleFormatStrategy(this);
        }
    }
}
