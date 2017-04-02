import Model.Show;
import Model.Shows;
import javafx.util.Pair;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by ZitZ on 01.04.2017.
 */
public class TimetableEditor {
    private static TimetableEditor timetableEditor;


    private Document doc;

    private TimetableEditor() throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        doc = db.parse(new File("timeTable.xml"));
        doc.getDocumentElement();

    }

    public static TimetableEditor getTimetableEditor() throws IOException, SAXException, ParserConfigurationException {
        if (timetableEditor == null)
            timetableEditor = new TimetableEditor();
        return timetableEditor;
    }

    public Map<String, Pair<Date, Date>> getFilms() throws ParseException {
        Map<String, Pair<Date, Date>> filmsList = new HashMap<>();
        NodeList films = doc.getElementsByTagName("film");
        Element film;
        String filmName;
        Date start = null, end = null;
        for (int i = 0; i < films.getLength(); i++) {
            film = (Element) films.item((i));
            filmName = film.getAttribute("name");
            NodeList showList = film.getElementsByTagName("show");
            if (showList.getLength() > 0) {
                SimpleDateFormat sdf = new SimpleDateFormat("d.M.y H:m");
                start = end = sdf.parse(showList.item(0).getAttributes().item(0).getNodeValue());
                for (int j = 1; j < showList.getLength(); j++) {
                    Date d = sdf.parse(showList.item(j).getAttributes().item(0).getNodeValue());
                    if (d.after(end)) end = d;
                    if (start.after(d)) start = d;
                }
                if (filmsList.containsKey(filmName)) {
                    start = start.after(filmsList.get(filmName).getKey()) ? filmsList.get(filmName).getKey() : start;
                    end = end.after(filmsList.get(filmName).getValue()) ? end : filmsList.get(filmName).getValue();
                }
                filmsList.put(filmName, new Pair<>(start, end));
            }
        }
        return filmsList;

    }

    public Shows getFilm(String name) throws ParseException {
        NodeList cinemas = doc.getElementsByTagName("cinema");
        Shows shows = new Shows(name);
        if (cinemas.getLength() > 0)
            for (int i = 0; i < cinemas.getLength(); i++) {
                Element cinema = (Element) cinemas.item(i);
                NodeList films = cinema.getElementsByTagName("film");
                if (films.getLength() > 0)
                    for (int j = 0; j < films.getLength(); j++) {
                        Element film = (Element) films.item(j);
                        if (film.getAttributes().item(0).getNodeValue().equals(name)) {
                            String cinemaName = cinema.getAttribute("name");
                            NodeList showList = film.getElementsByTagName("show");
                            if (showList.getLength() > 0) {
                                fillShows(shows, showList, cinemaName);
                            }
                        }

                    }
            }
        return shows;
    }

    private void fillShows(Shows shows, NodeList showList, String cinemaName) throws ParseException {
        Date date;
        byte[][] seats;
        SimpleDateFormat sdf = new SimpleDateFormat("d.M.y H:m");
        for (int k = 0; k < showList.getLength(); k++) {
            Element show = (Element) showList.item(k);
            date = sdf.parse(show.getAttribute("date"));
            NodeList rowList = show.getElementsByTagName("row");
            seats = new byte[rowList.getLength()][];
            for (int l = 0; l < rowList.getLength(); l++) {
                Element row = (Element) rowList.item(l);
                seats[l] = fillSeats(row.getTextContent());
            }
            shows.addShow(new Show(cinemaName, date, seats));
        }
    }

    private byte[] fillSeats(String s) {
        String[] rowS = s.split(" ");
        byte[] rowB = new byte[rowS.length];
        for (int i = 0; i < rowB.length; i++)
            rowB[i] = Byte.parseByte(rowS[i]);
        return rowB;
    }

    public String bookTicket(User){

    }




    public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException, ParseException {
        Map<String, Pair<Date, Date>> films = getTimetableEditor().getFilms();
        SimpleDateFormat format = new SimpleDateFormat("dd.MM");
        for (Map.Entry<String, Pair<Date, Date>> s : films.entrySet())
            System.out.println(s.getKey() + "  " + format.format(s.getValue().getKey()) + "   " + format.format(s.getValue().getValue()));
        Shows shows = getTimetableEditor().getFilm("Avatar");
        for (Show s : shows.getShows()) {
            System.out.println("Фильм " + shows.getFilmName() + " в кинотеатре " + s.getCinemaName() + " " + s.getDate());
            for (byte[] b : s.getSeats()) {
                System.out.print("Ряд №" + " [eq pyftn    ");
                for (byte c : b)
                    System.out.print(c + " ");
                System.out.println();
            }
            System.out.println();

        }

    }

}