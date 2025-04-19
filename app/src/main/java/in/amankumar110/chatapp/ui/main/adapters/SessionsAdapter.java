package in.amankumar110.chatapp.ui.main.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.databinding.BindingAdapter;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.qualifiers.ApplicationContext;
import in.amankumar110.chatapp.R;
import in.amankumar110.chatapp.databinding.ChatSessionItemLayoutBinding;
import in.amankumar110.chatapp.models.chat.ChatSession;

public class SessionsAdapter extends RecyclerView.Adapter<SessionsAdapter.SessionViewHolder> {

    public interface OnChatSessionClicked {
         void onClick(ChatSession chatSession);
    }

    private OnChatSessionClicked onChatSessionClicked;
    private List<ChatSession> chatSessionList;


    public SessionsAdapter() {
        this.chatSessionList = new ArrayList<>();
    }

    @NonNull
    @Override
    public SessionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SessionViewHolder(
        ChatSessionItemLayoutBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        ));
    }

    @Override
    public void onBindViewHolder(@NonNull SessionViewHolder holder, int position) {
        ChatSession chatSession = chatSessionList.get(position);
        holder.binding.setChatSession(chatSession);
        holder.binding.btnChat.setOnClickListener(v -> onChatSessionClicked.onClick(chatSession));
        holder.binding.getRoot().setOnClickListener(v -> onChatSessionClicked.onClick(chatSession));
    }

    @Override
    public int getItemCount() {
        return chatSessionList.size();
    }

    public OnChatSessionClicked getOnChatSessionClicked() {
        return onChatSessionClicked;
    }

    public void setOnChatSessionClicked(OnChatSessionClicked onChatSessionClicked) {
        this.onChatSessionClicked = onChatSessionClicked;
    }

    public List<ChatSession> getChatSessionList() {
        return chatSessionList;
    }

    public void setChatSessionList(List<ChatSession> chatSessionList) {
        this.chatSessionList = chatSessionList;
        notifyDataSetChanged();
    }

    public static class SessionViewHolder extends RecyclerView.ViewHolder {

        private final ChatSessionItemLayoutBinding binding;

        public SessionViewHolder(ChatSessionItemLayoutBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
