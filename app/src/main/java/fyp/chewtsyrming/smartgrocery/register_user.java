package fyp.chewtsyrming.smartgrocery;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Pattern;

import fyp.chewtsyrming.smartgrocery.object.UserModel;

public class register_user extends AppCompatActivity {
    Uri imageuri;
    String url = "https://firebasestorage.googleapis.com/v0/b/gogreen-3de65.appspot.com/o/users%2FARAL0T5yu2evBzMzj9yReB4bkfJ3?alt=media&token=02ef3058-11f1-4e25-bf33-bb4e90df51dc";
    DatabaseReference reff;
    private Button goto_loginBtn, registerBtn;
    private EditText reg_name;
    private EditText reg_email;
    private EditText reg_pass;

    // Uri defaultURL =Uri.parse("https://firebasestorage.googleapis.com/v0/b/new-loginregister.appspot.com/o/users%2Fprofile.jpg?alt=media&token=6923468f-092d-4536-a7fc-fbccb8b66bda");
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);
        reg_email = findViewById(R.id.r_uemail);
        reg_pass = findViewById(R.id.r_upassword);
        goto_loginBtn = findViewById(R.id.goto_loginBtn);
        registerBtn = findViewById(R.id.registerBtn);
        dialog = new ProgressDialog(this);
        reff = FirebaseDatabase.getInstance().getReference().child("users");
        reg_name = findViewById(R.id.r_uname);
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean status = true;
                boolean emailStat = false, passStat = false, nameStat = false;
                String email = reg_email.getText().toString();
                String pass = reg_pass.getText().toString();
                String name = reg_name.getText().toString();
                String message = "";
                if (email.isEmpty()) {
                    status=false;
                    message = "email.";
                    if (pass.isEmpty()) {
                        message = "email and password.";
                        if (name.isEmpty()) {
                            message = "email, password and name.";

                        }

                    }
                }
                if (pass.isEmpty()) {
                    status=false;
                    if (message.isEmpty()) {
                        message = "password.";
                        if (name.isEmpty()) {
                            message = " password and name.";

                        }
                    }
                }
                if (name.isEmpty()) {
                    status=false;
                    if (message.isEmpty()) {
                        message = "name.";
                    }
                }


                if (status) {
                    dialog.setMessage("Please wait a while...");
                    dialog.show();
                    Runnable r = new Runnable() {
                        @Override
                        public void run() {
                            signup();
                        }
                    };
                    Handler h = new Handler();
                    h.postDelayed(r, 1000);
                }
                else{
                    Toast.makeText(getApplicationContext(),"Please enter your "+ message,Toast.LENGTH_LONG).show();
                }


            }
        });


        goto_loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(register_user.this, login_user.class);
                startActivity(i);
                finish();
            }
        });
    }


    private void signup() {
        if(UserModel.isValidEmail(reg_email.getText().toString())) {
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(
                    reg_email.getText().toString(), reg_pass.getText().toString()).
                    addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {


                            if (task.isSuccessful()) {

                                final String c_uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

                                String defaultImageUrl = "https://firebasestorage.googleapis.com/v0/b/gogreen-3de65.appspot.com/o/profile.jpeg?alt=media&token=04b39c44-d9db-4e2b-900a-33f339e1f5b7";
                                UserModel userModel = new UserModel();
                                userModel.name = reg_name.getText().toString();
                                userModel.email = reg_email.getText().toString();
                                userModel.uid = c_uid;
                                userModel.imageurl = defaultImageUrl;

                                FirebaseDatabase.getInstance().getReference().child("user").
                                        child(c_uid).child("profile").setValue(userModel);

                                Toast.makeText(getApplicationContext(), "Register Success", Toast.LENGTH_LONG).show();

                                Intent i = new Intent(getApplicationContext(), login_user.class);
                                dialog.cancel();
                                startActivity(i);


                            } else {
                                String TAG = "register";
                                String message = "Error! Please try again";
                                try {
                                    throw task.getException();
                                }
                                // if user enters wrong email.
                                catch (FirebaseAuthInvalidUserException invalidEmail) {
                                    Log.d(TAG, "onComplete: invalid_email");
                                    message = "Error! Invalid email.";
                                    // TODO: take your actions!
                                }
                                // if user enters wrong password.
                                catch (FirebaseAuthInvalidCredentialsException wrongPassword) {
                                    Log.d(TAG, "onComplete: user exist");
                                    message = "User already exist!";
                                    // TODO: Take your action
                                } catch (Exception e) {
                                    message = e.getMessage();
                                    Log.d(TAG, "onComplete: " + e.getMessage());
                                }
                                dialog.cancel();
                                Toast.makeText(register_user.this, message, Toast.LENGTH_LONG).show();
                            }

                        }


                    });

        }
        else{
            Toast.makeText(getApplicationContext(), "Invalid email!",Toast.LENGTH_LONG).show();
            dialog.cancel();

        }

    }

   /* public static boolean isValidEmail(String email)
    {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\."+
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";

        Pattern pat = Pattern.compile(emailRegex);
        if (email == null)
            return false;
        return pat.matcher(email).matches();
    }*/
}