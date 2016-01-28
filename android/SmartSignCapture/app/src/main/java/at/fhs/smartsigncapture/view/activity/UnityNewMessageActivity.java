package at.fhs.smartsigncapture.view.activity;

import android.os.Bundle;

import com.unity3d.player.UnityPlayer;

import java.util.Date;

import at.fhs.smartsigncapture.Callback;
import at.fhs.smartsigncapture.controller.MessageController;
import at.fhs.smartsigncapture.model.Message;

/**
 * Created by MartinTiefengrabner on 28/07/15.
 */
public class UnityNewMessageActivity extends UnityActivity {

    public static String EXTRAS_KEY_RECEIVER_ID = "receiverID";



    private long receiverID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getIntent().hasExtra(EXTRAS_KEY_RECEIVER_ID)) {
            this.receiverID = getIntent().getLongExtra(EXTRAS_KEY_RECEIVER_ID, -1);
        }
    }

    @Override
    public void onUnitySceneLoaded() {
        if (this.receiverID != -1) {
            UnityPlayer.UnitySendMessage("App", "StartAsEditor", "");
        }
    }

    private String dataFromUnity = null;

    @Override
    public void onEditorClosed(String jsonResult) {
        dataFromUnity = jsonResult;

        this.runOnUiThread(new Runnable() {
            public void run() {
                mUnityPlayer.pause();

                //unityViewHolder.removeView(mUnityPlayer.getView());
                sendMessage(dataFromUnity);
//                Intent i = new Intent(UnityNewMessageActivity.this, SendMessageActivity.class);
//                i.putExtra(SendMessageActivity.EXTRAS_KEY_MESSAGE_BODY, dataFromUnity);
//                i.putExtra(SendMessageActivity.EXTRAS_KEY_RECEIVER_ID, receiverID);
//                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP );
//                startActivity(i);
                //finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        mUnityPlayer.quit();
        super.onBackPressed();

    }

    private void sendMessage(String messageBody) {
        MessageController.getInstance(this).sendMessage(this.receiverID, messageBody, new Date(), new Callback<Message>() {
            @Override
            public void success(Message message) {
                onBackPressed();
            }

            @Override
            public void failure(String errorMessage) {
                onBackPressed();
            }
        });
    }

//    @Override
//    protected void onDestroy() {
//        Intent i = null;
//        if (dataFromUnity != null) {ü
//            i = new Intent(this, SendMessageActivity.class);
//            i.putExtra(SendMessageActivity.EXTRAS_KEY_MESSAGE_BODY, dataFromUnity);
//            i.putExtra(SendMessageActivity.EXTRAS_KEY_RECEIVER_ID, receiverID);
//            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        }
//        super.onDestroy();
//        if (i != null)
//            startActivity(i);
//    }

    public void finish() {

    }
}
