package fyp.chewtsyrming.smartgrocery.object;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseHandler {
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    UserModel um=new UserModel();
    DatabaseReference userRef= database.getReference().child("user").child(um.getUserIDFromDataBase());

    public DatabaseReference getRef() {
        return ref;
    }

    DatabaseReference ref=database.getReference();
    public FirebaseDatabase getDatabase() {
        return database;
    }




    public DatabaseReference getUserRef() {
        return userRef;
    }

    public void setUserRef(DatabaseReference userRef) {
        this.userRef = userRef;
    }
}
