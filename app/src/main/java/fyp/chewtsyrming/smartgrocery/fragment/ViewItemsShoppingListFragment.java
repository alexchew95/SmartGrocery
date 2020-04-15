package fyp.chewtsyrming.smartgrocery.fragment;

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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.core.widget.ContentLoadingProgressBar;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.gms.common.api.CommonStatusCodes;
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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import fyp.chewtsyrming.smartgrocery.FirebaseHandler;
import fyp.chewtsyrming.smartgrocery.FragmentHandler;
import fyp.chewtsyrming.smartgrocery.R;
import fyp.chewtsyrming.smartgrocery.nestedRv.Goods;
import fyp.chewtsyrming.smartgrocery.object.ShoppingPlanItem;
import fyp.chewtsyrming.smartgrocery.object.UserModel;

public class ViewItemsShoppingListFragment extends Fragment implements View.OnClickListener {
    private static final int RC_OCR_CAPTURE = 9003;
    private static final int REQUEST_CODE = 11;
    FragmentHandler h = new FragmentHandler();
    UserModel um = new UserModel();
    FirebaseHandler fb = new FirebaseHandler();
    String name, quantity;
    Button button_add_to_shop_list;
    String imageFilePath;
    private ImageView imgGoods;
    private ArrayAdapter<String> adapter;
    private EditText editTextGoodsName, editTextQuantity;
    private Spinner spinnerCategory;
    private Boolean barcodeExist = false;
    private ContentLoadingProgressBar pb_item_status;
    private RelativeLayout rl_goods_info;
    private Uri imageURI;
    private StorageReference storageRef, imagesRef;
    private TextView tv_item_inventory_status, tv_imageURL;
    private int REQUEST_IMAGE_CAPTURE = 1;
    private ImageButton ibGallery, ibCamera;
    private CheckBox cb_quantity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
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
        View fragmentView = inflater.inflate(R.layout.fragment_view_items_shopping_list, null);
        if (getArguments() == null) {
            h.prevFragment(getContext());
        }

