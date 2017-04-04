package Model;

import java.util.Arrays;
import java.util.Date;

/**
 * Created by ZitZ on 04.04.2017.
 */
public class Tickets {

    private String cinemaName;
    private String filmName;
    private Date date;
    private String[] seats;

    public Tickets(String cinemaName, String filmName, Date date, String[] seats) {
        this.cinemaName = cinemaName;
        this.filmName = filmName;
        this.date = date;
        this.seats = seats;
        Arrays.sort(seats);
    }

    public String getCinemaName() {
        return cinemaName;
    }

    public String getFilmName() {
        return filmName;
    }

    public Date getDate() {
        return date;
    }

    public String[] getSeats() {
        return seats;
    }


}
