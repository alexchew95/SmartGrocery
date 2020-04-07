package fyp.chewtsyrming.smartgrocery.fragment;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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

import net.glxn.qrgen.android.QRCode;

import fyp.chewtsyrming.smartgrocery.FragmentHandler;
import fyp.chewtsyrming.smartgrocery.R;
import fyp.chewtsyrming.smartgrocery.SplashActivity;


public class MyProfileFragment extends Fragment implements View.OnClickListener {
    @Nullable
    private DatabaseReference dbRef, goodsRef, followRef;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private String userId = user.getUid();
    private TextView tvName, tvEmail, tvFollowingCount, tvFollowerCount, tvInventoryCount;
    private RelativeLayout rellay_profile;
    private ProgressBar progressBar_profile;
    private ImageView ivUserProfile;
    LinearLayout ll_myQrCode, ll_scanQrCode;
    Button btn_logout;
    FragmentHandler h = new FragmentHandler();


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View fragmentView = inflater.inflate(R.layout.fragment_my_profile, null);

        //just change the fragment_dashboard
        //with the fragment you want to inflate
        //like if the class is HomeFragment it should have R.layout.home_fragment
        //if it is DashboardFragment it should have R.layout.fragment_dashboard
        tvName = fragmentView.findViewById(R.id.tvName);
        tvEmail = fragmentView.findViewById(R.id.tvEmail);
        tvFollowingCount = fragmentView.findViewById(R.id.tvFollowingCount);
        tvFollowerCount = fragmentView.findViewById(R.id.tvFollowerCount);
        tvInventoryCount = fragmentView.findViewById(R.id.tvInventoryCount);
        ivUserProfile = fragmentView.findViewById(R.id.ivUserProfile);
        btn_logout = fragmentView.findViewById(R.id.btn_logout);
        rellay_profile = fragmentView.findViewById(R.id.rellay_profile);
        progressBar_profile = fragmentView.findViewById(R.id.progressBar_profile);
        ll_scanQrCode = fragmentView.findViewById(R.id.ll_scanQrCode);
        ll_myQrCode = fragmentView.findViewById(R.id.ll_myQrCode);
        ll_myQrCode.setOnClickListener(this);
        ll_scanQrCode.setOnClickListener(this);
        btn_logout.setOnClickListener(this);

        getUserInfo();
        return fragmentView;
    }

    @Override
    public void onClick(final View view) {
        switch (view.getId()) {
            case R.id.ll_myQrCode:
                btn_showMessage(getView());
                break;
            case R.id.btn_logout:
                FirebaseAuth.getInstance()
                        .signOut();
                Intent i = new Intent(getActivity(),
                        SplashActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                        Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
                break;
            case R.id.ll_scanQrCode:
                Bundle bundle = new Bundle();
                bundle.putString("message", "Scan QR");
                bundle.putString("code", "9003");//9003 indicate add goods
                Fragment fragment = null;
                fragment = new BarcodeReaderFragment();
                fragment.setArguments(bundle);
                h.loadFragment(fragment,getContext());

                break;

        }

    }
    private void getUserInfo() {
        followRef = database.getReference().child("user").child(userId).child("sharedInventory");

        followRef.addListenerForSingleValueEvent(new ValueEventListener() {
            int followerCount = 0;
            int followingCount = 0;

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    if (snapshot.getKey().equals("follower")) {
                        for (DataSnapshot s : snapshot.getChildren()) {
                            if (s.getValue().toString().equals("true")) {
                                followerCount++;

                            }

                        }
                    }
                    if (snapshot.getKey().equals("following")) {
                        for (DataSnapshot s : snapshot.getChildren()) {
                            if (s.getValue().toString().equals("true")) {
                                Toast.makeText(getContext(), s.getValue().toString(), Toast.LENGTH_SHORT).show();

                                followingCount++;
                            }

                        }
                    }
                }
                tvFollowerCount.setText(followerCount + "");
                tvFollowingCount.setText(followingCount + "");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "user is single.", Toast.LENGTH_SHORT).show();

            }
        });
        goodsRef = database.getReference().child("user").child(userId).child("goods");
        goodsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            int inventoryCount = 0;

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (final DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if (!snapshot.getKey().equals("fav")) {
                        if (!snapshot.getKey().equals("recent")) {
                            //  Toast.makeText(getContext(), snapshot.getKey(), Toast.LENGTH_SHORT).show();

                            for (DataSnapshot snap : snapshot.getChildren()) {

                                inventoryCount++;

                            }
                        }
                    }

                }
                tvInventoryCount.setText(inventoryCount + "");
                // Toast.makeText(getContext(), Integer.toString(inventoryCount), Toast.LENGTH_SHORT).show();
                progressBar_profile.setVisibility(View.GONE);
                rellay_profile.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        dbRef = database.getReference().child("user").child(userId).child("profile");
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String name = dataSnapshot.child("name").getValue().toString();
                String uid = dataSnapshot.child("uid").getValue().toString();
                String email = dataSnapshot.child("email").getValue().toString();
                String imageurl = dataSnapshot.child("imageurl").getValue().toString();
                tvName.setText(name);
                tvEmail.setText(email);
                Glide.with(getActivity()).load(imageurl).into(ivUserProfile);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void btn_showMessage(View view) {
        final AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
        View mView = getLayoutInflater().inflate(R.layout.qr_code_dialog, null);
        ImageView ivQrCode = mView.findViewById(R.id.ivQrCode);
        Button btn_done = mView.findViewById(R.id.btn_done);
        Bitmap myBitmap = QRCode.from(userId).bitmap();
        ivQrCode.setImageBitmap(myBitmap);
        alert.setView(mView);
        final AlertDialog alertDialog = alert.create();
        alertDialog.setCanceledOnTouchOutside(false);
        btn_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        alertDialog.show();
    }
}
