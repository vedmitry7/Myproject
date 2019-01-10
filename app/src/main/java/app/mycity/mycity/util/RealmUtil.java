package app.mycity.mycity.util;

import android.util.Log;

import app.mycity.mycity.api.model.RealmUser;
import io.realm.Realm;

public class RealmUtil {

    public static void createOrUpdateRealmUser(String userId, String name, String photoUrl){
        Realm mRealm = Realm.getDefaultInstance();
        RealmUser user = mRealm.where(RealmUser.class).equalTo("id", userId).findFirst();

        mRealm.beginTransaction();
        if(user==null){
            Log.d("TAG21", "user  " + userId + " null, add to db");
            RealmUser user1 = mRealm.createObject(RealmUser.class, userId);
            user1.setFirstName(name);
            user1.setPhoto130(photoUrl);
        } else {
            RealmUser realmUser = new RealmUser();
            realmUser.setId(userId);
            realmUser.setFirstName(name);
            realmUser.setPhoto130(photoUrl);
            mRealm.copyToRealmOrUpdate(realmUser);
            Log.d("TAG21", "user  " + name + " update");
        }
        mRealm.commitTransaction();

        mRealm.close();
    }


}
