package fyp.chewtsyrming.smartgrocery;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPasswordActivity extends AppCompatActivity implements View.OnClickListener {
    Handler handler = new Handler();
    ProgressBar clpb;
    LinearLayout ll_main;
    private EditText et_email;
    Runnable loginProcess = new Runnable() {
        @Override
        public void run() {
            clpb.setVisibility(View.VISIBLE);
            String email = et_email.getText().toString();
            ll_main.setVisibility(View.GONE);
            sendEmailResetPW(email);


        }
    };
    private Button buttonSendEmail, buttonCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        et_email = findViewById(R.id.et_email);
        buttonSendEmail = findViewById(R.id.buttonSendEmail);
        buttonCancel = findViewById(R.id.buttonCancel);
        clpb = findViewById(R.id.clpb);
        ll_main = findViewById(R.id.ll_main);
        buttonSendEmail.setOnClickListener(this);
        buttonCancel.setOnClickListener(this);


    }


    public void sendEmailResetPW(String email) {


        FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "Email sent!", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "Unable to sent email", Toast.LENGTH_LONG).show();

                        }
                        clpb.setVisibility(View.GONE);
                        ll_main.setVisibility(View.VISIBLE);

                    }
                });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.buttonSendEmail:
                handler.postDelayed(loginProcess, 1000);


                break;
            case R.id.buttonCancel:
                Intent i = new Intent(ResetPasswordActivity.this, login_user.class);
                startActivity(i);
                finish();
                break;
        }
    }
}
