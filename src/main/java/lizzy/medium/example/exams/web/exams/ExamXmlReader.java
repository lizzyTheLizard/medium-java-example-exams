package lizzy.medium.example.exams.web.exams;

import lizzy.medium.example.exams.domain.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Component
@RequiredArgsConstructor
public class ExamXmlReader {
    private final static DocumentBuilderFactory dbf = documentBuilderFactory();
    private final ExamFactory examFactory;
    private final ExamRepository examRepository;

    private static String getText(Element root) {
        NodeList childNodes = root.getChildNodes();
        return IntStream.range(0, childNodes.getLength())
                .mapToObj(childNodes::item)
                .filter(n -> n.getNodeType() == Node.TEXT_NODE)
                .map(Node::getTextContent)
                .findFirst()
                .orElse("")
                .trim();
    }

    private static String getTitle(Element root) {
        return root.getAttribute("title");
    }

    private static int getMaxAttempt(Element root) {
        return Integer.parseInt(root.getAttribute("maxAttempts"));
    }

    private static void readQuestions(Element root, Exam exam) {
        NodeList nodes = root.getElementsByTagName("question");
        IntStream.range(0, nodes.getLength())
                .mapToObj(nodes::item)
                .filter(n -> n.getNodeType() == Node.ELEMENT_NODE)
                .filter(n -> n.getNodeName().equals("question"))
                .map(ExamXmlReader::readQuestion)
                .forEach(exam::addQuestion);
    }

    private static Question readQuestion(Node questionNode) {
        return Question.builder()
                .id(UUID.randomUUID())
                .correctOption(getCorrectOption(questionNode))
                .text(getQuestionText(questionNode))
                .options(getOptions(questionNode))
                .build();
    }

    private static int getCorrectOption(Node questionNode) {
        return Integer.parseInt(questionNode.getAttributes()
                .getNamedItem("correctSolution")
                .getNodeValue());
    }

    private static String getQuestionText(Node questionNode) {
        NodeList childNodes = questionNode.getChildNodes();
        return IntStream.range(0, childNodes.getLength())
                .mapToObj(childNodes::item)
                .filter(n -> n.getNodeType() == Node.TEXT_NODE)
                .map(Node::getTextContent)
                .findFirst()
                .orElse("")
                .trim();
    }

    private static List<String> getOptions(Node questionNode) {
        NodeList childNodes = questionNode.getChildNodes();
        return IntStream.range(0, childNodes.getLength())
                .mapToObj(childNodes::item)
                .filter(n -> n.getNodeType() == Node.ELEMENT_NODE)
                .filter(n -> n.getNodeName().equals("option"))
                .map(Node::getTextContent)
                .map(String::trim)
                .collect(Collectors.toList());
    }

    private static DocumentBuilderFactory documentBuilderFactory() {
        try {
            //Generate a safe XML factory according to https://cheatsheetseries.owasp.org/cheatsheets/XML_External_Entity_Prevention_Cheat_Sheet.html
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            dbf.setFeature("http://xml.org/sax/features/external-general-entities", false);
            dbf.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            dbf.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
            dbf.setXIncludeAware(false);
            dbf.setExpandEntityReferences(false);
            return dbf;
        } catch (ParserConfigurationException e) {
            throw new RuntimeException("Cannot initialte document builder factory", e);
        }
    }

    public Exam read(User user, InputStream in) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.parse(in);
        doc.getDocumentElement().normalize();
        Element root = doc.getDocumentElement();
        Exam result = examFactory.create()
                .id(UUID.randomUUID())
                .text(getText(root))
                .title(getTitle(root))
                .ownerId(user.getId())
                .maxAttempts(getMaxAttempt(root))
                .closed(false)
                .build();
        examRepository.add(result);
        readQuestions(root, result);
        return result;
    }
}
