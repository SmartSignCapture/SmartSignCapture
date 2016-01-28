package at.fhs.smartsigncapture.data.scheme;

/**
 * Created by MartinTiefengrabner on 19/08/15.
 */

interface SignTagRelationColumns {
    public static final String SIGN_ID = "signID";
    public static final String TAG_ID = "tagID";
}


public class SignTagRelationTable implements SignTagRelationColumns {
    public static final String TABLE_NAME = "SignTagRelationTable";

    public static final String SQL_CREATE_STMNT = "CREATE TABLE " + TABLE_NAME + " ("
            + " " + SIGN_ID + " INTEGER NOT NULL,"
            + " " + TAG_ID + " INTEGER NOT NULL,"
            +" UNIQUE ("+SIGN_ID+","+TAG_ID+"));";


    public static final String SQL_SELECT_BY_SIGN_ID = SIGN_ID + " = ? ";

    public static final String[] ALL_COLUMNS = {SIGN_ID, TAG_ID};

    public static final int SIGN_ID_COLUMN_IDX = 0;
    public static final int TAG__ID_COLUMN_IDX = 1;
}

