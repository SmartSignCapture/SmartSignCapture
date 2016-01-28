package at.fhs.smartsigncapture.view.activity;

import android.content.Intent;
import android.os.Bundle;

import com.unity3d.player.UnityPlayer;

/**
 * Created by MartinTiefengrabner on 19/08/15.
 */
public class UnityNewDictionaryEntryActivity extends UnityActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onUnitySceneLoaded() {
        UnityPlayer.UnitySendMessage("App", "StartAsEditor", "");
    }

    private String dataFromUnity = null;

    @Override
    public void onEditorClosed(final String jsonResult) {
        dataFromUnity = jsonResult;

        this.runOnUiThread(new Runnable() {
            public void run() {

                Intent i = new Intent(UnityNewDictionaryEntryActivity.this, SaveDictionaryEntryActivity.class);
                //i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                i.putExtra(SaveDictionaryEntryActivity.EXTRAS_BUNDLE_KEY_SIGN, jsonResult);

                startActivity(i);
                finish();

            }
        });
    }

    @Override
    public void onBackPressed() {
        mUnityPlayer.quit();
        super.onBackPressed();

    }

    public void finish() {
        mUnityPlayer.quit();
        super.finish();

    }
}
