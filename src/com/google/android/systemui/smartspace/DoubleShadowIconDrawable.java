package com.google.android.systemui.smartspace;

import android.content.Context;
import android.graphics.BlendMode;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RenderEffect;
import android.graphics.RenderNode;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.InsetDrawable;

import com.android.internal.graphics.ColorUtils;
import com.android.systemui.bcsmartspace.R;

public class DoubleShadowIconDrawable extends Drawable {
    public int mAmbientShadowRadius;
    public final int mCanvasSize;
    public RenderNode mDoubleShadowNode;
    public InsetDrawable mIconDrawable;
    public final int mIconInsetSize;
    public int mKeyShadowOffsetX;
    public int mKeyShadowOffsetY;
    public int mKeyShadowRadius;
    public boolean mShowShadow;

    public DoubleShadowIconDrawable(Context context) {
        int dimensionPixelSize = context.getResources().getDimensionPixelSize(R.dimen.enhanced_smartspace_icon_size);
        int dimensionPixelSize2 = context.getResources().getDimensionPixelSize(R.dimen.enhanced_smartspace_icon_inset);
        mIconInsetSize = dimensionPixelSize2;
        int i = (dimensionPixelSize2 * 2) + dimensionPixelSize;
        mCanvasSize = i;
        mAmbientShadowRadius = context.getResources().getDimensionPixelSize(R.dimen.ambient_text_shadow_radius);
        mKeyShadowRadius = context.getResources().getDimensionPixelSize(R.dimen.key_text_shadow_radius);
        mKeyShadowOffsetX = context.getResources().getDimensionPixelSize(R.dimen.key_text_shadow_dx);
        mKeyShadowOffsetY = context.getResources().getDimensionPixelSize(R.dimen.key_text_shadow_dy);
        setBounds(0, 0, i, i);
    }

    @Override // android.graphics.drawable.Drawable
    public int getOpacity() {
        return 0;
    }

    public void setIcon(Drawable drawable) {
        RenderNode renderNode = null;
        if (drawable == null) {
            mIconDrawable = null;
            return;
        }
        InsetDrawable insetDrawable = new InsetDrawable(drawable, mIconInsetSize);
        mIconDrawable = insetDrawable;
        int i = mCanvasSize;
        insetDrawable.setBounds(0, 0, i, i);
        if (mIconDrawable != null) {
            RenderNode renderNode2 = new RenderNode("DoubleShadowNode");
            int i2 = mCanvasSize;
            renderNode2.setPosition(0, 0, i2, i2);
            RenderEffect createShadowRenderEffect = createShadowRenderEffect(mAmbientShadowRadius, 0, 0, 48);
            RenderEffect createShadowRenderEffect2 = createShadowRenderEffect(mKeyShadowRadius, mKeyShadowOffsetX, mKeyShadowOffsetY, 72);
            if (createShadowRenderEffect != null && createShadowRenderEffect2 != null) {
                renderNode2.setRenderEffect(RenderEffect.createBlendModeEffect(createShadowRenderEffect, createShadowRenderEffect2, BlendMode.DARKEN));
                renderNode = renderNode2;
            }
        }
        mDoubleShadowNode = renderNode;
    }

    public static RenderEffect createShadowRenderEffect(int i, int i2, int i3, int i4) {
        return RenderEffect.createColorFilterEffect(new PorterDuffColorFilter(Color.argb(i4, 0, 0, 0), PorterDuff.Mode.MULTIPLY), RenderEffect.createOffsetEffect(i2, i3, RenderEffect.createBlurEffect(i, i, Shader.TileMode.CLAMP)));
    }

    @Override // android.graphics.drawable.Drawable
    public void setAlpha(int i) {
        mIconDrawable.setAlpha(i);
    }

    @Override // android.graphics.drawable.Drawable
    public void setColorFilter(ColorFilter colorFilter) {
        mIconDrawable.setColorFilter(colorFilter);
    }

    @Override // android.graphics.drawable.Drawable
    public void setTint(int alpha) {
        if (mIconDrawable != null) {
            mIconDrawable.setTint(alpha);
        }
        mShowShadow = ColorUtils.calculateLuminance(alpha) > 0.5d;
    }

    @Override // android.graphics.drawable.Drawable
    public void draw(Canvas canvas) {
        RenderNode renderNode;
        if (canvas.isHardwareAccelerated() && (renderNode = mDoubleShadowNode) != null && mShowShadow) {
            if (!renderNode.hasDisplayList()) {
                mIconDrawable.draw(mDoubleShadowNode.beginRecording());
                mDoubleShadowNode.endRecording();
            }
            canvas.drawRenderNode(mDoubleShadowNode);
        }
        InsetDrawable insetDrawable = mIconDrawable;
        if (insetDrawable != null) {
            insetDrawable.draw(canvas);
        }
    }
}
