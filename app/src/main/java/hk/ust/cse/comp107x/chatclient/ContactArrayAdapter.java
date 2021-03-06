package hk.ust.cse.comp107x.chatclient;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by muppala on 18/6/16.
 */
public class ContactArrayAdapter  extends ArrayAdapter<Info> {
    private final Context context;
    private final List<Info> friendInfoArrayList;

    public ContactArrayAdapter(Context context, List<Info> friendInfoArrayList) {
        super(context, R.layout.friend_item, friendInfoArrayList);
        this.context = context;
        this.friendInfoArrayList = friendInfoArrayList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View friendInfoView;

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // Change the layout based on who the message is from
        friendInfoView = inflater.inflate(R.layout.friend_item, parent, false);
        TextView friendName = (TextView) friendInfoView.findViewById(R.id.friendName);
        friendName.setText(friendInfoArrayList.get(position).name);
        TextView statusMsg = (TextView) friendInfoView.findViewById(R.id.statusMsg);
        statusMsg.setText(friendInfoArrayList.get(position).statusMsg);
        ImageView imageView = (ImageView) friendInfoView.findViewById(R.id.avatar);
        // This set of steps are used to load the friend's picture into the ImageView. We take
        // the help of the Picasso image downloading library to do this for us asynchronously
        // TODO Asynchronously load the images from the server using Picasso.
        Picasso.with(context).load(Constants.Contacts_SERVER + Constants.Contacts_PORT+ friendInfoArrayList.get(position).imageURL).resize(50, 50).centerCrop().into(imageView);

        Picasso.with(context).load(Constants.Contacts_SERVER + Constants.Contacts_PORT + friendInfoArrayList.get(position).imageURL).placeholder(R.mipmap.ic_launcher)
                .error(R.mipmap.ic_launcher)
                .into(imageView);
        return friendInfoView;
    }
}
