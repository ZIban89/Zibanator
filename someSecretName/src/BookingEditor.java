import Model.Show;
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

import java.text.SimpleDateFormat;

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

    boolean addTickets(String userName, Show show, String filmName, String[] seats) {
        try {
            NodeList users = doc.getElementsByTagName("user");
            Node user = null;
            if (users.getLength() > 0) {
                for (int u = 0; u < users.getLength(); u++) {
                    if (((Element) users.item(u)).getAttribute("name").equals(userName))
                        user = users.item(u);
                }
                if (user == null) {
                    user = doc.createElement("user");
                    Element elUser = (Element) user;
                    elUser.setAttribute("name", userName);
                    doc.getElementsByTagName("users").item(0).appendChild(user);
                }
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

    String[] watchTickets(String userName) {
        NodeList users = doc.getElementsByTagName("user");
        String[] userTickets = null;
        for (int i = 0; i < users.getLength(); i++) {
            Element user = (Element) users.item(i);
            if (user.getAttribute("name").equals(userName)) {
                NodeList tickets = user.getElementsByTagName("tickets");
                userTickets = new String[tickets.getLength()];
                for (int t = 0; t < tickets.getLength(); t++) {
                    StringBuilder sb = new StringBuilder("");
                    Element ticket = (Element) tickets.item(t);
                    sb.append("Фильм ").append(ticket.getAttribute("film")).append(" в кинотеатре ").append(ticket.getAttribute("cinema")).
                            append(" состоится ").append(ticket.getAttribute("date")).append(". Забронированные Вами места(ряд,место): ").append(ticket.getTextContent());
                    userTickets[t] = sb.toString();
                }
            }
        }
        return userTickets;
    }


}
