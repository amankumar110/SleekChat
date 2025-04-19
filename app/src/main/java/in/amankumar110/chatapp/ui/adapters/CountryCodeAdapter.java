package in.amankumar110.chatapp.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import in.amankumar110.chatapp.R;
import in.amankumar110.chatapp.databinding.CountryCodeItemLayoutBinding;
import in.amankumar110.chatapp.models.auth.Country;

public class CountryCodeAdapter extends RecyclerView.Adapter<CountryCodeAdapter.CountryCodeViewHolder> {

    public interface OnItemClicked {
        void onClick(Country country);
    }

    private List<Country> countryList = new ArrayList<>();
    private Context context;


    private OnItemClicked onItemClicked;

    public CountryCodeAdapter(Context context) {

        this.context = context;
        initializeCountryList();
    }

    @NonNull
    @Override
    public CountryCodeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        CountryCodeItemLayoutBinding binding = CountryCodeItemLayoutBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new CountryCodeViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CountryCodeViewHolder holder, int position) {
        Country country = countryList.get(position);
        holder.binding.setCountry(country);

        Glide.with(context).load(country.getFlag()).into(holder.binding.ivCountryFlag);

        holder.binding.getRoot().setOnClickListener(v -> onItemClicked.onClick(country));
    }

    @Override
    public int getItemCount() {
        return countryList.size();
    }

    public class CountryCodeViewHolder extends RecyclerView.ViewHolder {

        private CountryCodeItemLayoutBinding binding;
        public CountryCodeViewHolder(@NonNull CountryCodeItemLayoutBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    private void initializeCountryList() {

        try {
            InputStream inputStream = context.getResources().openRawResource(R.raw.countries);
            InputStreamReader reader = new InputStreamReader(inputStream);
            Type listType = new TypeToken<List<Country>>() {}.getType();
            this.countryList = new Gson().fromJson(reader, listType);
            reader.close();
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
            countryList = new ArrayList<>(); // Fallback to an empty list if an error occurs
        }
    }

    public OnItemClicked getOnItemClicked() {
        return onItemClicked;
    }

    public void setOnItemClicked(OnItemClicked onItemClicked) {
        this.onItemClicked = onItemClicked;
    }

}
