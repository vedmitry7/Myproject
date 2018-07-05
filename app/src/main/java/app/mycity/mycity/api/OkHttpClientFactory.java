package app.mycity.mycity.api;

import okhttp3.OkHttpClient;

public class OkHttpClientFactory {
    private static OkHttpClient client;

    public synchronized static OkHttpClient getClient() {
        if (client != null)
            return client;
        client = new OkHttpClient();
        return client;

    }

}
