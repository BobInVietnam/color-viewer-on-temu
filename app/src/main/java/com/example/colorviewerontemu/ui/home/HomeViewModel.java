package com.example.colorviewerontemu.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class HomeViewModel extends ViewModel {

    private final MutableLiveData<String> mText;
    private int counter = 0;
    private MutableLiveData<String> counterText;
    public HomeViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is home fragment");
//        counterText = new MutableLiveData<>();
//        counterText.setValue("Counter = " + counter);
    }


    public LiveData<String> getText() {
        return mText;
    }
//    public void increment() {
//        counter++;
//        counterText.setValue("Counter = " + counter);
//    }
//    public LiveData<String> getCounterText() { return counterText;}
}