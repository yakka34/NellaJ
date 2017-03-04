package Nella;

import java.time.LocalDateTime;

public class Session {
    
    private String accessToken;
    private String tokenType;
    private int expiryIn;
    private LocalDateTime expiry_date;

    public Session(String accessToken, String tokenType, int expiryIn) {
        this.accessToken = accessToken;
        this.tokenType = tokenType;
        this.expiryIn = expiryIn;
        this.expiry_date = LocalDateTime.now().plusSeconds(expiryIn);
    }
    
    public String getAccessToken() {
        return accessToken;
    }
    
    public void updateExpiryDate(){
        this.expiry_date = LocalDateTime.now().plusSeconds(expiryIn);
    }
    
    public Boolean isExpired(){
        return expiry_date.isBefore(LocalDateTime.now());
    }
}
