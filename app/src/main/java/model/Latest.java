package model;


import java.util.List;


public class Latest {
    private List<StoriesEntity> stories;
    private List<Top_stroies> top_stories;
    private String date;

    public static class Top_stroies {
        /**
         * top_stories: [
         * {
         * title: "商场和很多人家里，竹制家具越来越多（多图）",
         * image: "http://p2.zhimg.com/9a/15/9a1570bb9e5fa53ae9fb9269a56ee019
         * .jpg",
         * ga_prefix: "052315",
         * type: 0,
         * id: 3930883
         * },
         */
        int id;
        int type;
        String title;
        String image;
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

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
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

    public List<StoriesEntity> getStories() {
        return stories;
    }

    public void setStroies(List<StoriesEntity> stroies) {
        this.stories = stroies;
    }

    public List<Top_stroies> getTop_stroies() {
        return top_stories;
    }

    public void setTop_stroies(List<Top_stroies> top_stroies) {
        this.top_stories = top_stroies;
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
                "top_stories=" + top_stories +
                ", stories=" + stories +
                ", date='" + date + '\'' +
                '}';
    }
}
