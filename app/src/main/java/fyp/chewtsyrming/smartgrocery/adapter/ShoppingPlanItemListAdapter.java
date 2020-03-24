package fyp.chewtsyrming.smartgrocery.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

import fyp.chewtsyrming.smartgrocery.R;
import fyp.chewtsyrming.smartgrocery.object.ShoppingPlanItem;

public class ShoppingPlanItemListAdapter extends RecyclerView.Adapter<ShoppingPlanItemListAdapter.HomeViewHolder> {
    private Context context;
    private List<ShoppingPlanItem> shoppingPlanItems;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private String userId = user.getUid();
    final FirebaseDatabase database = FirebaseDatabase.getInstance();

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
        String goodsName = shoppingPlanItem.getGoodsName();
        final String shoppingPlanID = shoppingPlanItem.getShoppingPlanID();
        final String itemID = shoppingPlanItem.getItemID();
        final String buyStatus = shoppingPlanItem.getBuyStatus();
        final String goodsCategory = shoppingPlanItem.getGoodsCategory();
        String goodsQuantity = shoppingPlanItem.getQuantity();
        String barcode = shoppingPlanItem.getGoodsBarcode();
        holder.tv_item_title.setText(goodsName);
        holder.tv_item_quantity.setText(goodsQuantity);
        holder.ib_edit_item_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

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
