package com.google.android.systemui.smartspace.logging;

import java.util.Objects;

public final class BcSmartspaceCardLoggingInfo {
    public final int mCardinality;
    public final int mDisplaySurface;
    public int mFeatureType;
    public int mInstanceId;
    public final int mRank;
    public final int mReceivedLatency;
    public BcSmartspaceSubcardLoggingInfo mSubcardInfo;
    public final int mUid;

    public static final class Builder {
        public int mCardinality;
        public int mDisplaySurface = 1;
        public int mFeatureType;
        public int mInstanceId;
        public int mRank;
        public int mReceivedLatency;
        public BcSmartspaceSubcardLoggingInfo mSubcardInfo;
        public int mUid;
    }

    public final boolean equals(Object obj) {
        boolean z = true;
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof BcSmartspaceCardLoggingInfo)) {
            return false;
        }
        BcSmartspaceCardLoggingInfo bcSmartspaceCardLoggingInfo = (BcSmartspaceCardLoggingInfo) obj;
        if (mInstanceId != bcSmartspaceCardLoggingInfo.mInstanceId || mDisplaySurface != bcSmartspaceCardLoggingInfo.mDisplaySurface || mRank != bcSmartspaceCardLoggingInfo.mRank || mCardinality != bcSmartspaceCardLoggingInfo.mCardinality || mFeatureType != bcSmartspaceCardLoggingInfo.mFeatureType || mReceivedLatency != bcSmartspaceCardLoggingInfo.mReceivedLatency || mUid != bcSmartspaceCardLoggingInfo.mUid || !Objects.equals(mSubcardInfo, bcSmartspaceCardLoggingInfo.mSubcardInfo)) {
            z = false;
        }
        return z;
    }

    public final String toString() {
        StringBuilder m = LogBuilder.m("instance_id = ");
        m.append(mInstanceId);
        m.append(", feature type = ");
        m.append(mFeatureType);
        m.append(", display surface = ");
        m.append(mDisplaySurface);
        m.append(", rank = ");
        m.append(mRank);
        m.append(", cardinality = ");
        m.append(mCardinality);
        m.append(", receivedLatencyMillis = ");
        m.append(mReceivedLatency);
        m.append(", uid = ");
        m.append(mUid);
        m.append(", subcardInfo = ");
        m.append(mSubcardInfo);
        return m.toString();
    }

    public BcSmartspaceCardLoggingInfo(Builder builder) {
        mInstanceId = builder.mInstanceId;
        mDisplaySurface = builder.mDisplaySurface;
        mRank = builder.mRank;
        mCardinality = builder.mCardinality;
        mFeatureType = builder.mFeatureType;
        mReceivedLatency = builder.mReceivedLatency;
        mUid = builder.mUid;
        mSubcardInfo = builder.mSubcardInfo;
    }

    public final int hashCode() {
        return Objects.hash(Integer.valueOf(mInstanceId), Integer.valueOf(mDisplaySurface), Integer.valueOf(mRank), Integer.valueOf(mCardinality), Integer.valueOf(mFeatureType), Integer.valueOf(mReceivedLatency), Integer.valueOf(mUid), mSubcardInfo);
    }
}
