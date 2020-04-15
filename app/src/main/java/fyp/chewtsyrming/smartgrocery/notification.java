package fyp.chewtsyrming.smartgrocery;

import android.app.Application;

import com.onesignal.OneSignal;
public class notification extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        // OneSignal Initialization
        OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .setNotificationOpenedHandler(new NotificationOpenedHandler(this))
                .init();
    }
}