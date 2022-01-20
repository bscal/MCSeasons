package me.bscal.seasons.common.utils;

/**
 * rgba Color utility class
 */
public class Color
{
	public static Color BLACK = new Color(0, 0, 0);
	public static Color WHITE = new Color(255, 255, 255);

	public int r, g, b, a;

	public Color(Color c)
	{
		this(c.r, c.g, c.b, c.a);
	}

	public Color(int r, int g, int b)
	{
		this(r, g, b, 255);
	}

	public Color(double r, double g, double b)
	{
		this((int) Math.round(r), (int) Math.round(g), (int) Math.round(b), 255);
	}

	/**
	 * Class representing a rgba color. Values between 0-255
	 */
	public Color(int r, int g, int b, int a)
	{
		this.r = Math.min(255, Math.max(0, r));
		this.g = Math.min(255, Math.max(0, g));
		this.b = Math.min(255, Math.max(0, b));
		this.a = Math.min(255, Math.max(0, a));
	}

	public Color(int color)
	{
		this(color, true);
	}

	/**
	 * If hasAlpha is true get the alpha channel, if false a = 255
	 */
	public Color(int color, boolean hasAlpha)
	{
		a = hasAlpha ? (color >> 24) & 0xff : 255;
		r = (color >> 16) & 0xff;
		g = (color >> 8) & 0xff;
		b = color & 0xff;
		clampRgbaValues();
	}

	public void clampRgbaValues()
	{
		this.r = Math.min(255, Math.max(0, this.r));
		this.g = Math.min(255, Math.max(0, this.g));
		this.b = Math.min(255, Math.max(0, this.b));
		this.a = Math.min(255, Math.max(0, this.a));
	}

	public void setRgbaFromInt(int color)
	{
		a = Math.min(255, Math.max(0, (color >> 24) & 0xff));
		r = Math.min(255, Math.max(0, (color >> 16) & 0xff));
		g = Math.min(255, Math.max(0, (color >> 8) & 0xff));
		b = Math.min(255, Math.max(0, color & 0xff));
	}

	public void setRgbaFromColor(Color color)
	{
		this.r = Math.min(255, Math.max(0, color.r));
		this.g = Math.min(255, Math.max(0, color.g));
		this.b = Math.min(255, Math.max(0, color.b));
		this.a = Math.min(255, Math.max(0, color.a));
	}

	/**
	 * Blends 2 `Color`'s together. Weight of the color is determined by the alpha.
	 */
	public void blend(Color other)
	{
		double totalAlpha = a + other.a;
		double weight0 = a / totalAlpha;
		double weight1 = other.a / totalAlpha;
		r = (int) Math.round(weight0 * r + weight1 * other.r);
		g = (int) Math.round(weight0 * g + weight1 * other.g);
		b = (int) Math.round(weight0 * b + weight1 * other.b);
		a = Math.max(a, other.a);
	}

	/**
	 * Blends 2 `Color`'s together using a weight. Weight value between 0-1.
	 * If otherWeight is 0 `Color` is unchanged. If otherWeight is 1 the new `Color` will complete replace the originals.
	 */
	public void blend(Color other, double otherWeight)
	{
		otherWeight = Math.min(1.0, Math.max(0.0, otherWeight));
		double weight0 = 1.0 - otherWeight;
		r = (int) Math.round(weight0 * r + otherWeight * other.r);
		g = (int) Math.round(weight0 * g + otherWeight * other.g);
		b = (int) Math.round(weight0 * b + otherWeight * other.b);
		a = Math.max(a, other.a);
	}

	/**
	 * Linearly interpolates between current `Color` this -> the `to` `Color`
	 */
	public void lerp(Color to, float progress)
	{
		progress = Math.min(1f, Math.max(0f, progress));
		r = Math.round(r + (to.r - r) * progress);
		g = Math.round(g + (to.g - g) * progress);
		b = Math.round(b + (to.b - b) * progress);
		a = Math.round(a + (to.a - a) * progress);
		clampRgbaValues();
	}

	/**
	 * Value ranges from 0-360;
	 */
	public void setHue(int hue)
	{
		hue = Math.max(hue, 0);
		hue = Math.min(hue, 360);
		double[] hsv = toHsv(r, g, b);
		hsv[0] = hue / 360D;
		setRgbaFromColor(fromHsv(hsv[0], hsv[1], hsv[2]));
	}

