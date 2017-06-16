package com.example.tahajjudtutorial;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Andrii Chernysh
 *         Developed by <u>Ubrainians</u>
 */
public class TahajjudClock extends View implements ViewTreeObserver.OnGlobalLayoutListener {
    private Context mContext;

    private Paint mTimeIconPaint;
    private Paint mIndicatorBackgroundPaint;
    private Paint mTimeIndicatorBackgroundPaint;
    private Paint mIntervalTimeSeparatorLinePaint;
    private Paint mIntervalTextPaint;
    private Paint mCircleTextPaint;
    private Paint mOuterPaintForTextCircle;
    private Paint mInnerPaintForTextCircle;
    private Paint mIndicatorArcPaint;

    private Matrix mIndicatorMatrix;

    private Path mIndicatorArcPath;
    private Path mIndicatorIconPath;
    private SweepGradient mIndicatorGradient;

    private Point mCenter;
    private int mInnerRadius;
    private int mOuterRadius;
    private int mClockRadius;
    private int startAngle;

    private Point mClockCoords;
    private Point mBellCoords;
    private RectF mInnerRect;
    private RectF mOuterRect;
    private String mTahajjudTimeHours;
    private String mTahajjudTimeMinutes;

    /*Time fields*/
    private long mInterval;
    private double mDivision;
    private long time;
    private SimpleDateFormat mSimpleDateFormat;
    private boolean isBellMoving;

    public TahajjudClock(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        getViewTreeObserver().addOnGlobalLayoutListener(this);
        mTimeIconPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mIntervalTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCircleTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mIndicatorBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTimeIndicatorBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mIntervalTimeSeparatorLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mOuterPaintForTextCircle = new Paint(Paint.ANTI_ALIAS_FLAG);
        mInnerPaintForTextCircle = new Paint(Paint.ANTI_ALIAS_FLAG);
        mIndicatorArcPath = new Path();
        mIndicatorIconPath = new Path();
        mIndicatorMatrix = new Matrix();
        mSimpleDateFormat = new SimpleDateFormat("HH:mm");
    }

    @Override
    public void onGlobalLayout() {
        getViewTreeObserver().removeOnGlobalLayoutListener(this);
        mCenter = new Point(getWidth() / 2, getHeight() / 2);
        mOuterRadius = (int) (mCenter.x - AndroidUtils.convertDpToPixel(40, mContext));
        mInnerRadius = (int) (mCenter.x - AndroidUtils.convertDpToPixel(90, mContext));
        mClockRadius = mOuterRadius - (((mOuterRadius - mInnerRadius) / 2));

        mOuterRect = new RectF(mCenter.x - mOuterRadius, mCenter.y - mOuterRadius,
                mCenter.x + mOuterRadius, mCenter.y + mOuterRadius);
        mInnerRect = new RectF(mCenter.x - mInnerRadius, mCenter.y - mInnerRadius,
                mCenter.x + mInnerRadius, mCenter.y + mInnerRadius);

        mInterval = 180;
        try {
            mDivision = 2 * Math.PI / mInterval;
        } catch (ArithmeticException e) {
            Log.e("LOG_TAG", "Tahajjud interval is 0");
        }
        mClockCoords = new Point();
        mClockCoords.x = (int) (mCenter.x + mClockRadius * Math.cos(Math.PI / 2 + mDivision * 0));
        mClockCoords.y = (int) (mCenter.y + mClockRadius * Math.sin(Math.PI / 2 + mDivision * 0));

        mBellCoords = new Point();
        mBellCoords.x = (int) (mCenter.x + mClockRadius * Math.cos(Math.PI / 2 + mDivision * 0));
        mBellCoords.y = (int) (mCenter.y + mClockRadius * Math.sin(Math.PI / 2 + mDivision * 0));

        startAngle = (int) Math.toDegrees(Math.PI - Math.atan2(mCenter.y - mClockCoords.y, mClockCoords.x - mCenter.x));

        time = 0 * 60 * 1000;
        String[] timeStr = mSimpleDateFormat.format(new Date(time)).split(":");
        mTahajjudTimeHours = timeStr[0];
        mTahajjudTimeMinutes = timeStr[1];

        setLayerType(LAYER_TYPE_SOFTWARE, mTimeIconPaint);

        initPaints();
    }

