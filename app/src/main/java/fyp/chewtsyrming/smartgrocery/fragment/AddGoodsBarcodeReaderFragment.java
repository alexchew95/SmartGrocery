package fyp.chewtsyrming.smartgrocery.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;

import fyp.chewtsyrming.smartgrocery.R;
import fyp.chewtsyrming.smartgrocery.barcode.BarcodeCaptureActivity;

public class AddGoodsBarcodeReaderFragment extends Fragment {

    private CompoundButton autoFocus;
    private CompoundButton useFlash;
    private TextView statusMessage;
    private TextView barcodeValue;
    Button scan_barcode;

    private static int RC_BARCODE_CAPTURE;

    //private static final String TAG = "BarcodeMain";
    String barcode_value;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //just change the fragment_dashboard
        //with the fragment you want to inflate
        //like if the class is HomeFragment it should have R.layout.home_fragment
        //if it is DashboardFragment it should have R.layout.fragment_dashboard
        View v = inflater.inflate(R.layout.fragment_add_goods_barcode_reader, null);

        statusMessage = v.findViewById(R.id.status_message);
        barcodeValue = v.findViewById(R.id.barcode_value);
        scan_barcode = v.findViewById(R.id.read_barcode);
        autoFocus = v.findViewById(R.id.auto_focus);
        useFlash = v.findViewById(R.id.use_flash);
        String strtext = getArguments().getString("message");
        String code = getArguments().getString("code");
        if (code.equals("9001")) {
            RC_BARCODE_CAPTURE = 9001;//search goods code
        } else if (code.equals("9002")) {
            RC_BARCODE_CAPTURE = 9002;//add goods code
        }
        statusMessage.setText(strtext);
        scan_barcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), BarcodeCaptureActivity.class);
                intent.putExtra(BarcodeCaptureActivity.AutoFocus, autoFocus.isChecked());
                intent.putExtra(BarcodeCaptureActivity.UseFlash, useFlash.isChecked());

                startActivityForResult(intent, RC_BARCODE_CAPTURE);
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
                    barcodeValue.setText(barcode.displayValue);
                    Bundle barcodeBundle = new Bundle();
                    barcodeBundle.putString("barcode", barcode_value);
                    AddGoodsFragment addGoodsFragStart = new AddGoodsFragment();
                    addGoodsFragStart.setArguments(barcodeBundle);
                    FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.fragment_container, addGoodsFragStart);
                    transaction.addToBackStack(null);
                    transaction.commit();
                } else {

                    statusMessage.setText(barcode_value);
                    //  statusMessage.setText(R.string.barcode_failure);
                    //this will go into if, laptop camera not working.
                    barcode_value = "1060109929970";
                    Bundle barcodeBundle = new Bundle();
                    barcodeBundle.putString("barcode", barcode_value);
                    AddGoodsFragment addGoodsFragStart = new AddGoodsFragment();
                    addGoodsFragStart.setArguments(barcodeBundle);
                    FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.fragment_container, addGoodsFragStart);
                    transaction.addToBackStack(null);
                    transaction.commit();
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