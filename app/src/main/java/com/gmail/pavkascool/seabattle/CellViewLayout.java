package com.gmail.pavkascool.seabattle;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class CellViewLayout extends ViewGroup implements View.OnTouchListener, View.OnDragListener {

    public CellViewLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setOnTouchListener(this);
        TypedArray ar = context.obtainStyledAttributes(attrs, R.styleable.CellViewLayout, 0, 0);
        cols = ar.getInt(R.styleable.CellViewLayout_cvl_cols, 1);
        rows = ar.getInt(R.styleable.CellViewLayout_cvl_rows, 1);
        paint = new Paint();
        setWillNotDraw(false);
        setOnDragListener(this);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        System.out.println("ON LAYOUT");
        for(int i = 0; i < getChildCount(); i++) {
            CellView cv = (CellView)getChildAt(i);
            cv.layout(cellSize * cv.getLocationCol(), cellSize * cv.getLocationRow(),
                    cellSize * (cv.getLocationCol() + cv.getCols()), cellSize * (cv.getLocationRow() + cv.getRows()));
            System.out.println("Rows = " + cv.getRows() + " Cell Size = " + cv.getCellSize());
        }
    }


    private int rows;
    private int cols;
    private Paint paint;
    private int cellSize;
    public final String TAG = "MyFavoriteTag";

    public int getCellSize() {
        return cellSize;
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
        paint.setColor(Color.BLUE);

        //cellSize = Math.min((getWidth() / cols), getHeight() / rows);
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
        Log.d(TAG, "DRAWING..." + getId());
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_DOWN) {
            float x = event.getX();
            float y = event.getY();

            int r = (int)(y / cellSize);
            int c = (int)(x / cellSize);
            if (r < rows && c < cols) {
                System.out.println("Row = " + r + " Column = " + c);
                System.out.println("ID = " + getId());
            }
        }
        return true;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int originalWidth = MeasureSpec.getSize(widthMeasureSpec);

        int originalHeight = MeasureSpec.getSize(heightMeasureSpec);

        int calculatedHeight = originalWidth * rows / cols;

        int finalWidth, finalHeight;

        if (calculatedHeight > originalHeight)
        {
            finalWidth = originalHeight * cols / rows;
            finalHeight = originalHeight;
        }
        else
        {
            finalWidth = originalWidth;
            finalHeight = calculatedHeight;
        }

        cellSize = finalWidth / cols;

        super.onMeasure(
                MeasureSpec.makeMeasureSpec(finalWidth, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(finalHeight, MeasureSpec.EXACTLY));
    }

    @Override
    public boolean onDrag(View v, DragEvent event) {
        Log.d(TAG, "ON DRAG");
        switch (event.getAction()) {
            case DragEvent.ACTION_DRAG_STARTED:
                // do nothing
                Log.d(TAG, "STARTED");
                return true;
            case DragEvent.ACTION_DRAG_EXITED:
                //v.setBackgroundDrawable(normalShape);
                Log.d(TAG, "EXITED");
                break;
            case DragEvent.ACTION_DROP:
                // Dropped, reassign View to ViewGroup
                CellView view = (CellView) event.getLocalState();
                CellViewLayout owner = (CellViewLayout) view.getParent();
                owner.removeView(view);
                view.setLocationCol(5);
                view.setLocationRow(7);
                //LinearLayout container = (LinearLayout) v;
                //container.addView(view);
                addView(view);
                view.setVisibility(View.VISIBLE);
                Log.d(TAG, "DROPPED");
                return true;
            case DragEvent.ACTION_DRAG_ENDED:
                /*CellView ship = (CellView)v;
                ship.setLocationCol(5);
                ship.setLocationRow(8);
                invalidate();
                ship.setVisibility(VISIBLE);
                Log.d(TAG, "ENDED");*/
                return true;
            default:
                break;
        }
        return true;

    }
}
