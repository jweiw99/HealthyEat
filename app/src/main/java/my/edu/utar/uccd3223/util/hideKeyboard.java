package my.edu.utar.uccd3223.util;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

public class hideKeyboard {

    public hideKeyboard(Context context, View view) {
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
