package at.fhs.smartsigncapture.controller;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import at.fhs.smartsigncapture.R;
import at.fhs.smartsigncapture.data.API.adapter.SSCUserAPIAdapter;
import at.fhs.smartsigncapture.data.GCM.GCMConstants;
import at.fhs.smartsigncapture.data.GCM.SSCGCMListenerService;
import at.fhs.smartsigncapture.data.MessageDAL;
import at.fhs.smartsigncapture.model.Message;
import at.fhs.smartsigncapture.utils.NotificationCenter;
import at.fhs.smartsigncapture.view.activity.UnityPlayActivity;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by MartinTiefengrabner on 24/07/15.
 */
public class MessageController {

    public interface OnNewMessageReceivedListener {
        public void newMessageReceived(Message message);
    }

    public interface OnMessageSentListener {
        public void messageSent(Message message);
    }

    private SSCGCMListenerService.GCMMessageReceiver messageReceiver = new SSCGCMListenerService.GCMMessageReceiver() {
        @Override
        public String getGCMKey() {
            return GCMConstants.GCM_KEY_MESSAGE_NEW;
        }

        @Override
        public void receivedMessage(JSONObject data) {
            try {
                if (data.getLong("receiver") == userController.getCurrentUser().getId()) {
                    SSCUserAPIAdapter.APIInterface.getReceivedMessage(userController.getCurrentUser().getId(), data.getLong("id"), new Callback<Message>() {
                        @Override
                        public void success(Message message, Response response) {
                            message.isReceivedMessage(true);
                            saveMessage(message);
                            String notificationTitle = context.getResources().getString(R.string.notifcation_new_message_title);
                            String notificationMsg = message.getSendingFriend().getUserName() + " " + context.getResources().getString(R.string.notifcation_new_message_msg_stump);

                            Intent intent = new Intent(context, UnityPlayActivity.class);
                            Bundle extras = new Bundle();

                            extras.putString(UnityPlayActivity.EXTRAS_KEY_SIGN_DATA, message.getMessage());

                            intent.putExtras(extras);

                            NotificationCenter.showNotification(context, notificationTitle, notificationMsg, R.drawable.ic_new_message, intent, true);

                            for (OnNewMessageReceivedListener listener : listeners) {
                                listener.newMessageReceived(message);
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

    private static MessageController instance;

    private Context context;
    private UserController userController;
    private ContactsController contactsController;

    private MessageDAL dal;

    private static List<OnNewMessageReceivedListener> listeners = new ArrayList<>();

    public static void addOnNewMessageReceivedListener(OnNewMessageReceivedListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    public static void removeOnNewMessageReceivedListener(OnNewMessageReceivedListener listener) {
        if (listeners.contains(listener)) {
            listeners.remove(listener);
        }
    }

    public static MessageController getInstance(Context context) {
        if (instance == null) {
            instance = new MessageController(context);
        }
        return instance;
    }

    public List<Message> newestMessagesPerUser() {
        List<Message> result = this.dal.newestMessagesPerUser(userController.getCurrentUser().getId());

        for (Message m : result) {
            this.fillWithCorrespondingContacts(m);
        }
        return result;
    }

    public List<Message> getConversation(long counterpartID) {
        List<Message> result = this.dal.selectConversationWithUser(counterpartID, userController.getCurrentUser().getId());

        for (Message m : result) {
            this.fillWithCorrespondingContacts(m);
        }
        return result;
    }

    private MessageController(Context context) {
        this.context = context;

        this.dal = new MessageDAL(context);
        this.userController = UserController.getInstance(context);
        this.contactsController = ContactsController.getInstance(context);

        SSCGCMListenerService.registerMessageReceiver(messageReceiver);
    }

    private Message fillWithCorrespondingContacts(Message msg) {
        if (msg.isReceivedMessage()) {
            msg.setSendingFriend(this.contactsController.getContact(msg.getSenderID()));
            msg.setReceivingFriend(this.userController.getCurrentUser());
        } else {
            msg.setReceivingFriend(this.contactsController.getContact(msg.getReceiverID()));
            msg.setSendingFriend(this.userController.getCurrentUser());
        }


        return msg;
    }

    private void saveMessage(Message message) {
        this.dal.saveMessage(message, userController.getCurrentUser().getId());
        this.fillWithCorrespondingContacts(message);
    }

    public Message getMessage(long id) {
        return this.fillWithCorrespondingContacts(this.dal.loadMessage(id, userController.getCurrentUser().getId()));
    }

    public void requestMessage(long userID, long messageID, final at.fhs.smartsigncapture.Callback<Message> cb) {
        SSCUserAPIAdapter.APIInterface.getMessage(userID, messageID, new Callback<Message>() {
            @Override
            public void success(Message message, Response response) {
                cb.success(message);
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d("ERR: ", error.getUrl());

                cb.failure(error.getMessage());
            }
        });

    }

//    public void requestAllMessages(final long userID, final at.fhs.smartsigncapture.Callback<List<Message>> cb) {
//        SSCUserAPIAdapter.APIInterface.getMessageIDs(userID, new Callback<List<Message>>() {
//            @Override
//            public void success(List<Message> messageIDs, Response response) {
//
//                final List<Message> ids = messageIDs;
//
//
//                final int numberOfMessages = ids.size();
//                final List<Message> messages = new ArrayList<>();
//
//                at.fhs.smartsigncapture.Callback<Message> messageCallback = new at.fhs.smartsigncapture.Callback<Message>() {
//
//                    private int numberOfReceivedMessages = 0;
//
//                    public int numberOfMessages;
//
//                    @Override
//                    public void success(Message message) {
//                        messages.add(message);
//                        resultReceived();
//                    }
//
//                    @Override
//                    public void failure(String errorMessage) {
//                        resultReceived();
//                    }
//
//                    private void resultReceived() {
//                        numberOfReceivedMessages++;
//
//                        if (numberOfReceivedMessages == numberOfMessages) {
//                            cb.success(messages);
//                        }
//                    }
//                };
//
//
//                for (Message m : ids) {
//                    requestMessage(userID, m.getId(), messageCallback);
//                }
//            }
//
//
//            @Override
//            public void failure(RetrofitError error) {
//                cb.failure(error.getMessage());
//            }
//        });
//    }

    public void requestAllMessages(final long userID, final at.fhs.smartsigncapture.Callback<List<Message>> cb) {
        SSCUserAPIAdapter.APIInterface.getMessageIDs(userID, new Callback<List<Message>>() {
            @Override
            public void success(final List<Message> messageIDs, Response response) {

                final List<Message> ids = messageIDs;

                final List<Message> messages = new ArrayList<>();



                    at.fhs.smartsigncapture.Callback<Message> messageCallback = new at.fhs.smartsigncapture.Callback<Message>() {

                        private int numberOfReceivedMessages = 0;


                        @Override
                        public void success(Message message) {
                            saveMessage(message);
                            messages.add(message);
                            resultReceived();
                        }

                        @Override
                        public void failure(String errorMessage) {
                            resultReceived();
                        }

                        private void resultReceived() {
                            numberOfReceivedMessages++;

                            if (numberOfReceivedMessages < ids.size()) {
                                requestMessage(userID, ids.get(numberOfReceivedMessages).getId(), this);
                            } else {
                                cb.success(messages);

                            }

                        }
                    };

                if (ids != null && ids.size() > 0) {
                         requestMessage(userID, ids.get(0).getId(), messageCallback);
                }
                else{
                    cb.success(messages);
                }

            }


            @Override
            public void failure(RetrofitError error) {
                cb.failure(error.getMessage());
            }
        });
    }


    public void sendMessage(long receiver, String content, Date date) {
        this.sendMessage(receiver, content, date, new at.fhs.smartsigncapture.Callback<Message>() {
            @Override
            public void success(Message message) {

            }

            @Override
            public void failure(String errorMessage) {

            }
        });
    }

    public void sendMessage(long receiver, String content, Date date, final at.fhs.smartsigncapture.Callback<Message> messageCallback) {
        final Message newMessage = new Message(this.userController.getCurrentUser().getId(), receiver, date, content);

        //this.dal.saveMessage(newMessage, userController.getCurrentUser().getId());

        SSCUserAPIAdapter.APIInterface.sendMessage(newMessage.getSenderID(), newMessage.getReceiverID(), newMessage, new Callback<Message>() {
            @Override
            public void success(Message message, Response response) {
                dal.saveMessage(message, userController.getCurrentUser().getId());
                messageCallback.success(message);
            }

            @Override
            public void failure(RetrofitError error) {
                messageCallback.failure(error.getMessage());
            }
        });
    }

}
