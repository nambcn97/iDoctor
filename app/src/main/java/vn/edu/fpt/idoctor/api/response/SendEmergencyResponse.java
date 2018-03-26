package vn.edu.fpt.idoctor.api.response;

import java.util.List;

import vn.edu.fpt.idoctor.api.model.EmergencyBean;

/**
 * Created by NamBC on 3/25/2018.
 */

public class SendEmergencyResponse extends BaseResponse {
    private List<EmergencyBean> emergencies;
    public List<EmergencyBean> getEmergencies() {
        return this.emergencies;
    }

    public void setEmergencies(List<EmergencyBean> emergencies) {
        this.emergencies = emergencies;
    }
}
