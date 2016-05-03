package com.smapley.education.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.smapley.education.R;
import com.smapley.education.fragment.MainFragment;
import com.smapley.education.fragment.SetFragment;
import com.smapley.education.utils.Exit;
import com.smapley.education.utils.MyData;

import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends Activity {

    /**
     * 是否退出
     */
    private static Boolean isExit = false;
    private MainFragment mainFragment;
    private SetFragment setFragment;
    private ImageView main;
    private ImageView set;
    private LinearLayout mainlayout;
    private LinearLayout setlayout;
    private Bitmap bmp = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        initVIew();


    }


    private void initVIew() {
        mainFragment = new MainFragment();
        setFragment = new SetFragment();
        mainlayout = (LinearLayout) findViewById(R.id.main_content);
        setlayout = (LinearLayout) findViewById(R.id.set_content);
        getFragmentManager().beginTransaction().replace(R.id.main_content, mainFragment, "mainFragment").commit();
        getFragmentManager().beginTransaction().replace(R.id.set_content, setFragment, "setFragment").commit();

        main = (ImageView) findViewById(R.id.main);
        set = (ImageView) findViewById(R.id.set);
        main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                main.setImageResource(R.drawable.main_checked);
                set.setImageResource(R.drawable.set_normal);

                setlayout.setVisibility(View.GONE);
                if (mainlayout.getVisibility() == View.VISIBLE) {
                    int num = getFragmentManager().getBackStackEntryCount();
                    for (int i = 0; i < num; i++) {
                        getFragmentManager().popBackStack();
                    }
                } else {
                    mainlayout.setVisibility(View.VISIBLE);
                    if (MyData.UTYPECHANGED) {
                        int num = getFragmentManager().getBackStackEntryCount();
                        for (int i = 0; i < num; i++) {
                            getFragmentManager().popBackStack();
                        }
                        //   mainFragment.onchange();
                        MyData.UTYPECHANGED = false;
                    }
                }
                mainFragment.onchange();


            }
        });
        set.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                set.setImageResource(R.drawable.set_checked);
                main.setImageResource(R.drawable.main_normal);

                mainlayout.setVisibility(View.GONE);
                if (setlayout.getVisibility() == View.GONE) {
                    setlayout.setVisibility(View.VISIBLE);
                } else {
                    int num = getFragmentManager().getBackStackEntryCount();
                    for (int i = 0; i < num; i++) {
                        getFragmentManager().popBackStack();
                    }
                }

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK&&requestCode == 0) {
            MyData.getPhoto.onResult();
        }
    }

    /*
         * 监听返回键，菜单打开时，按一次关闭菜单
         */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        super.onKeyDown(keyCode, event);
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            if (getFragmentManager().getBackStackEntryCount() == 0) {
                exitBy2Click();
            } else {
                getFragmentManager().popBackStack();
            }
        }
        return false;

    }

    /**
     * 两次返回键，退出程序
     */
    public void exitBy2Click() {
        Timer tExit = null;
        if (isExit == false) {
            isExit = true; // 准备退出
            Toast.makeText(this, R.string.Exit, Toast.LENGTH_SHORT).show();
            tExit = new Timer();
            tExit.schedule(new TimerTask() {
                @Override
                public void run() {
                    isExit = false; // 取消退出
                }
            }, 2000); // 如果2秒钟内没有按下返回键，则启动定时器取消掉刚才执行的任务

        } else {
            new Exit();
            Exit.getInstance().exit();
        }
    }
}
