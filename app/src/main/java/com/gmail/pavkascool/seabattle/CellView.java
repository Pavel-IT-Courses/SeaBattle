package com.gmail.pavkascool.seabattle;

import android.content.ClipData;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import androidx.annotation.Nullable;

public class CellView extends View implements View.OnTouchListener {

    private int rows;
    private int cols;
    private int cellSize;
    private int locationRow;
    private int locationCol;
    private int vertical;
    private int decks;
    private float touchX;
    private float touchY;
    private boolean isDrowned;



    public CellView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        setOnTouchListener(this);
        TypedArray ar = context.obtainStyledAttributes(attrs, R.styleable.CellView, 0, 0);
        cols = ar.getInt(R.styleable.CellView_cols, 1);
        rows = ar.getInt(R.styleable.CellView_rows, 1);
        locationCol = ar.getInt(R.styleable.CellView_location_col, 0);
        locationRow = ar.getInt(R.styleable.CellView_location_row, 0);
        vertical = ar.getInt(R.styleable.CellView_vertical, 0);
        decks = Math.max(cols, rows);
        System.out.println("DECKS = " + decks);
        paint = new Paint();
    }

    public void setOrientation(int length, int vertical) {
        if(vertical == 0) {
            cols = length;
            rows = 1;
        }
        else {
            cols = 1;
            rows = length;
        }
    }

    public int getDecks() {
        return decks;
    }

    public void setDecks(int decks) {
        this.decks = decks;
    }

    public void damage() {
        if(--decks == 0) isDrowned = true;
        else isDrowned = false;
    }
    public boolean isDrowned() {
        return isDrowned;
    }

    public void setDrowned(boolean drowned) {
        isDrowned = drowned;
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

    public int getLength() { return cols + rows - 1; }

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
            DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(v);
            v.startDrag(data, shadowBuilder, v, 0);

            v.setVisibility(View.INVISIBLE);
            return true;
        } else {
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
        int temp;
        if(cols > rows) {

            switch(cols) {
                case 2:
                    temp = cols;
                    cols = rows;
                    rows = temp;
                    break;

                case 3:
                    locationCol++;
                    locationRow--;
                    temp = cols;
                    cols = rows;
                    rows = temp;
                    break;

                case 4:
                    locationCol++;
                    locationRow--;
                    temp = cols;
                    cols = rows;
                    rows = temp;
                    break;

            }
        }
        else if(rows > cols){
            switch(rows) {
                case 2:
                    locationCol--;
                    temp = cols;
                    cols = rows;
                    rows = temp;
                    break;
                case 3:
                    locationCol--;
                    locationRow++;
                    temp = cols;
                    cols = rows;
                    rows = temp;
                    break;
                case 4:
                    locationCol -= 2;
                    locationRow++;
                    temp = cols;
                    cols = rows;
                    rows = temp;
                    break;

            }
        }
    }

    public void rotateBack() {
        int temp;
        switch(getLength()) {
            case 2:
                if(cols > rows) {
                    locationCol++;
                    temp = cols;
                    cols = rows;
                    rows = temp;
                }
                else {
                    temp = cols;
                    cols = rows;
                    rows = temp;
                }
                break;
            case 3:
            case 1:
                rotate();
                break;
            case 4:
                if(cols > rows) {
                    locationCol += 2;
                    locationRow--;
                    temp = cols;
                    cols = rows;
                    rows = temp;
                }
                else {
                    locationCol--;
                    locationRow++;
                    temp = cols;
                    cols = rows;
                    rows = temp;
                }

        }
    }

    public List<Coordinates> getCoordinates() {
        List<Coordinates> coordintates = new ArrayList<Coordinates>();
        for(int i = getLocationRow(); i < getLocationRow() + getRows(); i++) {
            for(int j = getLocationCol(); j < getLocationCol() + getCols(); j++) {
                coordintates.add(new Coordinates(i, j));
            }
        }
        return coordintates;
    }

    public Set<Coordinates> getPrihibitedZone() {
        Set<Coordinates> prohibitedZone = new HashSet<Coordinates>();
        for(Coordinates coords: getCoordinates()) {
            prohibitedZone.addAll(coords.getZone());
        }
        return prohibitedZone;
    }

}
