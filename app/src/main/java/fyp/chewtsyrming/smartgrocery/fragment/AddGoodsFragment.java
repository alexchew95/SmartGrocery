package fyp.chewtsyrming.smartgrocery.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import fyp.chewtsyrming.smartgrocery.DatePickerFragment;
import fyp.chewtsyrming.smartgrocery.R;
import fyp.chewtsyrming.smartgrocery.object.SubGoods;
import fyp.chewtsyrming.smartgrocery.object.TempGoods;
import fyp.chewtsyrming.smartgrocery.object.BarcodeGoods;
import fyp.chewtsyrming.smartgrocery.ocr.OcrCaptureActivity;

public class AddGoodsFragment extends Fragment {
    String selectedDate;
    EditText tv_goodsName, expirationDate, quantity, dateOfBirthET;
    TextView barcodeTV;
    Spinner spinnerCategory;
    Button buttonAddGoods;
    ImageButton imageButtomScanGoodsName;
    Button addGoodsBtn;
    FirebaseAuth.AuthStateListener aSL;
    DatabaseReference reff;
    SubGoods subGoods;
    BarcodeGoods bg;
    TempGoods goods;
    Boolean barcodeExist = false;
    private static final int RC_OCR_CAPTURE = 9003;
    public static final int REQUEST_CODE = 11;

    @SuppressLint("CutPasteId")
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final String[] celebrities = {

                "Fruit & Vegetables",
                "Milk & Cheese",


        };


        View fragmentView = inflater.inflate(R.layout.fragment_add_goods, null);
        tv_goodsName = fragmentView.findViewById(R.id.editTextGoodsName);
        expirationDate = fragmentView.findViewById(R.id.editTextExpiryDate);
        quantity = fragmentView.findViewById(R.id.editTextQuantity);
        spinnerCategory = (Spinner) fragmentView.findViewById(R.id.spinnerCategory);

        barcodeTV = fragmentView.findViewById(R.id.barcodeTV);

        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, celebrities);
        spinnerCategory.setAdapter(adapter);

        final Bundle barcodeBundle = this.getArguments();
        if (barcodeBundle != null) {
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
//call datepicker
        final FragmentManager fm = ((AppCompatActivity) getActivity()).getSupportFragmentManager();
        dateOfBirthET = fragmentView.findViewById(R.id.editTextExpiryDate);
        dateOfBirthET.setOnClickListener(new View.OnClickListener() {
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

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final String userId = user.getUid();


        addGoodsBtn = fragmentView.findViewById(R.id.buttonAddGoods);
        addGoodsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (barcodeExist == false) {
                    String scanned_barcode = barcodeBundle.getString("barcode");
                    String category = spinnerCategory.getSelectedItem().toString();
                    String goodsName = tv_goodsName.getText().toString();
                    bg=new BarcodeGoods(scanned_barcode,category,goodsName);
                    reff = FirebaseDatabase.getInstance().getReference().child("barcode").child(scanned_barcode);
                    reff.setValue(bg);
                }
                String scanned_barcode = barcodeBundle.getString("barcode");


                String category = spinnerCategory.getSelectedItem().toString();
                reff = FirebaseDatabase.getInstance().getReference().child("user").child(userId).child("goods")
                        .child(category).child(scanned_barcode);
                String goodsName = tv_goodsName.getText().toString();
                subGoods = new SubGoods(goodsName);
                reff.setValue(subGoods);
                reff = FirebaseDatabase.getInstance().getReference().child("user").child(userId).child("goods")
                        .child(category).child(scanned_barcode).child("masterExpirationQuantity");

                String id = reff.push().getKey();
                reff = FirebaseDatabase.getInstance().getReference().child("user").child(userId).child("goods")
                        .child(category).child(scanned_barcode).child("masterExpirationQuantity").child(id);
                String quantt = quantity.getText().toString();
                String expirationdate = expirationDate.getText().toString();

                String barcode = barcodeTV.getText().toString();

                subGoods = new SubGoods(barcode, category, expirationdate, id, quantt,goodsName);
                reff.setValue(subGoods);

                Toast.makeText(getContext(),userId , Toast.LENGTH_LONG).show();

            }
        });

        return fragmentView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // check for the results
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            // get date from string
            selectedDate = data.getStringExtra("selectedDate");
            // set the value of the editText
            dateOfBirthET.setText(selectedDate);
        }
        if (requestCode == RC_OCR_CAPTURE) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    String text = data.getStringExtra(OcrCaptureActivity.TextBlockObject);
                    Toast.makeText(getContext(), text, Toast.LENGTH_LONG).show();
                    tv_goodsName.setText(text);

                } else {

                }
            } else {

            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

}