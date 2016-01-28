package at.fhs.smartsigncapture.data.scheme;

/**
 * Created by MartinTiefengrabner on 24/07/15.
 */

interface MessageTableColumns {
    public static final String GLOBAL_ID = "globalID";
    public static final String COUNTERPART_ID = "counterpartID";
    public static final String DATE = "date";
    public static final String MESSAGE = "message";
    public static final String RECEIVED = "received";
    public static final String TRANSMITTED = "transmitted";
}


public class MessageTable implements MessageTableColumns {
    public static final String TABLE_NAME = "MessageTable";

    public static final String SQL_CREATE_STMNT = "CREATE TABLE " + TABLE_NAME + " ("
            + " " + GLOBAL_ID + " INTEGER NOT NULL,"
            + " " + COUNTERPART_ID + " INTEGER NOT NULL,"
            + " " + DATE + " TEXT NOT NULL, "
            + " " + MESSAGE + " TEXT NOT NULL,"
            + " " + RECEIVED + " INTEGER NOT NULL DEFAULT 0,"

            + " " + TRANSMITTED + " INTEGER NOT NULL DEFAULT 0);";


    public static final String SQL_SELECT_BY_ID = GLOBAL_ID + " = ? ";

    public static final String SQL_SELECT_BY_COUNTERPART = COUNTERPART_ID + " = ? ";

    public static final String[] ALL_COLUMNS = {GLOBAL_ID, COUNTERPART_ID, DATE, MESSAGE, RECEIVED, TRANSMITTED};

    public static final int GLOBAL_ID_COLUMN_IDX = 0;
    public static final int COUNTERPART_COLUMN_IDX = 1;
    public static final int DATE_COLUMN_IDX = 2;
    public static final int MESSAGECOLUMN_IDX = 3;
    public static final int RECEIVED_COLUMN_IDX = 4;
    public static final int TRANSMITTED_COLUMN_IDX = 5;

}
