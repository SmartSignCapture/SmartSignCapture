package at.fhs.smartsigncapture.data.API.interfaces;

import java.util.HashMap;
import java.util.List;

import at.fhs.smartsigncapture.model.Friend;
import at.fhs.smartsigncapture.model.User;
import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Query;

/**
 * Created by MartinTiefengrabner on 17/07/15.
 */
public interface SSCClientAPI {
    @GET("/users/")
    void listUsers(Callback<List<User>> cb);

    @GET("/users/")
    void usersWithUsername(@Query("user_name") String userName, Callback<List<User>> cb);

    @GET("/users/")
    void usersWithEmail(@Query("email") String email, Callback<List<User>> cb);

    @GET("/users/")
    void searchUsers(@Query("q") String searchTerm, Callback<List<User>> cb);

    @GET("/users/")
    void searchContacts(@Query("q") String searchTerm, Callback<List<Friend>> cb);

    @POST("/users/")
    void createUser(@Body HashMap user, Callback<User> cb);
}
