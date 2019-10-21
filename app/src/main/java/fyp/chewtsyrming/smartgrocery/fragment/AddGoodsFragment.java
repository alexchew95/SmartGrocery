package fyp.chewtsyrming.smartgrocery.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.bumptech.glide.Glide;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import fyp.chewtsyrming.smartgrocery.DatePickerFragment;
import fyp.chewtsyrming.smartgrocery.R;
import fyp.chewtsyrming.smartgrocery.object.BarcodeGoods;
import fyp.chewtsyrming.smartgrocery.object.RecentGoods;
import fyp.chewtsyrming.smartgrocery.object.SubGoods;
import fyp.chewtsyrming.smartgrocery.ocr.OcrCaptureActivity;

public class AddGoodsFragment extends Fragment {
    String selectedDate;
    EditText tv_goodsName, expirationDate, quantity, dateOfBirthET;
    TextView barcodeTV;
    Spinner spinnerCategory;
    Button buttonAddGoods;
    ImageButton imageButtomScanGoodsName, ibCamera, ibGallery;
    ImageView imgGoods;
    Button addGoodsBtn;
    FirebaseAuth.AuthStateListener aSL;
    DatabaseReference reff, mainReff;
    SubGoods subGoods;
    BarcodeGoods bg;
    Uri imageURI;
    FirebaseStorage storage;
    StorageReference storageRef, imagesRef;
    Boolean barcodeExist = false;
    private static final int RC_OCR_CAPTURE = 9003;
    public static final int REQUEST_CODE = 11;
    int REQUEST_IMAGE_CAPTURE = 1;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String userId = user.getUid();
    ArrayList<RecentGoods> recentGoodsArrayList;
    Bundle barcodeBundle;
    ArrayAdapter<String> adapter;
    View fragmentView;

