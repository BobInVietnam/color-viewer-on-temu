package com.example.colorviewerontemu.ui.detect;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class DetectViewModel extends ViewModel {

    private final MutableLiveData<String> mText;
    private int counter = 0;
    private MutableLiveData<String> counterText;
    public DetectViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is home fragment");
//        counterText = new MutableLiveData<>();
//        counterText.setValue("Counter = " + counter);
    }

    public void setmText(String s) { mText.setValue(s);}
    public LiveData<String> getText() {
        return mText;
    }
//    public void increment() {
//        counter++;
//        counterText.setValue("Counter = " + counter);
//    }
//    public LiveData<String> getCounterText() { return counterText;}
}