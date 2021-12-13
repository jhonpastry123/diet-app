package com.diet.trinity;

import android.app.Application;
import android.content.Context;

import com.diet.trinity.provider.JacksonProvider;
import com.diet.trinity.provider.RetrofitProvider;
import com.pixplicity.easyprefs.library.Prefs;
import com.vaibhavpandey.katora.Container;
import com.vaibhavpandey.katora.contracts.ImmutableContainer;

public class MainApplication extends Application {
    private static final Container CONTAINER = new Container();
    private static final String TAG = "MainApplication";

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }

    public static ImmutableContainer getContainer() {
        return CONTAINER;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        new Prefs.Builder()
                .setContext(this)
                .setUseDefaultSharedPreference(true)
                .build();
        CONTAINER.install(new RetrofitProvider(this));
        CONTAINER.install(new JacksonProvider());
        System.setProperty("https.protocols", "TLSv1,TLSv1.1,TLSv1.2");
    }

}
