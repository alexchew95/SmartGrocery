package fyp.chewtsyrming.smartgrocery.adapter;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
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

import androidx.annotation.NonNull;
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

import fyp.chewtsyrming.smartgrocery.R;
import fyp.chewtsyrming.smartgrocery.fragment.AddGoodsFragment;
import fyp.chewtsyrming.smartgrocery.nestedRv.Goods;
import fyp.chewtsyrming.smartgrocery.object.FirebaseHandler;
import fyp.chewtsyrming.smartgrocery.object.ShoppingPlanItem;

public class ShoppingPlanItemListAdapter extends RecyclerView.Adapter<ShoppingPlanItemListAdapter.HomeViewHolder> {
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private Context context;
    private List<ShoppingPlanItem> shoppingPlanItems;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private String userId = user.getUid();
    private ArrayAdapter<String> adapter2;
    private Spinner spinnerGoodsLocation;

    public ShoppingPlanItemListAdapter(List<ShoppingPlanItem> shoppingPlanItems, Context context) {
        this.shoppingPlanItems = shoppingPlanItems;
        this.context = context;


    }


    @NonNull
    @Override
    public HomeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View theView = LayoutInflater.from(context).inflate(R.layout.shopping_plan_item_row, parent, false);


        return new HomeViewHolder(theView);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeViewHolder holder, final int position) {
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
        holder.tv_item_quantity.setText(goodsQuantity);
        holder.ib_edit_item_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder alert = new AlertDialog.Builder(context);
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View mView = inflater.inflate(R.layout.dialog_view_shopping_list_item, null);
                TextView tv_textGoodsName = mView.findViewById(R.id.tv_textGoodsName);
                TextView tv_category = mView.findViewById(R.id.tv_category);
                Button button_cancel = mView.findViewById(R.id.button_cancel);
                Button button_add_to_inventory_list = mView.findViewById(R.id.button_add_to_inventory_list);
                Button button_delete = mView.findViewById(R.id.button_delete);

                final Switch switch_reminderStatus= mView.findViewById(R.id.switch_reminderStatus);
                final LinearLayout ll_alert_day= mView.findViewById(R.id.ll_alert_day);

                final EditText editTextQuantity= mView.findViewById(R.id.editTextQuantity);
                final EditText editAlertTextQuantity= mView.findViewById(R.id.editAlertTextQuantity);
                final ImageButton ib_selectExpDate = mView.findViewById(R.id.ib_selectExpDate);
                ImageView imgGoods = mView.findViewById(R.id.imgGoods);
                spinnerGoodsLocation = mView.findViewById(R.id.spinnerGoodsLocation);
                getGoodsLocation();
                final FirebaseHandler fh = new FirebaseHandler();
                DatabaseReference userPrefRef = fh.getUserRef().child("goodsData").child(shoppingPlanItem.getGoodsBarcode());
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
                Glide.with(context)
                        .load(imageURL)
                        .centerCrop()
                        .placeholder(R.drawable.ic_loading_static)
                        .dontAnimate()
                        .into(imgGoods);

                switch_reminderStatus.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        if(b){
                            ll_alert_day.setVisibility(View.VISIBLE);
                        }
                        else{
                            ll_alert_day.setVisibility(View.GONE);

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
                        View mView = inflater.inflate(R.layout.shopping_plan_delete_dialog, null);

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
                        String barcode, goodsId, goodsName, imageURL, goodsCategory, expirationDate,
                                quantity, goodsLocation, alertData, alertStatus, insertDate;
                        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
                        Date date = new Date();

                        barcode = shoppingPlanItem.getGoodsBarcode();
                        goodsId = shoppingPlanItem.getItemID();
                        goodsName = shoppingPlanItem.getGoodsName();
                        imageURL = shoppingPlanItem.getImageURL();
                        goodsCategory = shoppingPlanItem.getGoodsCategory();
                        expirationDate = tv_expirationDate.getText().toString();//
                        quantity =editTextQuantity.getText().toString();
                        goodsLocation = spinnerGoodsLocation.getSelectedItem().toString();
                        if (switch_reminderStatus.isChecked()) {
                            alertStatus = switch_reminderStatus.getTextOn().toString();
                            alertData = editAlertTextQuantity.getText().toString();

                        } else {
                            alertStatus = switch_reminderStatus.getTextOff().toString();
                            alertData = switch_reminderStatus.getTextOff().toString();
                        }
                        insertDate = dateFormat.format(date);


                        Goods good = new Goods(barcode, goodsId, goodsName, imageURL, goodsCategory, expirationDate,
                                quantity, goodsLocation, alertData, alertStatus, insertDate);
                        good.addGoods(good);
                        AddGoodsFragment addGoodsFragment=new AddGoodsFragment();
                        addGoodsFragment.checkGoodsDataExist(barcode, goodsCategory, goodsLocation, alertData, alertStatus);

                        mainDialog.dismiss();
                    }
                });


            }
        });

        holder.tv_item_title.setOnClickListener(new View.OnClickListener() {
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

        if (goodsCategory.equals("Beverages")) {
            holder.iv_category.setImageResource(R.drawable.ic_category_beverage);
        } else if (goodsCategory.equals("Canned or Jarred Goods")) {
            holder.iv_category.setImageResource(R.drawable.ic_category_canned);
        } else if (goodsCategory.equals("Dairy")) {
            holder.iv_category.setImageResource(R.drawable.ic_category_dairy);
        } else if (goodsCategory.equals("Bread or Bakery")) {
            holder.iv_category.setImageResource(R.drawable.ic_category_bread_bakery);
        } else if (goodsCategory.equals("Dry or Baking Goods")) {
            holder.iv_category.setImageResource(R.drawable.ic_category_dry_baking);
        } else if (goodsCategory.equals("Frozen Foods")) {
            holder.iv_category.setImageResource(R.drawable.ic_category_frozen);
        } else if (goodsCategory.equals("Fruit & Vegetables")) {
            holder.iv_category.setImageResource(R.drawable.ic_category_vege_fruit);
        } else if (goodsCategory.equals("Meat")) {
            holder.iv_category.setImageResource(R.drawable.ic_category_meat);
        } else if (goodsCategory.equals("Fish")) {
            holder.iv_category.setImageResource(R.drawable.ic_category_fish);
        } else if (goodsCategory.equals("Other")) {
            holder.iv_category.setImageResource(R.drawable.ic_category_other);
        }
    }


    @Override
    public int getItemCount() {
        return shoppingPlanItems.size();

    }

    private void getGoodsLocation() {

        DatabaseReference storageLocationRef = FirebaseDatabase.getInstance().getReference().child("user").child(userId).child("inventoryLocation");

        storageLocationRef.addListenerForSingleValueEvent(new ValueEventListener() {
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

    public static class HomeViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_category;
        TextView tv_item_title, tv_item_quantity;
        ImageButton ib_edit_item_list;

        public HomeViewHolder(View itemView) {
            super(itemView);
            iv_category = itemView.findViewById(R.id.iv_category);
            tv_item_title = itemView.findViewById(R.id.tv_item_title);
            tv_item_quantity = itemView.findViewById(R.id.tv_item_quantity);
            ib_edit_item_list = itemView.findViewById(R.id.ib_edit_item_list);
        }


    }
}
