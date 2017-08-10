package pamela.com.aircarbusiness;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseException;
import com.parse.ParseUser;

/**
 * Created by pamela on 08/11/2016.
 * This Activity is where the users enter their log in details or where the users sign up.
 * This Activity is called from the MainActivity if the user is not yet logged in.
 * If the user press the Sign up button then in goes to RegisterActivity.
 */

public class LoginActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button login_button = (Button) findViewById(R.id.login_button);
        //if the login button is clicked then log in the user
        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText email_editText = (EditText) findViewById(R.id.email_editText);
                EditText password_editText = (EditText) findViewById(R.id.password_editText);

                final String email = String.valueOf(email_editText.getText());
                final String password = String.valueOf(password_editText.getText());

                //if the emails and password is correct, then it logs in the user and goes to MainActivity.
                ParseUser.logInInBackground(email, password, new LogInCallback() {
                    @Override
                    public void done(ParseUser user, ParseException e) {
                        if(e == null) {
                            Toast.makeText(LoginActivity.this, "Logged in", Toast.LENGTH_SHORT).show();
                            Intent i = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(i);
                        }else{
                            if(e.getCode() == 101){
                                Toast.makeText(LoginActivity.this, "Invalid username/password.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
            }
        });

        Button register_button = (Button) findViewById(R.id.register_button);
        //if the register button is clicked the go to RegisterActivity
        register_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(i);
            }
        });
    }
}