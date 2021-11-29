package com.diet.trinity.provider;

import android.content.Context;
import android.text.TextUtils;

import com.diet.trinity.BuildConfig;
import com.diet.trinity.Utility.Constants;
import com.diet.trinity.R;
import com.diet.trinity.data.api.REST;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pixplicity.easyprefs.library.Prefs;
import com.vaibhavpandey.katora.contracts.Factory;
import com.vaibhavpandey.katora.contracts.ImmutableContainer;
import com.vaibhavpandey.katora.contracts.MutableContainer;
import com.vaibhavpandey.katora.contracts.Provider;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;



public class RetrofitProvider implements Provider {

    private final Context mContext;

    public RetrofitProvider(Context context) {
        mContext = context;
    }

    @Override
    public void provide(MutableContainer container) {
        container.factory(OkHttpClient.Builder.class, new Factory<OkHttpClient.Builder>() {
            @Override
            public OkHttpClient.Builder create(ImmutableContainer c) throws Exception {
                OkHttpClient.Builder builder = new OkHttpClient.Builder()
                        .connectTimeout(Constants.TIMEOUT_CONNECT, TimeUnit.MILLISECONDS)
                        .readTimeout(Constants.TIMEOUT_READ, TimeUnit.MILLISECONDS)
                        .writeTimeout(Constants.TIMEOUT_WRITE, TimeUnit.MILLISECONDS);
                if (BuildConfig.DEBUG) {
                    HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
                    interceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);
                    builder.addInterceptor(interceptor);
                }

                return builder;
            }
        });
        container.factory(Retrofit.Builder.class, new Factory<Retrofit.Builder>() {
            @Override
            public Retrofit.Builder create(ImmutableContainer c) throws Exception {
                ObjectMapper om = c.get(ObjectMapper.class);
                return new Retrofit.Builder()
                        .baseUrl(mContext.getString(R.string.server_url))
                        .addConverterFactory(JacksonConverterFactory.create(om));
            }
        });
        container.factory(Retrofit.class, new Factory<Retrofit>() {
            @Override
            public Retrofit create(ImmutableContainer c) throws Exception {
                OkHttpClient client = c.get(OkHttpClient.Builder.class)
                        .addInterceptor(new Interceptor() {
                            @NotNull
                            @Override
                            public Response intercept(@NotNull Chain chain) throws IOException {
                                Request request = chain.request();
                                String token =
                                        Prefs.getString(Constants.PREF_SERVER_TOKEN, null);
                                if (!TextUtils.isEmpty(token)) {
                                    request = request.newBuilder()
                                            .header("Authorization", "Bearer " + token)
                                            .build();
                                }
                                return chain.proceed(request);
                            }
                        })
                        .build();
                return c.get(Retrofit.Builder.class)
                        .client(client)
                        .build();
            }
        });
        container.singleton(REST.class, new Factory<REST>() {
            @Override
            public REST create(ImmutableContainer c) throws Exception {
                return c.get(Retrofit.class).create(REST.class);
            }
        });
    }
}