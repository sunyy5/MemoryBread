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

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class RegisterActivity extends Activity {
	private Button mCancel;
	private Button mRegistering;
	private EditText mAccount;
	private EditText mPassword;
	private EditText surepwd;
	private EditText userphone;
	
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
					Toast.makeText(RegisterActivity.this, str, Toast.LENGTH_SHORT).show();
					Intent i = new Intent(RegisterActivity.this,MainActivity.class);
					startActivity(i);
				}
				else{
					Toast.makeText(RegisterActivity.this, "∏√’À∫≈“—±ª◊¢≤·£°£°", Toast.LENGTH_SHORT).show();
				}
			}
		}
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		ActionBar actionBar = getActionBar();
        actionBar.hide();
        
        mCancel = (Button) findViewById(R.id.bt_cancel);
        mRegistering = (Button) findViewById(R.id.bt_registering);
        mAccount = (EditText) findViewById(R.id.et_new_useraccount);
        mPassword = (EditText) findViewById(R.id.et_new_password);
        surepwd = (EditText) findViewById(R.id.et_new_usersure);
        userphone = (EditText) findViewById(R.id.et_new_userphone);
        
        mRegistering.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub	
				
				String account = mAccount.getText().toString();
				String password = mPassword.getText().toString();
				String sure_pwd = surepwd.getText().toString();
				String phonenumber = userphone.getText().toString();
				if(account.equals("")||password.equals("")||sure_pwd.equals("")||phonenumber.equals("")){
					Toast.makeText(v.getContext(), "◊¢≤·–≈œ¢Œ¥ÃÓ–¥ÕÍ’˚", Toast.LENGTH_SHORT).show();
				}else if(password.equals(sure_pwd)){
					register(account,password,phonenumber);					
				}else{
					Toast.makeText(v.getContext(),"»∑»œ√‹¬Î¥ÌŒÛ",Toast.LENGTH_SHORT).show();
				}
				/*
				if(account.equals("")){
					Toast.makeText(v.getContext(),"«Î ‰»Î’À∫≈",Toast.LENGTH_SHORT).show();
				}else if(password.equals("")){
					Toast.makeText(v.getContext(),"«Î ‰»Î√‹¬Î",Toast.LENGTH_SHORT).show();
				}else{
					register(account,password);
				}*/
			}
		});
        
        mCancel.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}
	
	private void register(final String account,final String password,final String phonenumber){
		
		new Thread(new Runnable(){
			public void run(){
				HttpURLConnection connection = null;
				try{
					URL url = new URL("http://115.28.204.167:8000/register");
					connection = (HttpURLConnection) url.openConnection();
					connection.setRequestMethod("POST");
					String account_utf8 = "";
    				String password_utf8 = "";
    				String phonenumber_utf8 = "";
    				try{
    					account_utf8 = URLEncoder.encode(account,"UTF-8");
    					password_utf8 = URLEncoder.encode(password,"UTF-8");
    					phonenumber_utf8 = URLEncoder.encode(phonenumber,"UTF-8");
    					
    				}catch(Exception e){
    					e.printStackTrace();
    					
    				}
					DataOutputStream out = new DataOutputStream(connection.getOutputStream());
					out.writeBytes("account="+account_utf8+"&password="+password_utf8+
							"&phonenumber="+phonenumber_utf8);
					
					InputStream in = connection.getInputStream();
					BufferedReader reader=new BufferedReader(new InputStreamReader(in));
					StringBuilder response=new StringBuilder();
					
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
