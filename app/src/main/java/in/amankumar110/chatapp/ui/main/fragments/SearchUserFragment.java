package in.amankumar110.chatapp.ui.main.fragments;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.firebase.auth.FirebaseAuth;
import dagger.hilt.android.AndroidEntryPoint;
import in.amankumar110.chatapp.R;
import in.amankumar110.chatapp.data.repository.ChatSessionRepositoryImpl;
import in.amankumar110.chatapp.databinding.FragmentSearchUserBinding;
import in.amankumar110.chatapp.domain.repository.ChatSessionRepository;
import in.amankumar110.chatapp.models.user.User;
import in.amankumar110.chatapp.ui.chat.ChatFragment;
import in.amankumar110.chatapp.utils.UiHelper;
import in.amankumar110.chatapp.viewmodels.chat.ChatSessionViewModel;
import in.amankumar110.chatapp.viewmodels.user.UserViewModel;

@AndroidEntryPoint
public class SearchUserFragment extends Fragment {

    private NavController navController;
    private FragmentSearchUserBinding binding;
    private ChatSessionViewModel viewModel;
    private UserViewModel userViewModel;
    private boolean isSessionCreated = false;
    private User user;
    private String myPhoneNumber;
    private String sessionId = null;


    public static SearchUserFragment newInstance() {

        return new SearchUserFragment();
    }

    public SearchUserFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewModel = new ViewModelProvider(this).get(ChatSessionViewModel.class);
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        isSessionCreated = savedInstanceState != null && savedInstanceState.getBoolean("isSessionCreated");

        myPhoneNumber = FirebaseAuth.getInstance().getCurrentUser() == null ?
                null :
                FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSearchUserBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        this.navController = Navigation.findNavController(view);

        binding.userItemLayout.btnAdd.setOnClickListener(view3 -> createSession());

        observeUserSearch();
        observeCreatedSession();
    }

    private void observeUserSearch() {

        userViewModel.userByPhoneNumber.observe(getViewLifecycleOwner(), user -> {

            if(user==null)
                return;

            binding.spinKit.setVisibility(View.GONE);

            if (user.isUserEmpty()) {
                showNoUserFoundMessage();
            } else {
                showUser(user);
            }
        });
    }

    private void observeCreatedSession() {

        viewModel.createdChatSession.observe(getViewLifecycleOwner(), createdSession -> {

            if(!viewModel.isIdle())
                return;

            if(createdSession!=null) {
                Log.v("fetchedSession",createdSession.getSessionId());
                Bundle bundle = new Bundle();
                bundle.putSerializable(ChatFragment.ARG_CHAT_SESSION, createdSession);
                navController.navigate(R.id.action_mainFragment_to_chatFragment, bundle);
            } else if(viewModel.errorMessage.getValue()!=null) {
                UiHelper.showMessage(requireContext(),R.string.session_creation_error);
            }

            viewModel.reset();
        });
    }

    public void searchUser(String receiverPhoneNumber) {

        binding.spinKit.setVisibility(View.VISIBLE);

        if (receiverPhoneNumber.isEmpty()) {
            UiHelper.showMessage(requireContext(), "Please enter a phone number");
            binding.spinKit.setVisibility(View.GONE);
            return;
        }

        if(receiverPhoneNumber.equals(myPhoneNumber)) {
            showNoUserFoundMessage();
            return;
        }

        userViewModel.getUserByPhoneNumber(receiverPhoneNumber);
    }

    private void showNoUserFoundMessage() {

        binding.userItemLayout.getRoot().setVisibility(View.GONE);
        binding.tvNoUserFound.setVisibility(View.VISIBLE);
        binding.spinKit.setVisibility(View.GONE);
    }

    private void showUser(User user) {
        this.user = user;
        binding.userItemLayout.userContainer.setVisibility(View.VISIBLE);
        binding.userItemLayout.setUser(user);
        binding.tvNoUserFound.setVisibility(View.GONE);
    }

    private void hideUser() {
        binding.userItemLayout.getRoot().setVisibility(View.GONE);
        binding.tvNoUserFound.setVisibility(View.GONE);
    }


    private void createSession() {

        if (isSessionCreated || user == null) return;

        String senderPhoneNumber = myPhoneNumber;

        if (senderPhoneNumber == null) {
            UiHelper.showMessage(requireContext(), "Authentication error. Please log in again.");
            return;
        }

        viewModel.createSession(senderPhoneNumber, user.getPhoneNumber());
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("isSessionCreated", isSessionCreated);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
