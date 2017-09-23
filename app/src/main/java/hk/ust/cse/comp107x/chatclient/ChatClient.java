package hk.ust.cse.comp107x.chatclient;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
//import android.provider.ContactsContract;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
//import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
//import android.widget.ListAdapter;
//import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

//import org.json.JSONException;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
//import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
//import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static android.content.ContentValues.TAG;
import static hk.ust.cse.comp107x.chatclient.R.id.friendName;
import static hk.ust.cse.comp107x.chatclient.R.id.messageList;



public class ChatClient extends AppCompatActivity implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {



    public class Chats {
        int id;
        Message message;
    }

    String myName;
    ImageButton sendButton;
    EditText messageText;
    RecyclerView messageList;
    String json;
    RecyclerView.Adapter mAdapter = null;
    ArrayList<Message> messages = null;
    int in_index ;
    Context mContext;
    String friendNamed;
    List<Chats> list;

    SwipeRefreshLayout sLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_client);
        mContext = this;

        sendButton = (ImageButton) findViewById(R.id.sendButton);
        sendButton.setOnClickListener(this);

        messageText = (EditText) findViewById(R.id.messageText);
        sLayout = (SwipeRefreshLayout) findViewById(R.id.swipeLayout);
        sLayout.setOnRefreshListener(this);


        Intent in = getIntent();
        friendNamed = in.getStringExtra(getString(R.string.friend));
        myName = in.getStringExtra("MyName");


        getSupportActionBar().setTitle(getResources().getString(R.string.app_name) + ": " + friendNamed);

        messageList = (RecyclerView) findViewById(R.id.messageList);
        messageList.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        messageList.setLayoutManager(llm);


        if (isOnline()) {
            // Start the AsyncTask to process the Json string in the background and then initialize the listview
            MessageProcessor mytask = new MessageProcessor();

            mytask.execute(Constants.JSON_URL1);
        }
        else {
            // Toast displays the message on the screen for a period of time
            Toast.makeText(this, "You are Offline! Turn on your network!", Toast.LENGTH_LONG).show();
        }
    }

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
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.sendButton:

                String messString = messageText.getText().toString();

                // If the message is not empty string
                if (!messString.equals("")) {

                    // Create a new Message object and initialize it with the information
                    Message message = new Message(friendNamed, myName, messString, true, new Date());

                    // Add the Message object to the ArrayList
                    messages.add(message);
                    messageList.scrollToPosition(messages.size()-1);
                    //mAdapter.notifyDataSetChanged();

                    Chats Nchat = new Chats();
                    Nchat.id = in_index + 2;
                    Nchat.message = message;
                    json = new Gson().toJson(Nchat);

                    message = null;
                    messageText.setText("");

                    if (isOnline()) {

                        MessageSender task = new MessageSender();
                        task.execute(Constants.JSON_URL1);

                        MessageProcessor mytask = new MessageProcessor();
                        mytask.execute(Constants.JSON_URL1);
                    }
                    else {
                        // Toast displays the message on the screen for a period of time
                        Toast.makeText(this, "You are Offline! Turn on your network!", Toast.LENGTH_LONG).show();
                    }


                }
            default:
                break;
        }
    }
    @Override
    public void onRefresh() {

        if (isOnline()) {
            // Start the AsyncTask to process the Json string in the background and then initialize the listview

            MessageProcessor mytask = new MessageProcessor();
            mytask.execute(Constants.JSON_URL1);
        }
        else {
            // Toast displays the message on the screen for a period of time
            Toast.makeText(this, "You are Offline! Turn on your network!", Toast.LENGTH_LONG).show();

        }
        sLayout.setRefreshing(false);
    }

    private class MessageProcessor extends AsyncTask<String, Void, Integer> {

        public MessageProcessor() {
            super();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

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
                    processMessage(response);
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

            MyAdapter mAdapter = new MyAdapter(mContext, messages);
            messageList.setAdapter(mAdapter);
            messageList.scrollToPosition(messages.size()-1);
            mAdapter.notifyDataSetChanged();

        }

        // This method is called if we cancel the background processing
        @Override
        protected void onCancelled() {
            super.onCancelled();
        }
    }

    private class MessageSender extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

            URL object= null;
            try {
                object = new URL(params[0]);
                HttpURLConnection con = (HttpURLConnection) object.openConnection();
                con.setDoOutput(true);
                con.setDoInput(true);
                con.setRequestProperty("Content-Type", "application/json");
                con.setRequestProperty("Accept", "application/json");
                con.setRequestMethod("POST");


                OutputStreamWriter wr = new OutputStreamWriter(con.getOutputStream());
                wr.write(json);
                wr.flush();

                StringBuilder sb = new StringBuilder();
                int HttpResult = con.getResponseCode();
                if (HttpResult == HttpURLConnection.HTTP_OK) {
                    BufferedReader br = new BufferedReader(
                            new InputStreamReader(con.getInputStream(), "utf-8"));
                    String line = null;
                    while ((line = br.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    br.close();
                    return sb.toString();
                } else {
                    return con.getResponseMessage();
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.e("TAG", result);
        }
    }

    protected String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null){
            result += line;
        }

        if(null!=inputStream){
            inputStream.close();
        }
        return result;
    }

    private void processMessage(String infoString) {

        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();

        messages = new ArrayList<Message>();
        list = new ArrayList<Chats>();

        list =  Arrays.asList(gson.fromJson(infoString, Chats[].class));
        int size = list.size();
        for (in_index = 0; in_index <size; ++in_index) {
            if ((list.get(in_index).message.getFromName().equals(friendNamed)&&list.get(in_index).message.getFromName2().equals(myName))||(list.get(in_index).message.getFromName().equals(myName)&&list.get(in_index).message.getFromName2().equals(friendNamed)) )
            {
                if(list.get(in_index).message.getFromName2().equals(myName)) list.get(in_index).message.setSelf(true);
                else list.get(in_index).message.setSelf(false);

                messages.add(list.get(in_index).message);
            }
        }
        --in_index;

    }



}

