package fyp.chewtsyrming.smartgrocery.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import fyp.chewtsyrming.smartgrocery.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SortDialogFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SortDialogFragment extends DialogFragment implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    TextView tv_expDate, tv_insertDate, tv_quantity;
    ImageButton button_closeDialog;
    RadioGroup radioGroup;
    RadioButton radioButton;
    String orderChoice = "Ascending";
    int selectedId;

    private String mParam1;
    private String mParam2;

    public SortDialogFragment() {
        // Required empty public constructor
    }

    /*
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SortFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SortDialogFragment newInstance() {
        SortDialogFragment fragment = new SortDialogFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    /*    if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }*/
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_sort, container, false);
        tv_expDate = view.findViewById(R.id.tv_expDate);
        tv_insertDate = view.findViewById(R.id.tv_insertDate);
        tv_quantity = view.findViewById(R.id.tv_quantity);
        button_closeDialog = view.findViewById(R.id.button_closeDialog);

        tv_expDate.setOnClickListener(this);
        tv_insertDate.setOnClickListener(this);
        tv_quantity.setOnClickListener(this);
        button_closeDialog.setOnClickListener(this);
        radioGroup = view.findViewById(R.id.radioGroup);
        int selectedId = radioGroup.getCheckedRadioButtonId();
        radioButton = view.findViewById(selectedId);
        orderChoice = radioButton.getText().toString();
        // Toast.makeText(getContext(), orderChoice, Toast.LENGTH_SHORT).show();
        // find the radiobutton by returned id

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                radioButton = view.findViewById(i);
                orderChoice = radioButton.getText().toString();

            }
        });

        return view;
    }

    private void sendResult(String dataType, String orderChoice) {
        if (getTargetFragment() == null) {
            return;
        }

        Intent intent = GoodsFromSameGoodsFragment.newIntent(dataType, orderChoice);
        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);
        dismiss();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_expDate:

                sendResult("expirationDate", orderChoice);
                break;
            case R.id.tv_insertDate:

                sendResult("insertDate", orderChoice);
                break;
            case R.id.tv_quantity:

                sendResult("quantity", orderChoice);
                break;
            case R.id.button_closeDialog:
                dismiss();
                break;

        }
    }
}
