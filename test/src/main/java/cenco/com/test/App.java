package cenco.com.test;

import android.app.Application;
import android.os.Environment;

import com.cenco.lib.log.Level;
import com.cenco.lib.log.LogUtils;
import com.cenco.log.XLogUtils;

/**
 * Created by Administrator on 2019/2/19.
 */

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        String path = Environment.getExternalStorageDirectory().getPath()+"/xz/";
        XLogUtils.init("testlogapp",Level.VERBOSE,path,10,"");
    }
}
