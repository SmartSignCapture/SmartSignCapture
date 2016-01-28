package at.fhs.smartsigncapture.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import at.fhs.smartsigncapture.R;
import at.fhs.smartsigncapture.controller.ContactsController;
import at.fhs.smartsigncapture.controller.MessageController;
import at.fhs.smartsigncapture.model.Contact;
import at.fhs.smartsigncapture.model.Message;
import at.fhs.smartsigncapture.view.adapter.MessageThreadListAdapter;

public class MessageThreadActivity extends ActionBarActivity implements MessageController.OnNewMessageReceivedListener {

    public final static String EXTRAS_BUNDLE_KEY_CONTACT_ID = "contactID";

    private MessageController messageController;

    private ContactsController contactsController;

    private ListView messagesListView;

    private MessageThreadListAdapter threadListAdapter;

    private long counterpartID = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_thread);

        if (getIntent().hasExtra(EXTRAS_BUNDLE_KEY_CONTACT_ID)) {
            this.counterpartID = getIntent().getLongExtra(EXTRAS_BUNDLE_KEY_CONTACT_ID, -1);
        }

        initActivity();
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.threadListAdapter.setMessages(getMessagesOfConversation());
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

    @Override
    protected void onDestroy() {
        MessageController.removeOnNewMessageReceivedListener(this);
        super.onDestroy();
    }

    private void initActivity() {
        this.messageController = MessageController.getInstance(this);
        this.contactsController = ContactsController.getInstance(this);

        Contact counterpart =  this.contactsController.getContact(this.counterpartID);

        if(counterpart != null){
            this.setTitle(counterpart.getUserName());
        }
        else{
            this.setTitle("");
        }

        this.messagesListView = (ListView) findViewById(R.id.lvMessages);

        threadListAdapter = new MessageThreadListAdapter(this, getMessagesOfConversation());

        this.messagesListView.setAdapter(threadListAdapter);

        this.messagesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                showMessage((Message) threadListAdapter.getItem(position));
            }
        });

        findViewById(R.id.fabNewMessage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MessageThreadActivity.this, UnityNewMessageActivity.class);
                i.putExtra(UnityNewMessageActivity.EXTRAS_KEY_RECEIVER_ID, counterpartID);

                startActivity(i);
            }
        });

        MessageController.addOnNewMessageReceivedListener(this);
    }

    private void showMessage(Message msg){
//        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
//        Fragment prev = getSupportFragmentManager().findFragmentByTag("dialog");
//        if (prev != null) {
//            ft.remove(prev);
//        }
//
//        // Create and show the dialog.
//        UnityPlayMessageFragment newFragment = UnityPlayMessageFragment.newInstance();
//
//        newFragment.show(ft, "dialog");

        Intent i = new Intent(this, UnityPlayActivity.class);
        i.putExtra(UnityPlayActivity.EXTRAS_KEY_SIGN_DATA, msg.getMessage());

        startActivity(i);
    }

    private List<Message> getMessagesOfConversation(){
        List<Message> messages = this.messageController.getConversation(counterpartID);
        Collections.sort(messages, new Comparator<Message>() {
            @Override
            public int compare(Message lhs, Message rhs) {
                return lhs.getDate().compareTo(rhs.getDate());
            }
        });

        return messages;

    }

    @Override
    public void newMessageReceived(Message message) {
        if(this.counterpartID == message.getReceiverID() || this.counterpartID == message.getSenderID()){
            this.threadListAdapter.setMessages(this.getMessagesOfConversation());
        }
    }
}
