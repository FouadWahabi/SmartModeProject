package com.iac.smartmodeproject.ui.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

public class AnimLinearLayout extends LinearLayout {

	public float xFraction;
	public float yFraction;

	public AnimLinearLayout(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public AnimLinearLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public AnimLinearLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public float getXFraction() {
		return getX() / getWidth();
	}

	public void setXFraction(float xFraction) {
		final int width = getWidth();
		setX((width > 0) ? (xFraction * width) : -9999);
	}

	public float getYFraction() {
		return getY() / getHeight();
	}

	public void setYFraction(float yFraction) {
		final int height = getHeight();
		setY((height > 0) ? (yFraction * height) : -9999);
	}

}
