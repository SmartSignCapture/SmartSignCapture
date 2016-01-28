package at.fhs.smartsigncapture.utils;

import android.app.LauncherActivity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import java.util.Date;

import at.fhs.smartsigncapture.R;

/**
 * Created by MartinTiefengrabner on 21/07/15.
 */
public class NotificationCenter {

    private static TextToSpeech tts;

    private static int currentNotificationID = 3000;

    public static int showNotification(Context context, String title, String text, int icon, Class<?> intentClass, boolean playSound) {
        Intent resultIntent = new Intent(context, intentClass);
        return showNotification(context, title, text, icon, resultIntent, playSound);

    }

    public static int showNotification(Context context, String title, String text, int icon, Intent intent, boolean playSound) {
        PendingIntent pIntent = PendingIntent.getActivity(context, nextRequestCode(), intent, 0);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        Notification notification = null;

        if (!SDKVersionUtils.isFeatureSupported(SDKVersionUtils.APIFeatureColoredNotifications)) {

            notification = new Notification.Builder(context)
                    .setContentTitle(title)
                    .setContentText(text).setSmallIcon(icon)
                    .setContentIntent(pIntent)
                            //.setColor(context.getResources().getColor(R.color.health_diary_blue))
                    .getNotification();
        } else {
            notification = new Notification.Builder(context)
                    .setContentTitle(title)
                    .setContentText(text).setSmallIcon(icon)
                    .setContentIntent(pIntent)
                    .setColor(context.getResources().getColor(R.color.ripple_material_light))
                    .getNotification();
        }

        notification.flags |= Notification.FLAG_AUTO_CANCEL;

        if (playSound) {
            notification.defaults |= Notification.DEFAULT_VIBRATE;
            notification.defaults |= Notification.DEFAULT_SOUND;
        }
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(++currentNotificationID, notification);

        return currentNotificationID;

    }

    public static void showNotification(Context context, String title, String text, int icon, Class<?> intentClass) {
        NotificationCenter.showNotification(context, title, text, icon, intentClass, false);
    }

    public static void showNotification(Context context, int titleResID, int msgResID, int iconResID, Class<?> intentClass) {

        String title = context.getResources().getString(titleResID);
        String text = context.getResources().getString(msgResID);

        showNotification(context, title, text, iconResID, intentClass);

    }

    public static void showNotification(Context context, int titleResID, String text, int iconResID, Class<?> intentClass) {

        String title = context.getResources().getString(titleResID);

        showNotification(context, title, text, iconResID, intentClass);

    }

    public static void notifyAboutNewlyReceivedDeviceData(Context context, String title, String msg, int icon) {
        speakText(msg, context);
        showNotification(context, title, msg, icon);
    }

    private static void showNotification(Context context, String title, String text, int icon) {
        showNotification(context, title, text, icon, LauncherActivity.class);
    }

    private static void speakText(String text, Context context) {
        final String tmpText = text;
        Log.d("Notif", tmpText);
        if (tts == null) {
            tts = new TextToSpeech(context,
                    new TextToSpeech.OnInitListener() {
                        @Override
                        public void onInit(int status) {
                            if (status != TextToSpeech.ERROR) {
                                Log.d("Notif", "in it");
                                tts.speak(tmpText, TextToSpeech.QUEUE_FLUSH, null);
                            }
                        }
                    });
        } else {
            tts.speak(tmpText, TextToSpeech.QUEUE_FLUSH, null);
        }
    }
    static int requestCode = 1000;

    private static int nextRequestCode(){
        return (int) new Date().getTime();
    }
}
