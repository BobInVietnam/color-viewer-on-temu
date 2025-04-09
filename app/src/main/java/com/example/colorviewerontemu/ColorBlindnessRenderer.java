package com.example.colorviewerontemu;

import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class ColorBlindnessRenderer implements GLSurfaceView.Renderer {

    private int shaderProgram = 0;
    private int textureId = 0;
    private Bitmap cameraFrame;
    private boolean textureLoaded = false;
    private int colorBlindnessType = 0; // 0 = Normal, 1 = Protanopia, 2 = Deuteranopia, 3 = Tritanopia

    private boolean isInitialized = false;

    private FloatBuffer vertexBuffer;
    private FloatBuffer texCoordBuffer;

    private final float[] squareCoords = {
            -1.0f,  1.0f,   // Top left
            -1.0f, -1.0f,   // Bottom left
            1.0f,  1.0f,   // Top right
            1.0f, -1.0f    // Bottom right
    };

    private final float[] texCoords = {
            0.0f, 0.0f,     // Top left
            0.0f, 1.0f,     // Bottom left
            1.0f, 0.0f,     // Top right
            1.0f, 1.0f      // Bottom right
    };

    public void setColorBlindnessType(int type) {
        this.colorBlindnessType = type;  // Set the selected color blindness type
    }

    public void setCameraFrame(Bitmap frame, GLSurfaceView glSurfaceView) {
        this.cameraFrame = frame;
        textureLoaded = false;

        // Request render only after setting a new frame
        if (glSurfaceView != null) {
            glSurfaceView.requestRender();
        }
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        if (!isInitialized) {
            shaderProgram = createShaderProgram(vertexShaderCode, fragmentShaderCode);
            GLES20.glUseProgram(shaderProgram);

            int[] textures = new int[1];
            GLES20.glGenTextures(1, textures, 0);
            textureId = textures[0];

            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);

            // Prepare position and texture coordinate buffers only once
            vertexBuffer = ByteBuffer.allocateDirect(squareCoords.length * 4)
                    .order(ByteOrder.nativeOrder())
                    .asFloatBuffer();
            vertexBuffer.put(squareCoords).position(0);

            texCoordBuffer = ByteBuffer.allocateDirect(texCoords.length * 4)
                    .order(ByteOrder.nativeOrder())
                    .asFloatBuffer();
            texCoordBuffer.put(texCoords).position(0);

            textureLoaded = false;  // Ensure texture reload on surface recreation
            isInitialized = true;
        }
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);  // Set the OpenGL viewport to the new dimensions
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        GLES20.glUseProgram(shaderProgram);

        // Set the color blindness type
        GLES20.glUniform1i(GLES20.glGetUniformLocation(shaderProgram, "colorBlindnessType"), colorBlindnessType);

        // Ensure the camera frame texture is correctly loaded
        if (cameraFrame != null && !textureLoaded) {
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, cameraFrame, 0);
            textureLoaded = true;
        }

        // Setup vertex and texture coordinates
        int positionHandle = GLES20.glGetAttribLocation(shaderProgram, "position");
        GLES20.glEnableVertexAttribArray(positionHandle);
        GLES20.glVertexAttribPointer(positionHandle, 2, GLES20.GL_FLOAT, false, 0, vertexBuffer);

        int texCoordHandle = GLES20.glGetAttribLocation(shaderProgram, "texCoord");
        GLES20.glEnableVertexAttribArray(texCoordHandle);
        GLES20.glVertexAttribPointer(texCoordHandle, 2, GLES20.GL_FLOAT, false, 0, texCoordBuffer);

        // Draw the frame
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);

        // Disable attributes after drawing
        GLES20.glDisableVertexAttribArray(positionHandle);
        GLES20.glDisableVertexAttribArray(texCoordHandle);
    }


    private int createShaderProgram(String vertexCode, String fragmentCode) {
        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexCode);
        int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentCode);
        int program = GLES20.glCreateProgram();
        GLES20.glAttachShader(program, vertexShader);
        GLES20.glAttachShader(program, fragmentShader);
        GLES20.glLinkProgram(program);
        return program;
    }

    private int loadShader(int type, String shaderCode) {
        int shader = GLES20.glCreateShader(type);
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);
        return shader;
    }

    private final String vertexShaderCode =
            "attribute vec4 position;" +
                    "attribute vec2 texCoord;" +
                    "varying vec2 vTexCoord;" +
                    "void main() {" +
                    "  gl_Position = position;" +
                    "  vTexCoord = texCoord;" +
                    "}";


    private final String fragmentShaderCode =
            "precision mediump float;\n" +
                    "uniform sampler2D texture;\n" +
                    "varying vec2 vTexCoord;\n" +
                    "uniform int colorBlindnessType;\n" +

                    "// Convert from sRGB to linear RGB\n" +
                    "vec3 srgbToLinear(vec3 c) {\n" +
                    "    vec3 threshold = vec3(0.04045);\n" +
                    "    return step(c, threshold) * c / 12.92 + step(threshold, c) * pow((c + 0.055) / 1.055, vec3(2.4));\n" +
                    "}\n" +

                    "// Convert from linear RGB to sRGB\n" +
                    "vec3 linearToSrgb(vec3 c) {\n" +
                    "    vec3 threshold = vec3(0.0031308);\n" +
                    "    return step(c, threshold) * c * 12.92 + step(threshold, c) * (1.055 * pow(c, vec3(1.0 / 2.4)) - 0.055);\n" +
                    "}\n" +

                    "// Protanopia matrix\n" +
                    "mat3 protanopiaMatrix = mat3(\n" +
                    "    0.567, 0.433, 0.000,\n" +
                    "    0.558, 0.442, 0.000,\n" +
                    "    0.000, 0.242, 0.758\n" +
                    ");\n" +

                    "// Deuteranopia matrix\n" +
                    "mat3 deuteranopiaMatrix = mat3(\n" +
                    "    0.625, 0.375, 0.000,\n" +
                    "    0.800, 0.200, 0.000,\n" +
                    "    0.000, 0.300, 0.700\n" +
                    ");\n" +

                    "// Tritanopia matrix\n" +
                    "mat3 tritanopiaMatrix = mat3(\n" +
                    "    0.950, 0.050, 0.000,\n" +
                    "    0.000, 0.433, 0.567,\n" +
                    "    0.000, 0.475, 0.525\n" +
                    ");\n" +

                    "void main() {\n" +
                    "    // Sample the color from the texture\n" +
                    "    vec4 color = texture2D(texture, vTexCoord);\n" +

                    "    // Convert the color from sRGB to linear RGB\n" +
                    "    vec3 linearColor = srgbToLinear(color.rgb);\n" +

                    "    // Apply the color blindness transformation based on the selected type\n" +
                    "    if (colorBlindnessType == 1) {\n" +
                    "        linearColor = protanopiaMatrix * linearColor;\n" +
                    "    } else if (colorBlindnessType == 2) {\n" +
                    "        linearColor = deuteranopiaMatrix * linearColor;\n" +
                    "    } else if (colorBlindnessType == 3) {\n" +
                    "        linearColor = tritanopiaMatrix * linearColor;\n" +
                    "    }\n" +

                    "    // Convert the transformed color back to sRGB\n" +
                    "    gl_FragColor = vec4(linearToSrgb(linearColor), color.a);\n" +
                    "}";


    private final String colorCorrectionShaderCode =
            "precision mediump float;\n" +
                    "uniform sampler2D texture;\n" +
                    "varying vec2 vTexCoord;\n" +
                    "uniform int colorBlindnessType;\n" +

                    "// Remap for Protanopia (Red-blindness)\n" +
                    "vec3 remapProtanopia(vec3 color) {\n" +
                    "    // Color transformation for Protanopia (adjust red channel to make it distinguishable)\n" +
                    "    return vec3(color.r * 0.7 + color.g * 0.3, color.g, color.b);\n" +
                    "}\n" +

                    "// Remap for Deuteranopia (Green-blindness)\n" +
                    "vec3 remapDeuteranopia(vec3 color) {\n" +
                    "    // Color transformation for Deuteranopia (adjust green channel to make it distinguishable)\n" +
                    "    return vec3(color.r, color.g * 0.7 + color.b * 0.3, color.b);\n" +
                    "}\n" +

                    "// Remap for Tritanopia (Blue-yellow blindness)\n" +
                    "vec3 remapTritanopia(vec3 color) {\n" +
                    "    // Color transformation for Tritanopia (adjust blue channel to make it distinguishable)\n" +
                    "    return vec3(color.r * 0.7 + color.g * 0.3, color.g, color.b * 0.6);\n" +
                    "}\n" +

                    "void main() {\n" +
                    "    // Sample the color from the texture\n" +
                    "    vec4 color = texture2D(texture, vTexCoord);\n" +

                    "    // Apply the color remapping based on the selected color blindness type\n" +
                    "    if (colorBlindnessType == 1) {\n" +
                    "        color.rgb = remapProtanopia(color.rgb);\n" +
                    "    } else if (colorBlindnessType == 2) {\n" +
                    "        color.rgb = remapDeuteranopia(color.rgb);\n" +
                    "    } else if (colorBlindnessType == 3) {\n" +
                    "        color.rgb = remapTritanopia(color.rgb);\n" +
                    "    }\n" +

                    "    // Output the remapped color\n" +
                    "    gl_FragColor = color;\n" +
                    "}";

