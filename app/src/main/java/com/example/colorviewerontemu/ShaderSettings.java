package com.example.colorviewerontemu;


public class ShaderSettings {
    private static ShaderSettings instance = null;
    private int shaderMode = 0; // 0: Normal, 1: Protanopia, 2: Deuteranopia, 3: Tritanopia
    private ShaderSettings() {} // Private constructor to prevent external instantiation


    public static ShaderSettings getInstance() {
        if (instance == null) {
            instance = new ShaderSettings();
        }
        return instance;
    }

    public int getShaderMode() {
        return shaderMode;
    }

    public void setShaderMode(int shaderMode) {
        this.shaderMode = shaderMode;
    }


}