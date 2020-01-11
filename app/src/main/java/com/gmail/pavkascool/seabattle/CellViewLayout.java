package com.gmail.pavkascool.seabattle;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import androidx.annotation.Nullable;

public class CellViewLayout extends ViewGroup implements View.OnTouchListener, View.OnDragListener {

    private boolean isOut;
    private boolean dragAllowed;
    private boolean isProhibited;
    private CellView selectedShip;
    private int rows;
    private int cols;
    private Paint paint;
    private int cellSize;

    private List<Coordinates> shots;
    private List<Coordinates> hits;
    private Set<Coordinates> neighbours;
    public final String TAG = "MyFavoriteTag";

    private OnFireListener listener;

    public void setOnFireListener(OnFireListener listener) {
        this.listener = listener;
    }

    public CellViewLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        hits = new ArrayList<Coordinates>();
        shots = new ArrayList<Coordinates>();
        neighbours = new HashSet<Coordinates>();
        setOnTouchListener(this);
        TypedArray ar = context.obtainStyledAttributes(attrs, R.styleable.CellViewLayout, 0, 0);
        cols = ar.getInt(R.styleable.CellViewLayout_cvl_cols, 1);
        rows = ar.getInt(R.styleable.CellViewLayout_cvl_rows, 1);
        dragAllowed = ar.getBoolean(R.styleable.CellViewLayout_drag_allowed, false);
        paint = new Paint();
        setWillNotDraw(false);
        setOnDragListener(this);
        ar.recycle();
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        for(int i = 0; i < getChildCount(); i++) {
            CellView cv = (CellView)getChildAt(i);
            if(cv.getParent() == this)
            cv.layout(cellSize * cv.getLocationCol(), cellSize * cv.getLocationRow(),
                    cellSize * (cv.getLocationCol() + cv.getCols()),
                    cellSize * (cv.getLocationRow() + cv.getRows()));

        }
    }



//


    public void setShots(List<Coordinates> shots) {
        this.shots = shots;
    }

    public void setNeighbours(Set<Coordinates> neighbours) {
        this.neighbours = neighbours;
    }

    public void setHits(List<Coordinates> hits) {
        this.hits = hits;
    }

    public boolean isDragAllowed() {
        return dragAllowed;
    }

    public void setDragAllowed(boolean dragAllowed) {
        this.dragAllowed = dragAllowed;
    }

    public CellView getSelectedShip() {
        return selectedShip;
    }

    public void setSelectedShip(CellView selectedShip) {
        this.selectedShip = selectedShip;
    }

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

        canvas.drawRect(0, 0, cellSize * cols, cellSize * rows, paint);

        if (!shots.isEmpty()) {

            paint.setColor(Color.GREEN);

            for (Coordinates crd: shots) {
                int r = crd.getRow();
                int c = crd.getCol();
                canvas.drawRect(cellSize * c, cellSize * r, cellSize * (c + 1), cellSize * (r + 1), paint);
            }
        }
        if (!neighbours.isEmpty()) {

            paint.setColor(Color.YELLOW);

            for (Coordinates crd: neighbours) {
                int r = crd.getRow();
                int c = crd.getCol();
                canvas.drawRect(cellSize * c, cellSize * r, cellSize * (c + 1), cellSize * (r + 1), paint);
            }
        }
        if (!hits.isEmpty()) {

            paint.setColor(Color.RED);

            for (Coordinates crd: hits) {
                int r = crd.getRow();
                int c = crd.getCol();
                canvas.drawRect(cellSize * c, cellSize * r, cellSize * (c + 1), cellSize * (r + 1), paint);
            }
        }

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
        int rw = -1;
        int cl = -1;
        if(event.getAction() == MotionEvent.ACTION_DOWN) {
            float x = event.getX();
            float y = event.getY();

            int r = (int)(y / cellSize);
            int c = (int)(x / cellSize);
            if (r < rows && c < cols && r >= 0 && c >= 0) {
                rw = r;
                cl = c;
            }
            if(listener != null) listener.onFire(cl, rw);
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

        CellView view = (CellView) event.getLocalState();
        switch (event.getAction()) {
            case DragEvent.ACTION_DRAG_STARTED:
                break;

            case DragEvent.ACTION_DRAG_EXITED:
                isOut = true;
                break;

            case DragEvent.ACTION_DRAG_ENTERED:
                isOut = false;
                break;

            case DragEvent.ACTION_DROP:
                if(!dragAllowed && !isOut) {
                    view.setVisibility(View.VISIBLE);
                    return false;
                }
                if(isOut) {
                    return false;
                }
                float cX = view.getCols() / 2.0f * cellSize;
                float cY = view.getRows() / 2.0f * cellSize;
                float cornerX = event.getX() - cX;
                float cornerY = event.getY() - cY;
                int r = (int) (cornerY / cellSize);
                int c = (int) (cornerX / cellSize);

                if (cornerX % cellSize > cellSize / 2.0) c++;
                if (c < 0) c = 0;
                if (c + view.getCols() > cols) c = cols - view.getCols();

                if (cornerY % cellSize > cellSize / 2.0) r++;
                if (r < 0) r = 0;
                if (r + view.getRows() > rows) r = rows - view.getRows();


                if(isLocProhibited(c, r, view)) {
                    isProhibited = true;
                    return false;
                }

                CellViewLayout owner = (CellViewLayout) view.getParent();
                owner.removeView(view);
                view.setLocationCol(c);
                view.setLocationRow(r);
                addView(view);
                view.setVisibility(View.VISIBLE);
                break;

            case DragEvent.ACTION_DRAG_ENDED:
                if(isOut || isProhibited) {
                    view.setVisibility(View.VISIBLE);
                    isOut = false;
                    isProhibited = false;
                    return false;
                }
                else {
                    if(view.getParent() == this) selectedShip = view;
                }
                break;

            default:
                break;
        }
        return true;

    }

    public boolean isLocProhibited(int c, int r, CellView view) {
        int cols = view.getCols();
        int rows = view.getRows();

        if(c < 0) return true;
        if(c + cols > this.cols) return true;
        if(r < 0) return true;
        if(r + rows > this.rows) return true;

        for (int i = 0; i < getChildCount(); i++) {
            CellView ship = (CellView) getChildAt(i);
            if (view != ship) {
                for (int ic = c; ic < c + cols; ic++) {
                    for (int ir = r; ir < r + rows; ir++) {
                        for (Coordinates crd : ship.getPrihibitedZone()) {
                            if (new Coordinates(ir, ic).equals(crd)) return true;
                        }
                    }
                }
            }
        }

        return false;
    }
}
