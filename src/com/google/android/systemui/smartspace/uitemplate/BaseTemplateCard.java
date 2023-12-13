package com.google.android.systemui.smartspace.uitemplate;

import android.app.smartspace.SmartspaceTarget;
import android.app.smartspace.SmartspaceUtils;
import android.app.smartspace.uitemplatedata.BaseTemplateData;
import android.app.smartspace.uitemplatedata.Icon;
import android.app.smartspace.uitemplatedata.TapAction;
import android.app.smartspace.uitemplatedata.Text;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.ImageView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.android.app.animation.Interpolators;
import com.android.launcher3.icons.GraphicsUtils;
import com.android.systemui.bcsmartspace.R;
import com.android.systemui.plugins.BcSmartspaceDataPlugin;
import com.google.android.systemui.smartspace.BcSmartSpaceUtil;
import com.google.android.systemui.smartspace.BcSmartspaceCardSecondary;
import com.google.android.systemui.smartspace.BcSmartspaceTemplateDataUtils;
import com.google.android.systemui.smartspace.DoubleShadowIconDrawable;
import com.google.android.systemui.smartspace.DoubleShadowTextView;
import com.google.android.systemui.smartspace.IcuDateTextView;
import com.google.android.systemui.smartspace.logging.BcSmartspaceCardLoggingInfo;
import com.google.android.systemui.smartspace.logging.BcSmartspaceCardMetadataLoggingInfo;
import com.google.android.systemui.smartspace.logging.BcSmartspaceSubcardLoggingInfo;

import java.util.List;

public class BaseTemplateCard extends ConstraintLayout {
    public IcuDateTextView mDateView;
    public ImageView mDndImageView;
    public float mDozeAmount;
    public ViewGroup mExtrasGroup;
    public int mFeatureType;
    public int mIconTintColor;
    public boolean mIsDreaming;
    public String mUiSurface;
    public BcSmartspaceCardLoggingInfo mLoggingInfo;
    public ImageView mNextAlarmImageView;
    public DoubleShadowTextView mNextAlarmTextView;
    public String mPrevSmartspaceTargetId;
    public BcSmartspaceCardSecondary mSecondaryCard;
    public ViewGroup mSecondaryCardPane;
    public boolean mShouldShowPageIndicator;
    public DoubleShadowTextView mSubtitleSupplementalView;
    public DoubleShadowTextView mSubtitleTextView;
    public DoubleShadowTextView mSupplementalLineTextView;
    public SmartspaceTarget mTarget;
    public BaseTemplateData mTemplateData;
    public ViewGroup mTextGroup;
    public DoubleShadowTextView mTitleTextView;
    public int mTopPadding;
    public boolean mValidSecondaryCard;

    public BaseTemplateCard(Context context) {
        this(context, null);
    }

    public BaseTemplateCard(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        mSecondaryCard = null;
        mFeatureType = 0;
        mLoggingInfo = null;
        mIconTintColor = GraphicsUtils.getAttrColor(getContext(), 16842806);
        mPrevSmartspaceTargetId = "";
        mTextGroup = null;
        mSecondaryCardPane = null;
        mDateView = null;
        mTitleTextView = null;
        mSubtitleTextView = null;
        mSubtitleSupplementalView = null;
        mExtrasGroup = null;
        mDndImageView = null;
        mNextAlarmImageView = null;
        mNextAlarmTextView = null;
        mSupplementalLineTextView = null;
    }

    public static boolean shouldTint(BaseTemplateData.SubItemInfo subItemInfo) {
        if (subItemInfo != null && subItemInfo.getIcon() != null) {
            return subItemInfo.getIcon().shouldTint();
        }
        return false;
    }

    public final void resetTextView(DoubleShadowTextView doubleShadowTextView) {
        if (doubleShadowTextView == null) {
            return;
        }
        doubleShadowTextView.setCompoundDrawablesRelative(null, null, null, null);
        doubleShadowTextView.setOnClickListener(null);
        doubleShadowTextView.setContentDescription(null);
        doubleShadowTextView.setText((CharSequence) null);
        BcSmartspaceTemplateDataUtils.offsetTextViewForIcon(doubleShadowTextView, null, isRtl());
    }

