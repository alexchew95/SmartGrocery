package fyp.chewtsyrming.smartgrocery.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.widget.ContentLoadingProgressBar;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import fyp.chewtsyrming.smartgrocery.FirebaseHandler;
import fyp.chewtsyrming.smartgrocery.FragmentHandler;
import fyp.chewtsyrming.smartgrocery.R;
import fyp.chewtsyrming.smartgrocery.nestedRv.Goods;
import fyp.chewtsyrming.smartgrocery.nestedRv.fragment_home;


public class ViewAlertedItemFragment extends Fragment implements View.OnClickListener {

    FragmentHandler h = new FragmentHandler();
    FirebaseHandler firebaseHandler = new FirebaseHandler();
    ImageView iv_showInfo, iv_item, iv_consumeRate;
    Boolean showInfo = false, snoozeChange = false;
    LinearLayout ll_info, ll_snoozeChange, ll_main;
    RelativeLayout rl_infoButton;
    TextView tv_insertDate, tv_expDate, tv_quantity, tv_consumedRateStatus, tv_status, tv_itemName, tv_location, tv_reminderType;
    Context context;
    Button button_reset, button_back, button_one, button_two, button_five, button_ten, button_offSnooze,
            button_onSnooze, button_cancelSnoozeDay, button_acceptSnoozeDays, button_disable;
    EditText et_snoozeDays;
    MaterialButtonToggleGroup toggleButton;
    Goods goods;
    ContentLoadingProgressBar clpb_alert;
    FragmentHandler fh = new FragmentHandler();
    // TODO: Rename and change types of parameters
    private String actionType, itemId, reminderType;

    public ViewAlertedItemFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            itemId = getArguments().getString("itemId");
            reminderType = getArguments().getString("alertType");

