package in.amankumar110.chatapp.ui.customviews;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import in.amankumar110.chatapp.R;
import in.amankumar110.chatapp.databinding.CountryItemsSelectionDropdownBinding;
import in.amankumar110.chatapp.databinding.SelectedCountryDisplayLayoutBinding;
import in.amankumar110.chatapp.models.auth.Country;
import in.amankumar110.chatapp.ui.adapters.CountryCodeAdapter;
import in.amankumar110.chatapp.utils.CountryCodeUtil;

public class CountrySelectionView extends LinearLayout {
    private Context context;
    private SelectedCountryDisplayLayoutBinding displayBinding;
    private CountryItemsSelectionDropdownBinding binding;
    private RecyclerView recyclerView;
    private CountryCodeAdapter adapter;
    private String phoneCode = null;
    private static final String ARG_PHONE_CODE = "phone_code";

    public CountrySelectionView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {

        this.context = context;

        inflateLayout();
        setupCountryList();
        setupListeners();
        setDefaultCountry(CountryCodeUtil.getUsCountry());
    }

    private void inflateLayout() {
        View v = LayoutInflater.from(context).inflate(R.layout.country_items_selection_dropdown, this, true);
        binding = CountryItemsSelectionDropdownBinding.bind(v);
        displayBinding = binding.selectedViewContainer;
        recyclerView = binding.rvCountryItem;
    }

    private void setupCountryList() {
        // Setup RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        adapter = new CountryCodeAdapter(context);
        recyclerView.setAdapter(adapter);
    }

    private void setupListeners() {
        // Toggle RecyclerView visibility on click
        displayBinding.container.setOnClickListener(view ->
                recyclerView.setVisibility(recyclerView.getVisibility() == VISIBLE ? GONE : VISIBLE));

        // Handle item click
        adapter.setOnItemClicked(country -> {
            this.phoneCode = country.getCode();
            CountrySelectionView.this.displayBinding.setCountry(country);
            recyclerView.setVisibility(GONE);
        });
    }


    public void setDefaultCountry(Country country) {
        this.phoneCode = country.getCode();
        displayBinding.setCountry(country);
    }

    public String getSelectedCode() {
        return binding.selectedViewContainer.tvCountryCode.getText().toString();
    }

    public void setDefaultCountryByCode(String code) {
        recyclerView.post(()->{
            Country country = adapter.getCountryByCode(code);
            displayBinding.setCountry(country);
            recyclerView.setVisibility(GONE);
        });
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;

            phoneCode = bundle.getString(ARG_PHONE_CODE);

            // Restore default country if needed
            if (phoneCode != null) {
                setDefaultCountryByCode(phoneCode);
            }

            Parcelable superState = bundle.getParcelable("super_state");
            super.onRestoreInstanceState(superState);
        } else {
            super.onRestoreInstanceState(state);
        }
    }

    @Nullable
    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable("super_state", super.onSaveInstanceState());
        bundle.putString(ARG_PHONE_CODE, phoneCode);
        return bundle;
    }
}
