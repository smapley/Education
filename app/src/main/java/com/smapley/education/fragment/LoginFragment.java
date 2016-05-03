package com.smapley.education.fragment;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.smapley.education.R;
import com.smapley.education.http.HttpUtils;
import com.smapley.education.utils.MyData;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

/**
 * Created by Smapley on 2015/4/18.
 */
public class LoginFragment extends Fragment implements View.OnClickListener {

    private TextView teacher;
    private TextView parents;
    private TextView student;
    private EditText phone;
    private EditText key;
    private Button send;
    private Button testing;
    private int User = -1;
    private String phoneString;
    private Boolean CANSEND = true;
    private final int TEACHER = 1;
    private final int PARENTS = 0;
    private final int STUDENT = 2;
    private final int MSG_END = 6;
    private final int MSG_RESULT = 3;
    private final int MSG_TESTERR = 5;
    private final int MSG_SLEEP = 4;
    private View contentView;

    private EventHandler eventHandler;
    private SharedPreferences sp;
    private final String TAG = "Login";
    private Boolean STOP = false;
    private ProgressDialog dialog;
    private TextView tag;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        contentView = inflater.inflate(R.layout.login, container, false);
        sp = getActivity().getSharedPreferences(MyData.SP_USER, Context.MODE_PRIVATE);

