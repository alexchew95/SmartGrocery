package fyp.chewtsyrming.smartgrocery.nestedRv;

public class Goods {
    String id;
    String name;
    String poster;
    String genre;
  String imageUrl;



    public Goods() {
    }

    public Goods(String id, String name, String poster, String genre, String imageUrl) {
        this.id = id;
        this.name = name;
        this.poster = poster;
        this.genre = genre;
        this.imageUrl = imageUrl;
    }


    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPoster() {
        return poster;
    }


    public String getGenre() {
        return genre;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
