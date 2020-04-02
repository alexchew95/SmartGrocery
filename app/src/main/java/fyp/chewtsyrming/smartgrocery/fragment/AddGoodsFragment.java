package fyp.chewtsyrming.smartgrocery.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
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
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.core.content.FileProvider;
import androidx.core.widget.ContentLoadingProgressBar;
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

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import fyp.chewtsyrming.smartgrocery.DatePickerFragment;
import fyp.chewtsyrming.smartgrocery.R;
import fyp.chewtsyrming.smartgrocery.fragmentHandler;
import fyp.chewtsyrming.smartgrocery.nestedRv.Goods;
import fyp.chewtsyrming.smartgrocery.nestedRv.fragment_home;
import fyp.chewtsyrming.smartgrocery.object.FirebaseHandler;

public class AddGoodsFragment extends Fragment {
    private static final int RC_OCR_CAPTURE = 9003;
    private static final int REQUEST_CODE = 11;
    private Switch switch_reminderStatus;
    private LinearLayout ll_alert_day;
    private String currentDate, imageFilePath;
    /* public Uri getImageUri(Context inContext, Bitmap inImage) {
         ByteArrayOutputStream bytes = new ByteArrayOutputStream();
         inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
         String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
         return Uri.parse(path);
     }*/
    private EditText tv_goodsName, editTextExpiryDate, editTextQuantity, editAlertTextQuantity, goodsLocation;
    private Spinner spinnerCategory, spinnerGoodsLocation;
    private TextView tv_imageURL;
    private ImageView imgGoods;
    private DatabaseReference reff, mainReff;
    private Goods goods;
    private Uri imageURI;
    private StorageReference storageRef, imagesRef;
    private Boolean barcodeExist = false;
    private int REQUEST_IMAGE_CAPTURE = 1;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private String userId = user.getUid();
    private String alertType;
    private Bundle barcodeBundle;
    private ArrayAdapter<String> adapter, adapter2;
    private View fragmentView;
    private List<String> arrStorageLocation;
    private ContentLoadingProgressBar progress_bar_add_goods;

    @SuppressLint("CutPasteId")
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final String[] goodsCategory = {
                "Beverages",
                "Bread or Bakery",
                "Canned or Jarred Goods",
                "Dairy",
                "Dry or Baking Goods",
                "Frozen Foods",
                "Fruit & Vegetables",
                "Meat",
                "Fish",
                "Other",
        };

