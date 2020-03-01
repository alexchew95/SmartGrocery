package fyp.chewtsyrming.smartgrocery;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashActivity extends AppCompatActivity {
private Handler handler=new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    private Runnable runnable = new Runnable(){
        @Override
        public void run(){

            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

            if (user != null) {
                startActivity(new Intent(getApplicationContext(), home.class));
                finish();
            }
            else{
                startActivity(new Intent(getApplicationContext(), login_user.class));
                finish();
            }



          /* if(!isFinishing()){

           }*/
        }
    };

    @Override
    protected void onResume(){
        super.onResume();
        handler.postDelayed(runnable, 2000);
    }
    @Override
    protected void onPause(){
        super.onPause();
        handler.removeCallbacks(runnable) ;
    }
}
