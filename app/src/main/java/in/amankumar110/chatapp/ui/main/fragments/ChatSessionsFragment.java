package in.amankumar110.chatapp.ui.main.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import dagger.hilt.android.AndroidEntryPoint;
import in.amankumar110.chatapp.R;
import in.amankumar110.chatapp.databinding.FragmentChatSessionsBinding;
import in.amankumar110.chatapp.models.chat.ChatSession;
import in.amankumar110.chatapp.ui.chat.ChatFragment;
import in.amankumar110.chatapp.ui.main.adapters.SessionsAdapter;
import in.amankumar110.chatapp.utils.VerticalDividerItemDecoration;
import in.amankumar110.chatapp.viewmodels.chat.ChatSessionViewModel;

@AndroidEntryPoint
public class ChatSessionsFragment extends Fragment {

    private FragmentChatSessionsBinding binding;
    private ChatSessionViewModel viewModel;
    private List<ChatSession> sessions = new ArrayList<>();
    private SessionsAdapter sessionsAdapter;
    private NavController navController;


    public ChatSessionsFragment() {
        // Required empty public constructor
    }

    public static ChatSessionsFragment newInstance() {
        return new ChatSessionsFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(ChatSessionViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentChatSessionsBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        // Initializing Nav Controller
        navController = Navigation.findNavController(view);

        // Initializing recyclerview
        sessionsAdapter = new SessionsAdapter();
        binding.rvChatSessions.setAdapter(sessionsAdapter);
        VerticalDividerItemDecoration verticalDividerItemDecoration = new VerticalDividerItemDecoration(10,false);
        binding.rvChatSessions.addItemDecoration(verticalDividerItemDecoration);
        binding.rvChatSessions.setHasFixedSize(true);
        binding.rvChatSessions.setNestedScrollingEnabled(false);

        showLoader();
        getUserSessions();
        observeSessions();
    }

    private void getUserSessions() {

        binding.spinKit.setVisibility(View.VISIBLE);
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        viewModel.getSessions(uid);
    }

    private void showLoader() {
        binding.spinKit.setVisibility(View.VISIBLE);
        binding.tvNoUserFound.setVisibility(View.GONE);
        binding.rvChatSessions.setVisibility(View.GONE);
    }

    private void observeSessions() {

        viewModel.sessions.observe(getViewLifecycleOwner(), sessions -> {

            if (sessions == null)
                return;

            if (sessions.isEmpty()) {
                showNoSessionsMessage();
                return;
            }

            // Sort based on newest first message
            sessions.sort((s1, s2) -> Long.compare(s2.getLastMessageTime(), s1.getLastMessageTime()));

            this.sessions = sessions;
            showSessions(sessions);
        });
    }

    private void showSessions(List<ChatSession> sessions) {


        binding.spinKit.setVisibility(View.GONE);

        sessionsAdapter.setChatSessionList(sessions);

        sessionsAdapter.setOnChatSessionClicked(chatSession -> {
                showChat(chatSession);
        });

        binding.rvChatSessions.setVisibility(View.VISIBLE);
        binding.tvNoUserFound.setVisibility(View.GONE);
    }

    private void showNoSessionsMessage() {
        binding.rvChatSessions.setVisibility(View.GONE);
        binding.tvNoUserFound.setVisibility(View.VISIBLE);
        binding.spinKit.setVisibility(View.GONE);
    }

    public int filterUsersByPhoneNumber(String phoneNumber) {

        if(sessions==null)
            return -1;

        if(sessions.isEmpty())
            return -1;


        List<ChatSession> filteredSessions = sessions.stream()
                .filter(session -> session.getReceiverNumber().contains(phoneNumber))
                .collect(Collectors.toList());

        if(!filteredSessions.isEmpty())
            showSessions(filteredSessions);

        return filteredSessions.size();
    }

    private void showChat(ChatSession chatSession) {

        Bundle bundle = new Bundle();
        bundle.putSerializable(ChatFragment.ARG_CHAT_SESSION,chatSession);
        navController.navigate(R.id.action_mainFragment_to_chatFragment,bundle);
    }

}