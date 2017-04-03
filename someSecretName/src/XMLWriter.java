import org.w3c.dom.Document;

import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

/**
 * Created by ZitZ on 03.04.2017.
 */
class XMLWriter {
    static boolean writeXML(Document doc, String fileName) throws TransformerException, FileNotFoundException {
        doc.getDocumentElement();
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();


        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(new FileOutputStream(fileName));
        transformer.setOutputProperty(OutputKeys.METHOD, "xml");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.transform(source, result);
        return true;
    }
}
