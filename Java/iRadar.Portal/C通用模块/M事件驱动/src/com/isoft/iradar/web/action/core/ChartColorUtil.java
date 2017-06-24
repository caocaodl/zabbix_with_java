package com.isoft.iradar.web.action.core;

public class ChartColorUtil {
	
	public static class PrevColor{
		public int color = 0;
		public int gradient = 0;
	}
	
	private static void incrementNextColor(PrevColor prevColor) {
		prevColor.color++;
		if (prevColor.color == 7) {
			prevColor.color = 0;

			prevColor.gradient++;
			if (prevColor.gradient == 3) {
				prevColor.gradient = 0;
			}
		}
	}
	
	public static String getNextColor(PrevColor prevColor, int paletteType) {
		int[] palette;
		int gradient, r, g, b;

		switch (paletteType) {
			case 1:
				palette = new int[] {200, 150, 255, 100, 50, 0};
				break;
			case 2:
				palette = new int[] {100, 50, 200, 150, 250, 0};
				break;
			case 0:
			default:
				palette = new int[] {255, 200, 150, 100, 50, 0};
				break;
		}

		gradient = palette[prevColor.gradient];
		r = (100 < gradient) ? 0 : 255;
		g = r;
		b = r;

		switch (prevColor.color) {
			case 0:
				r = gradient;
				break;
			case 1:
				g = gradient;
				break;
			case 2:
				b = gradient;
				break;
			case 3:
				b = gradient;
				r = b;
				break;
			case 4:
				b = gradient;
				g = b;
				break;
			case 5:
				g = gradient;
				r = g;
				break;
			case 6:
				b = gradient;
				g = b;
				r = b;
				break;
		}

		incrementNextColor(prevColor);

		//禁止白色的出现
		if(r>0xF0 && g>0xF0 && b>0xF0) {
			return getNextColor(prevColor, paletteType);
		}else {
			String rs = ('0' + Integer.toHexString(r));
			String gs = ('0' + Integer.toHexString(g));
			String bs = ('0' + Integer.toHexString(b));
			
			String hexColor = rs.substring(rs.length()-2) + gs.substring(gs.length()-2) + bs.substring(bs.length()-2);
			return hexColor.toUpperCase();
		}
	}
}
