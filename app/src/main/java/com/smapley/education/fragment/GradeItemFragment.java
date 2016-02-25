package com.smapley.education.fragment;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
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
 * Created by Smapley on 2015/4/20.
 */
public class GradeItemFragment extends Fragment {
    private ListView listView;
    private LinearLayout back;
    private TextView backtext;
    private TextView tag;
    private TextView add;
    private View contentView;
    private final int GETDATA = 1;
    private final int UPDATA = 2;
    private final int INITDATA = 3;
    private final int INITDATA2 = 4;
    private final int DELECT = 5;
    private SharedPreferences sp;
    private GradeListFragment gradeListFragment;
    private AddGradefragment addGradefragment;
    private GradeContentFragment gradeContentFragment;
    private ProgressDialog dialog;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        contentView = inflater.inflate(R.layout.gradeitem, container, false);
        sp = getActivity().getSharedPreferences(MyData.SP_USER, getActivity().MODE_PRIVATE);

        dialog = new ProgressDialog(getActivity());
        dialog.setTitle(getString(R.string.tips));
        dialog.setMessage(getString(R.string.connect));

        initFragment();
        initView(contentView);
        getData();
        return contentView;
    }

    private void initFragment() {
        gradeListFragment = new GradeListFragment();
        addGradefragment = new AddGradefragment();
        gradeContentFragment = new GradeContentFragment();
    }


    private void initView(View view) {


        listView = (ListView) view.findViewById(R.id.gradeitem_list);
        back = (LinearLayout) view.findViewById(R.id.title_back);
        backtext = (TextView) view.findViewById(R.id.title_backtext);
        tag = (TextView) view.findViewById(R.id.title_title);
        back.setVisibility(View.VISIBLE);
        backtext.setText(MyData.GRADENAME);
        tag.setText(R.string.gradeitem_tag);
        add = (TextView) view.findViewById(R.id.title_more);
        if (MyData.UTYPE == 1 || (MyData.UTYPE == 2 && MyData.PROV == 1)) {
            add.setVisibility(View.VISIBLE);
            add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle(getString(R.string.addexam_dialog));
                    LayoutInflater inflater = getActivity().getLayoutInflater();
                    View layout = inflater.inflate(R.layout.layout_addexam, null);
                    final EditText name = (EditText) layout.findViewById(R.id.addexam_name);
                    builder.setView(layout)
                            // Add action buttons  
                            .setPositiveButton(getString(R.string.Okay),
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int id) {

                                            dialog.show();
                                            new Thread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    MyData.EXAMNAME = name.getText().toString();
                                                    HashMap map = new HashMap();
                                                    map.put("salt", MyData.getKey());
                                                    map.put("tphone", sp.getString("tphone", ""));
                                                    map.put("title", name.getText().toString());
                                                    map.put("subject", MyData.GRADENAME);
                                                    mhandler.obtainMessage(UPDATA, HttpUtils.updata(map, MyData.URL_ADDEXAM)).sendToTarget();

                                                }
                                            }).start();

                                        }
                                    }).setNegativeButton(getString(R.string.cancel), null).show();

                }
            });
        }
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
                map.put("subject", MyData.GRADENAME);

                mhandler.obtainMessage(GETDATA, HttpUtils.updata(map, MyData.URL_GETEXAMLIST)).sendToTarget();
            }
        }).start();


    }

    private void initData() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                HashMap map = new HashMap();
                map.put("salt", MyData.getKey());
                map.put("phone", sp.getString("tphone", ""));
                mhandler.obtainMessage(INITDATA, HttpUtils.updata(map, MyData.URL_GETSTULIST)).sendToTarget();
            }
        }).start();
    }

    private void initData2(final List<Map> list) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HashMap map = new HashMap();
                map.put("salt", MyData.getKey());
                map.put("eid", MyData.EID);
                String stu_str = "";
                for (int i = 0; i < list.size(); i++) {

                    stu_str = stu_str + list.get(i).get("name").toString();
                    if (i + 1 < list.size()) {
                        stu_str = stu_str + ",";
                    }
                }
                map.put("stu_str", stu_str);
                mhandler.obtainMessage(INITDATA2, HttpUtils.updata(map, MyData.URL_ADDSCORE)).sendToTarget();


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
                        final List list = JSON.parseObject(msg.obj.toString(), new TypeReference<List>() {
                        });
                        listView.setAdapter(new SimpleAdapter(getActivity(), list, R.layout.layout_gradeitem_item,
                                new String[]{
                                        "title"
                                },
                                new int[]{
                                        R.id.gradeitem_title
                                }));
                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                MyData.EID = Integer.parseInt(((Map) list.get(i)).get("eid").toString());
                                MyData.EXAMNAME = ((Map) list.get(i)).get("title").toString();
                                MyData.STUNAME = sp.getString("name", "");
                                if (MyData.UTYPE == 1) {
                                    getActivity().getFragmentManager().beginTransaction().hide(GradeItemFragment.this)
                                            .add(R.id.main_content, gradeListFragment, "gradeListFragment")
                                            .addToBackStack(null).commit();
                                } else {
                                    getActivity().getFragmentManager().beginTransaction().hide(GradeItemFragment.this)
                                            .add(R.id.main_content, gradeContentFragment, "gradeContentFragment")
                                            .addToBackStack(null).commit();
                                }

                            }
                        });
                        if (MyData.UTYPE == 1) {
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
                                                    map.put("eid", ((Map) list.get(i)).get("eid").toString());
                                                    mhandler.obtainMessage(DELECT, HttpUtils.updata(map, MyData.URL_DELEXAM)).sendToTarget();
                                                }
                                            }).start();
                                        }
                                    });
                                    builder.setNegativeButton(R.string.cancel, null);
                                    builder.create().show();
                                    return true;
                                }
                            });
                        }
                        break;


                    case DELECT:
                        dialog.dismiss();
                        Map map3 = JSON.parseObject(msg.obj.toString(), new TypeReference<Map>() {
                        });
                        if (Integer.parseInt(map3.get("count").toString()) > 0) {
                            getData();
                        }
                        break;
                    case UPDATA:
                        Map map = JSON.parseObject(msg.obj.toString(), new TypeReference<Map>() {
                        });
                        if (Integer.parseInt(map.get("newid").toString()) > 0) {
                            MyData.EID = Integer.parseInt(map.get("newid").toString());
                            initData();
                        }
                        break;
                    case INITDATA:
                        List list0 = JSON.parseObject(msg.obj.toString(), new TypeReference<List>() {
                        });
                        initData2(list0);
                        break;

                    case INITDATA2:
                        dialog.dismiss();
                        Map map2 = JSON.parseObject(msg.obj.toString(), new TypeReference<Map>() {
                        });
                        if (map2 != null && !map2.isEmpty()) {
                            Toast.makeText(getActivity(), R.string.added, Toast.LENGTH_SHORT).show();
                            MyData.FIRSTADDGRADE = true;
                            getActivity().getFragmentManager().beginTransaction().remove(GradeItemFragment.this)
                                    .add(R.id.main_content, addGradefragment, "addGradefragment")
                                    .addToBackStack(null).commit();
                        }
                        break;
                }
            } catch (Exception e) {
                dialog.dismiss();
                try {
                    Toast.makeText(getActivity(), R.string.connectfild, Toast.LENGTH_SHORT).show();
                } catch (Exception e2) {

                }
            }
        }
    };
}
