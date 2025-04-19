package in.amankumar110.chatapp.models.chat;

import android.content.Context;
import android.widget.TextView;

import androidx.databinding.BindingAdapter;

import in.amankumar110.chatapp.R;
import in.amankumar110.chatapp.utils.DateHelper;

public class BindingAdapters {

    @BindingAdapter("lastMessage")
    public static void setLastMessages(TextView view, String lastMessage) {
        Context context = view.getContext();

        if (lastMessage == null || lastMessage.trim().isEmpty())
            lastMessage = context.getString(R.string.no_last_message_text);

        view.setText(lastMessage);
    }

    @BindingAdapter("lastMessageTime")
    public static void setLastMessageTime(TextView view, Long lastMessageTime) {

        Context context = view.getContext();
        String lastMessageTimeText = context.getString(R.string.last_message_time_text);
        String formattedtime = DateHelper.getFormattedTime(lastMessageTime);
        view.setText(lastMessageTimeText + " " + formattedtime);
    }

}
