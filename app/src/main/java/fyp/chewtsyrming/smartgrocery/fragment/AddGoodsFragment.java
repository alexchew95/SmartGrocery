package fyp.chewtsyrming.smartgrocery.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.core.widget.ContentLoadingProgressBar;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
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
import fyp.chewtsyrming.smartgrocery.FirebaseHandler;
import fyp.chewtsyrming.smartgrocery.FragmentHandler;
import fyp.chewtsyrming.smartgrocery.R;
import fyp.chewtsyrming.smartgrocery.nestedRv.Goods;
import fyp.chewtsyrming.smartgrocery.nestedRv.fragment_home;

public class AddGoodsFragment extends Fragment {
    private static final int RC_OCR_CAPTURE = 9003;
    private static final int REQUEST_CODE = 11;
    FirebaseHandler fh = new FirebaseHandler();
    Goods g = new Goods();
    private Switch switch_reminderStatus, switch_daysToRemindStatus;
    private LinearLayout ll_alert_day, ll_numberPicker, ll_imageView;
    private String currentDate, imageFilePath;
    private Button button_one, button_two, button_five, button_reset,
            button_ten, button_resetD, button_oneD, button_twoD, button_fiveD, button_tenD;
    /* public Uri getImageUri(Context inContext, Bitmap inImage) {
         ByteArrayOutputStream bytes = new ByteArrayOutputStream();
         inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
         String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
         return Uri.parse(path);
     }*/
    private EditText editTextGoodsName, editTextExpiryDate, editTextQuantity, et_daysToRemind, goodsLocation;
    private Spinner spinnerCategory, spinnerGoodsLocation;
    private TextView tv_imageURL;
    private ImageView imgGoods;
    private DatabaseReference reff, mainReff;
    private Goods goods;
    private Uri imageURI;
    private StorageReference storageRef, imagesRef;
    private Boolean barcodeExist = false;
    private int REQUEST_IMAGE_CAPTURE = 1;
    private String alertType;
    private Bundle barcodeBundle;
    private ArrayAdapter<String> adapter, adapter2;
    private View fragmentView;
    private List<String> arrStorageLocation;
    private ContentLoadingProgressBar progress_bar_add_goods;
    private ScrollView sv_itemDetail;

