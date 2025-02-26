/*
 * Copyright (C) 2021 The Android Open Source Project
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

package com.android.internal.gmscompat;

import android.app.Application;
import android.os.Build;
import android.os.SystemProperties;
import android.util.Log;

import java.lang.reflect.Field;

/** @hide */
public final class AttestationHooks {
    private static final String TAG = "GmsCompat/Attestation";

    private static final String PACKAGE_GMS = "com.google.android.gms";
    private static final String PACKAGE_FINSKY = "com.android.vending";

    private AttestationHooks() { }

    private static void setBuildField(String key, String value) {
        try {
            // Unlock
            Field field = Build.VERSION.class.getDeclaredField(key);
            field.setAccessible(true);

            // Edit
            field.set(null, value);

            // Lock
            field.setAccessible(false);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            Log.e(TAG, "Failed to spoof Build." + key, e);
        }
    }

    private static void setCertifiedIdentifiers() {
        String securityPatch = SystemProperties.get("ro.rabbitescape.version.security_patch", "");
        String device = SystemProperties.get("ro.rabbitescape.device", "");
	String model = SystemProperties.get("ro.rabbitescape.model", "");
	String fingerprint = SystemProperties.get("ro.rabbitescape.fingerprint", "");
        if (!securityPatch.isEmpty()) {
            setBuildField("SECURITY_PATCH", securityPatch);
	}
        if (!device.isEmpty()) {
            setBuildField("DEVICE", device);
	}
        if (!model.isEmpty()) {
            setBuildField("MODEL", model);
	}
        if (!fingerprint.isEmpty()) {
            setBuildField("FINGERPRINT", fingerprint);
	}
    }

    public static void initApplicationBeforeOnCreate(Application app) {
        if (PACKAGE_GMS.equals(app.getPackageName())) {
            setCertifiedIdentifiers();
        }
        if (PACKAGE_FINSKY.equals(app.getPackageName())) {
            setCertifiedIdentifiers();
        }
    }
}
