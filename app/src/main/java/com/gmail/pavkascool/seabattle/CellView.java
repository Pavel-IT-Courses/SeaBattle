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

    private int step;

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
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.WHITE);

        step = Math.min((getWidth() / cols), getHeight() / rows);
        canvas.drawRect(0, 0, step * cols, step * rows, paint);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(1);
        canvas.drawRect(0, 0, step * cols, step * rows, paint);
        for(int i = 1; i < rows; i++) {
            canvas.drawLine(0, step * i, step * cols, step * i,  paint);
        }
        for(int i = 1; i < cols; i++) {
            canvas.drawLine(step * i, 0, step * i, step * rows, paint);
        }

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_DOWN) {
            float x = event.getX();
            float y = event.getY();

            int r = (int)(y / step);
            int c = (int)(x / step);

            System.out.println("Row = " + r + " Column = " + c );
            System.out.println("ID = " + getId());
        }
        return true;
    }
}
