package fyp.chewtsyrming.smartgrocery.adapter;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
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
import androidx.core.widget.ContentLoadingProgressBar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import fyp.chewtsyrming.smartgrocery.FirebaseHandler;
import fyp.chewtsyrming.smartgrocery.R;
import fyp.chewtsyrming.smartgrocery.fragment.AddGoodsFragment;
import fyp.chewtsyrming.smartgrocery.fragment.ShoppingPlanItemFragment;
import fyp.chewtsyrming.smartgrocery.nestedRv.Goods;
import fyp.chewtsyrming.smartgrocery.object.ShoppingPlanItem;

public class ShoppingPlanItemListAdapter extends RecyclerView.Adapter<ShoppingPlanItemListAdapter.HomeViewHolder> {
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    final FirebaseHandler fh;
    Boolean showCB = false;
    private SparseBooleanArray mSelectedItems, cbArray;
    private Context context;
    private List<ShoppingPlanItem> shoppingPlanItems;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private String userId = user.getUid();
    private ArrayAdapter<String> adapter2;
    private Spinner spinnerGoodsLocation;

    public ShoppingPlanItemListAdapter(List<ShoppingPlanItem> shoppingPlanItems, Context context) {
        this.shoppingPlanItems = shoppingPlanItems;
        this.context = context;
        fh = new FirebaseHandler();

        mSelectedItems = new SparseBooleanArray();
        cbArray = new SparseBooleanArray();
    }


    @NonNull
    @Override
    public HomeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View theView = LayoutInflater.from(context).inflate(R.layout.shopping_plan_item_row, parent, false);


