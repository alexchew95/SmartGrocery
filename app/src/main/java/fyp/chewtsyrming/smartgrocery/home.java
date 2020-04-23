package fyp.chewtsyrming.smartgrocery;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

import fyp.chewtsyrming.smartgrocery.fragment.MyProfileFragment;
import fyp.chewtsyrming.smartgrocery.fragment.NotificationFragment;
import fyp.chewtsyrming.smartgrocery.fragment.ShoppingPlanFragment;
import fyp.chewtsyrming.smartgrocery.fragment.ViewAlertedItemFragment;
import fyp.chewtsyrming.smartgrocery.nestedRv.fragment_home;

public class home extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {
    FirebaseAuth.AuthStateListener aSL;
    DatabaseReference reff;
    FloatingActionButton fab, fab1, fab2, fab3;
    Boolean isFABOpen = false;
    FragmentHandler h = new FragmentHandler();
    private TextView mTextMessage;

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        Fragment fragment = null;
        switch (item.getItemId()) {


            case R.id.navigation_home:
                fragment = new fragment_home();
                break;

            case R.id.navigation_shoppingplan:
                fragment = new ShoppingPlanFragment();
                break;

            case R.id.navigation_profile:
                fragment = new MyProfileFragment();
                break;
            case R.id.navigation_notification:
                fragment = new NotificationFragment();
                break;


        }

        return h.loadFragment(fragment, this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Bundle extras = getIntent().getExtras();
        //loading the default fragment
        if (extras == null) {
            h.loadFragment(new fragment_home(), this);

        } else {
            String alertType = extras.getString("alertType");

            if (alertType.matches("notif")) {
                NotificationFragment fragment = new NotificationFragment();
                h.loadFragment(fragment, this);
            } else {
                ViewAlertedItemFragment fragment = new ViewAlertedItemFragment();
                fragment.setArguments(extras);
                h.loadFragment(fragment, this);
            }


        }

        //getting bottom navigation view and attaching the listener
        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(this);


        /*fab1 = findViewById(R.id.fab1);
        fab2 = findViewById(R.id.fab2);
        fab3 = findViewById(R.id.fab3);*/

      /*  fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle=new Bundle();
                bundle.putString("message", "Search Goods");
                bundle.putString("code", "9001");//9001 indicate search goods
                Fragment fragment = null;
                fragment=new AddGoodsBarcodeReaderFragment();
                fragment.setArguments(bundle);
                loadFragment(fragment);

            }
        });
        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle=new Bundle();
                bundle.putString("message", "Add Goods");
                bundle.putString("code", "9002");//9002 indicate add goods
                Fragment fragment = null;
                fragment=new AddGoodsBarcodeReaderFragment();
                fragment.setArguments(bundle);
                loadFragment(fragment);

            }
        });*/

    }


}
