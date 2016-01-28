package at.fhs.smartsigncapture.data.scheme;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Set;

/**
 * Created by MartinTiefengrabner on 15/07/15.
 */
public class SSCSharedPreferncesHandler {

    public static final String PREFS_NAME = "at.fhs.smartsigncapture";

    public static void cleanSharedPreferences(Context context){
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.clear().commit();
    }

    public static void storeValueForKey(Context context, String key, String value) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(key, value).commit();
    }

    public static void storeValueForKey(Context context, String key, boolean value) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(key, value).commit();
    }

    public static void storeValueForKey(Context context, String key, float value) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putFloat(key, value).commit();
    }

    public static void storeValueForKey(Context context, String key, int value) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(key, value).commit();
    }

    public static void storeValueForKey(Context context, String key, long value) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putLong(key, value).commit();
    }

    public static void storeValueForKey(Context context, String key, Set<String> value) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putStringSet(key, value).commit();
    }

    public static String restoreValue(Context context, String key, String defaultValue) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
        return settings.getString(key, defaultValue);
    }

    public static float restoreValue(Context context, String key, float defaultValue) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
        return settings.getFloat(key, defaultValue);

    }

    public static int restoreValue(Context context, String key, int defaultValue) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
        return settings.getInt(key, defaultValue);
    }

    public static long restoreValue(Context context, String key, long defaultValue) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
        return settings.getLong(key, defaultValue);
    }

    public static Set<String> restoreValue(Context context, String key, Set<String> defaultValue) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
        return settings.getStringSet(key, defaultValue);
    }


}
