package com.google.android.systemui.smartspace;

import android.app.smartspace.SmartspaceAction;
import android.app.smartspace.SmartspaceTarget;
import android.content.ContentResolver;
import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Size;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;

import com.android.launcher3.icons.RoundDrawableWrapper;
import com.android.systemui.bcsmartspace.R;
import com.android.systemui.plugins.BcSmartspaceDataPlugin;
import com.google.android.systemui.smartspace.logging.BcSmartspaceCardLoggingInfo;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class BcSmartspaceCardDoorbell extends BcSmartspaceCardGenericImage {
    private static final String TAG = "BcSmartspaceCardBell";

    public int mGifFrameDurationInMs;
    public ImageView mLoadingIcon;
    public ViewGroup mLoadingScreenView;
    public String mPreviousTargetId;
    public ProgressBar mProgressBar;
    public final HashMap mUriToDrawable;

    public BcSmartspaceCardDoorbell(Context context) {
        super(context);
        mUriToDrawable = new HashMap();
        mGifFrameDurationInMs = 200;
    }

    public BcSmartspaceCardDoorbell(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        mUriToDrawable = new HashMap();
        mGifFrameDurationInMs = 200;
    }

    @Override // com.google.android.systemui.smartspace.BcSmartspaceCardGenericImage, com.google.android.systemui.smartspace.BcSmartspaceCardSecondary
    public final boolean setSmartspaceActions(SmartspaceTarget smartspaceTarget, BcSmartspaceDataPlugin.SmartspaceEventNotifier smartspaceEventNotifier, BcSmartspaceCardLoggingInfo bcSmartspaceCardLoggingInfo) {
        if (!isSysUiContext()) {
            return false;
        }
        SmartspaceAction baseAction = smartspaceTarget.getBaseAction();
        Bundle extras = baseAction != null ? baseAction.getExtras() : null;
        List<Uri> imageUris = getImageUris(smartspaceTarget);
        if (!imageUris.isEmpty()) {
            if (extras != null && extras.containsKey("frameDurationMs")) {
                mGifFrameDurationInMs = extras.getInt("frameDurationMs");
            }
            maybeResetImageView(smartspaceTarget);
            BcSmartspaceTemplateDataUtils.updateVisibility(mImageView, 0);
            loadImageUris(imageUris);
            Log.d(TAG, "imageUri is set");
            return true;
        } else if (extras != null && extras.containsKey("imageBitmap")) {
            Bitmap bitmap = (Bitmap) extras.get("imageBitmap");
            maybeResetImageView(smartspaceTarget);
            BcSmartspaceTemplateDataUtils.updateVisibility(mImageView, 0);
            if (bitmap != null) {
                setRoundedBitmapDrawable(bitmap);
                Log.d(TAG, "imageBitmap is set");
                return true;
            }
            return true;
        } else if (extras == null || !extras.containsKey("loadingScreenState")) {
            return false;
        } else {
            int i3 = extras.getInt("loadingScreenState");
            String dimensionRatio = BcSmartSpaceUtil.getDimensionRatio(extras);
            if (dimensionRatio == null) {
                return false;
            }
            maybeResetImageView(smartspaceTarget);
            showLoadingScreen(dimensionRatio, extras, i3);
            return true;
        }
    }

    private boolean isSysUiContext() {
        return getContext().getPackageName().equals("com.android.systemui");
    }

    private List<Uri> getImageUris(SmartspaceTarget smartspaceTarget) {
        return (List) smartspaceTarget.getIconGrid().stream().filter(action -> {
            return action.getExtras().containsKey("imageUri");
        }).map(action2 -> {
            return action2.getExtras().getString("imageUri");
        }).map(obj -> {
            return Uri.parse(obj);
        }).collect(Collectors.toList());
    }

    private void loadImageUris(List<Uri> list) {
        addFramesToAnimatedDrawable((List) list.stream().map(uri -> {
            return computeImageUri(getContext().getApplicationContext().getContentResolver(), getResources().getDimensionPixelOffset(R.dimen.enhanced_smartspace_height), getResources().getDimension(R.dimen.enhanced_smartspace_secondary_card_corner_radius), new WeakReference(mImageView), new WeakReference(mLoadingScreenView), uri);
        }).filter(d -> {
            return Objects.nonNull(d);
        }).collect(Collectors.toList()));
    }

    private void addFramesToAnimatedDrawable(List<Drawable> list) {
        AnimationDrawable animationDrawable = new AnimationDrawable();
        for (Drawable drawable : list) {
            animationDrawable.addFrame(drawable, mGifFrameDurationInMs);
        }
        mImageView.setImageDrawable(animationDrawable);
        animationDrawable.start();
    }

    private DrawableWithUri computeImageUri(ContentResolver contentResolver, int i, float f, WeakReference weakReference, WeakReference weakReference2, Uri uri) {
        return (DrawableWithUri) mUriToDrawable.computeIfAbsent(uri, newUri -> {
            return drawImageUri(contentResolver, i, f, weakReference, weakReference2, (Uri) newUri);
        });
    }

    public static DrawableWithUri drawImageUri(ContentResolver contentResolver, int i, float f, WeakReference weakReference, WeakReference weakReference2, Uri uri) {
        DrawableWithUri drawableWithUri = new DrawableWithUri(f, i, contentResolver, uri, weakReference, weakReference2);
        new LoadUriTask().execute(drawableWithUri);
        return drawableWithUri;
    }

    private void setRoundedBitmapDrawable(Bitmap bm) {
        if (bm.getHeight() != 0) {
            int dimension = (int) getResources().getDimension(R.dimen.enhanced_smartspace_height);
            bm = Bitmap.createScaledBitmap(bm, dimension * (bm.getWidth() / bm.getHeight()), dimension, true);
        }
        RoundedBitmapDrawable create = RoundedBitmapDrawableFactory.create(getResources(), bm);
        create.setCornerRadius(getResources().getDimension(R.dimen.enhanced_smartspace_secondary_card_corner_radius));
        mImageView.setImageDrawable(create);
    }

    public final void maybeResetImageView(SmartspaceTarget smartspaceTarget) {
        mPreviousTargetId = smartspaceTarget.getSmartspaceTargetId();
        if (!smartspaceTarget.getSmartspaceTargetId().equals(mPreviousTargetId)) {
            mImageView.getLayoutParams().width = -2;
            mImageView.setImageDrawable(null);
            mUriToDrawable.clear();
        }
    }

    private void showLoadingScreen(String str, Bundle extras, int i) {
        BcSmartspaceTemplateDataUtils.updateVisibility(mImageView, 8);
        ((ConstraintLayout.LayoutParams) mLoadingScreenView.getLayoutParams()).dimensionRatio = str;
        mLoadingScreenView.setBackgroundTintList(ColorStateList.valueOf(getContext().getColor(R.color.smartspace_button_background)));
        BcSmartspaceTemplateDataUtils.updateVisibility(mLoadingScreenView, 0);
        toggleProgressBarAndLoadingIcon(extras, i);
    }

    private void toggleProgressBarAndLoadingIcon(Bundle extras, int i) {
        boolean showProgress;
        int vis;
        if (extras.containsKey("progressBarWidth")) {
            mProgressBar.getLayoutParams().width = (int) (getContext().getResources().getDisplayMetrics().density * extras.getInt("progressBarWidth"));
        }
        if (extras.containsKey("progressBarHeight")) {
            mProgressBar.getLayoutParams().height = (int) (getContext().getResources().getDisplayMetrics().density * extras.getInt("progressBarHeight"));
        }
        mProgressBar.setIndeterminateTintList(ColorStateList.valueOf(getContext().getColor(R.color.smartspace_button_text)));
        if (i == 1) {
            showProgress = true;
        } else if (i == 4) {
            showProgress = extras.getBoolean("progressBarVisible", true);
        } else {
            showProgress = false;
        }
        if (showProgress) {
            vis = 0;
        } else {
            vis = 8;
        }
        BcSmartspaceTemplateDataUtils.updateVisibility(mProgressBar, showProgress ? 0 : 8);
        if (extras.containsKey("loadingIconWidth")) {
            mLoadingIcon.getLayoutParams().width = (int) (getContext().getResources().getDisplayMetrics().density * extras.getInt("loadingIconWidth"));
        }
        if (extras.containsKey("loadingIconHeight")) {
            mLoadingIcon.getLayoutParams().height = (int) (getContext().getResources().getDisplayMetrics().density * extras.getInt("loadingIconHeight"));
        }
        if (i == 2) {
            mLoadingIcon.setImageDrawable(getContext().getDrawable(R.drawable.videocam));
        } else if (i == 3) {
            mLoadingIcon.setImageDrawable(getContext().getDrawable(R.drawable.videocam_off));
        } else if (i == 4 || extras.containsKey("loadingScreenIcon")) {
            mLoadingIcon.setImageBitmap((Bitmap) extras.get("loadingScreenIcon"));
            if (extras.getBoolean("tintLoadingIcon", false)) {
                mLoadingIcon.setColorFilter(getContext().getColor(R.color.smartspace_button_text));
            }
        }
        BcSmartspaceTemplateDataUtils.updateVisibility(mLoadingIcon, vis);
    }

    @Override // com.google.android.systemui.smartspace.BcSmartspaceCardGenericImage
    public final void onFinishInflate() {
        super.onFinishInflate();
        mLoadingScreenView = (ViewGroup) findViewById(R.id.loading_screen);
        mProgressBar = (ProgressBar) findViewById(R.id.indeterminateBar);
        mLoadingIcon = (ImageView) findViewById(R.id.loading_screen_icon);
    }

    @Override // com.google.android.systemui.smartspace.BcSmartspaceCardGenericImage, com.google.android.systemui.smartspace.BcSmartspaceCardSecondary
    public final void resetUi() {
        super.resetUi();
        BcSmartspaceTemplateDataUtils.updateVisibility(mImageView, 8);
        BcSmartspaceTemplateDataUtils.updateVisibility(mLoadingScreenView, 8);
        BcSmartspaceTemplateDataUtils.updateVisibility(mProgressBar, 8);
        BcSmartspaceTemplateDataUtils.updateVisibility(mLoadingIcon, 8);
    }

    public static Drawable getSampleBitmapDrawable(InputStream inputStream, final int i) {
        try {
            return ImageDecoder.decodeDrawable(
                    ImageDecoder.createSource(null, inputStream),
                    new ImageDecoder.OnHeaderDecodedListener() {
                        @Override
                        public final void onHeaderDecoded(
                                ImageDecoder imageDecoder,
                                ImageDecoder.ImageInfo imageInfo,
                                ImageDecoder.Source source) {
                            float f;
                            imageDecoder.setAllocator(3);
                            Size size = imageInfo.getSize();
                            if (size.getHeight() != 0) {
                                f = size.getWidth() / size.getHeight();
                            } else {
                                f = 0.0f;
                            }
                            imageDecoder.setTargetSize((int) (i * f), i);
                        }
                    });
        } catch (IOException e) {
            Log.e(TAG, "Unable to decode stream: " + e);
            return null;
        }
    }

    public static class DrawableWithUri extends RoundDrawableWrapper {
        public ContentResolver mContentResolver;
        public Drawable mDrawable;
        public int mHeightInPx;
        public WeakReference<ImageView> mImageViewWeakReference;
        public WeakReference<View> mLoadingScreenWeakReference;
        public Uri mUri;

        public DrawableWithUri(float f, int height, ContentResolver contentResolver, Uri uri, WeakReference imageViewWeakReference, WeakReference loadingScreenWeakReference) {
            super(new ColorDrawable(0), f);
            mUri = uri;
            mHeightInPx = height;
            mContentResolver = contentResolver;
            mImageViewWeakReference = imageViewWeakReference;
            mLoadingScreenWeakReference = loadingScreenWeakReference;
        }
    }

    public static class LoadUriTask extends AsyncTask<DrawableWithUri, Void, DrawableWithUri> {
        @Override // android.os.AsyncTask
        public final DrawableWithUri doInBackground(DrawableWithUri[] drawableWithUriArr) {
            if (drawableWithUriArr.length > 0) {
                DrawableWithUri drawableWithUri = drawableWithUriArr[0];
                try {
                    drawableWithUri.mDrawable = BcSmartspaceCardDoorbell.getSampleBitmapDrawable(drawableWithUri.mContentResolver.openInputStream(drawableWithUri.mUri), drawableWithUri.mHeightInPx);
                } catch (Exception e) {
                    Log.w(TAG, "open uri:" + drawableWithUri.mUri + " got exception:" + e);
                }
                return drawableWithUri;
            }
            return null;
        }

        @Override // android.os.AsyncTask
        public final void onPostExecute(DrawableWithUri drawableWithUri) {
            if (drawableWithUri != null) {
                if (drawableWithUri.mDrawable != null) {
                    drawableWithUri.setDrawable(drawableWithUri.mDrawable);
                    ImageView imageView = drawableWithUri.mImageViewWeakReference.get();
                    int intrinsicWidth = drawableWithUri.mDrawable.getIntrinsicWidth();
                    if (imageView.getLayoutParams().width != intrinsicWidth) {
                        Log.d(TAG, "imageView requestLayout " + drawableWithUri.mUri);
                        imageView.getLayoutParams().width = intrinsicWidth;
                        imageView.requestLayout();
                    }
                } else {
                    BcSmartspaceTemplateDataUtils.updateVisibility(drawableWithUri.mImageViewWeakReference.get(), 8);
                }
                BcSmartspaceTemplateDataUtils.updateVisibility(drawableWithUri.mLoadingScreenWeakReference.get(), 8);
            }
        }
    }
}
