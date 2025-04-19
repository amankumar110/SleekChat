package in.amankumar110.chatapp.ui.chat.fragments;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.gson.Gson;

import dagger.hilt.android.AndroidEntryPoint;
import in.amankumar110.chatapp.R;
import in.amankumar110.chatapp.databinding.FragmentMessageEditBinding;
import in.amankumar110.chatapp.models.chat.Message;
import in.amankumar110.chatapp.utils.AnimationUtil;
import in.amankumar110.chatapp.utils.UiHelper;
import in.amankumar110.chatapp.viewmodels.realtimemessage.RealtimeMessageViewModel;

@AndroidEntryPoint
public class MessageEditDialogFragment extends DialogFragment {

    private static final String ARG_SESSION_ID = "sessionid";
    private FragmentMessageEditBinding binding;
    private Message message;
    private RealtimeMessageViewModel realtimeMessageViewModel;
    public static final String ARG_MESSAGE = "message";
    private String sessionId = null;


    public MessageEditDialogFragment() {
        // Required empty constructor
    }

    public static MessageEditDialogFragment getInstance(Message message,String sessionId) {
        MessageEditDialogFragment fragment = new MessageEditDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ARG_SESSION_ID, sessionId);
        bundle.putString(ARG_MESSAGE, new Gson().toJson(message));
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Theme_ChatApp);

        if (getArguments() != null) {
            String messageJson = getArguments().getString(ARG_MESSAGE);
            message = new Gson().fromJson(messageJson, Message.class);
            this.sessionId = getArguments().getString(ARG_SESSION_ID);
        }

  }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentMessageEditBinding.inflate(inflater, container, false);

        if (message != null) {
            binding.vMessageEditItemLayout.etEditMessage.setText(message.getMessage()); // Assuming `getText()` exists
            binding.vMessageEditItemLayout.etEditMessage.setSelection(binding.vMessageEditItemLayout.etEditMessage.getText().length());
        }

        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();

        Dialog dialog = getDialog();
        if (dialog != null && dialog.getWindow() != null) {
            dialog.getWindow().setLayout(MATCH_PARENT, MATCH_PARENT);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        realtimeMessageViewModel = new ViewModelProvider(requireActivity()).get(RealtimeMessageViewModel.class);

        binding.getRoot().setAlpha(0f);
        AnimationUtil.fadeIn(binding.getRoot(),()-> UiHelper.showKeyboard(binding.vMessageEditItemLayout.etEditMessage));

        binding.vMessageEditItemLayout.btnSaveChanges.setOnClickListener(view1 -> {

            String newMessage = binding.vMessageEditItemLayout.etEditMessage.getText().toString().trim();

            // Validate message
            if (newMessage.isEmpty()) {
                UiHelper.showMessage(requireContext(), R.string.empty_text_warning);
                return;
            }

            if(newMessage.equals(message.getMessage().trim())) {
                dismiss();
                return;
            }
            // Send if everything is right
            updateMessage(newMessage);
            UiHelper.hideKeyboard(requireContext(),binding.vMessageEditItemLayout.etEditMessage.getWindowToken());

        });

        observeMessageUpdate();
    }

    private void observeMessageUpdate() {

        if(!isAdded())
            return;

        realtimeMessageViewModel.isMessageUpdated.observe(requireActivity(),isUpdated->{

            Log.v("isUpdated",isUpdated+"");

            Log.v("isUpdated&Idle",realtimeMessageViewModel.isIdle()+"");

            if(!realtimeMessageViewModel.isIdle() || isUpdated==null)
                return;

            dismiss();

            if(isUpdated)
                UiHelper.showMessage(requireActivity(),R.string.message_updated);
            else
                UiHelper.showMessage(requireActivity(),R.string.message_update_failed);

            realtimeMessageViewModel.consumeMessageUpdate();
        });
    }

    private void updateMessage(String messageText) {
        message.setMessage(messageText);
        realtimeMessageViewModel.updateMessage(message,sessionId);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Make sure to remove observers to prevent memory leaks or unnecessary updates
        if (realtimeMessageViewModel.isMessageUpdated.hasObservers()) {
            realtimeMessageViewModel.isMessageUpdated.removeObservers(requireActivity());
        }
        AnimationUtil.fadeOut(binding.getRoot(), this::dismiss);
    }

}
