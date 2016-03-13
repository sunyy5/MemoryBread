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
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TimePicker;
import android.widget.Toast;

public class AddProjectActivity extends Activity  implements View.OnClickListener{

	private Button save_project;
	//private EditText edt_deadline;
	private EditText edt_name;
	private ImageButton contest,dinner,exam,exp,travel,email,meeting,interview,date,others;
	private ImageView imageView;
	private DatePicker datePicker;
	private int year,month,dateofmonth,logo;
	private String project_name_old;
	private String project_deadline_old;
	private int update = 0;
	private String logo_old = "";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_project);
		initial();
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
						if(update == 0)
							Toast.makeText(AddProjectActivity.this, "成功添加一个project", Toast.LENGTH_SHORT).show();
						else
							Toast.makeText(AddProjectActivity.this, "成功修改一个project", Toast.LENGTH_SHORT).show();
						setResult(RESULT_OK);
						finish();
					}
					break;
				default:
					break;
			}
		}
	};
	
	private void sendTo(final String _account, final String _pjdeadline,final String _pjname, final int logo){
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
    				
    				out.writeBytes("account="+account+"&project_deadline="+pjdeadline+"&project_name="+pjname+"&project_logo="+logo);
    				
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
	
	private void sendToUpdate(final String _account, final String _pjdeadline_old, final String _pjname_old, final int logo, final String _pjdeadline,final String _pjname) {
		new Thread(new Runnable(){
    		public void run(){
    			HttpURLConnection connection = null;
    			try{
    				URL url = new URL("http://115.28.204.167:8000/project_update");
    				connection = (HttpURLConnection) url.openConnection();
    				connection.setRequestMethod("POST");
    				DataOutputStream out = new DataOutputStream(connection.getOutputStream());
    				String account = "";
    				String pjdeadline = "";
    				String pjname = "";
    				
    				account = URLEncoder.encode(_account,"UTF-8");
    				pjdeadline = URLEncoder.encode(_pjdeadline,"UTF-8");
    				pjname = URLEncoder.encode(_pjname,"UTF-8");
    				
    				Log.d("test", "-----------");
    				Log.d("test", _pjname_old);
    				Log.d("test", _pjdeadline_old);
    				Log.d("test", _pjname);
    				Log.d("test", _pjdeadline);
    				Log.d("test", "----------");
    				
    				out.writeBytes("account="+account+"&project_deadline_new="+pjdeadline+"&project_name_new="+pjname
    						+"&project_deadline_old="+URLEncoder.encode(_pjdeadline_old,"UTF-8")
    						+"&project_name_old="+URLEncoder.encode(_pjname_old,"UTF-8")
    						+"&project_logo="+logo);
    				
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
		switch (v.getId()) {
		case R.id.time_contest:
			imageView.setBackgroundResource(R.drawable.time_contest);
			logo = 0;
			break;
		case R.id.time_dinner:
			imageView.setBackgroundResource(R.drawable.time_dinner);
			logo = 1;
			break;
		case R.id.time_exam:
			imageView.setBackgroundResource(R.drawable.time_exam);
			logo = 2;
			break;
		case R.id.time_exp:
			imageView.setBackgroundResource(R.drawable.time_exp);
			logo = 3;
			break;
		case R.id.time_travel:
			imageView.setBackgroundResource(R.drawable.time_travel);
			logo = 4;
			break;
		case R.id.time_email:
			imageView.setBackgroundResource(R.drawable.time_email);
			logo = 5;
			break;
		case R.id.time_meeting:
			imageView.setBackgroundResource(R.drawable.time_meeting);
			logo = 6;
			break;
		case R.id.time_interview:
			imageView.setBackgroundResource(R.drawable.time_interview);
			logo = 7;
			break;
		case R.id.time_date:
			imageView.setBackgroundResource(R.drawable.time_date);
			logo = 8;
			break;
		case R.id.time_others:
			imageView.setBackgroundResource(R.drawable.time_others);
			logo = 9;
			break;
		case R.id.time_save:
			year = datePicker.getYear();
			month = datePicker.getMonth() + 1;
			dateofmonth = datePicker.getDayOfMonth();
			String pj_deadline = String.valueOf(year)+"/"+String.valueOf(month)
				+"/"+String.valueOf(dateofmonth);
			String pj_name = edt_name.getText().toString();
			if(update == 0) {
				Log.d("test", "click to add");
				sendTo(LoginActivity.account, pj_deadline, pj_name, logo);
			}
			else {
				Log.d("test", "click to update");
				Log.d("test", project_deadline_old);
				Log.d("test", project_name_old);
				Log.d("test", LoginActivity.account);
				sendToUpdate(LoginActivity.account, project_deadline_old, project_name_old, logo, pj_deadline, pj_name);
			}
			break;
		default:
			break;
		}
	}
	
	private void initial() {
		contest = (ImageButton) findViewById(R.id.time_contest);
		contest.setOnClickListener(this);
		dinner = (ImageButton) findViewById(R.id.time_dinner);
		dinner.setOnClickListener(this);
		exam = (ImageButton) findViewById(R.id.time_exam);
		exam.setOnClickListener(this);
		exp = (ImageButton) findViewById(R.id.time_exp);
		exp.setOnClickListener(this);
		travel = (ImageButton) findViewById(R.id.time_travel);
		travel.setOnClickListener(this);
		email = (ImageButton) findViewById(R.id.time_email);
		email.setOnClickListener(this);
		meeting = (ImageButton) findViewById(R.id.time_meeting);
		meeting.setOnClickListener(this);
		interview = (ImageButton) findViewById(R.id.time_interview);
		interview.setOnClickListener(this);
		date = (ImageButton) findViewById(R.id.time_date);
		date.setOnClickListener(this);
		others = (ImageButton) findViewById(R.id.time_others);
		others.setOnClickListener(this);
		save_project = (Button)findViewById(R.id.time_save);
		save_project.setOnClickListener(this);
		imageView = (ImageView) findViewById(R.id.time_imgview);
		edt_name = (EditText)findViewById(R.id.time_projectname);
		datePicker = (DatePicker) findViewById(R.id.time_datePicker);
		
		Intent intent = getIntent();
		project_name_old = intent.getStringExtra("project_name");
		project_deadline_old = intent.getStringExtra("project_deadline");
		logo_old = intent.getStringExtra("project_logo");
		if(!logo_old.equals("")) {
			logo = Integer.parseInt(logo_old);
		}
		
		
		if (!project_name_old.equals("")){
			//edt_deadline.setText(project_deadline_old);
			String sub[];
			sub = project_deadline_old.split("/");
			int year = Integer.parseInt(sub[0]);
			int month = Integer.parseInt(sub[1]) - 1;
			int day = Integer.parseInt(sub[2]);
			datePicker.updateDate(year, month, day);
			edt_name.setText(project_name_old);
			Log.d("test", "backgroundResource" + logo_old);
			imageView.setBackgroundResource(getProjectlogo(Integer.parseInt(logo_old)));
			update = 1;
			Log.d("test", "project to update");
		}
		
	}
	
	/****根据logo_index获取资源图片****/
	public int getProjectlogo(int logoindex) {
		int resID = 0;
		ApplicationInfo appInfo = getApplicationInfo();
		switch (logoindex) {
		case 0:
			resID = R.drawable.small_contest;
			Log.d("test", "small_contest is " + resID);
			break;
		case 1:
			resID = R.drawable.small_dinner;
			Log.d("test", "small_contest is " + resID);
			break;
		case 2:
			resID = R.drawable.small_exam;
			Log.d("test", "small_contest is " + resID);
			break;
		case 3:
			resID = R.drawable.small_exp;
			Log.d("test", "small_contest is " + resID);
			break;
		case 4:
			resID = R.drawable.small_travel;
			Log.d("test", "small_contest is " + resID);
			break;
		case 5:
			resID = R.drawable.small_email;
			Log.d("test", "small_contest is " + resID);
			break;
		case 6:
			resID = R.drawable.small_meeting;
			Log.d("test", "small_contest is " + resID);
			break;
		case 7:
			resID = R.drawable.small_interview;
			Log.d("test", "small_contest is " + resID);
			break;
		case 8:
			resID = R.drawable.small_date;
			Log.d("test", "small_contest is " + resID);
			break;
		case 9:
			resID = R.drawable.small_others;
			Log.d("test", "small_contest is " + resID);
			break;
		default:
			resID = R.drawable.small_others;
			Log.d("test", "small_contest is " + resID);
			break;
		}
		return resID;
	}
}
