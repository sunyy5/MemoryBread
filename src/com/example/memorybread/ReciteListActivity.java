package com.example.memorybread;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
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
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class ReciteListActivity extends Activity {

	private ListView lv;
	private List<Map<String, String>> paragraphs = new ArrayList<Map<String, String>>();
	public static String my_content;
	public String get_p_name;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_recite_list);
		
		Intent intent = getIntent();
		get_p_name = intent.getStringExtra("p_name");
		
		lv = (ListView)findViewById(R.id.listView_paragraph);

        initList();
        Log.d("test", "Finish Init!");
        
        updateList();
	}
	
	protected void onResume(Bundle saveInstanceState) {
		super.onResume();
		initList();
	}
	
	private void parseJSONWithJSONObject(String jsonData) {
		
		String jsonData_new = "";
		for(int i = 0; i < jsonData.length(); i++) {
			jsonData_new += jsonData.charAt(i);
			if(jsonData.charAt(i) == ':') {
				i = i + 2;
			}
		}
		Log.d("test", "test");
		Log.d("test", jsonData_new);
		Log.d("test", "Go in function parse!");
		//jsonData = "[{'num':'������','����':88}, {'num':'��Ȥ', '����':28}, {'num':'����','����':48}]";
		
		try {
			Log.d("test", "1");
			JSONArray jsonArray = new JSONArray(jsonData_new);
			Log.d("test", "2");
			Integer len = jsonArray.length();
			Log.d("test", "len = " + len.toString());
			for(int i = 0; i < jsonArray.length(); i++) {
				JSONObject jsonObject = jsonArray.getJSONObject(i);
				//String ep_account = jsonObject.getString("account");
				String ep_content = jsonObject.getString("content");
				//String ep_content = "";
				//byte[] convertoBytes = ep_content_old.getBytes("UTF-8");
				//ep_content = new String(convertoBytes, "UTF-8");
				String ep_name = jsonObject.getString("pname");
		     	Log.d("test", "content is " + ep_content);
				Log.d("test", "pname is " + ep_name);
				Map<String, String> map = new HashMap<String, String>();
				map.put("_content", ep_content);
				map.put("pname", ep_name);
				
				paragraphs.add(map);
				//add_a_paragraph();
			}
		} catch(Exception e) {
			Log.d("test", "Go in function parse but error!");
			e.printStackTrace();
		}
	}
	
	private Handler handler = new Handler(){
		public void handleMessage(Message msg){
			switch (msg.what){
				case 1:
					Log.d("test", "Before parse!");
					updateList();
					break;
				case 2://after delete
					getAllParagraph(LoginActivity.account);
					updateList();
					break;
				default:
					break;
			}
		}
	};
	
	//���ƶ�ȡ���������ݣ��ŵ�paragraphs��
	private void getAllParagraph(final String account){
		new Thread(new Runnable(){
    		public void run(){
    			HttpURLConnection connection = null;
    			try{
    				if(paragraphs.size() > 0)
    					paragraphs.clear();
    				
    				URL url = new URL("http://115.28.204.167:8000/recite");
    				Log.d("test", "url");
    				Log.d("test", account);
    				connection = (HttpURLConnection) url.openConnection();
    				connection.setRequestMethod("POST");
    				DataOutputStream out = new DataOutputStream(connection.getOutputStream());
    				
    				out.writeBytes("account="+account);
    				Log.d("test", "send");
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
    				Log.d("test", "���ص���" + response.toString());
    				parseJSONWithJSONObject(response.toString());
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
	
	private void deleteParagraphOnCloud(final String account, final String p_name_to_delete){
		new Thread(new Runnable(){
    		public void run(){
    			HttpURLConnection connection = null;
    			try{
    				URL url = new URL("http://115.28.204.167:8000/recite_delete");
    				Log.d("test", "url");
    				
    				connection = (HttpURLConnection) url.openConnection();
    				connection.setRequestMethod("POST");
    				DataOutputStream out = new DataOutputStream(connection.getOutputStream());
    				
    				out.writeBytes("account="+URLEncoder.encode(account, "UTF-8")+"&pname="+URLEncoder.encode(p_name_to_delete, "UTF-8"));
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
    				Log.d("test", "���ص���ɾ���ɹ�����ʧ��" + response.toString());
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
	
	
	
	
	//��ʼ���б�
    private void initList(){
    	
    	getAllParagraph(LoginActivity.account);
    	
    	SimpleAdapter adapter = new SimpleAdapter(this, paragraphs, R.layout.paragraph_item
                 , new String[]{"_content","pname"}, new int[]{R.id.itm_content, R.id.textView_each_para});
    	lv.setAdapter(adapter);

		 //��ת�����н���
    	lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		    	Intent intent = new Intent(ReciteListActivity.this,ReciteActivity.class);
		    	TextView tx_content = (TextView) view.findViewById(R.id.itm_content);
				TextView tx_name = (TextView) view.findViewById(R.id.textView_each_para);
				intent.putExtra("_pcontent", tx_content.getText().toString());
				intent.putExtra("_pname", tx_name.getText().toString());
				Log.d("test", tx_content.getText().toString());
				Log.d("test", tx_name.getText().toString());
				//��id���͵����н��棬�Ӷ��ڱ��н�����Ը������id���ƶ����ݿ�ȡ����Ӧid�Ķ���
		    	startActivity(intent);
			}
		});

		//���б��������������ȷ�Ͽ�ɾ���ó�Ա��ˢ���б�
    	lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				// �� TextView ��һ�����ص�TextView ����������ų�Ա�� id 
				TextView tx = (TextView) view.findViewById(R.id.textView_each_para);
				
				final String name_to_delete = tx.getText().toString();
				
				// ����ȷ�϶Ի���
				AlertDialog.Builder builder = new AlertDialog.Builder(ReciteListActivity.this);
				// ���ñ���
		    	String s = String.format(getResources().getString(R.string.del_confirm), name_to_delete);
				builder.setTitle(s);
				// ���ð�ť
				builder.setPositiveButton("��", new DialogInterface.OnClickListener() {					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						//TODO: ʵ���ƶ����ݿ�ɾ������////////
						deleteParagraphOnCloud(LoginActivity.account, name_to_delete);
						//////////////////////////////////
						updateList();
					}
				}).setNegativeButton("��", null);
				// ��ʾ��
				builder.show();
				// ����ֵ������û����
				return true;
			}
		});
    }
    
    
    
	//ˢ�������б����ʾ�� �����ݿ����������¶�һ��
	private void updateList(){
		
		//TODO:ʵ���ƶ����ݿ�����ȫ��ȡ����///////

		///////////////////////////////////////
		
    	// ˢ��ListView
    	((SimpleAdapter)lv.getAdapter()).notifyDataSetChanged();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.recite_list, menu);
		return true;
	}

}
