package fyp.chewtsyrming.smartgrocery.object;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class UserModel {

    public String uid;
    public String name;
    public String email;
    public String imageurl;
    FirebaseUser user;

    public UserModel() {

    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUserIDFromDataBase() {
        user = FirebaseAuth.getInstance().getCurrentUser();
        uid = user.getUid();
        return uid;
    }
}
