package com.iac.smartmodeproject.helpers;

import java.util.Hashtable;

import android.content.Context;

public class Typefaces {

	private static final Hashtable<Integer, android.graphics.Typeface> cache = new Hashtable<Integer, android.graphics.Typeface>();

	public static android.graphics.Typeface obtainTypeface(Context context,
			int typefaceValue) throws IllegalArgumentException {
		synchronized (cache) {
			android.graphics.Typeface typeface = cache.get(typefaceValue);
			if (typeface == null) {
				typeface = createTypeface(context, typefaceValue);
				cache.put(typefaceValue, typeface);
			}
			return typeface;
		}
	}

	// public methos to return the typeface
	public static android.graphics.Typeface obtainTypeface(Context context,
			int fontFamily, int textWeight, int textStyle)
			throws IllegalArgumentException {
		int typefaceValue = getTypefaceValue(fontFamily, textWeight, textStyle);
		return obtainTypeface(context, typefaceValue);
	}

	private static android.graphics.Typeface createTypeface(Context context,
			int typefaceValue) throws IllegalArgumentException {
		String typefacePath;
		switch (typefaceValue) {
		case Typeface.ROBOTO_THIN:
			typefacePath = "fonts/Roboto-Thin.ttf";
			break;
		case Typeface.ROBOTO_THIN_ITALIC:
			typefacePath = "fonts/Roboto-ThinItalic.ttf";
			break;
		case Typeface.ROBOTO_LIGHT:
			typefacePath = "fonts/Roboto-Light.ttf";
			break;
		case Typeface.ROBOTO_LIGHT_ITALIC:
			typefacePath = "fonts/Roboto-LightItalic.ttf";
			break;
		case Typeface.ROBOTO_REGULAR:
			typefacePath = "fonts/Roboto-Regular.ttf";
			break;
		case Typeface.ROBOTO_ITALIC:
			typefacePath = "fonts/Roboto-Italic.ttf";
			break;
		case Typeface.ROBOTO_MEDIUM:
			typefacePath = "fonts/Roboto-Medium.ttf";
			break;
		case Typeface.ROBOTO_MEDIUM_ITALIC:
			typefacePath = "fonts/Roboto-MediumItalic.ttf";
			break;
		case Typeface.ROBOTO_BOLD:
			typefacePath = "fonts/Roboto-Bold.ttf";
			break;
		case Typeface.ROBOTO_BOLD_ITALIC:
			typefacePath = "fonts/Roboto-BoldItalic.ttf";
			break;
		case Typeface.ROBOTO_BLACK:
			typefacePath = "fonts/Roboto-Black.ttf";
			break;
		case Typeface.ROBOTO_BLACK_ITALIC:
			typefacePath = "fonts/Roboto-BlackItalic.ttf";
			break;
		case Typeface.ROBOTO_CONDENSED_LIGHT:
			typefacePath = "fonts/RobotoCondensed-Light.ttf";
			break;
		case Typeface.ROBOTO_CONDENSED_LIGHT_ITALIC:
			typefacePath = "fonts/RobotoCondensed-LightItalic.ttf";
			break;
		case Typeface.ROBOTO_CONDENSED_REGULAR:
			typefacePath = "fonts/RobotoCondensed-Regular.ttf";
			break;
		case Typeface.ROBOTO_CONDENSED_ITALIC:
			typefacePath = "fonts/RobotoCondensed-Italic.ttf";
			break;
		case Typeface.ROBOTO_CONDENSED_BOLD:
			typefacePath = "fonts/RobotoCondensed-Bold.ttf";
			break;
		case Typeface.ROBOTO_CONDENSED_BOLD_ITALIC:
			typefacePath = "fonts/RobotoCondensed-BoldItalic.ttf";
			break;
		case Typeface.ROBOTO_SLAB_THIN:
			typefacePath = "fonts/RobotoSlab-Thin.ttf";
			break;
		case Typeface.ROBOTO_SLAB_LIGHT:
			typefacePath = "fonts/RobotoSlab-Light.ttf";
			break;
		case Typeface.ROBOTO_SLAB_REGULAR:
			typefacePath = "fonts/RobotoSlab-Regular.ttf";
			break;
		case Typeface.ROBOTO_SLAB_BOLD:
			typefacePath = "fonts/RobotoSlab-Bold.ttf";
			break;
		default:
			throw new IllegalArgumentException(
					"Unknown `typeface` attribute value " + typefaceValue);
		}

		return android.graphics.Typeface.createFromAsset(context.getAssets(),
				typefacePath);
	}

