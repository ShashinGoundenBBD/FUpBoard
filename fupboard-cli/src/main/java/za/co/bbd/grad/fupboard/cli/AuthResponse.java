package za.co.bbd.grad.fupboard.cli;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class AuthResponse {
    private String accessToken;
    private int expiresIn;
    private String refreshToken;
    private String scope;
    private String tokenType;
    private String idToken;
    
    public String getAccessToken() {
        return accessToken;
    }
    public int getExpiresIn() {
        return expiresIn;
    }
    public String getRefreshToken() {
        return refreshToken;
    }
    public String getScope() {
        return scope;
    }
    public String getTokenType() {
        return tokenType;
    }
    public String getIdToken() {
        return idToken;
    }
}
