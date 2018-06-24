package com.nexterp.NLP1;

/**
 * Created by Dell on 30-06-2017.
 */

import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import com.google.android.gms.cast.CastRemoteDisplayLocalService;

/**
 * Service to keep the remote display running even when the app goes into the background
 */
public class RemoteDisplayService2 extends CastRemoteDisplayLocalService {

    private static final String TAG = "RDPresentation";

    private DetailPresentation mPresentation;
    public static AdViewModel adViewModel;
    @Override
    public void onCreate(){
        super.onCreate();
        Log.e(TAG,"Service Started");
    }
    @Override
    public void onCreatePresentation(Display display) {
        dismissPresentation();
        mPresentation = new DetailPresentation(this, display);
        try {
            mPresentation.show();
        } catch (WindowManager.InvalidDisplayException ex) {
            Log.e(TAG, "Unable to show presentation, display was " +
                    "removed.", ex);
            dismissPresentation();
        }
    }

    private void dismissPresentation() {
        if (mPresentation != null) {
            mPresentation.dismiss();
            mPresentation = null;
        }
    }

    @Override
    public void onDismissPresentation() {
        dismissPresentation();
    }
    public void setAdViewModel(AdViewModel ad) {
        adViewModel = ad;
        if (mPresentation != null) {
            mPresentation.updateAdDetail(ad);
        }
    }
    public static AdViewModel getadViewModel(){
        return adViewModel;
    }
}
