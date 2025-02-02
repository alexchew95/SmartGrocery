package fyp.chewtsyrming.smartgrocery.fragment;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.ContentLoadingProgressBar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import fyp.chewtsyrming.smartgrocery.FirebaseHandler;
import fyp.chewtsyrming.smartgrocery.FragmentHandler;
import fyp.chewtsyrming.smartgrocery.R;
import fyp.chewtsyrming.smartgrocery.adapter.ShoppingPlanItemListAdapter;
import fyp.chewtsyrming.smartgrocery.object.ShoppingPlanItem;

public class ShoppingPlanItemFragment extends Fragment implements View.OnClickListener {
    FirebaseHandler fh = new FirebaseHandler();
    TextView tv_fragment_title, tv_rv_empty, tv_clear_all, tv_clear_all2, tv_deleteAll;
    Button btn_add_shopping_plan_item, button_deleteItem;
    RelativeLayout rl_rv;
    LinearLayout ll_delete;

    List<ShoppingPlanItem> shoppingPlanItemList, shoppingPlanItemListCrossed;
    ContentLoadingProgressBar pb;
    Boolean checkboxStatus;
    ShoppingPlanItem shoppingPlanItem;
    Boolean showCB = false;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private String userId = Objects.requireNonNull(user).getUid();
    private DatabaseReference reference;
    private RecyclerView rvShoppingPlanItemPending, rvShoppingPlanItemCrossed;
    private ShoppingPlanItemListAdapter adapterPending, adapterCrossed;
    private RecyclerView.LayoutManager layoutManager, layoutManagerCrossed;

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View fragmentView = inflater.inflate(R.layout.fragment_item_list_shopping_plan, null);
        Bundle cate = this.getArguments();
        assert cate != null;
        checkboxStatus = false;
        String shoppingPlanID = cate.getString("shoppingPlanID");
        String shoppingPlanName = cate.getString("shoppingPlanName");
        tv_fragment_title = fragmentView.findViewById(R.id.tv_fragment_title);
        tv_rv_empty = fragmentView.findViewById(R.id.tv_rv_empty);
        rl_rv = fragmentView.findViewById(R.id.rl_rv);
        pb = fragmentView.findViewById(R.id.pb);
        btn_add_shopping_plan_item = fragmentView.findViewById(R.id.btn_add_shopping_plan_item);
        button_deleteItem = fragmentView.findViewById(R.id.button_deleteItem);
        tv_clear_all = fragmentView.findViewById(R.id.tv_clear_all);
        tv_clear_all2 = fragmentView.findViewById(R.id.tv_clear_all2);
        tv_deleteAll = fragmentView.findViewById(R.id.tv_deleteAll);
        ll_delete = fragmentView.findViewById(R.id.ll_delete);
        tv_fragment_title.setText(shoppingPlanName);

        //pending
        layoutManager = new LinearLayoutManager(fragmentView.getContext(), LinearLayoutManager.VERTICAL, false);
        shoppingPlanItemList = new ArrayList<>();
        adapterPending = new ShoppingPlanItemListAdapter(shoppingPlanItemList, fragmentView.getContext());
        rvShoppingPlanItemPending = fragmentView.findViewById(R.id.rvShoppingPlanItemPending);
        rvShoppingPlanItemPending.setNestedScrollingEnabled(false);
        rvShoppingPlanItemPending.setHasFixedSize(true);
        rvShoppingPlanItemPending.setLayoutManager(layoutManager);
        rvShoppingPlanItemPending.setAdapter(adapterPending);
        rvShoppingPlanItemPending.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        int resId = R.anim.layout_animation_fall_down;
        LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(getContext(), resId);
        rvShoppingPlanItemPending.setLayoutAnimation(animation);

