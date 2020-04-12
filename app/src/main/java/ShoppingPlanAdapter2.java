import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import fyp.chewtsyrming.smartgrocery.R;
import fyp.chewtsyrming.smartgrocery.object.ShoppingPlan;

public class ShoppingPlanAdapter2 extends ArrayAdapter<ShoppingPlan> {

    private final Context context;
    private final List<ShoppingPlan> shoppingPlanList;

    public ShoppingPlanAdapter2(Context context, List<ShoppingPlan> shoppingPlanList) {
        super(context, R.layout.linearlayout_shopping_list, shoppingPlanList);
        this.context = context;
        this.shoppingPlanList = shoppingPlanList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ShoppingPlan shoppingPlan = getItem(position);
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.linearlayout_shopping_list, parent, false);
        TextView textView = rowView.findViewById(R.id.tv_shopping_name);
        textView.setText(shoppingPlan.getShoppingPlanName());
        rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle b = new Bundle();
                b.putString("a", "b");
            }
        });
        // Change icon based on name


        return rowView;
    }
}
