package demo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
// import io.github.bonigarcia.wdm.WebDriverManager;
import demo.wrappers.Wrappers;

public class TestCases {
    ChromeDriver driver;

    /*
     * TODO: Write your tests here with testng @Test annotation.
     * Follow `testCase01` `testCase02`... format or what is provided in
     * instructions
     */

    /*
     * Do not change the provided methods unless necessary, they will help in
     * automation and assessment
     */
    @BeforeTest
    public void startBrowser() {
        System.setProperty("java.util.logging.config.file", "logging.properties");

        // NOT NEEDED FOR SELENIUM MANAGER
        // WebDriverManager.chromedriver().timeout(30).setup();

        ChromeOptions options = new ChromeOptions();
        LoggingPreferences logs = new LoggingPreferences();

        logs.enable(LogType.BROWSER, Level.ALL);
        logs.enable(LogType.DRIVER, Level.ALL);
        options.setCapability("goog:loggingPrefs", logs);
        options.addArguments("--remote-allow-origins=*");

        System.setProperty(ChromeDriverService.CHROME_DRIVER_LOG_PROPERTY, "build/chromedriver.log");

        driver = new ChromeDriver(options);

        driver.manage().window().maximize();
    }

    @Test(enabled = true)
    public void testCase01() throws InterruptedException, JsonProcessingException {

        Wrappers wrappers = new Wrappers(driver, 20);

        driver.get("https://www.scrapethissite.com/pages/");
        wrappers.clickElement(By.xpath("//a[text()=\"Hockey Teams: Forms, Searching and Pagination\"]"));

        // List<WebElement> teamNames =
        // driver.findElements(By.xpath("//*[@id=\"hockey\"]/div/table/tbody/tr/td[1]"));
        // List<WebElement> year =
        // driver.findElements(By.xpath("//*[@id=\"hockey\"]/div/table/tbody/tr/td[2]"));
        // List<WebElement> winPercentage =
        // driver.findElements(By.xpath("//*[@id=\"hockey\"]/div/table/tbody/tr/td[6]"));

        // WebElement clickOnNextPage =
        // driver.findElement(By.xpath("//*[@id=\"hockey\"]/div/div[5]/div[1]/ul/li/a"));

        // ArrayList to hold HashMap objects for each scraped row that meets the
        // criteria.
        List<HashMap<String, Object>> resultsList = new ArrayList<>();

        // Iterate from page 2 to page 4
        for (int page = 2; page <= 4; page++) {
            // Use the exact XPath provided to get all pagination links
            List<WebElement> paginationLinks = driver
                    .findElements(By.xpath("//*[@id='hockey']/div/div[5]/div[1]/ul/li/a"));

            // Loop through the pagination links and click the one matching the current page
            // number
            for (WebElement pageLink : paginationLinks) {

                if (pageLink.getText().trim().equals(String.valueOf(page))) {
                    pageLink.click();
                    break;
                }
            }

            // Wait for the page to load (adjust the sleep time if needed)
            Thread.sleep(2000);

            // Locate the table body element from which to scrape data
            WebElement tableBody = wrappers.getVisibleElement(By.xpath("//*[@id='hockey']/div/table/tbody"));

            // Retrieve lists of elements for team names, years, and win percentages
            List<WebElement> teamNames = tableBody.findElements(By.xpath("./tr/td[1]"));
            List<WebElement> years = tableBody.findElements(By.xpath("./tr/td[2]"));
            List<WebElement> winPercentages = tableBody.findElements(By.xpath("./tr/td[6]"));

            // Process each row in the table
            for (int i = 0; i < teamNames.size(); i++) {
                String teamName = teamNames.get(i).getText();
                String year = years.get(i).getText();
                double winPercentage = Double.parseDouble(winPercentages.get(i).getText().trim());

                // Check if win percentage is less than 40% (i.e. less than 0.4)
                if (winPercentage < 0.4) {
                    HashMap<String, Object> dataMap = new HashMap<>();
                    // Get current epoch time in seconds
                    long epoch = System.currentTimeMillis() / 1000;
                    dataMap.put("epochTime", epoch);
                    dataMap.put("teamName", teamName);
                    dataMap.put("year", year);
                    dataMap.put("win %", winPercentage);
                    resultsList.add(dataMap);
                }
            }
        }

        // Convert the ArrayList of HashMaps to JSON using Jackson
        ObjectMapper mapper = new ObjectMapper();
        String jsonResult = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(resultsList);
        System.out.println(jsonResult);

        // Write JSON to a file named hockey-team-data.json
        try (FileWriter fileWriter = new FileWriter("hockey-team-data.json")) {
            fileWriter.write(jsonResult);
            System.out.println("JSON data written to hockey-team-data.json");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Test(enabled = true)
    public void testCase02() throws Exception {
        Wrappers wrappers = new Wrappers(driver, 20);
        // WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        driver.get("https://www.scrapethissite.com/pages/");
        // List<WebElement> clickOnYears =
        // driver.findElements(By.xpath("//div[@class=\"col-md-12
        // text-center\"]/child::a"));
        // List<WebElement> filmTitle =
        // driver.findElements(By.xpath("//table/tbody/tr/td[1]"));
        // List<WebElement> nominations =
        // driver.findElements(By.xpath("//table/tbody/tr/td[2]"));
        // List<WebElement> awards =
        // driver.findElements(By.xpath("//table/tbody/tr/td[3]"));
        // List<WebElement> bestPicture =
        // driver.findElements(By.xpath("//table/tbody/tr/td[4]"));
        // WebElement flag = driver.findElement(By.xpath("//i[@class=\"glyphicon
        // glyphicon-flag\"]"));

        // Click on the Oscar Winning Films link
        wrappers.clickElement(By.xpath("//a[text()=\"Oscar Winning Films: AJAX and Javascript\"]"));

        // Wait until the year links are visible and then fetch them
        wrappers.getVisibleElement(By.xpath("//div[@class='col-md-12 text-center']/a"));
        List<WebElement> yearLinks = driver.findElements(By.xpath("//div[@class='col-md-12 text-center']/a"));

        // Prepare the list to hold movie data
        List<HashMap<String, Object>> moviesData = new ArrayList<>();

        // Iterate over each year link; re-find element each time to avoid stale element
        // exceptions
        for (int i = 0; i < yearLinks.size(); i++) {
            // Re-find the year link by index (XPath indices start at 1)
            WebElement yearLink = driver
                    .findElement(By.xpath("(//div[@class='col-md-12 text-center']/a)[" + (i + 1) + "]"));
            String yearText = yearLink.getText().trim();

            // Click the year link
            yearLink.click();

            // Wait for table to be visible
            wrappers.getVisibleElement(By.xpath("//table[@class='table']"));

            // Find all rows in the table
            List<WebElement> rows = driver.findElements(By.xpath("//table[@class='table']/tbody/tr"));

            // Process top 5 movies (or fewer if less rows)
            int moviesToProcess = Math.min(5, rows.size());
            for (int j = 0; j < moviesToProcess; j++) {
                WebElement row = rows.get(j);

                // Extract Title, Nominations, and Awards text
                String title = row.findElement(By.xpath("./td[1]")).getText().trim();
                String nomination = row.findElement(By.xpath("./td[2]")).getText().trim();
                String awards = row.findElement(By.xpath("./td[3]")).getText().trim();

                // Determine if the movie is Best Picture winner
                // Check if td[4] contains an <i> element with class containing "glyphicon-flag"
                boolean isWinner = false;
                List<WebElement> flagIcons = row
                        .findElements(By.xpath("./td[4]//i[contains(@class, 'glyphicon-flag')]"));
                if (!flagIcons.isEmpty()) {
                    isWinner = true;
                }

                // Create a map to store movie details
                HashMap<String, Object> movieMap = new HashMap<>();
                movieMap.put("epochTime", System.currentTimeMillis() / 1000);
                movieMap.put("year", yearText);
                movieMap.put("title", title);
                movieMap.put("nomination", nomination);
                movieMap.put("awards", awards);
                movieMap.put("isWinner", isWinner);

                // Add the map to our list
                moviesData.add(movieMap);
            }
        }
        // Convert the ArrayList of HashMaps to JSON using Jackson
        ObjectMapper mapper = new ObjectMapper();
        String jsonResult = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(moviesData);
        System.out.println(jsonResult);

        // Write JSON to a file named hockey-team-data.json
        try (FileWriter fileWriter = new FileWriter("oscar-winner-data.json")) {
            fileWriter.write(jsonResult);
            System.out.println("JSON data written to oscar-winner-data.json");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @AfterTest
    public void endTest() {
        driver.close();
        driver.quit();

    }
}