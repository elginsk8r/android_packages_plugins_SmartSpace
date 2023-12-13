package com.google.android.systemui.smartspace;

import android.app.smartspace.SmartspaceAction;
import android.app.smartspace.SmartspaceTarget;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.systemui.bcsmartspace.R;
import com.android.systemui.plugins.BcSmartspaceDataPlugin;
import com.google.android.systemui.smartspace.logging.BcSmartspaceCardLoggingInfo;

public class BcSmartspaceCardSports extends BcSmartspaceCardSecondary {
    public ImageView mFirstCompetitorLogo;
    public TextView mFirstCompetitorScore;
    public ImageView mSecondCompetitorLogo;
    public TextView mSecondCompetitorScore;
    public TextView mSummaryView;

    public BcSmartspaceCardSports(Context context) {
        super(context);
    }

    public BcSmartspaceCardSports(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    @Override // com.google.android.systemui.smartspace.BcSmartspaceCardSecondary
    public final void resetUi() {
        BcSmartspaceTemplateDataUtils.updateVisibility(mSummaryView, 4);
        BcSmartspaceTemplateDataUtils.updateVisibility(mFirstCompetitorScore, 4);
        BcSmartspaceTemplateDataUtils.updateVisibility(mSecondCompetitorScore, 4);
        BcSmartspaceTemplateDataUtils.updateVisibility(mFirstCompetitorLogo, 4);
        BcSmartspaceTemplateDataUtils.updateVisibility(mSecondCompetitorLogo, 4);
    }

    @Override // com.google.android.systemui.smartspace.BcSmartspaceCardSecondary
    public final void setTextColor(int i) {
        mSummaryView.setTextColor(i);
        mFirstCompetitorScore.setTextColor(i);
        mSecondCompetitorScore.setTextColor(i);
    }

    public final void onFinishInflate() {
        super.onFinishInflate();
        mSummaryView = (TextView) findViewById(R.id.match_time_summary);
        mFirstCompetitorScore = (TextView) findViewById(R.id.first_competitor_score);
        mSecondCompetitorScore = (TextView) findViewById(R.id.second_competitor_score);
        mFirstCompetitorLogo = (ImageView) findViewById(R.id.first_competitor_logo);
        mSecondCompetitorLogo = (ImageView) findViewById(R.id.second_competitor_logo);
    }

    @Override // com.google.android.systemui.smartspace.BcSmartspaceCardSecondary
    public final boolean setSmartspaceActions(SmartspaceTarget smartspaceTarget, BcSmartspaceDataPlugin.SmartspaceEventNotifier smartspaceEventNotifier, BcSmartspaceCardLoggingInfo bcSmartspaceCardLoggingInfo) {
        Bundle extras;
        boolean z;
        SmartspaceAction baseAction = smartspaceTarget.getBaseAction();
        if (baseAction == null) {
            extras = null;
        } else {
            extras = baseAction.getExtras();
        }
        if (extras == null) {
            return false;
        }
        if (extras.containsKey("matchTimeSummary")) {
            String string = extras.getString("matchTimeSummary");
            if (mSummaryView == null) {
                Log.w("BcSmartspaceCardSports", "No match time summary view to update");
            } else {
                BcSmartspaceTemplateDataUtils.updateVisibility(mSummaryView, 0);
                mSummaryView.setText(string);
            }
            z = true;
        } else {
            z = false;
        }
        if (extras.containsKey("firstCompetitorScore")) {
            String string2 = extras.getString("firstCompetitorScore");
            if (mFirstCompetitorScore == null) {
                Log.w("BcSmartspaceCardSports", "No first competitor logo view to update");
            } else {
                BcSmartspaceTemplateDataUtils.updateVisibility(mFirstCompetitorScore, 0);
                mFirstCompetitorScore.setText(string2);
            }
            z = true;
        }
        if (extras.containsKey("secondCompetitorScore")) {
            String string3 = extras.getString("secondCompetitorScore");
            if (mSecondCompetitorScore == null) {
                Log.w("BcSmartspaceCardSports", "No second competitor logo view to update");
            } else {
                BcSmartspaceTemplateDataUtils.updateVisibility(mSecondCompetitorScore, 0);
                mSecondCompetitorScore.setText(string3);
            }
            z = true;
        }
        if (extras.containsKey("firstCompetitorLogo")) {
            Bitmap bitmap = (Bitmap) extras.get("firstCompetitorLogo");
            if (mFirstCompetitorLogo == null) {
                Log.w("BcSmartspaceCardSports", "No first competitor logo view to update");
            } else {
                BcSmartspaceTemplateDataUtils.updateVisibility(mFirstCompetitorLogo, 0);
                mFirstCompetitorLogo.setImageBitmap(bitmap);
            }
            z = true;
        }
        if (extras.containsKey("secondCompetitorLogo")) {
            Bitmap bitmap2 = (Bitmap) extras.get("secondCompetitorLogo");
            if (mSecondCompetitorLogo == null) {
                Log.w("BcSmartspaceCardSports", "No second competitor logo view to update");
                return true;
            }
            BcSmartspaceTemplateDataUtils.updateVisibility(mSecondCompetitorLogo, 0);
            mSecondCompetitorLogo.setImageBitmap(bitmap2);
            return true;
        }
        return z;
    }
}
