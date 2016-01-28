package at.fhs.smartsigncapture.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import at.fhs.smartsigncapture.data.scheme.ContactTable;
import at.fhs.smartsigncapture.model.Friend;

/**
 * Created by MartinTiefengrabner on 13/07/15.
 */
public class ContactDAL extends BaseDAL {

    private SSCDatabaseHelper databaseHelper;
    private Context context;

    //region static

    static {
        SSCDatabaseHelper.registerTable(ContactTable.TABLE_NAME, ContactTable.SQL_CREATE_STMNT);
    }

    //endregion

    //region Constructors

    public ContactDAL(Context context) {
        this.context = context;
        this.databaseHelper = SSCDatabaseHelper.getInstance(this.context);
    }

    //endregion

    //region Public

    public List<Friend> selectAllContacts() {
        List<Friend> result = new ArrayList<Friend>();

        Cursor c = this.databaseHelper.getReadableDatabase().query(ContactTable.TABLE_NAME, ContactTable.ALL_COLUMNS, ContactTable.SQL_SELECT_NOT_DELETED, null, null, null, ContactTable.USER_NAME);

        result = this.fetchContactsFromCursor(c);

        c.close();

        return result;
    }

    public boolean insertOrUpdateContact(Friend c) {
        boolean result = false;

        if(this.selectContact(c.getId())==null){
            result = this.insertContact(c);
        }
        else{
            result = this.updateContact(c);
        }

        return result;
    }

    public boolean updateContact(Friend c) {
        boolean result = false;

        result = databaseHelper.getWritableDatabase().update(ContactTable.TABLE_NAME,
                this.createContentValues(c),
                ContactTable.SQL_SELECT_BY_ID,
                new String[]{String.valueOf(c.getId())}) == 1;

        if(result){
            c = selectContact(c.getId());
        }

        return result;
    }

    public boolean insertContact(Friend c) {
        boolean result = false;

        result = databaseHelper.getWritableDatabase().insert(ContactTable.TABLE_NAME,null,this.createContentValues(c)) != -1;

        return result;
    }

    public Friend selectContact(long contactID) {
        Friend result = null;

        Cursor c = this.databaseHelper.getReadableDatabase().query(ContactTable.TABLE_NAME,
                ContactTable.ALL_COLUMNS,
                ContactTable.SQL_SELECT_BY_ID,
                new String[]{String.valueOf(contactID)},
                null, null, null);

        if (c.moveToFirst()) {
            result = this.fetchContactFromCurrentCursorPosition(c);
        }

        c.close();

        return result;
    }

    //endregion

    //region Private

    private ContentValues createContentValues(Friend friend) {
        ContentValues values = new ContentValues();

        values.put(ContactTable.GLOBAL_ID, friend.getId());
        values.put(ContactTable.FIRST_NAME, friend.getFirstName());
        values.put(ContactTable.LAST_NAME, friend.getLastName());
        values.put(ContactTable.USER_NAME, friend.getUserName());
        values.put(ContactTable.IMAGE_URL, friend.getImageURL());
        values.put(ContactTable.IMAGE_FILE, friend.getLocalImage());
        values.put(ContactTable.STATUS, friend.getState().ordinal());
        values.put(ContactTable.FRIENDS_SINCE, SSCDatabaseHelper.formatDateTime(friend.getFriendsSince()));

        return values;
    }

    private List<Friend> fetchContactsFromCursor(Cursor c) {

        List<Friend> result = new ArrayList<Friend>();

        while (c.moveToNext()) {
            Friend newFriend = this.fetchContactFromCurrentCursorPosition(c);

            if (newFriend != null) {
                result.add(newFriend);
            }
        }

        return result;
    }

    private Friend fetchContactFromCurrentCursorPosition(Cursor c) {

        Friend result = null;

        long id = c.getLong(ContactTable.GLOBAL_ID_COLUMN_IDX);
        String userName = c.getString(ContactTable.USER_NAME_COLUMN_IDX);
        String firstName = c.getString(ContactTable.FIRST_NAME_COLUMN_IDX);
        String lastName = c.getString(ContactTable.LAST_NAME_COLUMN_IDX);
        String imageUrl = c.getString(ContactTable.IMAGE_URL_COLUMN_IDX);
        String imageFile = c.getString(ContactTable.IMAGE_FILE_COLUMN_IDX);
        Friend.FriendshipState state = Friend.FriendshipState.values()[c.getInt(ContactTable.STATUS_COLUMN_IDX)];
        Date friendsSince = SSCDatabaseHelper.getDateTime(c, ContactTable.FRIENDS_SINCE_COLUMN_IDX);

        result = new Friend(id, userName, firstName, lastName, imageUrl, imageFile, state, friendsSince);

        return result;
    }

    //endregion


}
