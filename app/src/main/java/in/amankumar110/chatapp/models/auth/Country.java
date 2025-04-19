
package in.amankumar110.chatapp.models.auth;

import android.widget.ImageView;

import androidx.databinding.BindingAdapter;

import com.bumptech.glide.Glide;

public class Country {
    private String code;
    private String flag;
    private String name;

    public Country(String code, String flag, String name) {
        this.code = code;
        this.flag = flag;
        this.name = name;
    }

    public Country(){}

    public String getCode() { return code; }
    public void setCode(String value) { this.code = value; }

    public String getFlag() { return flag; }
    public void setFlag(String value) { this.flag = value; }

    public String getName() { return name; }
    public void setName(String value) { this.name = value; }

    @BindingAdapter("app:countryFlag")
    public static void setCountryFlag(ImageView view, String code) {
        Glide.with(view.getContext())
                .load(code)
                .into(view);
    }
}
