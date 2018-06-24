package com.nexterp.NLP1;

import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
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

import java.util.ArrayList;
import java.util.List;

public class MainActivity3 extends AppCompatActivity implements CustomSimpleOnPageChangeListener.OnPageChangePosition {
    private static final String TAG = "LocalActivity";
    protected static final String INTENT_EXTRA_CAST_DEVICE = "CastDevice";
    private MediaRouter mMediaRouter;
    private MediaRouteSelector mMediaRouteSelector;
    private CastDevice castDevice;
    private int currentPosition;
    private ScreenSlidePagerAdapter fragmentStatePagerAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        List<AdViewModel> list = getAdViewModels();

        ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        fragmentStatePagerAdapter =
                new ScreenSlidePagerAdapter(getSupportFragmentManager());
        fragmentStatePagerAdapter.addAds(list);
        CustomSimpleOnPageChangeListener customSimpleOnPageChangeListener =
                new CustomSimpleOnPageChangeListener(this);
        if (viewPager != null) {
            viewPager.setAdapter(fragmentStatePagerAdapter);
            viewPager.addOnPageChangeListener(customSimpleOnPageChangeListener);
        }
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
        Intent intent = new Intent(MainActivity3.this,
                MainActivity3.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent notificationPendingIntent = PendingIntent.getActivity(
                MainActivity3.this, 0, intent, 0);
        Log.e(TAG, "intent manifested");
        CastRemoteDisplayLocalService.NotificationSettings settings =
                new CastRemoteDisplayLocalService.NotificationSettings.Builder()
                        .setNotificationPendingIntent(notificationPendingIntent).build();

        CastRemoteDisplayLocalService.startService(
                getApplicationContext(),
                RemoteDisplayService2.class, getString(R.string.app_id),
                castDevice, settings,
                new CastRemoteDisplayLocalService.Callbacks() {
                    @Override
                    public void onServiceCreated(CastRemoteDisplayLocalService service) {
                        Log.d(TAG, "onServiceCreated");
                        ((RemoteDisplayService2) service).setAdViewModel(
                                fragmentStatePagerAdapter.getAdAt(currentPosition));
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
                        MainActivity3.this.castDevice = null;
                        MainActivity3.this.finish();
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
    @Override
    public void onCurrentPageChange(int position) {
        currentPosition = position;
        if (CastRemoteDisplayLocalService.getInstance() != null) {
            ((RemoteDisplayService2) CastRemoteDisplayLocalService.getInstance()).setAdViewModel(
                    fragmentStatePagerAdapter.getAdAt(position));
        }
    }

    @NonNull
    private List<AdViewModel> getAdViewModels() {
        AdViewModel adViewModel1 = new AdViewModel("0", "image", "1",
                "https://coresites-cdn.factorymedia.com/dirt_new/wp-content/uploads/2015/06/solid-strike.jpg");
        AdViewModel adViewModel2 = new AdViewModel("1", "image", "2",
                "https://coresites-cdn.factorymedia.com/dirt_new/wp-content/uploads/2015/06/yt-tues.jpg");
        AdViewModel adViewModel3 = new AdViewModel("2", "image", "3",
                "https://descensonuevoleon.files.wordpress.com/2009/08/tr450_weblarge3.jpg");
        AdViewModel adViewModel4 = new AdViewModel("3", "image", "4",
                "https://coresites-cdn.factorymedia.com/dirt_new/wp-content/uploads/2015/06/lapierre-dh.jpg");
        AdViewModel adViewModel5 = new AdViewModel("4", "image", "5",
                "https://coresites-cdn.factorymedia.com/dirt_new/wp-content/uploads/2015/06/specialized-demo.jpg");
        AdViewModel adViewModel6 = new AdViewModel("5", "image", "6",
                "https://coresites-cdn.factorymedia.com/dirt_new/wp-content/uploads/2015/06/Trek-session-9.9.jpg");
        AdViewModel adViewModel7 = new AdViewModel("6", "image", "7",
                "https://coresites-cdn.factorymedia.com/dirt_new/wp-content/uploads/2015/06/mondraker-summum-pro-team.jpg");
        AdViewModel adViewModel8 = new AdViewModel("7", "image", "8",
                "https://coresites-cdn.factorymedia.com/dirt_new/wp-content/uploads/2015/06/intense-951-evo.jpg");
        AdViewModel adViewModel9 = new AdViewModel("8", "image", "9",
                "https://coresites-cdn.factorymedia.com/dirt_new/wp-content/uploads/2015/06/giant-glory.jpg");

        List<AdViewModel> list = new ArrayList<>();
        list.add(adViewModel1);
        list.add(adViewModel2);
        list.add(adViewModel3);
        list.add(adViewModel4);
        list.add(adViewModel5);
        list.add(adViewModel6);
        list.add(adViewModel7);
        list.add(adViewModel8);
        list.add(adViewModel9);
        return list;
    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        private final List<AdViewModel> ads = new ArrayList<>();

        ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return DetailFragment.newInstance(ads.get(position));
        }

        @Override
        public int getCount() {
            return ads.size();
        }

        public void addAds(List<AdViewModel> ads) {
            this.ads.addAll(ads);
            notifyDataSetChanged();
        }

        public AdViewModel getAdAt(int position) {
            return ads.get(position);
        }
    }
}
