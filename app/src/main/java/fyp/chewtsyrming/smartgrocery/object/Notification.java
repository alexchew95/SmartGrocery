package fyp.chewtsyrming.smartgrocery.object;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;

public class Notification {
    public static Comparator<Notification> sortInsertDateDesc = new Comparator<Notification>() {
        @Override
        public int compare(Notification o1, Notification o2) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
            Date d1 = null;
            Date d2 = null;
            try {
                d1 = sdf.parse(o1.getNotifInsertDate());
                d2 = sdf.parse(o2.getNotifInsertDate());
            } catch (ParseException e) {
                e.printStackTrace();
            }

            return (d2.compareTo(d1));
        }
    };
    String notifID, notifMessage, notifReadStatus, notifInsertDate, notifReadDate, notifCode;

    public Notification() {
    }

    public String getNotifID() {
        return notifID;
    }

    public void setNotifID(String notifID) {
        this.notifID = notifID;
    }

    public String getNotifMessage() {
        return notifMessage;
    }

    public void setNotifMessage(String notifMessage) {
        this.notifMessage = notifMessage;
    }

    public String getNotifReadStatus() {
        return notifReadStatus;
    }

    public void setNotifReadStatus(String notifReadStatus) {
        this.notifReadStatus = notifReadStatus;
    }

    public String getNotifInsertDate() {
        return notifInsertDate;
    }

    public void setNotifInsertDate(String notifInsertDate) {
        this.notifInsertDate = notifInsertDate;
    }

    public String getNotifReadDate() {
        return notifReadDate;
    }

    public void setNotifReadDate(String notifReadDate) {
        this.notifReadDate = notifReadDate;
    }

    public String getNotifCode() {
        return notifCode;
    }

    public void setNotifCode(String notifCode) {
        this.notifCode = notifCode;
    }
}
