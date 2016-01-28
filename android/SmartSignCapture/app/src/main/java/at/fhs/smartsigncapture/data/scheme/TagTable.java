package at.fhs.smartsigncapture.data.scheme;

/**
 * Created by MartinTiefengrabner on 19/08/15.
 */

interface TagTableColumns {
    public static final String ID = "id";
    public static final String TAG = "tag";
}


public class TagTable implements TagTableColumns {
    public static final String TABLE_NAME = "TagTable";

    public static final String SQL_CREATE_STMNT = "CREATE TABLE " + TABLE_NAME + " ("
            + " " + ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + " " + TAG + " TEXT NOT NULL);";


    public static final String SQL_SELECT_BY_ID = ID + " = ? ";

    public static final String SQL_SELECT_BY_TAG = "lower("+TAG+")" + " = lower(?) ";

    public static final String SQL_SELECT_BY_SIGN = ID+" IN (SELECT "+SignTagRelationTable.TAG_ID+" FROM "+SignTagRelationTable.TABLE_NAME+" WHERE "+SignTagRelationTable.SIGN_ID+" = ?)";

    public static final String[] ALL_COLUMNS = {ID, TAG};

    public static final int ID_COLUMN_IDX = 0;
    public static final int TAG_COLUMN_IDX = 1;
    public static final int AUTHOR_COLUMN_IDX = 2;
}
