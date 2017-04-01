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
 */
public class DOMxmlEditor {
    private static DOMxmlEditor xmlEditor;
    private DocumentBuilderFactory dbf;
    private DocumentBuilder db;
    private Document doc;

    private DOMxmlEditor() throws ParserConfigurationException, IOException, SAXException {
        dbf = DocumentBuilderFactory.newInstance();
        db = dbf.newDocumentBuilder();
        doc = db.parse(new File("timeTable.xml"));
        doc.getDocumentElement();

    }

    public static DOMxmlEditor getXmlEditor() throws IOException, SAXException, ParserConfigurationException {
        if (xmlEditor == null)
            xmlEditor = new DOMxmlEditor();
        return xmlEditor;
    }

    public Map<String,Pair<Date, Date>> getFilms() throws ParseException {
        Map<String,Pair<Date, Date>> filmsList=new HashMap<>();
        NodeList films=doc.getElementsByTagName("film");
        Element film;
        String filmName;
        Date start=null, end=null;
        for(int i=0; i < films.getLength();i++) {
            film=(Element)films.item((i));

            filmName=film.getAttributes().item(0).getNodeValue();
            NodeList showList=film.getElementsByTagName("show");

            if(showList.getLength()>0){
                SimpleDateFormat sdf=new SimpleDateFormat("d.M.y H:m");
                start=end=sdf.parse(showList.item(0).getAttributes().item(0).getNodeValue());
                for(int j = 1; j < showList.getLength(); j++){
                    Date d=sdf.parse(showList.item(j).getAttributes().item(0).getNodeValue());
                    if(d.after(end)) end=d;
                    if(start.after(d)) start=d;
                }
            }




            if(start!=null)
            filmsList.put(filmName,new Pair(start,end));
        }

        return filmsList;

    }


    public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException, ParseException {
        Map<String,Pair<Date,Date>> films=getXmlEditor().getFilms();
        for(Map.Entry<String,Pair<Date,Date>> s:films.entrySet())
            System.out.println(s.getKey()+"  "+s.getValue().getKey()+"   "+s.getValue().getKey());

    }

}