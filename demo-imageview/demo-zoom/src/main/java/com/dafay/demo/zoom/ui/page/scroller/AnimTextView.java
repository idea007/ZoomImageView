package com.dafay.demo.zoom.ui.page.scroller;

import android.content.Context;
import android.graphics.Canvas;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatTextView;

import java.util.ArrayList;

/**
 * Created by idea on 2017/2/8.
 */

public class AnimTextView extends AppCompatTextView {

    private float mW;//单列的宽
    private int mLayoutH;//StaticLayout高
    private float mSpacingmult = 1.0f;//StaticLayout行间距的倍数1.0为正常值


    private ArrayList<ColumnAttribute> columnAttributes = new ArrayList<>();

    public AnimTextView(Context context) {
        super(context);
    }

    public AnimTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AnimTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * 开启新数据替换老数据的动画
     *
     * @param oldNum 老数据
     * @param newNum 新数据
     */
    public void setText(String oldNum, String newNum) {

        if (TextUtils.isEmpty(oldNum) || TextUtils.isEmpty(newNum)) {
            return;
        }
        this.oldNum = oldNum;
        this.newNum = newNum;
        clearList();
        makeUnequalData(newNum, oldNum);
        super.setText(this.newNum);
    }

    private ColumnAttribute columnAttribute;
    private String str;

    private void makeUnequalData(String newNum, String oldNum) {


        if(oldNum.length()>newNum.length()) {
            int total=newNum.length()>oldNum.length()?newNum.length():oldNum.length();
            StringBuilder stringBuilder=new StringBuilder();
            if (total > newNum.length()) {
                for (int i = 0; i < total; i++) {
                    if (i < newNum.length()) {
                        stringBuilder.append(newNum.charAt(i));
                    } else {
                        stringBuilder.append('0');
                    }
                }
                this.newNum = stringBuilder.reverse().toString();
            }
        }


        StringBuilder sb = new StringBuilder();
        int l1 = oldNum.length() - 1;
        int l2 = newNum.length() - 1;


        char first;   //old
        char second;   //new

        for (; l1 > -1 || l2 > -1; --l1, --l2) {
            sb.setLength(0);

            first = l1 > -1 ? oldNum.charAt(l1) : ' ';
            second = l2 > -1 ? newNum.charAt(l2) : ' ';

            columnAttribute=new ColumnAttribute();

            if (first > second) {
                str = sb.append(second).append("\n").append(first).toString();
                columnAttribute.state=-1;
                columnAttribute.content=str;

//                columnAttribute.startValue = -mLayoutH / 2;
//                columnAttribute.durValue = mLayoutH / 2;


            } else if(first<second){
                str = sb.append(first).append("\n").append(second).toString();
                columnAttribute.state=1;
                columnAttribute.content=str;

//                columnAttribute.startValue = 0;
//                columnAttribute.durValue = -mLayoutH / 2;
            }else{
                str = sb.append(first).append("\n").append(second).toString();
                columnAttribute.state=0;
                columnAttribute.content=str;
            }

            columnAttributes.add(columnAttribute);

        }


    }

    private void clearList() {
//        mStrList.clear();
//        mScrList.clear();
//        mLayoutList.clear();
//        index = 0;
        currentValue = 0;
        columnAttributes.clear();
    }

    private String mLast = null;

//    private int index = 0;
    private String oldNum, newNum;

    private int timer = 3;                 //间隔值
    private int currentValue;


    @Override
    protected void onDraw(final Canvas canvas) {
        CharSequence str = getText();
        if (str == null) return;
        if (str != mLast) {
            mLast = str.toString();
            startAnim();
            postInvalidate();
            return;
        }

        if (columnAttributes.size() == 0) {
            super.onDraw(canvas);
            return;
        }

        currentValue += timer;


        try {
            boolean invalidate = false;

            for (int i = 0;i<columnAttributes.size();i++) {
                canvas.save();
                canvas.translate((columnAttributes.size()-i-1) * 3 * mW/ 4, 0);
                final ColumnAttribute columnAttribute = columnAttributes.get(i);

                int value = currentValue - columnAttribute.thresholdValue;

                if (columnAttribute.state == 0) {
                    StaticLayout layout = columnAttribute.mStaticLayout;
                    if (layout != null) layout.draw(canvas);
                } else {
                    if (value > 0) {
                        if (columnAttribute.state == 1) {
                            if (value <= mLayoutH / 2) {
                                canvas.translate(0, -value);
                            } else {
                                canvas.translate(0, -mLayoutH / 2+4);
                            }
                        } else if (columnAttribute.state == -1) {
                            if (value <= mLayoutH / 2) {
                                canvas.translate(0, -mLayoutH / 2 + value);
                            } else {
                                canvas.translate(0, 0);
                            }
                        }

                    }else{
                        if(columnAttribute.state==-1) {
                            canvas.translate(0, -mLayoutH / 2 + 4);
                        }
                    }

                    StaticLayout layout = columnAttribute.mStaticLayout;
                    if (layout != null) layout.draw(canvas);

                }

                invalidate = true;

                if (i == columnAttributes.size()-1&& currentValue - columnAttribute.thresholdValue > mLayoutH / 2) {
                    invalidate = false;
                }

                canvas.restore();

            }

            if (invalidate) postInvalidate();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private void startAnim() {
        if (columnAttributes.size() == 0) return;

        float x = 1 + (columnAttributes.size() * 0.06f < 0.1 ? 0 : columnAttributes.size() * 0.06f);
        x = x > 1.30f ? 1.30f : x;
        mW = (float) ((getWidth() / columnAttributes.size()) * x);
        mLayoutH = 0;
        TextPaint p = getPaint();
        p.setColor(getCurrentTextColor());

        int j=0;
        for (int i = 0; i < columnAttributes.size(); i++) {
            if (!TextUtils.isEmpty(columnAttributes.get(i).content)) {
                StaticLayout layout = new StaticLayout(columnAttributes.get(i).content, p, (int) mW, Layout.Alignment.ALIGN_CENTER, mSpacingmult, 0.0F, true);

                mLayoutH = layout.getHeight();

                if(columnAttributes.get(i).state==1){
                    columnAttributes.get(i).startValue = 0;
                    columnAttributes.get(i).durValue = -mLayoutH / 2;
                    columnAttributes.get(i).thresholdValue=mLayoutH*j/2;
                    j++;
                }else if(columnAttributes.get(i).state==-1){
                    columnAttributes.get(i).startValue = -mLayoutH / 2;
                    columnAttributes.get(i).durValue = mLayoutH / 2;
                    columnAttributes.get(i).thresholdValue = mLayoutH * j / 2;
                    j++;
                }else{
                    columnAttributes.get(i).thresholdValue = mLayoutH * j / 2;
                    j++;
                }

                columnAttributes.get(i).sortNum = i;
                columnAttributes.get(i).mStaticLayout = layout;

            }

        }

    }


    public class ColumnAttribute {

        String content;
        StaticLayout mStaticLayout;//绘制String的Layout

        int state; //1是增加 0是相等 -1是减少
        int startValue;  //起始值
        int durValue;     //持续值
        int thresholdValue;  //阀值
        int sortNum;
    }

}
