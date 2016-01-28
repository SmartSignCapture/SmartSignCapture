package at.fhs.smartsigncapture;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import android.support.annotation.Nullable;

import java.io.FileDescriptor;
import java.util.Date;

import at.fhs.smartsigncapture.controller.MessageController;
import at.fhs.smartsigncapture.model.Message;

/**
 * Created by MartinTiefengrabner on 04/08/15.
 */
public class SendMessageService extends Service {

    public static String INTENT_KEY_RECEIVER_ID = "receiverID";
    public static String INTENT_KEY_MESSAGE_BODY = "messageBody";

    private long receiverID;
    private String messageBody;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        this.receiverID = intent.getLongExtra(INTENT_KEY_RECEIVER_ID, -1);

        if(this.receiverID != -1){
            this.messageBody = intent.getStringExtra(INTENT_KEY_MESSAGE_BODY);

            if(this.messageBody != null){
                this.sendMessage();
            }
        }

        return super.onStartCommand(intent, flags, startId);

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new IBinder() {
            @Override
            public String getInterfaceDescriptor() throws RemoteException {
                return null;
            }

            @Override
            public boolean pingBinder() {
                return false;
            }

            @Override
            public boolean isBinderAlive() {
                return false;
            }

            @Override
            public IInterface queryLocalInterface(String descriptor) {
                return null;
            }

            @Override
            public void dump(FileDescriptor fd, String[] args) throws RemoteException {

            }

            @Override
            public void dumpAsync(FileDescriptor fd, String[] args) throws RemoteException {

            }

            @Override
            public boolean transact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
                return false;
            }

            @Override
            public void linkToDeath(DeathRecipient recipient, int flags) throws RemoteException {

            }

            @Override
            public boolean unlinkToDeath(DeathRecipient recipient, int flags) {
                return false;
            }
        };
    }

    public void sendMessage(){
        new Thread(){
            @Override
            public void run() {
                super.run();
                MessageController.getInstance(SendMessageService.this).sendMessage(receiverID, messageBody, new Date(), new Callback<Message>() {
                    @Override
                    public void success(Message message) {

                    }

                    @Override
                    public void failure(String errorMessage) {

                    }
                });
            }
        }.run();

    }
}
