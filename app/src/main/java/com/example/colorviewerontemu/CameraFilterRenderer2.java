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

public class CameraFilterRenderer2 implements GLSurfaceView.Renderer {

    private int shaderProgram = 0;
    private int textureId = 0;
    private Bitmap cameraFrame;
    private boolean textureLoaded = false;  // Flag to track texture loading
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
        GLES20.glViewport(0, 0, width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
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

    private final String fragmentShaderCode =
            "precision mediump float;" +
                    "uniform sampler2D texture;" +
                    "varying vec2 vTexCoord;" +
                    "uniform float minHue;" +
                    "uniform float maxHue;" +
                    "void main() {" +
                    "  vec4 color = texture2D(texture, vTexCoord);" +
                    "  float r = color.r, g = color.g, b = color.b;" +
                    "  float maxVal = max(r, max(g, b));" +
                    "  float minVal = min(r, min(g, b));" +
                    "  float delta = maxVal - minVal;" +
                    "  float hue = 0.0;" +
                    "  if (delta != 0.0) {" +
                    "    if (maxVal == r) hue = 60.0 * mod((g - b) / delta, 6.0);" +
                    "    else if (maxVal == g) hue = 60.0 * ((b - r) / delta + 2.0);" +
                    "    else hue = 60.0 * ((r - g) / delta + 4.0);" +
                    "    if (hue < 0.0) hue += 360.0;" +
                    "  }" +
                    "  if (hue >= minHue && hue <= maxHue) {" +
                    "    gl_FragColor = color;" +
                    "  } else {" +
                    "    float gray = (color.r + color.g + color.b) / 3.0;" +
                    "    gl_FragColor = vec4(vec3(gray), color.a);" +
                    "  }" +
                    "}";
}