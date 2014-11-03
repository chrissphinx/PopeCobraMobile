package com.popecobra.mobile;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Map;

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
    private static final int OBJECT_ID = 73;
    @InjectView(R.id.response) TextView mResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apitest);
        ButterKnife.inject(this);

        final API api = new RestAdapter.Builder()
            .setEndpoint("http://api.popecobra.com/1.0")
            .setRequestInterceptor(new RequestInterceptor() {
                @Override
                public void intercept(RequestFacade request) {
                    request.addHeader("X-Token", "test3");
                }
            })
            .build()
            .create(API.class);

        api.getFields(OBJECT_ID, new Callback<JsonObject>() {
            @Override
            public void success(JsonObject object, Response response) {
                final JsonArray fields = object.getAsJsonArray("fields");

                api.getRecords(OBJECT_ID, new Callback<JsonObject>() {
                    @Override
                    public void success(JsonObject object, Response response) {
                        Type listType = new TypeToken<ArrayList<Map<String, String>>>() {}.getType();
                        ArrayList<Map<String, String>> list = new Gson().fromJson(object.get("values"), listType);
                        for (Map<String, String> item : list) {
                            for (JsonElement field : fields) {
                                String name = field.getAsJsonObject().get("name").getAsString();
                                Log.d(TAG, name + ": " + item.get(name));
                            }
                            Log.d(TAG, "--------------------");
                        }

                        mResponse.setText(object.toString());
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Log.e(TAG, error.getMessage());
                    }
                });
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e(TAG, error.getMessage());
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

        @GET("/objects/{id}/fields")
        void getFields(@Path("id") int id, Callback<JsonObject> callback);
    }
}
