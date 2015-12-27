package com.example.memorybread;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class ReciteListActivity extends Activity {

	private ListView lv;
	private List<Map<String, String>> paragraphs = new ArrayList<Map<String, String>>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_recite_list);
		
		lv = (ListView)findViewById(R.id.listView_paragraph);

        initList();
        updateList();
	}
	
	//��ʼ���б�
    private void initList(){
    	// �����б��ǿյ�
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
						//deleteParagraph(_id);
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
	
	//ˢ�������б����ʾ�� �����ݿ����������¶�һ��
	private void updateList(){
		/*ParagraphDAO dao = new ParagraphDAO(this);
		if (paragraphs.size() > 0){
			paragraphs.clear();
		}
		// ��List<Paragraph> ת��Ϊ Adapter �����ݸ�ʽ ��List<Map<String,String>>��
    	for (Paragraph p: dao.getAll()){
    		Map<String, String> map = new HashMap<String, String>();
    		map.put("_id", p.getId().toString());
    		map.put("pname", p.getPname());
    		paragraphs.add(map);
    	}*/
		
		//TODO:ʵ���ƶ����ݿ�����ȫ��ȡ����///////
		//
		//
		Map<String, String> map = new HashMap<String, String>();
		Integer id = 1;
		map.put("_id", id.toString());
		map.put("pname", "Ŀ��");
		paragraphs.add(map);
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