	private static int getTypefaceValue(int fontFamily, int textWeight,
			int textStyle) {
		int typeface;
		if (fontFamily == FontFamily.ROBOTO) {
			if (textWeight == TextWeight.NORMAL) {
				switch (textStyle) {
				case TextStyle.NORMAL:
					typeface = Typeface.ROBOTO_REGULAR;
					break;
				case TextStyle.ITALIC:
					typeface = Typeface.ROBOTO_ITALIC;
					break;
				default:
					throw new IllegalArgumentException(
							"`textStyle` attribute value " + textStyle
									+ " is not supported for this fontFamily "
									+ fontFamily + " and textWeight "
									+ textWeight);
				}
			} else if (textWeight == TextWeight.THIN) {
				switch (textStyle) {
				case TextStyle.NORMAL:
					typeface = Typeface.ROBOTO_THIN;
					break;
				case TextStyle.ITALIC:
					typeface = Typeface.ROBOTO_THIN_ITALIC;
					break;
				default:
					throw new IllegalArgumentException(
							"`textStyle` attribute value " + textStyle
									+ " is not supported for this fontFamily "
									+ fontFamily + " and textWeight "
									+ textWeight);
				}
			} else if (textWeight == TextWeight.LIGHT) {
				switch (textStyle) {
				case TextStyle.NORMAL:
					typeface = Typeface.ROBOTO_LIGHT;
					break;
				case TextStyle.ITALIC:
					typeface = Typeface.ROBOTO_LIGHT_ITALIC;
					break;
				default:
					throw new IllegalArgumentException(
							"`textStyle` attribute value " + textStyle
									+ " is not supported for this fontFamily "
									+ fontFamily + " and textWeight "
									+ textWeight);
				}
			} else if (textWeight == TextWeight.MEDIUM) {
				switch (textStyle) {
				case TextStyle.NORMAL:
					typeface = Typeface.ROBOTO_MEDIUM;
					break;
				case TextStyle.ITALIC:
					typeface = Typeface.ROBOTO_MEDIUM_ITALIC;
					break;
				default:
					throw new IllegalArgumentException(
							"`textStyle` attribute value " + textStyle
									+ " is not supported for this fontFamily "
									+ fontFamily + " and textWeight "
									+ textWeight);
				}
			} else if (textWeight == TextWeight.BOLD) {
				switch (textStyle) {
				case TextStyle.NORMAL:
					typeface = Typeface.ROBOTO_BOLD;
					break;
				case TextStyle.ITALIC:
					typeface = Typeface.ROBOTO_BOLD_ITALIC;
					break;
				default:
					throw new IllegalArgumentException(
							"`textStyle` attribute value " + textStyle
									+ " is not supported for this fontFamily "
									+ fontFamily + " and textWeight "
									+ textWeight);
				}
			} else if (textWeight == TextWeight.ULTRA_BOLD) {
				switch (textStyle) {
				case TextStyle.NORMAL:
					typeface = Typeface.ROBOTO_BLACK;
					break;
				case TextStyle.ITALIC:
					typeface = Typeface.ROBOTO_BLACK_ITALIC;
					break;
				default:
					throw new IllegalArgumentException(
							"`textStyle` attribute value " + textStyle
									+ " is not supported for this fontFamily "
									+ fontFamily + " and textWeight "
									+ textWeight);
				}
			} else {
				throw new IllegalArgumentException(
						"`textWeight` attribute value " + textWeight
								+ " is not supported for this font family "
								+ fontFamily);
			}
		} else if (fontFamily == FontFamily.ROBOTO_CONDENSED) {
			if (textWeight == TextWeight.NORMAL) {
				switch (textStyle) {
				case TextStyle.NORMAL:
					typeface = Typeface.ROBOTO_CONDENSED_REGULAR;
					break;
				case TextStyle.ITALIC:
					typeface = Typeface.ROBOTO_CONDENSED_ITALIC;
					break;
				default:
					throw new IllegalArgumentException(
							"`textStyle` attribute value " + textStyle
									+ " is not supported for this fontFamily "
									+ fontFamily + " and textWeight "
									+ textWeight);
				}
			} else if (textWeight == TextWeight.LIGHT) {
				switch (textStyle) {
				case TextStyle.NORMAL:
					typeface = Typeface.ROBOTO_CONDENSED_LIGHT;
					break;
				case TextStyle.ITALIC:
					typeface = Typeface.ROBOTO_CONDENSED_LIGHT_ITALIC;
					break;
				default:
					throw new IllegalArgumentException(
							"`textStyle` attribute value " + textStyle
									+ " is not supported for this fontFamily "
									+ fontFamily + " and textWeight "
									+ textWeight);
				}
			} else if (textWeight == TextWeight.BOLD) {
				switch (textStyle) {
				case TextStyle.NORMAL:
					typeface = Typeface.ROBOTO_CONDENSED_BOLD;
					break;
				case TextStyle.ITALIC:
					typeface = Typeface.ROBOTO_CONDENSED_BOLD_ITALIC;
					break;
				default:
					throw new IllegalArgumentException(
							"`textStyle` attribute value " + textStyle
									+ " is not supported for this fontFamily "
									+ fontFamily + " and textWeight "
									+ textWeight);
				}
			} else {
				throw new IllegalArgumentException(
						"`textWeight` attribute value " + textWeight
								+ " is not supported for this font family "
								+ fontFamily);
			}
		} else if (fontFamily == FontFamily.ROBOTO_SLAB) {

			// roboto slab does not support 'textStyle'
			if (textStyle != TextStyle.NORMAL) {
				throw new IllegalArgumentException(
						"`textStyle` attribute value " + textStyle
								+ " is not supported for this fontFamily "
								+ fontFamily);
			}

			if (textWeight == TextWeight.NORMAL) {
				typeface = Typeface.ROBOTO_SLAB_REGULAR;

			} else if (textWeight == TextWeight.THIN) {
				typeface = Typeface.ROBOTO_SLAB_THIN;

			} else if (textWeight == TextWeight.LIGHT) {
				typeface = Typeface.ROBOTO_SLAB_LIGHT;

			} else if (textWeight == TextWeight.BOLD) {
				typeface = Typeface.ROBOTO_SLAB_BOLD;

			} else {
				throw new IllegalArgumentException(
						"`textWeight` attribute value " + textWeight
								+ " is not supported for this font family "
								+ fontFamily);
			}
		} else {
			throw new IllegalArgumentException(
					"Unknown `fontFamily` attribute value " + fontFamily);
		}
		return typeface;
	}

