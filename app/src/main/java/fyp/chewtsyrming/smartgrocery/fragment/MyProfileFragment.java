package fyp.chewtsyrming.smartgrocery.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import net.glxn.qrgen.android.QRCode;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fyp.chewtsyrming.smartgrocery.FirebaseHandler;
import fyp.chewtsyrming.smartgrocery.FragmentHandler;
import fyp.chewtsyrming.smartgrocery.R;
import fyp.chewtsyrming.smartgrocery.SplashActivity;
import fyp.chewtsyrming.smartgrocery.object.UserModel;


public class MyProfileFragment extends Fragment implements View.OnClickListener {
    public static final String PREFS_NAME = "MyPrefsFile";
    private static final String PREF_EMAIL = "username";
    private static final String PREF_PASSWORD = "password";
    private static final String PREF_REMEMBER = "remember_me";
    Boolean pictureChanged = false;
    FragmentHandler h = new FragmentHandler();
    UserModel um;
    Context context;
    ImageButton ibGallery, ibCamera;
    FirebaseHandler fh = new FirebaseHandler();
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference imagesRef;
    private LinearLayout ll_myQrCode, ll_scanQrCode, ll_edit, ll_camera, rl_resetPass, ll_mainButton, ll_button;
    private Button btn_logout, btn_editProfile, btn_resetPw, btn_SaveProfile, btn_cancelProfile;
    private RelativeLayout rl1, rlProfile;
    private String textMyProfile = "My Profile", textEditProfile = "Edit Profile", textResetPassword = "Reset Password";
    private Boolean resetLayoutStatus = false, editStatus = false;
    private int REQUEST_IMAGE_CAPTURE = 1;
    @Nullable
    private DatabaseReference dbRef, goodsRef, followRef;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private String userId = user.getUid(), imageFilePath;
    private TextView tv_title, tvName, tvEmail, tvFollowingCount, tvFollowerCount, tvInventoryCount;
    private EditText etName, etEmail, etNewPassword, etOldPassword;
    private RelativeLayout rellay_profile;
    private ProgressBar progressBar_profile, pb_resetPass;
    private ImageView ivUserProfile, ivName, ivEmail;
    private Uri imageURI;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View fragmentView = inflater.inflate(R.layout.fragment_my_profile, null);
        context = getContext();
        //just change the fragment_dashboard
        //with the fragment you want to inflate
        //like if the class is HomeFragment it should have R.layout.home_fragment
        //if it is DashboardFragment it should have R.layout.fragment_dashboard
        etName = fragmentView.findViewById(R.id.etName);
        etEmail = fragmentView.findViewById(R.id.etEmail);
        tvFollowingCount = fragmentView.findViewById(R.id.tvFollowingCount);
        tvFollowerCount = fragmentView.findViewById(R.id.tvFollowerCount);
        tvInventoryCount = fragmentView.findViewById(R.id.tvInventoryCount);
        ivUserProfile = fragmentView.findViewById(R.id.ivUserProfile);
        btn_logout = fragmentView.findViewById(R.id.btn_logout);
        rellay_profile = fragmentView.findViewById(R.id.rellay_profile);
        progressBar_profile = fragmentView.findViewById(R.id.progressBar_profile);
        ll_scanQrCode = fragmentView.findViewById(R.id.ll_scanQrCode);
        ll_myQrCode = fragmentView.findViewById(R.id.ll_myQrCode);
        ll_button = fragmentView.findViewById(R.id.ll_button);
        btn_editProfile = fragmentView.findViewById(R.id.btn_editProfile);
        btn_resetPw = fragmentView.findViewById(R.id.btn_resetPw);
        btn_cancelProfile = fragmentView.findViewById(R.id.btn_cancelProfile);
        btn_SaveProfile = fragmentView.findViewById(R.id.btn_SaveProfile);
        ll_camera = fragmentView.findViewById(R.id.ll_camera);
        ivName = fragmentView.findViewById(R.id.ivName);
        ivEmail = fragmentView.findViewById(R.id.ivEmail);
        ibGallery = fragmentView.findViewById(R.id.ibGallery);
        ibCamera = fragmentView.findViewById(R.id.ibCamera);
        rl1 = fragmentView.findViewById(R.id.rl1);
        rl_resetPass = fragmentView.findViewById(R.id.rl_resetPass);
        ll_mainButton = fragmentView.findViewById(R.id.ll_mainButton);
        tv_title = fragmentView.findViewById(R.id.tv_title);
        rlProfile = fragmentView.findViewById(R.id.rlProfile);
        etNewPassword = fragmentView.findViewById(R.id.etNewPassword);
        etOldPassword = fragmentView.findViewById(R.id.etOldPassword);
        pb_resetPass = fragmentView.findViewById(R.id.pb_resetPass);

