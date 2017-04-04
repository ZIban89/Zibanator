import Model.Show;
import Model.Tickets;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.LinkedList;

/**
 * Created by ZitZ on 02.04.2017.
 */
public class BookingEditor {
    private static BookingEditor instance;
    private Document doc;
    private String fileName = "booking.xml";

    private BookingEditor() throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        doc = db.parse(new File(fileName));
        doc.getDocumentElement();
    }

    public static BookingEditor getInstance() throws IOException, SAXException, ParserConfigurationException {
        if (instance == null)
            instance = new BookingEditor();
        return instance;
    }

    /**
     * Метод для бронирования мест, добавляет бронируемые места в профиль пользователя(записывает данные в файл booking.xml.
     *
     * @param userName Имя пользователя
     * @param show     Требуемый показ
     * @param filmName Название фильма
     * @param seats    Бронируемые места, хранятся в формате r,s где r-ряд, s- место
     * @return Булевское значение, true, если запись удалась, false- если нет.
     */
    boolean addTickets(String userName, Show show, String filmName, String[] seats) {
        try {
            NodeList users = doc.getElementsByTagName("user");
            Node user = null;
            if (users.getLength() > 0) {
                for (int u = 0; u < users.getLength(); u++) {
                    if (((Element) users.item(u)).getAttribute("name").equals(userName))
                        user = users.item(u);
                }
            }
            if (user == null) {
                user = doc.createElement("user");
                Element elUser = (Element) user;
                elUser.setAttribute("name", userName);
                //((Element)doc.getElementsByTagName("users")).appendChild(user);
                doc.getFirstChild().appendChild(user);

            }

            NodeList tickets = ((Element) user).getElementsByTagName("tickets");
            Element ticket = null;
            if (tickets.getLength() > 0)
                for (int t = 0; t < tickets.getLength(); t++) {
                    Element tempTicket = (Element) tickets.item(t);
                    if (tempTicket.getAttribute("cinema").equals(show.getCinemaName()) &&
                            tempTicket.getAttribute("film").equals(filmName) &&
                            new SimpleDateFormat("dd.MM.yyyy HH:mm").parse(tempTicket.getAttribute("date")).equals(show.getDate())) {
                        ticket = tempTicket;
                        StringBuilder sb = new StringBuilder(ticket.getTextContent());
                        for (String s : seats) {
                            sb.append(" ");
                            sb.append(s);
                        }
                        ticket.setTextContent(sb.toString());
                    }
                }
            if (ticket == null) {
                Element addedTickets = doc.createElement("tickets");
                addedTickets.setAttribute("cinema", show.getCinemaName());
                addedTickets.setAttribute("date", new SimpleDateFormat("dd.MM.yyyy HH:mm").format(show.getDate()));
                addedTickets.setAttribute("film", filmName);
                StringBuilder sb = new StringBuilder(seats[0]);
                for (int i = 1; i < seats.length; i++) {
                    sb.append(" ");
                    sb.append(seats[i]);
                }
                addedTickets.setTextContent(sb.toString());
                user.appendChild(addedTickets);
            }
            XMLWriter.writeXML(doc, fileName);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * Метод для просмотра забронированных мест пользователя
     *
     * @param userName Имя пользователя.
     * @return Массив объектов Tickets- информации о брони пользователя на отдельный кинопоказ.
     */
    Tickets[] watchTickets(String userName) {
        NodeList users = doc.getElementsByTagName("user");
        Tickets[] userTickets = null;
        for (int i = 0; i < users.getLength(); i++) {
            Element user = (Element) users.item(i);
            if (user.getAttribute("name").equals(userName)) {
                NodeList tickets = user.getElementsByTagName("tickets");
                userTickets = new Tickets[tickets.getLength()];
                for (int t = 0; t < tickets.getLength(); t++) {
                    StringBuilder sb = new StringBuilder("");
                    Element ticket = (Element) tickets.item(t);
                    String cinemaName = ticket.getAttribute("cinema");
                    String filmName = ticket.getAttribute("film");
                    try {
                        Date date = new SimpleDateFormat("dd.MM.yyyy HH:mm").parse(ticket.getAttribute("date"));
                        String[] stringSeats = ticket.getTextContent().split(" ");
                        if (stringSeats.length > 0 && !stringSeats[0].equals("")) {
                            userTickets[t] = new Tickets(cinemaName, filmName, date, stringSeats);
                        }
                    } catch (Exception e) {
                    }

                }
            }
        }
        return userTickets;
    }

    /**
     * Метод для отмены брони, удаляет из файла booking.xml забронированные места на отдельный кинопоказ
     * @param userName Имя пользователя
     * @param tickets Иформация о брони
     * @param seats Места, бронь которых отменяет пользователь
     * @return Булевское значение, true, если отмена брони удалась, false, усли нет.
     * @throws ParseException
     * @throws TransformerException
     * @throws FileNotFoundException
     */
    boolean removeTicket(String userName, Tickets tickets, String[] seats) throws ParseException, TransformerException, FileNotFoundException {

        NodeList users = doc.getElementsByTagName("user");
        for (int i = 0; i < users.getLength(); i++) {
            Element user = (Element) users.item(i);
            if (user.getAttribute("name").equals(userName)) {

                NodeList ticketsNodes = user.getElementsByTagName("tickets");
                for (int j = 0; j < ticketsNodes.getLength(); j++) {

                    Element ticket = (Element) ticketsNodes.item(j);
                    if (ticket.getAttribute("cinema").equals(tickets.getCinemaName()) &&
                            ticket.getAttribute("film").equals(tickets.getFilmName()) &&
                            new SimpleDateFormat("dd.MM.yyyy HH:mm").parse(ticket.getAttribute("date")).equals(tickets.getDate())) {

                        String[] bookedTickets = ticket.getTextContent().split(" ");
                        if (bookedTickets.length > 0 && !bookedTickets[0].equals("")) {
                            LinkedList<String> bookedTicketsAsList = new LinkedList<>();
                            for (String bookedTicket : bookedTickets) {

                                boolean flag = false;
                                for (String s : seats)
                                    if (s.equals(bookedTicket))
                                        flag = true;
                                if (!flag) bookedTicketsAsList.add(bookedTicket);
                            }
                            StringBuilder sb = new StringBuilder("");
                            for (String s : bookedTicketsAsList) {
                                sb.append(s);
                                sb.append(" ");
                            }
                            if (sb.length() > 0) {
                                sb.deleteCharAt(sb.length() - 1);
                                ticket.setTextContent(sb.toString());

                            } else
                                user.removeChild(ticket);
                            XMLWriter.writeXML(doc, fileName);
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }


}