        /*TODO add goods with image capture by user*/
        FirebaseStorage storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        user = FirebaseAuth.getInstance().getCurrentUser();
        userId = user.getUid();
        fragmentView = inflater.inflate(R.layout.fragment_add_goods, null);
        tv_goodsName = fragmentView.findViewById(R.id.editTextGoodsName);
        tv_imageURL = fragmentView.findViewById(R.id.tv_imageURL);
        editTextExpiryDate = fragmentView.findViewById(R.id.editTextExpiryDate);
        editTextQuantity = fragmentView.findViewById(R.id.editTextQuantity);
        spinnerCategory = fragmentView.findViewById(R.id.spinnerCategory);
        spinnerGoodsLocation = fragmentView.findViewById(R.id.spinnerGoodsLocation);
        imgGoods = fragmentView.findViewById(R.id.imgGoods);
        switch_reminderStatus = fragmentView.findViewById(R.id.switch_reminderStatus);
        ImageButton ibGallery = fragmentView.findViewById(R.id.ibGallery);
        ImageButton ibCamera = fragmentView.findViewById(R.id.ibCamera);
        ImageButton ib_add_storage_location = fragmentView.findViewById(R.id.ib_add_storage_location);
        progress_bar_add_goods = fragmentView.findViewById(R.id.progress_bar_add_goods);
        mainReff = FirebaseDatabase.getInstance().getReference().child("user").child(userId).child("goods");
        adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, goodsCategory);
        spinnerCategory.setAdapter(adapter);
        getGoodsLocation();
        barcodeBundle = this.getArguments();
        ll_alert_day = fragmentView.findViewById(R.id.ll_alert_day);
        editAlertTextQuantity = fragmentView.findViewById(R.id.editAlertTextQuantity);
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        Date date = new Date();
        currentDate = dateFormat.format(date);

        if (barcodeBundle != null) {
            getBarcodeData();
        }

        switch_reminderStatus.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    ll_alert_day.setVisibility(View.VISIBLE);
                } else {
                    ll_alert_day.setVisibility(View.GONE);

                }
            }
        });
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
        ImageButton imageButtomScanGoodsName = fragmentView.findViewById(R.id.imageButtonScanGoodsName);

        ib_add_storage_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btn_add_storage_location();

            }
        });

        Button addGoodsBtn = fragmentView.findViewById(R.id.buttonAddGoods);
        addGoodsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progress_bar_add_goods.show();
                addGoods();

            }
        });

        return fragmentView;
    }

    private void getGoodsLocation() {
        DatabaseReference storageLocationRef = FirebaseDatabase.getInstance().getReference().child("user").child(userId).child("inventoryLocation");

        storageLocationRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {

                    arrStorageLocation = new ArrayList<>();

                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        if (child.getValue(String.class).contains("true")) {
                            arrStorageLocation.add(child.getKey());


                        }
                    }
                    String[] arrayStorageLocation = new String[arrStorageLocation.size()];
                    for (int j = 0; j < arrStorageLocation.size(); j++) {

                        // Assign each value to String array
                        arrayStorageLocation[j] = arrStorageLocation.get(j);
                    }
                    adapter2 = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, arrStorageLocation);
                    spinnerGoodsLocation.setAdapter(adapter2);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getBarcodeData() {

        final String scanned_barcode;
        scanned_barcode = barcodeBundle.getString("barcode");
        final FirebaseHandler fh = new FirebaseHandler();
        assert scanned_barcode != null;
        DatabaseReference barCodeRef = fh.getRef().child("barcode").child(scanned_barcode);
        //Query query = barCodeRef.orderByChild("barcode").equalTo(scanned_barcode);//4260109922085
        barCodeRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    String goodsName = snapshot.child("goodsName").getValue(String.class);
                    String goodsCat = snapshot.child("goodsCategory").getValue(String.class);
                    String barcode = snapshot.child("barcode").getValue(String.class);
                    String imageURL = snapshot.child("imageURL").getValue(String.class);
                    tv_imageURL.setText(imageURL);
                    // Picasso.get().load(imageURL).fit().into(imgGoods);
                    Glide.with(getContext())
                            .load(imageURL)
                            .centerCrop()
                            .placeholder(R.drawable.ic_loading_static)
                            .dontAnimate()
                            .into(imgGoods);
                    tv_goodsName.setText(goodsName);
                    int spinnerPosition = adapter.getPosition(goodsCat);
                    spinnerCategory.setSelection(spinnerPosition);
                    barcodeExist = true;
                    //checked user preference for this goods
                    DatabaseReference userPrefRef = fh.getUserRef().child("goodsData").child(scanned_barcode);
                    userPrefRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.getValue() != null) {
                                String alertData = dataSnapshot.child("alertData").getValue(String.class);
                                String alertStatus = dataSnapshot.child("alertStatus").getValue(String.class);
                                if (alertStatus.contains("enabled")) {
                                    editAlertTextQuantity.setText(alertData);
                                    switch_reminderStatus.setChecked(true);
                                    ll_alert_day.setVisibility(View.VISIBLE);
                                } else {
                                    editAlertTextQuantity.setText(" ");
                                    switch_reminderStatus.setChecked(false);
                                    ll_alert_day.setVisibility(View.GONE);
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                } else {
                    Toast.makeText(getContext(), "Please register this barcode.", Toast.LENGTH_LONG).show();

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message

            }
        });
        //  Toast.makeText(getContext(), test, Toast.LENGTH_LONG).show();
    }

    public void btn_add_storage_location() {
        final AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
        View mView = getLayoutInflater().inflate(R.layout.dialog_box_add_goods_location, null);
        final EditText txt_input_new_storage_location = mView.findViewById(R.id.txt_input_new_storage_location);
        Button btn_cancel = mView.findViewById(R.id.btn_cancel);
        Button btn_okay = mView.findViewById(R.id.btn_okay);
        alert.setView(mView);
        final AlertDialog alertDialog = alert.create();
        alertDialog.setCanceledOnTouchOutside(false);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        btn_okay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newLocation = txt_input_new_storage_location.getText().toString();
                DatabaseReference addStorageReff = FirebaseDatabase.getInstance().getReference().child("user").child(userId).child("inventoryLocation");
                addStorageReff.child(newLocation).setValue("true").addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        getGoodsLocation();
                        alertDialog.dismiss();

                    }
                });
            }
        });
        alertDialog.show();
    }

    private void datePicker() {
        final FragmentManager fm = (getActivity()).getSupportFragmentManager();
        // expirationDate = fragmentView.findViewById(R.id.editTextExpiryDate);
        editTextExpiryDate.setOnClickListener(new View.OnClickListener() {
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
            String selectedDate = data.getStringExtra("selectedDate");
            // set the value of the editText
            editTextExpiryDate.setText(selectedDate);
        }
        if (requestCode == 10 && resultCode == Activity.RESULT_OK) {
            // Toast.makeText(getContext(), String.valueOf(resultCode), Toast.LENGTH_LONG).show();
            Toast.makeText(getContext(), String.valueOf(CommonStatusCodes.SUCCESS), Toast.LENGTH_LONG).show();
            imgGoods.setImageURI(data.getData());
            imageURI = data.getData();

            Toast.makeText(getContext(), String.valueOf(imageURI), Toast.LENGTH_LONG).show();

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
        return image;
    }

    private void addGoods() {

        if (!barcodeExist) {
            add_newGoods();
        } else {
            String imgUrl = null;
            add_existingGoods(imgUrl);
        }

    }

    private void add_newGoods() {


        final String scanned_barcode = barcodeBundle.getString("barcode");
        final String category = spinnerCategory.getSelectedItem().toString();
        final String goodsName = tv_goodsName.getText().toString();
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

                                            goods = new Goods(scanned_barcode, goodsName, downloadUrl, category);
                                            Toast.makeText(getContext(), downloadUrl
                                                    , Toast.LENGTH_LONG).show();
                                            goods.addNewBarcode();
                                            add_existingGoods(downloadUrl);

                                        }
                                    });
                                }
                            }
                        });
    }

    private void add_existingGoods(String downloadURL) {
        final String barcode, goodsId, goodsName, imageURL, goodsCategory, expirationDate,
                quantity, goodsLocation, alertData, alertStatus, insertDate;


        barcode = barcodeBundle.getString("barcode");
        goodsName = tv_goodsName.getText().toString();
        if (downloadURL == null) {
            imageURL = tv_imageURL.getText().toString();
        } else {
            imageURL = downloadURL;
        }
        goodsCategory = spinnerCategory.getSelectedItem().toString();
        expirationDate = editTextExpiryDate.getText().toString();
        quantity = editTextQuantity.getText().toString();
        insertDate = currentDate;
        goodsLocation = spinnerGoodsLocation.getSelectedItem().toString();
        if (switch_reminderStatus.isChecked()) {
            alertStatus = switch_reminderStatus.getTextOn().toString();
            alertData = editAlertTextQuantity.getText().toString();

        } else {
            alertStatus = switch_reminderStatus.getTextOff().toString();
            alertData = switch_reminderStatus.getTextOff().toString();
        }


        reff = FirebaseDatabase.getInstance().getReference().child("user").child(userId).child("goods")
                .child(goodsCategory).child(barcode);
        goodsId = reff.push().getKey();

        reff = FirebaseDatabase.getInstance().getReference().child("user").child(userId).child("goods")
                .child(goodsCategory).child(barcode).child(goodsId);
        Goods good = new Goods(barcode, goodsId, goodsName, imageURL, goodsCategory, expirationDate,
                quantity, goodsLocation, alertData, alertStatus, insertDate);

        good.addGoods(good);
        checkGoodsDataExist(barcode, goodsCategory, goodsLocation, alertData, alertStatus);

        progress_bar_add_goods.hide();
        fragment_home frag = new fragment_home();
        fragmentHandler h = new fragmentHandler();
        h.loadFragment(frag, getContext());
        //Toast.makeText(getContext(), userId, Toast.LENGTH_LONG).show();
    }

    //create new goods data when new product is addded
    public void checkGoodsDataExist(final String barcode, final String goodsCategory,
                                     final String goodsLocation, final String alertData,
                                     final String alertStatus) {
        final DatabaseReference goodsDataRef = FirebaseDatabase.getInstance().getReference().child("user").
                child(userId).child("goodsData").child(barcode);
        goodsDataRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() == null) {
                    String activeDays = "0";

                    String rate = "0.00";
                    String status = "ACTIVE";
                    String totalUsed = "0";
                    Goods goods = new Goods(goodsCategory, goodsLocation, alertData, alertStatus,
                            activeDays, status, totalUsed, rate);

                    goodsDataRef.setValue(goods);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

}