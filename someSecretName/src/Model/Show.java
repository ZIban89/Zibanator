package Model;

import java.util.Date;

/**
 * Created by ZitZ on 02.04.2017.
 */
public class Show {
    private String cinemaName;
    private Date date;
    private byte[][] seats;

    public Show(String cinemaName, Date date, byte[][] seats) {
        this.cinemaName = cinemaName;
        this.date = date;
        this.seats = seats;
    }

    public String getCinemaName() {
        return cinemaName;
    }

    public Date getDate() {
        return date;
    }

    public byte[][] getSeats() {
        return seats;
    }
}
