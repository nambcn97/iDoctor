package vn.edu.fpt.idoctor.common;

/**
 * Created by NamBC on 3/22/2018.
 */

public class APIError {

    private int statusCode;
    private String message;

    public APIError() {
    }

    public int getStatus() {
        return statusCode;
    }

    public String getMessage() {
        return message;
    }
}