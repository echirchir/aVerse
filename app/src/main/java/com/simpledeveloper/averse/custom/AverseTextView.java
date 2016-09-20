package com.simpledeveloper.averse.custom;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;


public class AverseTextView extends TextView {
    public AverseTextView(Context context) {
        super(context);

        applyCustomFont(context);
    }

    public AverseTextView(Context context, AttributeSet attrs) {
        super(context, attrs);

        applyCustomFont(context);
    }

    public AverseTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        applyCustomFont(context);
    }

    public AverseTextView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        applyCustomFont(context);
    }

    private void applyCustomFont(Context context) {
        Typeface customFont = TypeFaceSingleton.getInstance().getTypeface(context);
        setTypeface(customFont);
    }
}
