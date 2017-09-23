package hk.ust.cse.comp107x.chatclient;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by muppala on 8/6/16.
 */

public class Message {
    private String Name, Name2, message;
    private boolean fromMe;
    private Date date;

    public Message() {
    }

    public Message(String fromName, String Name2, String message, boolean fromMe, Date date) {
        this.Name = fromName;
        this.Name2 = Name2;
        this.message = message;
        this.fromMe = fromMe;
        this.date = date;
    }

    public String getFromName() {
        return Name;
    }

    public void setFromName(String fromName) {
        this.Name = fromName;
    }

    public String getFromName2() {
        return Name2;
    }

    public void setFromName2(String fromName2) {
        this.Name2 = fromName2;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean fromMe() {
        return fromMe;
    }

    public void setSelf(boolean fromMe) {
        this.fromMe = fromMe;
    }

    public void setDate(Date date) { this.date = date; }

    public String getDate() {

        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy");

        return sdf.format(date);

    }

    public String getTime() {

        SimpleDateFormat sdf = new SimpleDateFormat("h:mm a");

        return sdf.format(date);

    }

}