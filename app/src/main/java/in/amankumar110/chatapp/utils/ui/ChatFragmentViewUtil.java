package in.amankumar110.chatapp.utils.ui;

import static androidx.core.content.ContentProviderCompat.requireContext;

import android.content.Context;
import android.os.Parcelable;
import android.util.Log;

import in.amankumar110.chatapp.databinding.FragmentChatBinding;
import in.amankumar110.chatapp.models.user.UserStatus;
import in.amankumar110.chatapp.utils.UiHelper;

public class ChatFragmentViewUtil {

    public static void clearMessageField(Context context, FragmentChatBinding binding) {


        binding.chatBottomBarLayout.etMessage.setText("");
        binding.chatBottomBarLayout.etMessage.clearFocus();
        UiHelper.hideKeyboard(context, binding.chatBottomBarLayout.etMessage.getWindowToken());
    }

    public static void restoreScrollIfPossible(Parcelable recyclerViewState, FragmentChatBinding binding) {
        if (recyclerViewState != null && binding.rvMessages.getLayoutManager() != null) {
            // Ensure RecyclerView has been laid out before restoring scroll position
            binding.rvMessages.post(() -> {
                binding.rvMessages.getLayoutManager().onRestoreInstanceState(recyclerViewState);
            });
        }
    }

    public static boolean isOnline(String status) {
        return UserStatus.Status.ONLINE.getName().equals(status);
    }

    public static boolean isOffline(String status) {
        return UserStatus.Status.OFFLINE.getName().equals(status);
    }

    public static boolean isInChat(String status) {
        return UserStatus.Status.IN_CHAT.getName().equals(status);
    }
}
