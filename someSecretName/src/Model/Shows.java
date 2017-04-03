package Model;



import java.util.Collections;
import java.util.LinkedList;


/**
 * Created by ZitZ on 02.04.2017.
 *
 * Класс Shows представляет из себя хранилище показов указанного фильма.
 * Конструктор принимает строку- название фильма (filmName).
 * Поле filmName- название фильма.
 * Поле shows- список показов фильма.
 */
public class Shows {
    private String filmName;
    private LinkedList<Show> shows;

    /**
     *
     * @param filmName Строка- название фильма
     */
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
        Collections.sort(shows);
        return shows;
    }




}
