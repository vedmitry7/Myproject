package app.mycity.mycity.views.activities;

public interface RegisterActivityDataStore {

    void setPassword(String password, String confirm);
    void setEmail(String email);

    void setInfo(String firstName, String secondName, String birthday, String sex, String cityId, String countryId);

    void setCode(String code);
    void nextEmailStep();
    void nextConfirmEmailCodeStep();
    void checkEmail();
    void checkEmailCodeAndRegistration();
    void commitPassword();
}
