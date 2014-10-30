package com.popecobra.mobile;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.gson.JsonObject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.Callback;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.http.GET;
import retrofit.http.Path;

public class APITest extends Activity {
    private static final String TAG = "APITest";
    @InjectView(R.id.response) TextView mResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apitest);
        ButterKnife.inject(this);

        API api = new RestAdapter.Builder()
            .setEndpoint("http://api.popecobra.com/1.0")
            .setRequestInterceptor(new RequestInterceptor() {
                @Override
                public void intercept(RequestFacade request) {
                    request.addHeader("X-Token", "test3");
                }
            })
            .build()
            .create(API.class);

        api.getRecords(126, new Callback<JsonObject>() {
            @Override
            public void success(JsonObject jsonObject, Response response) {
                mResponse.setText(jsonObject.toString());
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e(TAG, error.toString());
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.apitest, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        return R.id.action_settings == id || super.onOptionsItemSelected(item);
    }

    private interface API {
        @GET("/objects/{id}/records")
        void getRecords(@Path("id") int id, Callback<JsonObject> callback);
    }
}
