package com.example.memorybread;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;




public class AddActivity extends Activity {
	public static String in = null; //全局变量判断是否插入成功
	private EditText para_name;
	private EditText content;
	private Button finish;
	
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
						in = "yes";
						Toast.makeText(AddActivity.this, "成功插入", Toast.LENGTH_SHORT).show();
						Intent i = new Intent(AddActivity.this,MainActivity.class);
						startActivity(i);
						}else{
						Toast.makeText(AddActivity.this, "该文本名称已被使用", Toast.LENGTH_SHORT).show();
					}
					break;
				default:
					break;
			}
		}
	};
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add);
		
		para_name = (EditText)findViewById(R.id.et_paragraph_name);
		content = (EditText)findViewById(R.id.et_content);
		finish = (Button)findViewById(R.id.bt_finish);
		
		finish.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String pname = para_name.getText().toString();
				String cont = content.getText().toString();
				if(pname.equals(""))
				{
					Toast.makeText(v.getContext(),"给面包起个名字吧", Toast.LENGTH_SHORT).show();
				}
				else if(cont.equals("")){
					Toast.makeText(v.getContext(),"面包呢",Toast.LENGTH_SHORT).show();
				}
				else{
					sendTo(pname,cont);
				    //Intent intent = new Intent(AddActivity.this,ReciteActivity.class);
				    //startActivity(intent);					
				}					
			}
		});
		
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.add, menu);
		return true;
	}
	
	private void sendTo(final String pname,final String cont){
		new Thread(new Runnable(){
    		public void run(){
    			HttpURLConnection connection = null;
    			try{
    				URL url = new URL("http://115.28.204.167:8000/add");
    				connection = (HttpURLConnection) url.openConnection();
    				connection.setRequestMethod("POST");
    				DataOutputStream out = new DataOutputStream(connection.getOutputStream());
    				out.writeBytes("pname="+pname+"&content="+cont);
    				
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
