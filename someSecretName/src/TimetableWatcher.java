import Model.Show;
import Model.Shows;
import javafx.util.Pair;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
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
 * <p>
 * Класс TimetableWatcher является синглотоном.
 * Объект класса используется для чтения расписания фильмов, чтения данных о наличиии мест, редактирования данных о наличии мест при бронировании места или отказа от брони.
 * Чтение/запись производится из/в файла timeTable.xml
 */

class TimetableWatcher {
    private static TimetableWatcher timetableWatcher;


    private Document doc;
    private String fileName = "timeTable.xml";

    private TimetableWatcher() throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        doc = db.parse(new File(fileName));
        doc.getDocumentElement();
    }

    /**
     * @return Экземпляр TimetableWatcher
     * @throws IOException
     * @throws SAXException
     * @throws ParserConfigurationException
     */
    static TimetableWatcher getTimetableWatcher() throws IOException, SAXException, ParserConfigurationException {
        if (timetableWatcher == null)
            timetableWatcher = new TimetableWatcher();
        return timetableWatcher;
    }

    /**
     * Метод getFilms не принимает никаких значений
     *
     * @return filmList возвращает ассоциированный массив(HashMap), заполненный различными неповторяющимися кинолентами
     * Каждый элемент(ассоциированная пара) состоит из:
     * -ключа- названия фильма(String)
     * -значения- пары дат(Pait<Date, Date), где:
     * -ключ пары- дата начала показа,
     * -значение- дата окончания показа.
     */
    HashMap<String, Pair<Date, Date>> getFilms() throws ParseException {
        HashMap<String, Pair<Date, Date>> filmsList = new HashMap<>();
        NodeList films = doc.getElementsByTagName("film");
        Element film;
        String filmName;
        Date start = null, end = null;
        for (int i = 0; i < films.getLength(); i++) {
            film = (Element) films.item((i));
            filmName = film.getAttribute("name");
            NodeList showList = film.getElementsByTagName("show");
            if (showList.getLength() > 0) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm");
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

    /**
     * @param name      Строка- название фильма
     * @param showSeats Булевское значение- обрабатывать ли наличие мест или нет
     * @return экземпляр Shows, созданный на основе выборки из расписания фильмов (файл timeTable.xml)
     * <p>
     * В теле метод создает новый экземпляр Shows, выбирает из файла xml все кинотеатры (cinemas), проходит по списку кинотеатров, выбирает все фильмы (films) для каждого кинотеатра.
     * Далее выполняется проход по списку фильмов. Если название фильма совпадает с требуемым, выбираются все показы требуемого фильма в данном кинотеатре(showList).
     * Далее вызывается метод fillShows.
     */
    Shows getFilm(String name, boolean showSeats) throws ParseException {
        Shows shows = new Shows(name);
        NodeList cinemas = doc.getElementsByTagName("cinema");
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
                                fillShows(shows, showList, cinemaName, showSeats);
                            }
                        }
                    }
            }
        return shows;
    }

    /**
     * Метод fillShows является вспомогательным для метода getFilm.
     *
     * @param shows      Объект Shows- хранилище показов требуемого фильма
     * @param showList   Коллекция узлов NodeList- список показов требуемого фильма в некоторм кинотеатре
     * @param cinemaName Строка- название кинотеатра
     * @param showSeats  Булевское значение- обрабатывать ли наличие мест или нет
     *                   <p>
     *                   В теле метод проходит по списку показов и выполняет следующие действия:
     *                   -получает дату показа,
     *                   -если showSeats равно true, то получает коллекцию узлов rowList (список рядов в зале кинотеатре), создает байтовый двухмерный массив seats,
     *                   проходит по коллекции рядов и для каждого элемента вызывает метод fillRow, в параметре передает строку- текстовое содержание узла-ряда,
     *                   заполняет полученными данными массив seats,
     *                   -создает на основании полученных данных экземпляр Show (показ требуемого фильма в данном кинотеатре в указанное время),
     *                   -добавляет созданный экземпляр Show в хранилище показов Shows.
     */
    private void fillShows(Shows shows, NodeList showList, String cinemaName, boolean showSeats) throws ParseException {
        Date date;
        byte[][] seats = null;
        SimpleDateFormat sdf = new SimpleDateFormat("d.M.y H:m");
        for (int k = 0; k < showList.getLength(); k++) {
            Element show = (Element) showList.item(k);
            date = sdf.parse(show.getAttribute("date"));
            if (showSeats) {
                NodeList rowList = show.getElementsByTagName("row");
                seats = new byte[rowList.getLength()][];
                for (int l = 0; l < rowList.getLength(); l++) {
                    Element row = (Element) rowList.item(l);
                    seats[l] = fillRow(row.getTextContent());
                }
            }
            shows.addShow(new Show(cinemaName, date, seats));
        }
    }

    /**
     * Вспомогательный метод для метода fillShows.
     *
     * @param s Строка- текстовое содержание узла-ряда.
     * @return Байтовый массив, заполненный номерами свободных мест и нулями, если место занято.
     */
    private byte[] fillRow(String s) {
        String[] rowS = s.split(" ");
        byte[] rowB = new byte[rowS.length];
        for (int i = 0; i < rowB.length; i++)
            rowB[i] = Byte.parseByte(rowS[i]);
        return rowB;
    }

    boolean bookingSeats(String filmName, Show requiredShow, String[] bookingSeats) {
        try {
            Element wantedShow = findWantedShow(filmName, requiredShow);
            if (wantedShow == null) return false;
            NodeList rows = wantedShow.getElementsByTagName("row");
            for (String s : bookingSeats) {
                String[] oneSeat = s.split(",");
                for (int i = 1; i < rows.getLength(); i++) {
                    Node rowNode = rows.item(i);
                    Element row = (Element) rowNode;
                    if (row.getAttribute("n").equals(oneSeat[0])) {
                        String seatsInRow = row.getTextContent();
                        String[] stringSeatsArray = seatsInRow.split(" ");
                        byte[] byteSeatsArray = new byte[stringSeatsArray.length];
                        byte seatN = Byte.parseByte(oneSeat[1]);
                        boolean isFree = false;
                        for (int j = 0; j < byteSeatsArray.length; j++) {
                            byte n = Byte.parseByte(stringSeatsArray[j]);
                            if (n == seatN) {
                                byteSeatsArray[j] = 0;
                                isFree = true;
                            } else
                                byteSeatsArray[j] = n;
                        }
                        if (!isFree) return false;
                        StringBuilder sb = new StringBuilder("");
                        sb.append(byteSeatsArray[0]);
                        for (int j = 1; j < byteSeatsArray.length; j++) {
                            sb.append(" ");
                            sb.append(byteSeatsArray[j]);
                        }
                        rowNode.getFirstChild().setNodeValue(sb.toString());
                        break;
                    }
                }
            }
            XMLWriter.writeXML(doc, fileName);
            return true;
        } catch (Exception e) {
            return false;
        }


    }

    private Element findWantedShow(String filmName, Show requiredShow) throws ParseException {
        Element wantedShow = null;
        NodeList cinemas = doc.getElementsByTagName("cinema");
        if (cinemas.getLength() > 0)
            for (int c = 0; c < cinemas.getLength(); c++) {
                Element cinema = (Element) cinemas.item(c);
                if (cinema.getAttribute("name").equals(requiredShow.getCinemaName())) {
                    NodeList films = cinema.getElementsByTagName("film");
                    if (films.getLength() > 0)
                        for (int f = 0; f < films.getLength(); f++) {
                            Element film = (Element) films.item(f);
                            if (film.getAttribute("name").equals(filmName)) {
                                NodeList shows = film.getElementsByTagName("show");
                                if (shows.getLength() > 0)
                                    for (int s = 0; s < shows.getLength(); s++) {
                                        Element show = (Element) shows.item(s);
                                        if (new SimpleDateFormat("dd.MM.yyyy HH:mm").parse(show.getAttribute("date")).equals(requiredShow.getDate())) {
                                            wantedShow = show;
                                            break;
                                        }
                                    }
                                break;
                            }

                        }
                    break;
                }

            }

        return wantedShow;
    }


}