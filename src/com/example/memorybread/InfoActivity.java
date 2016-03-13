package com.example.memorybread;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.baidu.tts.loopj.Base64;


import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class InfoActivity extends Activity implements OnItemClickListener {
	ListView listView;
	List<String> listviList = new ArrayList<String>();
	private static String path = "/sdcard/myHead/";// sd路径
	private Bitmap head;// 头像Bitmap

	private String back_ac = "";
	private String back_nickname = "";
	private String back_job = "";
	private String back_email = "";
	private String back_head = "";
	List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
	BitmapDrawable draw;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_info);
		listView = (ListView) findViewById(R.id.info_listview);

		draw = (BitmapDrawable) getResources().getDrawable(
				R.drawable.head);
		head = draw.getBitmap();

		updateListview();
		listView.setOnItemClickListener(this);
	}

	private List<Map<String, Object>> getData() {

		list.clear();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("name", "头像");
		map.put("imgview", head);
		list.add(map);

		map = new HashMap<String, Object>();
		map.put("name", "账号");
		map.put("textview", back_ac);
		list.add(map);

		map = new HashMap<String, Object>();
		map.put("name", "昵称");
		map.put("textview", back_nickname);
		list.add(map);

		map = new HashMap<String, Object>();
		map.put("name", "职业");
		map.put("textview", back_job);
		list.add(map);

		map = new HashMap<String, Object>();
		map.put("name", "邮箱");
		map.put("textview", back_email);
		list.add(map);
	
		SimpleAdapter simpleAdapter = new SimpleAdapter(this, list,
				R.layout.info_item, new String[] { "name", "textview",
						"imgview" }, new int[] { R.id.info_name,
						R.id.info_textview, R.id.info_img });
			simpleAdapter.setViewBinder(new ListViewBinder());
			listView.setAdapter(simpleAdapter);
		
		return list;

	}

	private Handler handler = new Handler(){
		public void handleMessage(Message msg){
			switch (msg.what){
				case 1:
					JSONObject j;
					try {
						Log.d("test", "handler begin 1!");
						j = new JSONObject(msg.obj.toString());
						Log.d("test", "handler begin 2!");
						back_ac = j.getString("account").toString();
						Log.d("test", back_ac);
						Log.d("test", "handler begin 3!");
						back_nickname = j.getString("nickname").toString();
						Log.d("test", back_nickname);
						back_email = j.getString("email").toString();
						Log.d("test", back_email);
						back_job = j.getString("occupation").toString();
						Log.d("test", back_job);
						
						back_head = j.getString("head").toString();
						Log.d("test", "headheadhead" + back_head);
						if(!back_head.equals("")) {
							back_head = back_head.replace("%2B", "+");
							byte[] bytes = Base64.decode(back_head, Base64.DEFAULT);
							head = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
						} else {
							head = draw.getBitmap();
						}
						getData();
						
					} catch (JSONException e) {
						e.printStackTrace();
					}
					break;
				case 2:
					JSONObject j2;
					String str = null;
					Boolean success = null;
					try {
						j2 = new JSONObject(msg.obj.toString());
						str = j2.getString("msg").toString();
						success = j2.getBoolean("success");
					} catch (JSONException e) {
						e.printStackTrace();
					}
					if(success){
						Toast.makeText(InfoActivity.this, "成功修改用户头像", Toast.LENGTH_SHORT).show();
						updateListview();
					}
				default:
					break;
			}
		}
	};
	
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
					message.what = 1;
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

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		if (position == 0) {
			Intent intent1 = new Intent(Intent.ACTION_PICK, null);
			intent1.setDataAndType(
					MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
			startActivityForResult(intent1, 1);
		}
		if (position == 2) {
			Intent intent = new Intent(InfoActivity.this,
					InfoUpdateActivity.class);
			Bundle mbundle = new Bundle();
			mbundle.putString("name", "昵称");
			intent.putExtras(mbundle);
			startActivityForResult(intent, 4);
		}
		if (position == 3) {
			Intent intent = new Intent(InfoActivity.this,
					InfoUpdateActivity.class);
			Bundle mbundle = new Bundle();
			mbundle.putString("name", "职业");
			intent.putExtras(mbundle);
			startActivityForResult(intent, 4);
		}
		if (position == 4) {
			Intent intent = new Intent(InfoActivity.this,
					InfoUpdateActivity.class);
			Bundle mbundle = new Bundle();
			mbundle.putString("name", "邮箱");
			intent.putExtras(mbundle);
			startActivityForResult(intent, 4);
		}
	}
	
	public String getBitmapByte(Bitmap bitmap){   
		   String d = "";
		   ByteArrayOutputStream out = null;
		   try {   
			   out = new ByteArrayOutputStream();
			   bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
		       out.flush();   
		       out.close(); 
		       byte[] imgBytes = out.toByteArray();
		       d = Base64.encodeToString(imgBytes, Base64.DEFAULT);
		      
		   } catch (IOException e) {   
		       e.printStackTrace();   
		   } finally {
			   try {
				   out.flush();
				   out.close();
			   } catch (IOException e) {
				   e.printStackTrace();
			   }
		   }
		   String rs = d.replace("+", "%2B");
		   System.out.println(rs);
		   return rs;   
		}   
	
	private void sendBitmapByte(final String account, final String rs) {
		new Thread(new Runnable(){
    		public void run(){
    			HttpURLConnection connection = null;
    			try{
    				URL url = new URL("http://115.28.204.167:8000/send_bitmap");
    				connection = (HttpURLConnection) url.openConnection();
    				connection.setRequestMethod("POST");
    				DataOutputStream out = new DataOutputStream(connection.getOutputStream());
    				
    				out.writeBytes("account="+URLEncoder.encode(account, "UTF-8")
    						+"&head="+URLEncoder.encode(rs, "UTF-8"));
    				
    				InputStream in = connection.getInputStream();
    				BufferedReader reader = new BufferedReader(new InputStreamReader(in));
    				StringBuilder response = new StringBuilder();
    				String line;
    				while((line=reader.readLine())!=null){
    					response.append(line);
    				}

    				Message message = new Message();
    				message.what=2;
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

	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.d("test", String.valueOf(requestCode));
		switch (requestCode) {
		case 1:
			if (resultCode == RESULT_OK) {
				cropPhoto(data.getData());// 裁剪图片
			}
			break;
		case 3:
			if (data != null) {
				Bundle extras = data.getExtras();
				head = extras.getParcelable("data");
				if (head != null) {
					/****/
					//得到string
					sendBitmapByte(LoginActivity.account, getBitmapByte(head)); 
					/***/
					setPicToView(head);// 保存在SD卡中
				}
			}
			break;
		case 4:
			if (resultCode == RESULT_OK) {
				Log.d("test", "4 ok");
				updateListview();
			}
			break;
		default:
			Log.d("test", "default");
			updateListview();
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	};

	/**
	 * 调用系统的裁剪
	 * 
	 * @param uri
	 */
	public void cropPhoto(Uri uri) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		intent.putExtra("crop", "true");
		// aspectX aspectY 是宽高的比例
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		// outputX outputY 是裁剪图片宽高
		intent.putExtra("outputX", 337);
		intent.putExtra("outputY", 337);
		intent.putExtra("return-data", true);
		startActivityForResult(intent, 3);
	}

	private void setPicToView(Bitmap mBitmap) {
		String sdStatus = Environment.getExternalStorageState();
		if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) { // 检测sd是否可用
			return;
		}
		FileOutputStream b = null;
		File file = new File(path);
		file.mkdirs();// 创建文件夹
		String fileName = path + "head.jpg";// 图片名字
		try {
			b = new FileOutputStream(fileName);
			mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, b);// 把数据写入文件
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			try {
				b.flush();
				b.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void updateListview() {
		getDataFromCloud(LoginActivity.account);
		Log.d("test", "get " + back_ac);
		Log.d("test", "get " + back_nickname);
		Log.d("test", "get " + back_job);
		Log.d("test", "get " + back_email);
	}

}
