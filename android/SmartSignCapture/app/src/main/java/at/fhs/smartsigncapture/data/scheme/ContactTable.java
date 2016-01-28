package at.fhs.smartsigncapture.data.scheme;

/**
 * Created by MartinTiefengrabner on 13/07/15.
 */

interface ContactTableColumns {
    public static final String GLOBAL_ID = "id";
    public static final String USER_NAME = "userName";
    public static final String FIRST_NAME = "firstName";
    public static final String LAST_NAME = "lastName";
    public static final String IMAGE_URL = "imageUrl";
    public static final String IMAGE_FILE = "imageFile";
    public static final String FRIENDS_SINCE = "friendSince";
    public static final String STATUS = "friendshipStatus";
    public static final String DELETED = "deleted";
}
public class ContactTable implements ContactTableColumns {

    public static final String TABLE_NAME = "ContactTable";

    public static final String SQL_CREATE_STMNT = "CREATE TABLE "+TABLE_NAME+" ("
            +" "+GLOBAL_ID+ " INTEGER NOT NULL,"
            +" "+USER_NAME+ " TEXT NOT NULL,"
            +" "+FIRST_NAME+ " TEXT NOT NULL,"
            +" "+LAST_NAME+ " TEXT NOT NULL,"
            +" "+IMAGE_URL+ " TEXT,"
            +" "+IMAGE_FILE+ " TEXT,"
            +" "+FRIENDS_SINCE+ " TEXT,"
            +" "+STATUS+" INTEGER DEFAULT 0,"
            +" "+DELETED+" INTEGER DEFAULT 0);";

    public static final String SQL_SELECT_NOT_DELETED = DELETED+" = 0";
    public static final String SQL_SELECT_BY_ID = GLOBAL_ID +" = ? ";

    public static final String[] ALL_COLUMNS = {GLOBAL_ID,USER_NAME, FIRST_NAME, LAST_NAME,IMAGE_FILE,IMAGE_URL, STATUS, FRIENDS_SINCE, DELETED};

    public static final int GLOBAL_ID_COLUMN_IDX = 0;
    public static final int USER_NAME_COLUMN_IDX = 1;
    public static final int FIRST_NAME_COLUMN_IDX = 2;
    public static final int LAST_NAME_COLUMN_IDX = 3;
    public static final int IMAGE_FILE_COLUMN_IDX = 4;
    public static final int IMAGE_URL_COLUMN_IDX = 5;
    public static final int STATUS_COLUMN_IDX = 6;
    public static final int FRIENDS_SINCE_COLUMN_IDX = 7;
    public static final int DELETED_COLUMN_IDX = 8;

}
