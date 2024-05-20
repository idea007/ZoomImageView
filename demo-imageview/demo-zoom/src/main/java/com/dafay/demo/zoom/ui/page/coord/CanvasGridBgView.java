package com.dafay.demo.zoom.ui.page.coord;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.View;

public class CanvasGridBgView extends View {

    // 画笔
    private Paint mPaint;        //画笔


    // View 宽高
    private int mViewWidth;
    private int mViewHeight;
    private int mCenterX, mCenterY;


    int redColor = Color.parseColor("#ff2d55");


    public CanvasGridBgView(Context context) {
        this(context, null);
    }

    public CanvasGridBgView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CanvasGridBgView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();

    }

    private void init() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(10);
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

    //TODO tagbyli 这中形式角度 和文字 都经过 x轴翻转 ，很不方便

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawCoordinateSystem(canvas);       // 绘制坐标系

        canvas.translate(mCenterX, mCenterY); // 将坐标系移动到画布中央
        canvas.scale(1, -1);                 // 翻转Y轴


//        canvas.drawArc(0,0,100,100,0,45,true,mPaint);


        canvas.drawPoint(100, 100, mPaint);


    }

    // 绘制坐标系
    private void drawCoordinateSystem(Canvas canvas) {
        canvas.save();                      // 绘制做坐标系

        canvas.translate(mCenterX, mCenterY); // 将坐标系移动到画布中央
        canvas.scale(1, -1);                 // 翻转Y轴

        int redColor = Color.parseColor("#ff2d55");

        Paint fuzhuPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        fuzhuPaint.setColor(redColor);
        fuzhuPaint.setStrokeWidth(2);
        fuzhuPaint.setStyle(Paint.Style.STROKE);

        canvas.drawLine(0, -mCenterY, 0, mCenterY, fuzhuPaint);
        canvas.drawLine(-mCenterX, 0, mCenterX, 0, fuzhuPaint);


        float radius = 30f;
        double angle = 150;


        PointF pointF10 = new PointF(mCenterX, 0);

        PointF pointF11 = new PointF();
        pointF11.x = (float) (mCenterX + Math.cos(Math.toRadians(angle)) * radius);
        pointF11.y = (float) (0 + Math.sin(Math.toRadians(angle)) * radius);

        angle = 210;
        PointF pointF12 = new PointF();
        pointF12.x = (float) (mCenterX + Math.cos(Math.toRadians(angle)) * radius);
        pointF12.y = (float) (0 + Math.sin(Math.toRadians(angle)) * radius);


        canvas.drawLine(pointF10.x, pointF10.y, pointF11.x, pointF11.y, fuzhuPaint);
        canvas.drawLine(pointF10.x, pointF10.y, pointF12.x, pointF12.y, fuzhuPaint);


        PointF pointF20 = new PointF(0, mCenterY);

        angle = 300;

        PointF pointF21 = new PointF();
        pointF21.x = (float) (0 + Math.cos(Math.toRadians(angle)) * radius);
        pointF21.y = (float) (mCenterY + Math.sin(Math.toRadians(angle)) * radius);

        angle = 240;
        PointF pointF22 = new PointF();
        pointF22.x = (float) (0 + Math.cos(Math.toRadians(angle)) * radius);
        pointF22.y = (float) (mCenterY + Math.sin(Math.toRadians(angle)) * radius);


        canvas.drawLine(pointF20.x, pointF20.y, pointF21.x, pointF21.y, fuzhuPaint);
        canvas.drawLine(pointF20.x, pointF20.y, pointF22.x, pointF22.y, fuzhuPaint);


        Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(redColor);
        textPaint.setTextSize(40);
        textPaint.setTextAlign(Paint.Align.CENTER);

        canvas.drawText("x", pointF12.x, pointF12.y - 10, textPaint);

        canvas.drawText("y", pointF22.x + 50, pointF22.y, textPaint);


        canvas.restore();
    }


}
