package fyp.chewtsyrming.smartgrocery.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

import fyp.chewtsyrming.smartgrocery.R;
import fyp.chewtsyrming.smartgrocery.fragment.GoodsFromSameGoodsFragment;
import fyp.chewtsyrming.smartgrocery.fragment.ShoppingPlanItemFragment;
import fyp.chewtsyrming.smartgrocery.object.ShoppingPlan;

public class ShoppingPlanAdapter extends RecyclerView.Adapter<ShoppingPlanAdapter.HomeViewHolder> {
    private Context context;
    private List<ShoppingPlan> shoppingPlans;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private String userId = user.getUid();

    public ShoppingPlanAdapter(List<ShoppingPlan> shoppingPlans, Context context) {
        this.shoppingPlans = shoppingPlans;
        this.context = context;


    }


    @NonNull
    @Override
    public HomeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View theView = LayoutInflater.from(context).inflate(R.layout.shopping_plan_row, parent, false);


        return new HomeViewHolder(theView);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeViewHolder holder, final int position) {
        final ShoppingPlan shoppingPlan = shoppingPlans.get(position);
        holder.tv_shopping_title.setText(shoppingPlan.getShoppingPlanName());
        holder.tv_create_date.setText(shoppingPlan.getDateCreated());
        holder.tv_no_of_item.setText(shoppingPlan.getNoOfItem());

        holder.ib_edit_shopping_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Bundle cate = new Bundle();
                String shoppingPlanID=shoppingPlan.getShoppingId();
                String shoppingPlanName=shoppingPlan.getShoppingPlanName();
                cate.putString("shoppingPlanID", shoppingPlanID);
                cate.putString("shoppingName", shoppingPlanName);
                ShoppingPlanItemFragment frag = new ShoppingPlanItemFragment();
                frag.setArguments(cate);
                FragmentTransaction transaction = ((FragmentActivity) context).getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, frag);
                transaction.addToBackStack(null);
                transaction.commit();

            }
        });
        holder.ib_delete_shopping_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder alert = new AlertDialog.Builder(context);
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View mView = inflater.inflate(R.layout.shopping_plan_delete_dialog, null);

                Button btn_yes = mView.findViewById(R.id.btn_yes);
                Button btn_no = mView.findViewById(R.id.btn_no);
                TextView tv_plan_name = mView.findViewById(R.id.tv_plan_name);
                tv_plan_name.setText(shoppingPlan.getShoppingPlanName());
                alert.setView(mView);
                final AlertDialog alertDialog = alert.create();
                alertDialog.setCanceledOnTouchOutside(false);
                btn_yes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final FirebaseDatabase database = FirebaseDatabase.getInstance();

                        DatabaseReference shoppingRef = database.getReference().child("user").child(userId).
                                child("shoppingPlan").child(shoppingPlan.getShoppingId());
                        shoppingRef.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                alertDialog.dismiss();

                            }
                        });

                    }
                });btn_no.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });

                alertDialog.show();
            }
        });
    }


    @Override
    public int getItemCount() {
        return shoppingPlans.size();

    }


    public static class HomeViewHolder extends RecyclerView.ViewHolder {

        private TextView tv_shopping_title, tv_create_date, tv_no_of_item;
        private ImageButton ib_edit_shopping_list, ib_delete_shopping_list;

        public HomeViewHolder(View itemView) {
            super(itemView);

            tv_shopping_title = itemView.findViewById(R.id.tv_shopping_title);
            tv_create_date = itemView.findViewById(R.id.tv_create_date);
            tv_no_of_item = itemView.findViewById(R.id.tv_no_of_item);
            ib_edit_shopping_list = itemView.findViewById(R.id.ib_edit_shopping_list);
            ib_delete_shopping_list = itemView.findViewById(R.id.ib_delete_shopping_list);
        }


    }

}
