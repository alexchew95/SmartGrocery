package fyp.chewtsyrming.smartgrocery;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class login_user extends AppCompatActivity {

    public static final String PREFS_NAME = "MyPrefsFile";
    private static final String PREF_EMAIL = "savusername";
    private static final String PREF_PASSWORD = "savpassword";
    EditText l_email, l_password;
    Button l_login, l_register;
    FirebaseAuth fAuth;
    private String uid;
    private FirebaseAuth.AuthStateListener fAuthListen;
    private ProgressDialog dialog;
    private TextView remember;
    private CheckBox cb1;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_user);

        fAuth = FirebaseAuth.getInstance();
        dialog = new ProgressDialog(this);
        l_email = (EditText) findViewById(R.id.emailField);
        l_password = (EditText) findViewById(R.id.passwordField);
        l_login = (Button) findViewById(R.id.login);
        l_register = (Button) findViewById(R.id.register);
        //switch1.findViewById(R.id.switch1);
        cb1 = (CheckBox) findViewById(R.id.checkbox);
        remember = (TextView) findViewById(R.id.remember);

        //Shared Preference (Remember Me)
        SharedPreferences pref = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        l_email.setText(pref.getString(PREF_EMAIL, null));
        l_password.setText(pref.getString(PREF_PASSWORD, null));


        cb1.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {
                if (cb1.isChecked()) {
                    String email = l_email.getText().toString();
                    String pass = l_password.getText().toString();
                    getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit().putString(PREF_EMAIL, email).putString(PREF_PASSWORD, pass).commit();

                } else if (!cb1.isChecked()) {
                    l_email.setText("");
                    l_password.setText("");

                    String email = l_email.getText().toString();
                    String pass = l_password.getText().toString();
                    getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit().putString(PREF_EMAIL, email).putString(PREF_PASSWORD, pass).commit();

                }
            }
        });


        //login button
        l_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (l_email.getText().length() == 0 || l_password.getText().length() == 0) {
                    Toast.makeText(getApplicationContext(), "Please enter your email & password", Toast.LENGTH_LONG).show();
                } else {
                    loginEvent();
                }
            }
        });
//register button
        l_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(login_user.this, register_user.class);
                startActivity(i);
                finish();

            }
        });

    }


    //login process
    private void loginEvent() {
        //check authnetiction
        fAuth.signInWithEmailAndPassword(l_email.getText().toString(), l_password.getText().toString()).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {

                    goMain();
                } else {
                    Toast.makeText(login_user.this, "Invalid account, try again.", Toast.LENGTH_LONG).show();
                }

            }
        });
    }

    private void goMain() {
        Intent i = new Intent(login_user.this, home.class);
        startActivity(i);
    }
}
