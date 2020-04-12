package fyp.chewtsyrming.smartgrocery.adapter;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import fyp.chewtsyrming.smartgrocery.FirebaseHandler;
import fyp.chewtsyrming.smartgrocery.R;
import fyp.chewtsyrming.smartgrocery.nestedRv.Goods;
import fyp.chewtsyrming.smartgrocery.object.ShoppingPlan;
import fyp.chewtsyrming.smartgrocery.object.ShoppingPlanItem;
import fyp.chewtsyrming.smartgrocery.object.UserModel;

public class GoodsListAdapter extends ArrayAdapter<Goods> implements View.OnClickListener {
    ArrayList<Goods> goodsArrayList;
    Context mContext;
    UserModel um = new UserModel();
    FirebaseHandler fh = new FirebaseHandler();
    private int lastPosition = -1;
    private ArrayAdapter<String> adapter;

    public GoodsListAdapter(ArrayList<Goods> goodsArrayList, Context context) {
        super(context, R.layout.linearlayout_subitems, goodsArrayList);
        this.goodsArrayList = goodsArrayList;
        this.mContext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Goods goods = getItem(position);
        final ViewHolder viewHolder;
        final View view;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.linearlayout_subitems, parent, false);
            viewHolder.tvQuantity = convertView.findViewById(R.id.tvQuantityEdit);
            viewHolder.tvExpDate = convertView.findViewById(R.id.tvExpDateEdit);
            viewHolder.tvBuyDate = convertView.findViewById(R.id.tvBuyDate);
            viewHolder.editBtn = convertView.findViewById(R.id.editBtn);
            viewHolder.deleteBtn = convertView.findViewById(R.id.deleteBtn);
            viewHolder.tv_remainingDaysStatus = convertView.findViewById(R.id.tv_remainingDaysStatus);
            viewHolder.tv_consumedRateStatus = convertView.findViewById(R.id.tv_consumedRateStatus);
            viewHolder.ll_remainingDays = convertView.findViewById(R.id.ll_remainingDays);
            viewHolder.ll_consumedRate = convertView.findViewById(R.id.ll_consumedRate);
            viewHolder.iv_consumeRate = convertView.findViewById(R.id.iv_consumeRate);

            view = convertView;
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            view = convertView;

        }
        Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        view.startAnimation(animation);
        lastPosition = position;
        viewHolder.tvExpDate.setText(goods.getExpirationDate());
        viewHolder.tvQuantity.setText(goods.getQuantity());
        viewHolder.editBtn.setOnClickListener(this);
        viewHolder.editBtn.setTag(position);
        viewHolder.deleteBtn.setOnClickListener(this);
        viewHolder.deleteBtn.setTag(position);
        viewHolder.tvBuyDate.setText(goods.getInsertDate());

        //calculate remaining days with alertData
        final DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        Date date = new Date();
        final String currentDate = dateFormat.format(date);
        // Return the completed view to render on screen
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
        final int remainingDays = Math.round(daysF);
        int userSetRemainingDays = Integer.parseInt(alertData);
        String message = "";
        if (remainingDays > 0) {
            message = remainingDays + " days left!";
            if (remainingDays > userSetRemainingDays) {
                // viewHolder.ll_remainingDays.setBackgroundColor(Color.GREEN);
                viewHolder.tv_remainingDaysStatus.setTextColor(Color.parseColor("#36b422"));
                //  viewHolder.tv_remainingDaysStatus.setTextColor(Color.GREEN);

                //goods in good condition set green bg
            } else {
                viewHolder.tv_remainingDaysStatus.setTextColor(Color.parseColor("#ffa812"));
                //   viewHolder.ll_remainingDays.setBackgroundColor(Color.YELLOW);
            }
            viewHolder.tv_remainingDaysStatus.setText(message);

        } else {
            // set background red
            message = "Expired " + remainingDays + " ago";
            //viewHolder.ll_remainingDays.setBackgroundColor(Color.RED);
            viewHolder.tv_remainingDaysStatus.setTextColor(Color.RED);
            viewHolder.tv_remainingDaysStatus.setText("Expired");
        }

