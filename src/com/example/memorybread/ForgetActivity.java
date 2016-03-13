package com.example.memorybread;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ForgetActivity extends Activity {
	
	private EditText to_et;
	private EditText username_et;
	private Button submit_bt;
	private Button cancel_bt;
	//handler
	private Handler handler = new Handler(){
		public void handleMessage(Message msg){
			switch(msg.what){
			case 1:
				JSONObject j;
				String str = null;
				Boolean success = null;
				try {
					j = new JSONObject(msg.obj.toString());
					str = j.getString("msg").toString();
					success = j.getBoolean("success");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
								
				//String response = (String)msg.obj;
				if(success){
					
					String ans = "亲爱的用户你好！ 你注册的密码是："+str;
					SmsManager smsManager = SmsManager.getDefault();
					smsManager.sendTextMessage(to_et.getText().toString(), null,
							ans, null, null);					
				}
				else{
					Toast.makeText(ForgetActivity.this, "对不起，该用户未被注册", Toast.LENGTH_SHORT).show();
					
				}
			}
		}
	};
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ActionBar actionBar = getActionBar();
        actionBar.hide();
		setContentView(R.layout.activity_forget);
		
		to_et=(EditText)findViewById(R.id.et_numberto);
		username_et=(EditText)findViewById(R.id.et_username);
		submit_bt=(Button)findViewById(R.id.bt_submit);
		cancel_bt=(Button)findViewById(R.id.bt_cancelforget);
		
		submit_bt.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){
				
				String to = to_et.getText().toString();
				String uname = username_et.getText().toString();
				
				if(to.equals("")){
					Toast.makeText(v.getContext(),"请输入手机号",Toast.LENGTH_SHORT).show();
				}else if(uname.equals("")){
					Toast.makeText(v.getContext(),"请输入用户名",Toast.LENGTH_SHORT).show();
				}else{
					//加权限
					//需要从云端数据库得到密码
					sendMsg(to,uname);		
					//SmsManager smsManager = SmsManager.getDefault();
					//smsManager.sendTextMessage(to_et.getText().toString(), null,
					//		uname, null, null);	
				}
				
			}
		});
				
		cancel_bt.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
	}
	
	private void sendMsg(final String to,final String uname){
		
		new Thread(new Runnable(){
			public void run(){
				HttpURLConnection connection = null;
				try{
					URL url = new URL("http://115.28.204.167:8000/forget");
					connection = (HttpURLConnection) url.openConnection();
					connection.setRequestMethod("POST");
					String to_utf8 = "";
    				String uname_utf8 = "";
    				try{
    					to_utf8 = URLEncoder.encode(to,"UTF-8");
    					uname_utf8 = URLEncoder.encode(uname,"UTF-8");
    					
    				}catch(Exception e){
    					e.printStackTrace();
    					
    				}
    				Log.e("except","before data out");
					DataOutputStream out = new DataOutputStream(connection.getOutputStream());
					out.writeBytes("account="+uname_utf8+"&phonenumber="+to_utf8);
					Log.e("except","before data in");
					
					InputStream in = connection.getInputStream();
					BufferedReader reader=new BufferedReader(new InputStreamReader(in));
					StringBuilder response=new StringBuilder();
					Log.e("except","after data in");
					String line;
					while((line=reader.readLine())!=null){
						response.append(line);
					}
					Message message = new Message();
					message.what = 1;
					message.obj=response.toString();
					
					handler.sendMessage(message);
				}catch (Exception e){
					e.printStackTrace();
					
					Log.e("except","catch");
				}finally{
					if(connection!=null)
						connection.disconnect();
				}		
			}
			
			
		}).start();	
		
	}
    
}