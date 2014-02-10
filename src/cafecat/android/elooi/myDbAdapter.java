package cafecat.android.elooi;

import android.content.ContentValues;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class myDbAdapter {
	private Context context;
	protected SQLiteDatabase database;
	protected myDatabaseHelper dbHelper;
	
	//myDbAdapter(){}
	public myDbAdapter(Context context)
	{
		this.context = context;
	}
	
	public myDbAdapter open() throws SQLException {
		dbHelper = new myDatabaseHelper(context);
		database = dbHelper.getWritableDatabase();
		return this;
	}
	
	public void close() {
		dbHelper.close();
	}
	
	public int addTimelineEvent(Integer serverid,String name, String profilepic, String audio, String timeStamp, String echoed, Integer favourited)
	{
		ContentValues values = new ContentValues();
		values.put("serverid",serverid);
		values.put("name", name);
		values.put("profilepic", profilepic);
		values.put("audio", audio);
		values.put("timeStamp", timeStamp);
		values.put("echoed", echoed);
		values.put("favourited", favourited);

		return (int) database.insertOrThrow("timeline", "serverid", values);
	}
	
	public int addTags(Integer timeline_id, Integer serverid, String tagicon,String tag)
	{
		ContentValues values = new ContentValues();
		values.put("timeline_id",timeline_id);
		values.put("serverid", serverid);
		values.put("tagicon", tagicon);
		values.put("tag", tag);
		
		return (int) database.insert("tags", "Tag", values);	
	}
	
	protected int cleanTable(String Table, String whereClause, String[] whereArg)
	{
		return (int)database.delete(Table, whereClause, whereArg);
	}

}
