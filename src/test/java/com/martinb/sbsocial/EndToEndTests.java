package com.martinb.sbsocial;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.interactions.Actions;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.FileCopyUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class EndToEndTests {

    private static ChromeDriverService service;
    private static ChromeDriver driver;

    @LocalServerPort
    private int port;

    @BeforeClass
    public static void setUp() throws IOException {
        System.setProperty("webdriver.chrome.driver", "ext/chromedriver");
        service = ChromeDriverService.createDefaultService();
        driver = new ChromeDriver(service);

        Path testResults = Paths.get("build", "test-results");
        if(!Files.exists(testResults)) {
            Files.createDirectory(testResults);
        }
    }

    @AfterClass
    public static void tearDown() {
        service.stop();
    }

    @Test
    public void homePageTest() throws IOException {
        driver.get("http://localhost:" + port);
        takeScreenshot("homePageTest-1");

        assertThat(driver.getTitle()).isEqualTo("SBSocial");

        String pageContent = driver.getPageSource();

        assertThat(pageContent).contains("<a href=\"/images/test-image-1.jpg/raw\">");

        WebElement webElement = driver.findElement(By.cssSelector("a[href*=\"test-image-1.jpg\"]"));
        Actions actions = new Actions(driver);
        actions.moveToElement(webElement).click().perform();

        takeScreenshot("homePageTest-2");
        driver.navigate().back();
    }

    private void takeScreenshot(String name) throws IOException {
        FileCopyUtils.copy(driver.getScreenshotAs(OutputType.FILE), new File("build/test-results/TEST-" + name + ".png"));
    }
}
