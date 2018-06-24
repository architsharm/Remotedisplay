package com.nexterp.NLP;

import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.MediaRouteActionProvider;
import android.support.v7.media.MediaRouteSelector;
import android.support.v7.media.MediaRouter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.cast.CastDevice;
import com.google.android.gms.cast.CastMediaControlIntent;
import com.google.android.gms.cast.CastRemoteDisplayLocalService;
import com.google.android.gms.common.api.Status;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "LocalActivity";
    protected static final String INTENT_EXTRA_CAST_DEVICE = "CastDevice";
    private MediaRouter mMediaRouter;
    private MediaRouteSelector mMediaRouteSelector;
    private CastDevice castDevice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_text);

        setupMediaRouter();
    }
    @Override
    protected void onResume() {
        super.onResume();
        if (!isRemoteDisplaying()) {
            if (castDevice != null) {
                startRemoteDisplayService(castDevice);
            }
        }
    }

    @Override
    protected void onStop() {
        // End media router discovery
        mMediaRouter.removeCallback(mMediaRouterCallback);
        super.onStop();
    }
    @Override
    public void onDestroy() {
        if (mMediaRouter != null) {
            mMediaRouter.removeCallback(mMediaRouterCallback);
        }
        super.onDestroy();
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
    private void setupMediaRouter() {
        Log.e(TAG,"Setupmediarouter");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            mMediaRouter = MediaRouter.getInstance(getApplicationContext());
            mMediaRouteSelector = new MediaRouteSelector.Builder().addControlCategory(
                    CastMediaControlIntent.categoryForCast(getString(R.string.app_id))).build();
            if (isRemoteDisplaying()) {
                this.castDevice = CastDevice.getFromBundle(mMediaRouter.getSelectedRoute().getExtras());
            } else {
                Bundle extras = getIntent().getExtras();
                if (extras != null) {
                    castDevice = extras.getParcelable(INTENT_EXTRA_CAST_DEVICE);
                }
            }

            mMediaRouter.addCallback(mMediaRouteSelector, mMediaRouterCallback,
                    MediaRouter.CALLBACK_FLAG_REQUEST_DISCOVERY);
        }
    }
    private boolean isRemoteDisplaying() {
        return CastRemoteDisplayLocalService.getInstance() != null;
    }
    private MediaRouter.Callback mMediaRouterCallback = new MediaRouter.Callback() {
        @Override
        public void onRouteSelected(MediaRouter router, MediaRouter.RouteInfo route) {
            startRemoteDisplayService(CastDevice.getFromBundle(route.getExtras()));
        }

        @Override
        public void onRouteUnselected(MediaRouter router, MediaRouter.RouteInfo route) {
            CastRemoteDisplayLocalService.stopService();
        }
    };

    private void startRemoteDisplayService(CastDevice castDevice) {
        Log.e(TAG,"Remote Display Service Started");
        Intent intent = new Intent(MainActivity.this,
                MainActivity.class); Log.e(TAG,"intent manifested");
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent notificationPendingIntent = PendingIntent.getActivity(
                MainActivity.this, 0, intent, 0);

        CastRemoteDisplayLocalService.NotificationSettings settings =
                new CastRemoteDisplayLocalService.NotificationSettings.Builder()
                        .setNotificationPendingIntent(notificationPendingIntent).build();
        Log.e(TAG,"notification settings");
        CastRemoteDisplayLocalService.startService(
                getApplicationContext(),
                RemoteDisplayService.class, getString(R.string.app_id),
                castDevice, settings,
                new CastRemoteDisplayLocalService.Callbacks() {

                    @Override
                    public void onServiceCreated(CastRemoteDisplayLocalService service) {
                        Log.d(TAG, "onServiceCreated");
                    }

                    @Override
                    public void onRemoteDisplaySessionStarted(
                            CastRemoteDisplayLocalService service) {
                        Log.d(TAG, "onServiceStarted");
                    }

                    @Override
                    public void onRemoteDisplaySessionError(Status errorReason) {
                        int code = errorReason.getStatusCode();
                        Log.d(TAG, "onServiceError: " + errorReason.getStatusCode());
                        initError();
                        MainActivity.this.castDevice = null;
                        MainActivity.this.finish();
                    }
                });
    }

    private void initError() {
        Toast toast = Toast.makeText(
                getApplicationContext(), R.string.init_error, Toast.LENGTH_SHORT);
        if (mMediaRouter != null) {
            mMediaRouter.selectRoute(mMediaRouter.getDefaultRoute());
        }
        toast.show();
    }
}
