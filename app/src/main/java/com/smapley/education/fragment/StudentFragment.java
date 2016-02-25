package com.smapley.education.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
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
 * Created by Smapley on 2015/4/18.
 */
public class StudentFragment extends Fragment implements StudentDialogFragment.OnItemClickListener, StudentDialogFragment2.OnItemClickListener2 {

    private LinearLayout back;
    private ListView listView;
    private TextView add;
    private TextView backtext;
    private TextView tag;
    private List<Map<String, Object>> list;
    private View countView;
    private final int GETDATA = 1;
    private final int UPDATA = 2;
    private final int DELECT = 4;
    private final int BOUND = 5;
    private final int ADD = 3;
    private SharedPreferences sp;
    private View contentView;
    private int PROV;
    private int SNO = 0;
    private int Sid;
    private CheckFragment checkFragment;
    private ProgressDialog dialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        contentView = inflater.inflate(R.layout.student, container, false);
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
        checkFragment = new CheckFragment();
    }

    private void initView(View view) {
        back = (LinearLayout) view.findViewById(R.id.title_back);
        backtext = (TextView) view.findViewById(R.id.title_backtext);
        tag = (TextView) view.findViewById(R.id.title_title);
        back.setVisibility(View.VISIBLE);
        backtext.setText(R.string.set_tag);
        tag.setText(R.string.student_tag);
        listView = (ListView) view.findViewById(R.id.student_list);
        add = (TextView) view.findViewById(R.id.title_more);
        add.setVisibility(View.VISIBLE);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getFragmentManager().popBackStack();
            }
        });
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showDialog2();
            }
        });
    }

    private void getData() {

        dialog.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                HashMap<String, Object> upmap = new HashMap();
                upmap.put("phone", sp.getString("tphone", ""));
                upmap.put("salt", MyData.getKey());
                String resultString = HttpUtils.updata(upmap, MyData.URL_GETSTULIST);
                mHandler.obtainMessage(GETDATA, resultString).sendToTarget();
            }
        }).start();


    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            try {
                switch (msg.what) {
                    case GETDATA:
                        dialog.dismiss();
                        list = JSON.parseObject(msg.obj.toString(),
                                new TypeReference<List<Map<String, Object>>>() {
                                });
                        try {
                            if (!list.isEmpty() && list != null) {
                                listView.setAdapter(new StudentAdapter(getActivity(), list));

                                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                        countView = view;
                                        final int sid = Integer.parseInt(list.get(i).get("sid").toString());
                                        if (Integer.parseInt(list.get(i).get("status").toString()) == 2) {
                                            showDialog(Integer.parseInt(list.get(i).get("sid").toString()), Integer.parseInt(list.get(i).get("prov").toString()));
                                        } else if (Integer.parseInt(list.get(i).get("status").toString()) == 1) {
                                            MyData.SID = Integer.parseInt(list.get(i).get("sid").toString());
                                            getActivity().getFragmentManager().beginTransaction().remove(StudentFragment.this)
                                                    .add(R.id.set_content, checkFragment, "checkFragment")
                                                    .addToBackStack(null).commit();
                                        } else if (Integer.parseInt(list.get(i).get("status").toString()) == 0) {
                                            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                            LayoutInflater inflater = getActivity().getLayoutInflater();
                                            View contentview = inflater.inflate(R.layout.layout_student_dialog3, null);
                                            builder.setView(contentview);
                                            builder.setTitle(getString(R.string.student_dialog));
                                            final Dialog dialogs = builder.create();
                                            contentview.findViewById(R.id.student_dialog31).setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    dialog.show();
                                                    new Thread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            HashMap map = new HashMap();
                                                            map.put("salt", MyData.getKey());
                                                            map.put("sid", sid);
                                                            mHandler.obtainMessage(DELECT, HttpUtils.updata(map, MyData.URL_DELSTU)).sendToTarget();
                                                        }
                                                    }).start();
                                                    dialogs.dismiss();
                                                }
                                            });
                                            contentview.findViewById(R.id.student_dialog32).setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    dialogs.dismiss();
                                                }
                                            });
                                            dialogs.show();

                                        }
                                    }
                                });
                            }
                        } catch (Exception e) {

                        }

                        break;
                    case UPDATA:
                        dialog.dismiss();
                        Map map = JSON.parseObject(msg.obj.toString(), new TypeReference<Map>() {
                        });
                        if (!map.isEmpty() && map != null) {
                            if (map.get("count").toString().equals("1")) {
                                TextView zhuangtai = (TextView) countView.findViewById(R.id.student_quanxian);
                                switch (PROV) {
                                    case 0:
                                        zhuangtai.setText("");
                                        break;
                                    case 1:
                                        zhuangtai.setText(getString(R.string.student_dialog2));
                                        break;
                                }
                            }
                        }
                        break;
                    case ADD:
                        dialog.dismiss();
                        Map map2 = JSON.parseObject(msg.obj.toString(), new TypeReference<Map>() {
                        });
                        if (!map2.isEmpty() && map2 != null) {
                            if (Integer.parseInt(map2.get("newid").toString()) > 0) {
                                getData();
                            }
                        }
                        break;
                    case DELECT:
                        dialog.dismiss();
                        Map map3 = JSON.parseObject(msg.obj.toString(), new TypeReference<Map>() {
                        });
                        if (!map3.isEmpty() && map3 != null) {
                            if (Integer.parseInt(map3.get("count").toString()) > 0) {
                                getData();
                            }
                        }
                        break;
                    case BOUND:
                        Map map4 = JSON.parseObject(msg.obj.toString(), new TypeReference<Map>() {
                        });
                        if (!map4.isEmpty() && map4 != null) {
                            if (Integer.parseInt(map4.get("count").toString()) > 0) {
                                getData();
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        HashMap map = new HashMap();
                                        map.put("salt", MyData.getKey());
                                        map.put("prov", 0);
                                        map.put("sid", Sid);
                                        mHandler.obtainMessage(UPDATA, HttpUtils.updata(map, MyData.URL_SETPROV)).sendToTarget();
                                    }
                                }).start();
                            }
                        }
                        break;
                }
            } catch (Exception e) {
                Toast.makeText(getActivity(), R.string.connectfild, Toast.LENGTH_SHORT).show();
            }
        }
    };


    @Override
    public void OnClick(final int sid, final int position) {
        PROV = position;
        Sid = sid;
        if (position == 3) {
            dialog.show();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    HashMap map = new HashMap();
                    map.put("salt", MyData.getKey());
                    map.put("status", 0);
                    map.put("sid", sid);
                    mHandler.obtainMessage(BOUND, HttpUtils.updata(map, MyData.URL_SETSTAT)).sendToTarget();
                }
            }).start();

        } else {
            dialog.show();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    HashMap map = new HashMap();
                    map.put("salt", MyData.getKey());
                    map.put("prov", position);
                    map.put("sid", sid);
                    mHandler.obtainMessage(UPDATA, HttpUtils.updata(map, MyData.URL_SETPROV)).sendToTarget();
                }
            }).start();
        }
    }

    @Override
    public void OnClick2(final String item1, final String item2, final String item3) {
        dialog.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                HashMap map = new HashMap();
                map.put("salt", MyData.getKey());
                // map.put("classname", item1);
                map.put("classname", sp.getString("class_name", ""));
                map.put("sno", item2);
                map.put("stuname", item3);
                map.put("tphone", sp.getString("tphone", ""));
                mHandler.obtainMessage(ADD, HttpUtils.updata(map, MyData.URL_ADDSTU)).sendToTarget();

            }
        }).start();
    }

    private void showDialog(int sid, int prov) {
        StudentDialogFragment dialogFragment = new StudentDialogFragment();
        dialogFragment.setlistener(this, sid, prov);
        dialogFragment.show(getActivity().getFragmentManager(), "");
    }

    private void showDialog2() {
        StudentDialogFragment2 dialogFragment = new StudentDialogFragment2();
        dialogFragment.setData(this, sp.getString("class_name", ""), SNO);
        dialogFragment.show(getActivity().getFragmentManager(), "");
    }

    private class StudentAdapter extends BaseAdapter {

        private Context context;
        private List<Map<String, Object>> list;
        private LayoutInflater mInflater;

        public StudentAdapter(Context context, List<Map<String, Object>> list) {
            this.mInflater = LayoutInflater.from(context);
            this.context = context;
            this.list = list;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int i) {
            return i;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final ViewHolder holder;


            if (convertView == null) {
                holder = new ViewHolder();
                convertView = mInflater.inflate(R.layout.layout_student_item, parent, false);
                holder.num = (TextView) convertView.findViewById(R.id.student_num);
                holder.name = (TextView) convertView.findViewById(R.id.student_name);
                holder.quanxian = (TextView) convertView.findViewById(R.id.student_quanxian);
                holder.bangding = (ImageView) convertView.findViewById(R.id.student_bangding);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            int sno = Integer.parseInt(list.get(position).get("sno").toString());
            if (SNO < sno) {
                SNO = sno;
            }

            holder.num.setText(list.get(position).get("sno").toString());
            holder.name.setText(list.get(position).get("name").toString());
            int pro = Integer.parseInt(list.get(position).get("prov").toString());
            switch (pro) {
                case 1:
                    holder.quanxian.setText(getString(R.string.student_dialog2));
                    break;
            }
            int bangding = Integer.parseInt(list.get(position).get("status").toString());
            switch (bangding) {
                case 1:
                    holder.bangding.setImageResource(R.drawable.student_state_defile);
                    break;
                case 2:
                    holder.bangding.setImageResource(R.drawable.student_state_ok);
                    break;
            }


            return convertView;
        }


        class ViewHolder {
            TextView num;
            TextView name;
            TextView quanxian;
            ImageView bangding;
        }
    }

}

