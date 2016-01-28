package at.fhs.smartsigncapture.controller;

import android.content.Context;

import java.io.File;
import java.util.List;
import java.util.concurrent.ExecutionException;

import at.fhs.smartsigncapture.Callback;
import at.fhs.smartsigncapture.data.API.adapter.SSCClientAPIAdapter;
import at.fhs.smartsigncapture.data.API.adapter.SSCUserAPIAdapter;
import at.fhs.smartsigncapture.data.LoadUserImageAsyncTask;
import at.fhs.smartsigncapture.data.UserDAL;
import at.fhs.smartsigncapture.model.Contact;
import at.fhs.smartsigncapture.model.Friend;
import at.fhs.smartsigncapture.model.Message;
import at.fhs.smartsigncapture.model.User;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by MartinTiefengrabner on 14/07/15.
 */
public class UserController {

    //region static

    private static UserController instance;

    public static final String ERROR_MESSAGE_NO_CONNECTION = "noConnectionAvailable";
    public static final String ERROR_MESSAGE_CREDENTIALS_WRONG = "wrongCredentials";

    //endregion

    //region Attributes

    private Context context;

    private UserDAL dal;

    private User currentUser;

    //endregion

    //region Static
    public static UserController getInstance(Context context) {
        if (instance == null) {
            instance = new UserController(context);
        }
        return instance;
    }
    //endregion

    //region Constructor

    public UserController(Context context) {
        this.context = context;
        this.dal = new UserDAL(context);

        this.loadCurrentUser();
    }

    //endregion

    //region Public

    public void isEmailAddressAvailable(String email, final Callback<Boolean> callback) {
        SSCClientAPIAdapter.APIInterface.usersWithEmail(email, new retrofit.Callback<List<User>>() {
            @Override
            public void success(List<User> users, Response response) {
                callback.success(users == null);
            }

            @Override
            public void failure(RetrofitError error) {
                callback.failure("");
            }
        });
    }

    public void isUserNameAvailable(String userName, final Callback<Boolean> callback) {
        SSCClientAPIAdapter.APIInterface.usersWithUsername(userName, new retrofit.Callback<List<User>>() {
            @Override
            public void success(List<User> users, Response response) {
                callback.success(users == null);
            }

            @Override
            public void failure(RetrofitError error) {
                callback.failure(ERROR_MESSAGE_CREDENTIALS_WRONG);
            }
        });
    }

    public void registerUser(final String email, String userName, String firstName, String lastName, final String password, final Callback<Boolean> callback) {
        SSCClientAPIAdapter.APIInterface.createUser(
                SSCUserAPIAdapter.createPostUserHashMap(email, userName, firstName, lastName, password),
                new retrofit.Callback<User>() {
                    @Override
                    public void success(User user, Response response) {
                        UserController.this.dal.saveUser(user, email, password);
                        currentUser = user;

                        SSCUserAPIAdapter.loginUser(context, email, password, new Callback<User>() {
                            @Override
                            public void success(User user) {
                                callback.success(true);
                            }

                            @Override
                            public void failure(String errorMessage) {

                            }
                        });


                    }

                    @Override
                    public void failure(RetrofitError error) {
                        callback.failure(error.getMessage());
                    }
                }
        );
    }


    public void loginUser(final String email, final String password, final Callback<Void> callback) {
        SSCUserAPIAdapter.loginUser(this.context, email, password, new Callback<User>() {
            @Override
            public void success(User user) {
                loadUserImage(user);

                UserController.this.dal.saveUser(user, email, password);
                currentUser = user;

                new Thread() {
                    public void run() {

                        ContactsController.getInstance(context).requestFriends(currentUser.getId(), new Callback<List<Friend>>() {
                            @Override
                            public void success(List<Friend> friends) {
                                MessageController.getInstance(context).requestAllMessages(currentUser.getId(), new Callback<List<Message>>() {
                                    @Override
                                    public void success(List<Message> messages) {

                                        callback.success(null);
                                    }

                                    @Override
                                    public void failure(String errorMessage) {
                                        callback.failure(errorMessage);
                                    }
                                });
                            }

                            @Override
                            public void failure(String errorMessage) {
                                callback.failure(errorMessage);
                            }
                        });
                    }
                }.run();
            }

            @Override
            public void failure(String errorMessage) {

                callback.failure(errorMessage);

            }
        });
    }

    private void loadUserImage(Contact c) {
        try {
            List<File> result = new LoadUserImageAsyncTask(context).execute(c).get();

            if (result.size() == 1 && result.get(0) != null)
                c.setLocalImage(result.get(0).getAbsolutePath());

        } catch (ExecutionException | InterruptedException ei) {
            ei.printStackTrace();
        }
    }


    //endregion

    //region Private

    private void loadCurrentUser() {
        currentUser = this.dal.restoreUser();

        if (currentUser != null) {
            final String email = this.dal.getUserEmail();
            final String password = this.dal.getUserPassword();
            SSCUserAPIAdapter.loginUser(this.context, email, password, new Callback<User>() {
                @Override
                public void success(User user) {
                    SSCUserAPIAdapter.setCredentials(email, password);
                }

                @Override
                public void failure(String errorMessage) {

                }
            });
        }
    }


    //endregion
    //region Properties

    public User getCurrentUser() {
        return this.currentUser;
    }

    //endregion
}
