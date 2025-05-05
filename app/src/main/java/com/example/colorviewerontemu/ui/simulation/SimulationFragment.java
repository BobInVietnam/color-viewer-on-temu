package com.example.colorviewerontemu.ui.simulation;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import com.example.colorviewerontemu.R;

import androidx.fragment.app.Fragment;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.example.colorviewerontemu.ColorBlindnessRenderer;
import com.example.colorviewerontemu.MainActivity;
import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetSequence;
import com.google.common.util.concurrent.ListenableFuture;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;

public class SimulationFragment extends Fragment {

    private GLSurfaceView glSurfaceView;
    private ColorBlindnessRenderer colorBlindnessRenderer;
    private ProcessCameraProvider cameraProvider;
    private PreviewView previewView;
    private ConstraintLayout navigation;
    private ConstraintLayout colorBlindnessType;
    private Button red, green, blue, normal;

    private TextView mimic;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_simulation, container, false);
        // Initialize UI components
        previewView = rootView.findViewById(R.id.pvPreview);
        glSurfaceView = rootView.findViewById(R.id.glSurfaceView);
        red = rootView.findViewById(R.id.red);
        green = rootView.findViewById(R.id.green);
        blue = rootView.findViewById(R.id.blue);
        normal = rootView.findViewById(R.id.normal);

        // Set up the Spinner for color blindness selection
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.color_blindness_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Initialize GLSurfaceView and set renderer
        glSurfaceView.setEGLContextClientVersion(2);
        glSurfaceView.setPreserveEGLContextOnPause(true);
        glSurfaceView.setEGLConfigChooser(8, 8, 8, 8, 16, 0);

        // Initialize the color blindness renderer
        colorBlindnessRenderer = new ColorBlindnessRenderer();
        glSurfaceView.setRenderer(colorBlindnessRenderer);
        glSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);

        // Bring glSurfaceView to the front
        glSurfaceView.setZOrderOnTop(true);
        glSurfaceView.getHolder().setFormat(PixelFormat.TRANSLUCENT);
        glSurfaceView.bringToFront();

        // Start CameraX after the renderer is initialized
        startCameraX();

        // Normal, Red, Green, Blue button listeners
        normal.setSelected(true);
        normal.setOnClickListener(v -> {
            normal.setSelected(true);
            red.setSelected(false);
            green.setSelected(false);
            blue.setSelected(false);
            colorBlindnessRenderer.setColorBlindnessType(0);
            glSurfaceView.requestRender();
        });

        red.setOnClickListener(v -> {
            normal.setSelected(false);
            red.setSelected(true);
            green.setSelected(false);
            blue.setSelected(false);
            colorBlindnessRenderer.setColorBlindnessType(1); // Protanopia
            glSurfaceView.requestRender();
        });

        green.setOnClickListener(v -> {
            normal.setSelected(false);
            red.setSelected(false);
            green.setSelected(true);
            blue.setSelected(false);
            colorBlindnessRenderer.setColorBlindnessType(2); // Deuteranopia
            glSurfaceView.requestRender();
        });

        blue.setOnClickListener(v -> {
            normal.setSelected(false);
            red.setSelected(false);
            green.setSelected(false);
            blue.setSelected(true);
            colorBlindnessRenderer.setColorBlindnessType(3); // Tritanopia
            glSurfaceView.requestRender();
        });

        // Initialize UI layouts
        navigation = rootView.findViewById(R.id.constraintLayout2);
        colorBlindnessType = rootView.findViewById(R.id.constraintLayout);
        navigation.bringToFront();
        colorBlindnessType.bringToFront();
        glSurfaceView.setZOrderMediaOverlay(true);

        return rootView;  // Return the inflated view
    }

    private void startTutorial(View back, View red, View green, View blue, View normal) {
        new TapTargetSequence(getActivity())  // Use getActivity() for Context
                .targets(
                        TapTarget.forView(back, "Back Button", "This button navigates back to the main activity.")
                                .outerCircleColor(R.color.choose_DRGB) // Custom color for this target
                                .outerCircleAlpha(0.96f)
                                .targetCircleColor(android.R.color.white)
                                .titleTextSize(20)
                                .descriptionTextSize(16)
                                .titleTextColor(android.R.color.white)
                                .descriptionTextColor(android.R.color.white)
                                .cancelable(true),
                        TapTarget.forView(normal, "Normal Colors", "Showing normal colors")
                                .outerCircleColor(R.color.white)
                                .outerCircleAlpha(0.96f)
                                .targetCircleColor(android.R.color.white)
                                .titleTextSize(20)
                                .descriptionTextSize(16)
                                .titleTextColor(android.R.color.white)
                                .descriptionTextColor(android.R.color.white)
                                .cancelable(true),
                        TapTarget.forView(red, "Red deficiency", "Simulating red deficiency (Protanopia)")
                                .outerCircleColor(R.color.red)
                                .outerCircleAlpha(0.96f)
                                .targetCircleColor(android.R.color.white)
                                .titleTextSize(20)
                                .descriptionTextSize(16)
                                .titleTextColor(android.R.color.white)
                                .descriptionTextColor(android.R.color.white)
                                .cancelable(true),
                        TapTarget.forView(green, "Green deficiency", "Simulating green deficiency (Deuteranopia)")
                                .outerCircleColor(R.color.green)
                                .outerCircleAlpha(0.96f)
                                .targetCircleColor(android.R.color.white)
                                .titleTextSize(20)
                                .descriptionTextSize(16)
                                .titleTextColor(android.R.color.white)
                                .descriptionTextColor(android.R.color.white)
                                .cancelable(true),
                        TapTarget.forView(blue, "Blue deficiency", "Simulating blue deficiency (Tritanopia)")
                                .outerCircleColor(R.color.blue)
                                .outerCircleAlpha(0.96f)
                                .targetCircleColor(android.R.color.white)
                                .titleTextSize(20)
                                .descriptionTextSize(16)
                                .titleTextColor(android.R.color.black)
                                .descriptionTextColor(android.R.color.white)
                                .cancelable(true)
                )
                .listener(new TapTargetSequence.Listener() {
                    @Override
                    public void onSequenceFinish() {
                        // Tutorial finished
                    }

                    @Override
                    public void onSequenceStep(TapTarget lastTarget, boolean targetClicked) {
                        // Each step of the tutorial
                    }

                    @Override
                    public void onSequenceCanceled(TapTarget lastTarget) {
                        // Tutorial canceled
                    }
                })
                .start();
    }

    private void startCameraX() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(getContext());
        cameraProviderFuture.addListener(() -> {
            try {
                cameraProvider = cameraProviderFuture.get();
                bindCamera(cameraProvider);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, ContextCompat.getMainExecutor(getContext()));
    }

    private void bindCamera(ProcessCameraProvider cameraProvider) {
        CameraSelector cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;
        Preview preview = new Preview.Builder().build();
        preview.setSurfaceProvider(previewView.getSurfaceProvider());

        ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build();

        imageAnalysis.setAnalyzer(ContextCompat.getMainExecutor(getContext()), imageProxy -> {
            Bitmap bitmap = imageProxyToBitmap(imageProxy);
            colorBlindnessRenderer.setCameraFrame(bitmap, glSurfaceView);
            imageProxy.close();
        });

        cameraProvider.unbindAll();
        cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalysis);
    }

    private Bitmap imageProxyToBitmap(ImageProxy image) {
        ImageProxy.PlaneProxy[] planes = image.getPlanes();
        ByteBuffer yBuffer = planes[0].getBuffer();
        ByteBuffer uBuffer = planes[1].getBuffer();
        ByteBuffer vBuffer = planes[2].getBuffer();

        int ySize = yBuffer.remaining();
        int uSize = uBuffer.remaining();
        int vSize = vBuffer.remaining();

        byte[] nv21 = new byte[ySize + uSize + vSize];
        yBuffer.get(nv21, 0, ySize);
        vBuffer.get(nv21, ySize, vSize);
        uBuffer.get(nv21, ySize + vSize, uSize);

        YuvImage yuvImage = new YuvImage(nv21, ImageFormat.NV21, image.getWidth(), image.getHeight(), null);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        yuvImage.compressToJpeg(new Rect(0, 0, image.getWidth(), image.getHeight()), 100, out);
        byte[] imageBytes = out.toByteArray();
        Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);

        return rotateBitmap(bitmap, image.getImageInfo().getRotationDegrees());
    }

    private Bitmap rotateBitmap(Bitmap bitmap, int rotationDegrees) {
        if (rotationDegrees == 0) return bitmap;

        Matrix matrix = new Matrix();
        matrix.postRotate(rotationDegrees);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }
}
