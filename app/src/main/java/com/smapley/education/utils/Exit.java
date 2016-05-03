package com.smapley.education.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.content.DialogInterface;

import java.util.LinkedList;
import java.util.List;

public class Exit extends Application {
	
	private List<Activity> activityList=new LinkedList<Activity>();
	private static Exit instance;
	//单例模式中获取唯一的ExitApplication 实例
	public static Exit getInstance()
	{
		if(null == instance)
		{
			instance = new Exit();
		}
		return instance;

	}
	
	//添加Activity 到容器中
	public void addActivity(Activity activity)
	{
		activityList.add(activity);
	}
	
	//遍历所有Activity 并finish
	public void exit()
	{
		for(Activity activity:activityList)
		{
			activity.finish();
		}
		System.exit(0);
	}

	
	public void exitAlert (AlertDialog.Builder builder){
		builder.setMessage("确定要退出SpareTime吗？");
		builder.setPositiveButton("是",new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				Exit.getInstance().exit();
			}
		});	
		builder.setNegativeButton("否",new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				return;
			}
		});		
		AlertDialog alert = builder.create();
		alert.show();
	}
	
	// 显示对话框
	public void infoAlert(AlertDialog.Builder builder,String msg){
		builder.setMessage(msg)
				   .setCancelable(false)
				       .setPositiveButton("确定", new DialogInterface.OnClickListener() {
				           public void onClick(DialogInterface dialog, int id) {
				           }
				       });
				AlertDialog alert = builder.create();
				alert.show();
			}	
}
