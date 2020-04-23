package fyp.chewtsyrming.smartgrocery;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import fyp.chewtsyrming.smartgrocery.object.UserModel;

public class FirebaseHandler {
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    UserModel um=new UserModel();
    DatabaseReference userRef= database.getReference().child("user").child(um.getUserIDFromDataBase());
    DatabaseReference goodsRef = database.getReference().child("user").child(um.getUserIDFromDataBase()).child("goods");
    DatabaseReference goodsDataRef = database.getReference().child("user").child(um.getUserIDFromDataBase()).child("goodsData");
    DatabaseReference shoppingRef = database.getReference().child("user").child(um.getUserIDFromDataBase()).child("shoppingPlan");

    public DatabaseReference getRef() {
        return ref;
    }

    public DatabaseReference getGoodsRef() {
        return goodsRef;
    }

    public DatabaseReference getGoodsDataRef() {
        return goodsDataRef;
    }

    public DatabaseReference getShoppingRef() {
        return shoppingRef;
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
