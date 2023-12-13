package com.google.android.systemui.smartspace;

import android.app.smartspace.SmartspaceAction;
import android.app.smartspace.SmartspaceTarget;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.android.app.animation.Interpolators;
import com.android.launcher3.icons.GraphicsUtils;
import com.android.systemui.bcsmartspace.R;
import com.android.systemui.plugins.BcSmartspaceDataPlugin;
import com.google.android.systemui.smartspace.logging.BcSmartspaceCardLoggingInfo;
import com.google.android.systemui.smartspace.logging.BcSmartspaceCardMetadataLoggingInfo;
import com.google.android.systemui.smartspace.logging.BcSmartspaceSubcardLoggingInfo;

import java.util.List;
import java.util.Locale;

public class BcSmartspaceCard extends ConstraintLayout {
    public DoubleShadowTextView mBaseActionIconSubtitleView;
    public IcuDateTextView mDateView;
    public final DoubleShadowIconDrawable mDndIconDrawable;
    public ImageView mDndImageView;
    public float mDozeAmount;
    public BcSmartspaceDataPlugin.SmartspaceEventNotifier mEventNotifier;
    public ViewGroup mExtrasGroup;
    public final DoubleShadowIconDrawable mIconDrawable;
    public int mIconTintColor;
    public boolean mIsDreaming;
    public final DoubleShadowIconDrawable mNextAlarmIconDrawable;
    public ImageView mNextAlarmImageView;
    public TextView mNextAlarmTextView;
    public String mPrevSmartspaceTargetId;
    public BcSmartspaceCardSecondary mSecondaryCard;
    public ViewGroup mSecondaryCardGroup;
    public TextView mSubtitleTextView;
    public SmartspaceTarget mTarget;
    public ViewGroup mTextGroup;
    public TextView mTitleTextView;
    public int mTopPadding;
    public boolean mUsePageIndicatorUi;
    public boolean mValidSecondaryCard;

    public BcSmartspaceCard(Context context) {
        this(context, null);
    }

    public BcSmartspaceCard(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        mSecondaryCard = null;
        mPrevSmartspaceTargetId = "";
        mIconTintColor = GraphicsUtils.getAttrColor(getContext(), 16842806);
        mTextGroup = null;
        mSecondaryCardGroup = null;
        mDateView = null;
        mTitleTextView = null;
        mSubtitleTextView = null;
        mBaseActionIconSubtitleView = null;
        mExtrasGroup = null;
        mDndImageView = null;
        mNextAlarmImageView = null;
        mNextAlarmTextView = null;
        mIsDreaming = false;
        mIconDrawable = new DoubleShadowIconDrawable(context);
        mNextAlarmIconDrawable = new DoubleShadowIconDrawable(context);
        mDndIconDrawable = new DoubleShadowIconDrawable(context);
    }

    public static int getClickedIndex(BcSmartspaceCardLoggingInfo bcSmartspaceCardLoggingInfo, int i) {
        List<BcSmartspaceCardMetadataLoggingInfo> list;
        BcSmartspaceSubcardLoggingInfo bcSmartspaceSubcardLoggingInfo = bcSmartspaceCardLoggingInfo.mSubcardInfo;
        if (bcSmartspaceSubcardLoggingInfo == null || (list = bcSmartspaceSubcardLoggingInfo.mSubcards) == null) {
            return 0;
        }
        for (int i2 = 0; i2 < list.size(); i2++) {
            BcSmartspaceCardMetadataLoggingInfo bcSmartspaceCardMetadataLoggingInfo = list.get(i2);
            if (bcSmartspaceCardMetadataLoggingInfo != null && bcSmartspaceCardMetadataLoggingInfo.mCardTypeId == i) {
                return i2 + 1;
            }
        }
        return 0;
    }

    public final void setDozeAmount(float f) {
        mDozeAmount = f;
        if (mTarget != null && mTarget.getBaseAction() != null && mTarget.getBaseAction().getExtras() != null) {
            Bundle extras = mTarget.getBaseAction().getExtras();
            if (mTitleTextView != null && extras.getBoolean("hide_title_on_aod")) {
                mTitleTextView.setAlpha(1.0f - f);
            }
            if (mSubtitleTextView != null && extras.getBoolean("hide_subtitle_on_aod")) {
                mSubtitleTextView.setAlpha(1.0f - f);
            }
        }
        if (mDndImageView != null) {
            mDndImageView.setAlpha(mDozeAmount);
        }
        if (mTextGroup != null) {
            ViewGroup viewGroup = mSecondaryCardGroup;
            int i = 0;
            int i2 = 1;
            boolean z = mDozeAmount == 1.0f || !mValidSecondaryCard;
            if (z) {
                i = 8;
            }
            BcSmartspaceTemplateDataUtils.updateVisibility(viewGroup, i);
            ViewGroup viewGroup2 = mSecondaryCardGroup;
            if (viewGroup2 != null && viewGroup2.getVisibility() != 8) {
                ViewGroup viewGroup3 = mTextGroup;
                if (!isRtl()) {
                    i2 = -1;
                }
                viewGroup3.setTranslationX(Interpolators.EMPHASIZED.getInterpolation(mDozeAmount) * mSecondaryCardGroup.getWidth() * i2);
                mSecondaryCardGroup.setAlpha(Math.max(0.0f, Math.min(1.0f, ((1.0f - mDozeAmount) * 9.0f) - 6.0f)));
                return;
            }
            mTextGroup.setTranslationX(0.0f);
        }
    }

