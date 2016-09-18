package model;

/**
 * Created by LYW on 2016/9/16.
 */
public class NewsListItem {
     private String name;
     private int id;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return super.toString()+"name is:["+name+"] "+"id is :[ " +id+"]";
    }
}
