package lizzy.medium.example.exams.e2e;

import lizzy.medium.example.exams.e2e.pages.AdminExamPage;
import lizzy.medium.example.exams.e2e.pages.LoginPage;
import lizzy.medium.example.exams.e2e.pages.MainPage;
import lizzy.medium.example.exams.e2e.pages.TakeExamPage;
import lombok.SneakyThrows;
import net.bytebuddy.utility.RandomString;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class SeleniumTest {
    private final static String ZAP_PROXY_PORT = System.getProperty("zap_proxy", "51432");
    private final static String createExamDescription = "Explain!";
    private static FirefoxDriver driver;
    private AdminExamPage adminExamPage;
    private String createdExamName;

    @BeforeAll
    static void beforeTest() {
        FirefoxBinary firefoxBinary = new FirefoxBinary();
        firefoxBinary.addCommandLineOptions("--headless");
        firefoxBinary.addCommandLineOptions("--no-sandbox");
        System.setProperty("webdriver.gecko.driver", "drivers/geckodriver-linux-64bit");
        FirefoxOptions firefoxOptions = new FirefoxOptions();
        firefoxOptions.setBinary(firefoxBinary);
        if(ZAP_PROXY_PORT != null && !ZAP_PROXY_PORT.isEmpty()) {
            firefoxOptions.addPreference("network.proxy.allow_hijacking_localhost", true);
            firefoxOptions.setProxy(new Proxy()
                    .setHttpProxy("localhost:" + ZAP_PROXY_PORT)
                    .setSslProxy("localhost:" + ZAP_PROXY_PORT)
                    .setNoProxy("*.mozilla.net,*.firefox.com"));
        }
        driver = new FirefoxDriver(firefoxOptions);
        MainPage mainPage = LoginPage.login(driver);
        mainPage.getMyExams().forEach(examName -> mainPage.adminExam(examName).close());
    }

    @AfterAll
    static void afterTest() {
        driver.quit();
    }

    private static String getRandomString() {
        return RandomString.make(7);
    }

    @BeforeEach
    @SneakyThrows
    void createExam(@TempDir File tmpDir) {
        File examFile = new File(tmpDir, "exam.xml");
        createdExamName = "Test Name " + getRandomString();
        FileUtils.writeStringToFile(examFile, "<?xml version=\"1.0\" ?>\n" +
                "<jdbcExam maxAttempts=\"1\" title=\"" + createdExamName + "\">\n" +
                "   " + createExamDescription + "\n" +
                "   <question correctSolution=\"0\">\n" +
                "       Question 1\n" +
                "       <option>Answer 1</option>\n" +
                "   </question>\n" +
                "   <question correctSolution=\"1\">\n" +
                "       Question 2\n" +
                "       <option>Answer 1</option>\n" +
                "       <option>Answer 2</option>\n" +
                "   </question>\n" +
                "</jdbcExam>\n", StandardCharsets.UTF_8);
        MainPage mainPage = MainPage.browse(driver);
        adminExamPage = mainPage.createExam(examFile);
        adminExamPage.assertNoParticipation();
    }

    @Test
    void getExistingExam() {
        UUID examId = adminExamPage.getUUID();
        MainPage mainPage = MainPage.browse(driver);
        TakeExamPage takeExamPage = mainPage.takeExam(examId);
        takeExamPage.assertExam(createdExamName, createExamDescription, 2);

        MainPage.browse(driver);
        mainPage.assertHasExamToAdmin(createdExamName);
        mainPage.adminExam(createdExamName);
    }

    @Test
    void createAndFillSuccessfulExam() {
        String randomComment = getRandomString();
        TakeExamPage takeExamPage = adminExamPage.takeExam();
        MainPage mainPage = takeExamPage.fillOut(randomComment, 0, 1);

        mainPage.adminExam(createdExamName);
        Assertions.assertTrue(adminExamPage.hasParticipation(LoginPage.USER_ID));
        AdminExamPage.Participation p = adminExamPage.getParticipation(LoginPage.USER_ID);
        Assertions.assertEquals("PASSED", p.getResult());
        Assertions.assertEquals(randomComment, p.getComment());
    }

    @Test
    void createAndFillFailExam() {
        String randomComment = getRandomString();
        TakeExamPage takeExamPage = adminExamPage.takeExam();
        MainPage mainPage = takeExamPage.fillOut(randomComment, 0, 0);

        mainPage.adminExam(createdExamName);
        Assertions.assertTrue(adminExamPage.hasParticipation(LoginPage.USER_ID));
        AdminExamPage.Participation p = adminExamPage.getParticipation(LoginPage.USER_ID);
        Assertions.assertEquals("FAILED", p.getResult());
        Assertions.assertEquals(randomComment, p.getComment());
    }

    @Test
    void createAndDeleteExam() {
        MainPage mainPage = MainPage.browse(driver);
        mainPage.assertHasExamToAdmin(createdExamName);
        mainPage.adminExam(createdExamName).close();
        mainPage.assertHasNoExamToAdmin(createdExamName);
    }
}
