package fyp.chewtsyrming.smartgrocery;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import fyp.chewtsyrming.smartgrocery.object.PasswordHash;

public class login_user extends AppCompatActivity {

    public static final String PREFS_NAME = "MyPrefsFile";
    private static final String PREF_EMAIL = "username";
    private static final String PREF_PASSWORD = "password";
    private static final String PREF_REMEMBER = "remember_me";
    String SaltPassword = null;
    EditText l_email, l_password;
    Button l_login, l_register;
    FirebaseAuth fAuth;
    private String uid;
    private FirebaseAuth.AuthStateListener fAuthListen;
    PasswordHash passwordHash;

    private CheckBox rememberMeCB1;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_user);

        fAuth = FirebaseAuth.getInstance();
        l_email = (EditText) findViewById(R.id.emailField);
        l_password = (EditText) findViewById(R.id.passwordField);
        l_login = (Button) findViewById(R.id.login);
        l_register = (Button) findViewById(R.id.register);
        //switch1.findViewById(R.id.switch1);
        rememberMeCB1 = (CheckBox) findViewById(R.id.rememberMeCheckBox);
        //Shared Preference (Remember Me)
        SharedPreferences pref = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        l_email.setText(pref.getString(PREF_EMAIL, null));
        l_password.setText(pref.getString(PREF_PASSWORD, null));
        check_rememberMePref();
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
        SharedPreferences rememberMePref =
                getPreferences(MODE_PRIVATE);
        final SharedPreferences.Editor prefEditor =
                rememberMePref.edit();
        String email = l_email.getText().toString();
        final String password = l_password.getText().toString();
        //check authnetiction
        fAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    if (rememberMeCB1.isChecked()) {
                        String email = l_email.getText().toString();

                        try {
                            //hash password
                            SaltPassword = passwordHash.encrypt(password);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        prefEditor.putString(PREF_EMAIL, email).putString(PREF_PASSWORD, SaltPassword).putBoolean(PREF_REMEMBER, true).apply();


                    } else if (!rememberMeCB1.isChecked()) {
                        prefEditor.clear().apply();

                    }

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
    private void check_rememberMePref() {
        String deHashPassword = null;
        SharedPreferences rememberMePref =
                getPreferences(MODE_PRIVATE);
        if (rememberMePref.contains(PREF_EMAIL)) {
            l_email.setText(rememberMePref.getString(PREF_EMAIL, null));
            String tempPass = rememberMePref.getString(PREF_PASSWORD, null);
            try {
                deHashPassword = passwordHash.decrypt(tempPass);
            } catch (Exception e) {
                e.printStackTrace();
            }
            Toast.makeText(login_user.this, deHashPassword, Toast.LENGTH_LONG).show();
            l_password.setText(deHashPassword);
            rememberMeCB1.setChecked(true);

        }


    }
}