        // Toast.makeText(getContext(), getArguments().getString("shoppingPlanName"), Toast.LENGTH_LONG).show();
        imgGoods = fragmentView.findViewById(R.id.imgGoods);
        ibGallery = fragmentView.findViewById(R.id.ibGallery);
        ibCamera = fragmentView.findViewById(R.id.ibCamera);
        spinnerCategory = fragmentView.findViewById(R.id.spinnerCategory);
        editTextGoodsName = fragmentView.findViewById(R.id.editTextGoodsName);
        editTextQuantity = fragmentView.findViewById(R.id.editTextQuantity);
        pb_item_status = fragmentView.findViewById(R.id.pb_item_status);
        rl_goods_info = fragmentView.findViewById(R.id.rl_goods_info);
        tv_item_inventory_status = fragmentView.findViewById(R.id.tv_item_inventory_status);
        button_add_to_shop_list = fragmentView.findViewById(R.id.button_add_to_shop_list);
        cb_quantity = fragmentView.findViewById(R.id.cb_quantity);
        tv_imageURL = fragmentView.findViewById(R.id.tv_imageURL);
        adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, goodsCategory);
        spinnerCategory.setAdapter(adapter);
        loadGoodsData();
        button_add_to_shop_list.setOnClickListener(this);
        ibGallery.setOnClickListener(this);
        ibCamera.setOnClickListener(this);
        cb_quantity.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    editTextQuantity.setText("?");
                    editTextQuantity.setFocusable(false);
                    editTextQuantity.setClickable(false);
                    editTextQuantity.setFocusableInTouchMode(false);

                } else {
                    editTextQuantity.setText("0");
                    editTextQuantity.setFocusableInTouchMode(true);
                    editTextQuantity.setFocusable(true);
                    editTextQuantity.setClickable(true);
                }
            }
        });
        return fragmentView;

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

        if (requestCode == 10 && resultCode == Activity.RESULT_OK) {
            // Toast.makeText(getContext(), String.valueOf(resultCode), Toast.LENGTH_LONG).show();
            Toast.makeText(getContext(), String.valueOf(CommonStatusCodes.SUCCESS), Toast.LENGTH_LONG).show();
            imgGoods.setImageURI(data.getData());
            imageURI = data.getData();

            //  Toast.makeText(getContext(), String.valueOf(imageURI), Toast.LENGTH_LONG).show();

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

    private void loadGoodsData() {
        String barcode = getArguments().getString("barcode");

        DatabaseReference goodsRef = fb.getRef().child("barcode").child(barcode);
        goodsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                rl_goods_info.setVisibility(View.VISIBLE);

                if (dataSnapshot.getValue() == null) {
                    Toast.makeText(getContext(), "Please register this barcode.", Toast.LENGTH_LONG).show();
                    final String itemCheckResult = "You don't have this item in your inventory.";
                    tv_item_inventory_status.setText(itemCheckResult);

                } else {
                    barcodeExist = true;
                    Goods bg = dataSnapshot.getValue(Goods.class);
                    editTextGoodsName.setText(bg.getGoodsName());
                    tv_imageURL.setText(bg.getImageURL());
                    // Picasso.get().load(bg.getImageURL()).fit().into(imgGoods);
                    Glide.with(getContext())
                            .load(bg.getImageURL())
                            .centerCrop()
                            .placeholder(R.drawable.ic_loading_static)
                            .dontAnimate()
                            .into(imgGoods);
                    int spinnerPosition = adapter.getPosition(bg.getGoodsCategory());
                    spinnerCategory.setSelection(spinnerPosition);
                    checkUserInventoryForItem(bg.getGoodsCategory());

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void saveIntoList() {
        final String category = spinnerCategory.getSelectedItem().toString();
        String imageURL = tv_imageURL.getText().toString();
        name = editTextGoodsName.getText().toString();
        quantity = editTextQuantity.getText().toString();
        String shoppingPlanID = getArguments().getString("shoppingPlanID");
        String barcode = getArguments().getString("barcode");
        DatabaseReference reference = fb.getUserRef().
                child("shoppingPlan").child(shoppingPlanID).
                child("itemList");
        String itemId = reference.push().getKey();
        reference = fb.getUserRef().child("shoppingPlan").child(shoppingPlanID).
                child("itemList").child(itemId);
        ShoppingPlanItem shoppingPlanItem = new
                ShoppingPlanItem(shoppingPlanID, itemId, "Pending", barcode, category,
                name, quantity, imageURL);
        //   Toast.makeText(getContext(), shoppingPlanID, Toast.LENGTH_LONG).show();
        reference.setValue(shoppingPlanItem).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {


                Bundle cate = new Bundle();
                String shoppingPlanID = getArguments().getString("shoppingPlanID");
                String shoppingPlanName = getArguments().getString("shoppingPlanName");

                cate.putString("shoppingPlanID", shoppingPlanID);
                cate.putString("shoppingPlanName", shoppingPlanName);
                ShoppingPlanItemFragment frag = new ShoppingPlanItemFragment();
                frag.setArguments(cate);
                FragmentHandler f = new FragmentHandler();
                f.loadFragment(frag, getContext());

            }
        });
    }

    private void saveBarcode() {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        String barcode = getArguments().getString("barcode");

        imagesRef = storageRef.child("goods").child(barcode);

        imagesRef.putFile(imageURI).
                addOnCompleteListener(
                        new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                if (task.isSuccessful()) {

                                    imagesRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Uri> task) {
                                            final String imageURL = task.getResult().toString();
                                            tv_imageURL.setText(imageURL);
                                            FirebaseHandler fb = new FirebaseHandler();
                                            String barcode = getArguments().getString("barcode");

                                            DatabaseReference addBarcodeRef = fb.getRef().child("barcode").child(barcode);
                                            String goodsCategory, goodsName;
                                            goodsCategory = spinnerCategory.getSelectedItem().toString();
                                            goodsName = editTextGoodsName.getText().toString();
                                            Goods barcodeGoods = new Goods(barcode, goodsName, imageURL, goodsCategory);
                                            addBarcodeRef.setValue(barcodeGoods).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    saveIntoList();

                                                }
                                            });
                                        }
                                    });
                                }
                            }
                        });

    }

    private void checkUserInventoryForItem(String category) {
        pb_item_status.hide();

        FirebaseHandler fb = new FirebaseHandler();
        String barcode = getArguments().getString("barcode");
        final String itemCheckResult = "You don't have this item in your inventory.";
        DatabaseReference checkItemRef = fb.getUserRef().child("goods").child(category).child(barcode);
        checkItemRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() == null) {
                    tv_item_inventory_status.setText(itemCheckResult);
                } else {
                    int fullQuantity = 0;
                    String goodsName = "";
                    for (DataSnapshot snapShot : dataSnapshot.getChildren()
                    ) {
                        goodsName = snapShot.child("goodsName").getValue(String.class);
                        int quantity = Integer.parseInt(snapShot.child("quantity").getValue().toString());
                        fullQuantity = fullQuantity + quantity;
                    }
                    String fullQuantityS = String.valueOf(fullQuantity);

                    String itemCheckResult = "You have " + fullQuantityS + " " + goodsName + " in your inventory";
                    tv_item_inventory_status.setText(itemCheckResult);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_add_to_shop_list:
                if (barcodeExist) {
                    saveIntoList();


                } else {
                    saveBarcode();


                }


                break;
            case R.id.ibGallery:
                upload();
                break;
            case R.id.ibCamera:
                takePicture();
                break;
        }
    }
}
