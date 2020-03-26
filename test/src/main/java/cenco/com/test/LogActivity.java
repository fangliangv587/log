package cenco.com.test;

import android.Manifest;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.cenco.log.LogUtils;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class LogActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);


//        int a = 0;
//        int b = 5/a;
        permission();
    }

    private void permission() {
        final RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions
                .request(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_PHONE_STATE)
                .subscribe(result->{
                    Log.i("testlogapp","permission result:"+result);
                    if (result){
                        init();
                    }else {

                    }
                });
    }

    private void init() {
        LogUtils.v("mmmmmm");
        LogUtils.i("helloworld");
        LogUtils.d("zhang","hahaha");
        LogUtils.w("xin","风雨");
        LogUtils.e("zhong","填写");
        LogUtils.d("----显示时间:" );
    }

    private void test(){
        List<? extends Number> list = Arrays.asList(1,2.1f);
        Number m=1;
//        list.add();
        Number number = list.get(1);
        list.remove(1);

        List<? super Integer> list1 = Arrays.asList(1,2.1f);
        list1.get(0);
        list1.add(2);

        Collections.emptyList();
    }

    public void test(View view) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    LogUtils.w("testaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
                }
            }
        }).start();
    }
}
