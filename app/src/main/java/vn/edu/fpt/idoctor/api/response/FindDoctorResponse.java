package vn.edu.fpt.idoctor.api.response;

import java.util.List;

import vn.edu.fpt.idoctor.api.model.User;

/**
 * Created by NamBC on 3/16/2018.
 */

public class FindDoctorResponse extends BaseResponse {
    private List<User> doctors;

    public List<User> getDoctors() {
        return doctors;
    }

    public void setDoctors(List<User> doctors) {
        this.doctors = doctors;
    }
}
