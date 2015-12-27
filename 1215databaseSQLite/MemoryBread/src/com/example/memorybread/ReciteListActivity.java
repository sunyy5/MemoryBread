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
	
	
	//��ʼ���б�
    private void initList(){
    	
    	SimpleAdapter adapter = new SimpleAdapter(this, paragraphs, R.layout.paragraph_item
                 , new String[]{"_id","pname"}, new int[]{R.id.itm_id, R.id.textView_each_para});
    	lv.setAdapter(adapter);

		 //��ת�����н���
    	lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		    	Intent intent = new Intent(ReciteListActivity.this,ReciteActivity.class);
				TextView tx = (TextView) view.findViewById(R.id.itm_id);
				intent.putExtra("_id", Integer.valueOf(tx.getText().toString()));
				//��id���͵����н��棬�Ӷ��ڱ��н�����Ը������id���ƶ����ݿ�ȡ����Ӧid�Ķ���
		    	startActivity(intent);
			}
		});

		//���б��������������ȷ�Ͽ�ɾ���ó�Ա��ˢ���б�
    	lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				// �� TextView ��һ�����ص�TextView ����������ų�Ա�� id 
				TextView tx = (TextView) view.findViewById(R.id.itm_id);
				final Integer _id = Integer.valueOf(tx.getText().toString());
				
				tx = (TextView) view.findViewById(R.id.textView_each_para);
				String name = tx.getText().toString();
				
				// ����ȷ�϶Ի���
				AlertDialog.Builder builder = new AlertDialog.Builder(ReciteListActivity.this);
				// ���ñ���
		    	String s = String.format(getResources().getString(R.string.del_confirm), name);
				builder.setTitle(s);
				// ���ð�ť
				builder.setPositiveButton("��", new DialogInterface.OnClickListener() {					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						//TODO: ʵ���ƶ����ݿ�ɾ������////////
						deleteParagraph(_id);
						//////////////////////////////////
						updateList();
					}
				}).setNegativeButton("��", null);
				// ��ʾ��
				builder.show();
				// ����ֵ������û����
				return false;
			}
		});
    }
    
	private void deleteParagraph(Integer id) {
		ParagraphDAO dao = new ParagraphDAO(this);
		dao.deleteById(id);
	}

	
	//ˢ�������б����ʾ�� �����ݿ����������¶�һ�� ��Ч���Եͣ����Ƿ��㣬������ʵ�飩
	private void updateList(){
		ParagraphDAO dao = new ParagraphDAO(this);
		if (paragraphs.size() > 0){
			paragraphs.clear();
		}
		// ��List<Paragraph> ת��Ϊ Adapter �����ݸ�ʽ ��List<Map<String,String>>��
    	for (Paragraph p: dao.getAll()){
    		Map<String, String> map = new HashMap<String, String>();
    		map.put("_id", p.getId().toString());
    		map.put("pname", p.getPname());
    		paragraphs.add(map);
    	}
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
