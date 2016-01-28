package at.fhs.smartsigncapture.data.GCM;

import android.content.Intent;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.google.android.gms.iid.InstanceIDListenerService;

import java.io.IOException;

import at.fhs.smartsigncapture.R;

/**
 * Created by MartinTiefengrabner on 21/07/15.
 */
public class SSCInstanceIDListenerService extends InstanceIDListenerService {

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if(!GCMController.hasToken(this)) {
            new Thread() {
                @Override
                public void run() {
                    super.run();
                    InstanceID instanceID = InstanceID.getInstance(SSCInstanceIDListenerService.this);
                    try {
                        String token = instanceID.getToken(getString(R.string.gcm_defaultSenderId), GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
                        GCMController.storeToken(SSCInstanceIDListenerService.this, token);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }.start();
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onTokenRefresh() {
       // Intent intent = new Intent(this, RegistrationIntentService.class);
       // startService(intent);
    }
}
