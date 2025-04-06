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

public class CameraFilterRenderer implements GLSurfaceView.Renderer {
    private boolean shaderNeedsReload = false;

    private int shaderProgram = 0;
    private int textureId = 0;
    private Bitmap cameraFrame;
    private boolean textureLoaded = false;
    private float minHue = 0.0f;
    private float maxHue = 360.0f;

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



    public void setHueRange(float minHue, float maxHue) {
        this.minHue = minHue;
        this.maxHue = maxHue;
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

            reloadShader();

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

    private void reloadShader() {
        if (shaderProgram != 0) {
            GLES20.glDeleteProgram(shaderProgram);
        }
        int mode = ShaderSettings.getInstance().getShaderMode();

        if (mode == 1) {
            shaderProgram = createShaderProgram(vertexShaderCode, fragmentShaderCode1);
        } else if (mode == 2) {
            shaderProgram = createShaderProgram(vertexShaderCode, fragmentShaderCode2);
        } else if(mode == 3) {
            shaderProgram = createShaderProgram(vertexShaderCode, fragmentShaderCode3);
        } else {
            shaderProgram = createShaderProgram(vertexShaderCode, fragmentShaderCode);
        }


        GLES20.glUseProgram(shaderProgram); // Important: Use the new program
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {

        if(shaderNeedsReload) {
            reloadShader();
            shaderNeedsReload = false;
        }

        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        GLES20.glUseProgram(shaderProgram);
        GLES20.glUniform1f(GLES20.glGetUniformLocation(shaderProgram, "minHue"), minHue);
        GLES20.glUniform1f(GLES20.glGetUniformLocation(shaderProgram, "maxHue"), maxHue);

        int positionHandle = GLES20.glGetAttribLocation(shaderProgram, "position");
        GLES20.glEnableVertexAttribArray(positionHandle);
        GLES20.glVertexAttribPointer(positionHandle, 2, GLES20.GL_FLOAT, false, 0, vertexBuffer);

        int texCoordHandle = GLES20.glGetAttribLocation(shaderProgram, "texCoord");
        GLES20.glEnableVertexAttribArray(texCoordHandle);
        GLES20.glVertexAttribPointer(texCoordHandle, 2, GLES20.GL_FLOAT, false, 0, texCoordBuffer);

        if (cameraFrame != null && !textureLoaded) {
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, cameraFrame, 0);
            textureLoaded = true;
        }

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);

        GLES20.glDisableVertexAttribArray(positionHandle);
        GLES20.glDisableVertexAttribArray(texCoordHandle);
    }


    public void show() {
        shaderNeedsReload = true; // Signal for shader reload
    }

