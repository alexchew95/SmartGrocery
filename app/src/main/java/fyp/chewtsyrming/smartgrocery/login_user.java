package fyp.chewtsyrming.smartgrocery;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import fyp.chewtsyrming.smartgrocery.object.PasswordHash;

public class login_user extends AppCompatActivity implements View.OnClickListener {

    public static final String PREFS_NAME = "MyPrefsFile";
    private static final String PREF_EMAIL = "username";
    private static final String PREF_PASSWORD = "password";
    private static final String PREF_REMEMBER = "remember_me";
    RelativeLayout rellay,rellay2;
    ProgressBar progressBar;
    ImageView imgView_logo;
    TextView loginTV,registerTV;
    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            loginTV.setVisibility(View.VISIBLE);
            rellay.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);


        }
    };
    Runnable loginToRegister = new Runnable() {
        @Override
        public void run() {
            registerTV.setVisibility(View.VISIBLE);

            rellay2.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);


        }
    }; Runnable registerToLogin = new Runnable() {
        @Override
        public void run() {
            loginTV.setVisibility(View.VISIBLE);

            rellay.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);

        }
    };
    String SaltPassword = null;
    EditText l_email, l_password;
    Button login,register, goto_register, goto_login;
    FirebaseAuth fAuth;
    private String uid;
    private FirebaseAuth.AuthStateListener fAuthListen;
    PasswordHash passwordHash;

    private CheckBox rememberMeCB1;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_user);

        imgView_logo = findViewById(R.id.imgView_logo);
        rellay = findViewById(R.id.rellay1);
        rellay2 = findViewById(R.id.rellay2);
        progressBar = findViewById(R.id.progressBar);
        loginTV = findViewById(R.id.loginTV);
        registerTV = findViewById(R.id.registerTV);
        handler.postDelayed(runnable, 3000);
        fAuth = FirebaseAuth.getInstance();
        l_email = (EditText) findViewById(R.id.emailField);
        l_password = (EditText) findViewById(R.id.passwordField);
        login = (Button) findViewById(R.id.login);
        register = (Button) findViewById(R.id.r_register);
        goto_register = (Button) findViewById(R.id.goto_register);
        goto_login = (Button) findViewById(R.id.goto_login);

        //switch1.findViewById(R.id.switch1);
        rememberMeCB1 = (CheckBox) findViewById(R.id.rememberMeCheckBox);
        //Shared Preference (Remember Me)
        SharedPreferences pref = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        l_email.setText(pref.getString(PREF_EMAIL, null));
        l_password.setText(pref.getString(PREF_PASSWORD, null));
        check_rememberMePref();
        login.setOnClickListener(this);
        register.setOnClickListener(this);
        goto_register.setOnClickListener(this);
        goto_login.setOnClickListener(this);

//register button
      /*  goto_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

              *//*  Intent i = new Intent(login_user.this, register_user.class);
                startActivity(i);
                finish();*//*

            }
        });*/

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
            // Toast.makeText(login_user.this, deHashPassword, Toast.LENGTH_LONG).show();
            l_password.setText(deHashPassword);
            rememberMeCB1.setChecked(true);

        }


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.goto_login:

                registerTV.setVisibility(View.GONE);

                rellay2.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
                handler.postDelayed(registerToLogin, 3000);
                break;
            case R.id.goto_register:
                loginTV.setVisibility(View.GONE);


                rellay.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
                handler.postDelayed(loginToRegister, 3000);
                break;
            case R.id.login:
                if (l_email.getText().length() == 0 || l_password.getText().length() == 0) {
                    Toast.makeText(getApplicationContext(), "Please enter your email & password", Toast.LENGTH_LONG).show();
                } else {
                    loginEvent();
                }
                break;
            case R.id.r_register:

                break;
        }
    }
}