    public final void setDozeAmount(float f) {
        mDozeAmount = f;
        ImageView imageView = mDndImageView;
        if (imageView != null) {
            imageView.setAlpha(f);
        }
        SmartspaceTarget smartspaceTarget = mTarget;
        if (smartspaceTarget != null && smartspaceTarget.getBaseAction() != null && mTarget.getBaseAction().getExtras() != null) {
            Bundle extras = mTarget.getBaseAction().getExtras();
            if (mTitleTextView != null && extras.getBoolean("hide_title_on_aod")) {
                mTitleTextView.setAlpha(1.0f - f);
            }
            if (mSubtitleTextView != null && extras.getBoolean("hide_subtitle_on_aod")) {
                mSubtitleTextView.setAlpha(1.0f - f);
            }
        }
        if (mTextGroup != null) {
            ViewGroup viewGroup = mSecondaryCardPane;
            int i = 0;
            int i2 = 1;
            boolean z = mDozeAmount == 1.0f || !mValidSecondaryCard;
            if (z) {
                i = 8;
            }
            BcSmartspaceTemplateDataUtils.updateVisibility(viewGroup, i);
            ViewGroup viewGroup2 = mSecondaryCardPane;
            if (viewGroup2 != null && viewGroup2.getVisibility() != 8) {
                ViewGroup viewGroup3 = mTextGroup;
                if (!isRtl()) {
                    i2 = -1;
                }
                viewGroup3.setTranslationX(Interpolators.EMPHASIZED.getInterpolation(mDozeAmount) * mSecondaryCardPane.getWidth() * i2);
                mSecondaryCardPane.setAlpha(Math.max(0.0f, Math.min(1.0f, ((1.0f - mDozeAmount) * 9.0f) - 6.0f)));
                return;
            }
            mTextGroup.setTranslationX(0.0f);
        }
    }

    public final void setPrimaryTextColor(int i) {
        mIconTintColor = i;
        if (mTitleTextView != null) {
            mTitleTextView.setTextColor(i);
            if (mTemplateData != null) {
                updateTextViewIconTint(mTitleTextView, shouldTint(mTemplateData.getPrimaryItem()));
            }
        }
        if (mDateView != null) {
            mDateView.setTextColor(i);
        }
        if (mSubtitleTextView != null) {
            mSubtitleTextView.setTextColor(i);
            if (mTemplateData != null) {
                updateTextViewIconTint(mSubtitleTextView, shouldTint(mTemplateData.getSubtitleItem()));
            }
        }
        if (mSubtitleSupplementalView != null) {
            mSubtitleSupplementalView.setTextColor(i);
            if (mTemplateData != null) {
                updateTextViewIconTint(mSubtitleSupplementalView, shouldTint(mTemplateData.getSubtitleSupplementalItem()));
            }
        }
        updateZenColors();
    }

    public final void setUpTextView(DoubleShadowTextView doubleShadowTextView, BaseTemplateData.SubItemInfo subItemInfo, BcSmartspaceDataPlugin.SmartspaceEventNotifier smartspaceEventNotifier) {
        BcSmartspaceSubcardLoggingInfo bcSmartspaceSubcardLoggingInfo;
        List<BcSmartspaceCardMetadataLoggingInfo> list;
        CharSequence text;
        CharSequence charSequence;
        if (doubleShadowTextView == null) {
            Log.d("SsBaseTemplateCard", "No text view can be set up");
            return;
        }
        resetTextView(doubleShadowTextView);
        if (subItemInfo == null) {
            Log.d("SsBaseTemplateCard", "Passed-in item info is null");
            BcSmartspaceTemplateDataUtils.updateVisibility(doubleShadowTextView, 8);
            return;
        }
        Text text2 = subItemInfo.getText();
        BcSmartspaceTemplateDataUtils.setText(doubleShadowTextView, subItemInfo.getText());
        if (!SmartspaceUtils.isEmpty(text2)) {
            doubleShadowTextView.setTextColor(mIconTintColor);
        }
        Icon icon = subItemInfo.getIcon();
        int i = 0;
        if (icon != null) {
            DoubleShadowIconDrawable doubleShadowIconDrawable = new DoubleShadowIconDrawable(getContext());
            doubleShadowIconDrawable.setIcon(BcSmartSpaceUtil.getIconDrawable(getContext(), icon.getIcon()));
            doubleShadowTextView.setCompoundDrawablesRelative(doubleShadowIconDrawable, null, null, null);
            if (SmartspaceUtils.isEmpty(text2)) {
                text = "";
            } else {
                text = text2.getText();
            }
            CharSequence contentDescription = icon.getContentDescription();
            if (TextUtils.isEmpty(text)) {
                charSequence = contentDescription;
            } else {
                charSequence = text;
                if (!TextUtils.isEmpty(contentDescription)) {
                    charSequence = mContext.getString(R.string.generic_smartspace_concatenated_desc, contentDescription, text);
                }
            }
            doubleShadowTextView.setContentDescription(charSequence);
            updateTextViewIconTint(doubleShadowTextView, icon.shouldTint());
            BcSmartspaceTemplateDataUtils.offsetTextViewForIcon(doubleShadowTextView, doubleShadowIconDrawable, isRtl());
        }
        BcSmartspaceTemplateDataUtils.updateVisibility(doubleShadowTextView, 0);
        SmartspaceTarget smartspaceTarget = mTarget;
        TapAction tapAction = subItemInfo.getTapAction();
        BcSmartspaceCardLoggingInfo bcSmartspaceCardLoggingInfo = mLoggingInfo;
        if (bcSmartspaceCardLoggingInfo != null && (bcSmartspaceSubcardLoggingInfo = bcSmartspaceCardLoggingInfo.mSubcardInfo) != null && (list = bcSmartspaceSubcardLoggingInfo.mSubcards) != null && !list.isEmpty() && subItemInfo.getLoggingInfo() != null) {
            int featureType = subItemInfo.getLoggingInfo().getFeatureType();
            BcSmartspaceCardLoggingInfo bcSmartspaceCardLoggingInfo2 = mLoggingInfo;
            if (featureType != bcSmartspaceCardLoggingInfo2.mFeatureType) {
                List<BcSmartspaceCardMetadataLoggingInfo> list2 = bcSmartspaceCardLoggingInfo2.mSubcardInfo.mSubcards;
                BaseTemplateData.SubItemLoggingInfo loggingInfo = subItemInfo.getLoggingInfo();
                int i2 = 0;
                while (true) {
                    if (i2 < list2.size()) {
                        BcSmartspaceCardMetadataLoggingInfo bcSmartspaceCardMetadataLoggingInfo = list2.get(i2);
                        if (bcSmartspaceCardMetadataLoggingInfo.mInstanceId == loggingInfo.getInstanceId() && bcSmartspaceCardMetadataLoggingInfo.mCardTypeId == loggingInfo.getFeatureType()) {
                            i = i2 + 1;
                            break;
                        }
                        i2++;
                    } else {
                        break;
                    }
                }
            }
        }
        BcSmartSpaceUtil.setOnClickListener(doubleShadowTextView, smartspaceTarget, tapAction, smartspaceEventNotifier, "SsBaseTemplateCard", bcSmartspaceCardLoggingInfo, i);
    }

