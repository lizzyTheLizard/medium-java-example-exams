package lizzy.medium.example.exams.e2e.pages;

import lombok.SneakyThrows;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.UUID;

public class LoginPage {
    public static final UUID USER_ID = UUID.fromString("64d066bb-a799-4904-81dc-0e851bef2a5f");
    private final WebDriver webDriver;
    @FindBy(name = "username")
    private WebElement username = null;
    @FindBy(name = "password")
    private WebElement password = null;
    @FindBy(name = "login")
    private WebElement login = null;

    private LoginPage(WebDriver webDriver) {
        this.webDriver = webDriver;
        PageFactory.initElements(webDriver, this);
        new WebDriverWait(webDriver, 10).until(ExpectedConditions.elementToBeClickable(login));
    }

    public static MainPage login(WebDriver driver) {
        driver.get(MainPage.URL);
        LoginPage page = new LoginPage(driver);
        return page.login();
    }

    @SneakyThrows
    private MainPage login() {
        username.sendKeys("user");
        password.sendKeys("test");
        login.click();
        return new MainPage(webDriver);
    }
}
