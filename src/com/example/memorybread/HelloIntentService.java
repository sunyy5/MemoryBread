package com.example.memorybread;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.IBinder;

public class HelloIntentService extends Service
{
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	public void onCreate(){
		super.onCreate();
	}
	public int onStartCommand(Intent intent,int flags,int startId){
		NotificationManager manager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);	
		//Notification notification = new Notification(R.drawable.logo,"�������",System.currentTimeMillis());
		
		Intent intent1 =  new Intent(this,MainActivity.class);
		PendingIntent pi = PendingIntent.getActivity(this, 0, intent1, PendingIntent.FLAG_CANCEL_CURRENT);
		
		
		
		
		
		Notification.Builder builder = new Notification.Builder(this)  
        .setAutoCancel(true)  
        .setContentTitle("�������")  
        .setContentText("��ʼѧϰ�ɣ�")  
        .setContentIntent(pi)  
        .setSmallIcon(R.drawable.logo)  
        .setWhen(System.currentTimeMillis())  
        .setOngoing(true);  
        Notification notification=builder.getNotification();  
		//notification.setLatestEventInfo(this, "�������", "ѧϰʱ�䵽~", pi);
		notification.defaults = Notification.DEFAULT_VIBRATE;
		long[] vibrates = {0,1000,1000,1000};
		notification.vibrate=vibrates;
		//LED��û��Ч��������
		notification.ledARGB = Color.GREEN;
		notification.ledOnMS = 1000;
		notification.ledOffMS = 1000;
		notification.flags = notification.flags|Notification.FLAG_SHOW_LIGHTS;
		//LED��û��Ч��������
		
		manager.notify(1,notification);
		return super.onStartCommand(intent, flags, startId);
	}
	public void onDestory(){
		super.onDestroy();
	}	
}