	/**
	 * Values ranges from 0-360
	 */
	public int getHue()
	{
		double[] hsv = toHsv(r, g, b);
		return (int) Math.round(hsv[0] * 360D);
	}

	/**
	 * Will add saturation to the Color. Values range from 0-100
	 */
	public void saturate(int amount)
	{
		double[] hsv = toHsv(r, g, b);
		hsv[1] = Math.max(0D, Math.min(1D, hsv[1] + amount / 100D));
		setRgbaFromColor(fromHsv(hsv[0], hsv[1], hsv[2]));
	}

	/**
	 * Values ranges from 0-100
	 */
	public void setSaturation(int saturation)
	{
		double[] hsv = toHsv(r, g, b);
		hsv[1] = saturation / 100D;
		setRgbaFromColor(fromHsv(hsv[0], hsv[1], hsv[2]));
	}

	/**
	 * Values ranges from 0-100
	 */
	public int getSaturation()
	{
		double[] hsv = toHsv(r, g, b);
		return (int) Math.round(hsv[1] * 100);
	}

	/**
	 * Modifies the lightness of the Color. Values ranges from 0-100.
	 */
	public void lighten(int amount)
	{
		double[] hsl = toHsl(r, g, b);
		hsl[2] = hsl[2] + amount / 100D;
		setRgbaFromColor(fromHsl(hsl[0], hsl[1], hsl[2]));
	}

	/**
	 * Values ranges from 0-100
	 */
	public void setLightness(int lightness)
	{
		double[] hsl = toHsl(r, g, b);
		hsl[2] = Math.max(0D, Math.min(1D, lightness / 100D));
		setRgbaFromColor(fromHsl(hsl[0], hsl[1], hsl[2]));
	}

	/**
	 * Values ranges from 0-100
	 */
	public int getLightness()
	{
		double[] hsl = toHsl(r, g, b);
		return (int) Math.round(hsl[2] * 100);
	}

	public double[] toHsv()
	{
		return toHsv(r, g, b);
	}

	/**
	 * returns a double[] of size 3 containing hsv values. All values range from 0.0 - 1.0;
	 */
	public double[] toHsv(double r, double g, double b)
	{
		r /= 255;
		g /= 255;
		b /= 255;

		double max = Math.max(r, Math.max(g, b));
		double min = Math.min(r, Math.min(g, b));
		double h, s, v = max;

		double d = max - min;
		s = max == 0 ? 0 : d / max;

		if (max == min)
		{
			h = 0; // achromatic
		}
		else
		{
			if (max == r)
				h = (g - b) / d + (g < b ? 6 : 0);
			else if (max == g)
				h = (b - r) / d + 2;
			else
				h = (r - g) / d + 4;

			h /= 6;
		}

		return new double[] { h, s, v };
	}

	public double[] toHsl()
	{
		return toHsl(r, g, b);
	}

	/**
	 * returns a double[] of size 3 containing hsl values. All values range from 0.0 - 1.0;
	 */
	public double[] toHsl(double r, double g, double b)
	{
		r /= 255D;
		g /= 255D;
		b /= 255D;

		double max = Math.max(r, Math.max(b, g));
		double min = Math.min(r, Math.min(b, g));
		double h, s;
		double l = (max + min) / 2D;

		if (max == min)
			h = s = 0;
		else
		{
			double d = max - min;
			s = l > 0.5 ? d / (2.0 - max - min) : d / (max + min);

			if (max == r)
				h = (g - b) / d + (g < b ? 6 : 0);
			else if (max == g)
				h = (b - r) / d + 2;
			else
				h = (r - g) / d + 4;

			h /= 6;
		}

		return new double[] { h, s, l };
	}

	public String toHex()
	{
		//String alpha = pad(Integer.toHexString(a));
		String red = pad(Integer.toHexString(r));
		String green = pad(Integer.toHexString(g));
		String blue = pad(Integer.toHexString(b));
		return "#" + red + green + blue;
	}

	public int toInt()
	{
		return getIntFromColor(r, g, b, a);
	}

	/**
	 * Returns a float array of size 4 contains the Colors rgba values in that respective order. All values are between 0.0 - 1.0
	 */
	public float[] toFloats()
	{
		return new float[] { r / 255f, g / 255f, b / 255f, a / 255f };
	}

