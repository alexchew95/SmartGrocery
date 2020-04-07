package fyp.chewtsyrming.smartgrocery.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.widget.ContentLoadingProgressBar;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import fyp.chewtsyrming.smartgrocery.FirebaseHandler;
import fyp.chewtsyrming.smartgrocery.FragmentHandler;
import fyp.chewtsyrming.smartgrocery.R;
import fyp.chewtsyrming.smartgrocery.nestedRv.Goods;
import fyp.chewtsyrming.smartgrocery.object.UserModel;


public class ItemSettingFragment extends Fragment implements View.OnClickListener {
    private String barcode;
    private TextView tv_itemName, tv_itemCategory;
    private Spinner spinner_goodsLocation;
    private Switch switch_reminderStatus;
    private EditText et_daysToRemind;
    private LinearLayout ll_alertData, ll_main;
    private ImageButton ib_back;
    private FirebaseHandler firebaseHandler;
    private ArrayAdapter<String> adapter2;
    private Button button_saveSetting;
    private ContentLoadingProgressBar pb_itemSettings;
    private FragmentHandler fragmentHandler;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_item_setting, container, false);
        fragmentHandler = new FragmentHandler();
        if (getArguments() != null) {
            barcode = getArguments().getString("barcode");
        } else {
            fragmentHandler.prevFragment(getContext());
        }
        firebaseHandler = new FirebaseHandler();
        tv_itemName = view.findViewById(R.id.tv_itemName);
        tv_itemCategory = view.findViewById(R.id.tv_itemCategory);
        spinner_goodsLocation = view.findViewById(R.id.spinner_goodsLocation);
        switch_reminderStatus = view.findViewById(R.id.switch_reminderStatus);
        et_daysToRemind = view.findViewById(R.id.et_daysToRemind);
        ll_alertData = view.findViewById(R.id.ll_alertData);
        pb_itemSettings = view.findViewById(R.id.pb_itemSettings);
        button_saveSetting = view.findViewById(R.id.button_saveSetting);
        ll_main = view.findViewById(R.id.ll_main);
        ib_back = view.findViewById(R.id.ib_back);


        button_saveSetting.setOnClickListener(this);
        ib_back.setOnClickListener(this);
        getGoodsLocation();
        getItemSetting();

        switch_reminderStatus.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    ll_alertData.setVisibility(View.VISIBLE);
                } else ll_alertData.setVisibility(View.GONE);
            }
        });
        return view;
    }

    private void getItemSetting() {
        String goodsName = getArguments().getString("goodsName");
        tv_itemName.setText(goodsName);

        DatabaseReference itemSettingRef = firebaseHandler.getUserRef().child("goodsData")
                .child(barcode);
        itemSettingRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String category = dataSnapshot.child("goodsCategory").getValue(String.class);
                String alertStatus = dataSnapshot.child("alertStatus").getValue(String.class);
                String alertData = dataSnapshot.child("alertData").getValue(String.class);
                String goodsLocation = dataSnapshot.child("goodsLocation").getValue(String.class);
                int spinnerPosition = adapter2.getPosition(goodsLocation);
                spinner_goodsLocation.setSelection(spinnerPosition);

                tv_itemCategory.setText(category);
                if (alertStatus.contains("enabled")) {
                    switch_reminderStatus.setChecked(true);
                } else {
                    switch_reminderStatus.setChecked(false);

                }
                tv_itemCategory.setText(category);
                if (alertData.contains("disabled")) {
                    alertData = "0";
                }
                et_daysToRemind.setText(alertData);

            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getGoodsLocation() {
        UserModel um = new UserModel();
        String userId = um.getUserIDFromDataBase();
        DatabaseReference storageLocationRef = FirebaseDatabase.getInstance().getReference().
                child("user").child(userId).child("inventoryLocation");

        storageLocationRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {

                    List<String> arrStorageLocation = new ArrayList<>();

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
                    spinner_goodsLocation.setAdapter(adapter2);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void saveSetting() {
        pb_itemSettings.show();
        ll_main.setVisibility(View.GONE);
        final DatabaseReference saveSettingRef = firebaseHandler.getUserRef().child("goodsData").child(barcode);
        saveSettingRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String alertStatus = "";
                String alertData = "";
                alertData = et_daysToRemind.getText().toString();
                String goodsLocation = spinner_goodsLocation.getSelectedItem().toString();
                if (switch_reminderStatus.isChecked()) {
                    alertStatus = "enabled";
                } else {
                    alertStatus = "disabled";
                }

                Goods goods = dataSnapshot.getValue(Goods.class);
                goods.setAlertData(alertData);
                goods.setAlertStatus(alertStatus);
                goods.setGoodsLocation(goodsLocation);
                //Toast.makeText(getContext(), goods.getAlertData(), Toast.LENGTH_SHORT).show();
                saveSettingRef.setValue(goods).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        pb_itemSettings.hide();
                        Toast.makeText(getContext(), "Setting saved!", Toast.LENGTH_LONG).show();
                        fragmentHandler.prevFragment(getContext());
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.ib_back:

                fragmentHandler.prevFragment(getContext());
                break;
            case R.id.button_saveSetting:
                saveSetting();
                break;


        }
    }
}
