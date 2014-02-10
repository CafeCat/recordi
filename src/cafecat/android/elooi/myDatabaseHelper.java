package cafecat.android.elooi;

import android.database.sqlite.SQLiteOpenHelper;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class myDatabaseHelper extends SQLiteOpenHelper {
	private static final String DATABASE_NAME = "elooi";
	private static final int DATABASE_VERSION = 4;
	private static final String TABLE_CREATE_TAG = "create table tags (tag_id INTEGER primary key autoincrement, " +
			"timeline_id INTEGER DEFAULT 0, serverid INTEGER DEFAULT 0, tag TEXT not null, tagicon TEXT not null);";
	private static final String TABLE_CREATE_TIMELINE="create table timeline (timeline_id INTEGER primary key autoincrement, serverid INTEGER DEFAULT 0 , name TEXT not null, " +
			"profilepic TEXT not null, audio TEXT not null, timestamp TEXT not null, echoed TEXT not null, favourited INTEGER DEFAULT 0)";
	public myDatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL(TABLE_CREATE_TAG);
		db.execSQL(TABLE_CREATE_TIMELINE);
	}
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		Log.w(myDatabaseHelper.class.getName(),
				"Upgrading database from version " + oldVersion + " to "
						+ newVersion + ", which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS tags");
		db.execSQL("DROP TABLE IF EXISTS timeline");
		onCreate(db);
	}
}
