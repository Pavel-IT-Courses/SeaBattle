package com.gmail.pavkascool.seabattle;

import android.content.ClipData;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

public class CellView extends View implements View.OnTouchListener {

    private int rows;
    private int cols;
    private int cellSize;
    private int locationRow;
    private int locationCol;
    int vertical;
    private float touchX;
    private float touchY;


    public CellView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setOnTouchListener(this);
        TypedArray ar = context.obtainStyledAttributes(attrs, R.styleable.CellView, 0, 0);
        cols = ar.getInt(R.styleable.CellView_cols, 1);
        rows = ar.getInt(R.styleable.CellView_rows, 1);
        locationCol = ar.getInt(R.styleable.CellView_location_col, 0);
        locationRow = ar.getInt(R.styleable.CellView_location_row, 0);
        vertical = ar.getInt(R.styleable.CellView_vertical, 0);
        paint = new Paint();
    }

    public float getTouchX() {
        return touchX;
    }

    public float getTouchY() {
        return touchY;
    }

    private Paint paint;

    public void setLocationRow(int locationRow) {
        this.locationRow = locationRow;
    }

    public void setLocationCol(int locationCol) {
        this.locationCol = locationCol;
    }

    public int getLocationRow() {
        return locationRow;
    }

    public int getLocationCol() {
        return locationCol;
    }

    public int getCellSize() {
        return cellSize;
    }

    public void setCellSize(int cellSize) {
        this.cellSize = cellSize;
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        this.rows = rows;
    }

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
        paint.setColor(Color.GRAY);

        cellSize = Math.min((getWidth() / cols), getHeight() / rows);

        canvas.drawRect(0, 0, cellSize * cols, cellSize * rows, paint);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(1);
        canvas.drawRect(0, 0, cellSize * cols, cellSize * rows, paint);
        for(int i = 1; i < rows; i++) {
            canvas.drawLine(0, cellSize * i, cellSize * cols, cellSize * i,  paint);
        }
        for(int i = 1; i < cols; i++) {
            canvas.drawLine(cellSize * i, 0, cellSize * i, cellSize * rows, paint);
        }

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            touchX = event.getX();
            touchY = event.getY();
            ClipData data = ClipData.newPlainText("", "");
            DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(
                    v);
            v.startDrag(data, shadowBuilder, v, 0);
            v.setVisibility(View.INVISIBLE);
            Log.d("MyFavoriteTag", "MOTION STARTED");
            return true;
        } else {
            Log.d("MyFavoriteTag", "MOTION DID NOT START");
            return true;
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int finalWidth = cols * cellSize;
        int finalHeight = rows * cellSize;

        super.onMeasure(
                MeasureSpec.makeMeasureSpec(finalWidth, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(finalHeight, MeasureSpec.EXACTLY));
    }

    public void rotate() {
        if(cols > rows) {
            switch(cols) {
                case 3:
                    locationCol++;
                    locationRow--;
                    int temp = cols;
                    cols = rows;
                    rows = temp;
                    break;

            }
        }
        else if(rows > cols){
            switch(rows) {
                case 3:
                    locationCol--;
                    locationRow++;
                    int temp = cols;
                    cols = rows;
                    rows = temp;
                    break;

            }
        }
    }

}