    private void initPaints() {
        initPaintIndicatorBackground();
        initPaintForIndicatorCircle();
        initPaintTimeIndicatorIconBackground();
        initPaintForTimeSeparatorLine();
        initPaintForCircleTimeText();
        initPaintForIntervalTimeText();
        initOuterPaintForTextCircle();
        initInnerPaintForTextCircle();
    }

    private void initPaintIndicatorBackground() {
        mIndicatorBackgroundPaint.setColor(ContextCompat.getColor(mContext, R.color.tahajjud_indicator_background));
    }

    private void initPaintForIndicatorCircle() {
        mIndicatorArcPaint = new Paint();
        mIndicatorArcPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mIndicatorArcPaint.setShadowLayer(14, 0, 0,
                ContextCompat.getColor(mContext, R.color.tahajjud_sweep_gradient_3));
        int[] gradientColors = new int[]{
                ContextCompat.getColor(mContext, R.color.tahajjud_indicator_background),
                ContextCompat.getColor(mContext, R.color.tahajjud_sweep_gradient_2),
                ContextCompat.getColor(mContext, R.color.tahajjud_sweep_gradient_3)
        };

        float[] gradientPositions = new float[]{0.5f, 0.7f, 0.9f};
        mIndicatorGradient = new SweepGradient(mCenter.x, mCenter.y, gradientColors, gradientPositions);
        mIndicatorMatrix.setRotate((float) startAngle - 180, mCenter.x, mCenter.y);
        mIndicatorGradient.setLocalMatrix(mIndicatorMatrix);
        mIndicatorArcPaint.setShader(mIndicatorGradient);
    }

    private void initPaintTimeIndicatorIconBackground() {
        mTimeIndicatorBackgroundPaint.reset();
        mTimeIndicatorBackgroundPaint.setStyle(Paint.Style.FILL);
        mTimeIndicatorBackgroundPaint.setColor(ContextCompat.getColor(mContext, R.color.tahajjud_sweep_gradient_3));
        mTimeIndicatorBackgroundPaint.setShadowLayer(16, 0, 0,
                ContextCompat.getColor(mContext, R.color.tahajjud_sweep_gradient_3));
    }

    private void initPaintForTimeSeparatorLine() {
        mIntervalTimeSeparatorLinePaint.setStrokeWidth(
                AndroidUtils.convertDpToPixel(3, mContext));
        mIntervalTimeSeparatorLinePaint.setColor(ContextCompat
                .getColor(mContext, R.color.tahajjud_time_separator_line));
    }

