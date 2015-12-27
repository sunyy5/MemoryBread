package com.example.memorybread;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ImageView.ScaleType;

public class LoginActivity extends Activity {
	private Button mLogin;
	private Button mRegister;
	private EditText mAccount;
	private EditText mPassword;
	public static String account;
	
	 //œ‘ æµ«¬º≥…π¶ªÚ’ﬂ’À∫≈ªÚ√‹¬Î¥ÌŒÛ 
	private Handler handler = new Handler(){
        	public void handleMessage(Message msg){
        		switch (msg.what){
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
        					account = mAccount.getText().toString();
        					Toast.makeText(LoginActivity.this, "µ«¬º≥…π¶", Toast.LENGTH_SHORT).show();
        					Intent i = new Intent(LoginActivity.this,MainActivity.class);
        					startActivity(i);
        				}else if (str=="∏√’À∫≈Œ¥±ª◊¢≤·"){
        					Toast.makeText(LoginActivity.this, "∏√’À∫≈Œ¥±ª◊¢≤·", Toast.LENGTH_SHORT).show();
        				}
        				else{
        					Toast.makeText(LoginActivity.this, "√‹¬Î¥ÌŒÛ", Toast.LENGTH_SHORT).show();
        				}
        				break;
        			default:
        				break;
        		}
        	}
        };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ActionBar actionBar = getActionBar();
        actionBar.hide();
        
        mLogin = (Button) findViewById(R.id.bt_login);
        mRegister = (Button) findViewById(R.id.bt_register);
        mAccount = (EditText) findViewById(R.id.et_useraccount);
        mPassword = (EditText) findViewById(R.id.et_password);
       
        
        mLogin.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent t = new Intent(LoginActivity.this,MainActivity.class);
				startActivity(t);
				
				String account = mAccount.getText().toString();
				String pwd = mPassword.getText().toString();
				
				if(account.equals(""))
				{
					Toast.makeText(v.getContext(),"«Î ‰»Î’À∫≈", Toast.LENGTH_SHORT).show();
				}
				else if(pwd.equals("")){
					Toast.makeText(v.getContext(),"«Î ‰»Î√‹¬Î",Toast.LENGTH_SHORT).show();
				}
				else
					login(account,pwd);
//				Intent intent = new Intent(LoginActivity.this, MainActivity.class);
//				startActivity(intent);
			}
		});
        
        mRegister.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
				startActivity(intent);
			}
		});
    }
    
    //∂®“Âµ«¬ºΩ¯»•µƒ∫Ø ˝
    private void login(final String account,final String pwd){
    	new Thread(new Runnable(){
    		public void run(){
    			HttpURLConnection connection = null;
    			try{
    				URL url = new URL("http://115.28.204.167:8000/login");
    				connection = (HttpURLConnection) url.openConnection();
    				connection.setRequestMethod("POST");
    				DataOutputStream out = new DataOutputStream(connection.getOutputStream());
    				out.writeBytes("account="+account+"&password="+pwd);
    				
    				InputStream in = connection.getInputStream();
    				BufferedReader reader = new BufferedReader(new InputStreamReader(in));
    				StringBuilder response = new StringBuilder();
    				String line;
    				while((line=reader.readLine())!=null){
    					response.append(line);
    				}
    				Message message = new Message();
    				message.what=1;
    				message.obj=response.toString();
    				handler.sendMessage(message);
    				
    			}catch(Exception e){
    				e.printStackTrace();
    				
    			}finally{
    				if(connection!=null)
    					connection.disconnect();  				
    			} 			
    		}
    	}).start();
    	
    }
}