    public static void btn_add_storage_location(final Context context) {
        final AlertDialog.Builder alert = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View mView = inflater.inflate(R.layout.dialog_box_add_goods_location, null);
        final FirebaseHandler firebaseHandler = new FirebaseHandler();
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
                DatabaseReference addStorageReff = firebaseHandler.getUserRef().child("inventoryLocation");
                addStorageReff.child(newLocation).setValue("true").addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(context, "New location added!", Toast.LENGTH_SHORT).show();
                        alertDialog.dismiss();

                    }

                });
            }
        });
        alertDialog.show();
    }

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

        fragmentView = inflater.inflate(R.layout.fragment_add_goods, null);
        editTextGoodsName = fragmentView.findViewById(R.id.editTextGoodsName);
        tv_imageURL = fragmentView.findViewById(R.id.tv_imageURL);
        editTextExpiryDate = fragmentView.findViewById(R.id.editTextExpiryDate);
        editTextQuantity = fragmentView.findViewById(R.id.editTextQuantity);
        spinnerCategory = fragmentView.findViewById(R.id.spinnerCategory);
        spinnerGoodsLocation = fragmentView.findViewById(R.id.spinnerGoodsLocation);
        imgGoods = fragmentView.findViewById(R.id.imgGoods);
        switch_reminderStatus = fragmentView.findViewById(R.id.switch_reminderStatus);
        switch_daysToRemindStatus = fragmentView.findViewById(R.id.switch_daysToRemindStatus);
        sv_itemDetail = fragmentView.findViewById(R.id.sv_itemDetail);
        ImageButton ibGallery = fragmentView.findViewById(R.id.ibGallery);
        ImageButton ibCamera = fragmentView.findViewById(R.id.ibCamera);
        ImageButton ib_add_storage_location = fragmentView.findViewById(R.id.ib_add_storage_location);
        ImageButton buttonHelp = fragmentView.findViewById(R.id.buttonHelp);
        button_one = fragmentView.findViewById(R.id.button_one);
        button_two = fragmentView.findViewById(R.id.button_two);
        button_five = fragmentView.findViewById(R.id.button_five);
        button_ten = fragmentView.findViewById(R.id.button_ten);
        button_resetD = fragmentView.findViewById(R.id.button_resetD);
        button_oneD = fragmentView.findViewById(R.id.button_oneD);
        button_twoD = fragmentView.findViewById(R.id.button_twoD);
        button_fiveD = fragmentView.findViewById(R.id.button_fiveD);
        button_tenD = fragmentView.findViewById(R.id.button_tenD);
        ll_numberPicker = fragmentView.findViewById(R.id.ll_numberPicker);
        button_reset = fragmentView.findViewById(R.id.button_reset);
        ll_imageView = fragmentView.findViewById(R.id.ll_imageView);

        progress_bar_add_goods = fragmentView.findViewById(R.id.progress_bar_add_goods);
        mainReff = fh.getUserRef().child("goods");
        adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, goodsCategory);
        spinnerCategory.setAdapter(adapter);
        getGoodsLocation();
        barcodeBundle = this.getArguments();
        ll_alert_day = fragmentView.findViewById(R.id.ll_alert_day);
        et_daysToRemind = fragmentView.findViewById(R.id.et_daysToRemind);
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        Date date = new Date();
        currentDate = dateFormat.format(date);

        if (barcodeBundle != null) {
            getBarcodeData();
        }
        button_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editTextQuantity.setText("1");
            }
        });
        button_one.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int currentQuantity = 0;
                if (!editTextQuantity.getText().toString().matches("")) {
                    currentQuantity = Integer.parseInt(editTextQuantity.getText().toString());
                }
                int newQuantity = returnQuantity(1, currentQuantity);
                editTextQuantity.setText(String.valueOf(newQuantity));
            }
        });
        button_two.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int currentQuantity = 0;
                if (!editTextQuantity.getText().toString().matches("")) {
                    currentQuantity = Integer.parseInt(editTextQuantity.getText().toString());
                }
                int newQuantity = returnQuantity(2, currentQuantity);
                editTextQuantity.setText(String.valueOf(newQuantity));

            }
        });
        button_five.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int currentQuantity = 0;
                if (!editTextQuantity.getText().toString().matches("")) {
                    currentQuantity = Integer.parseInt(editTextQuantity.getText().toString());
                }
                int newQuantity = returnQuantity(5, currentQuantity);
                editTextQuantity.setText(String.valueOf(newQuantity));

            }
        });
        button_ten.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int currentQuantity = 0;
                if (!editTextQuantity.getText().toString().matches("")) {
                    currentQuantity = Integer.parseInt(editTextQuantity.getText().toString());
                }
                int newQuantity = returnQuantity(10, currentQuantity);
                editTextQuantity.setText(String.valueOf(newQuantity));
            }
        });
        button_resetD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                et_daysToRemind.setText("1");


            }
        });
        button_oneD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int currentQuantity = 0;
                if (!et_daysToRemind.getText().toString().matches("")) {
                    currentQuantity = Integer.parseInt(et_daysToRemind.getText().toString());
                }
                int newQuantity = returnQuantity(1, currentQuantity);
                et_daysToRemind.setText(String.valueOf(newQuantity));

            }
        });
        button_twoD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int currentQuantity = 0;
                if (!et_daysToRemind.getText().toString().matches("")) {
                    currentQuantity = Integer.parseInt(et_daysToRemind.getText().toString());
                }
                int newQuantity = returnQuantity(2, currentQuantity);
                et_daysToRemind.setText(String.valueOf(newQuantity));

            }
        });
        button_fiveD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int currentQuantity = 0;
                if (!et_daysToRemind.getText().toString().matches("")) {
                    currentQuantity = Integer.parseInt(et_daysToRemind.getText().toString());
                }
                int newQuantity = returnQuantity(5, currentQuantity);
                et_daysToRemind.setText(String.valueOf(newQuantity));

            }
        });
        button_tenD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int currentQuantity = 0;
                if (!et_daysToRemind.getText().toString().matches("")) {
                    currentQuantity = Integer.parseInt(et_daysToRemind.getText().toString());
                }
                int newQuantity = returnQuantity(10, currentQuantity);
                et_daysToRemind.setText(String.valueOf(newQuantity));

            }
        });
        switch_daysToRemindStatus.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    et_daysToRemind.setVisibility(View.VISIBLE);
                    ll_numberPicker.setVisibility(View.VISIBLE);
                } else {
                    et_daysToRemind.setVisibility(View.GONE);
                    ll_numberPicker.setVisibility(View.GONE);

                }
            }
        });
        //call datepicker
        datePicker();
        buttonHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shoeHelpMessageDialog(getContext());
            }
        });
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
                btn_add_storage_location(getContext());

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
        DatabaseReference storageLocationRef = fh.getUserRef().child("inventoryLocation");

        storageLocationRef.addValueEventListener(new ValueEventListener() {
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

    public void shoeHelpMessageDialog(Context c) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(c);
        // set title
        alertDialogBuilder.setTitle("What is consumed rated reminder?");
        String message = "By enabling this function, Home Grocery will monitor this item consumption rate " +
                "and provide advise to the user. For example, user will be reminded when the items is predicted" +
                " to not be able to finish by the time it expired.";
        alertDialogBuilder
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("Close", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // if this button is clicked, close
                        // current activity
                        dialog.cancel();
                    }
                });
        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

    private void getBarcodeData() {
        progress_bar_add_goods.hide();

        final String scanned_barcode;
        scanned_barcode = barcodeBundle.getString("barcode");
        final FirebaseHandler fh = new FirebaseHandler();
        assert scanned_barcode != null;
        DatabaseReference barCodeRef = fh.getRef().child("barcode").child(scanned_barcode);
        //Query query = barCodeRef.orderByChild("barcode").equalTo(scanned_barcode);//4260109922085
        barCodeRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    editTextGoodsName.setClickable(false);
                    editTextGoodsName.setFocusableInTouchMode(false);
                    editTextGoodsName.setFocusable(false);
                    spinnerCategory.setEnabled(false);
                    spinnerCategory.setClickable(false);
                    spinnerCategory.setFocusableInTouchMode(false);
                    spinnerCategory.setFocusable(false);
                    ll_imageView.setVisibility(View.GONE);
                    g = snapshot.getValue(Goods.class);
                    String goodsName = g.getGoodsName();
                    String barcode = g.getBarcode();
                    String imageURL = g.getImageURL();
                    String goodsCat = g.getGoodsCategory();
                    tv_imageURL.setText(imageURL);
                    // Picasso.get().load(imageURL).fit().into(imgGoods);
                    Glide.with(getContext())
                            .load(imageURL)
                            .centerCrop()
                            .placeholder(R.drawable.ic_loading_static)
                            .dontAnimate()
                            .into(imgGoods);
                    editTextGoodsName.setText(goodsName);
                    int spinnerPosition = adapter.getPosition(goodsCat);
                    spinnerCategory.setSelection(spinnerPosition);
                    barcodeExist = true;
                    //checked user preference for this goods
                    DatabaseReference userPrefRef = fh.getUserRef().child("goodsData").child(scanned_barcode);
                    userPrefRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.getValue() != null) {
                                String alertData = dataSnapshot.child("alertData").getValue(String.class);
                                String consumedRateStatus = dataSnapshot.child("consumedRateStatus").getValue(String.class);
                                String alertDaysStatus = dataSnapshot.child("alertDaysStatus").getValue(String.class);
                                String goodsLocation = dataSnapshot.child("goodsLocation").getValue(String.class);
                                //Toast.makeText(getContext(), goodsLocation, Toast.LENGTH_LONG).show();

                                int spinnerPosition = adapter2.getPosition(goodsLocation);
                                spinnerGoodsLocation.setSelection(spinnerPosition);
                                et_daysToRemind.setText(alertData);
                                if (consumedRateStatus.contains("enabled")) {
                                    switch_reminderStatus.setChecked(true);
                                } else {
                                    switch_reminderStatus.setChecked(false);
                                }
                                if (alertDaysStatus.contains("enabled")) {
                                    switch_daysToRemindStatus.setChecked(true);
                                } else {
                                    switch_daysToRemindStatus.setChecked(false);
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

    private void datePicker() {
        final FragmentManager fm = (getActivity()).getSupportFragmentManager();
        // expirationDate = fragmentView.findViewById(R.id.editTextExpiryDate);
        editTextExpiryDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // create the datePickerFragment
                DialogFragment newFragment = new DatePickerFragment();
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
            //Toast.makeText(getContext(), String.valueOf(CommonStatusCodes.SUCCESS), Toast.LENGTH_LONG).show();
            imgGoods.setImageURI(data.getData());
            imageURI = data.getData();

            //   Toast.makeText(getContext(), String.valueOf(imageURI), Toast.LENGTH_LONG).show();

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

        return image;
    }

    private void addGoods() {

        if (!barcodeExist) {
            add_newGoods();
        } else {
            String imgUrl = null;
            add_existingGoods(imgUrl);
        }
        /*progress_bar_add_goods.hide();
        sv_itemDetail.setVisibility(View.VISIBLE);*/

    }

    private void add_newGoods() {
        boolean validationCheck = true;
        String messageError = "";


        final String scanned_barcode = barcodeBundle.getString("barcode");
        final String category = spinnerCategory.getSelectedItem().toString();
        final String goodsName = editTextGoodsName.getText().toString();
        //reff = FirebaseDatabase.getInstance().getReference().child("barcode").child(scanned_barcode);
        // reff.setValue(bg);
        //Toast.makeText(getContext(), imageURI.toString(), Toast.LENGTH_LONG).show();

        imagesRef = storageRef.child("goods").child(scanned_barcode);
        if (goodsName.isEmpty()) {
            validationCheck = false;
            messageError = " Please enter goods name.";
            if (imageURI == null) {
                messageError = "Please enter goods name and select goods image.";
            }
        }
        if (imageURI == null) {
            validationCheck = false;

            if (messageError.isEmpty()) {
                messageError = "Please select goods image.";
            }
        }
        if (validationCheck) {
            progress_bar_add_goods.show();
            sv_itemDetail.setVisibility(View.GONE);

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
                                                // Toast.makeText(getContext(), downloadUrl
                                                //     , Toast.LENGTH_LONG).show();
                                                goods.addNewBarcode();
                                                barcodeExist = true;
                                                Toast.makeText(getContext(), "New barcode registered!", Toast.LENGTH_SHORT).show();
                                                add_existingGoods(downloadUrl);

                                            }
                                        });
                                    } else {
                                        Toast.makeText(getContext(), "Barcode register failed! Please try again.", Toast.LENGTH_SHORT).show();
                                        progress_bar_add_goods.hide();
                                        sv_itemDetail.setVisibility(View.VISIBLE);
                                    }
                                }
                            });
        } else {
            Toast.makeText(getContext(), messageError, Toast.LENGTH_LONG).show();
        }


    }

    private void add_existingGoods(String downloadURL) {
        boolean validateStatus = true;
        String errorMessage = "";
        final String barcode;
        final String goodsId;
        final String goodsName;
        final String imageURL;
        final String goodsCategory;
        final String expirationDate;
        final String quantity;
        final String goodsLocation;
        String alertData;
        final String alertDaysStatus;
        final String consumedRateStatus;
        final String insertDate;
        expirationDate = editTextExpiryDate.getText().toString();
        quantity = editTextQuantity.getText().toString();
        alertData = et_daysToRemind.getText().toString();
        if (!switch_daysToRemindStatus.isChecked()) {
            alertData="1";
        }

        if (expirationDate.isEmpty()) {
            validateStatus = false;
            errorMessage = "Please enter expiration dates.";
            if (quantity.isEmpty()) {
                errorMessage = "Please enter expiration dates and quantity.";
                if (switch_daysToRemindStatus.isChecked()) {
                    if (alertData.isEmpty()) {
                        errorMessage = "Please enter expiration dates ,quantity and days to remind..";

                    }
                }
            }
        }
        if (quantity.isEmpty()) {
            validateStatus = false;
            if (errorMessage.isEmpty()) {
                errorMessage = "Please enter quantity.";
                if (switch_daysToRemindStatus.isChecked()) {

                    if (alertData.isEmpty()) {
                        errorMessage = "Please enter quantity and days to remind..";

                    }
                }
            }
        }
        if (switch_daysToRemindStatus.isChecked()) {
            if (alertData.isEmpty()) {
                validateStatus = false;

                if (errorMessage.isEmpty()) {
                    errorMessage = "Please enter days to remind..";
                }
            }
        }
        if (validateStatus) {
            progress_bar_add_goods.show();
            sv_itemDetail.setVisibility(View.GONE);

            barcode = barcodeBundle.getString("barcode");
            goodsName = editTextGoodsName.getText().toString();
            if (downloadURL == null) {
                imageURL = tv_imageURL.getText().toString();
            } else {
                imageURL = downloadURL;
            }
            goodsCategory = spinnerCategory.getSelectedItem().toString();

            insertDate = currentDate;
            goodsLocation = spinnerGoodsLocation.getSelectedItem().toString();
            if (switch_reminderStatus.isChecked()) {
                consumedRateStatus = switch_reminderStatus.getTextOn().toString();

            } else {
                consumedRateStatus = switch_reminderStatus.getTextOff().toString();
            }
            if (switch_daysToRemindStatus.isChecked()) {
                alertDaysStatus = switch_reminderStatus.getTextOn().toString();

            } else {
                alertDaysStatus = switch_reminderStatus.getTextOff().toString();
            }


            reff = fh.getUserRef().child("goods")
                    .child(goodsCategory).child(barcode);
            goodsId = reff.push().getKey();

            reff = fh.getUserRef().child("goods")
                    .child(goodsCategory).child(barcode).child(goodsId);
            Goods good = new Goods(barcode, goodsId, goodsName, imageURL, goodsCategory, expirationDate,
                    quantity, goodsLocation, alertData, consumedRateStatus, alertDaysStatus, insertDate
                    , "disabled", "1", "disabled", "1");

            good.addGoods(good, getContext());
            checkGoodsDataExist(barcode, goodsCategory, goodsLocation,
                    alertData, consumedRateStatus, alertDaysStatus, getContext(), "addGoods");

        } else {
            progress_bar_add_goods.hide();
            sv_itemDetail.setVisibility(View.VISIBLE);
            Toast.makeText(getContext(), errorMessage, Toast.LENGTH_LONG).show();
        }

        progress_bar_add_goods.hide();

        //Toast.makeText(getContext(), userId, Toast.LENGTH_LONG).show();
    }

    //create new goods data when new product is addded
    public void checkGoodsDataExist(final String barcode, final String goodsCategory,
                                    final String goodsLocation, final String alertData, final String consumedRateStatus,
                                    final String alertDaysStatus, final Context context,
                                    final String sourceFragment) {

        final DatabaseReference goodsDataRef = fh.getUserRef().child("goodsData").child(barcode);
        goodsDataRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                if (dataSnapshot.getValue() == null) {
                    if (sourceFragment.contains("addGoods")) {
                        progress_bar_add_goods.show();
                        sv_itemDetail.setVisibility(View.GONE);
                    }
                    String activeDays = "1";

                    String rate = "0.00";
                    String status = "ACTIVE";
                    String totalUsed = "0";
                    Goods goods = new Goods(goodsCategory, goodsLocation, alertData, consumedRateStatus, alertDaysStatus,
                            activeDays, status, totalUsed, rate);

                    goodsDataRef.setValue(goods).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (sourceFragment.contains("addGoods")) {
                                progress_bar_add_goods.hide();
                                sv_itemDetail.setVisibility(View.VISIBLE);
                            }
                        }
                    });
                } else {
                    if (sourceFragment.contains("addGoods")) {
                        progress_bar_add_goods.hide();
                        sv_itemDetail.setVisibility(View.VISIBLE);
                    }
                }
                if (sourceFragment.contains("addGoods")) {
                    fragment_home frag = new fragment_home();
                    FragmentHandler.loadFragment(frag, context);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public int returnQuantity(int toAddQuantity, int currentQuantity) {
        return toAddQuantity + currentQuantity;
    }
}