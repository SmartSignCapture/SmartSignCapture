package at.fhs.smartsigncapture.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by MartinTiefengrabner on 19/08/15.
 */
public class Sign {
    //region Attributes

    private long id;

    private String name;

    private String author;

    private Date date;

    private String sign;

    private List<Tag> tags;

    //endregion

    //region Methods

    public Sign(long id, String name, String author, Date date, String sign, List<Tag> tags) {
        this(name, author, sign, date);
        this.id = id;
        this.tags = tags;
    }

    public Sign(String name, String author, Date date, String sign, List<Tag> tags) {
        this(name, author, sign, date);
        this.tags = tags;
    }

    public Sign(String name, String author, String sign, Date date) {
        this.name = name;
        this.author = author;
        this.date = date;
        this.sign = sign;
        id = -1;
        tags = new ArrayList<>();

    }

    //region Public

    public void addTag(Tag tag){
        if(!this.tags.contains(tag)){
            this.tags.add(tag);
        }
    }

    public void removeTag(Tag tag){
        if(this.tags.contains(tag)){
            this.tags.remove(tag);
        }
    }

    //endregion

    //endregion

    //region Properties

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAuthor() {
        return author;
    }

    public Date getDate() {
        return date;
    }

    public String getSign() {
        return sign;
    }

    public List<Tag> getTags() {
        return tags;
    }

    //endregion

}
