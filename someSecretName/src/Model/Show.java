package Model;

import java.util.Date;

/**
 * Created by ZitZ on 02.04.2017.
 *
 * Класс Show представляет из себя хранилище информации по отдельному показу фильма.
 * Класс реализует интерфейс Comparable, переопределен метод compareTo, что позволяет сортировать показы:
 *  -в зависимости от названия кинотеатра,
 *  -если названия кинотеатров одинаковы- в зависимости от даты показа.
 */

public class Show implements Comparable{
    private String cinemaName;
    private Date date;
    private byte[][] seats;

    /**
     *
     * @param cinemaName Название кинотеатра (cinemaName)
     * @param date Дата и время показа
     * @param seats Двухмерный байтовый массив- сидячие места кинозала
     */
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

    @Override
    public int compareTo(Object o) {
        Show s=(Show) o;
        int n= this.getCinemaName().compareTo(s.getCinemaName());
        if(n==0) return this.getDate().after(s.getDate())?1:-1;
        return n;
    }
}
