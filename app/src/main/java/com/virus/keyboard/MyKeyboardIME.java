package com.virus.keyboard;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.Keyboard;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.ExtractedText;
import android.view.inputmethod.ExtractedTextRequest;
import android.view.inputmethod.InputConnection;

import com.virus.keyboard.ui.view.KeyboardView;

public class MyKeyboardIME extends InputMethodService implements KeyboardView.OnKeyboardActionListener {
    private KeyboardView keyboardView;
    private Keyboard keyboard;

    private boolean isCaps;
    private boolean isCapsLock;
    private boolean isKeyboardLayoutIsEnglish = true;
    private boolean isKeyboardLayoutIsSymbols;
    private boolean isKeyboardSymbolsLayoutIs123 = true;
    private boolean isKeyboardLayoutIsTextField;
    private boolean isTextFieldSelected;

    @Override
    public View onCreateInputView(){
        keyboardView = (KeyboardView) getLayoutInflater().inflate(R.layout.keyboard_view, null);
        keyboard = new Keyboard(this, R.xml.qwerty_en);

        keyboardView.setKeyboard(keyboard);
        keyboardView.setOnKeyboardActionListener(this);

        //((android.view.inputmethod.InputMethodManager) getSystemService(android.content.Context.INPUT_METHOD_SERVICE)).showInputMethodPicker();

        return keyboardView;
    }

    @Override
    public void onPress(int primaryCode){
        keyboardView.setPreviewEnabled(primaryCode >= -0);
    }

    @Override
    public void onRelease(int primaryCode){
        keyboardView.setPreviewEnabled(false);
    }

