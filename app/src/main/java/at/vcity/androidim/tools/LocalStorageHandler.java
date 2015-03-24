package at.vcity.androidim.tools;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class LocalStorageHandler extends SQLiteOpenHelper {

    private static final String TAG = LocalStorageHandler.class.getSimpleName();

    private static final String DATABASE_NAME = "AndroidIM.db";
    private static final int DATABASE_VERSION = 1;

    private static final String _ID = "_id";
    private static final String TABLE_NAME_MESSAGES = "androidim_messages";
    public static final String MESSAGE_RECEIVER = "receiver";
    public static final String MESSAGE_SENDER = "sender";
    private static final String MESSAGE_MESSAGE = "message";
    private static final String MESSAGE_PICLINK = "piclink";
    private static final String MESSAGE_TGT= "tgt";


    private static final String TABLE_MESSAGE_CREATE
            = "CREATE TABLE " + TABLE_NAME_MESSAGES
            + " (" + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + MESSAGE_RECEIVER + " VARCHAR(25), "
            + MESSAGE_SENDER + " VARCHAR(25), "
            +MESSAGE_MESSAGE + " VARCHAR(255),"
            +MESSAGE_PICLINK + " VARCHAR(255),"
            +MESSAGE_TGT + " VARCHAR(255));"        ;

    private static final String TABLE_MESSAGE_DROP =
            "DROP TABLE IF EXISTS "
                    + TABLE_NAME_MESSAGES;


    public LocalStorageHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_MESSAGE_CREATE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(TAG, "Upgrade der DB von V: "+ oldVersion + " zu V:" + newVersion + "; Alle Daten werden gelscht!");
        db.execSQL(TABLE_MESSAGE_DROP);
        onCreate(db);

    }

    public void insert(String sender, String receiver, String message, String piclink, String TGT){
        if(piclink==null)
            piclink="";

        if(TGT==null)
            TGT="";

        long rowId = -1;
        try{
            SQLiteDatabase db = getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(MESSAGE_RECEIVER, receiver);
            values.put(MESSAGE_SENDER, sender);
            values.put(MESSAGE_MESSAGE, message);
            values.put(MESSAGE_PICLINK, piclink);
            values.put(MESSAGE_TGT, TGT);
            rowId = db.insert(TABLE_NAME_MESSAGES, null, values);

        } catch (SQLiteException e){
            Log.e(TAG, "insert()", e);
        } finally {
            Log.d(TAG, "insert(): rowId=" + rowId);
        }

    }

    public void Update(String picname, String TGT){
        SQLiteDatabase db = getWritableDatabase();
        String Update_Query= "UPDATE "+ TABLE_NAME_MESSAGES + " SET " + MESSAGE_TGT + " = '" + TGT + "' WHERE " + MESSAGE_PICLINK + " LIKE '" + picname + "';";
        db.execSQL(Update_Query);
    }

    public Cursor get(String sender, String receiver) {

        SQLiteDatabase db = getWritableDatabase();
        String SELECT_QUERY = "SELECT * FROM " + TABLE_NAME_MESSAGES + " WHERE " + MESSAGE_SENDER + " LIKE '" + sender + "' AND " + MESSAGE_RECEIVER + " LIKE '" + receiver + "' OR " + MESSAGE_SENDER + " LIKE '" + receiver + "' AND " + MESSAGE_RECEIVER + " LIKE '" + sender + "' ORDER BY " + _ID + " ASC";
        //String SELECT_QUERY="";
        return db.rawQuery(SELECT_QUERY,null);

        //return db.query(TABLE_NAME_MESSAGES, null, MESSAGE_SENDER + " LIKE ? OR " + MESSAGE_SENDER + " LIKE ?", sender , null, null, _ID + " ASC");

    }

    public Cursor getTGT(String pic){
        SQLiteDatabase db = getWritableDatabase();
        String Select_Query = "SELECT " + MESSAGE_TGT + " FROM " + TABLE_NAME_MESSAGES + " WHERE " + MESSAGE_PICLINK + " LIKE '" + pic + "' ;";
        return db.rawQuery(Select_Query,null);
    }



}
