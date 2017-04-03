import javafx.util.Pair;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
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
 *
 * Класс SignInHandler является синглтоном.
 * Объект класса используется для входа в систему и создания нового пользователя.
 * Хранение данных о пользователе осуществляется в файле Users.xml.
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

    /**
     * @return Экземпляр SignInHandler
     * @throws IOException
     * @throws SAXException
     * @throws ParserConfigurationException
     */
    public static SignInHandler getSignInHandler() throws IOException, SAXException, ParserConfigurationException {
        if (signInHandler == null)
            signInHandler = new SignInHandler();
        return signInHandler;
    }

    /**
     * Метод для авторизации пользователя.
     * В теле метод выбирает всех пользователей, занесенных в файл (User.xml), данные, переданные в метод сравниваются с данными каждого пользователя,
     * если обнаруживается полное совпадение, создается строковый ключ из 16 символов, создается новая пара на основании булевского значения true и ключа.
     * Если совпадения нет, метод возвращает пару, состоящую из false и пустой строки.
     *
     * @param name Имя пользователя
     * @param password Пароль пользователя
     * @return Пара (Pair<Boolean, String>) где ключ пары- булевское значение (если имя и пароль верны- true, иначе- false), значение пары- сгенерированный строковый ключ.
     *
     */
    public Pair<Boolean, String> signIn(String name, String password) {
        NodeList users = doc.getElementsByTagName("user");
        String s;
        String p;
        for (int i = 0; i < users.getLength(); i++) {
            Element user = (Element) users.item(i);
            if ((s=user.getElementsByTagName("name").item(0).getTextContent()).equalsIgnoreCase(name))
                if ((p=user.getElementsByTagName("password").item(0).getTextContent()).equals(password)) {
                    StringBuilder sb = new StringBuilder();
                    for (int j = 0; j < 16; j++)
                        sb.append((char)(Math.random() * 26 + 'a'));
                    return new Pair<Boolean, String>(true, sb.toString());
                }
        }
        return new Pair<Boolean,String>(false,"");
    }

    /**
     * Метод для регистрации нового пользователя.
     * В теле метод выбирает всех пользователей из файла(Users.xml). Имя каждого пользователя сравнивается (без учета регистра).
     * Если нашлось совпадение, возвращает false, пользователь не создается. Если совпадений нет, записывает в файл данные нового пользователя, возвращает true.
     *
     * @param name Имя пользователя
     * @param password Пароль пользователя
     * @return Булевское выражение. В случае успешной регистрации новаого пользователя- true, иначе- false.
     */
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
