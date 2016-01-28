package at.fhs.smartsigncapture.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import at.fhs.smartsigncapture.data.scheme.MessageTable;
import at.fhs.smartsigncapture.model.Message;

/**
 * Created by MartinTiefengrabner on 24/07/15.
 */
public class MessageDAL extends BaseDAL {
    private SSCDatabaseHelper databaseHelper;
    private Context context;

    //region static

    static {
        SSCDatabaseHelper.registerTable(MessageTable.TABLE_NAME, MessageTable.SQL_CREATE_STMNT);
    }

    //endregion

    //region Constructors

    public MessageDAL(Context context) {
        this.context = context;
        this.databaseHelper = SSCDatabaseHelper.getInstance(this.context);
    }

    //endregion

    //region Public

    public boolean saveMessage(Message message, long currentUserID){
        boolean result = false;
        ContentValues values = new ContentValues();

        boolean received = true;
        long counterpartID = message.getSenderID();

        if(currentUserID != message.getReceiverID()){
            received = false;
            counterpartID = message.getReceiverID();
        }


        Log.d("Sender:", counterpartID+" - rec "+received);

        values.put(MessageTable.GLOBAL_ID, message.getId());
        values.put(MessageTable.MESSAGE, message.getMessage());
        values.put(MessageTable.COUNTERPART_ID, counterpartID);
        values.put(MessageTable.TRANSMITTED, 1);
        values.put(MessageTable.RECEIVED, received ? 1 : 0);
        values.put(MessageTable.DATE, SSCDatabaseHelper.formatDateTime(message.getDate()));

        result = this.databaseHelper.getWritableDatabase().insert(MessageTable.TABLE_NAME,null,values) != 1;

        return result;
    }

    public Message loadMessage(long messageID,long currentUserID){
        Message result = null;

        Cursor c = this.databaseHelper.getReadableDatabase().query(MessageTable.TABLE_NAME,MessageTable.ALL_COLUMNS,MessageTable.SQL_SELECT_BY_ID,new String[]{String.valueOf(messageID)},null,null,null);

        if(c.moveToFirst()){
            result = this.fetchMessageFromCurrentCursorPosition(c,currentUserID);
        }

        c.close();

        return result;
    }

    public List<Message> newestMessagesPerUser(long currentUserID){
        List<Message> result = new ArrayList<Message>();

        String[]columns = Arrays.copyOf(MessageTable.ALL_COLUMNS, MessageTable.ALL_COLUMNS.length + 1);
        columns[MessageTable.ALL_COLUMNS.length] ="MAX("+MessageTable.DATE+")";

        Cursor c = this.databaseHelper.getReadableDatabase().query(MessageTable.TABLE_NAME,columns,null,null,MessageTable.COUNTERPART_ID,null,MessageTable.DATE+" DESC");

        result = fetchMessages(c,currentUserID);

        c.close();

        return result;
    }

    public List<Message> selectConversationWithUser(long counterPartID, long currentUserID){
        List<Message> result = new ArrayList<Message>();

        Cursor c = this.databaseHelper.getReadableDatabase().query(
                MessageTable.TABLE_NAME,
                MessageTable.ALL_COLUMNS,
                MessageTable.SQL_SELECT_BY_COUNTERPART,
                new String[]{String.valueOf(counterPartID)},
                null,
                null,
                MessageTable.DATE+" DESC");

        result = fetchMessages(c,currentUserID);

        c.close();

        return result;
    }


    private List<Message> fetchMessages(Cursor c,long currentUserID){
        List<Message> result = new ArrayList<>();

        while(c.moveToNext()){
            Message msg =  this.fetchMessageFromCurrentCursorPosition(c,currentUserID);

            result.add(msg);
        }

        return result;
    }

    private Message fetchMessageFromCurrentCursorPosition(Cursor cursor,long currentUserID){
        Message result = null;
        long id = cursor.getLong(MessageTable.GLOBAL_ID_COLUMN_IDX);
        Date date = SSCDatabaseHelper.getDateTime(cursor, MessageTable.DATE_COLUMN_IDX);
        String message = cursor.getString(MessageTable.MESSAGECOLUMN_IDX);
        long counterpart = cursor.getLong(MessageTable.COUNTERPART_COLUMN_IDX);

        if(cursor.getInt(MessageTable.RECEIVED_COLUMN_IDX) == 1){
            result = new Message(id,counterpart,currentUserID,date,message,true);
        }
        else{
            result = new Message(id,currentUserID,counterpart,date,message,false);
        }


        return result;

    }
}
