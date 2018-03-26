package vn.edu.fpt.idoctor.api.model;

import java.util.Date;

/**
 * Created by NamBC on 3/25/2018.
 */

public class EmergencyBean {
    private Long id;
    private User fromUser;
    private User toUser;
    private Date date;
    private String status;
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public User getFromUser() {
        return this.fromUser;
    }
    public void setFromUser(User fromUser) {
        this.fromUser = fromUser;
    }
    public User getToUser() {
        return this.toUser;
    }
    public void setToUser(User toUser) {
        this.toUser = toUser;
    }
    public Date getDate() {
        return this.date;
    }
    public void setDate(Date date) {
        this.date = date;
    }
    public String getStatus() {
        return this.status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
}
