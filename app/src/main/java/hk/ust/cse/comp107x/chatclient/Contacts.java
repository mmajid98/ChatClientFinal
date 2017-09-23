package hk.ust.cse.comp107x.chatclient;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Contacts extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private static final String TAG="Contacts";

    ListView friendView;

    ContactArrayAdapter mArrayAdapter;

    String name;
    List<Info> friendInfoList = null;
    ArrayList<Info> FinalList = null;
    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        mContext = this;

        mArrayAdapter = new ContactArrayAdapter(this, FinalList);


        friendView = (ListView) findViewById(R.id.friendListView);
        friendView.setOnItemClickListener(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Should set up to add new contacts", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
           }
        });

        if (isOnline()) {

            Intent in = getIntent();
            name = in.getStringExtra("username");
            String password = in.getStringExtra("password");
            // Start the AsyncTask to process the Json string in the background and then initialize the listview
            FriendsProcessor mytask = new FriendsProcessor();
            mytask.execute(Constants.JSON_URL, name, password);


        }
        else {
            // Toast displays the message on the screen for a period of time
            Toast.makeText(this, "You are Offline! Turn on your network!", Toast.LENGTH_LONG).show();
        }
    }

    // This method checks to see if the device is online. Returns true if online, else false
    private boolean isOnline() {

        // Connectivity manager gives you access to the current state of the connection
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        }
        else {
            return false;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

        Intent mIntent = new Intent(this,ChatClient.class);
        TextView friendName = (TextView) view.findViewById(R.id.friendName);
        mIntent.putExtra(getString(R.string.friend), friendName.getText().toString());
        mIntent.putExtra("MyName", name);
        startActivity(mIntent);

    }

    // This AsyncTask processes the Json string by reading it from a file in the assets folder and
    // then converts the string into a list of FriendInfo objects. You will also see the use of
    // a progress dialog to show that work is being processed in the background.

    private class FriendsProcessor extends AsyncTask<String, Void, Integer> {

        ProgressDialog progressDialog;

        public FriendsProcessor() {
            super();
        }

        // The onPreExecute is executed on the main UI thread before background processing is
        // started. In this method, we start the progressdialog.
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            // Show the progress dialog on the screen
            progressDialog = ProgressDialog.show(mContext, "Wait!","Downloading Friends List");
        }

        // This method is executed in the background and will return a result to onPostExecute
        // method. It receives the file name as input parameter.
        @Override
        protected Integer doInBackground(String... urls) {

            InputStream inputStream = null;
            HttpURLConnection urlConnection = null;
            Integer result = 0;

            try {
                /* forming th java.net.URL object */
                URL url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();

                 /* optional request header */
                urlConnection.setRequestProperty("Content-Type", "application/json");

                /* optional request header */
                urlConnection.setRequestProperty("Accept", "application/json");

                /* for Get request */
                urlConnection.setRequestMethod("GET");
                int statusCode = urlConnection.getResponseCode();

                /* 200 represents HTTP OK */
                if (statusCode == 200) {
                    inputStream = new BufferedInputStream(urlConnection.getInputStream());

                    // Convert the read in information to a Json string
                    String response = convertInputStreamToString(inputStream);

                    // now process the string using the method that we implemented in the previous exercise
                    processFriendInfo(response, urls[1], urls[2]);


                    result = 1; // Successful
                } else {
                    result = 0; //"Failed to fetch data!";
                }
            } catch (Exception e) {
                Log.d(TAG, e.getLocalizedMessage());
            }
            return result; //"Failed to fetch data!";
        }

        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);

            mArrayAdapter = new ContactArrayAdapter(mContext, FinalList);
            friendView.setAdapter((ListAdapter) mArrayAdapter);

            progressDialog.dismiss();
        }

        // This method is called if we cancel the background processing
        @Override
        protected void onCancelled() {
            super.onCancelled();

            progressDialog.dismiss();
        }
    }

    // This method converts an input stream into a string. It reads in the input line by line
    // and then converts it into a string.
    protected String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null){
            result += line;
        }

            /* Close Stream */
        if(null!=inputStream){
            inputStream.close();
        }
        return result;
    }

    // This class processes the Json string and converts it into a list of FriendInfo objects
    // We make use of the Gson library to do this automatically
    private void processFriendInfo(String infoString, String name, String password) {

        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();

        friendInfoList = new ArrayList<Info>();
        friendInfoList = Arrays.asList(gson.fromJson(infoString, Info[].class));

        FinalList = AuthenticateAndGetData(friendInfoList, name, password);

    }

    private ArrayList<Info> AuthenticateAndGetData(List<Info> friendInfoList, String name, String password) {
        ArrayList<Info> FinalList = new ArrayList<Info>();
        for(int i = 0; i < friendInfoList.size(); ++i) {
            if (friendInfoList.get(i).name.equals(name) && friendInfoList.get(i).password.equals(password)) {
                for (int j = 0; j < friendInfoList.get(i).friends.size(); ++j) {
                    for (int k = 0; k < friendInfoList.size(); ++k) {
                        if (friendInfoList.get(i).friends.get(j)==friendInfoList.get(k).id)FinalList.add(friendInfoList.get(k));
                    }
                }

                break;
            }

        }
        if (FinalList.isEmpty()) {

            finish();
        }

        return FinalList;
    }
}
