package app.mycity.mycity.views.activities;

public interface Storage{
    Object getDate(String key);
    void setDate(String key, Object date);
    void remove(String key);
}
