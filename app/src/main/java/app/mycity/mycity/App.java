package app.mycity.mycity;

import android.app.Application;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        PersistantStorage.init(this);
    }
}
