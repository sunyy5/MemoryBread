package com.example.memorybread;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.example.memorybread.ReFlashListView.IReflashListener;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements IReflashListener{
	private ImageButton mAdd;
	private Button mRecite;
	private Button mSetting;
	
	/****project_list****/
	public ReFlashListView lv;
	private List<Map<String, String>> projects = new ArrayList<Map<String, String>>();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		mAdd = (ImageButton) findViewById(R.id.bt_add);
		mAdd.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this, AddActivity.class);
				startActivity(intent);
			}
		});
		
		mSetting = (Button) findViewById(R.id.bt_setting);
		mSetting.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this, SettingActivity.class);
				startActivity(intent);
			}
		});
		
		mRecite = (Button) findViewById(R.id.bt_recite);
		mRecite.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this, ReciteListActivity.class);
				startActivity(intent);
			}
		});
		
		/****project****/
		lv = (ReFlashListView)findViewById(R.id.listView_projects);
		lv.setInterface(this);
		initProjectList();
		/****preoject_end****/
	}
	/********/
	public void onReflash() {
		Intent intentaddproject = new Intent(MainActivity.this, AddProjectActivity.class);
		startActivity(intentaddproject);
		
		//Toast.makeText(MainActivity.this, "Add", Toast.LENGTH_SHORT).show();
		lv.reflashComplete();
		//Toast.makeText(MainActivity.this, "Add complete", Toast.LENGTH_SHORT).show();
	}
	/****project_list_end****/
	
	
	
	/****project****/
	protected void onResume(Bundle saveInstanceState) {
		super.onResume();
		initProjectList();
	}
	
	private void updateProjectList(){
		Log.d("test", "updateProjectList");
    	SimpleAdapter adapter = new SimpleAdapter(this, projects, R.layout.project_item2
                 , new String[]{"project_deadline","project_name"}, new int[]{R.id.show_time, R.id.title});
    	lv.setAdapter(adapter);
	}
	/****project_end****/
	
	/****project刷新ListView****/
	private Handler handler = new Handler(){
		public void handleMessage(Message msg){
			switch (msg.what){
				case 1:
					Log.d("test", "Before parse!");
					updateProjectList();
					break;
				case 2://after delete
					getAllProjects(LoginActivity.account);
					updateProjectList();
					break;
				default:
					break;
			}
		}
	};
	/****project_end****/
	
	
	/****project****/
	private void parseJSONWithJSONObject(String jsonData) {
		
		Log.d("test", "Project test");
		Log.d("test", jsonData);
		Log.d("test", "Project Go in function parse!");
		try {
			Log.d("test", "1");
			JSONArray jsonArray = new JSONArray(jsonData);
			Log.d("test", "2");
			Integer len = jsonArray.length();
			Log.d("test", "len = " + len.toString());
			for(int i = 0; i < jsonArray.length(); i++) {
				JSONObject jsonObject = jsonArray.getJSONObject(i);
				String response_deadline = jsonObject.getString("project_deadline");
				String response_name = jsonObject.getString("project_name");
		     	Log.d("test", "project deadline is " + response_deadline);
				Log.d("test", "project name is " + response_name);
				Map<String, String> map = new HashMap<String, String>();
				map.put("project_deadline", response_deadline);
				map.put("project_name", response_name);
				projects.add(map);
			}
		} catch(Exception e) {
			Log.d("test", "Project Go in function parse but error!");
			e.printStackTrace();
		}
	}
	/****project_end****/
	
	/****project(从云端取回所有数据，放到projects里)****/
	private void getAllProjects(final String account){
		/*Map<String, String> map = new HashMap<String, String>();
		map.put("project_deadline", "2015/12/26");
		map.put("project_name", "数据库project");
		projects.add(map);*/
		
		new Thread(new Runnable() {
			public void run() {
				HttpURLConnection connection = null;
				try {
					if(projects.size() > 0)
						projects.clear();
					URL url = new URL("http://115.28.204.167:8000/projects_find");
					Log.d("test", "project_url");
					
					connection = (HttpURLConnection) url.openConnection();
					connection.setRequestMethod("POST");
					DataOutputStream out = new DataOutputStream(connection.getOutputStream());
					out.writeBytes("account="+account);
					InputStream in = connection.getInputStream();
					BufferedReader reader = new BufferedReader(new InputStreamReader(in));
					StringBuilder response = new StringBuilder();
					String line;
					Log.d("test", "project_read");
					
					while((line=reader.readLine()) != null) {
						response.append(line);
					}
					Message message = new Message();
					message.what = 1;
					message.obj = response.toString();
					Log.d("test", "返回的是" + response.toString());
					parseJSONWithJSONObject(response.toString());
					handler.sendMessage(message);
				} catch(Exception e) {
					Log.d("test", "project_error! no response");
					e.printStackTrace();
				} finally {
					if(connection != null) 
						connection.disconnect();
				}
			}
			
		}).start();
		
	}
	/****project_end****/
	
	/***project****/
	private void initProjectList(){
    	
		getAllProjects(LoginActivity.account);
		

    }	
	/****project_end****/
	
	
	
}
