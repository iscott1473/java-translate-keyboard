package info.iscott.keyboard4;

import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.os.AsyncTask;
import android.os.Handler;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputConnection;
import android.widget.Toast;

import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;
import com.google.cloud.translate.Translate;

public class MyInputMethodService extends InputMethodService implements KeyboardView.OnKeyboardActionListener {
    private static final String API_KEY = "AIzaSyComoqje0tLCGh-htXdfU2VJZLW_av2FZo";
    final Handler textViewHandler = new Handler();
    StringBuilder translateString = new StringBuilder();

    private boolean caps = false;
    private KeyboardView kv;
    private Keyboard keyboard;

    @Override
    public View onCreateInputView() {
        kv = (KeyboardView) getLayoutInflater().inflate(R.layout.keyboard_view, null);
        keyboard = new Keyboard(this, R.xml.number_pad);
        kv.setKeyboard(keyboard);
        kv.setOnKeyboardActionListener(this);
        return kv;
    }

    @Override
    public void onPress(int i) {

    }

    @Override
    public void onRelease(int i) {

    }

    @Override
    public void onKey(int primatyCode, int[] keyCodes) {
        InputConnection inputConnection = getCurrentInputConnection();
        if (inputConnection != null) {
            switch (primatyCode) {
                case Keyboard.KEYCODE_DELETE:
                    //CharSequence selectedText = inputConnection.getSelectedText(0);

                    if (/*TextUtils.isEmpty(selectedText)*/translateString.length() != 0) {
                        //inputConnection.deleteSurroundingText(1, 0);
                        translateString.deleteCharAt(translateString.length() - 1);
                    } else {
                        //inputConnection.commitText("", 1);
                    }
                    break;
                case Keyboard.KEYCODE_SHIFT:
                    caps = !caps;
                    keyboard.setShifted(caps);
                    kv.invalidateAllKeys();
                    break;
                case Keyboard.KEYCODE_DONE:
                    //inputConnection.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER));
                    this.translate(translateString.toString(), inputConnection);
                    break;

                default:
                    char code = (char) primatyCode;
                    //inputConnection.commitText(String.valueOf(code), 1);
                    translateString.append(code);
            }
        }
    }

    @Override
    public void onText(CharSequence charSequence) {

    }

    @Override
    public void swipeLeft() {

    }

    @Override
    public void swipeRight() {

    }

    @Override
    public void swipeDown() {

    }

    @Override
    public void swipeUp() {

    }

    public static void translate(String insertText, InputConnection ic) {
        new AsyncTask<Object, Void, Void>() {
            @Override
            protected Void doInBackground(Object... params) {
                String text = (String) params[0];
                InputConnection thisIC = (InputConnection) params[1];
                TranslateOptions options = TranslateOptions.newBuilder()
                        .setApiKey(API_KEY)
                        .build();
                Translate translate = options.getService();
                final Translation translation =
                        translate.translate(text,
                                Translate.TranslateOption.targetLanguage("zh-CN"));
                thisIC.commitText(translation.toString(), 1);
                return null;
                }
            }.execute(insertText, ic);
        }
    }