        initSMS();
        initView(contentView);
        return contentView;
    }


    private void initSMS() {
        SMSSDK.initSDK(getActivity(), "63a4737b780c", "b5e1d1019f8707d0225ac35725c11e0b");
        eventHandler = new EventHandler() {

            @Override
            public void afterEvent(int event, int result, Object data) {
                if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                    //提交验证码成功
                    if (result == SMSSDK.RESULT_COMPLETE) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                HashMap<String, Object> map = new HashMap();
                                map.put("salt", MyData.getKey());
                                map.put("phone", phoneString);
                                map.put("utype", User);
                                String result = HttpUtils.updata(map, MyData.URL_REG);
                                mHandler.obtainMessage(MSG_RESULT, result).sendToTarget();

                            }
                        }).start();

                    } else {
                        mHandler.obtainMessage(MSG_TESTERR).sendToTarget();
                    }
                }
            }
        };
        SMSSDK.registerEventHandler(eventHandler); //注册短信回调
    }

    private void initView(View view) {
        tag = (TextView) view.findViewById(R.id.title_title);
        tag.setText(R.string.login_tag);
        teacher = (TextView) view.findViewById(R.id.login_teacher);
        parents = (TextView) view.findViewById(R.id.login_parents);
        student = (TextView) view.findViewById(R.id.login_student);
        phone = (EditText) view.findViewById(R.id.login_phone);
        key = (EditText) view.findViewById(R.id.login_key);
        send = (Button) view.findViewById(R.id.login_send);
        testing = (Button) view.findViewById(R.id.login_testing);

        teacher.setOnClickListener(this);
        parents.setOnClickListener(this);
        student.setOnClickListener(this);
        send.setOnClickListener(this);
        testing.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.login_teacher:
                teacher.setBackgroundResource(R.drawable.textview_background);
                parents.setBackgroundResource(R.drawable.textview_edge);
                student.setBackgroundResource(R.drawable.textview_edge);
                User = TEACHER;
                break;
            case R.id.login_parents:
                teacher.setBackgroundResource(R.drawable.textview_edge);
                parents.setBackgroundResource(R.drawable.textview_background);
                student.setBackgroundResource(R.drawable.textview_edge);
                User = PARENTS;
                break;
            case R.id.login_student:
                teacher.setBackgroundResource(R.drawable.textview_edge);
                parents.setBackgroundResource(R.drawable.textview_edge);
                student.setBackgroundResource(R.drawable.textview_background);
                User = STUDENT;
                break;
            case R.id.login_send:
                if (CANSEND) {
                    phoneString = phone.getText().toString();
                    if (User == -1) {
                        Toast.makeText(getActivity(), R.string.login_toast1, Toast.LENGTH_SHORT).show();
                    } else if (phoneString.length() != 11) {
                        Toast.makeText(getActivity(), R.string.login_toast3, Toast.LENGTH_SHORT).show();
                    } else {
                        SMSSDK.getVerificationCode("86", phoneString);
                        CANSEND = false;
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    int time = 60;
                                    while (time > 0) {
                                        Thread.sleep(1000);
                                        time--;
                                        if (!STOP) {
                                            mHandler.obtainMessage(MSG_SLEEP, time).sendToTarget();
                                        } else {
                                            break;
                                        }

                                    }
                                    mHandler.obtainMessage(MSG_END).sendToTarget();
                                } catch (Exception e) {

                                }

                            }
                        }).start();
                    }
                }
                break;

            case R.id.login_testing:
                dialog = ProgressDialog.show(getActivity(), getString(R.string.tips), getString(R.string.login));
                phoneString = phone.getText().toString();
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        HashMap<String, Object> map = new HashMap();
//                        map.put("salt", MyData.getKey());
//                        map.put("phone", phoneString);
//                        map.put("utype", User);
//                        String result = HttpUtils.updata(map, MyData.URL_REG);
//                        mHandler.obtainMessage(MSG_RESULT, result).sendToTarget();
//
//                    }
//                }).start();
                //    } else {
                SMSSDK.submitVerificationCode("86", phoneString, key.getText().toString());
                //   }
                break;
        }

    }

    private void getData(final int type) {
        dialog.setMessage(getString(R.string.connect));
        new Thread(new Runnable() {
            /**
             * 0=家长,1=教师,2=学生      
             */
            @Override
            public void run() {
                HashMap map = new HashMap();
                map.put("salt", MyData.getKey());


                String url = "";
                switch (type) {
                    case PARENTS:
                        map.put("jphone", phoneString);
                        url = MyData.URL_GETSTUTABLE;
                        break;
                    case TEACHER:
                        map.put("phone", phoneString);
                        url = MyData.URL_GETSTINFO;
                        break;
                    case STUDENT:
                        map.put("stuphone", phoneString);
                        url = MyData.URL_GETSTUTABLEEITHSTUPHONE;
                        break;
                }

                mHandler.obtainMessage(type, HttpUtils.updata(map, url)).sendToTarget();
            }
        }).start();
    }

    private Handler mHandler = new Handler() {
        // 重写handleMessage()方法，此方法在UI线程运行
        @Override
        public void handleMessage(Message msg) {
            try {
                switch (msg.what) {
                    case MSG_RESULT:
                        HashMap<String, String> resultmap = JSON.parseObject(msg.obj.toString(),
                                new TypeReference<HashMap<String, String>>() {
                                });
                        if (resultmap != null && !resultmap.isEmpty()) {
                            final int type = Integer.parseInt(resultmap.get("utype").toString());
                            String phone = null;
                            SharedPreferences.Editor edit = sp.edit();
                            edit.putString("uid", resultmap.get("uid").toString());
                            edit.putInt("utype", type);
                            switch (type) {
                                case PARENTS:
                                    phone = "jphone";
                                    break;
                                case TEACHER:
                                    phone = "tphone";
                                    break;
                                case STUDENT:
                                    phone = "stuphone";
                                    break;
                            }
                            edit.putString(phone, phoneString);
                            edit.putString("phone", phoneString);
                            edit.commit();
                            MyData.UTYPE = type;
                            getData(type);
                        }
                        break;
                    case TEACHER:
                    case STUDENT:
                        dialog.dismiss();
                        Map<String, Object> map2 = JSON.parseObject(msg.obj.toString(), new TypeReference<Map>() {
                        });
                        if (map2 != null && !map2.isEmpty()) {
                            for (Map.Entry<String, Object> entry : map2.entrySet()) {

                                SharedPreferences.Editor editor = sp.edit();
                                editor.putString(entry.getKey(), entry.getValue().toString());
                                editor.commit();
                            }
                        }
                        STOP = true;
                        Toast.makeText(getActivity(), R.string.login_ok, Toast.LENGTH_SHORT).show();
                        getActivity().getFragmentManager().popBackStack();
                        MyData.UTYPECHANGED = true;
                        break;
                    case PARENTS:
                        dialog.dismiss();
                        String childern = msg.obj.toString();
                        List<Map<String, Object>> list = JSON.parseObject(childern, new TypeReference<List>() {
                        });
                        if (list != null && !list.isEmpty()) {
                            SharedPreferences.Editor editor = sp.edit();
                            editor.putString("childrensize", list.size() + "");
                            editor.putString("children", childern);
                            editor.commit();
                            Map<String, Object> map3 = JSON.parseObject(sp.getString("children", ""), new TypeReference<List<Map<String, Object>>>() {
                            }).get(0);
                            for (Map.Entry<String, Object> entry : map3.entrySet()) {

                                SharedPreferences.Editor editor2 = sp.edit();
                                editor2.putString(entry.getKey(), entry.getValue().toString());
                                editor2.commit();
                            }
                        }
                        STOP = true;
                        Toast.makeText(getActivity(), R.string.login_ok, Toast.LENGTH_SHORT).show();
                        MyData.UTYPECHANGED = true;
                        getActivity().getFragmentManager().popBackStack();

                        break;

                    case MSG_END:
                        send.setText(R.string.login_send);
                        CANSEND = true;
                        break;
                    case MSG_TESTERR:
                        dialog.dismiss();
                        mHandler.obtainMessage(MSG_END).sendToTarget();
                        Toast.makeText(getActivity(), R.string.login_toast4, Toast.LENGTH_SHORT).show();
                        break;
                    case MSG_SLEEP:

                        String text = getString(R.string.login_sends);
                        send.setText(text + "( " + msg.obj + "s )");

                        break;

                }
            } catch (Exception e) {
                dialog.dismiss();
                try {
                    Toast.makeText(getActivity(), R.string.connectfild, Toast.LENGTH_SHORT).show();
                } catch (Exception e1) {

                }
            }
        }
    };
}
