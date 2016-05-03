package com.smapley.education.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
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

/**
 * Created by Smapley on 2015/4/21.
 */
public class BoundPhoneAdapter extends BaseAdapter {

    private final int Bound = 1;
    /**
     * 得到一个LayoutInfalter对象用来导入布局
     */
    private LayoutInflater mInflater;
    private Context context;
    /**
     * list的数据
     */
    private List<Map> listitem;
    private ProgressDialog dialog;

    /**
     * 构造函数
     */
    public BoundPhoneAdapter(Context context,
                             List<Map> listitem) {
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
        this.listitem = listitem;
        dialog = new ProgressDialog(context);
        dialog.setTitle(context.getString(R.string.tips));
        dialog.setMessage(context.getString(R.string.connect));
        
    }

    @Override
    public int getCount() {
        return listitem.size();// 返回数组的长度

    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;


        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.layout_boundphone_item, parent, false);
            holder.sname = (TextView) convertView.findViewById(R.id.boundphone_child_name);
            holder.tphone = (TextView) convertView.findViewById(R.id.boundphone_teacher_phone);
            holder.state = (TextView) convertView.findViewById(R.id.boundphone_state);
            holder.save = (TextView) convertView.findViewById(R.id.boundphone_save);
            holder.sphone = (EditText) convertView.findViewById(R.id.boundphone_child_phone);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.sname.setText(listitem.get(position).get("name").toString());
        holder.tphone.setText(listitem.get(position).get("tphone").toString());
        holder.sphone.setText(listitem.get(position).get("stuphone").toString());
        String status = "";
        switch (Integer.parseInt(listitem.get(position).get("status").toString())) {
            case 0:
                status = context.getString(R.string.bound_status0);
                break;
            case 1:
                status = context.getString(R.string.bound_status1);
                break;
            case 2:
                status = context.getString(R.string.bound_status2);
                holder.sphone.setEnabled(true);
                holder.save.setTextColor(context.getResources().getColor(R.color.blue));
                holder.save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.show();
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                HashMap map = new HashMap();
                                map.put("sid", listitem.get(position).get("sid"));
                                map.put("stuphone", holder.sphone.getText().toString());
                                map.put("salt", MyData.getKey());
                                mhandler.obtainMessage(Bound, HttpUtils.updata(map, MyData.URL_SETSTUPHONE)).sendToTarget();

                            }
                        }).start();
                    }
                });
                break;
        }
        holder.state.setText(status);

        return convertView;
    }


    class ViewHolder {
        TextView sname;
        TextView tphone;
        TextView state;
        TextView save;
        EditText sphone;
    }


    private Handler mhandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            try {
                switch (msg.what) {

                    case Bound:
                        dialog.dismiss();
                        Map map = JSON.parseObject(msg.obj.toString(), new TypeReference<Map>() {
                        });
                        if (map != null && !map.isEmpty()) {
                            if (Integer.parseInt(map.get("count").toString()) >= 0) {
                                Toast.makeText(context, R.string.boundstudentphone, Toast.LENGTH_SHORT).show();
                            }
                        }
                        break;
                }
            } catch (Exception e) {
                try {
                    Toast.makeText(context, R.string.connectfild, Toast.LENGTH_SHORT).show();
                } catch (Exception e1) {
                    
                }
            }
        }
    };
}
