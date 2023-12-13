package com.google.android.systemui.smartspace;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.smartspace.SmartspaceAction;
import android.app.smartspace.SmartspaceTarget;
import android.app.smartspace.SmartspaceTargetEvent;
import android.content.ComponentName;
import android.content.Context;
import android.database.ContentObserver;
import android.graphics.drawable.Drawable;
import android.os.Debug;
import android.os.Handler;
import android.os.Parcelable;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.ArraySet;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager.widget.ViewPager;

import com.android.systemui.bcsmartspace.R;
import com.android.systemui.plugins.BcSmartspaceDataPlugin;
import com.android.systemui.plugins.FalsingManager;
import com.google.android.systemui.smartspace.CardPagerAdapter;
import com.google.android.systemui.smartspace.logging.BcSmartspaceCardLogger;
import com.google.android.systemui.smartspace.logging.BcSmartspaceCardLoggerUtil;
import com.google.android.systemui.smartspace.logging.BcSmartspaceCardLoggingInfo;
import com.google.android.systemui.smartspace.logging.BcSmartspaceSubcardLoggingInfo;
import com.google.android.systemui.smartspace.uitemplate.BaseTemplateCard;

import java.time.DateTimeException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class BcSmartspaceView extends FrameLayout implements BcSmartspaceDataPlugin.SmartspaceTargetListener, BcSmartspaceDataPlugin.SmartspaceView {
    public static final String TAG = "BcSmartspaceView";
    public static final boolean DEBUG = Log.isLoggable(TAG, 3);
    public final CardPagerAdapter mAdapter;
    public boolean mAnimateSmartspaceUpdate;
    public final ContentObserver mAodObserver;
    public int mCardPosition;
    public BcSmartspaceDataPlugin mDataProvider;
    public boolean mIsAodEnabled;
    public ArraySet<String> mLastReceivedTargets;
    public final ViewPager.OnPageChangeListener mOnPageChangeListener;
    public PageIndicator mPageIndicator;
    public List<? extends Parcelable> mPendingTargets;
    public Animator mRunningAnimation;
    public int mScrollState;
    public ViewPager mViewPager;

    public BcSmartspaceView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        mLastReceivedTargets = new ArraySet<>();
        mIsAodEnabled = false;
        mCardPosition = 0;
        mAnimateSmartspaceUpdate = false;
        mScrollState = 0;
        mAodObserver = new ContentObserver(new Handler()) { // from class: com.google.android.systemui.smartspace.BcSmartspaceView.1
            @Override // android.database.ContentObserver
            public void onChange(boolean z) {
                BcSmartspaceView.onSettingsChanged();
            }
        };
        mAdapter = new CardPagerAdapter(this);
        mOnPageChangeListener = new ViewPager.OnPageChangeListener() { // from class: com.google.android.systemui.smartspace.BcSmartspaceView.2
            public void onPageScrollStateChanged(int state) {
                List<? extends Parcelable> list;
                BcSmartspaceView.mScrollState = state;
                if (state == 0 && (list = BcSmartspaceView.mPendingTargets) != null) {
                    BcSmartspaceView.onSmartspaceTargetsUpdated(list);
                }
            }

            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (BcSmartspaceView.mPageIndicator != null) {
                    BcSmartspaceView.mPageIndicator.setPageOffset(position, positionOffset);
                }
            }

            public void onPageSelected(int position) {
                SmartspaceTarget targetAtPosition = BcSmartspaceView.mAdapter.getTargetAtPosition(BcSmartspaceView.mCardPosition);
                BcSmartspaceView.mCardPosition = position;
                SmartspaceTarget targetAtPosition2 = BcSmartspaceView.mAdapter.getTargetAtPosition(position);
                BcSmartspaceView.logSmartspaceEvent(targetAtPosition2, BcSmartspaceView.mCardPosition, BcSmartspaceEvent.SMARTSPACE_CARD_SEEN);
                if (BcSmartspaceView.mDataProvider == null) {
                    Log.w(BcSmartspaceView.TAG, "Cannot notify target hidden/shown smartspace events: data provider null");
                    return;
                }
                if (targetAtPosition == null) {
                    Log.w(BcSmartspaceView.TAG, "Cannot notify target hidden smartspace event: previous target is null.");
                } else {
                    SmartspaceTargetEvent.Builder builder = new SmartspaceTargetEvent.Builder(3);
                    builder.setSmartspaceTarget(targetAtPosition);
                    SmartspaceAction baseAction = targetAtPosition.getBaseAction();
                    if (baseAction != null) {
                        builder.setSmartspaceActionId(baseAction.getId());
                    }
                    BcSmartspaceView.mDataProvider.notifySmartspaceEvent(builder.build());
                }
                SmartspaceTargetEvent.Builder builder2 = new SmartspaceTargetEvent.Builder(2);
                builder2.setSmartspaceTarget(targetAtPosition2);
                SmartspaceAction baseAction2 = targetAtPosition2.getBaseAction();
                if (baseAction2 != null) {
                    builder2.setSmartspaceActionId(baseAction2.getId());
                }
                BcSmartspaceView.mDataProvider.notifySmartspaceEvent(builder2.build());
            }
        };
    }

    @Override // android.view.View
    public void onVisibilityAggregated(boolean isVisible) {
        super.onVisibilityAggregated(isVisible);
        if (mDataProvider != null) {
            mDataProvider.notifySmartspaceEvent(new SmartspaceTargetEvent.Builder(isVisible ? 6 : 7).build());
        }
    }

    @Override // android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
        mViewPager = findViewById(R.id.smartspace_card_pager);
        mPageIndicator = (PageIndicator) findViewById(R.id.smartspace_page_indicator);
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mViewPager.setAdapter(mAdapter);
        mViewPager.addOnPageChangeListener(mOnPageChangeListener);
        mPageIndicator.setNumPages(mAdapter.getCount());
        try {
            getContext().getContentResolver().registerContentObserver(Settings.Secure.getUriFor("doze_always_on"), false, mAodObserver, -1);
            mIsAodEnabled = isAodEnabled(getContext());
        } catch (Exception e) {
            Log.w(TAG, "Unable to register Doze Always on content observer.", e);
        }
        if (mDataProvider != null) {
            registerDataProvider(mDataProvider);
        }
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        getContext().getContentResolver().unregisterContentObserver(mAodObserver);
        if (mDataProvider != null) {
            mDataProvider.unregisterListener(this);
        }
    }

    @Override // android.widget.FrameLayout, android.view.View
    protected void onMeasure(int i, int i2) {
        int size = View.MeasureSpec.getSize(i2);
        int dimensionPixelSize = getContext().getResources().getDimensionPixelSize(R.dimen.enhanced_smartspace_height);
        if (size > 0 && size < dimensionPixelSize) {
            float f3 = size / dimensionPixelSize;
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(Math.round(View.MeasureSpec.getSize(i) / f3), 1073741824), View.MeasureSpec.makeMeasureSpec(dimensionPixelSize, 1073741824));
            setScaleX(f3);
            setScaleY(f3);
            setPivotX(0.0f);
            setPivotY(dimensionPixelSize / 2.0f);
            return;
        }
        super.onMeasure(i, i2);
        setScaleX(1.0f);
        setScaleY(1.0f);
        resetPivot();
    }

    public void registerDataProvider(BcSmartspaceDataPlugin plugin) {
        mDataProvider = plugin;
        plugin.registerListener(this);
        mAdapter.setDataProvider(mDataProvider);
    }

    public void onSmartspaceTargetsUpdated(List<? extends Parcelable> list) {
        int i;
        BaseTemplateCard baseTemplateCard;
        BcSmartspaceCard bcSmartspaceCard;
        if (DEBUG) {
            Log.d(TAG, "@" + Integer.toHexString(hashCode()) + ", onTargetsAvailable called. Callers = " + Debug.getCallers(5));
            Log.d(TAG, "    targets.size() = " + list.size());
            Log.d(TAG, "    targets = " + list);
        }
        if (mScrollState != 0 && mAdapter.getCount() > 1) {
            mPendingTargets = list;
            return;
        }
        mPendingTargets = null;
        boolean z = getLayoutDirection() == 1;
        int i2 = mViewPager.getCurrentItem();
        if (z) {
            i = mAdapter.getCount() - i2;
            ArrayList<? extends Parcelable> arrayList = new ArrayList<>(list);
            Collections.reverse(arrayList);
            list = arrayList;
        } else {
            i = i2;
        }
        CardPagerAdapter.ViewHolder viewHolder = mAdapter.mViewHolders.get(i2);
        if (viewHolder == null) {
            baseTemplateCard = null;
        } else {
            baseTemplateCard = viewHolder.mCard;
        }
        CardPagerAdapter.ViewHolder viewHolder2 = mAdapter.mViewHolders.get(i2);
        if (viewHolder2 == null) {
            bcSmartspaceCard = null;
        } else {
            bcSmartspaceCard = viewHolder2.mLegacyCard;
        }
        mAdapter.mAODTargets.clear();
        mAdapter.mLockscreenTargets.clear();
        mAdapter.mHasDifferentTargets = false;
        mAdapter.mNextAlarmData.mHolidayAlarmsTarget = null;
        list.forEach(obj -> {
            int q;
            SmartspaceTarget it = (SmartspaceTarget) obj;
            if (it.getFeatureType() == 34) {
                mAdapter.mNextAlarmData.mHolidayAlarmsTarget = it;
                return;
            }
            if (it.getBaseAction() != null && it.getBaseAction().getExtras() != null) {
                q = it.getBaseAction().getExtras().getInt("SCREEN_EXTRA", 3);
            } else {
                q = 3;
            }
            if ((q & 2) != 0) {
                mAdapter.mAODTargets.add(it);
            }
            if ((q & 1) != 0) {
                mAdapter.mLockscreenTargets.add(it);
            }
            if (q != 3) {
                mAdapter.mHasDifferentTargets = true;
            }
        });
        mAdapter.addDefaultDateCardIfEmpty(mAdapter.mAODTargets);
        mAdapter.addDefaultDateCardIfEmpty(mAdapter.mLockscreenTargets);
        mAdapter.updateTargetVisibility();
        mAdapter.notifyDataSetChanged();
        int count = mAdapter.getCount();
        if (z) {
            mViewPager.setCurrentItem(Math.max(0, Math.min(count - 1, count - i)), false);
        }
        if (mPageIndicator != null) {
            mPageIndicator.setNumPages(count);
        }
        if (mAnimateSmartspaceUpdate) {
            if (baseTemplateCard != null) {
                animateSmartspaceUpdate(baseTemplateCard);
            } else if (bcSmartspaceCard != null) {
                animateSmartspaceUpdate(bcSmartspaceCard);
            }
        }
        for (int i3 = 0; i3 < count; i3++) {
            SmartspaceTarget targetAtPosition = mAdapter.getTargetAtPosition(i3);
            if (!mLastReceivedTargets.contains(targetAtPosition.getSmartspaceTargetId())) {
                logSmartspaceEvent(targetAtPosition, i3, BcSmartspaceEvent.SMARTSPACE_CARD_RECEIVED);
                SmartspaceTargetEvent.Builder builder = new SmartspaceTargetEvent.Builder(8);
                builder.setSmartspaceTarget(targetAtPosition);
                SmartspaceAction baseAction = targetAtPosition.getBaseAction();
                if (baseAction != null) {
                    builder.setSmartspaceActionId(baseAction.getId());
                }
                mDataProvider.notifySmartspaceEvent(builder.build());
            }
        }
        mLastReceivedTargets.clear();
        mLastReceivedTargets.addAll((Collection) mAdapter.mSmartspaceTargets.stream().map((v0) -> {
            return v0.getSmartspaceTargetId();
        }).collect(Collectors.toList()));
        mAdapter.notifyDataSetChanged();
    }

    public void logSmartspaceEvent(SmartspaceTarget smartspaceTarget, int rank, BcSmartspaceEvent bcSmartspaceEvent) {
        int i2;
        BcSmartspaceSubcardLoggingInfo createSubcardLoggingInfo;
        if (bcSmartspaceEvent == BcSmartspaceEvent.SMARTSPACE_CARD_RECEIVED) {
            try {
                i2 = (int) Instant.now().minusMillis(smartspaceTarget.getCreationTimeMillis()).toEpochMilli();
            } catch (ArithmeticException | DateTimeException e) {
                Log.e(TAG, "received_latency_millis will be -1 due to exception ", e);
                i2 = -1;
            }
        } else {
            i2 = 0;
        }
        BcSmartspaceCardLoggingInfo.Builder builder = new BcSmartspaceCardLoggingInfo.Builder();
        builder.mInstanceId = InstanceId.create(smartspaceTarget);
        builder.mFeatureType = smartspaceTarget.getFeatureType();
        String packageName = getContext().getPackageName();
        CardPagerAdapter cardPagerAdapter = mAdapter;
        builder.mDisplaySurface = BcSmartSpaceUtil.getLoggingDisplaySurface(packageName, cardPagerAdapter.mIsDreaming, cardPagerAdapter.mDozeAmount);
        builder.mRank = rank;
        builder.mCardinality = mAdapter.getCount();
        builder.mReceivedLatency = i2;
        builder.mUid = BcSmartspaceCardLoggerUtil.getUid(getContext().getPackageManager(), smartspaceTarget);
        if (smartspaceTarget.getTemplateData() != null) {
            createSubcardLoggingInfo = BcSmartspaceCardLoggerUtil.createSubcardLoggingInfo(smartspaceTarget.getTemplateData());
        } else {
            createSubcardLoggingInfo = BcSmartspaceCardLoggerUtil.createSubcardLoggingInfo(smartspaceTarget);
        }
        builder.mSubcardInfo = createSubcardLoggingInfo;
        BcSmartspaceCardLoggingInfo bcSmartspaceCardLoggingInfo = new BcSmartspaceCardLoggingInfo(builder);
        if (smartspaceTarget.getTemplateData() != null) {
            BcSmartspaceCardLoggerUtil.tryForcePrimaryFeatureType(bcSmartspaceCardLoggingInfo);
        } else {
            BcSmartspaceCardLoggerUtil.tryForcePrimaryFeatureTypeAndInjectWeatherSubcard(bcSmartspaceCardLoggingInfo, smartspaceTarget);
        }
        BcSmartspaceCardLogger.log(bcSmartspaceEvent, bcSmartspaceCardLoggingInfo);
    }

    public void animateSmartspaceUpdate(final ConstraintLayout constraintLayout) {
        if (mRunningAnimation == null && constraintLayout.getParent() == null) {
            final ViewGroup viewGroup = (ViewGroup) mViewPager.getParent();
            constraintLayout.measure(View.MeasureSpec.makeMeasureSpec(mViewPager.getWidth(), 1073741824), View.MeasureSpec.makeMeasureSpec(mViewPager.getHeight(), 1073741824));
            constraintLayout.layout(mViewPager.getLeft(), mViewPager.getTop(), mViewPager.getRight(), mViewPager.getBottom());
            AnimatorSet animatorSet = new AnimatorSet();
            float dimension = getContext().getResources().getDimension(R.dimen.enhanced_smartspace_dismiss_margin);
            animatorSet.play(ObjectAnimator.ofFloat(constraintLayout, View.TRANSLATION_Y, 0.0f, (-getHeight()) - dimension));
            animatorSet.play(ObjectAnimator.ofFloat(constraintLayout, View.ALPHA, 1.0f, 0.0f));
            animatorSet.play(ObjectAnimator.ofFloat(mViewPager, View.TRANSLATION_Y, getHeight() + dimension, 0.0f));
            animatorSet.addListener(new AnimatorListenerAdapter() { // from class: com.google.android.systemui.smartspace.BcSmartspaceView.3
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animator) {
                    constraintLayout.setTranslationY(0.0f);
                    constraintLayout.setAlpha(1.0f);
                    viewGroup.getOverlay().remove((View) constraintLayout);
                    BcSmartspaceView bcSmartspaceView = BcSmartspaceView.this;
                    bcSmartspaceView.mRunningAnimation = null;
                    bcSmartspaceView.mAnimateSmartspaceUpdate = false;
                }

                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationStart(Animator animator) {
                    viewGroup.getOverlay().add((View) constraintLayout);
                }
            });
            mRunningAnimation = animatorSet;
            animatorSet.start();
        }
    }

    public final int getCurrentCardTopPadding() {
        BcSmartspaceCard bcSmartspaceCard;
        BaseTemplateCard baseTemplateCard;
        CardPagerAdapter.ViewHolder viewHolder = mAdapter.mViewHolders.get(mViewPager.getCurrentItem());
        ViewGroup viewGroup = null;
        if (viewHolder == null) {
            bcSmartspaceCard = null;
        } else {
            bcSmartspaceCard = viewHolder.mLegacyCard;
        }
        if (bcSmartspaceCard != null) {
            CardPagerAdapter.ViewHolder viewHolder2 = mAdapter.mViewHolders.get(mViewPager.getCurrentItem());
            if (viewHolder2 != null) {
                viewGroup = viewHolder2.mLegacyCard;
            }
            return viewGroup.getPaddingTop();
        }
        CardPagerAdapter.ViewHolder viewHolder3 = mAdapter.mViewHolders.get(mViewPager.getCurrentItem());
        if (viewHolder3 == null) {
            baseTemplateCard = null;
        } else {
            baseTemplateCard = viewHolder3.mCard;
        }
        if (baseTemplateCard != null) {
            CardPagerAdapter.ViewHolder viewHolder4 = mAdapter.mViewHolders.get(mViewPager.getCurrentItem());
            if (viewHolder4 != null) {
                viewGroup = viewHolder4.mCard;
            }
            return viewGroup.getPaddingTop();
        }
        return 0;
    }

    public int getSelectedPage() {
        return mViewPager.getCurrentItem();
    }

    public void setSelectedPage(int i) {
        mViewPager.setCurrentItem(i, false);
        mPageIndicator.setPageOffset(i, 0.0f);
    }

    public void setPrimaryTextColor(int i) {
        mAdapter.setPrimaryTextColor(i);
        mPageIndicator.setPrimaryColor(i);
    }

    public void setDozeAmount(float f) {
        mPageIndicator.setAlpha(1.0f - f);
        ArrayList<SmartspaceTarget> arrayList = mAdapter.mSmartspaceTargets;
        mAdapter.setDozeAmount(f);
        CardPagerAdapter cardPagerAdapter2 = mAdapter;
        if (cardPagerAdapter2.mHasDifferentTargets && cardPagerAdapter2.mSmartspaceTargets != arrayList && cardPagerAdapter2.getCount() > 0) {
            mViewPager.setCurrentItem(0, false);
            mPageIndicator.setPageOffset(0, 0.0f);
        }
        mPageIndicator.setNumPages(mAdapter.getCount());
        String packageName = getContext().getPackageName();
        CardPagerAdapter cardPagerAdapter3 = mAdapter;
        int loggingDisplaySurface = BcSmartSpaceUtil.getLoggingDisplaySurface(packageName, cardPagerAdapter3.mIsDreaming, cardPagerAdapter3.mDozeAmount);
        if (loggingDisplaySurface == -1) {
            return;
        }
        if (loggingDisplaySurface == 3 && !mIsAodEnabled) {
            return;
        }
        if (DEBUG) {
            Log.d(TAG, "@" + Integer.toHexString(hashCode()) + ", setDozeAmount: Logging SMARTSPACE_CARD_SEEN, currentSurface = " + loggingDisplaySurface);
        }
        BcSmartspaceEvent bcSmartspaceEvent = BcSmartspaceEvent.SMARTSPACE_CARD_SEEN;
        SmartspaceTarget targetAtPosition = mAdapter.getTargetAtPosition(mCardPosition);
        if (targetAtPosition == null) {
            Log.w(TAG, "Current card is not present in the Adapter; cannot log.");
        } else {
            logSmartspaceEvent(targetAtPosition, mCardPosition, bcSmartspaceEvent);
        }
        if (mAdapter.mNextAlarmData.mImage != null) {
            logSmartspaceEvent(new SmartspaceTarget.Builder("upcoming_alarm_card_94510_12684", new ComponentName(getContext(), getClass()), getContext().getUser()).setFeatureType(23).build(), 0, bcSmartspaceEvent);
            if (!TextUtils.isEmpty(mAdapter.mNextAlarmData.getHolidayAlarmText(null))) {
                logSmartspaceEvent(mAdapter.mNextAlarmData.mHolidayAlarmsTarget, 0, bcSmartspaceEvent);
            }
        }
    }

    public void setIsDreaming(boolean isDreaming) {
        mAdapter.mIsDreaming = isDreaming;
    }

    public void setUiSurface(String uiSurface) {
        mAdapter.mUiSurface = uiSurface;
    }

    public void setKeyguardBypassEnabled(boolean isEnabled) {
        mAdapter.mKeyguardBypassEnabled = isEnabled;
        mAdapter.updateTargetVisibility();
    }

    public void setDnd(Drawable drawable, String str) {
        mAdapter.setDnd(drawable, str);
    }

    public void setNextAlarm(Drawable drawable, String str) {
        mAdapter.setNextAlarm(drawable, str);
    }

    public void setMediaTarget(SmartspaceTarget smartspaceTarget) {
        mAdapter.setMediaTarget(smartspaceTarget);
    }

    @Override // android.view.View
    public void setOnLongClickListener(View.OnLongClickListener onLongClickListener) {
        mViewPager.setOnLongClickListener(onLongClickListener);
    }

    private void onSettingsChanged() {
        mIsAodEnabled = isAodEnabled(getContext());
    }

    private static boolean isAodEnabled(Context context) {
        return Settings.Secure.getIntForUser(context.getContentResolver(), "doze_always_on", 0, context.getUserId()) == 1;
    }

    public void setFalsingManager(FalsingManager falsingManager) {
        BcSmartSpaceUtil.sFalsingManager = falsingManager;
    }

    public void setIntentStarter(BcSmartspaceDataPlugin.IntentStarter intentStarter) {
        BcSmartSpaceUtil.sIntentStarter = intentStarter;
    }
}