        //crossed
        layoutManagerCrossed = new LinearLayoutManager(fragmentView.getContext(), LinearLayoutManager.VERTICAL, false);
        shoppingPlanItemListCrossed = new ArrayList<>();
        adapterCrossed = new ShoppingPlanItemListAdapter(shoppingPlanItemListCrossed, fragmentView.getContext());
        rvShoppingPlanItemCrossed = fragmentView.findViewById(R.id.rvShoppingPlanItemCrossed);
        rvShoppingPlanItemCrossed.setNestedScrollingEnabled(false);
        rvShoppingPlanItemCrossed.setHasFixedSize(true);
        rvShoppingPlanItemCrossed.setLayoutManager(layoutManagerCrossed);
        rvShoppingPlanItemCrossed.setAdapter(adapterCrossed);
        rvShoppingPlanItemCrossed.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        LayoutAnimationController animation2 = AnimationUtils.loadLayoutAnimation(getContext(), resId);
        rvShoppingPlanItemCrossed.setLayoutAnimation(animation2);
        button_deleteItem.setOnClickListener(this);
        btn_add_shopping_plan_item.setOnClickListener(this);
        tv_clear_all.setOnClickListener(this);
        tv_clear_all2.setOnClickListener(this);
        tv_deleteAll.setOnClickListener(this);

