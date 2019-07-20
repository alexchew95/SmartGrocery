package fyp.chewtsyrming.smartgrocery;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

import fyp.chewtsyrming.smartgrocery.fragment.InventoryFragmentGrid;
import fyp.chewtsyrming.smartgrocery.fragment.MyProfileFragment;
import fyp.chewtsyrming.smartgrocery.fragment.ShoppingPlanFragment;

public class home extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener{
    FirebaseAuth.AuthStateListener aSL;
    DatabaseReference reff;

    private TextView mTextMessage;
    private boolean loadFragment(Fragment fragment) {
        //switching fragment
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
            return true;
        }
        return false;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        Fragment fragment = null;
        switch (item.getItemId()) {


            case R.id.navigation_home:
                fragment = new InventoryFragmentGrid();
                break;

            case R.id.navigation_shoppingplan:
                fragment = new ShoppingPlanFragment();
                break;

            case R.id.navigation_profile:
                fragment = new MyProfileFragment();
                break;



        }

        return loadFragment(fragment);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //loading the default fragment
        loadFragment(new InventoryFragmentGrid());

        //getting bottom navigation view and attaching the listener
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(this);



    }


}
