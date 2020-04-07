package fyp.chewtsyrming.smartgrocery.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import fyp.chewtsyrming.smartgrocery.FragmentHandler;
import fyp.chewtsyrming.smartgrocery.R;


public class FollowerFragment extends Fragment implements View.OnClickListener {
    @Nullable
    private DatabaseReference dbRef;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private String userId = user.getUid();
    private TextView tvName, tvEmail, tvFollowingCount, tvFollowerCount, tvInventoryCount;
    private ImageView iv_follower_profile_pic;
    private ImageButton btn_approve_followR,btn_reject_followR,btn_add_followR,btn_cancel_followR;
    ProgressBar progressBar;
    Button btn_logout;
    RelativeLayout mainRellay;
    Bundle bundle;
    LinearLayout ll_add_reject, ll_follower_request;
    FragmentHandler h = new FragmentHandler();

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View fragmentView = inflater.inflate(R.layout.fragment_follower, null);

        //just change the fragment_dashboard
        //with the fragment you want to inflate
        //like if the class is HomeFragment it should have R.layout.home_fragment
        //if it is DashboardFragment it should have R.layout.fragment_dashboard

        ll_add_reject = fragmentView.findViewById(R.id.ll_add_reject);
        ll_follower_request = fragmentView.findViewById(R.id.ll_follower_request);
        iv_follower_profile_pic = fragmentView.findViewById(R.id.iv_follower_profile_pic);
        tvName = fragmentView.findViewById(R.id.tvName);
        mainRellay = fragmentView.findViewById(R.id.mainRellay);
        progressBar = fragmentView.findViewById(R.id.progressBar);
        btn_approve_followR= fragmentView.findViewById(R.id.btn_approve_followR);
        btn_reject_followR= fragmentView.findViewById(R.id.btn_reject_followR);
        btn_add_followR= fragmentView.findViewById(R.id.btn_add_followR);
        btn_cancel_followR= fragmentView.findViewById(R.id.btn_cancel_followR);

        btn_approve_followR.setOnClickListener(this);
        btn_reject_followR.setOnClickListener(this);
        btn_add_followR.setOnClickListener(this);
        btn_cancel_followR.setOnClickListener(this);
        getProcessType();
        return fragmentView;
    }
    @Override
    public void onClick(final View view) {
        switch (view.getId()) {
            //add follower
            //add
            case R.id.btn_add_followR:

                break;
            //cancel
            case R.id.btn_cancel_followR:
                Fragment fragment = null;
                fragment = new MyProfileFragment();
                h.loadFragment(fragment,getContext());
                break;

            //follower request
            //approve
            case R.id.btn_approve_followR:

                break;
            //reject
            case R.id.btn_reject_followR:

                break;
        }

    }

    private void getProcessType() {
        bundle = this.getArguments();
        final String status = bundle.getString("status");
        String target_uid = bundle.getString("target_uid");

        dbRef = database.getReference().child("user").child(target_uid).child("profile");
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String userName = dataSnapshot.child("name").getValue().toString();
                String imageUrl = dataSnapshot.child("imageurl").getValue().toString();
                tvName.setText(userName);
                Glide.with(getActivity()).load(imageUrl).into(iv_follower_profile_pic);
                mainRellay.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                if (status.equals("add_friend")) {
                    ll_add_reject.setVisibility(View.VISIBLE);
                } else {
                    ll_follower_request.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //Toast.makeText(getContext(), status + "//" + target_uid, Toast.LENGTH_LONG).show();


    }


}
