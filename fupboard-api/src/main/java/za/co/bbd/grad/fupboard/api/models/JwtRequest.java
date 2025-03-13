package za.co.bbd.grad.fupboard.api.models;

public class JwtRequest {
    private String code;
    private String uri;
    
    public String getCode() {
        return code;
    }
    public void setCode(String code) {
        this.code = code;
    }
    public String getUri() {
        return uri;
    }
    public void setUri(String uri) {
        this.uri = uri;
    }
}
