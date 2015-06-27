package com.iac.smartmodeproject.ui.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import com.iac.smartmodeproject.R;
import com.iac.smartmodeproject.helpers.Typefaces;

public class AppTextView extends TextView {

	public AppTextView(Context context) {
		super(context);
	}

	public AppTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		if (!isInEditMode()) {
			initTypeface(this, context, attrs);
		}
	}

	public AppTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		if (!isInEditMode()) {
			initTypeface(this, context, attrs);
		}
	}

	// typeface initialization
	public static void initTypeface(TextView textView, Context context,
			AttributeSet attrs) {
		Typeface typeface;

		if (attrs != null) {
			TypedArray a = context.obtainStyledAttributes(attrs,
					R.styleable.AppWidgetView);

			if (a.hasValue(R.styleable.AppWidgetView_typeface)) {
				int typefaceValue = a.getInt(
						R.styleable.AppWidgetView_typeface,
						Typefaces.Typeface.ROBOTO_REGULAR);
				typeface = Typefaces.obtainTypeface(context, typefaceValue);
			} else {
				int fontFamily = a.getInt(R.styleable.AppWidgetView_fontFamily,
						Typefaces.FontFamily.ROBOTO);
				int textWeight = a.getInt(R.styleable.AppWidgetView_textWeight,
						Typefaces.TextWeight.NORMAL);
				int textStyle = a.getInt(R.styleable.AppWidgetView_textStyle,
						Typefaces.TextStyle.NORMAL);

				typeface = Typefaces.obtainTypeface(context, fontFamily,
						textWeight, textStyle);
			}

			a.recycle();
		} else {
			typeface = Typefaces.obtainTypeface(context,
					Typefaces.Typeface.ROBOTO_REGULAR);
		}

		setTypeface(textView, typeface);
	}

	public static void setTypeface(TextView textView, Typeface typeface) {
		textView.setPaintFlags(textView.getPaintFlags()
				| Paint.SUBPIXEL_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
		textView.setTypeface(typeface);
	}

}
