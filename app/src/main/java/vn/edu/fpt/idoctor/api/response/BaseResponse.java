package vn.edu.fpt.idoctor.api.response;

/**
 * Created by NamBC on 3/12/2018.
 */

public class BaseResponse {
    private Integer resultCode;
    private String resultMsg;

    public Integer getResultCode() {
        return resultCode;
    }

    public void setResultCode(Integer resultCode) {
        this.resultCode = resultCode;
    }

    public String getResultMsg() {
        return resultMsg;
    }

    public void setResultMsg(String resultMsg) {
        this.resultMsg = resultMsg;
    }
}
