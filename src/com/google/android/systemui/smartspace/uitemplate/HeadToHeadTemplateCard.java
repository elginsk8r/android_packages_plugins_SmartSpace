package com.google.android.systemui.smartspace.uitemplate;

import android.app.smartspace.SmartspaceTarget;
import android.app.smartspace.uitemplatedata.HeadToHeadTemplateData;
import android.app.smartspace.uitemplatedata.Icon;
import android.app.smartspace.uitemplatedata.Text;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.systemui.bcsmartspace.R;
import com.android.systemui.plugins.BcSmartspaceDataPlugin;
import com.google.android.systemui.smartspace.BcSmartSpaceUtil;
import com.google.android.systemui.smartspace.BcSmartspaceCardSecondary;
import com.google.android.systemui.smartspace.BcSmartspaceTemplateDataUtils;
import com.google.android.systemui.smartspace.logging.BcSmartspaceCardLoggingInfo;

public class HeadToHeadTemplateCard extends BcSmartspaceCardSecondary {
    private static final String TAG = "HeadToHeadTemplateCard";

    public ImageView mFirstCompetitorIcon;
    public TextView mFirstCompetitorText;
    public TextView mHeadToHeadTitle;
    public ImageView mSecondCompetitorIcon;
    public TextView mSecondCompetitorText;

    public HeadToHeadTemplateCard(Context context) {
        super(context);
    }

    public HeadToHeadTemplateCard(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    @Override // com.google.android.systemui.smartspace.BcSmartspaceCardSecondary
    public final void setTextColor(int i) {
        mFirstCompetitorText.setTextColor(i);
        mSecondCompetitorText.setTextColor(i);
    }

    public final void onFinishInflate() {
        super/*android.view.ViewGroup*/.onFinishInflate();
        mHeadToHeadTitle = (TextView) findViewById(R.id.head_to_head_title);
        mFirstCompetitorText = (TextView) findViewById(R.id.first_competitor_text);
        mSecondCompetitorText = (TextView) findViewById(R.id.second_competitor_text);
        mFirstCompetitorIcon = (ImageView) findViewById(R.id.first_competitor_icon);
        mSecondCompetitorIcon = (ImageView) findViewById(R.id.second_competitor_icon);
    }

    @Override // com.google.android.systemui.smartspace.BcSmartspaceCardSecondary
    public final void resetUi() {
        BcSmartspaceTemplateDataUtils.updateVisibility(mHeadToHeadTitle, 8);
        BcSmartspaceTemplateDataUtils.updateVisibility(mFirstCompetitorText, 8);
        BcSmartspaceTemplateDataUtils.updateVisibility(mSecondCompetitorText, 8);
        BcSmartspaceTemplateDataUtils.updateVisibility(mFirstCompetitorIcon, 8);
        BcSmartspaceTemplateDataUtils.updateVisibility(mSecondCompetitorIcon, 8);
    }

    @Override // com.google.android.systemui.smartspace.BcSmartspaceCardSecondary
    public final boolean setSmartspaceActions(SmartspaceTarget smartspaceTarget, BcSmartspaceDataPlugin.SmartspaceEventNotifier smartspaceEventNotifier, BcSmartspaceCardLoggingInfo bcSmartspaceCardLoggingInfo) {
        boolean z;
        boolean z2;
        boolean z3;
        boolean z4;
        boolean z5;
        boolean z6;
        boolean z7;
        HeadToHeadTemplateData templateData = (HeadToHeadTemplateData) smartspaceTarget.getTemplateData();
        if (templateData == null) {
            Log.w(TAG, "HeadToHeadTemplateData is null");
            return false;
        }
        if (templateData.getHeadToHeadTitle() != null) {
            Text headToHeadTitle = templateData.getHeadToHeadTitle();
            TextView textView = mHeadToHeadTitle;
            if (textView == null) {
                Log.w(TAG, "No head-to-head title view to update");
                z7 = false;
            } else {
                BcSmartspaceTemplateDataUtils.setText(textView, headToHeadTitle);
                BcSmartspaceTemplateDataUtils.updateVisibility(mHeadToHeadTitle, 0);
                z7 = true;
            }
            if (z7) {
                z = true;
                boolean z8 = z;
                if (templateData.getHeadToHeadFirstCompetitorText() != null) {
                    Text headToHeadFirstCompetitorText = templateData.getHeadToHeadFirstCompetitorText();
                    TextView textView2 = mFirstCompetitorText;
                    if (textView2 == null) {
                        Log.w(TAG, "No first competitor text view to update");
                        z6 = false;
                    } else {
                        BcSmartspaceTemplateDataUtils.setText(textView2, headToHeadFirstCompetitorText);
                        BcSmartspaceTemplateDataUtils.updateVisibility(mFirstCompetitorText, 0);
                        z6 = true;
                    }
                    if (!z6 && !z) {
                        z8 = false;
                    } else {
                        z8 = true;
                    }
                }
                boolean z9 = z8;
                if (templateData.getHeadToHeadSecondCompetitorText() != null) {
                    Text headToHeadSecondCompetitorText = templateData.getHeadToHeadSecondCompetitorText();
                    TextView textView3 = mSecondCompetitorText;
                    if (textView3 == null) {
                        Log.w(TAG, "No second competitor text view to update");
                        z5 = false;
                    } else {
                        BcSmartspaceTemplateDataUtils.setText(textView3, headToHeadSecondCompetitorText);
                        BcSmartspaceTemplateDataUtils.updateVisibility(mSecondCompetitorText, 0);
                        z5 = true;
                    }
                    if (!z5 && !z8) {
                        z9 = false;
                    } else {
                        z9 = true;
                    }
                }
                boolean z10 = z9;
                if (templateData.getHeadToHeadFirstCompetitorIcon() != null) {
                    Icon headToHeadFirstCompetitorIcon = templateData.getHeadToHeadFirstCompetitorIcon();
                    ImageView imageView = mFirstCompetitorIcon;
                    if (imageView == null) {
                        Log.w(TAG, "No first competitor icon view to update");
                        z4 = false;
                    } else {
                        BcSmartspaceTemplateDataUtils.setIcon(imageView, headToHeadFirstCompetitorIcon);
                        BcSmartspaceTemplateDataUtils.updateVisibility(mFirstCompetitorIcon, 0);
                        z4 = true;
                    }
                    if (!z4 && !z9) {
                        z10 = false;
                    } else {
                        z10 = true;
                    }
                }
                z2 = z10;
                if (templateData.getHeadToHeadSecondCompetitorIcon() != null) {
                    Icon headToHeadSecondCompetitorIcon = templateData.getHeadToHeadSecondCompetitorIcon();
                    ImageView imageView2 = mSecondCompetitorIcon;
                    if (imageView2 == null) {
                        Log.w(TAG, "No second competitor icon view to update");
                        z3 = false;
                    } else {
                        BcSmartspaceTemplateDataUtils.setIcon(imageView2, headToHeadSecondCompetitorIcon);
                        BcSmartspaceTemplateDataUtils.updateVisibility(mSecondCompetitorIcon, 0);
                        z3 = true;
                    }
                    if (!z3) {
                        z2 = false;
                    }
                    z2 = true;
                }
                if (z2 && templateData.getHeadToHeadAction() != null) {
                    BcSmartSpaceUtil.setOnClickListener(this, smartspaceTarget, templateData.getHeadToHeadAction(), smartspaceEventNotifier, TAG, bcSmartspaceCardLoggingInfo);
                }
                return z2;
            }
        }
        return false;
    }
}
