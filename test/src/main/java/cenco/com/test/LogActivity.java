package cenco.com.test;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.cenco.log.LogUtils;

public class LogActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);

        LogUtils.v("mmmmmm");
        LogUtils.i("helloworld");
        LogUtils.d("zhang","hahaha");
        LogUtils.w("xin","风雨");
        LogUtils.e("zhong","填写");
        LogUtils.d("----显示时间:" );
        int a = 0;
        int b = 5/a;
    }
}