        getItemList(shoppingPlanID);
        return fragmentView;
    }

    private void getItemList(final String shoppingPlanID) {
        pb.show();
        reference = database.getReference().child("user").child(userId).child("shoppingPlan")
                .child(shoppingPlanID).child("itemList");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                pb.hide();
                if (dataSnapshot.getValue() == null) {
                    //empty item list
                    rl_rv.setVisibility(View.GONE);
                    tv_rv_empty.setVisibility(View.VISIBLE);
                } else {
                    tv_rv_empty.setVisibility(View.GONE);
                    rl_rv.setVisibility(View.VISIBLE);
                    shoppingPlanItemList.clear();
                    shoppingPlanItemListCrossed.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String itemID, goodsBarcode, buyStatus, goodsCategory, goodsName, quantity, imageURL;
                        itemID = snapshot.getKey();
                        buyStatus = (String) snapshot.child("buyStatus").getValue();
                        goodsBarcode = (String) snapshot.child("goodsBarcode").getValue();
                        goodsCategory = (String) snapshot.child("goodsCategory").getValue();
                        goodsName = (String) snapshot.child("goodsName").getValue();
                        quantity = (String) snapshot.child("quantity").getValue();
                        imageURL = (String) snapshot.child("imageURL").getValue();

                        ShoppingPlanItem shoppingPlanItem = new ShoppingPlanItem(shoppingPlanID, itemID, buyStatus, goodsBarcode
                                , goodsCategory, goodsName, quantity, imageURL);
                        if (buyStatus.equals("Pending")) {
                            shoppingPlanItemList.add(shoppingPlanItem);
                        } else {
                            shoppingPlanItemListCrossed.add(shoppingPlanItem);
                        }
                    }
                    if (shoppingPlanItemList.size() == 0) {
                        tv_clear_all.setVisibility(View.GONE);
                    } else {
                        tv_clear_all.setVisibility(View.VISIBLE);
                    }
                    if (shoppingPlanItemListCrossed.size() == 0) {
                        tv_clear_all2.setVisibility(View.GONE);
                    } else {
                        tv_clear_all2.setVisibility(View.VISIBLE);
                    }

                    adapterPending.notifyDataSetChanged();
                    adapterCrossed.notifyDataSetChanged();


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
            case R.id.btn_add_shopping_plan_item:
                show_add_item_dialog();
                break;
            case R.id.button_deleteItem:
                adapterPending.deleteCheckedBox();
                adapterCrossed.deleteCheckedBox();
                break;

            case R.id.tv_clear_all:
                deleteList("Pending");

                break;

            case R.id.tv_clear_all2:
                deleteList("Crossed");
                break;
            case R.id.tv_deleteAll:
                deleteList("all");
                break;

        }

    }

    public void deleteList(final String listType) {
        final String shoppingPlanID = getArguments().getString("shoppingPlanID");
        final DatabaseReference deleteRef = fh.getShoppingRef().child(shoppingPlanID).child("itemList");

        if (listType.matches("all")) {
            deleteRef.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(getContext(), "All item list removed from shopping plan!", Toast.LENGTH_LONG).show();
                }
            });
        } else {
            deleteRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot itemIDSS : dataSnapshot.getChildren()) {
                        final DatabaseReference deleteRef2 = fh.getShoppingRef()
                                .child(shoppingPlanID).child("itemList").child(itemIDSS.getKey());
                        deleteRef2.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                String buyStatus = dataSnapshot.child("buyStatus").getValue(String.class);
                                if (buyStatus.matches(listType)) {
                                    deleteRef2.removeValue();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                    Toast.makeText(getContext(), listType + " item list removed from shopping plan!", Toast.LENGTH_LONG).show();

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    public void show_add_item_dialog() {
        final AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
        View mView = getLayoutInflater().inflate(R.layout.shopping_plan_add_item_choice_dialog, null);
        Button button_search_inventory = mView.findViewById(R.id.button_search_inventory);
        Button button_search_barcode = mView.findViewById(R.id.button_search_barcode);
        Button btn_cancel_add_shopping_item = mView.findViewById(R.id.btn_cancel_add_shopping_item);

        alert.setView(mView);
        final AlertDialog alertDialog = alert.create();
        alertDialog.setCanceledOnTouchOutside(false);
        button_search_inventory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewMyInventory();
                alertDialog.dismiss();
            }
        });
        button_search_barcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addItemswithBarcode();
                alertDialog.dismiss();
            }
        });
        btn_cancel_add_shopping_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }

    public void addItemswithBarcode() {
        Bundle cate = this.getArguments();
        assert cate != null;
        String shoppingPlanID = cate.getString("shoppingPlanID");
        String shoppingPlanName = cate.getString("shoppingPlanName");
        Bundle bundle = new Bundle();
        bundle.putString("message", "Add items to " + shoppingPlanName + " list");
        bundle.putString("shoppingPlanID", shoppingPlanID);
        bundle.putString("shoppingPlanName", shoppingPlanName);
        bundle.putString("code", "9004");//9004 indicate add goods to shopping list
        Fragment fragment = null;
        fragment = new BarcodeReaderFragment();
        fragment.setArguments(bundle);
        FragmentHandler.loadFragment(fragment, getContext());
    }

    private void viewMyInventory() {
        Bundle cate = this.getArguments();
        assert cate != null;
        String shoppingPlanID = cate.getString("shoppingPlanID");
        String shoppingPlanName = cate.getString("shoppingPlanName");
        Bundle bundle = new Bundle();
        bundle.putString("shoppingPlanID", shoppingPlanID);
        bundle.putString("shoppingPlanName", shoppingPlanName);
        bundle.putString("goodsCategory", "All Goods");
        bundle.putString("processType", "shoppinglist");
        Fragment fragment = null;
        fragment = new GoodsFromSameCategoryFragment();
        fragment.setArguments(bundle);
        FragmentHandler.loadFragment(fragment, getContext());
    }

    public void showAllCheckBox() {
        Integer itemCountPending = adapterPending.getItemCount();
        Integer itemCountCrossed = adapterCrossed.getItemCount();
        showCB = !showCB;
        for (int i = 0; i <= itemCountPending; i++) {

            adapterPending.setItemVisibilityByPosition(i, showCB);

        }
        for (int i = 0; i <= itemCountCrossed; i++) {

            adapterCrossed.setItemVisibilityByPosition(i, showCB);


        }
        if (showCB) {
            ll_delete.setVisibility(View.VISIBLE);
        } else {
            ll_delete.setVisibility(View.GONE);

        }
    }
}
