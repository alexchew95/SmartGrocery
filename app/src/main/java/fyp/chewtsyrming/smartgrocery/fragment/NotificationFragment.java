package fyp.chewtsyrming.smartgrocery.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.widget.ContentLoadingProgressBar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import fyp.chewtsyrming.smartgrocery.FirebaseHandler;
import fyp.chewtsyrming.smartgrocery.FragmentHandler;
import fyp.chewtsyrming.smartgrocery.R;
import fyp.chewtsyrming.smartgrocery.adapter.NotificationAdapter;
import fyp.chewtsyrming.smartgrocery.nestedRv.fragment_home;
import fyp.chewtsyrming.smartgrocery.object.Notification;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NotificationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NotificationFragment extends Fragment implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    boolean showCB;
    private FirebaseHandler firebaseHandler;
    private FragmentHandler fragmentHandler;
    private List<Notification> notificationList;
    private ContentLoadingProgressBar clpb_notification;
    private Button button_back, button_delete;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private RecyclerView rv_notification;
    private NotificationAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private Context context;
    private TextView tv_noNotification;

    public NotificationFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NotificationFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NotificationFragment newInstance(String param1, String param2) {
        NotificationFragment fragment = new NotificationFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseHandler = new FirebaseHandler();
        fragmentHandler = new FragmentHandler();
        showCB = false;
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_notification, container, false);
        context = view.getContext();

        rv_notification = view.findViewById(R.id.rv_notification);
        notificationList = new ArrayList<>();
        adapter = new NotificationAdapter(notificationList, view.getContext());
        layoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        rv_notification = view.findViewById(R.id.rv_notification);
        rv_notification.setNestedScrollingEnabled(false);
        rv_notification.setHasFixedSize(true);
        rv_notification.setLayoutManager(layoutManager);
        rv_notification.setAdapter(adapter);
        rv_notification.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        int resId = R.anim.layout_animation_slide_right;
        LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(context, resId);
        rv_notification.setLayoutAnimation(animation);

        clpb_notification = view.findViewById(R.id.clpb_notification);
        tv_noNotification = view.findViewById(R.id.tv_noNotification);
        button_back = view.findViewById(R.id.button_back);
        button_delete = view.findViewById(R.id.button_delete);

        button_back.setOnClickListener(this);
        button_delete.setOnClickListener(this);
        getNotification();
        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_back:
                fragmentHandler.loadFragment(new fragment_home(), context);
                break;
            case R.id.button_delete:
                adapter.deleteCheckedBox();
                break;

        }
    }

    private void getNotification() {
        clpb_notification.show();

        DatabaseReference notifRef = firebaseHandler.getUserRef().child("notification");
        notifRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() == null) {
                    tv_noNotification.setVisibility(View.VISIBLE);
                    rv_notification.setVisibility(View.GONE);
                } else {
                    notificationList.clear();
                    tv_noNotification.setVisibility(View.GONE);
                    rv_notification.setVisibility(View.VISIBLE);
                    for (DataSnapshot notifSnapShot : dataSnapshot.getChildren()) {
                        Notification notification = notifSnapShot.getValue(Notification.class);
                        notificationList.add(notification);
                    }
                    Collections.sort(notificationList, Notification.sortInsertDateDesc);
                    adapter.notifyDataSetChanged();
                }
                clpb_notification.hide();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
