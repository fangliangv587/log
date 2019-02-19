package cenco.com.test;

import android.app.Application;

import com.cenco.lib.log.LogUtils;

/**
 * Created by Administrator on 2019/2/19.
 */

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        LogUtils.init("logapp");
    }
}
