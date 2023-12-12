package com.google.android.systemui.smartspace.logging;

import java.util.Objects;

public final class BcSmartspaceCardMetadataLoggingInfo {

    public final int mCardTypeId;
    public final int mInstanceId;

    public static final class Builder {
        public int mCardTypeId;
        public int mInstanceId;
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof BcSmartspaceCardMetadataLoggingInfo)) {
            return false;
        }

        BcSmartspaceCardMetadataLoggingInfo bcSmartspaceCardMetadataLoggingInfo = (BcSmartspaceCardMetadataLoggingInfo) obj;
        if (mInstanceId != bcSmartspaceCardMetadataLoggingInfo.mInstanceId || mCardTypeId != bcSmartspaceCardMetadataLoggingInfo.mCardTypeId) {
            return false;
        }

        return true;
    }

    public final String toString() {
        StringBuilder m = LogBuilder.m("BcSmartspaceCardMetadataLoggingInfo{mInstanceId=");
        m.append(mInstanceId);
        m.append(", mCardTypeId=");
        return LogBuilder.m(m, mCardTypeId, '}');
    }

    public BcSmartspaceCardMetadataLoggingInfo(Builder builder) {
        mInstanceId = builder.mInstanceId;
        mCardTypeId = builder.mCardTypeId;
    }

    public final int hashCode() {
        return Objects.hash(Integer.valueOf(mInstanceId), Integer.valueOf(mCardTypeId));
    }
}
