package at.fhs.smartsigncapture.data.GCM;

import android.os.Bundle;

import com.google.android.gms.gcm.GcmListenerService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by MartinTiefengrabner on 21/07/15.
 */
public class SSCGCMListenerService extends GcmListenerService {

    private static final String TAG = "GCM_MSG";

    private static HashMap<String, List<GCMMessageReceiver>> gcmMessageReceiver = new HashMap<>();

    public interface  GCMMessageReceiver{
        public String getGCMKey();
        public void receivedMessage(JSONObject data);
    }

    public static void registerMessageReceiver(GCMMessageReceiver receiver){
        if(gcmMessageReceiver.get(receiver.getGCMKey()) == null){
            gcmMessageReceiver.put(receiver.getGCMKey(), new ArrayList<GCMMessageReceiver>());
        }

        gcmMessageReceiver.get(receiver.getGCMKey()).add(receiver);
    }

    public static void unregisterMessageReceiver(GCMMessageReceiver receiver){
        if(gcmMessageReceiver.get(receiver.getGCMKey()) != null) {
            gcmMessageReceiver.get(receiver.getGCMKey()).add(receiver);
        }
    }

    @Override
    public void onMessageReceived(String from, Bundle data) {
        String key = data.getString("key");
        String bodyString = data.getString("body");
        try {
            JSONObject message = new JSONObject(bodyString);
            distributeMessage(key, message);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void distributeMessage(String key, JSONObject body){
        for(GCMMessageReceiver receiver : gcmMessageReceiver.get(key)){
            receiver.receivedMessage(body);
        }
    }
}
