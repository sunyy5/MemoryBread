package com.example.memorybread;

import java.util.Calendar;
import java.util.TimeZone;

import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TimePicker;
import android.widget.TimePicker.OnTimeChangedListener;
import android.widget.Toast;

public class SettingActivity extends Activity {

	private Button setTime;
	private TimePicker timepicker;
	private CheckBox checkbox;
	int h_t;
	int m_t;
	@Override
	protected void onCreate(Bundle savedInstanceState) {	
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		setTime = (Button) findViewById(R.id.bt_setTime);
		timepicker = (TimePicker) findViewById(R.id.time);	
		checkbox = (CheckBox) findViewById(R.id.check1);
		
		timepicker.setIs24HourView(true);
		TimeListener times=new TimeListener();
		timepicker.setOnTimeChangedListener(times);
		
		 checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
				
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					// TODO Auto-generated method stub
					if(checkbox.isChecked()){
						//���������ע��
						Intent intent = new Intent(SettingActivity.this,HelloIntentService.class);
						intent.setAction("repeating");
						PendingIntent sender = PendingIntent.getService(SettingActivity.this, 0, intent, 0);
						
						
						long firsttime = SystemClock.elapsedRealtime();
						Calendar calendar = Calendar.getInstance();
						calendar.setTimeInMillis(System.currentTimeMillis());
						calendar.setTimeZone(TimeZone.getTimeZone("GMT+8"));
						calendar.set(Calendar.HOUR_OF_DAY,h_t);
						calendar.set(Calendar.MINUTE,m_t);
						calendar.set(Calendar.SECOND,0);
						calendar.set(Calendar.MILLISECOND,0);
						//����һ����ʱʱ��
						long selecttime = calendar.getTimeInMillis();
						long systemtime = System.currentTimeMillis();
						if(systemtime > selecttime){
							calendar.add(Calendar.DAY_OF_MONTH, 1);
							selecttime=calendar.getTimeInMillis();
						}
						long time = selecttime-systemtime;
						firsttime+=time;
						
						//����Ϊһ���ʱ����Ҫ��Ϊ��24*60*60*1000
						AlarmManager am = (AlarmManager)getSystemService(ALARM_SERVICE);
						am.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, firsttime,60*1000, sender);  
					}
					else{
						Intent intent = new Intent(SettingActivity.this,HelloIntentService.class);
						intent.setAction("repeating");
						PendingIntent sender = PendingIntent.getService(SettingActivity.this, 0, intent, 0);
						AlarmManager am = (AlarmManager)getSystemService(ALARM_SERVICE);
						am.cancel(sender);
					}					
				}
			});
	OnChangeListener buc = new OnChangeListener();		
	setTime.setOnClickListener(buc);	
    }
	 class OnChangeListener implements OnClickListener{
			@Override
			    public void onClick(View v){
				   Log.d("Test","time2");
				   Intent intent = new Intent(SettingActivity.this, MainActivity.class);
				   startActivity(intent);
			   }
	 }
	 
	 
	 class TimeListener implements OnTimeChangedListener{
			
			/**
			 * view ��ǰѡ��TimePicker�ؼ�
			 *  hourOfDay ��ǰ�ؼ�ѡ��TimePicker ��Сʱ
			 * minute ��ǰѡ�пؼ�TimePicker  �ķ���
			 */
			@Override
			public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
				// TODO Auto-generated method stub
				//System.out.println("h:"+ hourOfDay +" m:"+minute);
				h_t=hourOfDay;
				m_t=minute;
			}
			
		}
}
