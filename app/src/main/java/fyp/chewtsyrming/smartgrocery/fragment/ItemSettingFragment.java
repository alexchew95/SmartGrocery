package fyp.chewtsyrming.smartgrocery.fragment;

import android.content.Context;
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
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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
    Context context; private String barcode;
    private TextView tv_itemName, tv_itemCategory;
    private Spinner spinner_goodsLocation;
    private Switch switch_reminderStatus, switch_daysToRemindStatus;
    private EditText et_daysToRemind;
    private LinearLayout ll_alertData, ll_main;
    private ImageButton ib_back, buttonHelp, ib_add_storage_location;
    private FirebaseHandler firebaseHandler;
    private ArrayAdapter<String> adapter2;
    private Button button_saveSetting;
    private ContentLoadingProgressBar pb_itemSettings;
    private FragmentHandler fragmentHandler;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = getContext();

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_item_setting, container, false);

        if (getArguments() != null) {
            barcode = getArguments().getString("barcode");
        } else {
            FragmentHandler.prevFragment(context);
        }
        firebaseHandler = new FirebaseHandler();
        tv_itemName = view.findViewById(R.id.tv_itemName);
        tv_itemCategory = view.findViewById(R.id.tv_itemCategory);
        spinner_goodsLocation = view.findViewById(R.id.spinner_goodsLocation);
        switch_reminderStatus = view.findViewById(R.id.switch_reminderStatus);
        switch_daysToRemindStatus = view.findViewById(R.id.switch_daysToRemindStatus);
        et_daysToRemind = view.findViewById(R.id.et_daysToRemind);
        ll_alertData = view.findViewById(R.id.ll_alertData);
        pb_itemSettings = view.findViewById(R.id.pb_itemSettings);
        button_saveSetting = view.findViewById(R.id.button_saveSetting);
        ll_main = view.findViewById(R.id.ll_main);
        ib_back = view.findViewById(R.id.ib_back);
        buttonHelp = view.findViewById(R.id.buttonHelp);
        ib_add_storage_location = view.findViewById(R.id.ib_add_storage_location);

        button_saveSetting.setOnClickListener(this);
        buttonHelp.setOnClickListener(this);
        ib_back.setOnClickListener(this);
        ib_add_storage_location.setOnClickListener(this);
        getGoodsLocation();
        getItemSetting();

        switch_daysToRemindStatus.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    et_daysToRemind.setVisibility(View.VISIBLE);
                } else et_daysToRemind.setVisibility(View.GONE);
            }
        });
        return view;
    }

    private void getItemSetting() {
        String goodsName = getArguments().getString("goodsName");
        tv_itemName.setText(goodsName);

        DatabaseReference itemSettingRef = firebaseHandler.getUserRef().child("goodsData")
                .child(barcode);
        itemSettingRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Goods goods = dataSnapshot.getValue(Goods.class);
                String category = goods.getGoodsCategory();
                String consumedRateStatus = goods.getConsumedRateStatus();
                String alertDaysStatus = goods.getAlertDaysStatus();
                String alertData = goods.getAlertData();
                String goodsLocation = goods.getGoodsLocation();
                int spinnerPosition = adapter2.getPosition(goodsLocation);
                spinner_goodsLocation.setSelection(spinnerPosition);

                tv_itemCategory.setText(category);
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

        storageLocationRef.addValueEventListener(new ValueEventListener() {
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
                    adapter2 = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, arrStorageLocation);
                    spinner_goodsLocation.setAdapter(adapter2);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void saveSetting() {
        pb_itemSettings.show();
        ll_main.setVisibility(View.GONE);
        final DatabaseReference saveSettingRef = firebaseHandler.getUserRef().child("goodsData").child(barcode);
        saveSettingRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String alertDaysStatus = "", consumedRateStatus = "", alertData = "";

                alertData = et_daysToRemind.getText().toString();
                String goodsLocation = spinner_goodsLocation.getSelectedItem().toString();
                if (switch_reminderStatus.isChecked()) {
                    consumedRateStatus = switch_reminderStatus.getTextOn().toString();
                } else {
                    consumedRateStatus = switch_reminderStatus.getTextOff().toString();

                }
                if (switch_daysToRemindStatus.isChecked()) {
                    alertDaysStatus = switch_daysToRemindStatus.getTextOn().toString();
                } else {
                    alertDaysStatus = switch_daysToRemindStatus.getTextOff().toString();

                }

                Goods goods = dataSnapshot.getValue(Goods.class);
                goods.setAlertData(alertData);
                goods.setConsumedRateStatus(consumedRateStatus);
                goods.setAlertDaysStatus(alertDaysStatus);
                goods.setGoodsLocation(goodsLocation);
                //Toast.makeText(context, goods.getAlertData(), Toast.LENGTH_SHORT).show();
                saveSettingRef.setValue(goods).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if ((task.isSuccessful()))
                        {
                            pb_itemSettings.hide();
                            Toast.makeText(context, "Setting saved!", Toast.LENGTH_LONG).show();
                            FragmentManager fm = ((FragmentActivity) context).getSupportFragmentManager();
                            fm.popBackStack();
                        }
                    }
                } );
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onClick(View view) {
        AddGoodsFragment addGoodsFragment = new AddGoodsFragment();
        switch (view.getId()) {
            case R.id.ib_back:

                FragmentHandler.prevFragment(context);
                break;
            case R.id.button_saveSetting:
                saveSetting();
                break;

            case R.id.buttonHelp:
                addGoodsFragment.shoeHelpMessageDialog(context);
                break;
            case R.id.ib_add_storage_location:
                AddGoodsFragment.btn_add_storage_location(context);
                break;

        }
    }
}
