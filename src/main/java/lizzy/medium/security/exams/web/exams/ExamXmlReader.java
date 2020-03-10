package lizzy.medium.security.exams.web.exams;

import lizzy.medium.security.exams.domain.Exam;
import lizzy.medium.security.exams.domain.ExamFactory;
import lizzy.medium.security.exams.domain.ExamRepository;
import lizzy.medium.security.exams.domain.Question;
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
import java.security.Principal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

//TODO This is unsafe code, it contains unsafe XML-deserialization

@Component
@RequiredArgsConstructor
public class ExamXmlReader {
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

    public Exam read(Principal principal, InputStream in) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.parse(in);
        doc.getDocumentElement().normalize();
        Element root = doc.getDocumentElement();
        Exam result = examFactory.create()
                .id(UUID.randomUUID())
                .text(getText(root))
                .title(getTitle(root))
                .owner(principal.getName())
                .maxAttempts(getMaxAttempt(root))
                .closed(false)
                .build();
        examRepository.add(result);
        readQuestions(root, result);
        return result;
    }
}
