package in.amankumar110.chatapp.utils;

import static androidx.core.content.ContentProviderCompat.requireContext;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.text.method.ScrollingMovementMethod;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.StyleRes;

import in.amankumar110.chatapp.MainActivity;
import in.amankumar110.chatapp.R;

public class UiHelper {

    public static void hideKeyboard(Context context, IBinder focusableViewWindowToken) {

        //Closing Keyboard
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null)
            imm.hideSoftInputFromWindow(focusableViewWindowToken, 0);
    }

    // Todo: Implement a custom messaging view system
    public static void showMessage(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static void showMessage(Context context, int stringRes) {
        Toast.makeText(context, context.getString(stringRes), Toast.LENGTH_SHORT).show();
    }

    public static void setScrollingBarsBeyondMaxLines(EditText editText) {

        int maxLines = editText.getMaxLines();

        if(editText.getLineCount() > maxLines) {
            editText.setVerticalScrollBarEnabled(true);
            editText.setMovementMethod(new ScrollingMovementMethod());
        } else {
            editText.setVerticalScrollBarEnabled(false);
            editText.setMovementMethod(null);
        }
    }
    
    public static void setStatusBarColor(int colorRes, Resources.Theme theme, Window window) {
        TypedValue typedValue = new TypedValue();
        theme.resolveAttribute(colorRes, typedValue, true);
        window.setStatusBarColor(typedValue.data);}

    public static void showKeyboard(EditText editText) {
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            editText.requestFocus();
            InputMethodManager imm = (InputMethodManager) editText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
        }, 100);
    }
}
