package com.dafay.demo.zoom.ui.page.interpolator;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.View;

import com.dafay.demo.lib.base.utils.DpExtKt;

/**
 * TODO tagbyli
 * < a herf="https://github.com/GcsSloop/AndroidNote/blob/master/CustomView/Advance/%5B07%5DPath_Over.md"></>
 */
public class CanvasGridBg1View extends View {

    // 画笔
    private Paint mPaint;        //画笔


    // View 宽高
    private int mViewWidth;
    private int mViewHeight;
    private int mCenterX, mCenterY;


    int redColor = Color.parseColor("#ff2d55");


    public CanvasGridBg1View(Context context) {
        this(context, null);
    }

    public CanvasGridBg1View(Context context,  AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CanvasGridBg1View(Context context,  AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();

    }

    private void init() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(redColor);
        mPaint.setAntiAlias(true);
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mViewWidth = w;
        mViewHeight = h;
        mCenterX = mViewWidth / 2;
        mCenterY = mViewHeight / 2;

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawCoordinateSystem(canvas);       // 绘制坐标系
        drawCenterPoint(canvas);   //绘制 中心点
    }


    private void drawCenterPoint(Canvas canvas) {

        canvas.drawCircle(mCenterX, mCenterY, 3, mPaint);


        Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(Color.parseColor("#50ff2d55"));
        textPaint.setTextSize(30);
        textPaint.setTextAlign(Paint.Align.CENTER);

        String text = String.format("(%d,%d)", mCenterX, mCenterY);
        canvas.drawText(text, mCenterX + 70, mCenterY + 20, textPaint);


    }

    // 绘制坐标系
    private void drawCoordinateSystem(Canvas canvas) {


        int redColor = Color.parseColor("#ff2d55");
        int lightRedColor = Color.parseColor("#50ff2d55");

        Paint fuzhuPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        fuzhuPaint.setColor(redColor);
        fuzhuPaint.setStrokeWidth(6);
        fuzhuPaint.setStyle(Paint.Style.STROKE);

        canvas.drawLine(0, 0, mViewWidth, 0, fuzhuPaint);
        canvas.drawLine(0, 0, 0, mViewHeight, fuzhuPaint);


        fuzhuPaint.setColor(lightRedColor);
        fuzhuPaint.setStrokeWidth(1);

        int unit = DpExtKt.getDp2px( 10);
        for (int i = unit; i < mViewWidth; i += unit) {
            canvas.drawLine(i, 0, i, mViewHeight, fuzhuPaint);
        }

        for (int j = unit; j < mViewHeight; j += unit) {
            canvas.drawLine(0, j, mViewWidth, j, fuzhuPaint);
        }


        float radius = 30f;
        double angle = 150;


        PointF pointF10 = new PointF(mViewWidth, 0);

        PointF pointF11 = new PointF();
        pointF11.x = (float) (pointF10.x + Math.cos(Math.toRadians(angle)) * radius);
        pointF11.y = (float) (pointF10.y + Math.sin(Math.toRadians(angle)) * radius);


        fuzhuPaint.setStrokeWidth(3);
        fuzhuPaint.setColor(redColor);
        canvas.drawLine(pointF10.x, pointF10.y, pointF11.x, pointF11.y, fuzhuPaint);


        PointF pointF20 = new PointF(0, mViewHeight);

        angle = 300;

        PointF pointF21 = new PointF();
        pointF21.x = (float) (pointF20.x + Math.cos(Math.toRadians(angle)) * radius);
        pointF21.y = (float) (pointF20.y + Math.sin(Math.toRadians(angle)) * radius);


        fuzhuPaint.setStrokeWidth(3);
        fuzhuPaint.setColor(redColor);
        canvas.drawLine(pointF20.x, pointF20.y, pointF21.x, pointF21.y, fuzhuPaint);


        Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(redColor);
        textPaint.setTextSize(40);
        textPaint.setTextAlign(Paint.Align.CENTER);

        canvas.drawText("x", pointF11.x, pointF11.y + 30, textPaint);
        canvas.drawText("y", pointF21.x + 30, pointF21.y, textPaint);


    }


}
