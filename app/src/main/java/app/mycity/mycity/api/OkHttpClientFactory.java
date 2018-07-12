package app.mycity.mycity.api;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

public class OkHttpClientFactory {
    private static OkHttpClient client;

    public synchronized static OkHttpClient getClient() {
        if (client != null)
            return client;
        client = new OkHttpClient();
        client.newBuilder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
        return client;

    }

}
