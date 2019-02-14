package com.sunchenge.clientapp;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

public class ControlButton extends ImageView {
    private int originalX = 0;
    private int originalY = 0;
    private boolean initialized = false;
    public ControlButton(Context context) {
        super(context);
    }

    public ControlButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ControlButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public int getOriginalX() {
        return originalX;
    }

    public int getOriginalY() {
        return originalY;
    }

    public void draw(int x, int y, int max) {
        if (!initialized) {
            originalX = (int) this.getX() + this.getWidth()/2;
            originalY = (int) this.getY() + this.getHeight()/2;
            initialized = true;
        }
        if (Math.sqrt(Math.pow(x-originalX, 2) + Math.pow(y-originalY, 2)) <= max / 2 - this.getWidth() / 2) {
            this.setFrame(x - this.getWidth() / 2, y - this.getHeight() / 2, x + this.getWidth() / 2, y + this.getHeight() / 2);
        }
        else {
            int newX = originalX + (int) ((x - originalX) * (max / 2 - this.getWidth() / 2) / Math.sqrt(Math.pow(x-originalX, 2) + Math.pow(y-originalY, 2)));
            int newY = originalY + (int) ((y - originalY) * (max / 2 - this.getWidth() / 2) / Math.sqrt(Math.pow(x-originalX, 2) + Math.pow(y-originalY, 2)));
            this.setFrame(newX - this.getWidth() / 2, newY - this.getHeight() / 2, newX + this.getWidth() / 2, newY + this.getHeight() / 2);
        }
    }

    public void recover() {
        draw(originalX, originalY, this.getWidth());
    }
}