        ll_myQrCode.setOnClickListener(this);
        ll_scanQrCode.setOnClickListener(this);
        btn_logout.setOnClickListener(this);
        btn_editProfile.setOnClickListener(this);
        btn_resetPw.setOnClickListener(this);
        btn_cancelProfile.setOnClickListener(this);
        btn_SaveProfile.setOnClickListener(this);
        ibGallery.setOnClickListener(this);
        ibCamera.setOnClickListener(this);

        getUserInfo();
        return fragmentView;
    }

    @Override
    public void onClick(final View view) {
        switch (view.getId()) {
            case R.id.ibCamera:
                takePicture();
                break;
            case R.id.ibGallery:
                upload();
                break;
            case R.id.btn_SaveProfile:
                if (resetLayoutStatus) {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                            context);
                    // set title
                    alertDialogBuilder.setTitle("Reset Password");
                    // set dialog message
                    alertDialogBuilder
                            .setMessage("Your password will be reset and you will be log out. Press yes to continue. ")
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // if this button is clicked, close
                                    // current activity
                                    resetPW();
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // if this button is clicked, just close
                                    // the dialog box and do nothing
                                    dialog.cancel();
                                }
                            });

                    // create alert dialog
                    AlertDialog alertDialog = alertDialogBuilder.create();

                    // show it
                    alertDialog.show();
                } else if (editStatus) {
                    saveProfile();

                }
                break;
            case R.id.btn_editProfile:

                editStatus = !editStatus;
                setET(editStatus);


                break;
            case R.id.btn_cancelProfile:
                Boolean allCancel = false;
                loadResetLayout(allCancel);
                setET(allCancel);
                break;

            case R.id.btn_resetPw:
                resetLayoutStatus = !resetLayoutStatus;
                loadResetLayout(resetLayoutStatus);


                break;

            case R.id.ll_myQrCode:
                btn_showMessage(getView());
                break;
            case R.id.btn_logout:
                um.setPlayerId("notAvailable");
                fh.getUserRef().child("profile").setValue(um);
                FirebaseAuth.getInstance().signOut();

                Intent i = new Intent(getActivity(),
                        SplashActivity.class);

                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                        Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
                break;
            case R.id.ll_scanQrCode:
                Bundle bundle = new Bundle();
                bundle.putString("message", "Scan QR");
                bundle.putString("code", "9003");//9003 indicate add goods
                Fragment fragment = null;
                fragment = new BarcodeReaderFragment();
                fragment.setArguments(bundle);
                h.loadFragment(fragment, getContext());

                break;

        }

    }

    private void saveProfile() {
        final String newEmail = etEmail.getText().toString();
        final String newName = etName.getText().toString();
        Pattern pattern = Pattern.compile("[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}");
        Matcher mat = pattern.matcher(newEmail);
        String message = "";
        if (newName.isEmpty()) {
            message = "Please enter your new name.";
            if (newEmail.isEmpty()) {
                message = "Please enter your new name and new address.";

            }
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();

        } else if (newEmail.isEmpty()) {
            Toast.makeText(context, "Please enter your new email address", Toast.LENGTH_SHORT).show();
        } else if (!mat.matches()) {

            Toast.makeText(context, "Not a valid email address", Toast.LENGTH_SHORT).show();

        } else {

            final Dialog dialog = new Dialog(getActivity());
            dialog.setCancelable(true);

            View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_edit_profile, null);
            dialog.setContentView(view);
            Button btn_cancel = view.findViewById(R.id.btn_cancel);
            Button btn_Save = view.findViewById(R.id.btn_Save);
            final LinearLayout ll_button = view.findViewById(R.id.ll_button);
            final ProgressBar pb_editProfile = view.findViewById(R.id.pb_editProfile);
            final EditText etTest = view.findViewById(R.id.etConfirmPassword);

            btn_Save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ll_button.setVisibility(View.GONE);
                    pb_editProfile.setVisibility(View.VISIBLE);
                    final String pwConfirm = etTest.getText().toString();


                    final FirebaseUser user;
                    user = FirebaseAuth.getInstance().getCurrentUser();
                    if (user != null) {
                        AuthCredential credential = EmailAuthProvider
                                .getCredential(um.getEmail(), pwConfirm);
                        user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    user.updateEmail(newEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                //Toast.makeText(context, "URl:"+ imageURI, Toast.LENGTH_LONG).show();
                                                imagesRef = storage.getReference().child("user").child(um.getUid());
                                                if (pictureChanged) {
                                                    imagesRef.putFile(imageURI).
                                                            addOnCompleteListener(
                                                                    new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                                                            if (task.isSuccessful()) {

                                                                                imagesRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                                                                    @Override
                                                                                    public void onComplete(@NonNull Task<Uri> task) {
                                                                                        final String downloadUrl = task.getResult().toString();
                                                                                        um.setEmail(newEmail);
                                                                                        um.setName(newName);
                                                                                        um.setImageurl(downloadUrl);
                                                                                        // Toast.makeText(context, downloadUrl, Toast.LENGTH_LONG).show();

                                                                                        fh.getUserRef().child("profile").setValue(um).
                                                                                                addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                    @Override
                                                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                                                        Toast.makeText(context, "Success.User profile updated!", Toast.LENGTH_LONG).show();
                                                                                                        editStatus = !editStatus;
                                                                                                        setET(editStatus);
                                                                                                        dialog.dismiss();
                                                                                                    }
                                                                                                });

                                                                                    }
                                                                                });
                                                                            }

                                                                        }
                                                                    });

                                                } else {

                                                    um.setEmail(newEmail);
                                                    um.setName(newName);

                                                    fh.getUserRef().child("profile").setValue(um).
                                                            addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    Toast.makeText(context, "Success.User profile updated!", Toast.LENGTH_LONG).show();
                                                                    editStatus = !editStatus;
                                                                    setET(editStatus);
                                                                    dialog.dismiss();
                                                                }
                                                            });
                                                }

                                            } else {

                                                Toast.makeText(context, "Profile update failed. Pleas try again.", Toast.LENGTH_LONG).show();
                                                editStatus = !editStatus;
                                                setET(editStatus);
                                                dialog.dismiss();
                                            }
                                        }
                                    });
                                }


                            }
                        });

                    }
                }
            });
            btn_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

            dialog.show();
        }


    }

    private void loadResetLayout(Boolean resetLayoutStatus) {
        rlProfile.setVisibility(resetLayoutStatus ? View.GONE : View.VISIBLE);
        rl_resetPass.setVisibility(resetLayoutStatus ? View.VISIBLE : View.GONE);
        ll_button.setVisibility(resetLayoutStatus ? View.VISIBLE : View.GONE);
        tv_title.setText(resetLayoutStatus ? textResetPassword : textMyProfile);
        ll_mainButton.setVisibility(resetLayoutStatus ? View.GONE : View.VISIBLE);
        editStatus = false;
    }

    private void setET(Boolean editStatus) {
        ll_button.setVisibility(editStatus ? View.VISIBLE : View.GONE);
        resetLayoutStatus = false;
        etEmail.setFocusable(editStatus);
        etEmail.setFocusableInTouchMode(editStatus);
        etEmail.setEnabled(editStatus);
        etName.setFocusable(editStatus);
        etName.setFocusableInTouchMode(editStatus);
        etName.setEnabled(editStatus);
        if (!editStatus) {
            etName.setText(um.getName());
            etEmail.setText(um.getEmail());
            Glide.with(Objects.requireNonNull(getContext())).load(um.getImageurl()).into(ivUserProfile);

        }
        ll_camera.setVisibility(editStatus ? View.VISIBLE : View.GONE);
        ivEmail.setVisibility(editStatus ? View.VISIBLE : View.GONE);
        ivName.setVisibility(editStatus ? View.VISIBLE : View.GONE);
        ll_mainButton.setVisibility(editStatus ? View.GONE : View.VISIBLE);
        tv_title.setText(editStatus ? textEditProfile : textMyProfile);

    }

    private void getUserInfo() {
        followRef = database.getReference().child("user").child(userId).child("sharedInventory");

        followRef.addValueEventListener(new ValueEventListener() {
            int followerCount = 0;
            int followingCount = 0;

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    if (snapshot.getKey().equals("follower")) {
                        for (DataSnapshot s : snapshot.getChildren()) {
                            if (s.getValue().toString().equals("true")) {
                                followerCount++;

                            }

                        }
                    }
                    if (snapshot.getKey().equals("following")) {
                        for (DataSnapshot s : snapshot.getChildren()) {
                            if (s.getValue().toString().equals("true")) {
                                //   Toast.makeText(getContext(), s.getValue().toString(), Toast.LENGTH_SHORT).show();

                                followingCount++;
                            }

                        }
                    }
                }
                tvFollowerCount.setText(followerCount + "");
                tvFollowingCount.setText(followingCount + "");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //  Toast.makeText(getContext(), "user is single.", Toast.LENGTH_SHORT).show();

            }
        });
        goodsRef = database.getReference().child("user").child(userId).child("goods");
        goodsRef.addValueEventListener(new ValueEventListener() {
            int inventoryCount = 0;

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (final DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if (!snapshot.getKey().equals("fav")) {
                        if (!snapshot.getKey().equals("recent")) {
                            //  Toast.makeText(getContext(), snapshot.getKey(), Toast.LENGTH_SHORT).show();

                            for (DataSnapshot snap : snapshot.getChildren()) {

                                inventoryCount++;

                            }
                        }
                    }

                }
                tvInventoryCount.setText(inventoryCount + "");
                // Toast.makeText(getContext(), Integer.toString(inventoryCount), Toast.LENGTH_SHORT).show();
                progressBar_profile.setVisibility(View.GONE);
                rellay_profile.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        dbRef = database.getReference().child("user").child(userId).child("profile");
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                um = dataSnapshot.getValue(UserModel.class);
                etName.setText(um.getName());
                etEmail.setText(um.getEmail());
                Glide.with(getContext()).load(um.getImageurl()).into(ivUserProfile);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void btn_showMessage(View view) {
        final AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
        View mView = getLayoutInflater().inflate(R.layout.qr_code_dialog, null);
        ImageView ivQrCode = mView.findViewById(R.id.ivQrCode);
        Button btn_done = mView.findViewById(R.id.btn_done);
        Bitmap myBitmap = QRCode.from(userId).bitmap();
        ivQrCode.setImageBitmap(myBitmap);
        alert.setView(mView);
        final AlertDialog alertDialog = alert.create();
        alertDialog.setCanceledOnTouchOutside(false);
        btn_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        alertDialog.show();
    }

    private void resetPW() {

        String oldPW = etOldPassword.getText().toString();
        final String newPW = etNewPassword.getText().toString();
        String errorMessage = "";
        if (oldPW.isEmpty()) {
            errorMessage = "Please enter your old password";
            if (newPW.isEmpty()) {
                errorMessage = "Please enter your old and new password";
            }
            Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show();

        } else if (newPW.isEmpty()) {
            errorMessage = "Please enter your new password";
            Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show();

        } else {
            pb_resetPass.setVisibility(View.VISIBLE);
            ll_button.setVisibility(View.GONE);
            AuthCredential credential = EmailAuthProvider.getCredential(um.getEmail(), oldPW);
            final FirebaseUser user;
            user = FirebaseAuth.getInstance().getCurrentUser();
            user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {

                        user.updatePassword(newPW).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(context, "Password update succesful!", Toast.LENGTH_LONG).show();

                                } else {
                                    Toast.makeText(context, "Password update fail!", Toast.LENGTH_LONG).show();

                                }

                            }
                        });
                    } else {
                        Toast.makeText(context, "Incorrect old password. Please try again", Toast.LENGTH_LONG).show();

                    }
                    resetLayoutStatus = !resetLayoutStatus;
                    loadResetLayout(resetLayoutStatus);

                }
            });
        }


    }

    private void takePicture() {


        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            //Create a file to store the image
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File

            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(getActivity(), "fyp.chewtsyrming.smartgrocery", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        photoURI);
                startActivityForResult(takePictureIntent,
                        REQUEST_IMAGE_CAPTURE);
            }

        }

    }

    private File createImageFile() throws IOException {
        String timeStamp =
                new SimpleDateFormat("yyyyMMdd_HHmmss",
                        Locale.getDefault()).format(new Date());
        String imageFileName = "IMG_" + timeStamp + "_";
        File storageDir =
                getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        imageFilePath = image.getAbsolutePath();
        imageURI = Uri.fromFile(new File(imageFilePath));
//Toast.makeText(getContext(), String.valueOf(imageURI), Toast.LENGTH_SHORT).show();

        return image;
    }

    private void upload() {
        Intent i = new Intent(Intent.ACTION_PICK);
        i.setType(MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(i, 10);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            pictureChanged = true;
//           Bundle extras = data.getExtras();
            //          Bitmap imageBitmap = (Bitmap) extras.get("data");
            //imgGoods.setImageBitmap(imageBitmap);
            Glide.with(context)
                    .load(imageFilePath)
                    .centerCrop()
                    .placeholder(R.drawable.ic_loading_static)
                    .dontAnimate()
                    .into(ivUserProfile);
            //imageURI = data.getData();
            //imgGoods.setImageURI(Uri.fromFile(new File(imageFilePath)));
            //  imageURI = Uri.fromFile(new File(imageFilePath));
            // imagesRef.child(scanned_barcode).putFile(imageURI);
            // Toast.makeText(getContext(), imageURI.toString(), Toast.LENGTH_LONG).show();

        }

        if (requestCode == 10 && resultCode == Activity.RESULT_OK) {
            pictureChanged = true;
            // Toast.makeText(getContext(), String.valueOf(resultCode), Toast.LENGTH_LONG).show();
            //Toast.makeText(getContext(), String.valueOf(CommonStatusCodes.SUCCESS), Toast.LENGTH_LONG).show();
            ivUserProfile.setImageURI(data.getData());
            imageURI = data.getData();

            //Toast.makeText(getContext(), String.valueOf(imageURI), Toast.LENGTH_LONG).show();

        }

    }

}
