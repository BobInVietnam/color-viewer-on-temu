package com.example.colorviewerontemu.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.colorviewerontemu.databinding.FragmentHomeBinding;

import org.w3c.dom.Text;

import java.util.Timer;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private Timer timer;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textHome;
        homeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}