    public final void updateZenColors() {
        ImageView imageView = mNextAlarmImageView;
        if (imageView != null && imageView.getDrawable() != null) {
            imageView.getDrawable().setTint(mIconTintColor);
        }
        DoubleShadowTextView doubleShadowTextView = mNextAlarmTextView;
        if (doubleShadowTextView != null) {
            doubleShadowTextView.setTextColor(mIconTintColor);
        }
        ImageView imageView2 = mDndImageView;
        if (imageView2 != null && imageView2.getDrawable() != null) {
            imageView2.getDrawable().setTint(mIconTintColor);
        }
        DoubleShadowTextView doubleShadowTextView2 = mSupplementalLineTextView;
        if (doubleShadowTextView2 != null) {
            doubleShadowTextView2.setTextColor(mIconTintColor);
            BaseTemplateData baseTemplateData = mTemplateData;
            if (baseTemplateData != null) {
                updateTextViewIconTint(mSupplementalLineTextView, shouldTint(baseTemplateData.getSupplementalLineItem()));
            }
        }
    }

    public final void updateZenVisibility() {
        if (mExtrasGroup == null) {
            return;
        }
        ImageView imageView = mDndImageView;
        boolean z4 = true;
        int i = 0;
        boolean z = imageView != null && imageView.getVisibility() == 0;
        ImageView imageView2 = mNextAlarmImageView;
        boolean z2 = imageView2 != null && imageView2.getVisibility() == 0;
        DoubleShadowTextView doubleShadowTextView = mSupplementalLineTextView;
        boolean z3 = doubleShadowTextView != null && doubleShadowTextView.getVisibility() == 0;
        if ((!z && !z2 && !z3) || (mShouldShowPageIndicator && mDateView == null)) {
            z4 = false;
        }
        int i2 = mTopPadding;
        if (!z4) {
            BcSmartspaceTemplateDataUtils.updateVisibility(mExtrasGroup, 4);
            i = i2;
        } else {
            BcSmartspaceTemplateDataUtils.updateVisibility(mExtrasGroup, 0);
            updateZenColors();
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
        mSecondaryCardPane = (ViewGroup) findViewById(R.id.secondary_card_group);
        mDateView = (IcuDateTextView) findViewById(R.id.date);
        mTitleTextView = (DoubleShadowTextView) findViewById(R.id.title_text);
        mSubtitleTextView = (DoubleShadowTextView) findViewById(R.id.subtitle_text);
        mSubtitleSupplementalView = (DoubleShadowTextView) findViewById(R.id.base_action_icon_subtitle);
        mExtrasGroup = (ViewGroup) findViewById(R.id.smartspace_extras_group);
        mTopPadding = getPaddingTop();
        ViewGroup viewGroup = mExtrasGroup;
        if (viewGroup != null) {
            mDndImageView = (ImageView) viewGroup.findViewById(R.id.dnd_icon);
            mNextAlarmImageView = (ImageView) mExtrasGroup.findViewById(R.id.alarm_icon);
            mNextAlarmTextView = (DoubleShadowTextView) mExtrasGroup.findViewById(R.id.alarm_text);
            mSupplementalLineTextView = (DoubleShadowTextView) mExtrasGroup.findViewById(R.id.supplemental_line_text);
        }
    }

    public final void updateTextViewIconTint(DoubleShadowTextView doubleShadowTextView, boolean z) {
        Drawable[] compoundDrawablesRelative = doubleShadowTextView.getCompoundDrawablesRelative();
        for (Drawable drawable : compoundDrawablesRelative) {
            if (drawable != null) {
                if (z) {
                    drawable.setTint(mIconTintColor);
                } else {
                    drawable.setTintList(null);
                }
            }
        }
    }
}
