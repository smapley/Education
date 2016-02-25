package com.smapley.education.fragment;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
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

/**
 * Created by Smapley on 2015/4/19.
 */
public class ListViewFragment extends Fragment {
    private ListView listView;
    private LinearLayout back;
    private TextView backtext;
    private TextView add;
    private View contentview;
    private TextView tag;
    private int SRC = 0;
    private AddNoticeFragment addNoticeFragment;
    public static final int NOTICE = 0;
    public static final int HONOR = 1;
    public static final int ABSENT = 3;
    public static final int HOMEWORK = 4;
    public static final int LEAVE = 5;
    private final int GETDATA = 1;
    private final int DELECT = 2;
    private SharedPreferences sp;
    private List list;
    private ProgressDialog dialog;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        contentview = inflater.inflate(R.layout.listview, container, false);

        sp = getActivity().getSharedPreferences(MyData.SP_USER, getActivity().MODE_PRIVATE);
        dialog = new ProgressDialog(getActivity());
        dialog.setTitle(getString(R.string.tips));
        dialog.setMessage(getString(R.string.connect));
        SRC = MyData.SRC;

        initFragment();
        initView(contentview);
        setView();
        getData();
        return contentview;
    }

    private void initFragment() {
        addNoticeFragment = new AddNoticeFragment();
    }

    private void initView(View view) {
        listView = (ListView) view.findViewById(R.id.listview_list);
        back = (LinearLayout) view.findViewById(R.id.title_back);
        backtext = (TextView) view.findViewById(R.id.title_backtext);
        back.setVisibility(View.VISIBLE);
        backtext.setText(R.string.main_first);
        add = (TextView) view.findViewById(R.id.title_more);
        tag = (TextView) view.findViewById(R.id.title_title);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getFragmentManager().popBackStack();
            }
        });

    }

    private void setView() {


        switch (SRC) {
            case NOTICE:
                tag.setText(getActivity().getString(R.string.notice_tag));
                add.setText(getActivity().getString(R.string.add));
                if (MyData.UTYPE == 1 || (MyData.UTYPE == 2 && MyData.PROV == 1)) {
                    add.setVisibility(View.VISIBLE);
                    add.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            getActivity().getFragmentManager().beginTransaction().remove(ListViewFragment.this)
                                    .add(R.id.main_content, addNoticeFragment, "addNoticeFragment")
                                    .addToBackStack(null).commit();
                        }
                    });
                }

                break;

            case ABSENT:
                tag.setText(getActivity().getString(R.string.absent_tag));
                add.setText(getActivity().getString(R.string.add));
                if (MyData.UTYPE == 1 || (MyData.UTYPE == 2 && MyData.PROV == 1)) {
                    add.setVisibility(View.VISIBLE);
                    add.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            getActivity().getFragmentManager().beginTransaction().remove(ListViewFragment.this)
                                    .add(R.id.main_content, addNoticeFragment, "addNoticeFragment")
                                    .addToBackStack(null).commit();
                        }
                    });
                }
                break;
            case HONOR:
                tag.setText(getActivity().getString(R.string.honor_tag));
                add.setText(getActivity().getString(R.string.add));
                if (MyData.UTYPE == 1) {
                    add.setVisibility(View.VISIBLE);
                    add.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            getActivity().getFragmentManager().beginTransaction().remove(ListViewFragment.this)
                                    .add(R.id.main_content, addNoticeFragment, "addNoticeFragment")
                                    .addToBackStack(null).commit();
                        }
                    });
                }
                break;
            case LEAVE:
                tag.setText(getActivity().getString(R.string.leave_tag));
                add.setText(getActivity().getString(R.string.add));
                if (MyData.UTYPE == 0) {
                    add.setVisibility(View.VISIBLE);
                    add.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            getActivity().getFragmentManager().beginTransaction().remove(ListViewFragment.this)
                                    .add(R.id.main_content, addNoticeFragment, "addNoticeFragment")
                                    .addToBackStack(null).commit();
                        }
                    });
                }
                break;

        }


        MyData.BACKSTRING = tag.getText().toString();
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getFragmentManager().popBackStack();
            }
        });
    }

    private void getData() {
        dialog.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                HashMap map = new HashMap();
                map.put("salt", MyData.getKey());
                map.put("tphone", sp.getString("tphone", ""));
                map.put("msg_type", SRC);
                mhandler.obtainMessage(GETDATA, HttpUtils.updata(map, MyData.URL_GETMSGTITLELIST)).sendToTarget();
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
                        list = JSON.parseObject(msg.obj.toString(), new TypeReference<List>() {
                        });

                        listView.setAdapter(new SimpleAdapter(getActivity(), list, R.layout.layout_listview_item,
                                new String[]{"title", "body"},
                                new int[]{R.id.listview_title, R.id.listview_content}));

                        if ((MyData.UTYPE == 1 && SRC != LEAVE) || (MyData.UTYPE == 2 && MyData.PROV == 1 && SRC != HONOR && SRC != LEAVE)) {
                            listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                                @Override
                                public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int i, long l) {

                                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                    builder.setTitle(R.string.delectmag);
                                    builder.setPositiveButton(R.string.delect, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int position) {
                                            dialog.show();
                                            new Thread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    HashMap map = new HashMap();
                                                    map.put("salt", MyData.getKey());
                                                    map.put("mid", ((Map) list.get(i)).get("mid").toString());
                                                    mhandler.obtainMessage(DELECT, HttpUtils.updata(map, MyData.URL_DELMSG)).sendToTarget();
                                                }
                                            }).start();
                                        }
                                    });
                                    builder.setNegativeButton(R.string.cancel, null);
                                    builder.create().show();


                                    return false;
                                }
                            });

                        }
                        break;
                    case DELECT:
                        Map map = JSON.parseObject(msg.obj.toString(), new TypeReference<Map>() {
                        });

                        if (Integer.parseInt(map.get("count").toString()) > 0) {
                            Toast.makeText(getActivity(), R.string.delected, Toast.LENGTH_SHORT).show();
                            getData();
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
}