	public class Typeface {
		public final static int ROBOTO_THIN = 0;
		public final static int ROBOTO_THIN_ITALIC = 1;
		public final static int ROBOTO_LIGHT = 2;
		public final static int ROBOTO_LIGHT_ITALIC = 3;
		public final static int ROBOTO_REGULAR = 4;
		public final static int ROBOTO_ITALIC = 5;
		public final static int ROBOTO_MEDIUM = 6;
		public final static int ROBOTO_MEDIUM_ITALIC = 7;
		public final static int ROBOTO_BOLD = 8;
		public final static int ROBOTO_BOLD_ITALIC = 9;
		public final static int ROBOTO_BLACK = 10;
		public final static int ROBOTO_BLACK_ITALIC = 11;
		public final static int ROBOTO_CONDENSED_LIGHT = 12;
		public final static int ROBOTO_CONDENSED_LIGHT_ITALIC = 13;
		public final static int ROBOTO_CONDENSED_REGULAR = 14;
		public final static int ROBOTO_CONDENSED_ITALIC = 15;
		public final static int ROBOTO_CONDENSED_BOLD = 16;
		public final static int ROBOTO_CONDENSED_BOLD_ITALIC = 17;
		public final static int ROBOTO_SLAB_THIN = 18;
		public final static int ROBOTO_SLAB_LIGHT = 19;
		public final static int ROBOTO_SLAB_REGULAR = 20;
		public final static int ROBOTO_SLAB_BOLD = 21;
	}

	public class FontFamily {
		public static final int ROBOTO = 0;
		public static final int ROBOTO_CONDENSED = 1;
		public static final int ROBOTO_SLAB = 2;
	}

	public class TextWeight {
		public static final int NORMAL = 0;
		public static final int THIN = 1;
		public static final int LIGHT = 2;
		public static final int MEDIUM = 3;
		public static final int BOLD = 4;
		public static final int ULTRA_BOLD = 5;
	}

	public class TextStyle {
		public static final int NORMAL = 0;
		public static final int ITALIC = 1;
	}

}
