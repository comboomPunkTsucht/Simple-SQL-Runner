package components;

import java.awt.Color;
import java.awt.color.ColorSpace;

public class NORD_COLORS extends Color {

    private static final long serialVersionUID = 8400642762895586857L;
	// Nord color constants
	// Dark colors
    public static final NORD_COLORS BLACK = new NORD_COLORS(0x30343F);
    public static final NORD_COLORS RED = new NORD_COLORS(0xCF2A31);
    public static final NORD_COLORS GREEN = new NORD_COLORS(0x7BBAA8);
    public static final NORD_COLORS YELLOW = new NORD_COLORS(0xEFBF6A);
    public static final NORD_COLORS BLUE = new NORD_COLORS(0x63A2CA);
    public static final NORD_COLORS MAGENTA = new NORD_COLORS(0xC1AC9E);
    public static final NORD_COLORS CYAN = new NORD_COLORS(0xF0C9D0);
    public static final NORD_COLORS WHITE = new NORD_COLORS(0xEBEFF1);
	// Bright colors
    public static final NORD_COLORS BRIGHT_BLACK = new NORD_COLORS(0x4D4E59);
    public static final NORD_COLORS BRIGHT_RED = new NORD_COLORS(0xCF2A31);
    public static final NORD_COLORS BRIGHT_GREEN = new NORD_COLORS(0x7BBAA8);
    public static final NORD_COLORS BRIGHT_YELLOW = new NORD_COLORS(0xEFBF6A);
    public static final NORD_COLORS BRIGHT_BLUE = new NORD_COLORS(0x63A2CA);
    public static final NORD_COLORS BRIGHT_MAGENTA = new NORD_COLORS(0xBD8A9B);
    public static final NORD_COLORS BRIGHT_CYAN = new NORD_COLORS(0xF0C9D0);
    public static final NORD_COLORS BRIGHT_WHITE = new NORD_COLORS(0xEBEFF1);

	// Light colors

	public static final NORD_COLORS LIGHT_BLACK = BRIGHT_BLACK;
	public static final NORD_COLORS LIGHT_RED = BRIGHT_RED;
	public static final NORD_COLORS LIGHT_GREEN = BRIGHT_GREEN;
	public static final NORD_COLORS LIGHT_YELLOW = BRIGHT_YELLOW;
	public static final NORD_COLORS LIGHT_BLUE = BRIGHT_BLUE;
	public static final NORD_COLORS LIGHT_MAGENTA = BRIGHT_MAGENTA;
	public static final NORD_COLORS LIGHT_CYAN = BRIGHT_CYAN;
	public static final NORD_COLORS LIGHT_WHITE = BRIGHT_WHITE;

	//Grey
	public static final NORD_COLORS GREY = BRIGHT_BLACK;
	public static final NORD_COLORS LIGHT_GREY = BRIGHT_WHITE;
	public static final NORD_COLORS BRIGHT_GREY = BRIGHT_WHITE;

    public static final NORD_COLORS FOREGROUND = new NORD_COLORS(0xD8DEE9);
    public static final NORD_COLORS BACKGROUND = new NORD_COLORS(0x2E3440);
	public static final NORD_COLORS SELECTED_FOREGROUND = new NORD_COLORS(0x000000);
	public static final NORD_COLORS SELECTED_BACKGROUND = new NORD_COLORS(0xFFFACD);
	public static final NORD_COLORS URL_COLOR = new NORD_COLORS(0x808080);
    public static final NORD_COLORS CURSOR = new NORD_COLORS(0x81A1C1);

    // Existing constructors
    public NORD_COLORS(int rgb) {
        super(rgb);
    }

    public NORD_COLORS(int rgba, boolean hasalpha) {
        super(rgba, hasalpha);
    }

    public NORD_COLORS(int r, int g, int b) {
        super(r, g, b);
    }

    public NORD_COLORS(float r, float g, float b) {
        super(r, g, b);
    }

    public NORD_COLORS(ColorSpace cspace, float[] components, float alpha) {
        super(cspace, components, alpha);
    }

    public NORD_COLORS(int r, int g, int b, int a) {
        super(r, g, b, a);
    }

    public NORD_COLORS(float r, float g, float b, float a) {
        super(r, g, b, a);
    }


    // New constructor for HSLA color representation
    public NORD_COLORS(float h, float s, float l, float alpha) {
        super(hslToRgb(h, s, l));
        int rgb = getRGB();
        int a = Math.round(alpha * 255);
        super.setRGB((rgb & 0xFFFFFF) | (a << 24));
    }

    // Helper method to convert HSL to RGB
    private static int hslToRgb(float h, float s, float l) {
        float r, g, b;

        if (s == 0) {
            r = g = b = l;
        } else {
            float q = l < 0.5 ? l * (1 + s) : l + s - l * s;
            float p = 2 * l - q;
            r = hueToRgb(p, q, h + 1f/3);
            g = hueToRgb(p, q, h);
            b = hueToRgb(p, q, h - 1f/3);
        }

        return new NORD_COLORS(r, g, b).getRGB();
    }

    private static float hueToRgb(float p, float q, float t) {
        if (t < 0) t += 1;
        if (t > 1) t -= 1;
        if (t < 1f/6) return p + (q - p) * 6 * t;
        if (t < 1f/2) return q;
        if (t < 2f/3) return p + (q - p) * (2f/3 - t) * 6;
        return p;
    }
}
