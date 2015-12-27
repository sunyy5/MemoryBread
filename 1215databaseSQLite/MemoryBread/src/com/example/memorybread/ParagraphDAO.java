package com.example.memorybread;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ParagraphDAO extends SQLiteOpenHelper{
	private static final String DB_NAME = "paragraph.db";
	private static final int DB_VERSION = 1;
	private static final String TABLE_NAME = "paragraph";
	private static final String SQL_CREATE_TABLE = "create table "
			+ TABLE_NAME + "(_id integer primary key autoincrement,"
			+ "p_name text not null, p_content text, p_category integer);";
	
	public ParagraphDAO(Context c) {
		super(c, DB_NAME, null, DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// �������ݿ��
		db.execSQL(SQL_CREATE_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// �������ݿ�汾�����ﲻʹ�ã�
		// db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
		// onCreate(db);
	}
	
	//�������
	public long insert(Paragraph entity) {
		SQLiteDatabase db = getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("p_name", entity.getPname());
		values.put("p_content", entity.getPcontent());
		values.put("p_category", entity.getPcategory());
		// ���뱣֤ values ����һ���ֶβ�Ϊnull ���������
		long rid = db.insert(TABLE_NAME, null, values);
		db.close();
		return rid;
	}
	
	//ɾ������
	public int deleteById(Integer id) {
		SQLiteDatabase db = getWritableDatabase();
		String whereClause = "_id = ?";
		String[] whereArgs = { id.toString() };
		int row = db.delete(TABLE_NAME, whereClause, whereArgs);
		db.close();
		return row;
	}
	
	//���²���
	public int update(Paragraph entity) {
		SQLiteDatabase db = getWritableDatabase();
		String whereClause = "_id = ?";
		String[] whereArgs = { entity.getId().toString() };
		ContentValues values = new ContentValues();
		values.put("p_name", entity.getPname());
		values.put("p_content", entity.getPcontent());
		values.put("p_category", entity.getPcategory());
		int rows = db.update(TABLE_NAME, values, whereClause, whereArgs);
		db.close();
		return rows;
	}
	
	//��ѯ����
	public Paragraph getById(Integer id){
		Paragraph p = null;
		SQLiteDatabase db = getReadableDatabase();
		String selection = "_id = ?";
		String[] selectionArgs = { id.toString() };
		Cursor c = db.query(TABLE_NAME, null, selection, selectionArgs, null,null, null);
		if (c.moveToNext()){
			p = new Paragraph(c.getString(1),c.getString(2),c.getInt(3));
		}
		c.close();
		db.close();
		return p;
	}	
	
	//���������������
	public List<Paragraph> getAll(){
		List<Paragraph> list = new ArrayList<Paragraph>();
		SQLiteDatabase db = getReadableDatabase();
		Cursor c = db.query(TABLE_NAME, null, null, null, null,null, null);
		while(c.moveToNext()){
			Paragraph p = new Paragraph();
			p.setId(c.getInt(0));
			p.setPname(c.getString(1));
			p.setPcontent(c.getString(2));
			p.setPcategory(c.getInt(3));
			list.add(p);
		}
		c.close();
		db.close();
		return list;
	}	
	
	
	
}
