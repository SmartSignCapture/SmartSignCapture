package at.fhs.smartsigncapture.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import at.fhs.smartsigncapture.data.scheme.SignTable;
import at.fhs.smartsigncapture.data.scheme.SignTagRelationTable;
import at.fhs.smartsigncapture.data.scheme.TagTable;
import at.fhs.smartsigncapture.model.Sign;
import at.fhs.smartsigncapture.model.Tag;

/**
 * Created by MartinTiefengrabner on 19/08/15.
 */
public class SignDAL extends BaseDAL {
    private SSCDatabaseHelper databaseHelper;
    private Context context;

    //region static

    static {
        SSCDatabaseHelper.registerTable(SignTable.TABLE_NAME, SignTable.SQL_CREATE_STMNT);
        SSCDatabaseHelper.registerTable(TagTable.TABLE_NAME, TagTable.SQL_CREATE_STMNT);
        SSCDatabaseHelper.registerTable(SignTagRelationTable.TABLE_NAME, SignTagRelationTable.SQL_CREATE_STMNT);
    }

    //endregion

    //region Constructors

    public SignDAL(Context context) {
        this.context = context;
        this.databaseHelper = SSCDatabaseHelper.getInstance(this.context);
    }

    //endregion

    //region Public

    public boolean storeSign(Sign s) {
        boolean result = false;

        ContentValues values = new ContentValues();

        values.put(SignTable.AUTHOR, s.getAuthor());
        values.put(SignTable.NAME, s.getName());
        values.put(SignTable.SIGN, s.getSign());
        values.put(SignTable.DATE, SSCDatabaseHelper.formatDateTime(s.getDate()));

        long signID = databaseHelper.getWritableDatabase().insert(SignTable.TABLE_NAME, null, values);

        if (signID != -1) {
            for(Tag t : s.getTags()){
                if(t.getId() != -1){
                    this.insertSignTagRelation(signID,t.getId());
                }
                else{
                    long tagID = insertOrSelectTag(t);
                    this.insertSignTagRelation(signID, tagID);
                }
            }
        }

        return true;
    }

    public List<Tag> selectAllTags(){
        List<Tag> result = new ArrayList<>();

        Cursor c = databaseHelper.getReadableDatabase().query(TagTable.TABLE_NAME,TagTable.ALL_COLUMNS,null,null,null,null,TagTable.TAG);

        result = this.fetchTags(c);

        return result;
    }

    public List<Sign> selectAllSigns(){
        List<Sign> result = new ArrayList<>();

        Cursor c = databaseHelper.getReadableDatabase().query(SignTable.TABLE_NAME,SignTable.ALL_COLUMNS,SignTable.SQL_SELECT_ALL_NOT_DELETED,new String[]{},null,null,null);
        result = fetchSigns(c);
        c.close();

        return result;
    }

    private List<Sign> fetchSigns(Cursor c){
        List<Sign> result = new ArrayList<>();

        while(c.moveToNext()){
            Sign s = fetchSignFromCurrentPosition(c);
            if(s != null){
                result.add(s);
            }
        }
        return result;
    }

    private Sign fetchSignFromCurrentPosition(Cursor c){

        Sign result = null;

        long id = c.getLong(SignTable.ID_COLUMN_IDX);
        String name = c.getString(SignTable.NAME_COLUMN_IDX);
        String author = c.getString(SignTable.AUTHOR_COLUMN_IDX);
        String sign = c.getString(SignTable.SIGN_COLUMN_IDX);
        Date date = SSCDatabaseHelper.getDateTime(c, SignTable.DATE_COLUMN_IDX);

        List<Tag> tags = tagsOfSign(id);

        result = new Sign(id,name,author,date,sign,tags);

        return result;

    }

    private List<Tag> tagsOfSign(long signID){
        List<Tag> result = new ArrayList<>();

        Cursor c = databaseHelper.getReadableDatabase().query(TagTable.TABLE_NAME, TagTable.ALL_COLUMNS,TagTable.SQL_SELECT_BY_SIGN,new String[]{String.valueOf(signID)},null,null,TagTable.TAG);

        result = this.fetchTags(c);

        c.close();

        return result;
    }

    //endregion

    //region Private

    private List<Tag> fetchTags(Cursor cursor){
        List<Tag> result = new ArrayList<>();

        while(cursor.moveToNext()){
            result.add(fetchTagFromCurrentCursorPosition(cursor));
        }

        return result;
    }

    private Tag fetchTagFromCurrentCursorPosition(Cursor cursor){
        Tag result = null;

        long id = cursor.getLong(0);
        String tag =  cursor.getString(1);

        result = new Tag(id,tag);

        return result;
    }

    private long insertOrSelectTag(Tag tag) {
        long tagID = -1;

        Cursor c = this.databaseHelper.getReadableDatabase().query(
                TagTable.TABLE_NAME,
                new String[]{TagTable.ID},
                TagTable.SQL_SELECT_BY_TAG,
                new String[]{tag.getTag()},
                null, null, null);

        if(c.moveToFirst()){
            tagID = c.getLong(0);
        }
        else{
            if(insertTag(tag)){
                tagID = tag.getId();
            }
        }

        return tagID;
    }

    private boolean insertTag(Tag tag){
        boolean result = false;

        ContentValues values = new ContentValues();

        values.put(TagTable.TAG,tag.getTag());

        long id = databaseHelper.getWritableDatabase().insert(TagTable.TABLE_NAME,null, values);

        tag.setID(id);

        result = id != -1;

        return result;
    }

    private boolean insertSignTagRelation(long signID, long tagID) {
        boolean result;

        ContentValues values = new ContentValues();
        values.put(SignTagRelationTable.SIGN_ID, signID);
        values.put(SignTagRelationTable.TAG_ID, tagID);

        result = this.databaseHelper.getWritableDatabase().insertWithOnConflict(SignTagRelationTable.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_IGNORE) != -1;

        return result;
    }

    //endregion
}
