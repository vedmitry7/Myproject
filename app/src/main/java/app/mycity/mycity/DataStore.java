package app.mycity.mycity;

public interface DataStore {

    void setPassword(String password, String confirm);
    void setEmail(String email);
    void setInfo(String firstName,String secondName, String birthday, String sex);
    void setCode(String code);
    void nextEmailStep();
    void nextConfirmEmailCodeStep();
    void checkEmail();
    void checkEmailCode();
    void commitPassword();
}
