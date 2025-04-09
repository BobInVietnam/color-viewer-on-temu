package com.example.colorviewerontemu.ui.grading;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.colorviewerontemu.databinding.FragmentGradingBinding;

public class GradingFragment extends Fragment {
    private FragmentGradingBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        GradingViewModel gradingViewModel =
                new ViewModelProvider(this).get(GradingViewModel.class);

        binding = FragmentGradingBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textGrading;
        gradingViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
