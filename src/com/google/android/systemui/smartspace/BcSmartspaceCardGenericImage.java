package com.google.android.systemui.smartspace;

import android.app.smartspace.SmartspaceAction;
import android.app.smartspace.SmartspaceTarget;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.android.systemui.bcsmartspace.R;
import com.android.systemui.plugins.BcSmartspaceDataPlugin;
import com.google.android.systemui.smartspace.logging.BcSmartspaceCardLoggingInfo;

public class BcSmartspaceCardGenericImage extends BcSmartspaceCardSecondary {
    private static final String TAG = "BcSmartspaceCardGenericImage";

    public ImageView mImageView;

    public BcSmartspaceCardGenericImage(Context context) {
        super(context);
    }

    @Override // com.google.android.systemui.smartspace.BcSmartspaceCardSecondary
    public void setTextColor(int i) {
    }

    public BcSmartspaceCardGenericImage(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    @Override // com.google.android.systemui.smartspace.BcSmartspaceCardSecondary
    public void resetUi() {
        mImageView.setImageBitmap(null);
    }

    public void setImageBitmap(Bitmap bitmap) {
        mImageView.setImageBitmap(bitmap);
    }

    public void onFinishInflate() {
        super.onFinishInflate();
        mImageView = (ImageView) findViewById(R.id.image_view);
    }

    @Override // com.google.android.systemui.smartspace.BcSmartspaceCardSecondary
    public boolean setSmartspaceActions(SmartspaceTarget smartspaceTarget, BcSmartspaceDataPlugin.SmartspaceEventNotifier smartspaceEventNotifier, BcSmartspaceCardLoggingInfo bcSmartspaceCardLoggingInfo) {
        Bundle extras;
        SmartspaceAction baseAction = smartspaceTarget.getBaseAction();
        if (baseAction == null) {
            extras = null;
        } else {
            extras = baseAction.getExtras();
        }
        if (extras != null && extras.containsKey("imageBitmap")) {
            if (extras.containsKey("imageScaleType")) {
                String string = extras.getString("imageScaleType");
                try {
                    mImageView.setScaleType(ImageView.ScaleType.valueOf(string));
                } catch (IllegalArgumentException e) {
                    Log.e("SmartspaceGenericImg", "Invalid imageScaleType value: " + string);
                }
            }
            String dimensionRatio = BcSmartSpaceUtil.getDimensionRatio(extras);
            if (dimensionRatio != null) {
                ((ConstraintLayout.LayoutParams) mImageView.getLayoutParams()).dimensionRatio = dimensionRatio;
            }
            if (extras.containsKey("imageLayoutWidth")) {
                ((ViewGroup.MarginLayoutParams) mImageView.getLayoutParams()).width = extras.getInt("imageLayoutWidth");
            }
            if (extras.containsKey("imageLayoutHeight")) {
                ((ViewGroup.MarginLayoutParams) mImageView.getLayoutParams()).height = extras.getInt("imageLayoutHeight");
            }
            setImageBitmap((Bitmap) extras.get("imageBitmap"));
            return true;
        }
        return false;
    }
}