           /* Toast.makeText(getContext(), actionType, Toast.LENGTH_SHORT).show();
            Toast.makeText(getContext(), itemId, Toast.LENGTH_SHORT).show();*/
        } else {
            assert getContext() != null;
            h.loadFragment(new fragment_home(), getContext());

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View v = inflater.inflate(R.layout.fragment_view_alerted_item, container, false);
        context = v.getContext();
        iv_showInfo = v.findViewById(R.id.iv_showInfo);
        ll_info = v.findViewById(R.id.ll_info);
        rl_infoButton = v.findViewById(R.id.rl_infoButton);
        tv_insertDate = v.findViewById(R.id.tv_insertDate);
        tv_expDate = v.findViewById(R.id.tv_expDate);
        tv_quantity = v.findViewById(R.id.tv_quantity);
        tv_itemName = v.findViewById(R.id.tv_itemName);
        tv_location = v.findViewById(R.id.tv_location);
        iv_item = v.findViewById(R.id.iv_item);
        button_one = v.findViewById(R.id.button_one);
        button_two = v.findViewById(R.id.button_two);
        button_five = v.findViewById(R.id.button_five);
        button_ten = v.findViewById(R.id.button_ten);
        et_snoozeDays = v.findViewById(R.id.et_snoozeDays);
        toggleButton = v.findViewById(R.id.toggleButton);
        button_offSnooze = v.findViewById(R.id.button_offSnooze);
        button_onSnooze = v.findViewById(R.id.button_onSnooze);
        ll_snoozeChange = v.findViewById(R.id.ll_snoozeChange);
        button_reset = v.findViewById(R.id.button_reset);
        button_cancelSnoozeDay = v.findViewById(R.id.button_cancelSnoozeDay);
        button_acceptSnoozeDays = v.findViewById(R.id.button_acceptSnoozeDays);
        tv_reminderType = v.findViewById(R.id.tv_reminderType);
        button_disable = v.findViewById(R.id.button_disable);
        iv_consumeRate = v.findViewById(R.id.iv_consumeRate);
        ll_main = v.findViewById(R.id.ll_main);
        clpb_alert = v.findViewById(R.id.clpb_alert);
        tv_status = v.findViewById(R.id.tv_status);
        tv_consumedRateStatus = v.findViewById(R.id.tv_consumedRateStatus);
        button_back = v.findViewById(R.id.button_back);

        button_reset.setOnClickListener(this);
        button_one.setOnClickListener(this);
        button_two.setOnClickListener(this);
        button_five.setOnClickListener(this);
        button_ten.setOnClickListener(this);
        rl_infoButton.setOnClickListener(this);
        button_cancelSnoozeDay.setOnClickListener(this);
        button_acceptSnoozeDays.setOnClickListener(this);
        button_disable.setOnClickListener(this);
        button_back.setOnClickListener(this);


        loadItemData();

        toggleButton.addOnButtonCheckedListener(new MaterialButtonToggleGroup.OnButtonCheckedListener() {
            @Override
            public void onButtonChecked(MaterialButtonToggleGroup group, int checkedId, boolean isChecked) {

                if (snoozeChange) showLLsnooze(true);
                snoozeChange = true;

            }
        });

        return v;

    }

    private void loadItemData() {
        DatabaseReference itemRef = firebaseHandler.getUserRef().child("goods");
        itemRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot catSnapshot : dataSnapshot.getChildren()) {
                    if (!catSnapshot.getKey().equals("fav")) {
                        if (!catSnapshot.getKey().equals("recent")) {
                            for (DataSnapshot barcodeSnapshot : catSnapshot.getChildren()) {

                                if (barcodeSnapshot.hasChild(itemId)) {

                                    goods = barcodeSnapshot.child(itemId).getValue(Goods.class);
                                    tv_itemName.setText(goods.getGoodsName());
                                    tv_insertDate.setText(goods.getInsertDate());
                                    tv_expDate.setText(goods.getExpirationDate());
                                    tv_quantity.setText(goods.getQuantity());
                                    tv_location.setText(goods.getGoodsLocation());
                                    Glide.with(context)
                                            .load(goods.getImageURL())
                                            .centerCrop()
                                            .placeholder(R.drawable.ic_loading_static)
                                            .dontAnimate()
                                            .into(iv_item);
                                    String snoozeStatus = "",
                                            snoozeDay = "";
                                    if (reminderType.matches("rate")) {
                                        snoozeStatus = goods.getConsumeRateSnoozeStatus();
                                        snoozeDay = goods.getConsumeRateSnoozeDay();
                                        tv_reminderType.setText("Consumed Rate reminder");

                                    } else if (reminderType.matches("days")) {
                                        snoozeStatus = goods.getAlertDaySnoozeStatus();
                                        snoozeDay = goods.getAlertDaySnoozeDay();
                                        tv_reminderType.setText("Days reminder");

                                    }
                                    et_snoozeDays.setText(snoozeDay);
                                    int onSnz = button_onSnooze.getId();
                                    int ofSnz = button_offSnooze.getId();
                                    if (snoozeStatus.matches("enabled")) {
                                        toggleButton.check(onSnz);

                                    } else {
                                        toggleButton.check(ofSnz);

                                    }

                                    final DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
                                    Date date = new Date();
                                    final String currentDate = dateFormat.format(date);
                                    String expirationDate = goods.getExpirationDate();
                                    String alertData = goods.getAlertData();
                                    Date date1 = null;
                                    Date date2 = null;
                                    try {
                                        date1 = dateFormat.parse(currentDate);
                                        date2 = dateFormat.parse(expirationDate);

                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                    long diff = date2.getTime() - date1.getTime();
                                    float daysF = (diff / (1000 * 60 * 60 * 24));
                                    final Integer remainingDays = Math.round(daysF);
                                    int userSetRemainingDays = Integer.parseInt(alertData);
                                    String message = "";
                                    if (remainingDays > 0) {
                                        message = remainingDays + " days left!";
                                        if (remainingDays > userSetRemainingDays) {
                                            // viewHolder.ll_remainingDays.setBackgroundColor(Color.GREEN);
                                            tv_status.setTextColor(Color.parseColor("#36b422"));
                                            //  viewHolder.tv_remainingDaysStatus.setTextColor(Color.GREEN);

                                            //goods in good condition set green bg
                                        } else {
                                            tv_status.setTextColor(Color.parseColor("#ffa812"));
                                            //   viewHolder.ll_remainingDays.setBackgroundColor(Color.YELLOW);
                                        }
                                        tv_status.setText(message);

                                    } else if (remainingDays.equals(0)) {
                                        // set background red
                                        message = "Expired today!";
                                        //viewHolder.ll_remainingDays.setBackgroundColor(Color.RED);
                                        tv_status.setTextColor(Color.RED);
                                        tv_status.setText(message);
                                    } else {
                                        // set background red
                                        message = "Expired " + remainingDays + "days ago";
                                        //viewHolder.ll_remainingDays.setBackgroundColor(Color.RED);
                                        tv_status.setTextColor(Color.RED);
                                        tv_status.setText(message);
                                    }

                                    final DatabaseReference updateGoodsDataRef = firebaseHandler.getUserRef().child("goodsData")
                                            .child(goods.getBarcode()).child("rate");
                                    updateGoodsDataRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                            Integer currentQuantity = Integer.parseInt(goods.getQuantity());
                                            double rate = Double.parseDouble(dataSnapshot.getValue(String.class)), expectedConsumedQuantity;
                                            if (rate == 0) {
                                                tv_consumedRateStatus.setText("Not enough data");
                                                iv_consumeRate.setVisibility(View.GONE);
                                                tv_consumedRateStatus.setVisibility(View.VISIBLE);
                                            } else {
                                                iv_consumeRate.setVisibility(View.VISIBLE);
                                                tv_consumedRateStatus.setVisibility(View.GONE);
                                                expectedConsumedQuantity = rate * remainingDays;
                                                if (currentQuantity > expectedConsumedQuantity) {
                                                    iv_consumeRate.setImageResource(R.drawable.ic_thumb_down_black_24dp);
                                                } else {
                                                    iv_consumeRate.setImageResource(R.drawable.ic_thumb_up_green_24dp);
                                                }
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                                    clpb_alert.hide();
                                    ll_main.setVisibility(View.VISIBLE);
                                    break;
                                } else {
                                    fh.loadFragment(new NotificationFragment(), context);
                                }
                            }
                        }

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onClick(View view) {
        int currentQuantity = 0, newQuantity = 0;
        AddGoodsFragment addGoodsFragment = new AddGoodsFragment();
        switch (view.getId()) {
            case R.id.rl_infoButton:
                if (showInfo) {
                    showInfo = false;
                    ll_info.setVisibility(View.GONE);
                    iv_showInfo.setImageResource(R.drawable.ic_keyboard_arrow_up_black_24dp);
                } else {
                    showInfo = true;
                    ll_info.setVisibility(View.VISIBLE);
                    iv_showInfo.setImageResource(R.drawable.ic_keyboard_arrow_down_black_24dp);

                }

                break;
            case R.id.button_reset:
                showLLsnooze(true);
                et_snoozeDays.setText("1");
                break;
            case R.id.button_one:

                showLLsnooze(true);

                if (!et_snoozeDays.getText().toString().matches("")) {
                    currentQuantity = Integer.parseInt(et_snoozeDays.getText().toString());
                }
                newQuantity = addGoodsFragment.returnQuantity(1, currentQuantity);
                et_snoozeDays.setText(String.valueOf(newQuantity));
                break;
            case R.id.button_two:
                showLLsnooze(true);
                if (!et_snoozeDays.getText().toString().matches("")) {
                    currentQuantity = Integer.parseInt(et_snoozeDays.getText().toString());
                }
                newQuantity = addGoodsFragment.returnQuantity(2, currentQuantity);
                et_snoozeDays.setText(String.valueOf(newQuantity));
                break;
            case R.id.button_five:
                showLLsnooze(true);
                if (!et_snoozeDays.getText().toString().matches("")) {
                    currentQuantity = Integer.parseInt(et_snoozeDays.getText().toString());
                }
                newQuantity = addGoodsFragment.returnQuantity(5, currentQuantity);
                et_snoozeDays.setText(String.valueOf(newQuantity));
                break;
            case R.id.button_ten:
                showLLsnooze(true);
                if (!et_snoozeDays.getText().toString().matches("")) {
                    currentQuantity = Integer.parseInt(et_snoozeDays.getText().toString());
                }
                newQuantity = addGoodsFragment.returnQuantity(10, currentQuantity);
                et_snoozeDays.setText(String.valueOf(newQuantity));
                break;
            case R.id.button_cancelSnoozeDay:

                if (reminderType.matches("rate")) {
                    et_snoozeDays.setText(goods.getConsumeRateSnoozeDay());
                    String snoozeStatus = goods.getConsumeRateSnoozeStatus();
                    int onSnz = button_onSnooze.getId();
                    int ofSnz = button_offSnooze.getId();
                    if (snoozeStatus.matches("enabled")) {
                        toggleButton.check(onSnz);

                    } else {
                        toggleButton.check(ofSnz);

                    }
                } else if (reminderType.matches("days")) {
                    et_snoozeDays.setText(goods.getAlertDaySnoozeDay());
                    String alertDaySnoozeStatus = goods.getAlertDaySnoozeStatus();
                    int onSnz = button_onSnooze.getId();
                    int ofSnz = button_offSnooze.getId();
                    if (alertDaySnoozeStatus.matches("enabled")) {
                        toggleButton.check(onSnz);

                    } else {
                        toggleButton.check(ofSnz);

                    }
                }
                snoozeChange = false;
                showLLsnooze(false);

                break;
            case R.id.button_acceptSnoozeDays:
                saveSnoozeChanges();
                break;
            case R.id.button_disable:

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        context);

                // set title
                alertDialogBuilder.setTitle("Disable alert");

                // set dialog message
                alertDialogBuilder
                        .setMessage("Please consider as you will not receive anymore notification for this item ")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // if this button is clicked, close
                                // current activity
                                disableAlert();

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
                break;
            case R.id.button_back:
                h.loadFragment(new fragment_home(), context);
                break;

        }
    }

    private void showLLsnooze(boolean snoozeChange) {
        if (snoozeChange) {
            getView().findViewById(R.id.ll_snoozeChange).setVisibility(View.VISIBLE);
        } else {
            getView().findViewById(R.id.ll_snoozeChange).setVisibility(View.GONE);
        }
    }

    private void saveSnoozeChanges() {
        EditText et_snoozeDays = getView().findViewById(R.id.et_snoozeDays);
        MaterialButtonToggleGroup mtb = getView().findViewById(R.id.toggleButton);
        int selectedId = mtb.getCheckedButtonId();
        Button button = getView().findViewById(selectedId);
        String selectedSnooze = button.getText().toString();
        String alertDaySnoozeStatus = "";
        String alertDaySnoozeDay = et_snoozeDays.getText().toString();
        if (selectedSnooze.matches("ON")) {
            alertDaySnoozeStatus = "enabled";
        } else if (selectedSnooze.matches("ON")) {
            alertDaySnoozeStatus = "disabled";

        } else {

        }
        if (reminderType.matches("days")) {
            goods.setAlertDaySnoozeStatus(alertDaySnoozeStatus);
            goods.setAlertDaySnoozeDay(alertDaySnoozeDay);

        } else if (reminderType.matches("rate")) {
            goods.setConsumeRateSnoozeStatus(alertDaySnoozeStatus);
            goods.setConsumeRateSnoozeDay(alertDaySnoozeDay);

        }
        DatabaseReference updateSnoozeDayRef = firebaseHandler.getUserRef().child("goods").
                child(goods.getGoodsCategory()).child(goods.getBarcode()).child(goods.getGoodsId());
        updateSnoozeDayRef.setValue(goods).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(context, "Setting saved", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void disableAlert() {
        DatabaseReference disableAlertRef = firebaseHandler.getUserRef().child("goods").
                child(goods.getGoodsCategory()).child(goods.getBarcode()).child(goods.getGoodsId());
        String type = "";
        if (reminderType.matches("days")) {
            goods.setAlertDaysStatus("disabled");
            type = "alert day reminder";

        } else if (reminderType.matches("rate")) {
            goods.setConsumedRateStatus("disabled");
            type = "consumed rate reminder";

        }
        final String finalType = type;
        disableAlertRef.setValue(goods).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(context, "Items " + finalType + " has been disabled", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
