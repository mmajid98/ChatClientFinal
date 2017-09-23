package hk.ust.cse.comp107x.chatclient;

import java.util.ArrayList;

/**
 * Created by Ayesha on 8/28/2017.
 */

public class Info {
    String name, password;
    int id;
    String statusMsg;
    String imageURL;
    ArrayList<Integer>friends;

    public Info (int id, String name, String password, String statusMsg, String imageURL, ArrayList<Integer> friends) {
        this.name=name;
        this.password = password;
        this.id=id;
        this.statusMsg=statusMsg;
        this.imageURL = imageURL;
        this.friends = friends;

    }
}
