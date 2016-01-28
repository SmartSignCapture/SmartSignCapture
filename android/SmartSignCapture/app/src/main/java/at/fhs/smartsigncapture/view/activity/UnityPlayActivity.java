package at.fhs.smartsigncapture.view.activity;

import android.os.Bundle;

import com.unity3d.player.UnityPlayer;

/**
 * Created by MartinTiefengrabner on 28/07/15.
 */
public class UnityPlayActivity extends UnityActivity {

    public static String EXTRAS_KEY_SIGN_DATA = "sign";

    private String signData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            signData = extras.getString(EXTRAS_KEY_SIGN_DATA);

        }
    }

    @Override
    public void onUnitySceneLoaded() {
        if (this.signData != null) {
            UnityPlayer.UnitySendMessage("App", "StartAsPlayer", this.signData);
        }
    }

    @Override
    public void onPlayerClosed(){
        this.runOnUiThread(new Runnable() {
            public void run() {
                mUnityPlayer.pause();
                unityViewHolder.removeView(mUnityPlayer.getView());
                onBackPressed();
            }
        });
    }

    @Override
    public void onBackPressed() {
        mUnityPlayer.quit();
        super.onBackPressed();

    }
}
