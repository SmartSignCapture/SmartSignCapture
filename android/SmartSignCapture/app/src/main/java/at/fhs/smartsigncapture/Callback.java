package at.fhs.smartsigncapture;


/**
 * Created by MartinTiefengrabner on 15/07/15.
 */
public interface Callback<T> {
    void success(T t);
    void failure(String errorMessage);
}
