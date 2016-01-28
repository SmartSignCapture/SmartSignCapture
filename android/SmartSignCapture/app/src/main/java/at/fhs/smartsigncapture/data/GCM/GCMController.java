package at.fhs.smartsigncapture.data.GCM;

import android.content.Context;

import at.fhs.smartsigncapture.data.scheme.SSCSharedPreferncesHandler;

/**
 * Created by MartinTiefengrabner on 21/07/15.
 */
public class GCMController {

    private static String SHARED_PREFS_KEY_GCM_TOKEN = "at.fhs.ssc.gcmtoken";

    private static String GCM_TOKEN = null;


    public static String getToken(Context context){
        if(GCM_TOKEN == null) {
            GCM_TOKEN = SSCSharedPreferncesHandler.restoreValue(context, SHARED_PREFS_KEY_GCM_TOKEN, GCM_TOKEN);
        }
        return GCM_TOKEN;
    }

    public static boolean hasToken(Context context){
        return getToken(context) != null;
    }

    public static void storeToken(Context context, String token){
        GCM_TOKEN = token;
        SSCSharedPreferncesHandler.storeValueForKey(context, SHARED_PREFS_KEY_GCM_TOKEN, token);
    }

}
