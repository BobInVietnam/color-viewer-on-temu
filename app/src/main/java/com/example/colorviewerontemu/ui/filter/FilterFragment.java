package com.example.colorviewerontemu.ui.filter;

import android.animation.ObjectAnimator;
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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.colorviewerontemu.CameraFilterRenderer2;
import com.example.colorviewerontemu.databinding.FragmentFilterBinding;
import com.google.common.util.concurrent.ListenableFuture;
import it.mirko.rangeseekbar.OnRangeSeekBarListener;
import it.mirko.rangeseekbar.RangeSeekBar;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;

public class FilterFragment extends Fragment {

    RangeSeekBar rangeSeekBar;
    int str = 0;
    int ed = 360;
    private CameraFilterRenderer2 cameraFilterRenderer;
    private ProcessCameraProvider cameraProvider;
    private GLSurfaceView glSurfaceView;
    private FragmentFilterBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        FilterViewModel filterViewModel =
                new ViewModelProvider(this).get(FilterViewModel.class);

        binding = FragmentFilterBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        rangeSeekBar = binding.rangeSeekBar;
        rangeSeekBar.setStartProgress(0);
        rangeSeekBar.setEndProgress(360);
        rangeSeekBar.setMax(360);
        rangeSeekBar.setMinDifference(40);
        rangeSeekBar.setOnRangeSeekBarListener(new OnRangeSeekBarListener() {
            @Override
            public void onRangeValues(RangeSeekBar rangeSeekBar, int start, int end) {
                str = start;
                ed = end;
                cameraFilterRenderer.setHueRange(str, ed); // Pass hue range to renderer
            }
        });

        // Initialize GLSurfaceView and set renderer
        glSurfaceView = binding.glSurfaceView;
        glSurfaceView.setEGLContextClientVersion(2);
        glSurfaceView.setPreserveEGLContextOnPause(true); // Keep EGL context on pause
        glSurfaceView.setEGLConfigChooser(8, 8, 8, 8, 16, 0);

        // Initialize the renderer
        cameraFilterRenderer = new CameraFilterRenderer2();
        glSurfaceView.setRenderer(cameraFilterRenderer);
        glSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);

        // Bring glSurfaceView to the front
        glSurfaceView.setZOrderOnTop(true); // This ensures GLSurfaceView is on top
        glSurfaceView.getHolder().setFormat(PixelFormat.TRANSLUCENT);
        glSurfaceView.bringToFront();
        glSurfaceView.setZOrderMediaOverlay(true);

        final TextView instruction = binding.textInstruction;
        instruction.bringToFront();
        ObjectAnimator fadeout = ObjectAnimator.ofFloat(instruction, "alpha", 1f, 0f);
        fadeout.setDuration(1000);
        fadeout.setStartDelay(3000);
        fadeout.start();

        // Start CameraX after the renderer is initialized
        startCameraX();
        return root;
    }

    private void startCameraX() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this.requireContext());
        cameraProviderFuture.addListener(() -> {
            try {
                cameraProvider = cameraProviderFuture.get();
                bindCamera(cameraProvider);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, ContextCompat.getMainExecutor(this.requireContext()));
    }

    private void bindCamera(ProcessCameraProvider cameraProvider) {
        CameraSelector cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;
        Preview preview = new Preview.Builder().build();
        PreviewView pv = binding.pvPreview;
        preview.setSurfaceProvider(pv.getSurfaceProvider());

        ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build();

        imageAnalysis.setAnalyzer(ContextCompat.getMainExecutor(this.requireContext()), imageProxy -> {
            Bitmap bitmap = imageProxyToBitmap(imageProxy);
            cameraFilterRenderer.setCameraFrame(bitmap, glSurfaceView);
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

        // U and V are swapped
        yBuffer.get(nv21, 0, ySize);
        vBuffer.get(nv21, ySize, vSize);
        uBuffer.get(nv21, ySize + vSize, uSize);

        YuvImage yuvImage = new YuvImage(nv21, ImageFormat.NV21, image.getWidth(), image.getHeight(), null);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        yuvImage.compressToJpeg(new Rect(0, 0, image.getWidth(), image.getHeight()), 100, out);
        byte[] imageBytes = out.toByteArray();
        Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);

        // Rotate the bitmap to match the display orientation
        return rotateBitmap(bitmap, image.getImageInfo().getRotationDegrees());
    }

    private Bitmap rotateBitmap(Bitmap bitmap, int rotationDegrees) {
        if (rotationDegrees == 0) return bitmap;

        Matrix matrix = new Matrix();
        matrix.postRotate(rotationDegrees);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}