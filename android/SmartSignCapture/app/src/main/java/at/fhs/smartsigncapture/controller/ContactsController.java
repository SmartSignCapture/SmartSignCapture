package at.fhs.smartsigncapture.controller;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import at.fhs.smartsigncapture.R;
import at.fhs.smartsigncapture.data.API.adapter.SSCClientAPIAdapter;
import at.fhs.smartsigncapture.data.API.adapter.SSCUserAPIAdapter;
import at.fhs.smartsigncapture.data.ContactDAL;
import at.fhs.smartsigncapture.data.GCM.GCMConstants;
import at.fhs.smartsigncapture.data.GCM.SSCGCMListenerService;
import at.fhs.smartsigncapture.data.LoadUserImageAsyncTask;
import at.fhs.smartsigncapture.model.Contact;
import at.fhs.smartsigncapture.model.Friend;
import at.fhs.smartsigncapture.utils.NotificationCenter;
import at.fhs.smartsigncapture.view.activity.MainActivity;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by MartinTiefengrabner on 20/07/15.
 */
public class ContactsController {

    private SSCGCMListenerService.GCMMessageReceiver friendRequestsReceiver = new SSCGCMListenerService.GCMMessageReceiver() {
        @Override
        public String getGCMKey() {
            return GCMConstants.GCM_KEY_FRIEND_REQUEST;
        }

        @Override
        public void receivedMessage(JSONObject data) {
            try {
                if (data.getLong("toUser") == userController.getCurrentUser().getId()) {

                    long fromID = data.getLong("fromUser");

                    SSCUserAPIAdapter.APIInterface.getFriendInfo(userController.getCurrentUser().getId(), fromID, new Callback<Friend>() {
                        @Override
                        public void success(Friend friend, Response response) {
                            int idx = friends.indexOf(friend);
                            if (idx == -1 || (friends.get(idx).getState().ordinal() < Friend.FriendshipState.NEEDS_APPROVAL.ordinal())) {
                                friend.setState(Friend.FriendshipState.NEEDS_APPROVAL);


                                dal.insertContact(friend);
                                friends.add(friend);

                                String notificationTitle = context.getResources().getString(R.string.notifcation_friend_request_title);
                                String notificationMsg = friend.getUserName() + " " + context.getResources().getString(R.string.notifcation_friend_request_msg_stump);


                                NotificationCenter.showNotification(context, notificationTitle, notificationMsg, R.drawable.ic_friends_add, MainActivity.class, true);
                            }

                        }

                        @Override
                        public void failure(RetrofitError error) {

                        }
                    });
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    private SSCGCMListenerService.GCMMessageReceiver friendRequestsAcceptedReceiver = new SSCGCMListenerService.GCMMessageReceiver() {
        @Override
        public String getGCMKey() {
            return GCMConstants.GCM_KEY_FRIEND_REQUEST_ACCEPTED;
        }

        @Override
        public void receivedMessage(JSONObject data) {
            try {
                if (data.getLong("fromUser") == userController.getCurrentUser().getId()) {

                    long toID = data.getLong("toUser");

                    SSCUserAPIAdapter.APIInterface.getFriendInfo(userController.getCurrentUser().getId(), toID, new Callback<Friend>() {
                        @Override
                        public void success(Friend friend, Response response) {
                            int idx = friends.indexOf(friend);

                            if(idx != -1){
                                friend = friends.get(idx);
                            }

                            if(friend.getState().ordinal() < Friend.FriendshipState.FRIENDS.ordinal()){

                                loadUserImage(friend);

                                friend.setState(Friend.FriendshipState.FRIENDS);
                                dal.insertOrUpdateContact(friend);


                                if(idx == -1)
                                    friends.add(friend);

                                String notificationTitle = context.getResources().getString(R.string.notifcation_friend_request_accepted_title);
                                String notificationMsg = friend.getUserName() + " " + context.getResources().getString(R.string.notifcation_friend_request_accepted_msg_stump);

                                NotificationCenter.showNotification(context, notificationTitle, notificationMsg, R.drawable.ic_friends_accept, MainActivity.class, true);
                            }

                        }

                        @Override
                        public void failure(RetrofitError error) {

                        }
                    });
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    //region static
    public static String CALLBACK_ERROR_NOT_CONTACT_FOUND = "No Contact found";

    private static ContactsController instance;

    private static UserController userController;

    private List<Friend> friends;

//endregion

//region Attributes

    private Context context;

    private ContactDAL dal;


//endregion

    //region Static
    public static ContactsController getInstance(Context context) {
        if (instance == null) {
            instance = new ContactsController(context);
        }
        return instance;
    }
//endregion

//region Constructor

    public ContactsController(Context context) {
        this.context = context;
        this.dal = new ContactDAL(this.context);
        this.userController = UserController.getInstance(context);
        this.loadContacts();

        SSCGCMListenerService.registerMessageReceiver(this.friendRequestsReceiver);
        SSCGCMListenerService.registerMessageReceiver(this.friendRequestsAcceptedReceiver);

    }
//endregion

//region Public

    public void searchContacts(final String searchTerm, final at.fhs.smartsigncapture.Callback<List<Friend>> cb) {

        new Thread(){
          public void run(){
              SSCClientAPIAdapter.APIInterface.searchContacts(searchTerm, new Callback<List<Friend>>() {
                  @Override
                  public void success(List<Friend> friends, Response response) {
                      if (friends != null) {

                          friends.remove(userController.getCurrentUser());


                          for (Friend c : friends) {

                              int idx = ContactsController.this.friends.indexOf(c);

                              if (idx != -1) {
                                  c.setFriendsSince(ContactsController.this.friends.get(idx).getFriendsSince());
                                  c.setState(ContactsController.this.friends.get(idx).getState());
                              }
                              loadUserImage(c);
                          }
                          cb.success(friends);
                      } else {
                          cb.failure(CALLBACK_ERROR_NOT_CONTACT_FOUND);
                      }
                  }

                  @Override
                  public void failure(RetrofitError error) {
                      cb.failure(error.getMessage());
                  }
              });
          }
        }.run();

    }

    public void changeContactState(Friend friend, at.fhs.smartsigncapture.Callback<Friend> callback) {
        switch (friend.getState()) {
            case NOT_FRIENDS:
                this.requestFriendShip(friend, callback);
                break;
            case NEEDS_APPROVAL:
                this.acceptFriendShip(friend, callback);
        }
    }

    private void acceptFriendShip(final Friend friend, final at.fhs.smartsigncapture.Callback<Friend> callback) {
        SSCUserAPIAdapter.APIInterface.acceptFriendship(this.userController.getCurrentUser().getId(), friend.getId(), new Callback<Void>() {
            @Override
            public void success(Void aVoid, Response response) {
                friend.setState(Friend.FriendshipState.FRIENDS);
                loadUserImage(friend);
                if (dal.insertOrUpdateContact(friend)) {
                    if (!friends.contains(friend))
                        friends.add(friend);
                }

                callback.success(friend);
            }

            @Override
            public void failure(RetrofitError error) {
                callback.failure(error.getMessage());
            }
        });
    }

    private void requestFriendShip(final Friend friend, final at.fhs.smartsigncapture.Callback<Friend> callback) {
        SSCUserAPIAdapter.APIInterface.sendFriendRequest(this.userController.getCurrentUser().getId(), friend.getId(), new Callback<Void>() {
            @Override
            public void success(Void aVoid, Response response) {
                friend.setState(Friend.FriendshipState.WAITING_FOR_APPROVAL);
                if (dal.insertOrUpdateContact(friend)) {
                    if (!friends.contains(friend))
                        friends.add(friend);
                }

                callback.success(friend);
            }

            @Override
            public void failure(RetrofitError error) {
                callback.failure(error.getMessage());
            }
        });
    }

    public void requestFriends(long userID, final at.fhs.smartsigncapture.Callback<List<Friend>> cb) {
        SSCUserAPIAdapter.getFriends(this.context, userID, new at.fhs.smartsigncapture.Callback<List<Friend>>() {
            @Override
            public void success(List<Friend> friends) {
                for (Friend c : friends) {
                    loadUserImage(c);
                    dal.insertOrUpdateContact(c);

                }

                loadContacts();

                cb.success(friends);
            }

            @Override
            public void failure(String errorMessage) {
                cb.failure(errorMessage);
            }
        });
    }

    public Friend getContact(long id) {
        for (Friend c : friends) {
            if (c.getId() == id) {
                return c;
            }
        }
        return null;
    }

//endregion

//region Private

    private void loadUserImage(Contact c){
        try {
           List<File> result =  new LoadUserImageAsyncTask(context).execute(c).get();

            if(result.size() == 1 && result.get(0) != null)
                c.setLocalImage(result.get(0).getAbsolutePath());

        } catch (ExecutionException | InterruptedException ei) {
            ei.printStackTrace();
        }
    }

    private void loadContacts() {
        this.friends = new ArrayList<>(dal.selectAllContacts());
    }

//endregion

//region Properties

    public List<Friend> getContacts() {
        return this.friends;
    }

    public List<Friend> getFriends() {
        List<Friend> friends = new ArrayList<>();

        for (Friend c : this.friends) {
            if (c.getState() == Friend.FriendshipState.FRIENDS) {
                friends.add(c);
            }
        }

        return friends;
    }

    //enregion
}
