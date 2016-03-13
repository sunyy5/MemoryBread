package com.example.memorybread;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.baidu.tts.loopj.Base64;
import com.example.memorybread.ReFlashListView.IReflashListener;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements IReflashListener{
	private ImageButton mAdd;
	private Button mRecite;
	private Button mSetting;
	private ImageButton mInfo;
	private TextView tv_main_name;
	private TextView tv_title1;
	private String back_ac = "";
	private String back_nickname = "";
	private String back_job = "";
	private String back_email = "";
	private String back_head = "";
	Bitmap head;
	BitmapDrawable draw;
	
	/****project_list****/
	private static final int REQ_MEMBER = 0;
	public ReFlashListView lv;
	private List<Map<String, Object>> projects = new ArrayList<Map<String, Object>>();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		mAdd = (ImageButton) findViewById(R.id.bt_add);
		tv_main_name = (TextView)findViewById(R.id.main_name);
		tv_title1 = (TextView)findViewById(R.id.title1);
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
		mInfo = (ImageButton) findViewById(R.id.main_head);
		mInfo.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this, InfoActivity.class);
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
		initView();
		initProjectList();
		/****preoject_end****/
		
		/****头像****/
		initHead();
		/********/
	}
	
	/****头像****/
	private void initHead() {
		getDataFromCloud(LoginActivity.account);
	}
	/********/
	
	public void onResume() {
		initHead();
		super.onResume();
	}
	
	private void getDataFromCloud(final String account) {

		new Thread(new Runnable() {
			public void run() {
				HttpURLConnection connection = null;
				try {
					URL url = new URL(
							"http://115.28.204.167:8000/info_update");
					Log.d("test", "project_url");

					connection = (HttpURLConnection) url.openConnection();
					connection.setRequestMethod("POST");
					DataOutputStream out = new DataOutputStream(
							connection.getOutputStream());
					out.writeBytes("account=" + account);
					InputStream in = connection.getInputStream();
					BufferedReader reader = new BufferedReader(
							new InputStreamReader(in));
					StringBuilder response = new StringBuilder();
					String line;
					Log.d("test", "info_read");

					while ((line = reader.readLine()) != null) {
						response.append(line);
					}
					Message message = new Message();
					message.what = 3;
					message.obj = response.toString();
					Log.d("test", "返回的是" + response.toString());
					handler.sendMessage(message);
				} catch (Exception e) {
					Log.d("test", "info_error! no response");
					e.printStackTrace();
				} finally {
					if (connection != null)
						connection.disconnect();
				}
			}
		}).start();
	}
	
	
	
	/****project2****/
	public void initView() {
		lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				TextView tname = (TextView) view.findViewById(R.id.title);
				TextView tdeadline = (TextView) view.findViewById(R.id.show_time);
				final String name_to_delete = tname.getText().toString();
				final String deadline_to_delete = tdeadline.getText().toString();
				
				// 弹出确认对话框
				AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
				// 设置标题
		    	String s = String.format(getResources().getString(R.string.del_confirm_project), name_to_delete);
				builder.setTitle(s);
				// 设置按钮
				builder.setPositiveButton("是", new DialogInterface.OnClickListener() {					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						//TODO: 实现云端数据库删除段落////////
						deleteProjectsOnCloud(LoginActivity.account, name_to_delete, deadline_to_delete);
						//////////////////////////////////
					}
				}).setNegativeButton("否", null);
				// 显示！
				builder.show();
				// 返回值在这里没作用
				return true;
			}
		});
		
		//单击编辑
		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		    	Intent intent = new Intent(MainActivity.this, AddProjectActivity.class);
		    	TextView tname = (TextView) view.findViewById(R.id.title);
				TextView tdeadline = (TextView) view.findViewById(R.id.show_time);
				TextView tindex = (TextView) view.findViewById(R.id.project_logo_index);
				intent.putExtra("project_name", tname.getText().toString());
				intent.putExtra("project_deadline", tdeadline.getText().toString());
				intent.putExtra("project_logo", tindex.getText().toString());
				Log.d("test", tname.getText().toString());
				Log.d("test", tdeadline.getText().toString());
				Log.d("test", tindex.getText().toString());
				Log.d("test", "------");
				startActivityForResult(intent, REQ_MEMBER);
			}
		});
		
	}
	/****project2_end****/
	
	/********/
	public void onReflash() {
		Intent intentaddproject = new Intent(MainActivity.this, AddProjectActivity.class);
		intentaddproject.putExtra("project_name", "");
		intentaddproject.putExtra("project_deadline", "");
		intentaddproject.putExtra("project_logo", "");
		startActivityForResult(intentaddproject, REQ_MEMBER);
		
		//Toast.makeText(MainActivity.this, "Add", Toast.LENGTH_SHORT).show();
		lv.reflashComplete();
		//Toast.makeText(MainActivity.this, "Add complete", Toast.LENGTH_SHORT).show();
	}
	/****project_list_end****/
	
	
	
	/****project****/
	protected void onResume(Bundle saveInstanceState) {
		super.onResume();
		Log.d("test", "onResume");
		initProjectList();
	}
	/******/
	
	private void updateProjectList(){
		Log.d("test", "updateProjectList");
		
		Collections.sort(projects, new Comparator<Map<String, Object>>() {
            public int compare(Map<String, Object> m1, Map<String, Object> m2) {
            	return m1.get("project_deadline").toString().compareTo(m2.get("project_deadline").toString());
            }

        });
		
    	SimpleAdapter adapter = new SimpleAdapter(this, projects, R.layout.project_item2
         ,new String[]{"project_deadline","project_name","project_logo","project_logo_index"}, 
         new int[]{R.id.show_time, R.id.title, R.id.project_logo, R.id.project_logo_index});
    	
    	lv.setAdapter(adapter);
    	tv_title1.setText("待办事项：" + projects.size());
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
					initProjectList();
					updateProjectList();
					break;
				case 3:
					JSONObject j3;
					try {
						Log.d("test", "handler begin 1!");
						j3 = new JSONObject(msg.obj.toString());
						Log.d("test", "handler begin 2!");
						back_ac = j3.getString("account").toString();
						Log.d("test", back_ac);
						Log.d("test", "handler begin 3!");
						back_nickname = j3.getString("nickname").toString();
						Log.d("test", back_nickname);
						back_email = j3.getString("email").toString();
						Log.d("test", back_email);
						back_job = j3.getString("occupation").toString();
						Log.d("test", back_job);
						
						back_head = j3.getString("head").toString();
						Log.d("test", "headheadhead" + back_head);
						if(!back_head.equals("")) {
							back_head = back_head.replace("%2B", "+");
							byte[] bytes = Base64.decode(back_head, Base64.DEFAULT);
							head = Bitmap.createScaledBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.length), 300, 300, true);
//							head = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
							
							mInfo.setImageBitmap(head);	
							tv_main_name.setText(back_nickname);
						} else {
							draw = (BitmapDrawable) getResources().getDrawable(
									R.drawable.head);
							head = draw.getBitmap();
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
					break;
				default:
					break;
			}
		}
	};
	/****project_end****/
	
	
	/****project****/
	private void parseJSONWithJSONObject(String jsonData) {
		String jsonData_new = "";
		for(int i = 0; i < jsonData.length(); i++) {
			jsonData_new += jsonData.charAt(i);
			if(jsonData.charAt(i) == ':') {
				i = i + 2;
			}
		}
		
		Log.d("test", "Project test");
		Log.d("test", jsonData_new);
		Log.d("test", "Project Go in function parse!");
		try {
			Log.d("test", "1");
			JSONArray jsonArray = new JSONArray(jsonData_new);
			Log.d("test", "2");
			Integer len = jsonArray.length();
			Log.d("test", "len = " + len.toString());
			for(int i = 0; i < jsonArray.length(); i++) {
				JSONObject jsonObject = jsonArray.getJSONObject(i);
				String response_deadline = jsonObject.getString("project_deadline");
				String response_name = jsonObject.getString("project_name");
				//此处从数据库获得logo序号（0~9）
				int response_logo_index = jsonObject.getInt("project_logo"); 
				//根据logo序号获得图片id
				int response_logID = getProjectlogo(response_logo_index);
				
		     	Log.d("test", "project deadline is " + response_deadline);
				Log.d("test", "project name is " + response_name);
				Log.d("test", "project logo is" + String.valueOf(response_logID));
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("project_deadline", response_deadline);
				map.put("project_name", response_name);
				map.put("project_logo", response_logID);
				map.put("project_logo_index", response_logo_index);
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
	
	
	/***project2****/
	private void deleteProjectsOnCloud(final String account, final String project_name, final String project_deadline){
		new Thread(new Runnable(){
    		public void run(){
    			HttpURLConnection connection = null;
    			try{
    				URL url = new URL("http://115.28.204.167:8000/project_delete");
    				Log.d("test", "url");
    				
    				connection = (HttpURLConnection) url.openConnection();
    				connection.setRequestMethod("POST");
    				DataOutputStream out = new DataOutputStream(connection.getOutputStream());
    				
    				Log.d("test", "删除");
    				Log.d("test", project_name);
    				Log.d("test", project_deadline);
    				out.writeBytes("account="+URLEncoder.encode(account, "UTF-8")+"&project_name="+URLEncoder.encode(project_name, "UTF-8")
    						+"&project_deadline="+URLEncoder.encode(project_deadline, "UTF-8"));
    				InputStream in = connection.getInputStream();
    				BufferedReader reader = new BufferedReader(new InputStreamReader(in));
    				StringBuilder response = new StringBuilder();
    				String line;
    				Log.d("test", "read");
    				while((line=reader.readLine())!=null){
    					response.append(line);
    				}
    				Message message = new Message();
    				message.what=2;
    				message.obj=response.toString();
    				Log.d("test", "返回的是删除成功还是失败" + response.toString());
    				handler.sendMessage(message);
    				
    			}catch(Exception e){
    				Log.d("test", "error! no response!");
    				e.printStackTrace();
    				
    			}finally{
    				if(connection!=null)
    					connection.disconnect();  				
    			} 			
    		}
    	}).start();	
	}
	/***project_end2***/
	
	/***project****/
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQ_MEMBER && resultCode == RESULT_OK){
			initProjectList();				// 刷新列表
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	private void initProjectList(){
		getAllProjects(LoginActivity.account);
    }	
	/****project_end****/	
	
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
