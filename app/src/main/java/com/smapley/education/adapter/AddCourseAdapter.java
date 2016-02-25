package com.smapley.education.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.smapley.education.R;
import com.smapley.education.utils.MyData;

import java.util.List;
import java.util.Map;

/**
 * Created by Smaley on 2015/4/20.
 */
public class AddCourseAdapter extends BaseAdapter {
    /**
     * 得到一个LayoutInfalter对象用来导入布局
     */
    private LayoutInflater mInflater;
    private Context context;
    /**
     * list的数据
     */
    private List<Map> listitem;

    /**
     * 构造函数
     */
    public AddCourseAdapter(Context context,
                            List listitem) {
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
        this.listitem = listitem;
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
            convertView = mInflater.inflate(R.layout.layout_addcourse_item, parent, false);
            holder.title = (TextView) convertView.findViewById(R.id.addcourse_name);
            holder.check = (ImageView) convertView.findViewById(R.id.addcourse_check);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        String name = listitem.get(position).get("name").toString();
        holder.title.setText(name);
        List check = MyData.GRADELIST;
        Log.i("addcourseadapter", "----------------------->>" + check.toString());

        for (int i = 0; i < check.size(); i++) {
            if (name.equals(check.get(i))) {
                Log.i("addcourseadapter", "----------------------->>" + check.toString());
                holder.check.setVisibility(View.VISIBLE);
            }
        }

        return convertView;
    }


    class ViewHolder {
        TextView title;
        ImageView check;
    }


}

