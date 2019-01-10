package app.mycity.mycity.api;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import app.mycity.mycity.Constants;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class ApiRetrofitFactory {

    private static ApiRetrofitInterface mApi;

    public static ApiRetrofitInterface getApi(){
        if(mApi==null){

            Retrofit builder = new Retrofit.Builder()
                    .baseUrl(Constants.URL_BASE)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build();
            mApi = builder.create(ApiRetrofitInterface.class);
        }

        return mApi;
    }
}
