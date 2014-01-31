package com.hellotext.googleimagesearch;
import android.os.SystemClock;
import android.text.Editable;
import android.text.TextWatcher;

//http://stackoverflow.com/questions/16534369/avoid-button-multiple-rapid-clicks
public abstract class DebouncedTextWatcher implements TextWatcher {

    private final long minimumInterval;
    private long lastChanged;

    public abstract void onDebouncedTextChanged(CharSequence s, int start, int before, int count);

    public DebouncedTextWatcher(long minimumIntervalMsec) {
        this.minimumInterval = minimumIntervalMsec;
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        long currentTimestamp = SystemClock.uptimeMillis();
        if(lastChanged == 0 || (currentTimestamp - lastChanged > minimumInterval)) {
            lastChanged = currentTimestamp;
            onDebouncedTextChanged(s, start, before, count);
        }
    }
    
    @Override public void afterTextChanged(Editable s) {}
    @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
}