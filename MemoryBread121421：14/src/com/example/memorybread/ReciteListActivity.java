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
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class ReciteListActivity extends Activity {

	private ListView lv;
	private List<Map<String, String>> paragraphs = new ArrayList<Map<String, String>>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_recite_list);
		
		lv = (ListView)findViewById(R.id.listView_paragraph);

        initList();
        Log.d("test", "Finish Init!");
        updateList();
	}
	
	private void parseJSONWithJSONObject(String jsonData) {
		Log.d("test", "Go in function parse!");
		try {
			JSONArray jsonArray = new JSONArray(jsonData);
			for(int i = 0; i < jsonArray.length(); i++) {
				JSONObject jsonObject = jsonArray.getJSONObject(i);
				//String ep_account = jsonObject.getString("account");
				String ep_name = jsonObject.getString("pname");
				String ep_content = jsonObject.getString("content");
		     	//Log.d("test", "account is " + ep_account);
				Log.d("test", "account is " + ep_name);
				Log.d("test", "account is " + ep_content);
				Map<String, String> map = new HashMap<String, String>();
				Integer id = 2;
				map.put("_id", id.toString());
				map.put("pname", ep_name);
				
				paragraphs.add(map);
				//add_a_paragraph();
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	private Handler handler = new Handler(){
		public void handleMessage(Message msg){
			switch (msg.what){
				case 1:
					Log.d("test", "Before parse!");
					parseJSONWithJSONObject(msg.obj.toString());

				default:
					break;
			}
		}
	};
	
	//从云端取回所有数据，放到paragraphs里
	private void getAllParagraph(final String account){
		new Thread(new Runnable(){
    		public void run(){
    			HttpURLConnection connection = null;
    			try{
    				URL url = new URL("http://115.28.204.167:8000/recite");
    				Log.d("test", "url");
    				
    				connection = (HttpURLConnection) url.openConnection();
    				connection.setRequestMethod("POST");
    				DataOutputStream out = new DataOutputStream(connection.getOutputStream());
    				out.writeBytes("account="+account);
    				InputStream in = connection.getInputStream();
    				BufferedReader reader = new BufferedReader(new InputStreamReader(in));
    				StringBuilder response = new StringBuilder();
    				String line;
    				Log.d("test", "read");
    				
    				while((line=reader.readLine())!=null){
    					response.append(line);
    				}
    				Message message = new Message();
    				message.what=1;
    				message.obj=response.toString();
    				Log.d("test", "返回的是" + response.toString());
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
	
	
	
	
	//初始化列表
    private void initList(){
    	
    	getAllParagraph(LoginActivity.account);
    	
    	SimpleAdapter adapter = new SimpleAdapter(this, paragraphs, R.layout.paragraph_item
                 , new String[]{"_id","pname"}, new int[]{R.id.itm_id, R.id.textView_each_para});
    	lv.setAdapter(adapter);

		 //跳转到背诵界面
    	lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		    	Intent intent = new Intent(ReciteListActivity.this,ReciteActivity.class);
				TextView tx = (TextView) view.findViewById(R.id.itm_id);
				intent.putExtra("_id", Integer.valueOf(tx.getText().toString()));
				//把id传送到背诵界面，从而在背诵界面可以根据这个id到云端数据库取出对应id的段落
		    	startActivity(intent);
			}
		});

		//绑定列表项长按操作：弹出确认框，删除该成员并刷新列表。
    	lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				// 此 TextView 是一个隐藏的TextView ，仅用来存放成员的 id 
				TextView tx = (TextView) view.findViewById(R.id.itm_id);
				final Integer _id = Integer.valueOf(tx.getText().toString());
				
				tx = (TextView) view.findViewById(R.id.textView_each_para);
				String name = tx.getText().toString();
				
				// 弹出确认对话框
				AlertDialog.Builder builder = new AlertDialog.Builder(ReciteListActivity.this);
				// 设置标题
		    	String s = String.format(getResources().getString(R.string.del_confirm), name);
				builder.setTitle(s);
				// 设置按钮
				builder.setPositiveButton("是", new DialogInterface.OnClickListener() {					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						//TODO: 实现云端数据库删除段落////////
						//deleteParagraph(_id);
						//////////////////////////////////
						updateList();
					}
				}).setNegativeButton("否", null);
				// 显示！
				builder.show();
				// 返回值在这里没作用
				return false;
			}
		});
    }
	
	//刷新整个列表的显示： 把数据库表的数据重新读一遍
	private void updateList(){
		/*ParagraphDAO dao = new ParagraphDAO(this);
		if (paragraphs.size() > 0){
			paragraphs.clear();
		}
		// 把List<Paragraph> 转换为 Adapter 的数据格式 （List<Map<String,String>>）
    	for (Paragraph p: dao.getAll()){
    		Map<String, String> map = new HashMap<String, String>();
    		map.put("_id", p.getId().toString());
    		map.put("pname", p.getPname());
    		paragraphs.add(map);
    	}*/
		
		//TODO:实现云端数据库段落表全部取出来///////
		//
		//
		Map<String, String> map = new HashMap<String, String>();
		Integer id = 1;
		map.put("_id", id.toString());
		map.put("pname", "目送");
		paragraphs.add(map);
		///////////////////////////////////////
		
    	// 刷新ListView
    	((SimpleAdapter)lv.getAdapter()).notifyDataSetChanged();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.recite_list, menu);
		return true;
	}

}
