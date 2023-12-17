package com.google.android.systemui.smartspace;

import android.provider.DeviceConfig;

public final class LazyServerFlagLoader {
    private static final String TAG = "LazyServerFlagLoader";

    public final String mPropertyKey;
    public Boolean mValue = null;

    public LazyServerFlagLoader(String key) {
        mPropertyKey = key;
    }

    public boolean get() {
        if (mValue == null) {
            mValue = Boolean.valueOf(DeviceConfig.getBoolean("launcher", mPropertyKey, true));
            DeviceConfig.addOnPropertiesChangedListener("launcher", (v0) -> {
                v0.run();
            }, properties -> {
                if (properties.getKeyset().contains(mPropertyKey)) {
                    mValue = Boolean.valueOf(properties.getBoolean(mPropertyKey, true));
                }
            });
        }
        return mValue.booleanValue();
    }
}