    public void release() {
        if (isInitialized) {
            GLES20.glDeleteTextures(1, new int[]{textureId}, 0);
            GLES20.glDeleteProgram(shaderProgram);
            isInitialized = false;
        }
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


    //Protanopia
    private final String fragmentShaderCode1 =
            "precision mediump float;" +
                    "uniform sampler2D texture;" +
                    "varying vec2 vTexCoord;" +
                    "void main() {" +
                    "  vec4 color = texture2D(texture, vTexCoord);" +
                    "  float r = color.r, g = color.g, b = color.b;" +
                    "  float maxVal = max(r, max(g, b));" +
                    "  float minVal = min(r, min(g, b));" +
                    "  float delta = maxVal - minVal;" +
                    "  float hue = 0.0;" +
                    "  float saturation = 0.0;" +
                    "  float brightness = maxVal;" +

                    // Tính toán Hue và Saturation
                    "  if (delta != 0.0) {" +
                    "    if (maxVal == r) hue = 60.0 * mod((g - b) / delta, 6.0);" +
                    "    else if (maxVal == g) hue = 60.0 * ((b - r) / delta + 2.0);" +
                    "    else hue = 60.0 * ((r - g) / delta + 4.0);" +
                    "    if (hue < 0.0) hue += 360.0;" +
                    "    saturation = delta / maxVal;" +
                    "  }" +

                    // Quy tắc chuyển đổi Hue với chỉnh sửa thêm
                    "  float newHue = hue;" +
                    "  if (hue >= 0.0 && hue < 5.0) {" +
                    "    newHue = 5.0; saturation *= 0.3; brightness *= 1.3;" + // Đỏ -> Hồng nhạt hơn nữa
                    "  } else if (hue >= 5.0 && hue < 10.0) {" +
                    "    newHue = 8.0; saturation *= 0.4; brightness *= 1.2;" +
                    "  } else if (hue >= 10.0 && hue < 15.0) {" +
                    "    newHue = 12.0; saturation *= 0.5; brightness *= 1.2;" +
                    "  } else if (hue >= 15.0 && hue < 20.0) {" +
                    "    newHue = 18.0; saturation *= 0.6; brightness *= 1.1;" +
                    "  } else if (hue >= 20.0 && hue < 30.0) {" +
                    "    newHue = 25.0; saturation *= 0.7;" +
                    "  } else if (hue >= 30.0 && hue < 60.0) {" +
                    "    newHue = mix(35.0, 50.0, (hue - 30.0) / 30.0); saturation *= 0.8;" +
                    "  } else if (hue >= 60.0 && hue < 90.0) {" +
                    "    newHue = mix(75.0, 85.0, (hue - 60.0) / 30.0); saturation *= 1.5; brightness *= 0.8;" + // Vàng -> Xanh lá sáng
                    "  } else if (hue >= 90.0 && hue < 150.0) {" +
                    "    newHue = mix(130.0, 140.0, (hue - 90.0) / 60.0); saturation *= 2.2; brightness *= 0.7;" + // Xanh lá -> Xanh đậm hơn
                    "  } else if (hue >= 150.0 && hue < 180.0) {" +
                    "    newHue = mix(170.0, 175.0, (hue - 150.0) / 30.0); saturation *= 2.0;" +
                    "  } else if (hue >= 180.0 && hue < 240.0) {" +
                    "    newHue = mix(210.0, 220.0, (hue - 180.0) / 60.0); brightness *= 0.75;" + // Xanh lam -> Xanh lam đậm
                    "  } else if (hue >= 240.0 && hue < 300.0) {" +
                    "    newHue = mix(270.0, 280.0, (hue - 240.0) / 60.0); brightness *= 0.9;" + // Tím -> Tím nhạt
                    "  } else if (hue >= 300.0 && hue < 360.0) {" +
                    "    newHue = mix(320.0, 330.0, (hue - 300.0) / 60.0); brightness *= 1.1;" + // Hồng -> Hồng sáng hơn
                    "  }" +

                    // Chuyển đổi từ HSL về RGB
                    "  float c = brightness * saturation;" +
                    "  float x = c * (1.0 - abs(mod(newHue / 60.0, 2.0) - 1.0));" +
                    "  float m = brightness - c;" +
                    "  float newR, newG, newB;" +
                    "  if (newHue >= 0.0 && newHue < 60.0) {" +
                    "    newR = c; newG = x; newB = 0.0;" +
                    "  } else if (newHue >= 60.0 && newHue < 120.0) {" +
                    "    newR = x; newG = c; newB = 0.0;" +
                    "  } else if (newHue >= 120.0 && newHue < 180.0) {" +
                    "    newR = 0.0; newG = c; newB = x;" +
                    "  } else if (newHue >= 180.0 && newHue < 240.0) {" +
                    "    newR = 0.0; newG = x; newB = c;" +
                    "  } else if (newHue >= 240.0 && newHue < 300.0) {" +
                    "    newR = x; newG = 0.0; newB = c;" +
                    "  } else {" +
                    "    newR = c; newG = 0.0; newB = x;" +
                    "  }" +
                    "  gl_FragColor = vec4(newR + m, newG + m, newB + m, color.a);" +
                    "}";

    //Deuteranopia
    private final String fragmentShaderCode2 =
            "precision mediump float;" +
                    "uniform sampler2D texture;" +
                    "varying vec2 vTexCoord;" +
                    "void main() {" +
                    "  vec4 color = texture2D(texture, vTexCoord);" +
                    "  float r = color.r, g = color.g, b = color.b;" +
                    "  float maxVal = max(r, max(g, b));" +
                    "  float minVal = min(r, min(g, b));" +
                    "  float delta = maxVal - minVal;" +
                    "  float hue = 0.0;" +
                    "  float saturation = 0.0;" +
                    "  float brightness = maxVal;" +

                    // Tính toán Hue và Saturation
                    "  if (delta != 0.0) {" +
                    "    if (maxVal == r) hue = 60.0 * mod((g - b) / delta, 6.0);" +
                    "    else if (maxVal == g) hue = 60.0 * ((b - r) / delta + 2.0);" +
                    "    else hue = 60.0 * ((r - g) / delta + 4.0);" +
                    "    if (hue < 0.0) hue += 360.0;" +
                    "    saturation = delta / maxVal;" +
                    "  }" +

                    // Quy tắc chuyển đổi Hue
                    "  float newHue = hue;" +
                    "  if (hue >= 0.0 && hue < 15.0) {" +
                    "    newHue = 5.0; saturation *= 0.4; brightness *= 1.3;" + // Đỏ -> Hồng nhạt hơn
                    "  } else if (hue >= 15.0 && hue < 30.0) {" +
                    "    newHue = 20.0; saturation *= 0.5; brightness *= 1.2;" + // Cam -> Cam nhạt
                    "  } else if (hue >= 30.0 && hue < 60.0) {" +
                    "    newHue = mix(40.0, 50.0, (hue - 30.0) / 30.0); saturation *= 0.8;" + // Vàng -> Vàng sáng
                    "  } else if (hue >= 60.0 && hue < 90.0) {" +
                    "    newHue = mix(80.0, 90.0, (hue - 60.0) / 30.0); saturation *= 1.2; brightness *= 0.9;" + // Xanh lá nhạt -> Xanh sáng hơn
                    "  } else if (hue >= 90.0 && hue < 150.0) {" +
                    "    newHue = mix(130.0, 140.0, (hue - 90.0) / 60.0); saturation *= 2.2; brightness *= 0.7;" + // Xanh lá đậm -> Xanh đậm hơn
                    "  } else if (hue >= 150.0 && hue < 180.0) {" +
                    "    newHue = mix(170.0, 175.0, (hue - 150.0) / 30.0); saturation *= 2.0;" + // Cyan nhạt -> Cyan đậm
                    "  } else if (hue >= 180.0 && hue < 240.0) {" +
                    "    newHue = mix(210.0, 220.0, (hue - 180.0) / 60.0); brightness *= 0.75;" + // Xanh lam -> Xanh lam đậm
                    "  } else if (hue >= 240.0 && hue < 300.0) {" +
                    "    newHue = mix(270.0, 280.0, (hue - 240.0) / 60.0); brightness *= 0.9;" + // Tím -> Tím nhạt
                    "  } else if (hue >= 300.0 && hue < 360.0) {" +
                    "    newHue = mix(320.0, 330.0, (hue - 300.0) / 60.0); brightness *= 1.1;" + // Hồng -> Hồng sáng hơn
                    "  }" +

                    // Chuyển đổi từ HSL về RGB
                    "  float c = brightness * saturation;" +
                    "  float x = c * (1.0 - abs(mod(newHue / 60.0, 2.0) - 1.0));" +
                    "  float m = brightness - c;" +
                    "  float newR, newG, newB;" +
                    "  if (newHue >= 0.0 && newHue < 60.0) {" +
                    "    newR = c; newG = x; newB = 0.0;" +
                    "  } else if (newHue >= 60.0 && newHue < 120.0) {" +
                    "    newR = x; newG = c; newB = 0.0;" +
                    "  } else if (newHue >= 120.0 && newHue < 180.0) {" +
                    "    newR = 0.0; newG = c; newB = x;" +
                    "  } else if (newHue >= 180.0 && newHue < 240.0) {" +
                    "    newR = 0.0; newG = x; newB = c;" +
                    "  } else if (newHue >= 240.0 && newHue < 300.0) {" +
                    "    newR = x; newG = 0.0; newB = c;" +
                    "  } else {" +
                    "    newR = c; newG = 0.0; newB = x;" +
                    "  }" +
                    "  gl_FragColor = vec4(newR + m, newG + m, newB + m, color.a);" +
                    "}";


    //Tritanopia
    private final String fragmentShaderCode3 =
            "precision mediump float;" +
                    "uniform sampler2D texture;" +
                    "varying vec2 vTexCoord;" +
                    "void main() {" +
                    "  vec4 color = texture2D(texture, vTexCoord);" +
                    "  float r = color.r, g = color.g, b = color.b;" +
                    "  float maxVal = max(r, max(g, b));" +
                    "  float minVal = min(r, min(g, b));" +
                    "  float delta = maxVal - minVal;" +
                    "  float hue = 0.0;" +
                    "  float saturation = 0.0;" +
                    "  float brightness = maxVal;" +

                    // Tính toán Hue và Saturation
                    "  if (delta != 0.0) {" +
                    "    if (maxVal == r) hue = 60.0 * mod((g - b) / delta, 6.0);" +
                    "    else if (maxVal == g) hue = 60.0 * ((b - r) / delta + 2.0);" +
                    "    else hue = 60.0 * ((r - g) / delta + 4.0);" +
                    "    if (hue < 0.0) hue += 360.0;" +
                    "    saturation = delta / maxVal;" +
                    "  }" +

                    // Quy tắc chuyển đổi Hue
                    "  float newHue = hue;" +
                    "  if (hue >= 0.0 && hue < 30.0) {" +
                    "    newHue = mix(270.0, 290.0, hue / 30.0); saturation *= 2.0; brightness *= 0.8;" + // Đỏ -> Tím đậm hơn
                    "  } else if (hue >= 60.0 && hue < 90.0) {" +
                    "    newHue = mix(90.0, 110.0, (hue - 60.0) / 30.0); saturation *= 1.4; brightness *= 0.9;" + // Vàng -> Xanh lá nhạt
                    "  } else if (hue >= 90.0 && hue < 120.0) {" +
                    "    newHue = mix(120.0, 140.0, (hue - 90.0) / 30.0); saturation *= 1.6; brightness *= 0.85;" + // Xanh biển nhạt -> Xanh lá
                    "  } else if (hue >= 120.0 && hue < 180.0) {" +
                    "    newHue = mix(140.0, 160.0, (hue - 120.0) / 60.0); saturation *= 1.7; brightness *= 0.8;" + // Xanh biển đậm -> Xanh lục đậm
                    "  } else if (hue >= 240.0 && hue < 270.0) {" +
                    "    newHue = mix(250.0, 270.0, (hue - 240.0) / 30.0); saturation *= 1.8; brightness *= 0.75;" + // Tím -> Tím đậm hơn nữa
                    "  } else if (hue >= 270.0 && hue < 300.0) {" +
                    "    newHue = mix(290.0, 310.0, (hue - 270.0) / 30.0); saturation *= 1.4; brightness *= 0.85;" + // Tím nhạt hơn
                    "  }" +

                    // Chuyển đổi từ HSL về RGB
                    "  float c = brightness * saturation;" +
                    "  float x = c * (1.0 - abs(mod(newHue / 60.0, 2.0) - 1.0));" +
                    "  float m = brightness - c;" +
                    "  float newR, newG, newB;" +
                    "  if (newHue >= 0.0 && newHue < 60.0) {" +
                    "    newR = c; newG = x; newB = 0.0;" +
                    "  } else if (newHue >= 60.0 && newHue < 120.0) {" +
                    "    newR = x; newG = c; newB = 0.0;" +
                    "  } else if (newHue >= 120.0 && newHue < 180.0) {" +
                    "    newR = 0.0; newG = c; newB = x;" +
                    "  } else if (newHue >= 180.0 && newHue < 240.0) {" +
                    "    newR = 0.0; newG = x; newB = c;" +
                    "  } else if (newHue >= 240.0 && newHue < 300.0) {" +
                    "    newR = x; newG = 0.0; newB = c;" +
                    "  } else {" +
                    "    newR = c; newG = 0.0; newB = x;" +
                    "  }" +
                    "  gl_FragColor = vec4(newR + m, newG + m, newB + m, color.a);" +
                    "}";


    private final String fragmentShaderCode =
            "precision mediump float;" +
                    "uniform sampler2D texture;" +
                    "varying vec2 vTexCoord;" +
                    "uniform float minHue;" +
                    "uniform float maxHue;" +
                    "void main() {" +
                    "  vec4 color = texture2D(texture, vTexCoord);" +

                    "    gl_FragColor = color;" +

                    "}";
}