package com.smapley.education.chart;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;

public class PathView extends View {


    public String[] days_month = {"1", "2", "3", "4", "5", "6", "7", "8", "9",
            "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20",
            "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31"};
    public ArrayList<Float> xPoint = new ArrayList<Float>();


    // x,y轴的线条数量
    private int xLineCount = 10;
    private int yLineCount = 10;
    private Paint paintLine, paintPoint, textPaint, linkPaint, numPaint;
    private int[] data;

    // 靠左侧，底部的距离
    private float left;
    private float bottom;

    // x,y轴上显示的值
    private float xMaxValue, xMinValue;
    // 间距
    private float xInterval, yInterval;

    public PathView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public PathView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public PathView(Context context) {
        super(context);
        init(context);
    }


    private void init(Context cont) {
        paintLine = new Paint();
        paintLine.setAntiAlias(true);
        paintLine.setColor(Color.WHITE);
        paintLine.setFakeBoldText(true);
        paintLine.setStrokeWidth(1.5f);

        paintPoint = new Paint();
        paintPoint.setColor(Color.RED);
        paintPoint.setFakeBoldText(true);
        paintPoint.setStrokeWidth(20);

        textPaint = new Paint();
        textPaint.setColor(Color.BLACK);
        textPaint.setFakeBoldText(true);
        textPaint.setTextSize(40);
        textPaint.setTypeface(Typeface.DEFAULT);

        numPaint = new Paint();
        numPaint.setColor(Color.RED);
        numPaint.setFakeBoldText(true);
        numPaint.setTextSize(40);

        linkPaint = new Paint();
        linkPaint.setColor(Color.RED);
        linkPaint.setFakeBoldText(true);
        linkPaint.setTextSize(25);

    }

    public void setTextSize(int size) {
        textPaint.setTextSize(size);
    }

    public void setDate(int[] x) {
        data = x;
        invalidate();
    }

    private void calculateLeft() {
        for (int value : data) {
            float tempLeft = textPaint.measureText(value + "");
            if (tempLeft > left) {
                left = tempLeft;
            }
        }
        bottom = textPaint.getFontMetrics().descent
                - textPaint.getFontMetrics().ascent;


        yLineCount = data.length + 1;

        if (xInterval == 0)
            xInterval = (this.getHeight() - bottom) / (xLineCount + 1);


        if (yInterval == 0)
            yInterval = (this.getWidth() - left) / yLineCount;

    }

    public void setXCount(int maxValue, int minValue) {
        xLineCount = maxValue - minValue;
        xMaxValue = maxValue;
        xMinValue = minValue;
    }

    /**
     * 绘制曲线
     *
     * @param canvas
     */
    private void doDraw(Canvas canvas) {
        float sumHeight = xLineCount * xInterval;
        float tempInterval = (xMaxValue - xMinValue) / sumHeight;
        for (int i = 0; i < data.length; i++) {
            float x = xPoint.get(i);
            float yPotion = sumHeight - (xMaxValue - data[i]) / tempInterval + xInterval;
            canvas.drawCircle(x, yPotion, 5, paintPoint);
            canvas.drawText(data[i] + "", x + 30, yPotion - 30, numPaint);
            if (i != data.length - 1) {
                float nextYPotion = sumHeight - (xMaxValue - data[i + 1]) / tempInterval
                        + xInterval;
                canvas.drawLine(x, yPotion, xPoint.get(i + 1), nextYPotion,
                        linkPaint);
            }
        }
    }

    /**
     * 绘制连框
     */
    private void drawFrame(Canvas canvas) {
        calculateLeft();

        // 绘制横线
        for (int i = 0; i <= xLineCount; i++) {
            float startY = i * xInterval + xInterval;
            canvas.drawLine(left + 5, startY, getWidth(), startY, paintLine);
            textPaint.setTextAlign(Align.RIGHT);
            canvas.drawText(
                    Math.round(xMinValue + i - 0.5)
                            + "", left, startY + bottom / 4, textPaint);
        }

        for (int j = 0; j < yLineCount; j++) {

            float leftSpace = yInterval * j + left + 5;

            canvas.drawLine(leftSpace, bottom, leftSpace, this.getHeight()
                    - bottom, paintLine);
            textPaint.setTextAlign(Align.CENTER);

            if (j == 0) {
                continue;
            }
            xPoint.add(leftSpace);
            canvas.drawText(days_month[j - 1], leftSpace, this.getHeight(),
                    textPaint);

        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawFrame(canvas);
        doDraw(canvas);
        super.onDraw(canvas);
    }

}
