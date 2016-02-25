package com.smapley.education.fragment;

import android.app.ActionBar;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.smapley.education.R;
import com.smapley.education.http.HttpUtils;
import com.smapley.education.utils.MyData;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Smapley on 2015/4/21.
 */
public class SyllabusFragment extends Fragment {
    private LinearLayout mainlayout;
    private int week = 6;
    private int day = 9;
    private String content;
    private int scrW;
    private int scrH;
    private DisplayMetrics dm;
    private float downX;
    private float downY;
    private float upX;
    private float upY;
    private final int JD = 100;
    private final int JDS = 200;
    private View contentView;
    private final int GETDATA = 1;
    private final int UPDATA = 2;
    private final int INIT = 3;
    private SharedPreferences sp;
    private AddCourseFragment addCourseFragment;
    private String TAG = "SyllabusFragment";
    private TextView save;
    private LinearLayout back;
    private TextView backtext;
    private TextView tag;
    private ProgressDialog dialog;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        contentView = inflater.inflate(R.layout.syllabus, container, false);
        sp = getActivity().getSharedPreferences(MyData.SP_USER, getActivity().MODE_PRIVATE);

        dialog = new ProgressDialog(getActivity());
        dialog.setTitle(getString(R.string.tips));
        dialog.setMessage(getString(R.string.connect));

        dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        scrW = dm.widthPixels;
        scrH = dm.heightPixels;
        initFragment();
        initView(contentView);
        if (MyData.SYLL_BACK == 0) {
            MyData.SYLL_SELEC = getString(R.string.syl_null);
            Log.i(TAG, "------------------->>0");
            content = getString(R.string.syl_null);
            setStirng(day, week, content);
            getData();
        }
        if (MyData.SYLL_BACK == 1) {
            Log.i(TAG, "------------------->>1");
            setStirng(MyData.SYLL_DAY, MyData.SYLL_WEEK, MyData.SYLL);
        }

