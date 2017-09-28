package com.mo.progress;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;


/**
 * 模仿 android 6.0 的 Material 风格的 ProgressBar <br/>
 * Created by motw on 2017/9/25.
 * @author motw
 */
public class MaterialProgressBar extends View {

    private Paint barPaint;
    private RectF mRectF;

    private float startAngle;
    private float sweepAngle;
    private int startAngleBegin = 0;
    private int sweepAngleBegin = 20;
    /** 增加圆弧的最大值 */
    private final int increaseArc = 270;
    /** 每次增加的大小 */
    private final int add = 4;

    // View默认大小
    private int mDefaultWith;
    private int mDefaultHeight;
    private int strokeWith = 10;

    // 旋转动画
    private int mRotate;
    // 旋转的中心
    private int centerX;
    private int centerY;

    // 状态标志
    private int FLAG;
    /** 圆弧增加阶段 */
    private final int FLAG_ADD = 0x01;
    /** 圆弧减少阶段 */
    private final int FLAG_DECREASE = 0x02;

    public MaterialProgressBar(Context context) {
        this(context, null);
    }

    public MaterialProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MaterialProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mDefaultWith = getResources().getDimensionPixelSize(R.dimen.progressWith);
        mDefaultHeight = getResources().getDimensionPixelSize(R.dimen.progressHeight);

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.MaterialProgressBar);
        int color = ta.getColor(R.styleable.MaterialProgressBar_color, getResources().getColor(R.color.colorAccent));
        ta.recycle();

        barPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        barPaint.setColor(color);
        barPaint.setStyle(Paint.Style.STROKE);
        barPaint.setStrokeWidth(strokeWith);

        mRectF = new RectF();
        sweepAngle = sweepAngleBegin;
        FLAG = FLAG_ADD;
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        mDefaultWith += (getPaddingLeft() + getPaddingRight());
        mDefaultHeight += (getPaddingTop() + getPaddingBottom());
//        setMeasuredDimension(resolveSize(mDefaultWith, widthMeasureSpec), resolveSize(mDefaultHeight, heightMeasureSpec));
        setMeasuredDimension(getSpecSize(widthMeasureSpec, mDefaultWith), getSpecSize(heightMeasureSpec, mDefaultHeight));
    }

    /**
     *
     * @param measureSpec 测量的大小
     * @param size 默认大小
     * @return 返回实际使用大小
     */
    private int getSpecSize(int measureSpec, int size){
        final int result;
        final int mode = MeasureSpec.getMode(measureSpec);
        final int specSize = MeasureSpec.getSize(measureSpec);
        switch (mode){
            case MeasureSpec.AT_MOST:
                if (specSize < size) {
                    result = specSize;
                } else {
                    result = size;
                }
                break;
            case MeasureSpec.EXACTLY:
                result = specSize;
                break;
            case MeasureSpec.UNSPECIFIED:
            default:
                result = size;
        }

        return result;
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        mRectF.left = strokeWith;
        mRectF.top = strokeWith;
        mRectF.right = w - strokeWith;
        mRectF.bottom = h - strokeWith;

        centerX = w / 2;
        centerY = h / 2;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        /*
         旋转
         */
        canvas.rotate(mRotate, centerX, centerY);
        mRotate = mRotate + 5;
        if (mRotate >= 360){
            mRotate = 0;
        }

        /*
        圆弧增加 ，减少
         */
        if (FLAG == FLAG_ADD){
            //increase
            canvas.drawArc(mRectF, startAngle, sweepAngle, false, barPaint);

            sweepAngle = sweepAngle + add;
            if (sweepAngle > (increaseArc + sweepAngleBegin)){
                FLAG = FLAG_DECREASE;
            }
            invalidate();

        }else if (FLAG == FLAG_DECREASE){
            //decrease
            canvas.drawArc(mRectF, startAngle, sweepAngle, false, barPaint);
            startAngle = startAngle + add;
            sweepAngle = sweepAngle - add;

            //  startAngle 是 add 的倍数, i 也是 add 的倍数
            int i = getNext(increaseArc, add);
            if ((startAngle % i) == 0){
                // turn to increase
                FLAG = FLAG_ADD;
                sweepAngle = sweepAngleBegin;
            }
            invalidate();
        }

    }

    /**
     * 获取下一个比 dividend 大的 能整除 divisor 的值
     */
    private int getNext(int dividend, int divisor){
        int i = dividend % divisor;
        int y = (dividend - i) / divisor;
        return (y+1) *divisor;
    }
}
