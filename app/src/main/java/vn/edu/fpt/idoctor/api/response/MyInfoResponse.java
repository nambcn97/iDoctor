package vn.edu.fpt.idoctor.api.response;

import vn.edu.fpt.idoctor.api.model.User;

/**
 * Created by NamBC on 3/30/2018.
 */

public class MyInfoResponse extends BaseResponse {
    private User myInfo;

    public User getMyInfo() {
        return myInfo;
    }

    public void setMyInfo(User myInfo) {
        this.myInfo = myInfo;
    }
}
