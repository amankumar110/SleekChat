package in.amankumar110.chatapp.ui.chat.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

import in.amankumar110.chatapp.R;
import in.amankumar110.chatapp.databinding.MessageLeftItemLayoutBinding;
import in.amankumar110.chatapp.databinding.MessageRightItemLayoutBinding;
import in.amankumar110.chatapp.databinding.NoMessagesItemLayoutBinding;
import in.amankumar110.chatapp.models.chat.Message;
import in.amankumar110.chatapp.ui.chat.ChatFragment;
import in.amankumar110.chatapp.ui.chat.fragments.MessageEditDialogFragment;
import in.amankumar110.chatapp.utils.DateHelper;

public class MessagesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Message> messageList;
    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;
    private static final int VIEW_TYPE_NO_MESSAGES = 3;
    private RecyclerView recyclerView;
    private final Fragment fragment;
    private final String sessionId;

    public MessagesAdapter(Fragment fragment, String sessionId) {
        this.fragment = fragment;
        this.sessionId = sessionId;
        messageList = new ArrayList<>();
    }

    @Override
    public int getItemViewType(int position) {
        if (messageList == null || messageList.isEmpty()) {
            return VIEW_TYPE_NO_MESSAGES;
        }

        Message message = messageList.get(position);

        return isMessageSent(message)
                ? VIEW_TYPE_MESSAGE_SENT
                : VIEW_TYPE_MESSAGE_RECEIVED;
    }


    private boolean isMessageSent(Message message) {

        String uid = FirebaseAuth.getInstance().getUid();
        return message.getSenderUId().equals(uid);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        if (viewType == VIEW_TYPE_MESSAGE_SENT) {

            return new MessageRightViewHolder(
                    MessageRightItemLayoutBinding.inflate(inflater, parent, false));
        }

        if(viewType == VIEW_TYPE_MESSAGE_RECEIVED) {

            return new MessageLeftViewHolder(
                    MessageLeftItemLayoutBinding.inflate(inflater, parent, false));

        }

        return new NoMessageViewHolder(
                NoMessagesItemLayoutBinding.inflate(inflater, parent, false));

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        // No Need to Bind NoMessageHolder
        if(holder instanceof NoMessageViewHolder)
            return;

        Message message = messageList.get(position);

        if(holder instanceof MessageLeftViewHolder)
            ((MessageLeftViewHolder) holder).bind(message);

        if(holder instanceof MessageRightViewHolder)
            ((MessageRightViewHolder) holder).bind(message);


    }

    @Override
    public int getItemCount() {
        return messageList.isEmpty() ? 1 : messageList.size();
    }


    public void setMessageList(List<Message> messageList) {
        this.messageList = messageList != null ? new ArrayList<>(messageList) : new ArrayList<>();
        notifyDataSetChanged();
        recyclerView.scrollToPosition(getItemCount()-1);
    }

    public void addMessage(Message message) {

        if (messageList == null) {
            messageList = new ArrayList<>();
        }

        messageList.add(message);
        notifyDataSetChanged();
        recyclerView.scrollToPosition(getItemCount()-1);
    }

    public boolean contains(Message message) {
        return messageList.stream().anyMatch(m -> m.getId().equals(message.getId()));
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        this.recyclerView = recyclerView;
    }

    public void markMessagesAsSeen() {
        messageList.stream().filter(this::isMessageSent) // Filtered Messages
                .forEach(message -> message.setSeen(true));
        notifyDataSetChanged();
    }

    public static class MessageLeftViewHolder extends RecyclerView.ViewHolder {

        private final MessageLeftItemLayoutBinding binding;

        public void bind(Message message) {

            String formattedTime = DateHelper.getFormattedTime(message.getSentAt());
            binding.vMessageItemLayout.tvMessage.setText(message.getMessage());
            binding.vMessageItemLayout.tvMessageTime.setText(formattedTime);
        }

        public MessageLeftViewHolder(MessageLeftItemLayoutBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public class MessageRightViewHolder extends RecyclerView.ViewHolder {

        private MessageRightItemLayoutBinding binding;

        public void bind(Message message) {

            String formattedTime = DateHelper.getFormattedTime(message.getSentAt());
            binding.vMessageItemLayout.tvMessage.setText(message.getMessage());
            binding.vMessageItemLayout.tvMessageTime.setText(formattedTime);

            Log.v("AdapterMessage", String.valueOf(message));
            if(message.isSeen())
                binding.ivUserStatus.setImageResource(R.drawable.ic_seen);
            else
                binding.ivUserStatus.setImageResource(R.drawable.ic_sent);

            binding.vMessageItemLayout.getRoot().setOnLongClickListener(view -> {

                MessageEditDialogFragment messageEditDialogFragment = MessageEditDialogFragment.getInstance(message,sessionId);
                messageEditDialogFragment.show(fragment.getChildFragmentManager(), "MessageEditDialogFragment");

                return false;
            });
        }

        public MessageRightViewHolder(MessageRightItemLayoutBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public static class NoMessageViewHolder extends RecyclerView.ViewHolder {

        private NoMessagesItemLayoutBinding binding;
        public NoMessageViewHolder(NoMessagesItemLayoutBinding binding) {
            super(binding.getRoot());
             this.binding = binding;
        }
    }
}
