import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class EndToEndTestSubmitGrades {

    public static final String CHROME_DRIVER_FILE_LOCATION = "C:/chromedriver_win32/chromedriver.exe";
    public static final String URL = "http://localhost:3000";
    public static final int SLEEP_DURATION = 1000; // 1 second.
    public static final String NEW_ASSIGNMENT_NAME = "New Assignment";
    public static final String UPDATED_ASSIGNMENT_NAME = "Updated Assignment";
    public static final String NEW_DUE_DATE = "2024-12-31";

    WebDriver driver;

    @BeforeEach
    public void testSetup() throws Exception {
        System.setProperty("webdriver.chrome.driver", CHROME_DRIVER_FILE_LOCATION);
        ChromeOptions ops = new ChromeOptions();
        ops.addArguments("--remote-allow-origins=*");
        driver = new ChromeDriver(ops);

        driver.get(URL);
        new WebDriverWait(driver, 10).until(ExpectedConditions.presenceOfElementLocated(By.tagName("body")));
    }


    @AfterEach
    public void cleanup() {
        if (driver != null) {
            driver.close();
            driver.quit();
        }
    }
}
