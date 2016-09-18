package model;

import android.media.Image;

import java.util.List;

/**
 * Created by LYW on 2016/9/17.
 */
public class Latest {
    private List<StroiesEntity> stroies;
    private List<Top_stroies> top_stroies;
    private String date;
    static class Top_stroies{
        /**
         * top_stories: [
         {
         title: "商场和很多人家里，竹制家具越来越多（多图）",
         image: "http://p2.zhimg.com/9a/15/9a1570bb9e5fa53ae9fb9269a56ee019.jpg",
         ga_prefix: "052315",
         type: 0,
         id: 3930883
         },
         */
            int id;
            int type;
            String title;
            Image image;
            String ga_prefix;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public Image getImage() {
            return image;
        }

        public void setImage(Image image) {
            this.image = image;
        }

        public String getGa_prefix() {
            return ga_prefix;
        }

        public void setGa_prefix(String ga_prefix) {
            this.ga_prefix = ga_prefix;
        }

        @Override
        public String toString() {
            return "TopStoriesEntity{" +
                    "id=" + id +
                    ", title='" + title + '\'' +
                    ", ga_prefix='" + ga_prefix + '\'' +
                    ", image='" + image + '\'' +
                    ", type=" + type +
                    '}';
        }
    }

    public List<StroiesEntity> getStroies() {
        return stroies;
    }

    public void setStroies(List<StroiesEntity> stroies) {
        this.stroies = stroies;
    }

    public List<Top_stroies> getTop_stroies() {
        return top_stroies;
    }

    public void setTop_stroies(List<Top_stroies> top_stroies) {
        this.top_stroies = top_stroies;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "Latest{" +
                "top_stories=" + top_stroies +
                ", stories=" + stroies +
                ", date='" + date + '\'' +
                '}';
    }
}
