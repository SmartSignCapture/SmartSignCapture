package at.fhs.smartsigncapture.model;

import java.util.Date;

/**
 * Created by MartinTiefengrabner on 24/07/15.
 */
public class Message {

    private long id;
    private long sender;
    private long receiver;
    private Date date;
    private String message;
    private boolean received;

    private Contact sendingFriend;
    private Contact receivingFriend;

    public Message(long id, long sender, long receiver, Date date, String message){
        this.id = id;
        this.sender = sender;
        this.receiver = receiver;
        this.date = date;
        this.message = message;
    }

    public Message(long id, long sender, long receiver, Date date, String message, boolean received){
        this(id,sender,receiver,date,message);
        this.received = received;
    }

    public Message(long sender, long receiver, Date date, String message){
        this(-1, sender,receiver,date,message);
    }

    public long getId() {
        return id;
    }


    public long getSenderID() {
        return sender;
    }

    public long getReceiverID() {
        return receiver;
    }

    public Date getDate() {
        return date;
    }

    public String getMessage() {
        return message;
    }

    public boolean isReceivedMessage(){
        return this.received;
    }

    public void isReceivedMessage(boolean received){
         this.received = received;
    }

    public Contact getSendingFriend() {
        return sendingFriend;
    }

    public void setSendingFriend(Contact sendingFriend) {
        this.sendingFriend = sendingFriend;
    }

    public Contact getReceivingFriend() {
        return receivingFriend;
    }

    public void setReceivingFriend(Contact receivingFriend) {
        this.receivingFriend = receivingFriend;
    }
}
