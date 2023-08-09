package com.example.savethatbridge.DragMeImpl;

import android.view.View;

import java.util.List;
import com.example.savethatbridge.DragMeImpl.Input.TouchEvent;

/**Classe che gestirà gli eventi touch. Android affiderà la gestione di un evento touch ad un oggeto di tal genere. Il metodo setOnTouchListener() registrerò un oggetto
 touchHandler come ascoltatore di tali eventi.**/
public interface TouchHandler extends View.OnTouchListener {
    public boolean isTouchDown(int pointer);

    public int getTouchX(int pointer);

    public int getTouchY(int pointer);

    public List<TouchEvent> getTouchEvents();
}
