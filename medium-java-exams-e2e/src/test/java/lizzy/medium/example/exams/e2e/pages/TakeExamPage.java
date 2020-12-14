package lizzy.medium.example.exams.e2e.pages;

import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;
import java.util.stream.IntStream;

public class TakeExamPage {
    private final WebDriver webDriver;
    @FindBy(id = "mat-input-1")
    private WebElement comment = null;
    @FindBy(tagName = "button")
    private WebElement submit = null;
    @FindBy(tagName = "mat-card-title")
    private WebElement firstTitle = null;
    @FindBy(tagName = "mat-card-content")
    private WebElement firstContent = null;
    @FindBy(className = "question")
    private List<WebElement> questions = List.of();

    TakeExamPage(WebDriver webDriver) {
        this.webDriver = webDriver;
        PageFactory.initElements(webDriver, this);
        //Make sure spinner is not present any more
        WebDriverWait wait = new WebDriverWait(webDriver, 10);
        wait.until(ExpectedConditions.numberOfElementsToBeLessThan(By.className("cdk-overlay-backdrop"), 1));
    }

    public void assertExam(String expectedTitle, String expectedText, int questions) {
        Assertions.assertEquals(expectedTitle, firstTitle.getText());
        Assertions.assertEquals(expectedText, firstContent.getText());
        Assertions.assertEquals(questions, this.questions.size());
    }

    public MainPage fillOut(String comment, int... answers) {
        Assertions.assertFalse(submit.isEnabled());
        IntStream.range(0, answers.length).forEach(q -> answerRadioButton(q, answers[q]).click());
        this.comment.sendKeys(comment);
        Assertions.assertTrue(submit.isEnabled());
        submit.click();
        return new MainPage(webDriver);
    }

    private WebElement answerRadioButton(int question, int answer) {
        return questions
                .get(question)
                .findElements(By.className("mat-radio-label"))
                .get(answer);
    }
}
