package at.fhs.smartsigncapture.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import java.util.HashMap;

/**
 * Created by MartinTiefengrabner on 21/07/15.
 */
public class SDKVersionUtils {
    public static Context context;

    interface FeatureSupportVerifier{
        public boolean isFeatureSupported(Context context);
    }

    private static FeatureSupportVerifier bleSupportVerifier =  new FeatureSupportVerifier() {
        @Override
        public boolean isFeatureSupported(Context context) {
            boolean result = false;

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2 &&
                    context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)){
                result = true;
            }

            return result;
        }
    };

    private static FeatureSupportVerifier systemClockSupportVerifier =  new FeatureSupportVerifier() {
        @Override
        public boolean isFeatureSupported(Context context) {
            return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1;
        }
    };

    private static FeatureSupportVerifier notificationColorsVerifier =  new FeatureSupportVerifier() {
        @Override
        public boolean isFeatureSupported(Context context) {
            return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
        }
    };

    private static final HashMap<String, FeatureSupportVerifier> featuresSupportVerifier;

    public static final String APIFeatureBLE = "apiFeatureBLE";
    public static final String APIFeatureSystemClock = "apiFeatureSystemClock";
    public static final String APIFeatureColoredNotifications = "apiFeatureColoredNotifications";

    static{
        featuresSupportVerifier = new HashMap<String, FeatureSupportVerifier>();
        featuresSupportVerifier.put(APIFeatureBLE, bleSupportVerifier);
        featuresSupportVerifier.put(APIFeatureSystemClock, systemClockSupportVerifier);
        featuresSupportVerifier.put(APIFeatureColoredNotifications, notificationColorsVerifier);
    }

    public static boolean isFeatureSupported(String feature){
        boolean result = false;
        if (featuresSupportVerifier.containsKey(feature)){
            return featuresSupportVerifier.get(feature).isFeatureSupported(context);
        }
        return result;
    }
}
