package at.fhs.smartsigncapture.data.API.adapter;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;

import java.io.IOException;
import java.net.HttpURLConnection;

import at.fhs.smartsigncapture.data.API.APIConstants;
import at.fhs.smartsigncapture.data.API.AccessToken;
import at.fhs.smartsigncapture.data.API.interfaces.SSCClientAPI;
import retrofit.RestAdapter;
import retrofit.client.OkClient;
import retrofit.converter.GsonConverter;

/**
 * Created by MartinTiefengrabner on 17/07/15.
 */
public class SSCClientAPIAdapter {
    public static SSCClientAPI APIInterface;

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

        OkHttpClient okHttpClient = new OkHttpClient();

        okHttpClient.interceptors().add(new Interceptor() {
            @Override
            public com.squareup.okhttp.Response intercept(Chain chain) throws IOException {
                Request request = chain.request();

                int responseCode = HttpURLConnection.HTTP_UNAUTHORIZED;

                com.squareup.okhttp.Response response = null;

                if(token != null && token.getAccessToken() != null){
                    request = request.newBuilder()
                            .header("Accept", "application/json")
                            .header("Authorization","Bearer "+token.getAccessToken())
                            .build();
                    response = chain.proceed(request);
                    responseCode = response.code();
                }

                if (responseCode == HttpURLConnection.HTTP_UNAUTHORIZED) {

                    token = SSCLoginAPIAdapter.requestClientToken();

                    Request newRequest = request.newBuilder()
                            .header("Accept", "application/json")
                            .header("Authorization","Bearer "+token.getAccessToken())
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
        APIInterface = restAdapter.create(SSCClientAPI.class);
    }
}
