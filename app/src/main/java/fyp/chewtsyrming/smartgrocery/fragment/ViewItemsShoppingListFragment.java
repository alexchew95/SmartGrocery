package fyp.chewtsyrming.smartgrocery.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.widget.ContentLoadingProgressBar;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import fyp.chewtsyrming.smartgrocery.R;
import fyp.chewtsyrming.smartgrocery.fragmentHandler;
import fyp.chewtsyrming.smartgrocery.object.BarcodeGoods;
import fyp.chewtsyrming.smartgrocery.object.FirebaseHandler;
import fyp.chewtsyrming.smartgrocery.object.UserModel;

public class ViewItemsShoppingListFragment extends Fragment {
    fragmentHandler h = new fragmentHandler();
    UserModel um = new UserModel();
    FirebaseHandler fb = new FirebaseHandler();
    ImageView imgGoods;
    ArrayAdapter<String> adapter;
    EditText editTextGoodsName, editTextQuantity;
    private Spinner spinnerCategory;
    private Boolean barcodeExist = false;
    private ContentLoadingProgressBar pb_item_status;
    RelativeLayout rl_goods_info;
    private TextView tv_item_inventory_status;
    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final String[] goodsCategory = {
                "Beverages",
                "Bread or Bakery",
                "Canned or Jarred Goods",
                "Dairy",
                "Dry or Baking Goods",
                "Frozen Foods",
                "Fruit & Vegetables",
                "Meat",
                "Fish",
                "Other",
        };
        View fragmentView = inflater.inflate(R.layout.fragment_view_items_shopping_list, null);
        if (getArguments() == null) {
            h.prevFragment(getContext());
        }
        view = getActivity().findViewById(R.id.pb_main);
        if (view instanceof ContentLoadingProgressBar) {
            ((ContentLoadingProgressBar) view).show();
            //Do your stuff
        }
        imgGoods = fragmentView.findViewById(R.id.imgGoods);
        spinnerCategory = fragmentView.findViewById(R.id.spinnerCategory);
        editTextGoodsName = fragmentView.findViewById(R.id.editTextGoodsName);
        editTextQuantity = fragmentView.findViewById(R.id.editTextQuantity);
        pb_item_status = fragmentView.findViewById(R.id.pb_item_status);
        rl_goods_info = fragmentView.findViewById(R.id.rl_goods_info);
        tv_item_inventory_status = fragmentView.findViewById(R.id.tv_item_inventory_status);
        adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, goodsCategory);
        spinnerCategory.setAdapter(adapter);
        loadGoodsData();
        return fragmentView;

    }

    private void loadGoodsData() {
        String barcode = getArguments().getString("barcode");
        String userId = um.getUserIDFromDataBase();

        DatabaseReference goodsRef = fb.getRef().child("barcode").child(barcode);
        goodsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (view instanceof ContentLoadingProgressBar) {
                    ((ContentLoadingProgressBar) view).hide();
                    rl_goods_info.setVisibility(View.VISIBLE);
                }
                if (dataSnapshot.getValue() == null) {
                    Toast.makeText(getContext(), "Please register this barcode.", Toast.LENGTH_LONG).show();
                    final String itemCheckResult = "You don't have this item in your inventory.";
                    tv_item_inventory_status.setText(itemCheckResult);

                } else {
                    barcodeExist = true;
                    BarcodeGoods bg = dataSnapshot.getValue(BarcodeGoods.class);
                    editTextGoodsName.setText(bg.getGoodsName());
                    Picasso.get().load(bg.getImageURL()).fit().into(imgGoods);
                    int spinnerPosition = adapter.getPosition(bg.getGoodsCategory());
                    spinnerCategory.setSelection(spinnerPosition);
                    checkUserInventoryForItem(bg.getGoodsCategory());

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void saveIntoList() {
        String shoppingPlanID = getArguments().getString("shoppingPlanID");
        String barcode = getArguments().getString("barcode");
        DatabaseReference reference = fb.getUserRef().child("shoppingPlan").child(shoppingPlanID).
                child("itemList");
        String itemId = reference.push().getKey();

    }

    private void checkUserInventoryForItem(String category) {
        FirebaseHandler fb = new FirebaseHandler();
        String barcode = getArguments().getString("barcode");
        final String itemCheckResult = "You don't have this item in your inventory.";
        DatabaseReference checkItemRef = fb.getUserRef().child("goods").child(category).child(barcode);
        checkItemRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Toast.makeText(getContext(), "hello", Toast.LENGTH_SHORT).show();

                if (dataSnapshot.getValue() == null) {
                    tv_item_inventory_status.setText(itemCheckResult);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
