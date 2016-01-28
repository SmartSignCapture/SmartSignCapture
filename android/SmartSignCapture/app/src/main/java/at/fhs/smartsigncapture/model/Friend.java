package at.fhs.smartsigncapture.model;

import java.util.Date;

/**
 * Created by MartinTiefengrabner on 31/07/15.
 */
public class Friend extends Contact {

    public enum FriendshipState{
        NOT_FRIENDS,
        NEEDS_APPROVAL,
        WAITING_FOR_APPROVAL,
        FRIENDS
    }

    private FriendshipState state;

    private Date friendsSince;

    public Friend(long id, String userName, String firstName, String lastName, String imageURL, String localImage, FriendshipState state, Date friendsSince) {
        super(id, userName, firstName, lastName, imageURL, localImage);
        setState(state);
        this.friendsSince = friendsSince;
    }

    public Friend(long id, String userName, String firstName, String lastName, String imageURL, String localImage, FriendshipState state){
        this(id,userName,firstName,lastName,imageURL, localImage,state,null);
    }

    public FriendshipState getState() {
        return state != null ? state : FriendshipState.NOT_FRIENDS;
    }

    public void setState(FriendshipState state) {
        if(state != null) {
            this.state = state;
        }
        else{
            this.state = FriendshipState.NOT_FRIENDS;
        }

    }

    public Date getFriendsSince() {
        return friendsSince;
    }

    public void setFriendsSince(Date friendsSince) {
        this.friendsSince = friendsSince;
    }
}