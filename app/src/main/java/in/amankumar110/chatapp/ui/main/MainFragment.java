package in.amankumar110.chatapp.ui.main;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import in.amankumar110.chatapp.R;
import in.amankumar110.chatapp.databinding.FragmentMainBinding;
import in.amankumar110.chatapp.ui.main.fragments.ChatSessionsFragment;
import in.amankumar110.chatapp.ui.main.fragments.SearchUserFragment;
import in.amankumar110.chatapp.utils.AnimationUtil;
import in.amankumar110.chatapp.utils.CountryCodeUtil;
import in.amankumar110.chatapp.utils.UiHelper;

public class MainFragment extends Fragment {

    private FragmentMainBinding binding;
    private SearchUserFragment searchUserFragment;
    private ChatSessionsFragment chatSessionsFragment;
    private String lastSearchedPhoneNumber;
    private static final int MIN_PHONE_LENGTH = 12;
    private static final int DEBOUNCE_DELAY = 300;
    private Runnable searchRunnable;
    private Handler searchHandler = new Handler();

    public MainFragment() {
        // Required empty constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentMainBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        searchUserFragment = SearchUserFragment.newInstance();
        chatSessionsFragment = ChatSessionsFragment.newInstance();

        getChildFragmentManager().beginTransaction()
                .replace(R.id.container_chat_sessions_fragment, chatSessionsFragment)
                .commit();

        getChildFragmentManager().beginTransaction()
                .replace(R.id.container_search_user_fragment, searchUserFragment)
                .commit();

        binding.viewSearchBar.btnSearch.setOnClickListener(v -> filterOrSearchContact());
        binding.viewSearchBar.btnClose.setOnClickListener(v -> closeSearchUserFragment());

        binding.viewSearchBar.etSearchUser.setOnFocusChangeListener((view1, focused) -> {
            if(!focused)
                UiHelper.hideKeyboard(requireContext(), binding.viewSearchBar.etSearchUser.getWindowToken());
        });

        binding.viewSearchBar.etSearchUser.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (searchRunnable != null) searchHandler.removeCallbacks(searchRunnable);
                searchRunnable = () -> filterOrSearchContact();
                searchHandler.postDelayed(searchRunnable,DEBOUNCE_DELAY);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void searchUser(String phoneNumber) {

        if(searchUserFragment==null ||
                !searchUserFragment.isAdded() ||
                phoneNumber.equals(lastSearchedPhoneNumber))
            return;

        binding.containerSearchUserFragment.setVisibility(View.VISIBLE);
        AnimationUtil.fadeIn(binding.containerSearchUserFragment, () -> {});
        searchUserFragment.searchUser(phoneNumber);
        lastSearchedPhoneNumber = phoneNumber;
        binding.viewSearchBar.btnSearch.setVisibility(View.GONE);
        binding.viewSearchBar.btnClose.setVisibility(View.VISIBLE);
    }

    private void closeSearchUserFragment() {
        if (searchUserFragment != null) {

            AnimationUtil.fadeOut(binding.containerSearchUserFragment, () ->
                    binding.containerSearchUserFragment.setVisibility(View.GONE));

            // Restore UI state
            binding.viewSearchBar.btnSearch.setVisibility(View.VISIBLE);
            binding.viewSearchBar.btnClose.setVisibility(View.GONE);
            binding.viewSearchBar.etSearchUser.setText("");
            binding.viewSearchBar.etSearchUser.clearFocus();
            UiHelper.hideKeyboard(requireContext(), binding.viewSearchBar.etSearchUser.getWindowToken());
        }
    }

    private void filterOrSearchContact() {

        String phoneNumber = binding.viewSearchBar.etSearchUser.getText().toString().trim();

        if(phoneNumber.isEmpty()) return;

        int results = chatSessionsFragment.filterUsersByPhoneNumber(phoneNumber);

        // Make sure the phone number is correct to avoid searching for a user else just filter
        // the existing connections

        if(results<=0 && phoneNumber.length()>=MIN_PHONE_LENGTH && CountryCodeUtil.isPhoneNumberValid(phoneNumber))
            searchUser(phoneNumber);
    }

}
