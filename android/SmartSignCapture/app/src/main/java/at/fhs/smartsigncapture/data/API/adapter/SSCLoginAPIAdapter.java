package at.fhs.smartsigncapture.data.API.adapter;

import android.util.Base64;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import at.fhs.smartsigncapture.data.API.APIConstants;
import at.fhs.smartsigncapture.data.API.AccessToken;
import at.fhs.smartsigncapture.data.API.interfaces.SSCLoginAPI;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;

/**
 * Created by MartinTiefengrabner on 17/07/15.
 */
public class SSCLoginAPIAdapter {

    public static SSCLoginAPI APIInterface;

    private static GsonConverter converter;

    private static AccessToken token;

    static {
        initAPI();
    }

    public static void initAPI() {

        Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create();

        converter = new GsonConverter(gson);


        RestAdapter.Builder builder = new RestAdapter.Builder();
        builder.setLogLevel(RestAdapter.LogLevel.FULL)
                .setEndpoint(APIConstants.SSC_API_ENDPOINT)
                .setConverter(converter)
                .setRequestInterceptor(new RequestInterceptor() {
                    @Override
                    public void intercept(RequestFacade request) {
                        // create Base64 encodet string
                        String string = "Basic " + Base64.encodeToString(SSCLoginAPIAdapter.clientCredentials().getBytes(), Base64.NO_WRAP);
                        request.addHeader("Accept", "application/json");
                        request.addHeader("Authorization", string);
                    }
                });

        RestAdapter restAdapter = builder.build();
        APIInterface = restAdapter.create(SSCLoginAPI.class);
    }

    public static AccessToken requestClientToken(){
        return APIInterface.requestClientToken("client_credentials","");
    }

    public static AccessToken requestUserToken(String email, String password){
        return APIInterface.requestUserToken(email, password,"password","");
    }

    private static String clientCredentials() {
        return APIConstants.SSC_API_CLIENT_ID + ":" + APIConstants.SSC_API_CLIENT_SECRET;
    }
}

