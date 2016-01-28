package at.fhs.smartsigncapture.model;

import java.io.Serializable;

/**
 * Created by MartinTiefengrabner on 19/08/15.
 */
public class Tag implements Serializable {

    //region Attributes

    private long id;
    private String tag;

    //endregion

    //region Methods

    public Tag(long id, String tag) {
        this(tag);

        this.id = id;
    }

    public Tag(String tag) {
        this.tag = tag;
        this.id = -1;
    }

    @Override
    public String toString(){
        return this.tag;
    }

    //endregion

    //region Properties

    public void setID(long id){
        this.id  =id;
    }

    public long getId() {
        return id;
    }

    public String getTag() {
        return tag;
    }
    //endregion


}
