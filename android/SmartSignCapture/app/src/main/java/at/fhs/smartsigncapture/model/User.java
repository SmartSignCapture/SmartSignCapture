package at.fhs.smartsigncapture.model;

/**
 * Created by MartinTiefengrabner on 14/07/15.
 */
public class User extends Contact {

    public User(String userName, String firstName, String lastName, String imageURL, String localImage) {
        super(userName,firstName,lastName,imageURL,localImage);
    }

    public User(long id, String userName, String firstName, String lastName,String imageURL, String localImage) {
        super(id,userName,firstName,lastName,imageURL,localImage);
    }

}
