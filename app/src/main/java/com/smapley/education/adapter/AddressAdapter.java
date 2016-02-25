package com.smapley.education.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.smapley.education.R;

import java.util.List;
import java.util.Map;

/**
 * Created by smapley on 2015/4/30.
 */
public class AddressAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private List<Map<String, Object>> mData;// 存储的EditText值
    private Context context;

    public AddressAdapter(Context context, List<Map<String, Object>> data) {
        this.context = context;
        mData = data;
        mInflater = LayoutInflater.from(context);


    }


    public List getData() {
        return mData;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    private Integer index = -1;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        // convertView为null的时候初始化convertView。


        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.layout_addresslist_item, null);
            holder.sname = (TextView) convertView
                    .findViewById(R.id.addresslist_student_name);
            holder.jphone = (TextView) convertView
                    .findViewById(R.id.addresslist_parents_phone);
            holder.sphone = (TextView) convertView
                    .findViewById(R.id.addresslist_student_phone);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.sname.setText(mData.get(position).get("name").toString());
        holder.jphone.setText(mData.get(position).get("jphone").toString());
        holder.sphone.setText(mData.get(position).get("stuphone").toString());

        holder.jphone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle(R.string.tips);
                final String number = ((TextView) view).getText().toString();

                builder.setMessage(context.getString(R.string.call) + number);
                builder.setNegativeButton(R.string.cancel, null);
                builder.setPositiveButton(R.string.Okay, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + number));
                        context.startActivity(intent);
                    }
                });
                builder.create().show();
            }
        });
        holder.sphone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle(R.string.tips);
                final String number = ((TextView) view).getText().toString();

                builder.setMessage(context.getString(R.string.call) + number);
                builder.setNegativeButton(R.string.cancel, null);
                builder.setPositiveButton(R.string.Okay, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + number));
                        context.startActivity(intent);
                    }
                });
                builder.create().show();
            }
        });
        return convertView;
    }

    public final class ViewHolder {
        public TextView sname;
        public TextView jphone;
        public TextView sphone;

    }
}
