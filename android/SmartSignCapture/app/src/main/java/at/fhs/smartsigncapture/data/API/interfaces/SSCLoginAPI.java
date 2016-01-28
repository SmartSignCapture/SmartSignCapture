package at.fhs.smartsigncapture.data.API.interfaces;

import at.fhs.smartsigncapture.data.API.AccessToken;
import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.POST;
import retrofit.http.Query;

/**
 * Created by MartinTiefengrabner on 17/07/15.
 */
public interface SSCLoginAPI {
    @POST("/request-token/")
    void login(@Query("email") String email, @Query("password") String password,@Query("grant_type") String grantType, Callback<AccessToken> token);

    @POST("/request-token/")
    AccessToken requestUserToken(@Query("email") String email, @Query("password") String password,@Query("grant_type") String grantType, @Body String body);

    @POST("/request-token/")
    AccessToken requestClientToken(@Query("grant_type") String grantType,@Body String body);
}
