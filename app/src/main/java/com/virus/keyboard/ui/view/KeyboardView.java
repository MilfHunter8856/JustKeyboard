package com.virus.keyboard.ui.view;

import android.content.Context;
import android.graphics.*;
import android.graphics.drawable.Drawable;
import android.inputmethodservice.Keyboard.Key;
import android.util.AttributeSet;
import com.virus.keyboard.R;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.List;

public class KeyboardView extends android.inputmethodservice.KeyboardView {
    public KeyboardView(Context context, AttributeSet attrs){
        super(context, attrs);
    }

    public KeyboardView(Context context, AttributeSet attrs, int defStyleAttr){
        super(context, attrs, defStyleAttr);
    }

    public KeyboardView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes){
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void onDraw(Canvas canvas){
        super.onDraw(canvas);

        List<Key> keys = getKeyboard().getKeys();
        for(Key key : keys){
            if(key.codes[0] == -102 || key.codes[0] == -103){ // toolbar keys (102 - clipboard, -103 - text field)
                drawBackground(canvas, key, R.drawable.toolbar_key_background);
                drawIcon(canvas, key);
            }
            if(key.codes[0] == -114 && key.on){ // sticky keys (-114 - select)

            }
        }
    }

    private void drawBackground(Canvas canvas, @NotNull Key key, int drawableId){
        Drawable drawable = getContext().getResources().getDrawable(drawableId);
        drawable.setState(key.getCurrentDrawableState());
        drawable.setBounds(key.x, key.y, key.x + key.width, key.y + key.height);
        drawable.draw(canvas);
    }

    private void drawIcon(Canvas canvas, @NotNull Key key){
        key.icon.setBounds(key.x + (key.width - key.icon.getIntrinsicWidth()) / 2, key.y + (key.height - key.icon.getIntrinsicHeight()) / 2, key.x + (key.width - key.icon.getIntrinsicWidth()) / 2 + key.icon.getIntrinsicWidth(), key.y + (key.height - key.icon.getIntrinsicHeight()) / 2 + key.icon.getIntrinsicHeight());
        key.icon.draw(canvas);
    }

    /*private void drawText(Canvas canvas, Key key){
        Rect bounds = new Rect();
        Paint paint = new Paint();
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setAntiAlias(true);
        paint.setColor(Color.WHITE);

        if(key.label != null){
            String label = key.label.toString();

            try{
                Field field = android.inputmethodservice.KeyboardView.class.getDeclaredField("mLabelTextSize");
                field.setAccessible(true);
                paint.setTextSize((int) field.get(this));
            }catch(NoSuchFieldException | IllegalAccessException e){
                e.printStackTrace();
            }

            if(label.length() > 1 && key.codes.length < 2){
                paint.setTypeface(Typeface.DEFAULT_BOLD);
            }else{
                paint.setTypeface(Typeface.DEFAULT);
            }

            paint.getTextBounds(label, 0, label.length(), bounds);
            canvas.drawText(label, key.x + (float) (key.width / 2), (key.y + (float) key.height / 2) + (float) (bounds.height() / 2), paint);
        }else if(key.icon != null){
            key.icon.setBounds(key.x + (key.width - key.icon.getIntrinsicWidth()) / 2, key.y + (key.height - key.icon.getIntrinsicHeight()) / 2, key.x + (key.width - key.icon.getIntrinsicWidth()) / 2 + key.icon.getIntrinsicWidth(), key.y + (key.height - key.icon.getIntrinsicHeight()) / 2 + key.icon.getIntrinsicHeight());
            key.icon.draw(canvas);
        }
    }*/
}
