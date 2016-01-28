package at.fhs.smartsigncapture.data.API;

/**
 * Created by MartinTiefengrabner on 16/07/15.
 */
public class AccessToken {
    private final String accessToken;
    private String tokenType;
    private final String refreshToken;
    private final String scope;
    private final long expiresIn;

    public AccessToken(String accessToken, long expiresIn, String tokenType,String scope, String refreshToken){
        this.accessToken = accessToken;
        this.expiresIn = expiresIn;
        this.tokenType = tokenType;
        this.scope = scope;
        this.refreshToken = refreshToken;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getTokenType() {
        // OAuth requires uppercase Authorization HTTP header value for token type
        if (!Character.isUpperCase(tokenType.charAt(0))) {
            tokenType = Character.toString(tokenType.charAt(0)).toUpperCase() + tokenType.substring(1);
        }

        return tokenType;
    }
}
