package Model;

import java.util.Arrays;
import java.util.Date;

/**
 * Created by ZitZ on 04.04.2017.
 * Класс для хранения данных о брони отдельно взятого кинопоказа.
 */
public class Tickets {

    private String cinemaName;
    private String filmName;
    private Date date;
    private String[] seats;

    /**
     * @param cinemaName Название кинотеатра
     * @param filmName   Название фильма
     * @param date       Дата показа
     * @param seats      Места, хранятся в формате r,s где r-ряд, s- место
     */
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