    public final void setPrimaryTextColor(int i) {
        if (mTitleTextView != null) {
            mTitleTextView.setTextColor(i);
        }
        if (mDateView != null) {
            mDateView.setTextColor(i);
        }
        if (mSubtitleTextView != null) {
            mSubtitleTextView.setTextColor(i);
        }
        if (mBaseActionIconSubtitleView != null) {
            mBaseActionIconSubtitleView.setTextColor(i);
        }
        if (mSecondaryCard != null) {
            mSecondaryCard.setTextColor(i);
        }
        mIconTintColor = i;
        if (mNextAlarmTextView != null) {
            mNextAlarmTextView.setTextColor(i);
        }
        if (mNextAlarmImageView != null && mNextAlarmImageView.getDrawable() != null) {
            mNextAlarmImageView.getDrawable().setTint(mIconTintColor);
        }
        if (mDndImageView != null && mDndImageView.getDrawable() != null) {
            mDndImageView.getDrawable().setTint(mIconTintColor);
        }
        updateIconTint();
    }

    public final void setSubtitle(CharSequence charSequence, CharSequence charSequence2, boolean z) {
        DoubleShadowIconDrawable doubleShadowIconDrawable;
        int i;
        if (mSubtitleTextView == null) {
            Log.w("BcSmartspaceCard", "No subtitle view to update");
            return;
        }
        mSubtitleTextView.setText(charSequence);
        DoubleShadowIconDrawable doubleShadowIconDrawable2 = null;
        if (!TextUtils.isEmpty(charSequence) && z) {
            doubleShadowIconDrawable = mIconDrawable;
        } else {
            doubleShadowIconDrawable = null;
        }
        mSubtitleTextView.setCompoundDrawablesRelative(doubleShadowIconDrawable, null, null, null);
        SmartspaceTarget smartspaceTarget = mTarget;
        if (smartspaceTarget != null && smartspaceTarget.getFeatureType() == 5 && !mUsePageIndicatorUi) {
            i = 2;
        } else {
            i = 1;
        }
        mSubtitleTextView.setMaxLines(i);
        setFormattedContentDescription(mSubtitleTextView, charSequence, charSequence2);
        if (z) {
            doubleShadowIconDrawable2 = mIconDrawable;
        }
        BcSmartspaceTemplateDataUtils.offsetTextViewForIcon(mSubtitleTextView, doubleShadowIconDrawable2, isRtl());
    }

    public final void setTitle(CharSequence charSequence, CharSequence charSequence2, boolean z) {
        SmartspaceAction headerAction;
        Bundle extras;
        boolean z2;
        DoubleShadowIconDrawable doubleShadowIconDrawable;
        if (mTitleTextView == null) {
            Log.w("BcSmartspaceCard", "No title view to update");
            return;
        }
        mTitleTextView.setText(charSequence);
        DoubleShadowIconDrawable doubleShadowIconDrawable2 = null;
        if (mTarget == null) {
            headerAction = null;
        } else {
            headerAction = mTarget.getHeaderAction();
        }
        if (headerAction == null) {
            extras = null;
        } else {
            extras = headerAction.getExtras();
        }
        if (extras != null && extras.containsKey("titleEllipsize")) {
            String string = extras.getString("titleEllipsize");
            try {
                mTitleTextView.setEllipsize(TextUtils.TruncateAt.valueOf(string));
            } catch (IllegalArgumentException e) {
                Log.e("BcSmartspaceCard", "Invalid TruncateAt value: " + string);
            }
        } else if (mTarget != null && mTarget.getFeatureType() == 2 && Locale.ENGLISH.getLanguage().equals(getContext().getResources().getConfiguration().locale.getLanguage())) {
            mTitleTextView.setEllipsize(TextUtils.TruncateAt.MIDDLE);
        } else {
            mTitleTextView.setEllipsize(TextUtils.TruncateAt.END);
        }
        boolean z3 = false;
        if (extras != null) {
            int i = extras.getInt("titleMaxLines");
            if (i != 0) {
                mTitleTextView.setMaxLines(i);
            }
            z2 = extras.getBoolean("disableTitleIcon");
        } else {
            z2 = false;
        }
        if (z && !z2) {
            z3 = true;
        }
        if (z3) {
            setFormattedContentDescription(mTitleTextView, charSequence, charSequence2);
        }
        if (z3) {
            doubleShadowIconDrawable = mIconDrawable;
        } else {
            doubleShadowIconDrawable = null;
        }
        mTitleTextView.setCompoundDrawablesRelative(doubleShadowIconDrawable, null, null, null);
        if (z3) {
            doubleShadowIconDrawable2 = mIconDrawable;
        }
        BcSmartspaceTemplateDataUtils.offsetTextViewForIcon(mTitleTextView, doubleShadowIconDrawable2, isRtl());
    }