//    private final String fragmentShaderCode =
//            "precision mediump float;" +
//                    "uniform sampler2D texture;" +
//                    "varying vec2 vTexCoord;" +
//                    "uniform int colorBlindnessType;" +
//
//                    "void main() {" +
//                    "  vec4 color = texture2D(texture, vTexCoord);" +
//
//                    // Protanopia (Red Blindness) - Using the transformation matrix for Protanopia
//                    "  if (colorBlindnessType == 1) {" +
//                    "    float r = color.r * 0.567 + color.g * 0.433 + color.b * 0.000;" +  // Red and green transformed
//                    "    float g = color.r * 0.558 + color.g * 0.442 + color.b * 0.000;" +  // Red and green transformed
//                    "    float b = color.r * 0.000 + color.g * 0.242 + color.b * 0.758;" +  // Blue remains unaffected
//                    "    gl_FragColor = vec4(r, g, b, color.a);" +
//                    "  }" +
//
//                    // Deuteranopia (Green Blindness) - Using the transformation matrix for Deuteranopia
//                    "  else if (colorBlindnessType == 2) {" +
//                    "    float r = color.r * 0.625 + color.g * 0.375 + color.b * 0.000;" +  // Mixing red and green
//                    "    float g = color.r * 0.700 + color.g * 0.300 + color.b * 0.000;" +   // Green channel reduced and merged with red
//                    "    float b = color.r * 0.000 + color.g * 0.300 + color.b * 0.700;" +                            // Blue remains unaffected
//                    "    gl_FragColor = vec4(r, g, b, color.a);" +
//                    "  }" +
//
//                    // Tritanopia (Blue Blindness) - Using the transformation matrix for Tritanopia
//                    "  else if (colorBlindnessType == 3) {" +
//                    "    float r = color.r * 0.950 + color.g * 0.050 + color.b * 0.000;" + // Mixing red and blue
//                    "    float g = color.r * 0.000 + color.g * 0.433 + color.b * 0.567;" +                         // Green remains unaffected
//                    "    float b = color.r * 0.000 + color.g * 0.475 + color.b * 0.525;" + // Mixing red and blue
//                    "    gl_FragColor = vec4(r, g, b, color.a);" +
//                    "  }" +
//
//                    // Normal Vision (no color transformation)
//                    "  else {" +
//                    "    gl_FragColor = color;" +
//                    "  }" +
//                    "}";

}
