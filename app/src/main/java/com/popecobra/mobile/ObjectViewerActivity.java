package com.popecobra.mobile;

import android.app.Activity;
import android.os.Bundle;

public class ObjectViewerActivity extends Activity
{

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        setContentView(R.layout.activity_object_viewer);
        if (bundle == null) {
            getFragmentManager().beginTransaction()
                .add(R.id.container, new ObjectViewerFragment())
                .commit();
        }
    }
}
