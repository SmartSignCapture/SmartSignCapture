package at.fhs.smartsigncapture.model;

/**
 * Created by MartinTiefengrabner on 20/07/15.
 */
public class Contact{
    //region Attributes

    protected long id;
    protected String userName;
    protected String firstName;
    protected String lastName;
    protected String imageUrl;
    protected String localImage;


    //endregion

    //region Constructor

    public Contact(String userName, String firstName, String lastName, String imageURL, String localImage) {
        this.userName = userName;
        this.firstName = firstName;
        this.lastName = lastName;
        this.imageUrl = imageURL;
        this.localImage = localImage;

        this.id = -1;

    }

    public Contact(long id, String userName, String firstName, String lastName, String imageURL, String localImage) {
        this(userName, firstName, lastName, imageURL, localImage);
        this.id = id;
    }

    //endregion

    //region Override

    @Override
    public int hashCode() {
        return userName.hashCode();
    }

    //endregion

    //region Properties

    public long getId() {
        return id;
    }

    public String getUserName() {
        return userName;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getImageURL() {
        return imageUrl;
    }

    public String getLocalImage(){
        return this.localImage;
    }

    public void setLocalImage(String localImage){this.localImage = localImage;}

    @Override
    public boolean equals(Object o) {
        boolean result = false;
        if(o instanceof Contact) {
            result = ((Contact) o).getId() == id;
        }

        return result;
    }

    //endregion


}