//calculate consumption rate
        final DatabaseReference updateGoodsDataRef = fh.getUserRef().child("goodsData")
                .child(goods.getBarcode()).child("rate");
        updateGoodsDataRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Integer currentQuantity = Integer.parseInt(goods.getQuantity());
                double rate = Double.parseDouble(dataSnapshot.getValue(String.class)), expectedConsumedQuantity;
                if (rate == 0) viewHolder.tv_consumedRateStatus.setText("Not enough data");
                else {
                    expectedConsumedQuantity = rate * remainingDays;
                    if (currentQuantity > expectedConsumedQuantity) {
                        viewHolder.iv_consumeRate.setImageResource(R.drawable.ic_thumb_down_black_24dp);
                    } else {
                        viewHolder.iv_consumeRate.setImageResource(R.drawable.ic_thumb_up_green_24dp);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return convertView;
    }

    @Override
    public void onClick(View v) {

        int position = (Integer) v.getTag();
        Object object = getItem(position);
        final Goods goods = (Goods) object;

        switch (v.getId()) {
            case R.id.editBtn:
                show_dialog(goods);
                break;
            case R.id.deleteBtn:
                UserModel um = new UserModel();
                final String userID = um.getUserIDFromDataBase();
                final AlertDialog.Builder alert = new AlertDialog.Builder(mContext);
                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View mView = inflater.inflate(R.layout.dialog_delete, null);
                TextView dialog_message = mView.findViewById(R.id.dialog_message),
                        tv_quantity = mView.findViewById(R.id.tv_quantity),
                        tv_insertDate = mView.findViewById(R.id.tv_insertDate), tv_expDate = mView.findViewById(R.id.tv_expDate);
                dialog_message.setText("Delete this item?");
                Button btn_yes = mView.findViewById(R.id.btn_yes);
                Button btn_no = mView.findViewById(R.id.btn_no);
                final LinearLayout ll_itemDelete = mView.findViewById(R.id.ll_itemDelete);
                TextView tv_plan_name = mView.findViewById(R.id.tv_plan_name);
                tv_plan_name.setText(goods.getGoodsName());
                tv_quantity.setText(goods.getQuantity());
                tv_insertDate.setText(goods.getInsertDate());
                tv_expDate.setText(goods.getExpirationDate());

                ll_itemDelete.setVisibility(View.VISIBLE);
                alert.setView(mView);
                final AlertDialog alertDialog = alert.create();
                alertDialog.setCanceledOnTouchOutside(false);
                btn_yes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        DatabaseReference deleteItemRef = fh.getUserRef().child("goods").
                                child(goods.getGoodsCategory()).child(goods.getBarcode())
                                .child(goods.getGoodsId());
                        deleteItemRef.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                ll_itemDelete.setVisibility(View.GONE);

                                alertDialog.dismiss();
                               /* notifyDataSetChanged();

                                mRecyclerView.scheduleLayoutAnimation();*/
                            }
                        });

                    }
                });
                btn_no.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ll_itemDelete.setVisibility(View.GONE);

                        alertDialog.dismiss();
                    }
                });

                alertDialog.show();

                //Toast.makeText(getContext(), goodsList.getBuyDate(), Toast.LENGTH_SHORT).show();
               /* final FirebaseDatabase database = FirebaseDatabase.getInstance();
                UserModel um = new UserModel();
                String userID = um.getUserIDFromDataBase();

                DatabaseReference goodsReference = database.getReference().child("user").child(userID).
                        child("goods").child(goods.getGoodsCategory()).child(goods.getBarcode())
                        .child(goods.getGoodsId());
                goodsReference.removeValue();*/
                break;
        }
    }

    private void show_dialog(final Goods goods) {

        final AlertDialog.Builder alert = new AlertDialog.Builder(mContext);
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View mView = inflater.inflate(R.layout.dialog_edit_goods, null);
        alert.setView(mView);
        final AlertDialog alertDialog = alert.create();
        alertDialog.setCanceledOnTouchOutside(false);

        final Switch switch_reminderStatus = mView.findViewById(R.id.switch_reminderStatus), switch_daysToRemindStatus = mView.findViewById(R.id.switch_daysToRemindStatus);
        final ImageButton ib_closeDialog = mView.findViewById(R.id.ib_closeDialog),
                ib_back = mView.findViewById(R.id.ib_back),
                ib_calenderPicker = mView.findViewById(R.id.ib_calenderPicker);
        Button button_editDetails = mView.findViewById(R.id.button_editDetails),
                button_consumedDetails = mView.findViewById(R.id.button_consumedDetails),
                button_saveEdit = mView.findViewById(R.id.button_saveEdit),
                button_saveConsumed = mView.findViewById(R.id.button_saveConsumed),
                button_one = mView.findViewById(R.id.button_one),
                button_two = mView.findViewById(R.id.button_two),
                button_five = mView.findViewById(R.id.button_five),
                button_max = mView.findViewById(R.id.button_max),
                button_selectSP = mView.findViewById(R.id.button_selectSP),
                button_resetQuantity = mView.findViewById(R.id.button_resetQuantity);
        final LinearLayout ll_edit = mView.findViewById(R.id.ll_edit),
                ll_consumed = mView.findViewById(R.id.ll_consumed),
                ll_one = mView.findViewById(R.id.ll_one),
                ll_addShoppingPlan = mView.findViewById(R.id.ll_addShoppingPlan);
        final Spinner spinner_goodsLocation = mView.findViewById(R.id.spinner_goodsLocation),
                spinner_shoppingList = mView.findViewById(R.id.spinner_shoppingList);

        final EditText et_quantity = mView.findViewById(R.id.et_quantity),
                et_expDate = mView.findViewById(R.id.et_expDate),
                et_consumedQuantity = mView.findViewById(R.id.et_consumedQuantity),
                et_daysToRemind = mView.findViewById(R.id.et_daysToRemind);
        final TextView tv_currentQuantity = mView.findViewById(R.id.tv_currentQuantity);
        final Switch switch_addToSP = mView.findViewById(R.id.switch_addToSP);
        //load data
        String alertDaysStatus = goods.getAlertDaysStatus(),
                consumedRateStatus = goods.getConsumedRateStatus();
        et_expDate.setText(goods.getExpirationDate());
        et_quantity.setText(goods.getQuantity());
        tv_currentQuantity.setText(goods.getQuantity());
        et_daysToRemind.setText(goods.getAlertData());


        switch_addToSP.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    ll_addShoppingPlan.setVisibility(View.VISIBLE);
                } else ll_addShoppingPlan.setVisibility(View.GONE);

            }
        });
        switch_daysToRemindStatus.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    et_daysToRemind.setVisibility(View.VISIBLE);
                } else et_daysToRemind.setVisibility(View.GONE);
            }
        });
        if (alertDaysStatus.contains("enabled")) switch_daysToRemindStatus.setChecked(true);
        else switch_daysToRemindStatus.setChecked(false);
        if (consumedRateStatus.contains("enabled")) switch_reminderStatus.setChecked(true);
        else switch_reminderStatus.setChecked(false);
        final List<ShoppingPlan> shoppingPlanList = new ArrayList<>();
        DatabaseReference shoppingListRef = fh.getUserRef().child("shoppingPlan");
        shoppingListRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                shoppingPlanList.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    String shoppingPlanID = ds.getKey();
                    String shoppingPlanName = ds.child("shoppingPlanName").getValue(String.class);
                    shoppingPlanList.add(new ShoppingPlan(shoppingPlanID, shoppingPlanName, shoppingPlanName));
                    //Toast.makeText(mContext,shoppingPlanName,Toast.LENGTH_SHORT).show();
                }
                ArrayAdapter<ShoppingPlan> adapter = new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_dropdown_item, shoppingPlanList);
                spinner_shoppingList.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        button_saveConsumed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Integer currentQuantity = Integer.parseInt(tv_currentQuantity.getText().toString());
                final Integer consumedQuantity = Integer.parseInt(et_consumedQuantity.getText().toString());
                String newQuantity = String.valueOf((currentQuantity - consumedQuantity));

                DatabaseReference itemRef = fh.getUserRef().child("goods").
                        child(goods.getGoodsCategory()).child(goods.getBarcode()).child(goods.getGoodsId());
                if (newQuantity.contains("0")) {

                    itemRef.removeValue();

                } else {
                    goods.setQuantity(newQuantity);
                    itemRef.setValue(goods);

                }
                if (switch_addToSP.isChecked()) {
                    ShoppingPlan shoppingPlan = (ShoppingPlan) spinner_shoppingList.getSelectedItem();
                    String selectedShoppingListID = shoppingPlan.getShoppingId();
                    String itemId = goods.getGoodsId(), buyStatus = "Pending",
                            barcode = goods.getBarcode(), category = goods.getGoodsCategory(),
                            name = goods.getGoodsName(), quantity = String.valueOf(consumedQuantity),
                            imageURL = goods.getImageURL();
                    ShoppingPlanItem shoppingPlanItem = new
                            ShoppingPlanItem(selectedShoppingListID, itemId, buyStatus, barcode, category,
                            name, quantity, imageURL);
                    DatabaseReference addToShopRef = fh.getUserRef().child("shoppingPlan").child(selectedShoppingListID).child("itemList");
                    addToShopRef.push().setValue(shoppingPlanItem);
                }
                final DatabaseReference updateGoodsDataRef = fh.getUserRef().child("goodsData")
                        .child(goods.getBarcode());
                updateGoodsDataRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Goods goodsData = dataSnapshot.getValue(Goods.class);
                        String totalUsed = goodsData.getTotalUsed();
                        int newTotalUsed = Integer.parseInt(totalUsed) + consumedQuantity;
                        goodsData.setTotalUsed(String.valueOf(newTotalUsed));
                        Integer activeDays = Integer.parseInt(goodsData.getActiveDays());
                        double newRate = (double) newTotalUsed / (double) activeDays;
                        goodsData.setRate(String.valueOf(newRate));

                        updateGoodsDataRef.setValue(goodsData);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                alertDialog.dismiss();

            }

        });
        button_resetQuantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                et_consumedQuantity.setText("0");
            }
        });

        button_one.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String str_consumedQuantity = String.valueOf(et_consumedQuantity.getText());
                Integer int_consumedQuantity = Integer.parseInt(str_consumedQuantity);
                int_consumedQuantity = int_consumedQuantity + 1;

                et_consumedQuantity.setText(String.valueOf(int_consumedQuantity));

            }
        });
        button_two.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String str_consumedQuantity = String.valueOf(et_consumedQuantity.getText());
                Integer int_consumedQuantity = Integer.parseInt(str_consumedQuantity);
                int_consumedQuantity = int_consumedQuantity + 2;

                et_consumedQuantity.setText(String.valueOf(int_consumedQuantity));
            }
        });
        button_five.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String str_consumedQuantity = String.valueOf(et_consumedQuantity.getText());
                Integer int_consumedQuantity = Integer.parseInt(str_consumedQuantity);
                int_consumedQuantity = int_consumedQuantity + 5;

                et_consumedQuantity.setText(String.valueOf(int_consumedQuantity));

            }
        });
        button_max.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                et_consumedQuantity.setText(goods.getQuantity());

            }
        });
        DatabaseReference storageLocationRef = FirebaseDatabase.getInstance().getReference().
                child("user").child(um.getUserIDFromDataBase()).child("inventoryLocation");

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
                    adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, arrStorageLocation);
                    spinner_goodsLocation.setAdapter(adapter);
                    int spinnerPosition = adapter.getPosition(goods.getGoodsLocation());
                    spinner_goodsLocation.setSelection(spinnerPosition);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        button_saveEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String currentQuantity = goods.getQuantity();
                String newQuantity = et_quantity.getText().toString();
                String newExpDate = et_expDate.getText().toString();
                String newLocation = spinner_goodsLocation.getSelectedItem().toString();
                DatabaseReference updateItemRef = fh.getUserRef().child("goods").child(goods.getGoodsCategory())
                        .child(goods.getBarcode()).child(goods.getGoodsId());
                goods.setQuantity(newQuantity);
                goods.setExpirationDate(newExpDate);
                goods.setGoodsLocation(newLocation);
                String daysToRemindStatus = "", reminderStatus = "";
                if (switch_daysToRemindStatus.isChecked())
                    daysToRemindStatus = switch_daysToRemindStatus.getTextOn().toString();
                else daysToRemindStatus = switch_daysToRemindStatus.getTextOff().toString();

                if (switch_reminderStatus.isChecked())
                    reminderStatus = switch_reminderStatus.getTextOn().toString();
                else reminderStatus = switch_reminderStatus.getTextOff().toString();
                goods.setAlertDaysStatus(daysToRemindStatus);
                goods.setConsumedRateStatus(reminderStatus);
                updateItemRef.setValue(goods).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(mContext, "Item updated!", Toast.LENGTH_LONG).show();
                        alertDialog.dismiss();
                    }
                });
            }
        });
        ib_closeDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();

            }
        });
        button_editDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ll_edit.setVisibility(View.VISIBLE);
                ll_consumed.setVisibility(View.GONE);
                ib_back.setVisibility(View.VISIBLE);
                ll_one.setVisibility(View.GONE);
            }
        });
        button_consumedDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ll_edit.setVisibility(View.GONE);
                ll_consumed.setVisibility(View.VISIBLE);
                ll_one.setVisibility(View.GONE);
                ib_back.setVisibility(View.VISIBLE);

            }
        });
        ib_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ll_edit.setVisibility(View.GONE);
                ll_consumed.setVisibility(View.GONE);
                ll_one.setVisibility(View.VISIBLE);
                ib_back.setVisibility(View.GONE);

            }
        });
        ib_calenderPicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
                Date currentExpDate = null;
                try {
                    currentExpDate = sdf.parse(goods.getExpirationDate());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                Calendar c = Calendar.getInstance();
                c.setTime(currentExpDate);
                //final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR); // current year
                int mMonth = c.get(Calendar.MONTH); // current month
                int mDay = c.get(Calendar.DAY_OF_MONTH); // current day
                DatePickerDialog datePickerDialog;
                datePickerDialog = new DatePickerDialog(mContext,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                // set day of month , month and year value in the edit text
                                et_expDate.setText(year + "/"
                                        + (monthOfYear + 1) + "/" + dayOfMonth);

                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });

        alertDialog.show();
    }

    public static class ViewHolder {
        TextView tvQuantity, tvExpDate, tvBuyDate, tv_remainingDaysStatus, tv_consumedRateStatus;
        ImageButton editBtn, deleteBtn;
        LinearLayout ll_remainingDays, ll_consumedRate;
        ImageView iv_consumeRate;
    }


}
