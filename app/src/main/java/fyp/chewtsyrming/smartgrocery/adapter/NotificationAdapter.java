package fyp.chewtsyrming.smartgrocery.adapter;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import fyp.chewtsyrming.smartgrocery.FirebaseHandler;
import fyp.chewtsyrming.smartgrocery.FragmentHandler;
import fyp.chewtsyrming.smartgrocery.R;
import fyp.chewtsyrming.smartgrocery.fragment.ViewAlertedItemFragment;
import fyp.chewtsyrming.smartgrocery.object.Notification;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.HomeViewHolder> {

    RecyclerView mRecyclerView;
    Boolean showCB = false;
    FirebaseHandler fh;
    private Context context;
    private List<Notification> notificationsList;
    private SparseBooleanArray mSelectedItems, cbArray;

    public NotificationAdapter(List<Notification> notificationsList, Context context) {
        this.notificationsList = notificationsList;
        this.context = context;
        mSelectedItems = new SparseBooleanArray();
        cbArray = new SparseBooleanArray();
        fh = new FirebaseHandler();

    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);

        mRecyclerView = recyclerView;
    }

    @NonNull
    @Override
    public HomeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View theView = LayoutInflater.from(context).inflate(R.layout.linearlayout_notification, parent, false);

        return new HomeViewHolder(theView);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeViewHolder holder, final int position) {
        final Notification notification = notificationsList.get(position);

        holder.tv_message.setText(notification.getNotifMessage());
        String messageStatus = notification.getNotifReadStatus();
        if (messageStatus.matches("unread")) {
            holder.iv_messageStatus.setImageResource(R.drawable.ic_unread_black_24dp);
        } else {
            holder.iv_messageStatus.setImageResource(R.drawable.ic_read);
        }
        final DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        Date date = new Date();
        final String currentDate = dateFormat.format(date);
        final String notifInsertDate = notification.getNotifInsertDate();
        Date date1 = null;
        Date date2 = null;
        try {
            date1 = dateFormat.parse(currentDate);
            date2 = dateFormat.parse(notifInsertDate);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        long diff = date1.getTime() - date2.getTime();
        float daysF = (diff / (1000 * 60 * 60 * 24));
        int days = Math.round(daysF);
        String dayStatus = "";
        if (days == 0) {
            dayStatus = "Today (" + notifInsertDate + ")";
        } else if (days == 1) {
            dayStatus = "Yesterday (" + notifInsertDate + ")";
        } else {
            dayStatus = days + " days ago (" + notifInsertDate + ")";
        }
        holder.tv_date.setText(dayStatus);
        holder.ll_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String messsageStatus = notification.getNotifReadStatus();
                if (messsageStatus.matches("unread")) {
                    notification.setNotifReadStatus("read");
                    FirebaseHandler firebaseHandler = new FirebaseHandler();
                    DatabaseReference notifRef = firebaseHandler.getUserRef().child("notification").child(notification.getNotifID());
                    notifRef.setValue(notification);
                }

                String notifCode = notification.getNotifCode();
                String[] splitIdArr = notifCode.split("/", -2);
                String reminderType = splitIdArr[0];
                String itemId = splitIdArr[1];
                Log.i("reminderType :", reminderType);
                Log.i("itemId :", itemId);
                ViewAlertedItemFragment fragment = new ViewAlertedItemFragment();
                Bundle bundle = new Bundle();
                bundle.putString("alertType", reminderType);
                bundle.putString("itemId", itemId);
                fragment.setArguments(bundle);
                FragmentHandler.loadFragment(fragment, context);
            }
        });

        boolean visible = mSelectedItems.get(position);
        boolean cbChecked = cbArray.get(position);
        holder.cb_box.setVisibility(visible ? View.VISIBLE : View.GONE);
        holder.cb_box.setChecked(cbChecked);

        holder.ll_main.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                int itemCount = getItemCount();

                showCB = !showCB;
                for (int i = 0; i <= itemCount; i++) {
                    setItemVisibilityByPosition(i, showCB);
                }
                return true;
            }
        });
        holder.cb_box.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                cbArray.put(position, b);

            }
        });
    }

    @Override
    public int getItemCount() {
        return notificationsList.size();
    }

    public void setItemVisibilityByPosition(int position, boolean visible) {
        mSelectedItems.put(position, visible);
        notifyItemChanged(position);
    }

    public void deleteCheckedBox() {

        int itemCount = getItemCount();
        for (int i = 0; i <= itemCount; i++) {

            if (cbArray.get(i)) {
                final Notification notification = notificationsList.get(i);
                DatabaseReference deletCBRef = fh.getUserRef().child("notification").
                        child(notification.getNotifID());
                final int finalI = i;
                deletCBRef.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        cbArray.put(finalI, false);
                        notifyItemChanged(finalI);
                        Toast.makeText(context, "Notification deleted", Toast.LENGTH_LONG).show();

                    }
                });
            }
        }


    }

    public static class HomeViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_messageStatus;
        TextView tv_message, tv_date;
        LinearLayout ll_main;
        CheckBox cb_box;

        public HomeViewHolder(@NonNull View itemView) {
            super(itemView);
            iv_messageStatus = itemView.findViewById(R.id.iv_messageStatus);
            tv_message = itemView.findViewById(R.id.tv_message);
            tv_date = itemView.findViewById(R.id.tv_date);
            ll_main = itemView.findViewById(R.id.ll_main);
            cb_box = itemView.findViewById(R.id.cb_box);

        }
    }
}