    private void initPaintForCircleTimeText() {
        mCircleTextPaint.setColor(ContextCompat.getColor(mContext, R.color.tahajjud_text));
        mCircleTextPaint.setTextSize(AndroidUtils.convertSpToPixels(93, mContext));
        mCircleTextPaint.setTypeface(Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD));
    }

    private void initPaintForIntervalTimeText() {
        mIntervalTextPaint.setColor(ContextCompat.getColor(mContext, R.color.tahajjud_text));
        mIntervalTextPaint.setTextSize(AndroidUtils.convertSpToPixels(18, mContext));
    }

    private void initOuterPaintForTextCircle() {
        mOuterPaintForTextCircle.setStyle(Paint.Style.FILL_AND_STROKE);
        int[] gradientColors = new int[]{
                ContextCompat.getColor(mContext, R.color.tahajjud_center_circle_bg),
                ContextCompat.getColor(mContext, R.color.tahajjud_center_shadow_dark),
                ContextCompat.getColor(mContext, R.color.tahajjud_center_circle_bg),
                ContextCompat.getColor(mContext, R.color.tahajjud_center_shadow_light),
                ContextCompat.getColor(mContext, R.color.tahajjud_center_circle_bg)
        };
        SweepGradient gradient = new SweepGradient(mCenter.x, mCenter.y, gradientColors, null);
        mOuterPaintForTextCircle.setShader(gradient);
    }

    private void initInnerPaintForTextCircle() {
        mInnerPaintForTextCircle.setColor(ContextCompat.getColor(mContext, R.color.tahajjud_center_circle_bg));
        mInnerPaintForTextCircle.setStyle(Paint.Style.FILL);
        mInnerPaintForTextCircle.setShadowLayer(3, 0, 0, ContextCompat.getColor(mContext, R.color.tahajjud_center_circle_bg));
    }

    //TODO Change all pixels to DP and move to constants
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        drawIndicatorBackground(canvas);
        //drawTimeIndicator(canvas);
        //drawTimeIndicatorIconBackground(canvas);
        //drawTimeIcon(canvas);
        drawTimeSeparationLine(canvas);
        drawIntervalTime(canvas);
        drawCenterCircleForTime(canvas);
        drawCircleTime(canvas);
        drawBell(canvas);
    }

    private void drawIndicatorBackground(Canvas canvas) {
        canvas.drawCircle(mCenter.x, mCenter.y, mOuterRadius, mIndicatorBackgroundPaint);
    }

    private void drawTimeIndicatorIconBackground(Canvas canvas) {
        float clockRadius = (mOuterRadius - mInnerRadius) / 2;
        canvas.drawCircle(mClockCoords.x, mClockCoords.y, clockRadius, mTimeIndicatorBackgroundPaint);
    }

    private void drawTimeIndicator(Canvas canvas) {
        mIndicatorArcPath.arcTo(mOuterRect, startAngle, 180);
        mIndicatorArcPath.arcTo(mInnerRect, startAngle + 180, -180);
        mIndicatorArcPath.close();

        mIndicatorMatrix.setRotate((float) startAngle - 180, mCenter.x, mCenter.y);
        mIndicatorGradient.setLocalMatrix(mIndicatorMatrix);
        mIndicatorArcPaint.setShader(mIndicatorGradient);

        canvas.drawPath(mIndicatorArcPath, mIndicatorArcPaint);
    }

    private void drawTimeIcon(Canvas canvas) {
        mTimeIconPaint.reset();
        mTimeIconPaint.setColor(ContextCompat.getColor(mContext, R.color.tahajjud_clock_bg));
        float buttonRadius = ((mOuterRadius - mInnerRadius) / 2);
        canvas.drawCircle(mClockCoords.x, mClockCoords.y, buttonRadius - AndroidUtils.convertDpToPixel(4, mContext), mTimeIconPaint);

        mTimeIconPaint.setColor(ContextCompat.getColor(mContext, R.color.tahajjud_sweep_gradient_3));
        mIndicatorIconPath.reset();
        mIndicatorIconPath.moveTo(mClockCoords.x, mClockCoords.y);

        mIndicatorIconPath.addRect(mClockCoords.x - AndroidUtils.convertDpToPixel(1.5f, mContext),
                mClockCoords.y - buttonRadius + AndroidUtils.convertDpToPixel(10, mContext),
                mClockCoords.x + AndroidUtils.convertDpToPixel(1.5f, mContext),
                mClockCoords.y, Path.Direction.CW);
        mIndicatorIconPath.addRect(mClockCoords.x - AndroidUtils.convertDpToPixel(1.5f, mContext),
                mClockCoords.y - AndroidUtils.convertDpToPixel(3, mContext),
                mClockCoords.x + buttonRadius - AndroidUtils.convertDpToPixel(10, mContext),
                mClockCoords.y, Path.Direction.CW);
        canvas.drawPath(mIndicatorIconPath, mTimeIconPaint);
    }

    private void drawCenterCircleForTime(Canvas canvas) {
        canvas.drawCircle(mCenter.x, mCenter.y, mInnerRadius, mOuterPaintForTextCircle);
        canvas.drawCircle(mCenter.x, mCenter.y, mInnerRadius - 10, mInnerPaintForTextCircle);
    }

    private void drawCircleTime(Canvas canvas) {
        Rect textBounds = new Rect();
        mCircleTextPaint.getTextBounds("00", 0, 2, textBounds);

        canvas.drawText(mTahajjudTimeHours,
                mCenter.x - (textBounds.width() / 2),
                mCenter.y - AndroidUtils.convertDpToPixel(4, mContext),
                mCircleTextPaint);
        canvas.drawText(mTahajjudTimeMinutes,
                mCenter.x - (textBounds.width() / 2),
                mCenter.y + textBounds.height() + AndroidUtils.convertDpToPixel(4, mContext),
                mCircleTextPaint);
    }

    private void drawIntervalTime(Canvas canvas) {
        Rect textBounds = new Rect();
        mIntervalTextPaint.getTextBounds("00:00", 0, 5, textBounds);

        float yText = mCenter.y + mOuterRadius +
                AndroidUtils.convertDpToPixel(32, mContext);
        canvas.drawText("01:00",
                mCenter.x - textBounds.width() - AndroidUtils.convertDpToPixel(8, mContext),
                yText, mIntervalTextPaint);
        canvas.drawText("04:00",
                mCenter.x + AndroidUtils.convertDpToPixel(8, mContext),
                yText, mIntervalTextPaint);
    }

    private void drawTimeSeparationLine(Canvas canvas) {
        canvas.drawLine(
                mCenter.x,
                mCenter.y + mOuterRadius,
                mCenter.x,
                mCenter.y + mOuterRadius + AndroidUtils.convertDpToPixel(32, mContext),
                mIntervalTimeSeparatorLinePaint);
    }

    private void drawBell(Canvas canvas) {
        Resources res = getResources();
        Bitmap bitmap = BitmapFactory.decodeResource(res, R.drawable.ic_bell);
        canvas.drawBitmap(bitmap, mBellCoords.x - bitmap.getWidth() / 2,
                mBellCoords.y - bitmap.getHeight() / 2, null);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Point currentPos = new Point();
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            currentPos.x = (int) event.getX();
            currentPos.y = (int) event.getY();
        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            currentPos.x = (int) event.getX();
            currentPos.y = (int) event.getY();
            if (isBellMoving || isTouchedInsideClockIndicator(currentPos)) {
                isBellMoving = true;

                double currentAngle = -(Math.atan2(mCenter.y - currentPos.y, +currentPos.x - mCenter.x));
                mBellCoords.x = (int) (mCenter.x + mClockRadius * Math.cos(currentAngle));
                mBellCoords.y = (int) (mCenter.y + mClockRadius * Math.sin(currentAngle));

                currentAngle -= Math.PI / 2;
                if (currentAngle < 0) {
                    currentAngle = (2 * Math.PI) - Math.abs(currentAngle);
                }
                time = (long) ((currentAngle) / mDivision) * 60 * 1000;
                String[] timeStr = mSimpleDateFormat.format(time).split(":");
                mTahajjudTimeHours = timeStr[0];
                mTahajjudTimeMinutes = timeStr[1];


                // Needed to calculate time indicator coordinates
                /*currentAngle = -(Math.atan2(mCenter.y - currentPos.y, +currentPos.x - mCenter.x));
                startAngle = (int) Math.toDegrees(Math.PI + currentAngle);
                Log.d("LOG_TAG", "angle : " + currentAngle + " ||  " + startAngle);
                mClockCoords.x = (int) (mCenter.x + mClockRadius * Math.cos(currentAngle));
                mClockCoords.y = (int) (mCenter.y + mClockRadius * Math.sin(currentAngle));*/
                invalidate();
            }
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            isBellMoving = false;
        }
        return true;
    }

    private boolean isTouchedInsideClockIndicator(Point touchedPos) {
        return Math.pow((mBellCoords.x - touchedPos.x), 2) +
                Math.pow((mBellCoords.y - touchedPos.y), 2) <
                Math.pow(((mOuterRadius - mInnerRadius) / 2), 2);
    }

    public void setInterval(long interval) {
        this.mInterval = interval;
    }
}
