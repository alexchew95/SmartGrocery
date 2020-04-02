package fyp.chewtsyrming.smartgrocery.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

import fyp.chewtsyrming.smartgrocery.R;
import fyp.chewtsyrming.smartgrocery.barcode.BarcodeCaptureActivity;
import fyp.chewtsyrming.smartgrocery.fragmentHandler;

public class BarcodeReaderFragment extends Fragment {

    private CompoundButton autoFocus;
    private CompoundButton useFlash;
    private TextView statusMessage;
    private TextView barcodeValue;
    private ImageView iv_task;
    Button read_barcode, no_barcode;
    fragmentHandler h= new fragmentHandler();

    private static int RC_BARCODE_CAPTURE;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private String userId = Objects.requireNonNull(user).getUid();
    private DatabaseReference reference;
    //private static final String TAG = "BarcodeMain";
    String barcode_value;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //just change the fragment_dashboard
        //with the fragment you want to inflate
        //like if the class is HomeFragment it should have R.layout.home_fragment
        //if it is DashboardFragment it should have R.layout.fragment_dashboard
        final View v = inflater.inflate(R.layout.fragment_barcode_reader, null);

        statusMessage = v.findViewById(R.id.status_message);
        iv_task = v.findViewById(R.id.iv_task);
        barcodeValue = v.findViewById(R.id.barcode_value);
        read_barcode = v.findViewById(R.id.read_barcode);
        autoFocus = v.findViewById(R.id.auto_focus);
        useFlash = v.findViewById(R.id.use_flash);
        no_barcode = v.findViewById(R.id.no_barcode);

        String strtext = getArguments().getString("message");
        final String code = getArguments().getString("code");
        if (code.equals("9001")) {
            iv_task.setImageResource(R.drawable.search_goods);
            RC_BARCODE_CAPTURE = 9001;//search goods code
        } else if (code.equals("9002")) {
            iv_task.setImageResource(R.drawable.add_goods);

            RC_BARCODE_CAPTURE = 9002;//add goods code
        } else if (code.equals("9003")) {
            RC_BARCODE_CAPTURE = 9003;//add friends
        } else if (code.equals("9004")) {
            RC_BARCODE_CAPTURE = 9004;//add items to shopping plan
        }
        statusMessage.setText(strtext);
        read_barcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), BarcodeCaptureActivity.class);
                intent.putExtra(BarcodeCaptureActivity.AutoFocus, autoFocus.isChecked());
                intent.putExtra(BarcodeCaptureActivity.UseFlash, useFlash.isChecked());

                startActivityForResult(intent, RC_BARCODE_CAPTURE);
            }
        });
        no_barcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               h.prevFragment(getContext());
            }
        });
        //getActivity().findViewById(R.id.read_barcode).setOnClickListener(this);

        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 9001) {//search goods
            //Toast.makeText(getContext(), String.valueOf(requestCode), Toast.LENGTH_LONG).show();
        } else if (requestCode == 9002) {//add goods

            if (resultCode == CommonStatusCodes.SUCCESS) {
                //Toast.makeText(getContext(), String.valueOf(requestCode), Toast.LENGTH_LONG).show();
                if (data != null) {
                    Barcode barcode = data.getParcelableExtra(BarcodeCaptureActivity.BarcodeObject);

                    statusMessage.setText(R.string.barcode_success);
                    String barcodeString = barcode.displayValue;
                    //Toast.makeText(getContext(), barcodeString, Toast.LENGTH_LONG).show();

                    barcodeValue.setText(barcode.displayValue);
                    Bundle barcodeBundle = new Bundle();
                    barcodeBundle.putString("barcode", barcodeString);
                    AddGoodsFragment addGoodsFragStart = new AddGoodsFragment();
                    addGoodsFragStart.setArguments(barcodeBundle);
                    h.loadFragment(addGoodsFragStart,getContext());


                } else {





                }
            } else {
                statusMessage.setText(String.format(getString(R.string.barcode_error),
                        CommonStatusCodes.getStatusCodeString(resultCode)));
            }
        } else if (requestCode == 9003) {
            Bundle barcodeBundle = new Bundle();
            Barcode barcode = data.getParcelableExtra(BarcodeCaptureActivity.BarcodeObject);
            String qrCodeValue = barcode.displayValue;
            barcodeBundle.putString("status", "add_friend");
            barcodeBundle.putString("target_uid", qrCodeValue);
            FollowerFragment followerFragment = new FollowerFragment();
            followerFragment.setArguments(barcodeBundle);
            h.loadFragment(followerFragment,getContext());
        } else if (requestCode == 9004) {//add item to shopping list

            if (resultCode == CommonStatusCodes.SUCCESS) {
                //Toast.makeText(getContext(), String.valueOf(requestCode), Toast.LENGTH_LONG).show();
                if (data != null) {
                    Barcode barcode = data.getParcelableExtra(BarcodeCaptureActivity.BarcodeObject);
                    String shoppingPlanID = getArguments().getString("shoppingPlanID");
                    String shoppingPlanName = getArguments().getString("shoppingPlanName");
                    Bundle shoppingListItemBundle = new Bundle();
                    String qrCodeValue = barcode.displayValue;
                    shoppingListItemBundle.putString("shoppingPlanID",shoppingPlanID );
                    shoppingListItemBundle.putString("barcode", qrCodeValue);
                    shoppingListItemBundle.putString("shoppingPlanName", shoppingPlanName);
                    ViewItemsShoppingListFragment viewItemsShoppingListFragment = new ViewItemsShoppingListFragment();
                    viewItemsShoppingListFragment.setArguments(shoppingListItemBundle);
                    h.loadFragment(viewItemsShoppingListFragment,getContext());
                    /*FragmentManager fm = getActivity().getSupportFragmentManager();
                    fm.popBackStack();*/
                } else {
                    // end of if code
                }
            } else {
                statusMessage.setText(String.format(getString(R.string.barcode_error),
                        CommonStatusCodes.getStatusCodeString(resultCode)));

            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }


}