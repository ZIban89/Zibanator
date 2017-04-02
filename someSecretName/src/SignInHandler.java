import javafx.util.Pair;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;

/**
 * Created by ZitZ on 02.04.2017.
 */
public class SignInHandler {
    private static SignInHandler signInHandler;
    private Document doc;

    private SignInHandler() throws ParserConfigurationException, IOException, SAXException {
        this.doc = doc;
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        doc = db.parse(new File("users.xml"));
        doc.getDocumentElement();
    }

    public static SignInHandler getSignInHandler() throws IOException, SAXException, ParserConfigurationException {
        if (signInHandler == null)
            signInHandler = new SignInHandler();
        return signInHandler;
    }

    public Pair<Boolean, String> signIn(String name, String password) {
        NodeList users = doc.getElementsByTagName("user");
        String s;
        String p;
        for (int i = 0; i < users.getLength(); i++) {
            Element user = (Element) users.item(i);

            if ((s=user.getElementsByTagName("name").item(0).getTextContent()).equals(name))
                if ((p=user.getElementsByTagName("password").item(0).getTextContent()).equals(password)) {
                    StringBuilder sb = new StringBuilder();
                    for (int j = 0; j < 16; j++)
                        sb.append((char)(Math.random() * 26 + 'a'));
                    return new Pair<Boolean, String>(true, sb.toString());
                }


        }
        return new Pair<Boolean,String>(false,"");
    }

    public boolean signUp(String name, String password) {
        NodeList names=doc.getElementsByTagName("name");
        String s;
        for(int i=0; i<names.getLength();i++){
            if(names.item(i).getTextContent().equals(name))
                return false;
        }
        try {
            Element user = doc.createElement("user");
            Element nameU = doc.createElement("name");
            Element passwordU = doc.createElement("password");
            nameU.appendChild(doc.createTextNode(name));
            passwordU.appendChild(doc.createTextNode(password));
            user.appendChild(nameU);
            user.appendChild(passwordU);
            doc.getElementsByTagName("users").item(0).appendChild(user);
            doc.getDocumentElement();
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File("users.xml"));
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.transform(source, result);
            return true;
        }catch(Exception e){return false;}




    }

}
