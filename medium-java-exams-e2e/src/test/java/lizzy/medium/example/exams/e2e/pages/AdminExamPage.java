package lizzy.medium.example.exams.e2e.pages;

import lombok.Value;
import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class AdminExamPage {
    private final WebDriver webDriver;
    @FindBy(className = "mat-warn")
    private WebElement closeButton = null;
    @FindBy(tagName = "a")
    private WebElement examLink = null;

    AdminExamPage(WebDriver webDriver) {
        this.webDriver = webDriver;
        PageFactory.initElements(webDriver, this);
        //Make sure spinner is not present any more
        WebDriverWait wait = new WebDriverWait(webDriver, 10);
        wait.until(ExpectedConditions.numberOfElementsToBeLessThan(By.className("cdk-overlay-backdrop"), 1));
        wait.until(ExpectedConditions.elementToBeClickable(closeButton));
    }

    public TakeExamPage takeExam() {
        examLink.click();
        return new TakeExamPage(webDriver);
    }

    public boolean hasParticipation(UUID userId) {
        return getParticipation().stream()
                .anyMatch(p -> p.getUser().contains(userId.toString()));
    }

    public void assertNoParticipation() {
        Assertions.assertEquals(0, getParticipation().size());
    }

    public List<Participation> getParticipation() {
        return webDriver.findElements(By.tagName("tr")).stream()
                .map(e -> e.findElements(By.tagName("td")))
                .map(l -> new Participation(l.get(0).getText(), l.get(1).getText(), l.get(2).getText()))
                .collect(Collectors.toList());
    }

    public Participation getParticipation(UUID userId) {
        return getParticipation().stream()
                .filter(p -> p.getUser().contains(userId.toString()))
                .findFirst()
                .orElseThrow();
    }

    public UUID getUUID() {
        return UUID.fromString(examLink.getText().substring(examLink.getText().length() - 36));
    }

    @SuppressWarnings("UnusedReturnValue")
    public MainPage close() {
        closeButton.click();
        return new MainPage(webDriver);
    }

    @Value
    public static class Participation {
        String user;
        String result;
        String comment;
    }
}
