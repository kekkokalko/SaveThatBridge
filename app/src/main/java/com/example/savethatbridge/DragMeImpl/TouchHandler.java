package com.example.savethatbridge.DragMeImpl;

import android.view.View;

import java.util.List;
import com.example.savethatbridge.DragMeImpl.Input.TouchEvent;

public interface TouchHandler extends View.OnTouchListener {
    public boolean isTouchDown(int pointer);

    public int getTouchX(int pointer);

    public int getTouchY(int pointer);

    public List<TouchEvent> getTouchEvents();
}
