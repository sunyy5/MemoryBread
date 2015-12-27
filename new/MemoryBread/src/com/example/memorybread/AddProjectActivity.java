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
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AddProjectActivity extends Activity  implements View.OnClickListener{

	private Button save_project;
	private EditText edt_deadline;
	private EditText edt_name;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_project);
		save_project = (Button)findViewById(R.id.button_save_project);
		save_project.setOnClickListener(this);
		edt_deadline = (EditText)findViewById(R.id.editText_pjdeadline);
		edt_name = (EditText)findViewById(R.id.editText_pjname);
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
						Toast.makeText(AddProjectActivity.this, "成功添加一个project", Toast.LENGTH_SHORT).show();
						finish();
					}
					break;
				default:
					break;
			}
		}
	};
	
	private void sendTo(final String _account, final String _pjdeadline,final String _pjname){
		new Thread(new Runnable(){
    		public void run(){
    			HttpURLConnection connection = null;
    			try{
    				URL url = new URL("http://115.28.204.167:8000/projects");
    				connection = (HttpURLConnection) url.openConnection();
    				connection.setRequestMethod("POST");
    				DataOutputStream out = new DataOutputStream(connection.getOutputStream());
    				String account = "";
    				String pjdeadline = "";
    				String pjname = "";
    				
    				account = URLEncoder.encode(_account,"UTF-8");
    				pjdeadline = URLEncoder.encode(_pjdeadline,"UTF-8");
    				pjname = URLEncoder.encode(_pjname,"UTF-8");
    				
    				out.writeBytes("account="+account+"&project_deadline="+pjdeadline+"&project_name="+pjname);
    				
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

	
	public void onClick(View v) {
		String pj_deadline = edt_deadline.getText().toString();
		String pj_name = edt_name.getText().toString();
		sendTo(LoginActivity.account, pj_deadline, pj_name);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.add_project, menu);
		return true;
	}

}
