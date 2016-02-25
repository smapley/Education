package com.smapley.education.fragment;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableRow;
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
 * Created by Smapley on 2015/4/18.
 */
public class SetFragment extends Fragment {
    private TextView item1_text;
    private TextView item2_text;
    private TextView item3_text;
    private TextView item4_text;
    private TextView bar1;
    private TextView bar2;
    private TextView bar3;
    private TableRow item1;
    private TableRow item2;
    private TableRow item3;
    private TableRow item4;
    private TableRow logout;
    private TextView logout_text;
    private SharedPreferences sp;
    private int utype;
    private final int ERRON = -1;
    private final int TEACHER = 1;
    private final int PARENTS = 0;
    private final int STUDENT = 2;
    private final int LOGOUT = 3;
    private View countView;
    private LoginFragment loginFragment;
    private SchoolFragment schoolFragment;
    private StudentFragment studentFragment;
    private AboutFragment aboutFragment;
    private AddressListFragment addressListFragment;
    private BoundFragment boundFragment;
    private TextView tag;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        countView = inflater.inflate(R.layout.set, container, false);
        sp = getActivity().getSharedPreferences(MyData.SP_USER, Context.MODE_PRIVATE);
        utype = sp.getInt("utype", -1);
        initFragment();
        initView(countView);
        drowView();
        return countView;
    }

    private void initFragment() {
        loginFragment = new LoginFragment();
        schoolFragment = new SchoolFragment();
        studentFragment = new StudentFragment();
        aboutFragment = new AboutFragment();
        addressListFragment = new AddressListFragment();
        boundFragment = new BoundFragment();
    }

    private void initView(View view) {
        tag = (TextView) view.findViewById(R.id.title_title);
        tag.setText(R.string.set_tag);
        item1 = (TableRow) view.findViewById(R.id.set_item1);
        item2 = (TableRow) view.findViewById(R.id.set_item2);
        item3 = (TableRow) view.findViewById(R.id.set_item3);
        item4 = (TableRow) view.findViewById(R.id.set_item4);
        bar1 = (TextView) view.findViewById(R.id.set_bar1);
        bar2 = (TextView) view.findViewById(R.id.set_bar2);
        bar3 = (TextView) view.findViewById(R.id.set_bar3);
        item1_text = (TextView) view.findViewById(R.id.set_item1_text);
        item2_text = (TextView) view.findViewById(R.id.set_item2_text);
        item3_text = (TextView) view.findViewById(R.id.set_item3_text);
        item4_text = (TextView) view.findViewById(R.id.set_item4_text);
        logout = (TableRow) view.findViewById(R.id.set_logout);
        logout_text = (TextView) view.findViewById(R.id.set_logout_text);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (utype == ERRON) {
                    getActivity().getFragmentManager().beginTransaction().remove(SetFragment.this)
                            .add(R.id.set_content, loginFragment, "loginFragment")
                            .addToBackStack(null).commit();
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle(getString(R.string.tips));
                    builder.setMessage(getString(R.string.logout));
                    builder.setNegativeButton(R.string.cancel, null);
                    builder.setPositiveButton(R.string.Okay, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    HashMap map = new HashMap();
                                    map.put("phone", sp.getString("phone", ""));
                                    map.put("salt", MyData.getKey());
                                    mhandler.obtainMessage(LOGOUT, HttpUtils.updata(map, MyData.URL_LOGOUT)).sendToTarget();
                                }
                            }).start();

                        }
                    });
                    builder.create().show();

                }
            }
        });

        item1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (utype == TEACHER) {
                    getActivity().getFragmentManager().beginTransaction().hide(SetFragment.this)
                            .add(R.id.set_content, schoolFragment, "schoolFragment")
                            .addToBackStack(null).commit();
                } else {
                    getActivity().getFragmentManager().beginTransaction().hide(SetFragment.this)
                            .add(R.id.set_content, boundFragment, "boundFragment")
                            .addToBackStack(null).commit();

                }
            }
        });

        item2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getFragmentManager().beginTransaction().hide(SetFragment.this)
                        .add(R.id.set_content, studentFragment, "studentFragment")
                        .addToBackStack(null).commit();
            }
        });

        item3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getFragmentManager().beginTransaction().hide(SetFragment.this)
                        .add(R.id.set_content, addressListFragment, "addressListFragment")
                        .addToBackStack(null).commit();
            }
        });
        item4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getFragmentManager().beginTransaction().hide(SetFragment.this)
                        .add(R.id.set_content, aboutFragment, "aboutFragment")
                        .addToBackStack(null).commit();
            }
        });

    }

    private void drowView() {
        switch (utype) {
            case ERRON:
                item1.setVisibility(View.GONE);
                item2.setVisibility(View.GONE);
                item3.setVisibility(View.GONE);
                bar1.setVisibility(View.GONE);
                bar2.setVisibility(View.GONE);
                bar3.setVisibility(View.GONE);
                item4_text.setText(R.string.set_item0);
                logout_text.setText(R.string.set_login);

                break;
            case TEACHER:
                item1_text.setText(R.string.set_item11);
                item2_text.setText(R.string.set_item12);
                item3_text.setText(R.string.set_item13);
                item4_text.setText(R.string.set_item0);
                logout_text.setText(R.string.set_logout);
                break;
            case PARENTS:
                item1_text.setText(R.string.set_item01);
                item2.setVisibility(View.GONE);
                item3.setVisibility(View.GONE);
                bar2.setVisibility(View.GONE);
                bar3.setVisibility(View.GONE);
                item4_text.setText(R.string.set_item0);
                logout_text.setText(R.string.set_logout);

                break;
            case STUDENT:
                item1.setVisibility(View.GONE);
                item2.setVisibility(View.GONE);
                item3.setVisibility(View.GONE);
                bar1.setVisibility(View.GONE);
                bar2.setVisibility(View.GONE);
                bar3.setVisibility(View.GONE);
                item4_text.setText(R.string.set_item0);
                logout_text.setText(R.string.set_logout);
                break;
        }
    }

    private Handler mhandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            try {
                if (msg.what == LOGOUT) {
                    Map map = JSON.parseObject(msg.obj.toString(), new TypeReference<Map>() {
                    });
                    if (map.get("count").toString().equals("1")) {
                        utype = ERRON;
                        SharedPreferences.Editor editor = sp.edit();
                        editor.clear();
                        editor.commit();
                        MyData.UTYPE = -1;
                        MyData.UTYPECHANGED = true;
                        drowView();
                        int num = getFragmentManager().getBackStackEntryCount();
                        for (int j = 0; j < num; j++) {
                            getFragmentManager().popBackStack();
                        }
                    }else{
                        Toast.makeText(getActivity(), R.string.connectfild, Toast.LENGTH_SHORT).show();

                    }
                }

            } catch (Exception e) {
                try {
                    Toast.makeText(getActivity(), R.string.connectfild, Toast.LENGTH_SHORT).show();

                } catch (Exception e1) {
                }
            }

        }
    };

}
