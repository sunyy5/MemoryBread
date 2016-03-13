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
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class InfoUpdateActivity extends Activity {
	private TextView textView;
	private EditText editText;
	private Button button;
	
	private String nickname = "";
	private String job = "";
	private String email = "";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_info_update);
		Bundle bundle = getIntent().getExtras();
		final String s = bundle.getString("name");
		textView = (TextView) findViewById(R.id.info_newname);
		editText = (EditText) findViewById(R.id.info_edittext);
		button = (Button) findViewById(R.id.info_save);
		
		textView.setText(s);
		button.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				/****上传****/
				if(s.equals("昵称")) {
					nickname = editText.getText().toString();
					job = "";
					email = "";
					sendToUpdateInfo(LoginActivity.account, nickname, job, email);
				} else if(s.equals("职业")) {
					nickname = "";
					job = editText.getText().toString();
					email = "";
					sendToUpdateInfo(LoginActivity.account, nickname, job, email);
				} else if(s.equals("邮箱")) {
					nickname = "";
					job = "";
					email = editText.getText().toString();
					sendToUpdateInfo(LoginActivity.account, nickname, job, email);
				}
				/****上传_end****/
			}
		});
	}
	

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
						e.printStackTrace();
					}
					if(success){
						Toast.makeText(InfoUpdateActivity.this, "成功更改个人信息", Toast.LENGTH_SHORT).show();
						setResult(RESULT_OK);
						finish();
					}
					break;
				default:
					break;
			}
		}
	};
	
	private void sendToUpdateInfo(final String account, final String nickname, final String job, final String email) {
		new Thread(new Runnable(){
    		public void run(){
    			HttpURLConnection connection = null;
    			try{
    				URL url = new URL("http://115.28.204.167:8000/info");
    				connection = (HttpURLConnection) url.openConnection();
    				connection.setRequestMethod("POST");
    				DataOutputStream out = new DataOutputStream(connection.getOutputStream());
    				
    				out.writeBytes("account="+URLEncoder.encode(account, "UTF-8")
    						+"&nickname="+URLEncoder.encode(nickname, "UTF-8")
    						+"&occupation="+URLEncoder.encode(job, "UTF-8")
    						+"&email="+URLEncoder.encode(email,"UTF-8"));
    				
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
