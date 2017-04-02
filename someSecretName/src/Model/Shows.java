package Model;



import java.util.Collections;
import java.util.LinkedList;


/**
 * Created by ZitZ on 02.04.2017.
 */
public class Shows {
    private String filmName;
    private LinkedList<Show> shows;

    public Shows(String filmName) {
        this.filmName = filmName;
        shows=new LinkedList<>();
    }
    public void addShow(Show show){
        shows.add(show);
    }

    public String getFilmName() {
        return filmName;
    }

    public LinkedList<Show> getShows() {
        return shows;
    }

    public void sort(){
        Collections.sort(shows);

    }


}