    public final void updateIconTint() {
        if (mTarget != null && mIconDrawable != null) {
            boolean z = mTarget.getFeatureType() != 1;
            if (z) {
                mIconDrawable.setTint(mIconTintColor);
            } else {
                mIconDrawable.setTintList(null);
            }
        }
    }

    public final void updateZenVisibility() {
        if (mExtrasGroup == null) {
            return;
        }
        ImageView imageView = mDndImageView;
        boolean z3 = true;
        int i = 0;
        boolean z = imageView != null && imageView.getVisibility() == 0;
        ImageView imageView2 = mNextAlarmImageView;
        boolean z2 = imageView2 != null && imageView2.getVisibility() == 0;
        if ((!z && !z2) || (mUsePageIndicatorUi && (mTarget == null || mTarget.getFeatureType() != 1))) {
            z3 = false;
        }
        int i2 = mTopPadding;
        if (!z3) {
            BcSmartspaceTemplateDataUtils.updateVisibility(mExtrasGroup, 4);
            i = i2;
        } else {
            BcSmartspaceTemplateDataUtils.updateVisibility(mExtrasGroup, 0);
            if (mNextAlarmTextView != null) {
                mNextAlarmTextView.setTextColor(mIconTintColor);
            }
            if (mNextAlarmImageView != null && mNextAlarmImageView.getDrawable() != null) {
                mNextAlarmImageView.getDrawable().setTint(mIconTintColor);
            }
            if (mDndImageView != null && mDndImageView.getDrawable() != null) {
                mDndImageView.getDrawable().setTint(mIconTintColor);
            }
        }
        setPadding(getPaddingLeft(), i, getPaddingRight(), getPaddingBottom());
    }

    public final AccessibilityNodeInfo createAccessibilityNodeInfo() {
        AccessibilityNodeInfo createAccessibilityNodeInfo = super.createAccessibilityNodeInfo();
        createAccessibilityNodeInfo.getExtras().putCharSequence("AccessibilityNodeInfo.roleDescription", " ");
        return createAccessibilityNodeInfo;
    }

    public final void onFinishInflate() {
        super.onFinishInflate();
        mTextGroup = (ViewGroup) findViewById(R.id.text_group);
        mSecondaryCardGroup = (ViewGroup) findViewById(R.id.secondary_card_group);
        mDateView = (IcuDateTextView) findViewById(R.id.date);
        mTitleTextView = (TextView) findViewById(R.id.title_text);
        mSubtitleTextView = (TextView) findViewById(R.id.subtitle_text);
        mBaseActionIconSubtitleView = (DoubleShadowTextView) findViewById(R.id.base_action_icon_subtitle);
        mExtrasGroup = (ViewGroup) findViewById(R.id.smartspace_extras_group);
        mTopPadding = getPaddingTop();
        if (mExtrasGroup != null) {
            mDndImageView = (ImageView) mExtrasGroup.findViewById(R.id.dnd_icon);
            mNextAlarmImageView = (ImageView) mExtrasGroup.findViewById(R.id.alarm_icon);
            mNextAlarmTextView = (TextView) mExtrasGroup.findViewById(R.id.alarm_text);
        }
    }

    public final void setFormattedContentDescription(TextView textView, CharSequence charSequence, CharSequence charSequence2) {
        String string;
        String str;
        if (TextUtils.isEmpty(charSequence)) {
            string = String.valueOf(charSequence2);
        } else if (TextUtils.isEmpty(charSequence2)) {
            string = String.valueOf(charSequence);
        } else {
            string = getContext().getString(R.string.generic_smartspace_concatenated_desc, charSequence2, charSequence);
        }
        Object[] objArr = new Object[4];
        if (textView == mTitleTextView) {
            str = "TITLE";
        } else if (textView == mSubtitleTextView) {
            str = "SUBTITLE";
        } else {
            str = "SUPPLEMENTAL";
        }
        objArr[0] = str;
        objArr[1] = charSequence;
        objArr[2] = charSequence2;
        objArr[3] = string;
        Log.i("BcSmartspaceCard", String.format("setFormattedContentDescription: textView=%s, text=%s, iconDescription=%s, contentDescription=%s", objArr));
        textView.setContentDescription(string);
    }
}
