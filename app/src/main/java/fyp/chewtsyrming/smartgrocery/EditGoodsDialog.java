package fyp.chewtsyrming.smartgrocery;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;

public class EditGoodsDialog extends Dialog implements View.OnClickListener {
    Activity mActivity;
    private EditText etQuantity;
    private EditText etExpDate;
    private Button dialogButtonOK;

    public EditGoodsDialog(Activity activity) {
        super(activity);
        mActivity = activity;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_goods_dialog);
        etQuantity = findViewById(R.id.etQuantity);
        etExpDate = findViewById(R.id.etExpDate);
        dialogButtonOK = (Button) findViewById(R.id.dialogButtonOK);
        dialogButtonOK.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dialogButtonOK:
                break;
        }
    }
}
