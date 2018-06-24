package com.nexterp.NLP;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.MediaRouteActionProvider;
import android.support.v7.app.MediaRouteButton;
import android.support.v7.media.MediaRouteSelector;
import android.support.v7.media.MediaRouter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.cast.CastDevice;
import com.google.android.gms.cast.CastMediaControlIntent;

/**
 * Created by Dell on 29-06-2017.
 */

public class MainActivity2 extends AppCompatActivity {

    private static final String TAG = "MainActivity2";
    protected static final String INTENT_EXTRA_CAST_DEVICE = "CastDevice";

    private MediaRouter mMediaRouter;
    private MediaRouteSelector mMediaRouteSelector;
    private MediaRouteButton mMediaRouteButton;
    private int mRouteCount = 0;
 //   private MediaRouterButtonView mMediaRouterButtonView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_main_text);
       // setFullScreen();

        mMediaRouteSelector = new MediaRouteSelector.Builder()
                .addControlCategory(
                        CastMediaControlIntent.categoryForCast(getString(R.string.app_id)))
                .build();
        mMediaRouter = MediaRouter.getInstance(getApplicationContext());
    }

    private void setFullScreen() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mMediaRouter.addCallback(mMediaRouteSelector, mMediaRouterCallback,
                MediaRouter.CALLBACK_FLAG_REQUEST_DISCOVERY);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mMediaRouter.removeCallback(mMediaRouterCallback);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu, menu);
        MenuItem mediaRouteMenuItem = menu.findItem(R.id.media_route_menu_item);
        MediaRouteActionProvider mediaRouteActionProvider
                = (MediaRouteActionProvider) MenuItemCompat
                .getActionProvider(mediaRouteMenuItem);

        // Set the MediaRouteActionProvider selector for device discovery.

        mediaRouteActionProvider.setRouteSelector(mMediaRouteSelector);
        return true;
    }
    private final MediaRouter.Callback mMediaRouterCallback =
            new MediaRouter.Callback() {

                @Override
                public void onRouteSelected(MediaRouter router, MediaRouter.RouteInfo info) {
                    Log.d(TAG, "onRouteSelected");
                    CastDevice castDevice = CastDevice.getFromBundle(info.getExtras());
                    if (castDevice != null) {
                        Intent intent = new Intent(MainActivity2.this,
                                SecondScreen.class);
                        intent.putExtra(INTENT_EXTRA_CAST_DEVICE, castDevice);
                        startActivity(intent);
                    }
                }

                @Override
                public void onRouteUnselected(MediaRouter router, MediaRouter.RouteInfo info) {
                }
            };


}