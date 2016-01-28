package at.fhs.smartsigncapture.data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import at.fhs.smartsigncapture.Constants;
import at.fhs.smartsigncapture.data.scheme.SignTable;
import at.fhs.smartsigncapture.data.scheme.SignTagRelationTable;
import at.fhs.smartsigncapture.data.scheme.TagTable;

/**
 * Created by MartinTiefengrabner on 13/07/15.
 */
public class SSCDatabaseHelper extends SQLiteOpenHelper {

    //region Statics

    private static SSCDatabaseHelper instance;

    private static HashMap<String,String> tablesAndCreateStatements = new HashMap<String, String>();

    private static SimpleDateFormat SQL_DATE_TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    //endregion

    //region Attributes

    private Context context;

    //endregion


    //region Static

    static{
        SSCDatabaseHelper.registerTable(SignTable.TABLE_NAME, SignTable.SQL_CREATE_STMNT);
        SSCDatabaseHelper.registerTable(TagTable.TABLE_NAME, TagTable.SQL_CREATE_STMNT);
        SSCDatabaseHelper.registerTable(SignTagRelationTable.TABLE_NAME, SignTagRelationTable.SQL_CREATE_STMNT);
    }

    public static  SSCDatabaseHelper getInstance(Context context){
        if(instance == null){
            instance = new SSCDatabaseHelper(context);
        }
        return instance;
    }

    public static void registerTable(String tableName, String createStatement){
        tablesAndCreateStatements.put(tableName,createStatement);
    }

    public static Date getDateTime(Cursor c, int dateColumnIdx){

        Date result = null;

        String dateString = c.getString(dateColumnIdx);

        if(dateString != null) {
            try {
                result = SQL_DATE_TIME_FORMAT.parse(dateString);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        return result;
    }

    public static String formatDateTime(Date date){

        String result = null;

        if(date != null)
            result = SQL_DATE_TIME_FORMAT.format(date);

        return result;
    }

    //endregion

    //region Constructor

    private SSCDatabaseHelper(Context context){
        super(context, Constants.DATABASE_NAME,null,Constants.DATABASE_VERSION);

        this.context = context;
    }

    //endregion


    //region Override

    @Override
    public void onCreate(SQLiteDatabase db) {
        for(String statement : tablesAndCreateStatements.values()){
            db.execSQL(statement);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        for(String tableName : tablesAndCreateStatements.keySet()) {
            db.execSQL("DROP TABLE IF EXISTS " + tableName);
            db.execSQL(tablesAndCreateStatements.get(tableName));
        }
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
//        for(String tableName : tablesAndCreateStatements.keySet()) {
//            db.execSQL("DROP TABLE IF EXISTS " + tableName);
//            db.execSQL(tablesAndCreateStatements.get(tableName));
//        }
    }

    //endregion
}
