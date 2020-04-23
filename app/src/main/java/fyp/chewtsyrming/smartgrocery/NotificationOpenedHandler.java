package fyp.chewtsyrming.smartgrocery;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.onesignal.OSNotificationAction;
import com.onesignal.OSNotificationOpenResult;
import com.onesignal.OneSignal;

import org.json.JSONObject;

public class NotificationOpenedHandler implements OneSignal.NotificationOpenedHandler {
    private Context context;

    public NotificationOpenedHandler(Context context) {
        this.context = context;
    }

    // This fires when a notification is opened by tapping on it.
    @Override
    public void notificationOpened(OSNotificationOpenResult result) {
        OSNotificationAction.ActionType actionType = result.action.type;
        JSONObject data = result.notification.payload.additionalData;
        String customKey;

        Log.i("OSNotificationPayload", "result.notification.payload.toJSONObject().toString(): " + data);


        if (data != null) {
            customKey = data.optString("customkey", null);
            if (customKey != null)
                Log.i("OneSignalExample", "customkey set with value: " + customKey);
        }

        if (actionType == OSNotificationAction.ActionType.ActionTaken) {
        }
        Log.i("OneSignalExample", "Button pressed with id: " + actionType);
        if (result.action.actionID != null) {
            String receivedId = result.action.actionID;
            String[] splitIdArr = receivedId.split("/", -2);
            String reminderType = splitIdArr[0];
            String itemId = splitIdArr[1];
            Log.i("OneSignalExample", "reminderType: " + reminderType);
            Log.i("OneSignalExample", "itemId: " + itemId);
            Intent intent = new Intent(context, home.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("alertType", reminderType);
            intent.putExtra("itemId", itemId);
            context.startActivity(intent);
        } else {
            String reminderType = "notif";
            Intent intent = new Intent(context, home.class);
            intent.putExtra("alertType", reminderType);

            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }


        // The following can be used to open an Activity of your choice.
        // Replace - getApplicationContext() - with any Android Context.


        // Add the following to your AndroidManifest.xml to prevent the launching of your main Activity
        //   if you are calling startActivity above.
     /*
        <application ...>
          <meta-data android:name="com.onesignal.NotificationOpened.DEFAULT" android:value="DISABLE" />
        </application>
     */
    }
}
