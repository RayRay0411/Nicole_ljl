package com.app.book.components;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

public class BookGridView extends GridView {
    public BookGridView(Context context) {
        super(context);
    }

    public BookGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BookGridView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);

    }
}
