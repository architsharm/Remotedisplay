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

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;

import com.google.android.gms.cast.CastPresentation;

/**
 * The presentation to show on the first screen (the TV).
 * <p>
 * Note that this display may have different metrics from the display on
 * which the main activity is showing so we must be careful to use the
 * presentation's own {@link Context} whenever we load resources.
 * </p>
 */
public class RemoteDisplayPresentation extends CastPresentation {

    private static final String TAG = "RDPresentatio2";


    public RemoteDisplayPresentation(Context serviceContext, Display display) {
        super(serviceContext, display);
        Log.e(TAG,"Presentation Started");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.e(TAG,"Remote Display Presentation Started");
        super.onCreate(savedInstanceState);
        Log.e(TAG,"Remote Display Presentation Started");
        setContentView(R.layout.presentation_remote);
        Log.e(TAG,"Remote Display Presentation ended");
    }


}
