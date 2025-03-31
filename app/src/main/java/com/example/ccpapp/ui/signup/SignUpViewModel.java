package com.example.ccpapp.ui.signup;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SignUpViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public SignUpViewModel(){
        mText = new MutableLiveData<>();
        mText.setValue("This is a Login Fragment");
    }

    public LiveData<String> getText(){
        return mText;
    }
}
