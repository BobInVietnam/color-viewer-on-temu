package com.example.colorviewerontemu.ui.detect;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.colorviewerontemu.CameraFilterRenderer;
import com.example.colorviewerontemu.ColorUtils;
import com.example.colorviewerontemu.ShaderSettings;
import com.example.colorviewerontemu.databinding.FragmentDetectBinding;
import com.google.common.util.concurrent.ListenableFuture;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;

public class DetectFragment extends Fragment {

    private static final int PERMISSION_REQUEST_CAMERA = 100;
    private CameraFilterRenderer cameraFilterRenderer;
    private GLSurfaceView glSurfaceView;
    private ProcessCameraProvider cameraProvider;
    private FragmentDetectBinding binding;
    private DetectViewModel detectViewModel;

    private Button normButton;
    private Button proButton;
    private Button deuButton;
    private Button triButton;

    boolean pr, de, tr;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        // Camera Permission
        if (ContextCompat.checkSelfPermission(this.requireContext(), Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this.requireActivity(),
                    new String[]{Manifest.permission.CAMERA},
                    PERMISSION_REQUEST_CAMERA);
        } else {
            startCameraX();
        }

        detectViewModel = new ViewModelProvider(this).get(DetectViewModel.class);

        binding = FragmentDetectBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textHome;
        detectViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        // Menu for color fixing filters. Normally hidden.
//        final RelativeLayout fixMenu = binding.fixMenu;
//        fixMenu.setVisibility(View.GONE);

        // Filter buttons
        normButton = binding.normal;
        proButton = binding.red;
        deuButton = binding.green;
        triButton = binding.blue;

        normButton.setOnClickListener(view -> {
            pr = false;
            de = false;
            tr = false;
            changeFilter();
        });
        proButton.setOnClickListener(view -> {
            pr = true;
            de = false;
            tr = false;
            changeFilter();
        });

        deuButton.setOnClickListener(view -> {
            pr = false;
            de = true;
            tr = false;
            changeFilter();
        });

        triButton.setOnClickListener(view -> {
            pr = false;
            de = false;
            tr = true;
            changeFilter();
        });

        changeFilter();

        // Button to turn on menu.
//        final Button toggleFixButton = binding.changeFilterButton;
//
//        toggleFixButton.setOnClickListener(view -> {
//            if (fixMenu.getVisibility() != View.GONE) {
//                fixMenu.setVisibility(View.GONE);
//                toggleFixButton.setText("Change Filter");
//            } else {
//                fixMenu.setVisibility(View.VISIBLE);
//                toggleFixButton.setText("Close");
//            }
//        });

        // Initialize GLSurfaceView and set renderer
        glSurfaceView = binding.glSurfaceView;
        glSurfaceView.setEGLContextClientVersion(2);
        glSurfaceView.setPreserveEGLContextOnPause(true);
        glSurfaceView.setEGLConfigChooser(8, 8, 8, 8, 16, 0);

        // Initialize the renderer
        cameraFilterRenderer = new CameraFilterRenderer();
        glSurfaceView.setRenderer(cameraFilterRenderer);
        glSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);

//        // Bring glSurfaceView to the front
        glSurfaceView.setZOrderOnTop(true);
        glSurfaceView.getHolder().setFormat(PixelFormat.TRANSLUCENT);
        glSurfaceView.bringToFront();

        glSurfaceView.setZOrderMediaOverlay(true);
        textView.bringToFront();
        final TextView centerPlus = binding.centerPlus;
        centerPlus.bringToFront();
        centerPlus.setX((int) (glSurfaceView.getWidth() / 2f - centerPlus.getWidth() / 2f));
        centerPlus.setY((int) (glSurfaceView.getHeight() / 2f - centerPlus.getHeight() / 2f));

        final TextView instruction = binding.textInstruction;
        instruction.bringToFront();
        ObjectAnimator fadeout = ObjectAnimator.ofFloat(instruction, "alpha", 1f, 0f);
        fadeout.setDuration(1000);
        fadeout.setStartDelay(3000);
        fadeout.start();

//        fixMenu.bringToFront();

        // Initial testing

//        final ToggleButton tbutton = binding.funButton;
//        tbutton.setOnClickListener(view -> {
//            if (tbutton.isChecked()) {
//                homeViewModel.increment();
//            }
//        });
//        final TextView counterTextView = binding.counterText;
//        homeViewModel.getCounterText().observe(getViewLifecycleOwner(), counterTextView::setText);
        return root;
    }
    private void changeFilter() {
        normButton.setSelected(!(pr||de||tr));
        proButton.setSelected(pr);
        deuButton.setSelected(de);
        triButton.setSelected(tr);
        if (pr) ShaderSettings.getInstance().setShaderMode(1);
        else if (de) ShaderSettings.getInstance().setShaderMode(2);
        else if(tr) ShaderSettings.getInstance().setShaderMode(3);
        else ShaderSettings.getInstance().setShaderMode(0);
        if(cameraFilterRenderer!=null) {
            cameraFilterRenderer.show();
            glSurfaceView.requestRender();
        }
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

        imageAnalysis.setAnalyzer(ContextCompat.getMainExecutor(this.getContext()), imageProxy -> {
            Bitmap bitmap = imageProxyToBitmap(imageProxy);
            cameraFilterRenderer.setCameraFrame(bitmap, glSurfaceView);
            detectColorAtCenter(imageProxy);
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


    private void detectColorAtCenter(ImageProxy imageProxy) {
        // Get image planes for Y, U, V channels
        ImageProxy.PlaneProxy[] planes = imageProxy.getPlanes();

        ByteBuffer yBuffer = planes[0].getBuffer();  // Y channel
        ByteBuffer uBuffer = planes[1].getBuffer();  // U channel
        ByteBuffer vBuffer = planes[2].getBuffer();  // V channel

        int width = imageProxy.getWidth();
        int height = imageProxy.getHeight();

        // Get the pixel in the center of the image
        int centerX = width / 2;
        int centerY = height / 2;

        // Calculate the pixel offset in the buffer for the center
        int yRowStride = planes[0].getRowStride();
        int uvRowStride = planes[1].getRowStride();  // U and V planes have the same stride
        int uvPixelStride = planes[1].getPixelStride();

        // Read Y, U, V values for the center pixel
        int yIndex = centerY * yRowStride + centerX;
        int uvIndex = (centerY / 2) * uvRowStride + (centerX / 2) * uvPixelStride;

        // Extract YUV values
        int y = yBuffer.get(yIndex) & 0xFF;  // Y is a byte, convert to unsigned
        int u = uBuffer.get(uvIndex) & 0xFF; // U is a byte, convert to unsigned
        int v = vBuffer.get(uvIndex) & 0xFF; // V is a byte, convert to unsigned

        // Convert YUV to RGB with refined formula (ITU-R BT.601)
        int r = (int) (y + 1.402 * (v - 128));
        int g = (int) (y - 0.344136 * (u - 128) - 0.714136 * (v - 128));
        int b = (int) (y + 1.772 * (u - 128));

        // Clamp RGB values to [0, 255]
        r = Math.max(0, Math.min(255, r));
        g = Math.max(0, Math.min(255, g));
        b = Math.max(0, Math.min(255, b));

        // Convert to a color integer
        int color = Color.rgb(r, g, b);

        // Get the color name using the custom method
        String colorName = ColorUtils.getColorName(color);

        // Display the color name in the TextView
        detectViewModel.setmText(colorName);

        Log.d("ColorDetection", "Detected color: " + colorName);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}