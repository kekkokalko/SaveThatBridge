package com.example.savethatbridge.DragMeImpl;

import java.util.ArrayList;
import java.util.List;

import android.view.MotionEvent;
import android.view.View;

import com.example.savethatbridge.DragMeImpl.Input.TouchEvent;
import com.example.savethatbridge.DragMeImpl.Pool;
import com.example.savethatbridge.DragMeImpl.Pool.PoolObjectFactory;

/**Classe usata per la gestione dei multipli eventi touch**/
public class MultiTouchHandler implements TouchHandler {
    boolean[] isTouching = new boolean[20];
    int[] touchX = new int[20];
    int[] touchY = new int[20];
    Pool<Input.TouchEvent> touchEventPool;
    List<TouchEvent> touchEvents = new ArrayList<TouchEvent>();
    List<TouchEvent> touchEventsBuffer = new ArrayList<TouchEvent>();
    float scaleX;
    float scaleY;
    private static final int MAXPOOLSIZE = 100;

    public MultiTouchHandler(View view, float scaleX, float scaleY) {
        PoolObjectFactory<TouchEvent> factory = new PoolObjectFactory<TouchEvent>() {
            @Override
            public TouchEvent createObject() {
                return new TouchEvent();
            }
        };
        touchEventPool = new Pool<TouchEvent>(factory, MAXPOOLSIZE);
        view.setOnTouchListener(this);

        this.scaleX = scaleX;
        this.scaleY = scaleY;
    }

    /**Metodo che effettua la conversione tra gli eventi Android MotionEvent in uno o più semplici touchEvent**/
    @Override
    public synchronized boolean onTouch(View v, MotionEvent event) {
        int action = event.getActionMasked();
        int pointerIndex = event.getActionIndex();
        int pointerId = event.getPointerId(pointerIndex);
        TouchEvent touchEvent;

        switch (action) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN:
                touchEvent = touchEventPool.newObject();
                touchEvent.type = TouchEvent.TOUCH_DOWN;
                touchEvent.pointer = pointerId;
                touchEvent.x = touchX[pointerId] = (int) (event
                        .getX(pointerIndex) * scaleX);
                touchEvent.y = touchY[pointerId] = (int) (event
                        .getY(pointerIndex) * scaleY);
                isTouching[pointerId] = true;
                touchEventsBuffer.add(touchEvent);
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
            case MotionEvent.ACTION_CANCEL:
                touchEvent = touchEventPool.newObject();
                touchEvent.type = TouchEvent.TOUCH_UP;
                touchEvent.pointer = pointerId;
                touchEvent.x = touchX[pointerId] = (int) (event
                        .getX(pointerIndex) * scaleX);
                touchEvent.y = touchY[pointerId] = (int) (event
                        .getY(pointerIndex) * scaleY);
                isTouching[pointerId] = false;
                touchEventsBuffer.add(touchEvent);
                break;

            case MotionEvent.ACTION_MOVE:
                int pointerCount = event.getPointerCount();
                for (int i = 0; i < pointerCount; i++) {
                    pointerIndex = i;
                    pointerId = event.getPointerId(pointerIndex);

                    touchEvent = touchEventPool.newObject();
                    touchEvent.type = TouchEvent.TOUCH_DRAGGED;
                    touchEvent.pointer = pointerId;
                    touchEvent.x = touchX[pointerId] = (int) (event
                            .getX(pointerIndex) * scaleX);
                    touchEvent.y = touchY[pointerId] = (int) (event
                            .getY(pointerIndex) * scaleY);
                    touchEventsBuffer.add(touchEvent);
                }
                break;
        }

        return true;
    }

    @Override
    public synchronized  boolean isTouchDown(int pointer) {
        if (pointer < 0 || pointer >= 20)
            return false;
        else
            return isTouching[pointer];
    }

    @Override
    public synchronized int getTouchX(int pointer) {
        if (pointer < 0 || pointer >= 20)
            return 0;
        else
            return touchX[pointer];
    }

    @Override
    public synchronized int getTouchY(int pointer) {
        if (pointer < 0 || pointer >= 20)
            return 0;
        else
            return touchY[pointer];
    }

    /**Metodo che usufruisce di 2 liste, efficiente in quanto non effettua continue allocazione per ogni evento;
     Usufruisce di 2 liste che vengono continuamente riciclate;
     Mette in un pool tutti gli eventi touch avvenuti;
     Svuota la lista vecchia degli eventi che sono stati già gestiti;
     Fa lo swap delle  liste;
     E' un metoto sincronizzato, impedisce a più chiamate che arrivano da diversi thread di rimanere bloccate o crashare
     **/
    @Override
    public synchronized List<TouchEvent> getTouchEvents() {
        // empty the old list and return the events to the pool
        for (TouchEvent event: touchEvents)
            touchEventPool.free(event);
        touchEvents.clear();

        // swap the lists
        List<TouchEvent> temp = touchEvents;
        touchEvents = touchEventsBuffer;
        touchEventsBuffer = temp;

        /* old:
        // copy the event buffer into the list
        touchEvents.addAll(touchEventsBuffer);
        touchEventsBuffer.clear();
        */

        return touchEvents;
    }
}
