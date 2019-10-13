package fyp.chewtsyrming.smartgrocery;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import fyp.chewtsyrming.smartgrocery.object.UserModel;

public class register_user extends AppCompatActivity {
    Uri imageuri;
    String url = "https://firebasestorage.googleapis.com/v0/b/gogreen-3de65.appspot.com/o/users%2FARAL0T5yu2evBzMzj9yReB4bkfJ3?alt=media&token=02ef3058-11f1-4e25-bf33-bb4e90df51dc";
    DatabaseReference reff;
    private Button btnReg;
    private Button btnLog;
    private EditText reg_name;
    private EditText reg_email;
    private EditText reg_pass;
    private ImageView profile;

    // Uri defaultURL =Uri.parse("https://firebasestorage.googleapis.com/v0/b/new-loginregister.appspot.com/o/users%2Fprofile.jpg?alt=media&token=6923468f-092d-4536-a7fc-fbccb8b66bda");
    private ProgressDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);
        reg_email = (EditText) findViewById(R.id.r_uemail);
        reg_pass = (EditText) findViewById(R.id.r_upassword);
        btnLog = (Button) findViewById(R.id.r_back);
        btnReg = (Button) findViewById(R.id.r_register);
        profile = (ImageView) findViewById(R.id.img_profile);
        dialog = new ProgressDialog(this);
        reff = FirebaseDatabase.getInstance().getReference().child("users");
        reg_name = (EditText) findViewById(R.id.r_uname);
        btnReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.setMessage("Please wait a while...");
                dialog.show();
                Runnable r = new Runnable() {
                    @Override
                    public void run() {
                        dialog.cancel();
                    }
                };
                Handler h = new Handler();
                h.postDelayed(r, 6000);
                signup();
            }
        });
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                upload();
            }
        });

        btnLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(register_user.this, login_user.class);
                startActivity(i);
                finish();
            }
        });
    }
    private void upload() {
        Intent i = new Intent(Intent.ACTION_PICK);
        i.setType(MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(i, 10);

    }
    private void signup() {

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(reg_email.getText().toString(), reg_pass.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {


                if (task.isSuccessful()) {

                    final String c_uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

                    final StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("users").child(c_uid);


                    if (imageuri == null) {
                        String defaultImageUrl = "https://firebasestorage.googleapis.com/v0/b/gogreen-3de65.appspot.com/o/profile.jpeg?alt=media&token=04b39c44-d9db-4e2b-900a-33f339e1f5b7";
                        UserModel userModel = new UserModel();
                        userModel.name = reg_name.getText().toString();
                        userModel.email = reg_email.getText().toString();
                        userModel.uid = c_uid;
                        userModel.imageurl = defaultImageUrl;

                        FirebaseDatabase.getInstance().getReference().child("user").child(c_uid).child("profile").setValue(userModel);

                        Toast.makeText(getApplicationContext(), "Register Success", Toast.LENGTH_LONG).show();

                        Intent i = new Intent(getApplicationContext(), login_user.class);

                        startActivity(i);

                    } else {
                        storageReference.putFile(imageuri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                                if (task.isSuccessful()) {

                                    storageReference.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Uri> task) {

                                            final String downloadUrl = task.getResult().toString();

//                                        String imageurl = task.toString();

                                            UserModel userModel = new UserModel();
                                            userModel.name = reg_name.getText().toString();
                                            userModel.email = reg_email.getText().toString();
                                            userModel.uid = c_uid;
                                            userModel.imageurl = downloadUrl;


                                            FirebaseDatabase.getInstance().getReference().child("user").child(c_uid).child("profile").setValue(userModel);
                                            Toast.makeText(getApplicationContext(), "Register Success", Toast.LENGTH_LONG).show();

                                            Intent i = new Intent(getApplicationContext(), login_user.class);

                                            startActivity(i);

                                        }
                                    });

                                } else {
                                    Toast.makeText(register_user.this, "Error", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }


                } else {

                    Toast.makeText(register_user.this, "Error", Toast.LENGTH_SHORT).show();
                }
            }


        });


    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 10 && resultCode == RESULT_OK) {


            profile.setImageURI(data.getData());
            imageuri = data.getData();

        }
    }


}