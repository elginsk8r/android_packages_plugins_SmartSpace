package com.google.android.systemui.smartspace.uitemplate;

import android.app.smartspace.SmartspaceTarget;
import android.app.smartspace.SmartspaceUtils;
import android.app.smartspace.uitemplatedata.SubCardTemplateData;
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

public class SubCardTemplateCard extends BcSmartspaceCardSecondary {
    public ImageView mImageView;
    public TextView mTextView;

    public SubCardTemplateCard(Context context) {
        super(context);
    }

    public SubCardTemplateCard(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    @Override // com.google.android.systemui.smartspace.BcSmartspaceCardSecondary
    public final void setTextColor(int i) {
        mTextView.setTextColor(i);
    }

    public final void onFinishInflate() {
        super/*android.view.ViewGroup*/.onFinishInflate();
        mImageView = (ImageView) findViewById(R.id.image_view);
        mTextView = (TextView) findViewById(R.id.card_prompt);
    }

    @Override // com.google.android.systemui.smartspace.BcSmartspaceCardSecondary
    public final void resetUi() {
        BcSmartspaceTemplateDataUtils.updateVisibility(mImageView, 8);
        BcSmartspaceTemplateDataUtils.updateVisibility(mTextView, 8);
    }

    @Override // com.google.android.systemui.smartspace.BcSmartspaceCardSecondary
    public final boolean setSmartspaceActions(SmartspaceTarget smartspaceTarget, BcSmartspaceDataPlugin.SmartspaceEventNotifier smartspaceEventNotifier, BcSmartspaceCardLoggingInfo bcSmartspaceCardLoggingInfo) {
        boolean z;
        SubCardTemplateData templateData = (SubCardTemplateData) smartspaceTarget.getTemplateData();
        if (templateData == null) {
            Log.w("SubCardTemplateCard", "SubCardTemplateData is null");
            return false;
        }
        if (templateData.getSubCardIcon() != null) {
            BcSmartspaceTemplateDataUtils.setIcon(mImageView, templateData.getSubCardIcon());
            BcSmartspaceTemplateDataUtils.updateVisibility(mImageView, 0);
            z = true;
        } else {
            z = false;
        }
        if (!SmartspaceUtils.isEmpty(templateData.getSubCardText())) {
            BcSmartspaceTemplateDataUtils.setText(mTextView, templateData.getSubCardText());
            BcSmartspaceTemplateDataUtils.updateVisibility(mTextView, 0);
            z = true;
        }
        if (z && templateData.getSubCardAction() != null) {
            BcSmartSpaceUtil.setOnClickListener(this, smartspaceTarget, templateData.getSubCardAction(), smartspaceEventNotifier, "SubCardTemplateCard", bcSmartspaceCardLoggingInfo);
        }
        return z;
    }
}