	@Override
	public String toString()
	{
		return String.format("Color[r=%d, g=%d, b=%d, a=%d]", r, g, b, a);
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		Color color = (Color) o;

		if (r != color.r)
			return false;
		if (g != color.g)
			return false;
		if (b != color.b)
			return false;
		return a == color.a;
	}

	@Override
	public int hashCode()
	{
		int result = r;
		result = 31 * result + g;
		result = 31 * result + b;
		result = 31 * result + a;
		return result;
	}

	public static int getIntFromColor(int r, int g, int b, int a)
	{
		a = (a << 24) & 0xff000000;
		r = (r << 16) & 0x00ff0000;
		g = (g << 8) & 0x0000ff00;
		b = b & 0x000000ff;
		return a | r | g | b;
	}

	public static Color fromHsl(double[] hsl)
	{
		return fromHsl(hsl[0], hsl[1], hsl[2]);
	}

	/**
	 * returns a new `Color` from hsv values. Expected hsl values to be between 0.0-1.0
	 */
	public static Color fromHsl(double h, double s, double l)
	{
		double r, g, b;

		if (s == 0)
		{
			r = g = b = (int) l; // achromatic
		}
		else
		{
			var q = l < 0.5 ? l * (1 + s) : l + s - l * s;
			var p = 2 * l - q;

			r = hue2rgb(p, q, h + 1D / 3D);
			g = hue2rgb(p, q, h);
			b = hue2rgb(p, q, h - 1D / 3D);
		}
		return new Color(r * 255D, g * 255D, b * 255D);
	}

	public static Color fromHsv(double[] hsv)
	{
		return fromHsv(hsv[0], hsv[1], hsv[2]);
	}

	/**
	 * returns a new `Color` from hsv values. Expected hsv values to be between 0.0-1.0
	 */
	public static Color fromHsv(double h, double s, double v)
	{
		double r, g, b, f, p, q, t;
		r = g = b = 0;
		int i = (int) Math.floor(h * 6);
		f = h * 6 - i;
		p = v * (1 - s);
		q = v * (1 - f * s);
		t = v * (1 - (1 - f) * s);
		switch (i % 6)
		{
		case 0 -> {
			r = v;
			g = t;
			b = p;
		}
		case 1 -> {
			r = q;
			g = v;
			b = p;
		}
		case 2 -> {
			r = p;
			g = v;
			b = t;
		}
		case 3 -> {
			r = p;
			g = q;
			b = v;
		}
		case 4 -> {
			r = t;
			g = p;
			b = v;
		}
		case 5 -> {
			r = v;
			g = p;
			b = q;
		}
		}

		r = Math.round(r * 255);
		g = Math.round(g * 255);
		b = Math.round(b * 255);

		return new Color(r, g, b);
	}

	public static Color fromHex(String hex)
	{
		int red = Integer.parseInt(hex.substring(1, 3), 16);
		int green = Integer.parseInt(hex.substring(3, 5), 16);
		int blue = Integer.parseInt(hex.substring(5, 7), 16);
		return new Color(red, green, blue);
	}

	public static Color fromHex(String hex, int alpha)
	{
		int red = Integer.parseInt(hex.substring(1, 3), 16);
		int green = Integer.parseInt(hex.substring(3, 5), 16);
		int blue = Integer.parseInt(hex.substring(5, 7), 16);
		return new Color(red, green, blue, alpha);
	}

	public static Color fromBlend(Color color1, Color color2)
	{
		Color result = new Color(color1);
		result.blend(color2);
		return result;
	}

	public static Color fromBlend(Color color1, Color color2, float weight)
	{
		Color result = new Color(color1);
		result.blend(color2, weight);
		return result;
	}

	private static String pad(String s)
	{
		return (s.length() == 1) ? "0" + s : s;
	}

	private static double hue2rgb(double p, double q, double t)
	{
		if (t < 0D)
			t += 1D;
		if (t > 1D)
			t -= 1D;
		if (t < 1D / 6D)
			return p + (q - p) * 6D * t;
		if (t < 1D / 2D)
			return q;
		if (t < 2D / 3D)
			return p + (q - p) * (2D / 3D - t) * 6D;
		return p;
	}
}
