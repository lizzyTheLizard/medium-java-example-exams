package lizzy.medium.example.exams.e2e.pages;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


public class MainPage {
    final static String URL = "http://localhost:8080/";
    private final WebDriver webDriver;
    @FindBy(id = "mat-input-0")
    private WebElement examInput = null;
    @FindBy(xpath = "//mat-grid-tile[1]//button")
    private WebElement startButton = null;
    @FindBy(xpath = "//mat-grid-tile[2]//button")
    private WebElement createButton = null;
    @FindBy(xpath = "//mat-grid-tile[2]//input")
    private WebElement fileUpload = null;
    @FindBy(xpath = "//tr")
    private List<WebElement> myExamRows = List.of();
    @FindBy(xpath = "//tr//button")
    private List<WebElement> myExamButtons = List.of();

    MainPage(WebDriver webDriver) {
        this.webDriver = webDriver;
        PageFactory.initElements(webDriver, this);
        //Make sure spinner is not present any more
        WebDriverWait wait = new WebDriverWait(webDriver, 10);
        wait.until(ExpectedConditions.numberOfElementsToBeLessThan(By.className("cdk-overlay-backdrop"), 1));
        wait.until(ExpectedConditions.elementToBeClickable(createButton));
    }

    public static MainPage browse(WebDriver driver) {
        driver.get(URL);
        MainPage mainPage = new MainPage(driver);
        return mainPage;
    }

    public List<String> getMyExams() {
        return myExamRows.stream()
                .map(e -> e.findElement(By.tagName("td")))
                .map(WebElement::getText)
                .collect(Collectors.toList());
    }

    @SneakyThrows
    public TakeExamPage takeExam(UUID examId) {
        Assertions.assertFalse(startButton.isEnabled());
        examInput.sendKeys(examId.toString());
        Assertions.assertTrue(startButton.isEnabled());
        startButton.click();
        return new TakeExamPage(webDriver);
    }

    public void assertHasExamToAdmin(String examTitle) {
        int examsIndex = getMyExams().indexOf(examTitle);
        Assertions.assertNotEquals(-1, examsIndex, "Possibles exam titels are " + String.join(", ", getMyExams()) + " but not " + examTitle);
    }

    public AdminExamPage adminExam(String examTitle) {
        int examsIndex = getMyExams().indexOf(examTitle);
        assert examsIndex != -1;
        myExamButtons.get(examsIndex).click();
        return new AdminExamPage(webDriver);
    }

    public AdminExamPage createExam(File exam) {
        Assertions.assertTrue(createButton.isEnabled());
        fileUpload.sendKeys(exam.getAbsolutePath());
        return new AdminExamPage(webDriver);
    }

    public void assertHasNoExamToAdmin(String name) {
        Assertions.assertFalse(getMyExams().contains(name));
    }
}