    @SuppressLint("CutPasteId")
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final String[] goodsCategory = {
                "Fruit & Vegetables",
                "Milk & Cheese",
                "Bread",
                "Water",


        };
        /*TODO add goods with image capture by user*/
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        user = FirebaseAuth.getInstance().getCurrentUser();
        userId = user.getUid();
        fragmentView = inflater.inflate(R.layout.fragment_add_goods, null);
        tv_goodsName = fragmentView.findViewById(R.id.editTextGoodsName);
        expirationDate = fragmentView.findViewById(R.id.editTextExpiryDate);
        quantity = fragmentView.findViewById(R.id.editTextQuantity);
        spinnerCategory = (Spinner) fragmentView.findViewById(R.id.spinnerCategory);
        imgGoods = fragmentView.findViewById(R.id.imgGoods);
        barcodeTV = fragmentView.findViewById(R.id.barcodeTV);
        ibGallery = fragmentView.findViewById(R.id.ibGallery);
        ibCamera = fragmentView.findViewById(R.id.ibCamera);
        mainReff = FirebaseDatabase.getInstance().getReference().child("user").child(userId).child("goods");
        adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, goodsCategory);
        spinnerCategory.setAdapter(adapter);
        barcodeBundle = this.getArguments();

        if (barcodeBundle != null) {
            getBarcodeData();
        }
        //call datepicker
        datePicker();

        ibGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                upload();
            }
        });
        ibCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePicture();
            }
        });

        //imagebutton scan goods Name
        imageButtomScanGoodsName = fragmentView.findViewById(R.id.imageButtonScanGoodsName);
        imageButtomScanGoodsName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), OcrCaptureActivity.class);
                intent.putExtra(OcrCaptureActivity.AutoFocus, true);
                intent.putExtra(OcrCaptureActivity.UseFlash, false);

                startActivityForResult(intent, RC_OCR_CAPTURE);

            }
        });


        addGoodsBtn = fragmentView.findViewById(R.id.buttonAddGoods);
        addGoodsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                addGoods();

            }
        });

        return fragmentView;
    }


    private void getBarcodeData() {
        String scanned_barcode = barcodeBundle.getString("barcode");
        DatabaseReference barCodeRef = FirebaseDatabase.getInstance().getReference().child("barcode").child(scanned_barcode);
        //Query query = barCodeRef.orderByChild("barcode").equalTo(scanned_barcode);//4260109922085
        barCodeRef.addValueEventListener(new ValueEventListener() {
            // barCodeRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot child : snapshot.getChildren()) {
                    List<BarcodeGoods> barcodeGoods = new ArrayList<>();
                    String goodsName = snapshot.child("goodsName").getValue().toString();
                    String goodsCat = snapshot.child("goodsCategory").getValue().toString();
                    String barcode = snapshot.child("barcode").getValue().toString();
                    String imageURL = snapshot.child("imageURL").getValue().toString();
                    Picasso.get().load(imageURL).fit().into(imgGoods);

                    tv_goodsName.setText(goodsName);
                    int spinnerPosition = adapter.getPosition(goodsCat);
                    spinnerCategory.setSelection(spinnerPosition);
                    barcodeTV.setText(barcode);


                    barcodeExist = true;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message

            }
        });
        //  Toast.makeText(getContext(), test, Toast.LENGTH_LONG).show();
    }

    private void datePicker() {
        final FragmentManager fm = ((AppCompatActivity) getActivity()).getSupportFragmentManager();
        // expirationDate = fragmentView.findViewById(R.id.editTextExpiryDate);
        expirationDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // create the datePickerFragment
                AppCompatDialogFragment newFragment = new DatePickerFragment();
                // set the targetFragment to receive the results, specifying the request code
                newFragment.setTargetFragment(AddGoodsFragment.this, REQUEST_CODE);
                // show the datePicker
                newFragment.show(fm, "datePicker");
            }
        });
    }

    private void upload() {
        Intent i = new Intent(Intent.ACTION_PICK);
        i.setType(MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(i, 10);

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


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            String scanned_barcode = barcodeBundle.getString("barcode");

//           Bundle extras = data.getExtras();
            //          Bitmap imageBitmap = (Bitmap) extras.get("data");
            //imgGoods.setImageBitmap(imageBitmap);
            Glide.with(getActivity()).load(imageFilePath).into(imgGoods);
            //imageURI = data.getData();
            //imgGoods.setImageURI(Uri.fromFile(new File(imageFilePath)));
            //  imageURI = Uri.fromFile(new File(imageFilePath));
            // imagesRef.child(scanned_barcode).putFile(imageURI);
            // Toast.makeText(getContext(), imageURI.toString(), Toast.LENGTH_LONG).show();

        }
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            // get date from string
            selectedDate = data.getStringExtra("selectedDate");
            // set the value of the editText
            expirationDate.setText(selectedDate);
        }
        if (requestCode == 10 && resultCode == Activity.RESULT_OK) {
            // Toast.makeText(getContext(), String.valueOf(resultCode), Toast.LENGTH_LONG).show();
            Toast.makeText(getContext(), String.valueOf(CommonStatusCodes.SUCCESS), Toast.LENGTH_LONG).show();
            imgGoods.setImageURI(data.getData());
            imageURI = data.getData();

            Toast.makeText(getContext(), String.valueOf(imageURI), Toast.LENGTH_LONG).show();

        }
        if (requestCode == RC_OCR_CAPTURE) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    String text = data.getStringExtra(OcrCaptureActivity.TextBlockObject);
                    //  Toast.makeText(getContext(), text, Toast.LENGTH_LONG).show();
                    tv_goodsName.setText(text);

                } else {

                }
            } else {

            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    /* public Uri getImageUri(Context inContext, Bitmap inImage) {
         ByteArrayOutputStream bytes = new ByteArrayOutputStream();
         inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
         String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
         return Uri.parse(path);
     }*/
    String imageFilePath;

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
        return image;
    }

    private void addGoods() {

        if (barcodeExist == false) {
            add_newGoods();
        } else {
            add_existingGoods();
        }

    }

    private void add_newGoods() {
        final String scanned_barcode = barcodeBundle.getString("barcode");
        final String category = spinnerCategory.getSelectedItem().toString();
        final String goodsName = tv_goodsName.getText().toString();
        bg = new BarcodeGoods(scanned_barcode, category, goodsName);
        //reff = FirebaseDatabase.getInstance().getReference().child("barcode").child(scanned_barcode);
        // reff.setValue(bg);

        //Toast.makeText(getContext(), imageURI.toString(), Toast.LENGTH_LONG).show();
        imageURI = Uri.fromFile(new File(imageFilePath));
        imagesRef = storageRef.child("goods").child(scanned_barcode);

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
                                            bg = new BarcodeGoods(scanned_barcode, category, goodsName, downloadUrl);
                                            reff = FirebaseDatabase.getInstance().getReference().child("barcode").child(scanned_barcode);
                                            reff.setValue(bg).
                                                    addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                             @Override
                                                                             public void onSuccess(Void aVoid) {
                                                                                 add_existingGoods();

                                                                             }
                                                                         }

                                                    );
                                        }
                                    });
                                }
                            }
                        });
    }

    private void add_existingGoods() {
        final String scanned_barcode = barcodeBundle.getString("barcode");
        String category = spinnerCategory.getSelectedItem().toString();
        reff = FirebaseDatabase.getInstance().getReference().child("user").child(userId).child("goods")
                .child(category).child(scanned_barcode);
        final String goodsId = reff.push().getKey();
        reff = mainReff.child(category).child(scanned_barcode).child(goodsId);
        String quantt = quantity.getText().toString();
        String expirationdate = expirationDate.getText().toString();
        subGoods = new SubGoods(expirationdate, quantt);
        reff.setValue(subGoods).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                mainReff.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                        final DatabaseReference recentReff = mainReff.child("recent");

                        if (dataSnapshot.hasChild("recent")) {
                            recentReff.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    recentGoodsArrayList = new ArrayList<>();
                                    int recentFireBaseSize = (int) dataSnapshot.getChildrenCount();

                                    //max recent size is 10
                                    if (recentFireBaseSize < 10) {
                                        RecentGoods recentGoods = new RecentGoods(scanned_barcode, goodsId);
                                        String recentID = String.valueOf(recentFireBaseSize + 1);
                                        DatabaseReference addRecentReff = recentReff.child(recentID);
                                        addRecentReff.setValue(recentGoods);
                                    } else {
                                        String retrievedRecentID, retrievedBarcode, retrievedGoodsID;
                                        for (final DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                            retrievedRecentID = snapshot.getKey();
                                            retrievedBarcode = snapshot.child("barcode").getValue().toString();
                                            retrievedGoodsID = snapshot.child("goodsID").getValue().toString();
                                            RecentGoods oldRecentGoods = new RecentGoods(retrievedBarcode, retrievedGoodsID, retrievedRecentID);
                                            recentGoodsArrayList.add(oldRecentGoods);
                                        }
                                        for (int x = 0; x < recentGoodsArrayList.size(); x++) {
                                            //x=0=recentID=1
                                            RecentGoods oldRecentGoods = recentGoodsArrayList.get(x);
                                            retrievedRecentID = oldRecentGoods.getRecentID();
                                            retrievedBarcode = oldRecentGoods.getBarcode();
                                            retrievedGoodsID = oldRecentGoods.getGoodsID();
                                            if (Integer.valueOf(retrievedRecentID) != 1) {
                                                int newRecentID = Integer.valueOf(retrievedRecentID) - 1;
                                                RecentGoods newRecentGoods = new RecentGoods(retrievedBarcode, retrievedGoodsID);
                                                DatabaseReference newAddRecentReff = recentReff.child(String.valueOf(newRecentID));
                                                newAddRecentReff.setValue(newRecentGoods);
                                            }
                                        }
                                        RecentGoods recentGoods = new RecentGoods(scanned_barcode, goodsId);
                                        DatabaseReference addRecentReff = recentReff.child("10");
                                        addRecentReff.setValue(recentGoods);

                                    }

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        } else {
                            RecentGoods recentGoods = new RecentGoods(scanned_barcode, goodsId);
                            DatabaseReference addRecentReff = recentReff.child("1");
                            addRecentReff.setValue(recentGoods);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });

        //Toast.makeText(getContext(), userId, Toast.LENGTH_LONG).show();
    }
}