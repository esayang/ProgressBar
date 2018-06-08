package com.easyang.progresslib;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;


/**
 * @author SC16004984
 * @date 2018/6/7 0007.
 */

public class SmartProgressBar extends View {

    /**
     * 进度内居中
     */
    public static final int TEXT_CENTER_PROGRESS = 1;
    /**
     * 进度外
     */
    public static final int TEXT_OUTER_PROGRESS = 2;


    public static final int TEXT_FIXED_LEFT_PROGRESS = 6;


    public static final int TEXT_FIXED_RIGHT_PROGRESS = 7;

    /**
     * 进度条外居右
     */
    public static final int TEXT_OUTER_RIGHT_BAR = 3;
    /**
     * 进度条外居左
     */
    public static final int TEXT_OUTER_LEFT_BAR = 4;


    /**
     * 进度颜色
     */
    private int mProgressColor;
    /**
     * 进度整体颜色
     */
    private int mBackgroundColor;

    /**
     * 进度条高度
     */
    private float mBarHeight;

    /**
     * 进度百分比位置
     */
    private int mPercentGravity;


    /**
     * 文字位置
     */
    private int mTextGravity;

    /**
     * 文字颜色
     */
    private int mTextColor;

    /**
     * 文字大小
     */
    private float mTextSize;

    /**
     * 是否显示进度百分比
     */
    private boolean mPercentVisibility;

    /**
     * 是否显示文字
     */
    private boolean mTextVisibility;

    /**
     * 显示文字内容
     */
    private String mProgressText = "";

    /**
     * corner radius
     */
    private float mCornerRadius = 2;

    /**
     * MAX
     */
    private int max;
    /**
     * 进度
     */
    private int progress;

    private Paint mProgressPaint;

    private Paint mBackPaint;

    private Paint mTextPaint;

    private Paint mPercentPaint;

    private String currentDrawText = "";

    private RectF mProgressRect = new RectF();

    private Rect mPercentRect = new Rect();

    private Rect mTextRect = new Rect();

    private RectF mBackRect = new RectF();

    private float offset = 0;
    private float scaleText = 0.8F;

    private float mPercentWidth;
    private float mPercentHeight;
    private float mPercentHeightOffset;

    private float mTextWidth;
    private float mTextHeight;
    private float mTextHeightOffset;

    private float percentTextStart;
    private float percentTextEnd;

    private float textStart;
    private float textEnd;

    private int percent;


    public SmartProgressBar(Context context) {
        this(context, null);
    }