    @Override
    public void onKey(int primaryCode, int[] keyCodes){
        InputConnection inputConnection = getCurrentInputConnection();
        ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ExtractedText extractedText;

        if(inputConnection != null){
            extractedText = inputConnection.getExtractedText(new ExtractedTextRequest(),0);

            switch(primaryCode){
                case Keyboard.KEYCODE_DELETE: //delete
                    if(inputConnection.getSelectedText(0) != null){
                        inputConnection.commitText("", 1);
                    }else{
                        inputConnection.deleteSurroundingText(1, 0);
                    }

                    isTextFieldSelected = false;
                    break;
                case Keyboard.KEYCODE_SHIFT: //caps
                    if(isKeyboardLayoutIsSymbols){
                        keyboard = new Keyboard(this, isKeyboardSymbolsLayoutIs123 ? R.xml.symbols_caps : R.xml.symbols);
                        isKeyboardSymbolsLayoutIs123 = !isKeyboardSymbolsLayoutIs123;
                        keyboardView.setKeyboard(keyboard);
                        break;
                    }

                    if(isCaps && !isCapsLock){
                        isCaps = false;
                        isCapsLock = true;

                        keyboard.setShifted(isCapsLock);
                    }else if(!isCaps && !isCapsLock){
                        isCaps = true;

                        keyboard.setShifted(isCaps);
                    }else{
                        isCaps = false;
                        isCapsLock = false;

                        keyboard.setShifted(isCaps);
                    }
                    keyboardView.invalidateAllKeys();
                    break;
                case Keyboard.KEYCODE_DONE: //enter
                    inputConnection.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER));
                    break;
                case -100: //EN - RU
                    keyboard = new Keyboard(this, isKeyboardLayoutIsEnglish ? R.xml.qwerty_ru : R.xml.qwerty_en);
                    isKeyboardLayoutIsEnglish = !isKeyboardLayoutIsEnglish;
                    if(isCaps || isCapsLock){
                        keyboard.setShifted(true);
                    }
                    keyboardView.setKeyboard(keyboard);
                    break;
                case -101: //123? - ABC
                    keyboard = new Keyboard(this, isKeyboardLayoutIsSymbols ? (isKeyboardLayoutIsEnglish ? R.xml.qwerty_en : R.xml.qwerty_ru) : R.xml.symbols);

                    isKeyboardLayoutIsSymbols = !isKeyboardLayoutIsSymbols;
                    isKeyboardSymbolsLayoutIs123 = true;

                    if(isCaps || isCapsLock) keyboard.setShifted(true);
                    keyboardView.setKeyboard(keyboard);
                    break;
                case -102: //clipboard_layout (layout)



                    inputConnection.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_SHIFT_LEFT));
                    break;
                case -103: case -116: //text_field (xml)
                    keyboard = new Keyboard(this, isKeyboardLayoutIsTextField ? (isKeyboardLayoutIsEnglish ? R.xml.qwerty_en : R.xml.qwerty_ru) : R.xml.text_field);

                    isKeyboardLayoutIsTextField = !isKeyboardLayoutIsTextField;

                    if(isCaps || isCapsLock) keyboard.setShifted(true);
                    keyboardView.setKeyboard(keyboard);

                    inputConnection.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_SHIFT_LEFT));
                    if(inputConnection.getSelectedText(0) != null) inputConnection.commitText(inputConnection.getSelectedText(0), 1);
                    break;
                case -110: //All
                    inputConnection.setSelection(0, extractedText.text.length());
                    break;
                case -111: //up arrow
                    inputConnection.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DPAD_UP));
                    break;
                case -112: //clear
                    inputConnection.setSelection(0, extractedText.text.length());
                    inputConnection.commitText("", 1);

                    inputConnection.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_SHIFT_LEFT));
                    break;
                case -113: //left arrow
                    if(extractedText.text == null) break;
                    if(inputConnection.getTextBeforeCursor(extractedText.text.length(), 0).length() != 0){
                        inputConnection.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DPAD_LEFT));
                    }
                    break;
                case -114: //select
                    if(isTextFieldSelected && inputConnection.getSelectedText(0) != null){
                        inputConnection.commitText(inputConnection.getSelectedText(0), 1);
                        inputConnection.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_SHIFT_LEFT));
                        isTextFieldSelected = false;
                        break;
                    }

                    inputConnection.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_SHIFT_LEFT));
                    isTextFieldSelected = true;
                    break;
                case -115: //right arrow
                    if(extractedText.text == null) break;
                    if(inputConnection.getTextAfterCursor(extractedText.text.length(), 0).length() != 0){
                        inputConnection.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DPAD_RIGHT));
                    }
                    break;
                case -117: //home
                    inputConnection.setSelection(0, 0);
                    break;
                case -118: //down arrow
                    inputConnection.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DPAD_DOWN));
                    break;
                case -119: //end
                    inputConnection.setSelection(extractedText.text.length(), extractedText.text.length());
                    break;
                case -120: //copy
                    if(inputConnection.getSelectedText(0) != null){
                        ClipData clip = ClipData.newPlainText("text", inputConnection.getSelectedText(0));
                        clipboardManager.setPrimaryClip(clip);

                        inputConnection.commitText(inputConnection.getSelectedText(0), 1);
                        inputConnection.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_SHIFT_LEFT));
                    }
                    break;
                case -121: //cut
                    if(inputConnection.getSelectedText(0) != null){
                        ClipData clip = ClipData.newPlainText("text", inputConnection.getSelectedText(0));
                        clipboardManager.setPrimaryClip(clip);

                        inputConnection.commitText("", 1);
                        inputConnection.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_SHIFT_LEFT));
                    }
                    break;
                case -122: //paste
                    ClipData clip = clipboardManager.getPrimaryClip();
                    if(clip.getItemAt(0) != null){
                        inputConnection.commitText(clip.getItemAt(0).getText().toString(), 1);
                        inputConnection.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_SHIFT_LEFT));
                    }
                    break;
                case -123: //tab
                    inputConnection.commitText("\t", 1);

                    inputConnection.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_SHIFT_LEFT));
                    break;
                default:
                    char code = (char) primaryCode;
                    if(Character.isLetter(code) && isCapsLock || isCaps) code = Character.toUpperCase(code);
                    inputConnection.commitText(String.valueOf(code), 1);

                    if(isCaps && !isCapsLock){
                        isCaps = false;
                        keyboard.setShifted(isCaps);
                        keyboardView.invalidateAllKeys();
                    }

                    if(primaryCode == 32) inputConnection.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_SHIFT_LEFT));
            }
        }
    }

    @Override
    public void onText(CharSequence text){

    }

    @Override
    public void swipeLeft(){

    }

    @Override
    public void swipeRight(){

    }

    @Override
    public void swipeDown(){

    }

    @Override
    public void swipeUp(){

    }
}