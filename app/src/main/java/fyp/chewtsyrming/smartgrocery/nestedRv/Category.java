package fyp.chewtsyrming.smartgrocery.nestedRv;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class Category {
    List<Goods> list;
    String genre;
    String type;

    public Category(List<Goods> list, String genre, String type) {
        this.list = list;
        this.genre = genre;
        this.type = type;
    }

    public List<Goods> getList() {
        return list;
    }

    public String getGenre() {
        return genre;
    }

    public String getType() {
        return type;
    }

}
