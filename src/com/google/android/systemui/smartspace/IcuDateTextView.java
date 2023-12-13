package com.google.android.systemui.smartspace;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.icu.text.DateFormat;
import android.icu.text.DisplayContext;
import android.os.Handler;
import android.os.SystemClock;
import android.util.AttributeSet;

import com.android.systemui.bcsmartspace.R;

import java.util.Locale;
import java.util.Objects;

public class IcuDateTextView extends DoubleShadowTextView {
    public DateFormat mFormatter;
    public Handler mHandler;
    public final BroadcastReceiver mIntentReceiver;
    public String mText;
    public final Runnable mTicker;

    public IcuDateTextView(Context context) {
        this(context, null);
    }

    public IcuDateTextView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet, 0);
        mTicker = this::onTimeTick;
        mIntentReceiver = new BroadcastReceiver() { // from class: com.google.android.systemui.smartspace.IcuDateTextView.1
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context2, Intent intent) {
                IcuDateTextView.onTimeChanged(!intent.getAction().equals(Intent.ACTION_TIME_TICK));
            }
        };
    }

    @Override // android.widget.TextView, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_TIME_CHANGED);
        intentFilter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
        getContext().registerReceiver(mIntentReceiver, intentFilter);
        onTimeChanged(true);
        mHandler = new Handler();
    }

    @Override // android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mHandler != null) {
            getContext().unregisterReceiver(mIntentReceiver);
            mHandler = null;
        }
    }

    private void onTimeTick() {
        onTimeChanged(false);
        if (mHandler != null) {
            long uptimeMillis = SystemClock.uptimeMillis();
            mHandler.postAtTime(mTicker, uptimeMillis + (1000 - (uptimeMillis % 1000)));
        }
    }

    @Override // android.view.View
    public void onVisibilityAggregated(boolean isVisible) {
        super.onVisibilityAggregated(isVisible);
        if (mHandler != null) {
            mHandler.removeCallbacks(mTicker);
            if (isVisible) {
                mTicker.run();
            }
        }
    }

    public void onTimeChanged(boolean force) {
        if (!isShown()) {
            return;
        }
        if (mFormatter == null || force) {
            DateFormat format = DateFormat.getInstanceForSkeleton(getContext().getString(R.string.smartspace_icu_date_pattern), Locale.getDefault());
            mFormatter = format;
            format.setContext(DisplayContext.CAPITALIZATION_FOR_BEGINNING_OF_SENTENCE);
        }
        String format2 = mFormatter.format(Long.valueOf(System.currentTimeMillis()));
        if (!Objects.equals(mText, format2)) {
            mText = format2;
            setText(format2);
            setContentDescription(format2);
        }
    }
}