        return contentView;
    }

    private void initFragment() {
        addCourseFragment = new AddCourseFragment();
    }

    private void initView(View view) {
        mainlayout = (LinearLayout) view.findViewById(R.id.syl_main);
        save = (TextView) view.findViewById(R.id.title_more);
        save.setText(R.string.save);
        back = (LinearLayout) view.findViewById(R.id.title_back);
        backtext = (TextView) view.findViewById(R.id.title_backtext);
        back.setVisibility(View.VISIBLE);
        backtext.setText(R.string.main_first);
        tag = (TextView) view.findViewById(R.id.title_title);
        tag.setText(R.string.syl_tag);
        MyData.BACKSTRING = tag.getText().toString();
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getFragmentManager().popBackStack();
            }
        });

        if (MyData.UTYPE == 1) {
            save.setVisibility(View.VISIBLE);
            save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.show();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            HashMap map = new HashMap();
                            map.put("tphone", sp.getString("tphone", ""));
                            map.put("salt", MyData.getKey());
                            map.put("w", MyData.SYLL_WEEK - 1);
                            map.put("d", MyData.SYLL_DAY - 1);
                            map.put("content", MyData.SYLL);
                            mhandler.obtainMessage(UPDATA, HttpUtils.updata(map, MyData.URL_UPDATATEATIMETABLE)).sendToTarget();
                        }
                    }).start();
                }
            });
        }

    }

    private void getData() {

        dialog.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                HashMap map = new HashMap();
                map.put("salt", MyData.getKey());
                map.put("tphone", sp.getString("tphone", ""));
                mhandler.obtainMessage(GETDATA, HttpUtils.updata(map, MyData.URL_GETTIMETABLE)).sendToTarget();
            }
        }).start();
    }

    private Handler mhandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            try {
                switch (msg.what) {
                    case GETDATA:
                        dialog.dismiss();
                        Map resultmap = JSON.parseObject(msg.obj.toString(), new TypeReference<Map>() {
                        });
                        if (resultmap.get("count").toString().equals("0")) {
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    HashMap map = new HashMap();
                                    map.put("tphone", sp.getString("tphone", ""));
                                    map.put("salt", MyData.getKey());
                                    mhandler.obtainMessage(INIT, HttpUtils.updata(map, MyData.URL_ADDTEATIMETABLE)).sendToTarget();
                                }
                            }).start();
                        } else {
                            Map map = JSON.parseObject(resultmap.get("data").toString(), new TypeReference<Map>() {
                            });
                            week = Integer.parseInt(map.get("week").toString());
                            day = Integer.parseInt(map.get("day").toString());
                            content = map.get("content").toString();
                            setStirng(day + 1, week + 1, content);
                        }
                        break;
                    case UPDATA:
                        dialog.dismiss();
                        Map map2 = JSON.parseObject(msg.obj.toString(), new TypeReference<Map>() {
                        });
                        if (Integer.parseInt(map2.get("count").toString()) > 0) {
                            Toast.makeText(getActivity(), R.string.saveed, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getActivity(), R.string.count0, Toast.LENGTH_SHORT).show();

                        }
                        break;

                    case INIT:
                        Map map3 = JSON.parseObject(msg.obj.toString(), new TypeReference<Map>() {
                        });
                        if (map3 != null && !map3.isEmpty()) {
                            if (Integer.parseInt(map3.get("newid").toString()) > 0) {
                                getData();
                            }
                        }
                        break;
                }
            } catch (Exception e) {
                try {
                    Toast.makeText(getActivity(), R.string.connectfild, Toast.LENGTH_SHORT).show();
                } catch (Exception e2) {

                }
            }
        }
    };

    private void setStirng(int day, int week, String content) {

        String[] dataString = content.split(",");
        String[][] data = new String[8][10];
        String[] dayString = getString(R.string.syl_day).split(",");
        String[] weekString = getString(R.string.syl_weeek).split(",");
        int size = 0;
        for (int a = 0; a < 8; a++) {
            for (int z = 0; z < 10; z++) {
                if (z == 0) {
                    data[a][z] = weekString[a];
                } else if (a == 0) {
                    data[a][z] = dayString[z];
                } else {
                    if (a >= week || z >= day || size == dataString.length) {
                        data[a][z] = getString(R.string.syl_null);
                    } else {

                        data[a][z] = dataString[size];
                        size++;
                        if (size < dataString.length) {
                            MyData.SYLL = MyData.SYLL + ",";
                        }
                    }
                }


            }
        }
        if (MyData.SYLL_BACK == 1) {
            data[MyData.SYLL_NDAY][MyData.SYLL_NWEEK] = MyData.SYLL_SELEC;
        }
        createView(day, week, data);
    }

    private void createView(final int day, final int week, final String[][] content) {

        MyData.SYLL_DAY = day;
        MyData.SYLL_WEEK = week;
        MyData.SYLL = "";
        for (int i = 1; i < week; i++) {
            for (int j = 1; j < day; j++) {
                MyData.SYLL = MyData.SYLL + content[i][j];
                if ((week - 1) != i || (day - 1) != j) {
                    MyData.SYLL = MyData.SYLL + ",";
                }
            }
        }

        if (day > 10 || week > 8) {
            Toast.makeText(getActivity(), R.string.syl_max, Toast.LENGTH_SHORT).show();
        } else if (day < 2 || week < 2) {
            Toast.makeText(getActivity(), R.string.syl_min, Toast.LENGTH_SHORT).show();
        } else {
            mainlayout.removeAllViews();
            for (int i = 0; i < day; i++) {
                LinearLayout lin = new LinearLayout(getActivity());
                ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (scrH - 400) / day);
                for (int z = 0; z < week; z++) {

                    Button button = new Button(getActivity());
                    button.setText(content[z][i]);
                    button.setBackgroundResource(R.drawable.textview_edge);
                    ActionBar.LayoutParams params1 = new ActionBar.LayoutParams(scrW / week, ViewGroup.LayoutParams.MATCH_PARENT);
                    lin.addView(button, params1);
                    if (MyData.UTYPE == 1) {
                        button.setOnTouchListener(new MyTouch(day, week, z, i, content));
                    }
                }

                mainlayout.addView(lin, params);
            }
        }
    }

    private class MyTouch implements View.OnTouchListener {
        private int nday;
        private int nweek;
        private int day;
        private int week;

        private String[][] content;

        MyTouch(int day, int week, int nday, int nweek, String[][] content) {
            this.nday = nday;
            this.nweek = nweek;
            this.day = day;
            this.week = week;
            this.content = content;
        }

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    downX = motionEvent.getX();
                    downY = motionEvent.getY();
                    break;
                case MotionEvent.ACTION_UP:
                    upX = motionEvent.getX();
                    upY = motionEvent.getY();
                    if (Math.abs(downX - upX) > JD && downX - upX < JDS && Math.abs(downY - upY) < JD) {
                        createView(day, week - 1, content);
                        Toast.makeText(getActivity(), R.string.syl_item1, Toast.LENGTH_SHORT).show();
                    } else if (Math.abs(downX - upX) > JD && downX - upX > JDS && Math.abs(downY - upY) < JD) {
                        createView(day, week + 1, content);
                        Toast.makeText(getActivity(), R.string.syl_item2, Toast.LENGTH_SHORT).show();
                    } else if (Math.abs(downY - upY) > JD && downY - upY < JDS && Math.abs(downX - upX) < JD) {
                        createView(day - 1, week, content);
                        Toast.makeText(getActivity(), R.string.syl_item3, Toast.LENGTH_SHORT).show();
                    } else if (Math.abs(downY - upY) > JD && downY - upY > JDS && Math.abs(downX - upX) < JD) {
                        createView(day + 1, week, content);
                        Toast.makeText(getActivity(), R.string.syl_item4, Toast.LENGTH_SHORT).show();
                    } else if (Math.abs(downX - upX) < JD && Math.abs(downY - upY) < JD) {
                        MyData.ADDCOURSETYPE = 2;
                        MyData.GRADELIST.clear();
                        MyData.SYLL_NDAY = nday;
                        MyData.SYLL_NWEEK = nweek;
                        getActivity().getFragmentManager().beginTransaction().remove(SyllabusFragment.this)
                                .add(R.id.main_content, addCourseFragment, "addCourseFragment")
                                .addToBackStack(null).commit();
                    }
                {

                }
                break;
            }

            return true;
        }
    }
}

