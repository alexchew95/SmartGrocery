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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.onesignal.OSPermissionSubscriptionState;
import com.onesignal.OneSignal;

import fyp.chewtsyrming.smartgrocery.object.PasswordHash;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class login_user extends AppCompatActivity implements View.OnClickListener {
    private DatabaseReference mDatabase;

    ProgressBar progressBar;
    public static final String PREFS_NAME = "MyPrefsFile";
    private static final String PREF_EMAIL = "username";
    private static final String PREF_PASSWORD = "password";
    private static final String PREF_REMEMBER = "remember_me";
    RelativeLayout rellay, rellay2;
    ImageView imgView_logo;
    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            check_rememberMePref();


        }
    };
    Runnable loginToRegister = new Runnable() {
        @Override
        public void run() {
            progressBar.setVisibility(View.GONE);

            rellay2.setVisibility(View.VISIBLE);


        }
    };
    Runnable registerToLogin = new Runnable() {
        @Override
        public void run() {
            progressBar.setVisibility(View.GONE);

            rellay.setVisibility(View.VISIBLE);

        }
    };Runnable registerProcess = new Runnable() {
        @Override
        public void run() {
            progressBar.setVisibility(View.GONE);

            rellay2.setVisibility(View.GONE);

        }
    };Runnable loginProcess = new Runnable() {
        @Override
        public void run() {
            loginEvent();


        }
    };
    String SaltPassword = null;
    EditText l_email, l_password;
    Button loginBtn, registerBtn, goto_registerBtn, goto_loginBtn;
    FirebaseAuth fAuth;
    private FirebaseAuth.AuthStateListener fAuthListen;
    PasswordHash passwordHash;
    private CheckBox rememberMeCB1;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_user);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        rellay = findViewById(R.id.rellay1);
        rellay2 = findViewById(R.id.rellay2);
        handler.postDelayed(runnable, 0);
        fAuth = FirebaseAuth.getInstance();
        l_email = findViewById(R.id.emailField);
        l_password = findViewById(R.id.passwordField);
        loginBtn = findViewById(R.id.loginBtn);
        registerBtn = findViewById(R.id.registerBtn);
        goto_registerBtn = findViewById(R.id.goto_registerBtn);
        goto_loginBtn = findViewById(R.id.goto_loginBtn);
        progressBar = findViewById(R.id.progressBar);
        //switch1.findViewById(R.id.switch1);
        rememberMeCB1 = findViewById(R.id.rememberMeCheckBox);
        //Shared Preference (Remember Me)
        SharedPreferences pref = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        l_email.setText(pref.getString(PREF_EMAIL, null));
        l_password.setText(pref.getString(PREF_PASSWORD, null));
        loginBtn.setOnClickListener(this);
        goto_registerBtn.setOnClickListener(this);

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
                    FirebaseUser user = task.getResult().getUser();
                     String userId =user.getUid();

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
                    OSPermissionSubscriptionState status = OneSignal.getPermissionSubscriptionState();
                    String playerId= status.getSubscriptionStatus().getUserId();

                    //Toast.makeText(login_user.this, playerId, Toast.LENGTH_LONG).show();
                    //goMain();

                    mDatabase.child("user").child(userId).child("profile").child("playerId").setValue(playerId);
                    goMain();


                } else {
                    progressBar.setVisibility(View.GONE);
                    rellay.setVisibility(View.VISIBLE);
                    Toast.makeText(login_user.this, "Invalid account, try again.", Toast.LENGTH_LONG).show();
                }

            }
        });
    }
private void updatePlayerId(String playerId){

}
    private void goMain() {
        Intent i = new Intent(getApplicationContext(), home.class);
        i.setFlags(FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
        finish();
    }

    private void check_rememberMePref() {
        String deHashPassword = null;
        SharedPreferences rememberMePref =
                getPreferences(MODE_PRIVATE);
        if (rememberMePref.contains(PREF_EMAIL)) {
            l_email.setText(rememberMePref.getString(PREF_EMAIL, null));
            String tempPass = rememberMePref.getString(PREF_PASSWORD, null);
            try {
                deHashPassword = PasswordHash.decrypt(tempPass);
            } catch (Exception e) {
                e.printStackTrace();
            }
            // Toast.makeText(login_user.this, deHashPassword, Toast.LENGTH_LONG).show();
            l_password.setText(deHashPassword);
            rememberMeCB1.setChecked(true);

        }
       else{
           rellay.setVisibility(View.VISIBLE);

       }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.goto_registerBtn:
                Intent i = new Intent(login_user.this, register_user.class);
                startActivity(i);
                finish();
               /* progressBar.setVisibility(View.VISIBLE);

                rellay.setVisibility(View.GONE);
                handler.postDelayed(loginToRegister, 2000);*/
                break;
            case R.id.loginBtn:
                if (l_email.getText().length() == 0 || l_password.getText().length() == 0) {
                    Toast.makeText(getApplicationContext(), "Please enter your email & password", Toast.LENGTH_LONG).show();
                } else {
                    progressBar.setVisibility(View.VISIBLE);

                    rellay.setVisibility(View.GONE);
                    handler.postDelayed(loginProcess, 2000);

                }
                break;

        }
    }
}
