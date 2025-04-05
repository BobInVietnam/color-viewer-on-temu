package com.example.colorviewerontemu.ui.grading;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class GradingViewModel extends ViewModel {
    private final MutableLiveData<String> mText;

    public GradingViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is wtf fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}
