package at.fhs.smartsigncapture.data.scheme;

/**
 * Created by MartinTiefengrabner on 19/08/15.
 */

interface SignTableColumns {
    public static final String ID = "id";
    public static final String DATE = "date";
    public static final String AUTHOR = "author";
    public static final String NAME = "name";
    public static final String SIGN = "message";
    public static final String TRANSMITTED = "transmitted";
    public static final String DELETED = "deleted";
}


public class SignTable implements SignTableColumns {
    public static final String TABLE_NAME = "SignTable";

    public static final String SQL_CREATE_STMNT = "CREATE TABLE " + TABLE_NAME + " ("
            + " " + ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + " " + DATE + " TEXT NOT NULL, "
            + " " + AUTHOR + " TEXT NOT NULL,"
            + " " + NAME + " TEXT NOT NULL,"
            + " " + SIGN + " TEXT NOT NULL,"
            + " " + DELETED + " INTEGER NOT NULL DEFAULT 0,"
            + " " + TRANSMITTED + " INTEGER NOT NULL DEFAULT 0);";


    public static final String SQL_SELECT_BY_ID = ID + " = ? ";

    public static final String SQL_SELECT_ALL_NOT_DELETED = DELETED + " = 0 ";

    public static final String[] ALL_COLUMNS = {ID, DATE, AUTHOR, NAME, SIGN, DELETED, TRANSMITTED};

    public static final int ID_COLUMN_IDX = 0;
    public static final int DATE_COLUMN_IDX = 1;
    public static final int AUTHOR_COLUMN_IDX = 2;
    public static final int NAME_COLUMN_IDX = 3;
    public static final int SIGN_COLUMN_IDX = 4;
    public static final int DELETED_COLUMN_IDX = 5;
    public static final int TRANSMITTED_COLUMN_IDX = 6;
}