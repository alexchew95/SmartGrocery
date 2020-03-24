package fyp.chewtsyrming.smartgrocery.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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

import fyp.chewtsyrming.smartgrocery.R;
import fyp.chewtsyrming.smartgrocery.adapter.ShoppingPlanAdapter;
import fyp.chewtsyrming.smartgrocery.object.ShoppingPlan;

public class ShoppingPlanFragment extends Fragment implements View.OnClickListener {
    Button btn_add_shopping_plan;
    ContentLoadingProgressBar pb;
    TextView tv_rv_empty;

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private String userId = user.getUid();

    private RecyclerView rvShoppingPlan;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    List<ShoppingPlan> shoppingPlanArrayList;
    DatabaseReference reference;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        View fragmentView = inflater.inflate(R.layout.fragment_shopping_plan, null);
        btn_add_shopping_plan = fragmentView.findViewById(R.id.btn_add_shopping_plan);
        pb = fragmentView.findViewById(R.id.pb);
        tv_rv_empty = fragmentView.findViewById(R.id.tv_rv_empty);
        shoppingPlanArrayList = new ArrayList<>();
        adapter = new ShoppingPlanAdapter(shoppingPlanArrayList, fragmentView.getContext());
        layoutManager = new LinearLayoutManager(fragmentView.getContext(), LinearLayoutManager.VERTICAL, false);
        rvShoppingPlan = fragmentView.findViewById(R.id.rvShoppingPlan);
        rvShoppingPlan.setNestedScrollingEnabled(false);
        rvShoppingPlan.setHasFixedSize(true);
        rvShoppingPlan.setLayoutManager(layoutManager);
        rvShoppingPlan.setAdapter(adapter);
        rvShoppingPlan.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        btn_add_shopping_plan.setOnClickListener(this);
        getShoppingList();

        return fragmentView;
    }

    private void getShoppingList() {
        reference = database.getReference().child("user").child(userId).child("shoppingPlan");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                shoppingPlanArrayList.clear();

                pb.hide();
                if (dataSnapshot.getValue() == null) {
                    tv_rv_empty.setVisibility(View.VISIBLE);
                } else {
                    rvShoppingPlan.setVisibility(View.VISIBLE);
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String shoppingId= snapshot.getKey();
                        String shoppingPlanName= (String)snapshot.child("shoppingPlanName").getValue();
                        String dateCreated=(String) snapshot.child("dateCreated").getValue();
                        String noOfItem="0";

                        if (snapshot.hasChild("itemList")){
                            noOfItem=Long.toString(snapshot.child("itemList").getChildrenCount());
                        }
                        ShoppingPlan shoppingPlan = new ShoppingPlan(shoppingId,shoppingPlanName,dateCreated,noOfItem);
                        shoppingPlanArrayList.add(shoppingPlan);

                    }
                    adapter.notifyDataSetChanged();

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
            case R.id.btn_add_shopping_plan:
                addShoppingPlan();
                break;

        }
    }

    private void addShoppingPlan(){


        final AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
        View mView = getLayoutInflater().inflate(R.layout.shopping_plan_add_dialog, null);

        Button btn_add_shopping = mView.findViewById(R.id.btn_add_shopping);
        Button btn_cancel_shopping = mView.findViewById(R.id.btn_cancel_shopping);
        final EditText et_shopping_plan_name = mView.findViewById(R.id.et_shopping_plan_name);
        alert.setView(mView);
        final AlertDialog alertDialog = alert.create();
        alertDialog.setCanceledOnTouchOutside(false);
        btn_add_shopping.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final FirebaseDatabase database = FirebaseDatabase.getInstance();
                String shoppingPlanName=et_shopping_plan_name.getText().toString();

                DatabaseReference addShoppingRef = database.getReference().child("user").child(userId).
                        child("shoppingPlan");
                String shoppingPlanId=addShoppingRef.push().getKey();
                DatabaseReference addShoppingRefwithKey=addShoppingRef.child(shoppingPlanId);
                ShoppingPlan shoppingPlan=new ShoppingPlan(shoppingPlanName,"2020/02/20");
                Toast.makeText(getContext(), shoppingPlanName, Toast.LENGTH_LONG).show();

                addShoppingRefwithKey.setValue(shoppingPlan).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        alertDialog.dismiss();

                    }
                });
            }
        });btn_cancel_shopping.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        alertDialog.show();
    }
}