        return new HomeViewHolder(theView);
    }

    @Override
    public void onBindViewHolder(@NonNull final HomeViewHolder holder, final int position) {

        final ShoppingPlanItem shoppingPlanItem = shoppingPlanItems.get(position);
        final String goodsName = shoppingPlanItem.getGoodsName();
        final String shoppingPlanID = shoppingPlanItem.getShoppingPlanID();
        final String itemID = shoppingPlanItem.getItemID();
        final String buyStatus = shoppingPlanItem.getBuyStatus();
        final String goodsCategory = shoppingPlanItem.getGoodsCategory();
        final String imageURL = shoppingPlanItem.getImageURL();
        String goodsQuantity = shoppingPlanItem.getQuantity();
        final String barcode = shoppingPlanItem.getGoodsBarcode();
        holder.tv_item_title.setText(goodsName);
        boolean visible = mSelectedItems.get(position);
        boolean cbChecked = cbArray.get(position);
        holder.cb_row.setVisibility(visible ? View.VISIBLE : View.GONE);
        holder.cb_row.setChecked(cbChecked);

        holder.ll_main.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                Fragment f = ((FragmentActivity) context).getSupportFragmentManager().findFragmentById(R.id.fragment_container);
                if (f instanceof ShoppingPlanItemFragment)
                    // do something with f
                    ((ShoppingPlanItemFragment) f).showAllCheckBox();
                return true;
            }
        });
        holder.cb_row.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                cbArray.put(position, b);

            }
        });
        holder.tv_item_quantity.setText(goodsQuantity);
        holder.ib_edit_item_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder alert = new AlertDialog.Builder(context);
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View mView = inflater.inflate(R.layout.dialog_view_shopping_list_item, null);
                TextView tv_textGoodsName = mView.findViewById(R.id.tv_textGoodsName);
                TextView tv_category = mView.findViewById(R.id.tv_category);
                final TextView tv_item_inventory_status = mView.findViewById(R.id.tv_item_inventory_status);
                Button button_cancel = mView.findViewById(R.id.button_cancel);
                Button button_add_to_inventory_list = mView.findViewById(R.id.button_add_to_inventory_list);
                Button button_delete = mView.findViewById(R.id.button_delete);
                Button button_resetQuantity = mView.findViewById(R.id.button_resetQuantity);
                Button button_one = mView.findViewById(R.id.button_one);
                Button button_two = mView.findViewById(R.id.button_two);
                Button button_five = mView.findViewById(R.id.button_five);
                Button button_ten = mView.findViewById(R.id.button_ten);
                Button button_resetDR = mView.findViewById(R.id.button_resetDR);
                Button button_oneDR = mView.findViewById(R.id.button_oneDR);
                Button button_twoDR = mView.findViewById(R.id.button_twoDR);
                Button button_fiveDR = mView.findViewById(R.id.button_fiveDR);
                Button button_tenDR = mView.findViewById(R.id.button_tenDR);
                ImageButton buttonHelp = mView.findViewById(R.id.buttonHelp);
                final LinearLayout ll_button = mView.findViewById(R.id.ll_button);

                ContentLoadingProgressBar pb_item_status = mView.findViewById(R.id.pb_item_status);
                final Switch switch_reminderStatus = mView.findViewById(R.id.switch_reminderStatus),
                        switch_daysToRemindStatus = mView.findViewById(R.id.switch_daysToRemindStatus);
                final LinearLayout ll_alert_day = mView.findViewById(R.id.ll_alert_day);

                final EditText editTextQuantity = mView.findViewById(R.id.editTextQuantity);
                final EditText editAlertTextQuantity = mView.findViewById(R.id.editAlertTextQuantity);
                final ImageButton ib_selectExpDate = mView.findViewById(R.id.ib_selectExpDate),
                        ib_add_storage_location = mView.findViewById(R.id.ib_add_storage_location);
                ImageView imgGoods = mView.findViewById(R.id.imgGoods);
                spinnerGoodsLocation = mView.findViewById(R.id.spinnerGoodsLocation);
                getGoodsLocation();

                ib_add_storage_location.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AddGoodsFragment.btn_add_storage_location(context);

                    }
                });
                button_one.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int currentQuantity = 0;
                        int newQuantity = 1;
                        if (!editTextQuantity.getText().toString().isEmpty()) {
                            if (!editTextQuantity.getText().toString().contains("?")) {
                                currentQuantity = Integer.parseInt(editTextQuantity.getText().toString());
                            }
                        }
                        editTextQuantity.setText(addCount(currentQuantity, newQuantity));
                    }
                });
                button_two.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int currentQuantity = 0;
                        int newQuantity = 2;
                        if (!editTextQuantity.getText().toString().isEmpty()) {
                            if (!editTextQuantity.getText().toString().contains("?")) {
                                currentQuantity = Integer.parseInt(editTextQuantity.getText().toString());
                            }
                        }

                        editTextQuantity.setText(addCount(currentQuantity, newQuantity));
                    }
                });
                button_five.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int currentQuantity = 0;
                        int newQuantity = 5;
                        if (!editTextQuantity.getText().toString().isEmpty()) {
                            if (!editTextQuantity.getText().toString().contains("?")) {
                                currentQuantity = Integer.parseInt(editTextQuantity.getText().toString());
                            }
                        }
                        editTextQuantity.setText(addCount(currentQuantity, newQuantity));
                    }
                });
                button_ten.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int currentQuantity = 0;
                        int newQuantity = 10;
                        if (!editTextQuantity.getText().toString().isEmpty()) {
                            if (!editTextQuantity.getText().toString().contains("?")) {
                                currentQuantity = Integer.parseInt(editTextQuantity.getText().toString());
                            }
                        }
                        editTextQuantity.setText(addCount(currentQuantity, newQuantity));
                    }
                });
                button_oneDR.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int currentQuantity = 0;
                        int newQuantity = 1;
                        if (!editAlertTextQuantity.getText().toString().isEmpty()) {
                            currentQuantity = Integer.parseInt(editAlertTextQuantity.getText().toString());
                        }
                        editAlertTextQuantity.setText(addCount(currentQuantity, newQuantity));
                    }
                });
                button_twoDR.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int currentQuantity = 0;
                        int newQuantity = 2;
                        if (!editAlertTextQuantity.getText().toString().isEmpty()) {
                            currentQuantity = Integer.parseInt(editAlertTextQuantity.getText().toString());
                        }

                        editAlertTextQuantity.setText(addCount(currentQuantity, newQuantity));
                    }
                });
                button_fiveDR.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int currentQuantity = 0;
                        int newQuantity = 5;
                        if (!editAlertTextQuantity.getText().toString().isEmpty()) {
                            currentQuantity = Integer.parseInt(editAlertTextQuantity.getText().toString());
                        }

                        editAlertTextQuantity.setText(addCount(currentQuantity, newQuantity));
                    }
                });
                button_tenDR.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int currentQuantity = 0;
                        int newQuantity = 10;
                        if (!editAlertTextQuantity.getText().toString().isEmpty()) {
                            currentQuantity = Integer.parseInt(editAlertTextQuantity.getText().toString());
                        }

                        editAlertTextQuantity.setText(addCount(currentQuantity, newQuantity));
                    }
                });

                buttonHelp.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AddGoodsFragment addGoodsFragment = new AddGoodsFragment();
                        addGoodsFragment.shoeHelpMessageDialog(context);
                    }
                });
                button_resetQuantity.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        editTextQuantity.setText("1");
                    }
                });
                button_resetDR.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        editAlertTextQuantity.setText("1");
                    }
                });
                DatabaseReference userPrefRef = fh.getUserRef().child("goodsData").child(shoppingPlanItem.getGoodsBarcode());
                userPrefRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() != null) {
                            String alertDaysStatus = dataSnapshot.child("alertDaysStatus").getValue(String.class);
                            String alertData = dataSnapshot.child("alertData").getValue(String.class);
                            String consumedRateStatus = dataSnapshot.child("consumedRateStatus").getValue(String.class);
                            String goodsLocation = dataSnapshot.child("goodsLocation").getValue(String.class);
                            editAlertTextQuantity.setText(alertData);
                            int spinnerPosition = adapter2.getPosition(goodsLocation);
                            spinnerGoodsLocation.setSelection(spinnerPosition);
                            if (alertDaysStatus.contains("enabled")) {

                                switch_daysToRemindStatus.setChecked(true);
                                editAlertTextQuantity.setVisibility(View.VISIBLE);
                            } else {

                                switch_daysToRemindStatus.setChecked(false);
                                editAlertTextQuantity.setVisibility(View.GONE);
                            }
                            if (consumedRateStatus.contains("enabled")) {

                                switch_reminderStatus.setChecked(true);
                                editAlertTextQuantity.setVisibility(View.VISIBLE);
                            } else {

                                switch_reminderStatus.setChecked(false);
                                editAlertTextQuantity.setVisibility(View.GONE);
                            }

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                pb_item_status.hide();

                FirebaseHandler fb = new FirebaseHandler();
                final String itemCheckResult = "You don't have this item in your inventory.";
                DatabaseReference checkItemRef = fb.getUserRef().child("goods").child(goodsCategory).child(barcode);
                checkItemRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() == null) {
                            tv_item_inventory_status.setText(itemCheckResult);
                        } else {
                            int fullQuantity = 0;

                            for (DataSnapshot snapShot : dataSnapshot.getChildren()
                            ) {
                                int quantity = Integer.parseInt(snapShot.child("quantity").getValue().toString());
                                fullQuantity = fullQuantity + quantity;
                            }
                            String fullQuantityS = String.valueOf(fullQuantity);

                            String itemCheckResult = "You have " + fullQuantityS + " " + shoppingPlanItem.getGoodsName() + " in your inventory";
                            tv_item_inventory_status.setText(itemCheckResult);

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                Glide.with(context)
                        .load(imageURL)
                        .centerCrop()
                        .placeholder(R.drawable.ic_loading_static)
                        .dontAnimate()
                        .into(imgGoods);
                editTextQuantity.setText(shoppingPlanItem.getQuantity());
                switch_daysToRemindStatus.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        if (b) {
                            editAlertTextQuantity.setVisibility(View.VISIBLE);
                            ll_button.setVisibility(View.VISIBLE);

                        } else {
                            editAlertTextQuantity.setVisibility(View.GONE);
                            ll_button.setVisibility(View.GONE);

                        }
                    }
                });
                tv_textGoodsName.setText(shoppingPlanItem.getGoodsName());
                tv_category.setText(shoppingPlanItem.getGoodsCategory());
                final TextView tv_expirationDate = mView.findViewById(R.id.tv_expirationDate);
                ib_selectExpDate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final Calendar c = Calendar.getInstance();
                        int mYear = c.get(Calendar.YEAR); // current year
                        int mMonth = c.get(Calendar.MONTH); // current month
                        int mDay = c.get(Calendar.DAY_OF_MONTH); // current day
                        DatePickerDialog datePickerDialog;
                        datePickerDialog = new DatePickerDialog(context,
                                new DatePickerDialog.OnDateSetListener() {

                                    @Override
                                    public void onDateSet(DatePicker view, int year,
                                                          int monthOfYear, int dayOfMonth) {
                                        // set day of month , month and year value in the edit text
                                        tv_expirationDate.setText(year + "/"
                                                + (monthOfYear + 1) + "/" + dayOfMonth);

                                    }
                                }, mYear, mMonth, mDay);
                        datePickerDialog.show();
                    }
                });
                alert.setView(mView);
                final AlertDialog mainDialog = alert.create();
                mainDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                mainDialog.show();
                button_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mainDialog.dismiss();
                    }
                });

                button_delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        final AlertDialog.Builder alert = new AlertDialog.Builder(context);
                        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        View mView = inflater.inflate(R.layout.dialog_delete_shopping_plan, null);

                        Button btn_yes = mView.findViewById(R.id.btn_yes);
                        Button btn_no = mView.findViewById(R.id.btn_no);
                        TextView tv_plan_name = mView.findViewById(R.id.tv_plan_name);
                        TextView dialog_message = mView.findViewById(R.id.dialog_message);
                        tv_plan_name.setText(shoppingPlanItem.getGoodsName());
                        dialog_message.setText("Delete this item?");
                        alert.setView(mView);
                        final AlertDialog deleteDialog = alert.create();
                        deleteDialog.setCanceledOnTouchOutside(false);
                        btn_yes.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                final FirebaseDatabase database = FirebaseDatabase.getInstance();

                                DatabaseReference itemRef = database.getReference().child("user").child(userId).
                                        child("shoppingPlan").child(shoppingPlanItem.getShoppingPlanID()).
                                        child("itemList").child(shoppingPlanItem.getItemID());

                                itemRef.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        deleteDialog.dismiss();
                                        mainDialog.dismiss();
                               /* notifyDataSetChanged();

                                mRecyclerView.scheduleLayoutAnimation();*/
                                    }
                                });

                            }
                        });
                        btn_no.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                deleteDialog.dismiss();
                            }
                        });

                        deleteDialog.show();
                    }
                });
                button_add_to_inventory_list.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        boolean validateStatus = true;
                        String errorMessage = "";
                        String barcode, goodsId, goodsName, imageURL, goodsCategory, expirationDate,
                                quantity, goodsLocation, alertData, alertDaysStatus, consumedRateStatus, insertDate;
                        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
                        Date date = new Date();

                        barcode = shoppingPlanItem.getGoodsBarcode();
                        goodsId = shoppingPlanItem.getItemID();
                        goodsName = shoppingPlanItem.getGoodsName();
                        imageURL = shoppingPlanItem.getImageURL();
                        goodsCategory = shoppingPlanItem.getGoodsCategory();
                        expirationDate = tv_expirationDate.getText().toString();//
                        quantity = editTextQuantity.getText().toString();
                        goodsLocation = spinnerGoodsLocation.getSelectedItem().toString();
                        int spinnerPosition = adapter2.getPosition(goodsLocation);
                        spinnerGoodsLocation.setSelection(spinnerPosition);

                        alertData = editAlertTextQuantity.getText().toString();

                        if (switch_reminderStatus.isChecked()) {
                            alertDaysStatus = switch_reminderStatus.getTextOn().toString();

                        } else {
                            alertDaysStatus = switch_reminderStatus.getTextOff().toString();
                        }
                        if (switch_reminderStatus.isChecked()) {
                            consumedRateStatus = switch_reminderStatus.getTextOn().toString();

                        } else {
                            consumedRateStatus = switch_reminderStatus.getTextOff().toString();
                        }
                        insertDate = dateFormat.format(date);


                        Goods good = new Goods(barcode, goodsId, goodsName, imageURL, goodsCategory, expirationDate,
                                quantity, goodsLocation, alertData, alertDaysStatus, consumedRateStatus, insertDate
                                , "disabled", "1", "disabled", "1");
                        if (quantity.isEmpty()) {
                            validateStatus = false;
                            errorMessage = "Please enter quantity.";
                            if (expirationDate.isEmpty()) {
                                errorMessage = "Please enter quantity and expiration dates.";
                                if (goodsLocation.isEmpty()) {
                                    errorMessage = "Please enter quantity, expiration dates and select goods location.";

                                }
                                if (switch_daysToRemindStatus.isChecked()) {
                                    if (alertData.isEmpty()) {
                                        errorMessage = "Please enter quantity, expiration dates and days to remind.";
                                        if (goodsLocation.isEmpty()) {
                                            errorMessage = "Please enter quantity, expiration dates, days to remind and select goods location.";

                                        }
                                    }
                                }

                            }
                        }

                        if (expirationDate.isEmpty()) {
                            validateStatus = false;

                            if (errorMessage.isEmpty()) {
                                errorMessage = "Please enter expiration dates.";
                                if (goodsLocation.isEmpty()) {
                                    errorMessage = "Please enter expiration dates and select goods location.";

                                }
                                if (switch_daysToRemindStatus.isChecked()) {
                                    if (alertData.isEmpty()) {
                                        errorMessage = "Please enter expiration dates and days to remind.";
                                        if (goodsLocation.isEmpty()) {
                                            errorMessage = "Please enter expiration dates, days to remind and select goods location.";

                                        }
                                    }
                                }

                            }
                        }
                        if (switch_daysToRemindStatus.isChecked()) {
                            if (errorMessage.isEmpty()) {

                                if (alertData.isEmpty()) {
                                    validateStatus = false;

                                    errorMessage = "Please enter  days to remind.";
                                    if (goodsLocation.isEmpty()) {
                                        errorMessage = "Please enter  days to remind and select goods location.";

                                    }
                                }
                            }

                        }
                        if (goodsLocation.isEmpty()) {
                            validateStatus = false;
                            if (errorMessage.isEmpty()) {

                                errorMessage = "Please enter expiration dates and select goods location.";
                            }
                        }
                        if(quantity.equals("?")){
                            validateStatus = false;
                            if (errorMessage.isEmpty()) {

                                errorMessage = "Please enter goods quantity.";
                            }
                        }
                        if (validateStatus) {
                            good.addGoods(good, context);
                            AddGoodsFragment addGoodsFragment = new AddGoodsFragment();
                            addGoodsFragment.checkGoodsDataExist(barcode, goodsCategory, goodsLocation,
                                    alertData, alertDaysStatus, consumedRateStatus, context, "shoppingPlan");
                            Toast.makeText(context, "Items added to your inventory", Toast.LENGTH_SHORT).show();
                            mainDialog.dismiss();
                        } else {
                            Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();
                        }
                    }
                });


            }
        });

        holder.ll_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference updateBuyStatusRef = database.getReference().child("user").child(userId).
                        child("shoppingPlan").child(shoppingPlanID).child("itemList").child(itemID).child("buyStatus");

                if (buyStatus.equals("Pending")) {
                    updateBuyStatusRef.setValue("Crossed");

                } else {
                    updateBuyStatusRef.setValue("Pending");


                }

            }
        });

        switch (goodsCategory) {
            case "Beverages":
                holder.iv_category.setImageResource(R.drawable.ic_category_beverage);
                break;
            case "Canned or Jarred Goods":
                holder.iv_category.setImageResource(R.drawable.ic_category_canned);
                break;
            case "Dairy":
                holder.iv_category.setImageResource(R.drawable.ic_category_dairy);
                break;
            case "Bread or Bakery":
                holder.iv_category.setImageResource(R.drawable.ic_category_bread_bakery);
                break;
            case "Dry or Baking Goods":
                holder.iv_category.setImageResource(R.drawable.ic_category_dry_baking);
                break;
            case "Frozen Foods":
                holder.iv_category.setImageResource(R.drawable.ic_category_frozen);
                break;
            case "Fruit & Vegetables":
                holder.iv_category.setImageResource(R.drawable.ic_category_vege_fruit);
                break;
            case "Meat":
                holder.iv_category.setImageResource(R.drawable.ic_category_meat);
                break;
            case "Fish":
                holder.iv_category.setImageResource(R.drawable.ic_category_fish);
                break;
            case "Other":
                holder.iv_category.setImageResource(R.drawable.ic_category_other);
                break;

        }
    }


    @Override
    public int getItemCount() {
        return shoppingPlanItems.size();

    }

    private void getGoodsLocation() {

        DatabaseReference storageLocationRef = FirebaseDatabase.getInstance().getReference().child("user").child(userId).child("inventoryLocation");

        storageLocationRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<String> arrStorageLocation;

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
                    adapter2 = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, arrStorageLocation);
                    spinnerGoodsLocation.setAdapter(adapter2);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void setItemVisibilityByPosition(int position, boolean visible) {
        mSelectedItems.put(position, visible);
        notifyItemChanged(position);
    }

    public void deleteCheckedBox() {

        int itemCount = getItemCount();
        for (int i = 0; i <= itemCount; i++) {

            if (cbArray.get(i)) {
                final ShoppingPlanItem shoppingPlanItem = shoppingPlanItems.get(i);
                DatabaseReference deletCBRef = fh.getUserRef().child("shoppingPlan").
                        child(shoppingPlanItem.getShoppingPlanID()).child("itemList")
                        .child(shoppingPlanItem.getItemID());
                final int finalI = i;
                deletCBRef.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        cbArray.put(finalI, false);
                        notifyItemChanged(finalI);

                    }
                });
            }
        }


    }

    public String addCount(int currentQuantity, int newQuantity) {
        return String.valueOf(currentQuantity + newQuantity);
    }

    public static class HomeViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_category;
        TextView tv_item_title, tv_item_quantity;
        ImageButton ib_edit_item_list;
        LinearLayout ll_main;
        CheckBox cb_row;

        public HomeViewHolder(View itemView) {
            super(itemView);
            iv_category = itemView.findViewById(R.id.iv_category);
            tv_item_title = itemView.findViewById(R.id.tv_item_title);
            tv_item_quantity = itemView.findViewById(R.id.tv_item_quantity);
            ib_edit_item_list = itemView.findViewById(R.id.ib_edit_item_list);
            ll_main = itemView.findViewById(R.id.ll_main);
            cb_row = itemView.findViewById(R.id.cb_row);
        }


    }


}
