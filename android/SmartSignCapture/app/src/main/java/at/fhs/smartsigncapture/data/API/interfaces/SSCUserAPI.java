package at.fhs.smartsigncapture.data.API.interfaces;

import java.util.List;

import at.fhs.smartsigncapture.model.Friend;
import at.fhs.smartsigncapture.model.Message;
import at.fhs.smartsigncapture.model.Sign;
import at.fhs.smartsigncapture.model.User;
import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;

/**
 * Created by MartinTiefengrabner on 15/07/15.
 */


public interface SSCUserAPI {
    @GET("/users/{id}")
    void getUser(@Path("id") int userID, Callback<User> cb);

    @POST("/users/{id}/gcm-token/{token}/")
    void addGCMToken(@Path("id") long userID, @Path("token") String gcmToken, Callback<Void> cb);

    @POST("/users/{id}/friend-request/{friendID}/")
    void sendFriendRequest(@Path("id") long userID, @Path("friendID") long friendID, Callback<Void> cb);

    @POST("/users/{id}/send-message/{receiverID}/")
    void sendMessage(@Path("id") long userID, @Path("receiverID") long receiverID, @Body Message message, Callback<Message> cb);

    @POST("/users/{id}/friendship-accepted/{friendID}/")
    void acceptFriendship(@Path("id") long userID, @Path("friendID") long friendID, Callback<Void> cb);

    @GET("/users/{id}/friend/{friendID}/")
    void getFriendInfo(@Path("id") long userID, @Path("friendID") long friendID, Callback<Friend> cb);

    @GET("/users/{id}/messages/received/{messageID}/")
    void getReceivedMessage(@Path("id") long userID, @Path("messageID") long messageID, Callback<Message> cb);

    @GET("/users/{id}/messages/{messageID}")
    void getMessage(@Path("id") long userID, @Path("messageID") long messageID, Callback<Message> cb);

    @GET("/users/{id}/messages/?fields=id")
    void getMessageIDs(@Path("id") long userID, Callback<List<Message>> cb);

    @GET("/users/{id}/friends/")
    void getFriends(@Path("id") long userID, Callback<Response> cb);

    @POST("/users/{id}/signs/")
    void postSign(@Path("id") long userID, @Body Sign sign, Callback<Response> cb);
}
