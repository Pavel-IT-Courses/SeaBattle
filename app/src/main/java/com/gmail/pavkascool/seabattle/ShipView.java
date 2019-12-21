package com.gmail.pavkascool.seabattle;

import android.content.Context;
import android.util.AttributeSet;
import android.view.DragEvent;
import android.view.View;

import androidx.annotation.Nullable;

public class ShipView extends CellView implements View.OnDragListener {
    public ShipView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onDrag(View v, DragEvent event) {
        return false;
    }
}
