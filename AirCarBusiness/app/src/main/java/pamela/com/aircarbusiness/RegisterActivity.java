package pamela.com.aircarbusiness;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import java.util.Calendar;

/**
 * Created by pamela on 08/11/2016.
 * This Activity is called when the user click the Sign up button from the LoginActivity
 * The user enters login details and if the details are valid, then then user is registered.
 * The user data is then saved to the database.
 */

public class RegisterActivity extends AppCompatActivity {
    private EditText email_editText;
    private EditText password_editText;
    private EditText confirm_password_editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        email_editText = (EditText) findViewById(R.id.email_editText);
        password_editText = (EditText) findViewById(R.id.password_editText);
        confirm_password_editText = (EditText) findViewById(R.id.confirm_password_editText);
        Button register_button = (Button) findViewById(R.id.register_button);

        register_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = String.valueOf(email_editText.getText());
                CharSequence emailchar = (CharSequence) email;
                String password = String.valueOf(password_editText.getText());
                String confirm_password = String.valueOf(confirm_password_editText.getText());

                if (isValidEmail(emailchar)) {
                    if (password.equals(confirm_password)) {
                        ParseUser user = new ParseUser();
                        user.setUsername(email);
                        user.setEmail(email);
                        user.setPassword(password);
                        //ParseUser.getCurrentUser().logOut();
                        user.signUpInBackground(new SignUpCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null) {
                                    Toast.makeText(RegisterActivity.this, "Sign up success", Toast.LENGTH_SHORT).show();
                                    Intent i = new Intent(RegisterActivity.this,MainActivity.class);
                                    startActivity(i);
                                } else {
                                    Toast.makeText(RegisterActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                }else{
                    Toast.makeText(RegisterActivity.this, "Invalid email address", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //checks if the email is in the right format.
    private boolean isValidEmail(CharSequence email) {
        if (email == null)
            return false;

        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}
