package in.amankumar110.chatapp.utils;

import in.amankumar110.chatapp.models.auth.Country;

public class CountryCodeUtil {


    public static Country getUsCountry() {

        String flagPath = "https://flagcdn.com/w320/us.png";
        return new Country("+1",flagPath,"US");
    }

    public static boolean isPhoneNumberValid(String phoneNumber) {
        return phoneNumber.matches("^\\+[1-9]\\d{10,14}$");
    }


}
