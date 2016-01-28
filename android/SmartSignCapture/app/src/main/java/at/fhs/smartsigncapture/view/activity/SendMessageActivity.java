package at.fhs.smartsigncapture.view.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import java.util.Date;

import at.fhs.smartsigncapture.Callback;
import at.fhs.smartsigncapture.R;
import at.fhs.smartsigncapture.controller.MessageController;
import at.fhs.smartsigncapture.model.Message;

public class SendMessageActivity extends ActionBarActivity {

    public static String EXTRAS_KEY_RECEIVER_ID = "receiverID";
    public static String EXTRAS_KEY_MESSAGE_BODY = "messageBody";

    private MessageController messageController;

    private long receiverID;
    private String messageBody;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_message);

        this.messageController = MessageController.getInstance(this);

        if (getIntent().hasExtra(EXTRAS_KEY_RECEIVER_ID) && getIntent().hasExtra(EXTRAS_KEY_MESSAGE_BODY)) {
            this.receiverID = getIntent().getLongExtra(EXTRAS_KEY_RECEIVER_ID, -1);
            this.messageBody = getIntent().getStringExtra(EXTRAS_KEY_MESSAGE_BODY);

            if(this.receiverID != -1 && this.messageBody != null){
                sendMessage();
            }
        }
    }

    private void sendMessage() {
        messageController.sendMessage(this.receiverID, this.messageBody, new Date(), new Callback<Message>() {
            @Override
            public void success(Message message) {

            }

            @Override
            public void failure(String errorMessage) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_send_message, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
