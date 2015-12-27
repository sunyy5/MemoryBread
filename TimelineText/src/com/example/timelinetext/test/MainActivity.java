package com.example.timelinetext.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;

public class MainActivity extends Activity {

	private ListView listView;
	List<String> data ;
	private TimelineAdapter timelineAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_listview);

		listView = (ListView) this.findViewById(R.id.listview);
		listView.setDividerHeight(0);
		timelineAdapter = new TimelineAdapter(this, getData());
		listView.setAdapter(timelineAdapter);

	}

	private List<Map<String, Object>> getData() {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("title", "���ǵ�1�в�������");
		list.add(map);

		map = new HashMap<String, Object>();
		map.put("title", "���ǵ�2�в�������");
		list.add(map);

		map = new HashMap<String, Object>();
		map.put("title", "���ǵ�3�в�������");
		list.add(map);

		map = new HashMap<String, Object>();
		map.put("title", "���ǵ�4�в�������");
		list.add(map);
		return list;
	}

}
