package com.gmail.pavkascool.seabattle;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

public class CellView extends View implements View.OnTouchListener {
    public CellView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setOnTouchListener(this);
        TypedArray ar = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CellView, 0, 0);
        cols = ar.getInt(R.styleable.CellView_cols, 1);
        rows = ar.getInt(R.styleable.CellView_rows, 1);
        paint = new Paint();
    }



    private int rows;
    private int cols;

    private Paint paint;

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        this.rows = rows;
    }

    private float size;
    private float stepV, stepH;

    @Override
    public int getId() {
        return super.getId();
    }


    public int getCols() {
        return cols;
    }

    public void setCols(int cols) {
        this.cols = cols;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int minDim = Math.min(widthMeasureSpec, heightMeasureSpec);
        super.onMeasure(minDim, minDim);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.WHITE);
        size = Math.min(getWidth() - getPaddingLeft() - getPaddingRight(), getHeight() - getPaddingTop() - getPaddingBottom());
        int left = getPaddingLeft();
        int top = getPaddingTop();
        stepV = size / rows;
        stepH = size / cols;
        canvas.drawRect(left, top, left + size, top + size, paint);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(1);
        for(int i = 1; i < rows; i++) {
            canvas.drawLine(left, top + stepV * i, left + size, top + stepV * i, paint);
        }
        for(int i = 1; i < cols; i++) {
            canvas.drawLine(left + stepH * i, top, left + stepH * i, top + size, paint);
        }
        canvas.drawRect(left, top, left + size, top + size, paint);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_DOWN) {
            float x = event.getX();
            float y = event.getY();

            int r = (int)(y / stepV);
            int c = (int)(x / stepH);

            System.out.println("Row = " + r + " Column = " + c );
            System.out.println("ID = " + getId());
        }
        return true;
    }
}
