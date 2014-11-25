package com.popecobra.mobile;

import android.app.ListFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
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

public class ObjectViewerFragment extends ListFragment
{
    private static final String TAG    = "ObjectViewerFragment";
    private static final int OBJECT_ID = 73;

    @InjectView(android.R.id.list) ListView mListView;
    private ArrayAdapter mAdapter;
    private ArrayList<Map<String, String>> mEntries;

    public ObjectViewerFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mEntries = new ArrayList<Map<String, String>>();
        mAdapter = new ObjectViewerAdapter(mEntries);

        final Cobra cobra = new RestAdapter.Builder()
            .setEndpoint("http://api.popecobra.com/1.0")
            .setRequestInterceptor(new RequestInterceptor() {
                @Override
                public void intercept(RequestFacade request) {
                    request.addHeader("X-Token", "test3");
                }
            })
            .build()
            .create(Cobra.class);

        cobra.getFields(OBJECT_ID, new Callback<JsonObject>() {
            @Override
            public void success(JsonObject object, Response response) {
                final JsonArray fields = object.getAsJsonArray("fields");

                cobra.getRecords(OBJECT_ID, new Callback<JsonObject>() {
                    @Override
                    public void success(JsonObject object, Response response) {
                        Type listType = new TypeToken<ArrayList<Map<String, String>>>() {}.getType();
                        ArrayList<Map<String, String>> list = new Gson().fromJson(object.get("values"), listType);
                        for (Map<String, String> item : list) {
                            HashMap<String, String> map = new HashMap<String, String>();
                            for (JsonElement field : fields) {
                                String name = field.getAsJsonObject().get("name").getAsString();
                                Log.d(TAG, name + ": " + item.get(name));
                                map.put(name, item.get(name));
                            } mEntries.add(map);
                            Log.d(TAG, "--------------------");
                        } mAdapter.notifyDataSetChanged();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_objectrecord_list, container, false);
        ButterKnife.inject(this, view);

        mListView.setAdapter(mAdapter);

        return view;
    }

    public class ObjectViewerAdapter extends ArrayAdapter<Map<String, String>>
    {
        public ObjectViewerAdapter(ArrayList<Map<String, String>> objects) {
            super(getActivity(), 0, objects);
        }

        @Override
        public boolean isEmpty() {
            ((TextView) mListView.getEmptyView()).setText("No Records");
            return super.isEmpty();
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            ViewHolder holder;
            if (view != null) {
                holder = (ViewHolder) view.getTag();
            } else {
                view = getActivity().getLayoutInflater().inflate(android.R.layout.simple_list_item_1, parent, false);
                holder = new ViewHolder(view);
                view.setTag(view);
            }

            holder.entry.setText(this.getItem(position).get("nonprofits-1"));

            return view;
        }

        class ViewHolder
        {
            @InjectView(android.R.id.text1) TextView entry;

            public ViewHolder(View view) {
                ButterKnife.inject(this, view);
            }
        }
    }

    private interface Cobra {
        @GET("/objects/{id}/records")
        void getRecords(@Path("id") int id, Callback<JsonObject> callback);

        @GET("/objects/{id}/fields")
        void getFields(@Path("id") int id, Callback<JsonObject> callback);
    }
}
