package com.example.colorviewerontemu;

import android.graphics.Color;

import java.util.HashMap;

public class ColorUtils {

    // Create a map of common colors with more shades of green and brown
    private static final HashMap<String, Integer> colorMap = new HashMap<String, Integer>() {{
        put("AliceBlue", Color.rgb( 0xF0, 0xF8, 0xFF));
        put("AntiqueWhite", Color.rgb( 0xFA, 0xEB, 0xD7));
        put("Aqua", Color.rgb( 0x00, 0xFF, 0xFF));
        put("Aquamarine", Color.rgb( 0x7F, 0xFF, 0xD4));
        put("Azure", Color.rgb( 0xF0, 0xFF, 0xFF));
        put("Beige", Color.rgb( 0xF5, 0xF5, 0xDC));
        put("Bisque", Color.rgb( 0xFF, 0xE4, 0xC4));
        put("Black", Color.rgb( 0x00, 0x00, 0x00));
        put("BlanchedAlmond", Color.rgb( 0xFF, 0xEB, 0xCD));
        put("Blue", Color.rgb( 0x00, 0x00, 0xFF));
        put("BlueViolet", Color.rgb( 0x8A, 0x2B, 0xE2));
        put("Brown", Color.rgb( 0xA5, 0x2A, 0x2A));
        put("BurlyWood", Color.rgb( 0xDE, 0xB8, 0x87));
        put("CadetBlue", Color.rgb( 0x5F, 0x9E, 0xA0));
        put("Chartreuse", Color.rgb( 0x7F, 0xFF, 0x00));
        put("Chocolate", Color.rgb( 0xD2, 0x69, 0x1E));
        put("Coral", Color.rgb( 0xFF, 0x7F, 0x50));
        put("CornflowerBlue", Color.rgb( 0x64, 0x95, 0xED));
        put("Cornsilk", Color.rgb( 0xFF, 0xF8, 0xDC));
        put("Crimson", Color.rgb( 0xDC, 0x14, 0x3C));
        put("Cyan", Color.rgb( 0x00, 0xFF, 0xFF));
        put("DarkBlue", Color.rgb( 0x00, 0x00, 0x8B));
        put("DarkCyan", Color.rgb( 0x00, 0x8B, 0x8B));
        put("DarkGoldenRod", Color.rgb( 0xB8, 0x86, 0x0B));
        put("DarkGray", Color.rgb( 0xA9, 0xA9, 0xA9));
        put("DarkGreen", Color.rgb( 0x00, 0x64, 0x00));
        put("DarkKhaki", Color.rgb( 0xBD, 0xB7, 0x6B));
        put("DarkMagenta", Color.rgb( 0x8B, 0x00, 0x8B));
        put("DarkOliveGreen", Color.rgb( 0x55, 0x6B, 0x2F));
        put("DarkOrange", Color.rgb( 0xFF, 0x8C, 0x00));
        put("DarkOrchid", Color.rgb( 0x99, 0x32, 0xCC));
        put("DarkRed", Color.rgb( 0x8B, 0x00, 0x00));
        put("DarkSalmon", Color.rgb( 0xE9, 0x96, 0x7A));
        put("DarkSeaGreen", Color.rgb( 0x8F, 0xBC, 0x8F));
        put("DarkSlateBlue", Color.rgb( 0x48, 0x3D, 0x8B));
        put("DarkSlateGray", Color.rgb( 0x2F, 0x4F, 0x4F));
        put("DarkTurquoise", Color.rgb( 0x00, 0xCE, 0xD1));
        put("DarkViolet", Color.rgb( 0x94, 0x00, 0xD3));
        put("DeepPink", Color.rgb( 0xFF, 0x14, 0x93));
        put("DeepSkyBlue", Color.rgb( 0x00, 0xBF, 0xFF));
        put("DimGray", Color.rgb( 0x69, 0x69, 0x69));
        put("DodgerBlue", Color.rgb( 0x1E, 0x90, 0xFF));
        put("FireBrick", Color.rgb( 0xB2, 0x22, 0x22));
        put("FloralWhite", Color.rgb( 0xFF, 0xFA, 0xF0));
        put("ForestGreen", Color.rgb( 0x22, 0x8B, 0x22));
        put("Fuchsia", Color.rgb( 0xFF, 0x00, 0xFF));
        put("Gainsboro", Color.rgb( 0xDC, 0xDC, 0xDC));
        put("GhostWhite", Color.rgb( 0xF8, 0xF8, 0xFF));
        put("Gold", Color.rgb( 0xFF, 0xD7, 0x00));
        put("GoldenRod", Color.rgb( 0xDA, 0xA5, 0x20));
        put("Gray", Color.rgb( 0x80, 0x80, 0x80));
        put("Green", Color.rgb( 0x00, 0x80, 0x00));
        put("GreenYellow", Color.rgb( 0xAD, 0xFF, 0x2F));
        put("HoneyDew", Color.rgb( 0xF0, 0xFF, 0xF0));
        put("HotPink", Color.rgb( 0xFF, 0x69, 0xB4));
        put("IndianRed", Color.rgb( 0xCD, 0x5C, 0x5C));
        put("Indigo", Color.rgb( 0x4B, 0x00, 0x82));
        put("Ivory", Color.rgb( 0xFF, 0xFF, 0xF0));
        put("Khaki", Color.rgb( 0xF0, 0xE6, 0x8C));
        put("Lavender", Color.rgb( 0xE6, 0xE6, 0xFA));
        put("LavenderBlush", Color.rgb( 0xFF, 0xF0, 0xF5));
        put("LawnGreen", Color.rgb( 0x7C, 0xFC, 0x00));
        put("LemonChiffon", Color.rgb( 0xFF, 0xFA, 0xCD));
        put("LightBlue", Color.rgb( 0xAD, 0xD8, 0xE6));
        put("LightCoral", Color.rgb( 0xF0, 0x80, 0x80));
        put("LightCyan", Color.rgb( 0xE0, 0xFF, 0xFF));
        put("LightGoldenRodYellow", Color.rgb( 0xFA, 0xFA, 0xD2));
        put("LightGray", Color.rgb( 0xD3, 0xD3, 0xD3));
        put("LightGreen", Color.rgb( 0x90, 0xEE, 0x90));
        put("LightPink", Color.rgb( 0xFF, 0xB6, 0xC1));
        put("LightSalmon", Color.rgb( 0xFF, 0xA0, 0x7A));
        put("LightSeaGreen", Color.rgb( 0x20, 0xB2, 0xAA));
        put("LightSkyBlue", Color.rgb( 0x87, 0xCE, 0xFA));
        put("LightSlateGray", Color.rgb( 0x77, 0x88, 0x99));
        put("LightSteelBlue", Color.rgb( 0xB0, 0xC4, 0xDE));
        put("LightYellow", Color.rgb( 0xFF, 0xFF, 0xE0));
        put("Lime", Color.rgb( 0x00, 0xFF, 0x00));
        put("LimeGreen", Color.rgb( 0x32, 0xCD, 0x32));
        put("Linen", Color.rgb( 0xFA, 0xF0, 0xE6));
        put("Magenta", Color.rgb( 0xFF, 0x00, 0xFF));
        put("Maroon", Color.rgb( 0x80, 0x00, 0x00));
        put("MediumAquaMarine", Color.rgb( 0x66, 0xCD, 0xAA));
        put("MediumBlue", Color.rgb( 0x00, 0x00, 0xCD));
        put("MediumOrchid", Color.rgb( 0xBA, 0x55, 0xD3));
        put("MediumPurple", Color.rgb( 0x93, 0x70, 0xDB));
        put("MediumSeaGreen", Color.rgb( 0x3C, 0xB3, 0x71));
        put("MediumSlateBlue", Color.rgb( 0x7B, 0x68, 0xEE));
        put("MediumSpringGreen", Color.rgb( 0x00, 0xFA, 0x9A));
        put("MediumTurquoise", Color.rgb( 0x48, 0xD1, 0xCC));
        put("MediumVioletRed", Color.rgb( 0xC7, 0x15, 0x85));
        put("MidnightBlue", Color.rgb( 0x19, 0x19, 0x70));
        put("MintCream", Color.rgb( 0xF5, 0xFF, 0xFA));
        put("MistyRose", Color.rgb( 0xFF, 0xE4, 0xE1));
        put("Moccasin", Color.rgb( 0xFF, 0xE4, 0xB5));
        put("NavajoWhite", Color.rgb( 0xFF, 0xDE, 0xAD));
        put("Navy", Color.rgb( 0x00, 0x00, 0x80));
        put("OldLace", Color.rgb( 0xFD, 0xF5, 0xE6));
        put("Olive", Color.rgb( 0x80, 0x80, 0x00));
        put("OliveDrab", Color.rgb( 0x6B, 0x8E, 0x23));
        put("Orange", Color.rgb( 0xFF, 0xA5, 0x00));
        put("OrangeRed", Color.rgb( 0xFF, 0x45, 0x00));
        put("Orchid", Color.rgb( 0xDA, 0x70, 0xD6));
        put("PaleGoldenRod", Color.rgb( 0xEE, 0xE8, 0xAA));
        put("PaleGreen", Color.rgb( 0x98, 0xFB, 0x98));
        put("PaleTurquoise", Color.rgb( 0xAF, 0xEE, 0xEE));
        put("PaleVioletRed", Color.rgb( 0xDB, 0x70, 0x93));
        put("PapayaWhip", Color.rgb( 0xFF, 0xEF, 0xD5));
        put("PeachPuff", Color.rgb( 0xFF, 0xDA, 0xB9));
        put("Peru", Color.rgb( 0xCD, 0x85, 0x3F));
        put("Pink", Color.rgb( 0xFF, 0xC0, 0xCB));
        put("Plum", Color.rgb( 0xDD, 0xA0, 0xDD));
        put("PowderBlue", Color.rgb( 0xB0, 0xE0, 0xE6));
        put("Purple", Color.rgb( 0x80, 0x00, 0x80));
        put("Red", Color.rgb( 0xFF, 0x00, 0x00));
        put("RosyBrown", Color.rgb( 0xBC, 0x8F, 0x8F));
        put("RoyalBlue", Color.rgb( 0x41, 0x69, 0xE1));
        put("SaddleBrown", Color.rgb( 0x8B, 0x45, 0x13));
        put("Salmon", Color.rgb( 0xFA, 0x80, 0x72));
        put("SandyBrown", Color.rgb( 0xF4, 0xA4, 0x60));
        put("SeaGreen", Color.rgb( 0x2E, 0x8B, 0x57));
        put("SeaShell", Color.rgb( 0xFF, 0xF5, 0xEE));
        put("Sienna", Color.rgb( 0xA0, 0x52, 0x2D));
        put("Silver", Color.rgb( 0xC0, 0xC0, 0xC0));
        put("SkyBlue", Color.rgb( 0x87, 0xCE, 0xEB));
        put("SlateBlue", Color.rgb( 0x6A, 0x5A, 0xCD));
        put("SlateGray", Color.rgb( 0x70, 0x80, 0x90));
        put("Snow", Color.rgb( 0xFF, 0xFA, 0xFA));
        put("SpringGreen", Color.rgb( 0x00, 0xFF, 0x7F));
        put("SteelBlue", Color.rgb( 0x46, 0x82, 0xB4));
        put("Tan", Color.rgb( 0xD2, 0xB4, 0x8C));
        put("Teal", Color.rgb( 0x00, 0x80, 0x80));
        put("Thistle", Color.rgb( 0xD8, 0xBF, 0xD8));
        put("Tomato", Color.rgb( 0xFF, 0x63, 0x47));
        put("Turquoise", Color.rgb( 0x40, 0xE0, 0xD0));
        put("Violet", Color.rgb( 0xEE, 0x82, 0xEE));
        put("Wheat", Color.rgb( 0xF5, 0xDE, 0xB3));
        put("White", Color.rgb( 0xFF, 0xFF, 0xFF));
        put("WhiteSmoke", Color.rgb( 0xF5, 0xF5, 0xF5));
        put("Yellow", Color.rgb( 0xFF, 0xFF, 0x00));
        put("YellowGreen", Color.rgb( 0x9A, 0xCD, 0x32));
        put("AntiqueRose", Color.rgb( 0xF4, 0xC2, 0xC2));
        put("Apricot", Color.rgb( 0xFB, 0xD1, 0x69));
        put("AzureMist", Color.rgb( 0xF0, 0xFF, 0xFF));
        put("BlushPink", Color.rgb( 0xF9, 0xC8, 0xD6));
        put("CelestialBlue", Color.rgb( 0x5B, 0x9A, 0xF4));
        put("Cinnamon", Color.rgb( 0x7B, 0x3F, 0x42));
        put("CottonCandy", Color.rgb( 0xFF, 0xB0, 0xD0));
        put("CoralPink", Color.rgb( 0xFF, 0x6F, 0x61));
        put("ElectricPurple", Color.rgb( 0xBF, 0x00, 0xFF));
        put("EmeraldGreen", Color.rgb( 0x50, 0xC8, 0x50));
        put("FrostedMint", Color.rgb( 0xD4, 0xFF, 0xD9));
        put("LavenderMist", Color.rgb( 0xE1, 0xD0, 0xF1));
        put("LimePunch", Color.rgb( 0x99, 0xFF, 0x00));
        put("Moonstone", Color.rgb( 0x3C, 0x6E, 0x8C));
        put("NeonPink", Color.rgb( 0xFF, 0x10, 0x72));
        put("Opal", Color.rgb( 0xA8, 0xC0, 0xD3));
        put("PalePeach", Color.rgb( 0xFF, 0xD1, 0xB8));
        put("RoyalPurple", Color.rgb( 0x6A, 0x33, 0x8F));
        put("SapphireBlue", Color.rgb( 0x0F, 0x52, 0x7A));
        put("SilverLake", Color.rgb( 0xC0, 0xD2, 0xD6));
        put("SunsetOrange", Color.rgb( 0xFF, 0x4F, 0x55));
        put("Tangerine", Color.rgb( 0xFF, 0x8C, 0x00));
        put("TurquoiseBlue", Color.rgb( 0x00, 0xF1, 0xF5));
        put("Vermilion", Color.rgb( 0xE5, 0x32, 0x3C));
        put("WildStrawberry", Color.rgb( 0xFF, 0x43, 0x6B));
        put("YellowGold", Color.rgb( 0xFF, 0xD7, 0x00));
        put("Zaffre", Color.rgb( 0x00, 0x1D, 0x6C));
        put("Auburn", Color.rgb( 0xA5, 0x2A, 0x2A));
        put("Burgundy", Color.rgb( 0x80, 0x00, 0x1C));
        put("Champagne", Color.rgb( 0xF7, 0xE7, 0xA6));
        put("ChartreuseYellow", Color.rgb( 0xD3, 0xF0, 0x40));
        put("Claret", Color.rgb( 0x7F, 0x23, 0x2D));
        put("Copper", Color.rgb( 0xB8, 0x6D, 0x2B));
        put("CrimsonRed", Color.rgb( 0xDC, 0x14, 0x3C));
        put("DeepLavender", Color.rgb( 0x9E, 0x4E, 0x9E));
        put("Feldgrau", Color.rgb( 0x4D, 0x5D, 0x53));
        put("FireOpal", Color.rgb( 0xF9, 0x5A, 0x4F));
        put("FuchsiaRose", Color.rgb( 0xD1, 0x00, 0x6C));
        put("Grape", Color.rgb( 0x6F, 0x2F, 0x92));
        put("IndianPink", Color.rgb( 0xF1, 0x6A, 0x6A));
        put("Jade", Color.rgb( 0x00, 0x80, 0x3D));
        put("Limeade", Color.rgb( 0x70, 0xE1, 0x60));
        put("MauveTaupe", Color.rgb( 0x91, 0x6A, 0x7B));
        put("Mulberry", Color.rgb( 0xC2, 0x1E, 0x56));
        put("Onyx", Color.rgb( 0x30, 0x30, 0x30));
        put("Periwinkle", Color.rgb( 0xC5, 0xC5, 0xFF));
        put("Raspberry", Color.rgb( 0xE3, 0x2A, 0x8C));
        put("Rosewood", Color.rgb( 0x65, 0x2E, 0x1F));
        put("ShamrockGreen", Color.rgb( 0x09, 0x75, 0x3B));
        put("SpicedApple", Color.rgb( 0xB4, 0x6A, 0x4C));
        put("Taffy", Color.rgb( 0xFF, 0x5F, 0x65));
        put("TurquoiseGreen", Color.rgb( 0x00, 0x9E, 0x86));
        put("VenetianRed", Color.rgb( 0xC0, 0x2D, 0x3C));
        put("Wisteria", Color.rgb( 0xB5, 0xA0, 0xD0));
        put("Zinfandel", Color.rgb( 0x5F, 0x3D, 0x4E));
    }};

    // Method to find the nearest color name
    public static String getColorName(int color) {
        int minDistance = Integer.MAX_VALUE;
        String closestColorName = null;

        // Extract RGB components from the color
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);

        // Iterate through the map and find the closest color
        for (String colorName : colorMap.keySet()) {
            int knownColor = colorMap.get(colorName);
            int knownRed = Color.red(knownColor);
            int knownGreen = Color.green(knownColor);
            int knownBlue = Color.blue(knownColor);

            // Calculate the Euclidean distance between the colors
            int distance = (int) Math.sqrt(Math.pow(red - knownRed, 2) + Math.pow(green - knownGreen, 2) + Math.pow(blue - knownBlue, 2));

            // Find the smallest distance and get the corresponding color name
            if (distance < minDistance) {
                minDistance = distance;
                closestColorName = colorName;
            }
        }

        return closestColorName != null ? closestColorName : "Unknown Color";
    }

}



