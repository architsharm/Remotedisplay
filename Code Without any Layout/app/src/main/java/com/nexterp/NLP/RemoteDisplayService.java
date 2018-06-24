/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.nexterp.NLP;

import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import com.google.android.gms.cast.CastRemoteDisplayLocalService;

/**
 * Service to keep the remote display running even when the app goes into the background
 */
public class RemoteDisplayService extends CastRemoteDisplayLocalService {

    private static final String TAG = "RDPresentation";
    private RemoteDisplayPresentation mPresentation;
 //Log.e(TAG,"Remote Display Service Started");
    @Override
     public void onCreate(){
        Log.e(TAG,"Service Started");
        }
    @Override
    public void onCreatePresentation(Display display) {
        dismissPresentation();
        Log.e(TAG,"mpresentation");
        mPresentation = new RemoteDisplayPresentation(this, display);

        try {
            mPresentation.show();
            Log.e(TAG, "Presentation Show");
        } catch (WindowManager.InvalidDisplayException ex) {
            Log.e(TAG, "Unable to show presentation, display was " +
                    "removed.", ex);
            dismissPresentation();
        }
        Log.e(TAG,"Remote Display Service Started");
    }
    @Override
    public void onDismissPresentation() {
        Log.e(TAG, "dismissed");
        dismissPresentation();

    }
    private void dismissPresentation() {
        if (mPresentation != null) {
            mPresentation.dismiss();
            mPresentation = null;
        }
    }
}
