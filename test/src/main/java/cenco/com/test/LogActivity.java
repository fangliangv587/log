package cenco.com.test;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.cenco.lib.log.LogUtils;
import com.cenco.log.XLogUtils;

public class LogActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);

        XLogUtils.v("mmmmmm");
        XLogUtils.i("helloworld");
        XLogUtils.d("zhang","hahaha");
        XLogUtils.w("xin","风雨");
        XLogUtils.e("zhong","填写");
        XLogUtils.d("----显示时间:" );
        int a = 0;
        int b = 5/a;
    }
}
