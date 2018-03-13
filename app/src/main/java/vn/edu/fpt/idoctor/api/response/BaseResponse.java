package vn.edu.fpt.idoctor.api.response;

/**
 * Created by NamBC on 3/12/2018.
 */

public class BaseResponse {
    private String code;
    private String message;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
