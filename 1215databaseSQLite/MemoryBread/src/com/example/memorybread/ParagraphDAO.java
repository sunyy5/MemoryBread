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
		// 创建数据库表
		db.execSQL(SQL_CREATE_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// 更新数据库版本（这里不使用）
		// db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
		// onCreate(db);
	}
	
	//插入操作
	public long insert(Paragraph entity) {
		SQLiteDatabase db = getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("p_name", entity.getPname());
		values.put("p_content", entity.getPcontent());
		values.put("p_category", entity.getPcategory());
		// 必须保证 values 至少一个字段不为null ，否则出错
		long rid = db.insert(TABLE_NAME, null, values);
		db.close();
		return rid;
	}
	
	//删除操作
	public int deleteById(Integer id) {
		SQLiteDatabase db = getWritableDatabase();
		String whereClause = "_id = ?";
		String[] whereArgs = { id.toString() };
		int row = db.delete(TABLE_NAME, whereClause, whereArgs);
		db.close();
		return row;
	}
	
	//更新操作
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
	
	//查询操作
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
	
	//返回整个表的数据
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
