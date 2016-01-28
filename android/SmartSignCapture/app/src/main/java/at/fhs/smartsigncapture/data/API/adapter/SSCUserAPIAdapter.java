package at.fhs.smartsigncapture.data.API.adapter;

import android.content.Context;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import at.fhs.smartsigncapture.data.API.APIConstants;
import at.fhs.smartsigncapture.data.API.AccessToken;
import at.fhs.smartsigncapture.data.API.interfaces.SSCUserAPI;
import at.fhs.smartsigncapture.data.GCM.GCMController;
import at.fhs.smartsigncapture.model.Friend;
import at.fhs.smartsigncapture.model.User;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.OkClient;
import retrofit.client.Response;
import retrofit.converter.GsonConverter;
import retrofit.mime.TypedByteArray;

/**
 * Created by MartinTiefengrabner on 15/07/15.
 */
public class SSCUserAPIAdapter {

    public static String ERROR_MESSAGE_USER_LOGIN_WRONG_CREDENTIALS = "wrongCredentials";

    public static SSCUserAPI APIInterface;

    private static GsonConverter converter;

    private static AccessToken token;

    private static String email;

    private static String password;

    private static SimpleDateFormat jsonDateTimeFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    static {
        initAPI();
    }

    public static void initAPI() {


        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd HH:mm:ss")
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create();

        converter = new GsonConverter(gson);

        OkHttpClient okHttpClient = new OkHttpClient();

        okHttpClient.interceptors().add(new Interceptor() {
            @Override
            public com.squareup.okhttp.Response intercept(Chain chain) throws IOException {
                Request request = chain.request();

                int responseCode = HttpURLConnection.HTTP_UNAUTHORIZED;

                com.squareup.okhttp.Response response = null;

                if (token != null && token.getAccessToken() != null) {
                    request = request.newBuilder()
                            .header("Accept", "application/json")
                            .header("Authorization", "Bearer " + token.getAccessToken())
                            .build();
                    response = chain.proceed(request);
                    responseCode = response.code();
                }

                if (responseCode == HttpURLConnection.HTTP_UNAUTHORIZED) {

                    token = SSCLoginAPIAdapter.requestUserToken(email, password);

                    Request newRequest = request.newBuilder()
                            .header("Accept", "application/json")
                            .header("Authorization", "Bearer " + token.getAccessToken())
                            .build();

                    response = chain.proceed(newRequest);

                }

                return response;
            }
        });

        RestAdapter.Builder builder = new RestAdapter.Builder();
        builder.setLogLevel(RestAdapter.LogLevel.FULL)
                .setEndpoint(APIConstants.SSC_API_ENDPOINT)
                .setConverter(converter)
                .setClient(new OkClient(okHttpClient));

        RestAdapter restAdapter = builder.build();
        APIInterface = restAdapter.create(SSCUserAPI.class);
    }

    public static void loginUser(final Context context, final String email, final String password, final at.fhs.smartsigncapture.Callback<User> callback) {
        SSCLoginAPIAdapter.APIInterface.login(email, password, "password", new Callback<AccessToken>() {
            @Override
            public void success(AccessToken accessToken, Response response) {
                token = accessToken;
                SSCClientAPIAdapter.APIInterface.usersWithEmail(email, new Callback<List<User>>() {
                    @Override
                    public void success(List<User> users, Response response) {
                        SSCUserAPIAdapter.password = password;
                        SSCUserAPIAdapter.email = email;

                        final User user = users.get(0);

                        APIInterface.addGCMToken(user.getId(), GCMController.getToken(context), new retrofit.Callback<Void>() {
                            @Override
                            public void success(Void aVoid, Response response) {
                                callback.success(user);
                            }

                            @Override
                            public void failure(RetrofitError error) {
                                callback.failure(error.getMessage());
                            }
                        });

                    }

                    @Override
                    public void failure(RetrofitError error) {
                        callback.failure("");
                    }
                });
            }

            @Override
            public void failure(RetrofitError error) {
                callback.failure(ERROR_MESSAGE_USER_LOGIN_WRONG_CREDENTIALS);
            }
        });
    }

    public static HashMap<String, String> createPostUserHashMap(String email, String userName, String firstName, String lastName, String password) {
        HashMap<String, String> result = new HashMap<String, String>();

        result.put("email", email);
        result.put("user_name", userName);
        result.put("first_name", firstName);
        result.put("last_name", lastName);
        result.put("password", password);

        return result;

    }

    public static void setCredentials(String email, String password) {
        SSCUserAPIAdapter.email = email;
        SSCUserAPIAdapter.password = password;
    }

    public static void getFriends(final Context context, final long userID, final at.fhs.smartsigncapture.Callback<List<Friend>> cb){
        APIInterface.getFriends(userID, new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {

                List<Friend> result = new ArrayList<Friend>();

                String bodyString = new String(((TypedByteArray) response.getBody()).getBytes());

                try {
                    JSONArray jsonResult = new JSONArray(bodyString);

                    for(int i = 0; i < jsonResult.length(); i++){
                        JSONObject friendship = jsonResult.getJSONObject(i);

                        JSONObject friend = friendship.getJSONObject("fromUser");
                        int direction = -1;

                        if(friend.getLong("id") == userID){
                            friend = friendship.getJSONObject("toUser");
                            direction = 1;
                        }

                        Friend c = null;

                        if(friendship.getInt("state") == 1){
                            Date friendsSince = jsonDateTimeFormatter.parse(friendship.getString("dateAccepted"));
                            c = new Friend(friend.getLong("id"),friend.getString("user_name"),friend.getString("first_name"),friend.getString("last_name"),friend.getString("image_url"),"", Friend.FriendshipState.FRIENDS,friendsSince);
                        }
                        else{
                            Friend.FriendshipState state = direction == 1 ? Friend.FriendshipState.WAITING_FOR_APPROVAL: Friend.FriendshipState.NEEDS_APPROVAL;
                            c = new Friend(friend.getLong("id"),friend.getString("user_name"),friend.getString("first_name"),friend.getString("last_name"),friend.getString("image_url"),"",state);
                        }

                        result.add(c);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

                cb.success(result);

            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }
}
