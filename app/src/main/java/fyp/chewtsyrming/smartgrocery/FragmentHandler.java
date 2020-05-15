package fyp.chewtsyrming.smartgrocery;

import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

public class FragmentHandler {
    public static boolean loadFragment(Fragment fragment, Context context) {
        //switching fragment
        if (fragment != null) {
            ((FragmentActivity) context).getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit();
            return true;
        }
        return false;
    }

    public static void prevFragment(Context context) {
        FragmentManager fm = ((FragmentActivity) context).getSupportFragmentManager();
        fm.popBackStack();
    }
}
