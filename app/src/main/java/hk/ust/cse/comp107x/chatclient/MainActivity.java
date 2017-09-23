package hk.ust.cse.comp107x.chatclient;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    EditText Name;
    EditText Password;
    Button SignIn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        SignIn = (Button) findViewById(R.id.signIn);
        SignIn.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        Name = (EditText) findViewById(R.id.MyName);
        Password = (EditText) findViewById(R.id.password);
        TextView invalid = (TextView) findViewById(R.id.Invalid);
        String userName = Name.getText().toString();
        String password = Password.getText().toString();
        Name.setText("");
        Password.setText("");

        Intent in = new Intent(this,Contacts.class);
        in.putExtra("username", userName);
        in.putExtra("password", password);
        invalid.setText("Invalid Name or Password.");
        startActivity(in);

    }
}
