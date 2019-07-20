package fyp.chewtsyrming.smartgrocery.adapter;

import android.app.Dialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.thoughtbot.expandablerecyclerview.ExpandableRecyclerViewAdapter;
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;

import java.util.List;

import fyp.chewtsyrming.smartgrocery.R;
import fyp.chewtsyrming.smartgrocery.object.Goods;
import fyp.chewtsyrming.smartgrocery.object.SubGoods;
import fyp.chewtsyrming.smartgrocery.viewholder.GoodsSubViewHolder;
import fyp.chewtsyrming.smartgrocery.viewholder.GoodsViewHolder;

public class GoodsListViewAdapter extends ExpandableRecyclerViewAdapter<GoodsViewHolder, GoodsSubViewHolder> {

    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    String masterExpirationQuantity = "masterExpirationQuantity";
    int newQuantity;

    public GoodsListViewAdapter(List<? extends ExpandableGroup> groups) {
        super(groups);
    }

    @Override
    public GoodsViewHolder onCreateGroupViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_goods, parent, false);
        return new GoodsViewHolder(view);
    }

    @Override
    public GoodsSubViewHolder onCreateChildViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_subgoods, parent, false);
        return new GoodsSubViewHolder(view);
    }

    @Override
    public void onBindChildViewHolder(final GoodsSubViewHolder holder, int flatPosition, ExpandableGroup group, int childIndex) {
        final SubGoods goodsSubView = ((Goods) group).getItems().get(childIndex);
        holder.setGoodsExpirationDate(goodsSubView.getExpirationDate());
        holder.setGoodsQuantity(goodsSubView.getQuantity());
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final String userId = user.getUid();
        final DatabaseReference parentReference = database.getReference().child("user").child(userId).
                child("goods").child(goodsSubView.getCategory()).child(goodsSubView.getBarcode()).child(masterExpirationQuantity).
                child(goodsSubView.getMasterExpirationQuantityID()).child("quantity");
        final DatabaseReference deleteReference = database.getReference().child("user").child(userId).
                child("goods").child(goodsSubView.getCategory()).child(goodsSubView.getBarcode()).child(masterExpirationQuantity).
                child(goodsSubView.getMasterExpirationQuantityID());
        /*holder.list_item_subgoods_quantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast toast = Toast.makeText(holder.list_item_subgoods_quantity.getContext(), goodsSubView.getExpirationDate(), Toast.LENGTH_SHORT);
                toast.show();
            }
        });*/
        holder.button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                newQuantity = Integer.parseInt(goodsSubView.getQuantity()) - 1;

                parentReference.setValue(newQuantity);

            }
        });
        holder.button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Dialog dialog = new Dialog(holder.button2.getContext());
                dialog.setContentView(R.layout.edit_goods_quantity_dialog);
                dialog.setTitle("Edit Goods Quantity");
                final EditText editQuantity = dialog.findViewById(R.id.text2);
                editQuantity.setText(goodsSubView.getQuantity());
                Button dialogButton = (Button) dialog.findViewById(R.id.dialogButtonOK);
                // if button is clicked, close the custom dialog
                dialogButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast toast = Toast.makeText(holder.list_item_subgoods_quantity.getContext(), editQuantity.getText(), Toast.LENGTH_SHORT);
                        toast.show();

                        newQuantity= Integer.parseInt(editQuantity.getText().toString());
                        parentReference.setValue(newQuantity);

                         dialog.dismiss();
                    }
                });
                dialog.show();


                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                final String userId = user.getUid();

               /* Toast toast = Toast.makeText(holder.list_item_subgoods_quantity.getContext(), userId, Toast.LENGTH_SHORT);
                toast.show();*/
            }
        });
        holder.button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteReference.removeValue();
            }
        });


    }

    @Override
    public void onBindGroupViewHolder(GoodsViewHolder holder, int flatPosition, ExpandableGroup group) {
        holder.setcategoryTitle(group);
    }


}
