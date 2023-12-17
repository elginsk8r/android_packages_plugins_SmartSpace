package com.google.android.systemui.smartspace;

import java.util.Objects;

public final class SmallHash {
    private static final String TAG = "SmallHash";

    public static int hash(String str) {
        return hash(Objects.hashCode(str));
    }

    public static int hash(int i) {
        return Math.abs(Math.floorMod(i, 8192));
    }
}
