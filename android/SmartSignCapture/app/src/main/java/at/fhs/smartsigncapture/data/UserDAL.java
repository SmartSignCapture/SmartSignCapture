package at.fhs.smartsigncapture.data;

import android.content.Context;

import at.fhs.smartsigncapture.data.scheme.SSCSharedPreferncesHandler;
import at.fhs.smartsigncapture.model.User;

/**
 * Created by MartinTiefengrabner on 15/07/15.
 */
public class UserDAL extends BaseDAL {

    private static final String SHARED_PREF_KEY_USER_ID = "user.id";
    private static final String SHARED_PREF_KEY_USER_NAME = "user.userName";
    private static final String SHARED_PREF_KEY_FIRST_NAME = "user.firstName";
    private static final String SHARED_PREF_KEY_LAST_NAME = "user.lastName";
    private static final String SHARED_PREF_KEY_EMAIL = "user.email";
    private static final String SHARED_PREF_KEY_PASSWORD = "user.password";
    private static final String SHARED_PREF_KEY_IMAGE_URL = "user.imageUrl";
    private static final String SHARED_PREF_KEY_LOCAL_IMAGE_PATH = "user.localImage";

    private Context context;



    public UserDAL(Context context) {
        this.context = context;
    }

    public boolean saveUser(User user, String email, String password) {
        boolean result = false;

        SSCSharedPreferncesHandler.storeValueForKey(context, SHARED_PREF_KEY_USER_ID, user.getId());
        SSCSharedPreferncesHandler.storeValueForKey(context, SHARED_PREF_KEY_USER_NAME, user.getUserName());
        SSCSharedPreferncesHandler.storeValueForKey(context, SHARED_PREF_KEY_FIRST_NAME, user.getFirstName());
        SSCSharedPreferncesHandler.storeValueForKey(context, SHARED_PREF_KEY_LAST_NAME, user.getLastName());
        SSCSharedPreferncesHandler.storeValueForKey(context, SHARED_PREF_KEY_EMAIL, email);
        SSCSharedPreferncesHandler.storeValueForKey(context, SHARED_PREF_KEY_PASSWORD, password);
        SSCSharedPreferncesHandler.storeValueForKey(context, SHARED_PREF_KEY_IMAGE_URL, user.getImageURL());
        SSCSharedPreferncesHandler.storeValueForKey(context, SHARED_PREF_KEY_LOCAL_IMAGE_PATH, user.getLocalImage());

        return result;
    }

    public User restoreUser() {
        User result = null;

        long id = SSCSharedPreferncesHandler.restoreValue(context, SHARED_PREF_KEY_USER_ID, -1l);

        if (id != -1) {

            String userName = SSCSharedPreferncesHandler.restoreValue(context, SHARED_PREF_KEY_USER_NAME, "");
            String firstName = SSCSharedPreferncesHandler.restoreValue(context, SHARED_PREF_KEY_FIRST_NAME, "");
            String imageUrl = SSCSharedPreferncesHandler.restoreValue(context, SHARED_PREF_KEY_IMAGE_URL, "");
            String imagePath = SSCSharedPreferncesHandler.restoreValue(context, SHARED_PREF_KEY_LOCAL_IMAGE_PATH, "");

            String lastName = SSCSharedPreferncesHandler.restoreValue(context, SHARED_PREF_KEY_LAST_NAME, "");

            result = new User(id, userName, firstName, lastName, imageUrl, imagePath);
        }

        return result;
    }

    public String getUserPassword(){
return  SSCSharedPreferncesHandler.restoreValue(context, SHARED_PREF_KEY_PASSWORD, "");
    }

    public String getUserEmail(){
return  SSCSharedPreferncesHandler.restoreValue(context, SHARED_PREF_KEY_EMAIL, "");
    }
}