    public SmartProgressBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SmartProgressBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr, 0);
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public SmartProgressBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs, defStyleAttr, defStyleRes);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        mProgressColor = getResources().getColor(android.R.color.holo_blue_light);
        mBackgroundColor = Color.parseColor("#aaaaaa");
        mTextColor = Color.parseColor("#666666");
        mCornerRadius = dp2px(2);
        mPercentGravity = TEXT_OUTER_PROGRESS;
        mBarHeight = dp2px(6f);

        offset = dp2px(3f);

        final TypedArray attributes = context.getTheme().obtainStyledAttributes(attrs, R.styleable.SmartProgressBar,
                defStyleAttr, defStyleRes);
        mBackgroundColor = attributes.getColor(R.styleable.SmartProgressBar_background_color, mBackgroundColor);
        mProgressColor = attributes.getColor(R.styleable.SmartProgressBar_progress_color, mProgressColor);
        mTextColor = attributes.getColor(R.styleable.SmartProgressBar_text_color, mTextColor);

        mBarHeight = attributes.getDimension(R.styleable.SmartProgressBar_bar_height, mBarHeight);
        offset = attributes.getDimension(R.styleable.SmartProgressBar_text_offset, offset);
        mCornerRadius = attributes.getDimension(R.styleable.SmartProgressBar_corner_radius, mCornerRadius);

        mPercentVisibility = attributes.getBoolean(R.styleable.SmartProgressBar_percent_visibility, mPercentVisibility);
        mPercentGravity = attributes.getInt(R.styleable.SmartProgressBar_percent_gravity, TEXT_OUTER_PROGRESS);

        mTextVisibility = attributes.getBoolean(R.styleable.SmartProgressBar_percent_visibility, mPercentVisibility);
        mTextGravity = attributes.getInt(R.styleable.SmartProgressBar_percent_gravity, TEXT_OUTER_LEFT_BAR);
        mProgressText = attributes.getString(R.styleable.SmartProgressBar_text);

        progress = attributes.getInt(R.styleable.SmartProgressBar_progress, 32);
        max = attributes.getInt(R.styleable.SmartProgressBar_max, 100);
        attributes.recycle();

        mTextSize = mBarHeight * scaleText;

        initPaint();

        setProgress(progress);
        setMax(max);

    }

    private void initPaint() {
        mProgressPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mProgressPaint.setColor(mProgressColor);

        mBackPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBackPaint.setColor(mBackgroundColor);

        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setColor(mTextColor);
        mTextPaint.setTextSize(mTextSize);

        mPercentPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPercentPaint.setColor(mTextColor);
        mPercentPaint.setTextSize(mTextSize);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(measure(widthMeasureSpec, true), measure(heightMeasureSpec, false));
    }

    private int measure(int measureSpec, boolean isWidth) {
        int result;
        int mode = MeasureSpec.getMode(measureSpec);
        int size = MeasureSpec.getSize(measureSpec);
        int padding = isWidth ? getPaddingLeft() + getPaddingRight() : getPaddingTop() + getPaddingBottom();
        if (mode == MeasureSpec.EXACTLY) {
            result = size;
        } else {
            result = isWidth ? getSuggestedMinimumWidth() : getSuggestedMinimumHeight();
            result += padding;
            if (mode == MeasureSpec.AT_MOST) {
                if (isWidth) {
                    result = Math.max(result, size);
                } else {
                    result = Math.min(result, size);
                }
            }
        }
        return result;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        percent = progress * 100 / max;
        calculatePosition();

        canvas.drawRoundRect(mBackRect, mCornerRadius, mCornerRadius, mBackPaint);

        canvas.drawRoundRect(mProgressRect, mCornerRadius, mCornerRadius, mProgressPaint);

        if (mPercentVisibility) {
            canvas.drawText(currentDrawText, percentTextStart, percentTextEnd, mTextPaint);
        }
        if (mTextVisibility) {
            canvas.drawText(mProgressText, textStart, textEnd, mTextPaint);
        }

    }

    @SuppressLint("DefaultLocale")
    private void calculatePosition() {

        mTextWidth = mTextPaint.measureText(currentDrawText);
        Paint.FontMetrics forFontMetrics = mTextPaint.getFontMetrics();
        mTextHeight = Math.abs(forFontMetrics.bottom - forFontMetrics.top);
        mTextHeightOffset = forFontMetrics.descent;
        textEnd = (int) (getHeight() / 2.0f - mTextHeightOffset + mTextHeight / 2.0f);

        currentDrawText = String.format("%d", percent) + "%";
        mPercentWidth = mPercentPaint.measureText(currentDrawText);
        Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
        mPercentHeight = Math.abs(fontMetrics.bottom - fontMetrics.top);
        mPercentHeightOffset = fontMetrics.descent;
        percentTextEnd = (int) (getHeight() / 2.0f - mTextHeightOffset + mTextHeight / 2.0f);

        if (mTextGravity == TEXT_OUTER_LEFT_BAR && mPercentGravity == TEXT_FIXED_LEFT_PROGRESS) {
            calculateTextOuterLeftBar();
        } else if (mTextGravity == TEXT_OUTER_RIGHT_BAR) {
            calculateTextOuterRightBar();
        } else if (mTextGravity == TEXT_FIXED_LEFT_PROGRESS) {
            calculateTextFixedLeftProgress();
        } else {
            calculateTextFixedRightProgress();
        }
    }

    private void calculateTextFixedRightProgress() {
        if (mTextVisibility) {
            textStart = getWidth() - getPaddingRight() - mTextWidth - offset;
        } else {
            textStart = getPaddingLeft();
        }

        mBackRect.left = getPaddingLeft();
        mBackRect.top = (int) (getHeight() / 2.0f - mBarHeight / 2.0f);
        mBackRect.right = (int) (getWidth() - getPaddingRight());
        mBackRect.bottom = (int) (getHeight() / 2.0f + mBarHeight / 2.0f);

        mProgressRect.left = getPaddingLeft();
        mProgressRect.top = (int) (getHeight() / 2.0f - mBarHeight / 2.0f);
        mProgressRect.right = (int) ((getWidth() - mProgressRect.left - getPaddingRight())
                * (percent > 100 ? 100 : percent) / 100 + mProgressRect.left);
        mProgressRect.bottom = (int) (getHeight() / 2.0f + mBarHeight / 2.0f);

        float width = mProgressRect.right - mProgressRect.left;
        if (width < mCornerRadius) {
            mProgressRect.top = (int) (getHeight() / 2.0f - mBarHeight * 0.35);
            mProgressRect.bottom = (int) (getHeight() / 2.0f + mBarHeight * 0.35);
        }

        mPercentPaint.getTextBounds(currentDrawText, 0, currentDrawText.length(), mPercentRect);
        if (mPercentGravity == TEXT_FIXED_LEFT_PROGRESS) {
            percentTextStart = getPaddingLeft() + offset;
        } else {
            if (mPercentWidth + offset > mProgressRect.width()) {
                mPercentRect.left = (int) (mProgressRect.right + offset);
            } else {
                mPercentRect.left = (int) (mProgressRect.left + mProgressRect.width() / 2 - mPercentRect.width() / 2 + offset);
            }
        }

    }

    private void calculateTextFixedLeftProgress() {
        if (mTextVisibility) {
            mTextPaint.getTextBounds(mProgressText, 0, mProgressText.length(), mTextRect);
            textStart = getPaddingLeft() + offset;
        } else {
            textStart = getPaddingLeft();
        }

        mBackRect.left = getPaddingLeft();
        mBackRect.top = (int) (getHeight() / 2.0f - mBarHeight / 2.0f);
        mBackRect.right = (int) (getWidth() - getPaddingRight());
        mBackRect.bottom = (int) (getHeight() / 2.0f + mBarHeight / 2.0f);


        mProgressRect.left = getPaddingLeft();
        mProgressRect.top = (int) (getHeight() / 2.0f - mBarHeight / 2.0f);
        mProgressRect.right = (int) ((getWidth() - mProgressRect.left - getPaddingRight()) * (percent > 100 ? 100 : percent) / 100 + mProgressRect.left);
        mProgressRect.bottom = (int) (getHeight() / 2.0f + mBarHeight / 2.0f);

        float width = mProgressRect.right - mProgressRect.left;
        if (width < mCornerRadius) {
            mProgressRect.top = (int) (getHeight() / 2.0f - mBarHeight * 0.35);
            mProgressRect.bottom = (int) (getHeight() / 2.0f + mBarHeight * 0.35);
        }

        mPercentPaint.getTextBounds(currentDrawText, 0, currentDrawText.length(), mPercentRect);
        percentTextStart = getWidth() - getPaddingRight() - mPercentWidth - offset;
    }


    private void calculateTextOuterRightBar() {
        if (mTextVisibility) {
            textStart = getWidth() - getPaddingRight() - mTextWidth;
        } else {
            textStart = getPaddingLeft();
        }

        mBackRect.left = getPaddingLeft();
        mBackRect.top = (int) (getHeight() / 2.0f - mBarHeight / 2.0f);
        mBackRect.right = (int) (getWidth() - mTextWidth - offset - getPaddingRight());
        mBackRect.bottom = (int) (getHeight() / 2.0f + mBarHeight / 2.0f);

        mProgressRect.left = getPaddingLeft();
        mProgressRect.top = (int) (getHeight() / 2.0f - mBarHeight / 2.0f);
        mProgressRect.right = (int) ((getWidth() - mProgressRect.left - mTextWidth - offset - getPaddingRight())
                * (percent > 100 ? 100 : percent) / 100 + mProgressRect.left);
        mProgressRect.bottom = (int) (getHeight() / 2.0f + mBarHeight / 2.0f);

        float width = mProgressRect.right - mProgressRect.left;
        if (width < mCornerRadius) {
            mProgressRect.top = (int) (getHeight() / 2.0f - mBarHeight * 0.35);
            mProgressRect.bottom = (int) (getHeight() / 2.0f + mBarHeight * 0.35);
        }

        mPercentPaint.getTextBounds(currentDrawText, 0, currentDrawText.length(), mPercentRect);
        if (mPercentGravity == TEXT_FIXED_LEFT_PROGRESS) {
            percentTextStart = getPaddingLeft() + offset;
        } else if (mPercentGravity == TEXT_FIXED_RIGHT_PROGRESS) {
            percentTextStart = getWidth() - getPaddingRight() - mTextWidth - offset * 2;
        } else if (mPercentGravity == TEXT_CENTER_PROGRESS) {
            if (mPercentWidth + offset > mProgressRect.width()) {
                mPercentRect.left = (int) (mProgressRect.right + offset);
            } else {
                mPercentRect.left = (int) (mProgressRect.left + mProgressRect.width() / 2 - mPercentRect.width() / 2 + offset);
            }
        } else {
            percentTextStart = (int) (mProgressRect.right + offset + 10);
            if (percentTextStart + mPercentWidth >= mBackRect.right) {
                percentTextStart = (int) (mBackRect.right - mPercentWidth - 10);
            }
        }
    }

    private void calculateTextOuterLeftBar() {
        if (mTextVisibility) {
            mTextPaint.getTextBounds(mProgressText, 0, mProgressText.length(), mTextRect);
            textStart = getPaddingLeft();
        } else {
            textStart = getPaddingLeft();
        }

        mBackRect.left = textEnd + offset;
        mBackRect.top = (int) (getHeight() / 2.0f - mBarHeight / 2.0f);
        mBackRect.right = (int) (getWidth() - getPaddingRight());
        mBackRect.bottom = (int) (getHeight() / 2.0f + mBarHeight / 2.0f);


        mProgressRect.left = textEnd + offset;
        mProgressRect.top = (int) (getHeight() / 2.0f - mBarHeight / 2.0f);
        mProgressRect.right = (int) ((getWidth() - mProgressRect.left - getPaddingRight()) * (percent > 100 ? 100 : percent) / 100 + mProgressRect.left);
        mProgressRect.bottom = (int) (getHeight() / 2.0f + mBarHeight / 2.0f);
        float width = mProgressRect.right - mProgressRect.left;
        if (width < mCornerRadius) {
            mProgressRect.top = (int) (getHeight() / 2.0f - mBarHeight * 0.35);
            mProgressRect.bottom = (int) (getHeight() / 2.0f + mBarHeight * 0.35);
        }

        mPercentPaint.getTextBounds(currentDrawText, 0, currentDrawText.length(), mPercentRect);
        if (mPercentGravity == TEXT_FIXED_LEFT_PROGRESS) {
            percentTextStart = textEnd + offset * 2;
        } else if (mPercentGravity == TEXT_FIXED_RIGHT_PROGRESS) {
            percentTextStart = getWidth() - getPaddingRight() - mPercentWidth - offset;
        } else if (mPercentGravity == TEXT_CENTER_PROGRESS) {
            if (mPercentWidth + offset > mProgressRect.width()) {
                mPercentRect.left = (int) (mProgressRect.right + offset);
            } else {
                mPercentRect.left = (int) (mProgressRect.left + mProgressRect.width() / 2 - mPercentRect.width() / 2 + offset);
            }
        } else {
            percentTextStart = (int) (mProgressRect.right + offset + 10);
            if (percentTextStart + mPercentWidth >= mBackRect.right) {
                percentTextStart = (int) (mBackRect.right - mPercentWidth - 10);
            }
        }
    }


    public int getProgressColor() {
        return mProgressColor;
    }

    public int getBackgroundColor() {
        return mBackgroundColor;
    }

    public float getBarHeight() {
        return mBarHeight;
    }

    public int getTextGravity() {
        return mPercentGravity;
    }

    public String getProgressText() {
        return mProgressText;
    }

    public float getCornerRadius() {
        return mCornerRadius;
    }


    public int getTextColor() {
        return mTextColor;
    }


    public int getProgress() {
        return progress;
    }


    public float getTextSize() {
        return mTextSize;
    }

    public int getMax() {
        return max;
    }

    public int getPercentGravity() {
        return mPercentGravity;
    }

    public boolean isPercentVisibility() {
        return mPercentVisibility;
    }

    public void setPercentVisibility(boolean mPercentVisibility) {
        this.mPercentVisibility = mPercentVisibility;
        invalidate();
    }

    public void setPercentGravity(int mPercentGravity) {
        this.mPercentGravity = mPercentGravity;
        invalidate();
    }

    public void setProgressColor(int mProgressColor) {
        this.mProgressColor = mProgressColor;
        mProgressPaint.setColor(mProgressColor);
        invalidate();
    }

    public void setBarHeight(float mBarHeight) {
        this.mBarHeight = mBarHeight;
        invalidate();
    }

    @Override
    public void setBackgroundColor(int mBackgroundColor) {
        this.mBackgroundColor = mBackgroundColor;
        mBackPaint.setColor(mBackgroundColor);
        invalidate();
    }

    public void setTextGravity(int mTextGravity) {
        this.mPercentGravity = mTextGravity;
    }

    public void setCornerRadius(float mCornerRadius) {
        this.mCornerRadius = mCornerRadius;
        invalidate();
    }

    public void setProgressText(String mProgressText) {
        this.mProgressText = mProgressText;
        invalidate();
    }

    public void setTextColor(int mTextColor) {
        this.mTextColor = mTextColor;
        mTextPaint.setColor(mTextColor);
        invalidate();
    }

    public void setTextSize(float mTextSize) {
        this.mTextSize = mTextSize;
        mTextPaint.setTextSize(mTextSize);
        invalidate();
    }

    public boolean isTextVisibility() {
        return mPercentVisibility;
    }

    public void setTextVisibility(boolean mTextVisibility) {
        this.mPercentVisibility = mTextVisibility;
        invalidate();
    }

    public void setMax(int max) {
        if (max > 0) {
            this.max = max;
            invalidate();
        }

    }

    public void setProgress(int progress) {
        this.progress = progress;
        invalidate();
    }

    public void incrementProgressBy(int by) {
        if (by > 0) {
            setProgress(getProgress() + by);
        }
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        super.onSaveInstanceState();
        final Bundle bundle = new Bundle();
        bundle.putFloat("bar_height", mBarHeight);
        bundle.putInt("background_color", mBackgroundColor);
        bundle.putInt("text_color", mTextColor);
        bundle.putInt("progress_color", mProgressColor);
        bundle.putFloat("text_size", mTextSize);
        bundle.putFloat("corner_radius", mCornerRadius);
        bundle.putBoolean("percent_visibility", mPercentVisibility);
        bundle.putInt("percent_gravity", mPercentGravity);
        bundle.putBoolean("text_visibility", mTextVisibility);
        bundle.putInt("text_gravity", mTextGravity);
        bundle.putString("text", mProgressText);
        bundle.putInt("progress", progress);
        bundle.putInt("max", max);
        bundle.putParcelable("state", super.onSaveInstanceState());
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            final Bundle bundle = (Bundle) state;
            mBarHeight = bundle.getFloat("bar_height");
            mBackgroundColor = bundle.getInt("background_color");
            mTextColor = bundle.getInt("text_color");
            mProgressColor = bundle.getInt("progress_color");
            mTextSize = bundle.getFloat("text_size");
            mCornerRadius = bundle.getFloat("corner_radius");
            mPercentVisibility = bundle.getBoolean("percent_visibility");
            mPercentGravity = bundle.getInt("percent_gravity");
            mTextVisibility = bundle.getBoolean("text_visibility");
            mTextGravity = bundle.getInt("text_gravity");
            mProgressText = bundle.getString("text");
            progress = bundle.getInt("progress");
            max = bundle.getInt("max");
            initPaint();
            setProgress(progress);
            setMax(max);
            super.onRestoreInstanceState(bundle.getParcelable("state"));
            return;
        }
        super.onRestoreInstanceState(state);
    }

    public float dp2px(float dp) {
        final float scale = getResources().getDisplayMetrics().density;
        return dp * scale + 0.5f;
    }

    public float sp2px(float sp) {
        final float scale = getResources().getDisplayMetrics().scaledDensity;
        return sp * scale;
    }

}
