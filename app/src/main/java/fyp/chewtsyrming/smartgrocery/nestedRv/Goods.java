package fyp.chewtsyrming.smartgrocery.nestedRv;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;

import fyp.chewtsyrming.smartgrocery.object.GoodsList;

public class Goods {
    String id;
    String name;
    String poster;
    String genre;
    String imageUrl;
    String expiredSoon;
    String remainingDays;



    public Goods() {
    }

    public Goods(String id, String name, String poster,
                 String genre, String imageUrl, String expiredSoon,
                 String remainingDays) {
        this.id = id;
        this.name = name;
        this.poster = poster;
        this.genre = genre;
        this.imageUrl = imageUrl;
        this.expiredSoon = expiredSoon;
        this.remainingDays = remainingDays;

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getExpiredSoon() {
        return expiredSoon;
    }

    public void setExpiredSoon(String expiredSoon) {
        this.expiredSoon = expiredSoon;
    }

    public String getRemainingDays() {
        return remainingDays;
    }

    public void setRemainingDays(String remainingDays) {
        this.remainingDays = remainingDays;
    }

    public static Comparator<Goods> sortExpDateAsc = new Comparator<Goods>() {
        @Override
        public int compare(Goods o1, Goods o2) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            Date d1 = null;
            try {
                d1 = sdf.parse(o1.getExpiredSoon());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Date d2 = null;
            try {
                d2 = sdf.parse(o2.getExpiredSoon());
            } catch (ParseException e) {
                e.printStackTrace();
            }


            return (d1.compareTo(d2));
        }
    };


}
