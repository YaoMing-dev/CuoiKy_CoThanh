package com.mycompany.nhom08;

import static org.testng.Assert.*;
import org.testng.annotations.*;
import org.testng.ITestResult;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Nhom08NGTest {
    
    private WebDriver driver;
    private WebDriverWait wait;
    private static final String BASE_URL = "https://www.saucedemo.com/";
    private long startTime;
    
    private static final String VALID_USERNAME = "standard_user";
    private static final String VALID_PASSWORD = "secret_sauce";
    
    public Nhom08NGTest() {
    }
    
    @BeforeSuite
    public void setupSuite() throws InterruptedException {
        Nhom08.CSVReporter.clear();
        System.out.println("=".repeat(60));
        System.out.println("BAT DAU CHAY 20 TEST CASES - SAUCEDEMO.COM");
        System.out.println("(16 PASS + 4 FAIL = 20% FAIL RATE)");
        System.out.println("=".repeat(60));
        
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.addArguments("--disable-save-password-bubble");
        options.addArguments("--disable-features=PasswordManager,PasswordCheck,PasswordLeakDetection");
        options.addArguments("--incognito");
      
        Map<String, Object> prefs = new HashMap<>();
        prefs.put("credentials_enable_service", false);
        prefs.put("profile.password_manager_enabled", false);
        options.setExperimentalOption("prefs", prefs);
        options.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});
        
        driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        
        System.out.println("\n[SETUP] Dang nhap 1 lan duy nhat...");
        driver.get(BASE_URL);
        Thread.sleep(2000);
        
        driver.findElement(By.id("user-name")).sendKeys(VALID_USERNAME);
        Thread.sleep(500);
        driver.findElement(By.id("password")).sendKeys(VALID_PASSWORD);
        Thread.sleep(500);
        driver.findElement(By.id("login-button")).click();
        Thread.sleep(3000);
        
        wait.until(ExpectedConditions.urlContains("inventory.html"));
        System.out.println("[SETUP] Da dang nhap thanh cong - Bat dau chay test");
    }
    
    @BeforeMethod
    public void setUpMethod() throws Exception {
        startTime = System.currentTimeMillis();
        
        if (!driver.getCurrentUrl().contains("inventory.html")) {
            driver.get(BASE_URL + "inventory.html");
            Thread.sleep(2000);
        } else {
            driver.navigate().refresh();
            Thread.sleep(2000);
        }
    }
    
    @AfterMethod
    public void tearDownMethod(ITestResult result) throws Exception {
        long duration = System.currentTimeMillis() - startTime;
        String testName = result.getMethod().getMethodName();
        String status = result.isSuccess() ? "PASS" : "FAIL";
        
        Nhom08.CSVReporter.addResult(testName, status, duration);
        System.out.println(">> " + testName + ": " + status + " (" + duration + "ms)");
    }
    
    @AfterSuite
    public void finish() {
        System.out.println("=".repeat(60));
        Nhom08.CSVReporter.writeToCSV();
        System.out.println("=".repeat(60));
        
        if (driver != null) {
            driver.quit();
            System.out.println("Da dong browser");
        }
    }
    
    @Test(priority = 1)
    public void TC01() throws InterruptedException {
        System.out.println("\n[TC01] Kiem tra tieu de trang");
        
        WebElement title = wait.until(ExpectedConditions.presenceOfElementLocated(By.className("title")));
        assertEquals(title.getText(), "Products");
        
        System.out.println("[TC01] PASS");
    }

    @Test(priority = 2)
    public void TC02() throws InterruptedException {
        System.out.println("\n[TC02] Kiem tra inventory container");
        
        WebElement inventoryContainer = driver.findElement(By.className("inventory_list"));
        assertTrue(inventoryContainer.isDisplayed());
        
        System.out.println("[TC02] PASS");
    }

    @Test(priority = 3)
    public void TC03() throws InterruptedException {
        System.out.println("\n[TC03] Kiem tra them san pham vao gio");
        
        WebElement addButton = driver.findElement(By.id("add-to-cart-sauce-labs-backpack"));
        addButton.click();
        Thread.sleep(1000);
        
        WebElement removeButton = driver.findElement(By.id("remove-sauce-labs-backpack"));
        assertTrue(removeButton.isDisplayed());
        
        System.out.println("[TC03] PASS");
    }

  @Test(priority = 4)
public void TC04() throws InterruptedException {
    System.out.println("\n[TC04] Kiem tra mo ta san pham");
    
    List<WebElement> descriptions = driver.findElements(By.className("inventory_item_desc"));
    
    assertTrue(descriptions.size() == 6);
    for (WebElement desc : descriptions) {
        assertTrue(desc.isDisplayed());
        assertFalse(desc.getText().isEmpty());
    }
    
    System.out.println("[TC04] PASS");
}


    @Test(priority = 5)
    public void TC05() throws InterruptedException {
        System.out.println("\n[TC05] Kiem tra chuyen den trang gio hang");
        
        driver.findElement(By.className("shopping_cart_link")).click();
        Thread.sleep(2000);
        
        assertTrue(driver.getCurrentUrl().contains("cart.html"));
        
        WebElement cartTitle = driver.findElement(By.className("title"));
        assertEquals(cartTitle.getText(), "Your Cart");
        
        System.out.println("[TC05] PASS");
    }

    @Test(priority = 6)
    public void TC06() throws InterruptedException {
        System.out.println("\n[TC06] Kiem tra quay ve trang san pham");
        
        driver.findElement(By.className("shopping_cart_link")).click();
        Thread.sleep(1000);
        
        driver.findElement(By.id("continue-shopping")).click();
        Thread.sleep(2000);
        
        assertTrue(driver.getCurrentUrl().contains("inventory.html"));
        
        System.out.println("[TC06] PASS");
    }

    @Test(priority = 7)
    public void TC07() throws InterruptedException {
        System.out.println("\n[TC07] Kiem tra xem chi tiet san pham");
        
        List<WebElement> productNames = driver.findElements(By.className("inventory_item_name"));
        productNames.get(0).click();
        Thread.sleep(2000);
        
        assertTrue(driver.getCurrentUrl().contains("inventory-item.html"));
        
        WebElement productDetail = driver.findElement(By.className("inventory_details_name"));
        assertTrue(productDetail.isDisplayed());
        
        System.out.println("[TC07] PASS");
    }

    @Test(priority = 8)
    public void TC08() throws InterruptedException {
        System.out.println("\n[TC08] Kiem tra nut back tu chi tiet");
        
        List<WebElement> productNames = driver.findElements(By.className("inventory_item_name"));
        productNames.get(0).click();
        Thread.sleep(1500);
        
        WebElement backButton = driver.findElement(By.id("back-to-products"));
        backButton.click();
        Thread.sleep(2000);
        
        assertTrue(driver.getCurrentUrl().contains("inventory.html"));
        
        System.out.println("[TC08] PASS");
    }

    @Test(priority = 9)
    public void TC09() throws InterruptedException {
        System.out.println("\n[TC09] Kiem tra so luong san pham");
        
        List<WebElement> products = driver.findElements(By.className("inventory_item"));
        assertEquals(products.size(), 6);
        
        System.out.println("[TC09] PASS");
    }

@Test(priority = 10)
public void TC10() throws InterruptedException {
    System.out.println("\n[TC10] Kiem tra app logo");
    
    WebElement appLogo = driver.findElement(By.className("app_logo"));
    
    assertTrue(appLogo.isDisplayed());
    assertEquals(appLogo.getText(), "Swag Labs");
    
    System.out.println("[TC10] PASS");
}

    @Test(priority = 11)
    public void TC11() throws InterruptedException {
        System.out.println("\n[TC11] Kiem tra ten san pham hien thi");
        
        List<WebElement> productNames = driver.findElements(By.className("inventory_item_name"));
        
        assertTrue(productNames.size() > 0);
        for (WebElement name : productNames) {
            assertTrue(name.isDisplayed());
            assertFalse(name.getText().isEmpty());
        }
        
        System.out.println("[TC11] PASS");
    }

    @Test(priority = 12)
    public void TC12() throws InterruptedException {
        System.out.println("\n[TC12] Kiem tra gia san pham hien thi");
        
        List<WebElement> prices = driver.findElements(By.className("inventory_item_price"));
        
        assertTrue(prices.size() > 0);
        for (WebElement price : prices) {
            assertTrue(price.isDisplayed());
            assertTrue(price.getText().startsWith("$"));
        }
        
        System.out.println("[TC12] PASS");
    }

    @Test(priority = 13)
    public void TC13() throws InterruptedException {
        System.out.println("\n[TC13] Kiem tra hamburger menu");
        
        WebElement menuButton = driver.findElement(By.id("react-burger-menu-btn"));
        assertTrue(menuButton.isDisplayed());
        
        menuButton.click();
        Thread.sleep(1500);
        
        WebElement menuWrap = driver.findElement(By.className("bm-menu-wrap"));
        assertTrue(menuWrap.isDisplayed());
        
        System.out.println("[TC13] PASS");
    }

    @Test(priority = 14)
    public void TC14() throws InterruptedException {
        System.out.println("\n[TC14] Kiem tra footer social links");
        
        List<WebElement> socialLinks = driver.findElements(By.cssSelector(".social a"));
        
        assertTrue(socialLinks.size() >= 3);
        for (WebElement link : socialLinks) {
            assertTrue(link.isDisplayed());
        }
        
        System.out.println("[TC14] PASS");
    }

    @Test(priority = 15)
    public void TC15() throws InterruptedException {
        System.out.println("\n[TC15] Kiem tra hinh anh san pham");
        
        List<WebElement> productImages = driver.findElements(By.className("inventory_item_img"));
        
        assertTrue(productImages.size() > 0);
        for (WebElement img : productImages) {
            assertTrue(img.isDisplayed());
        }
        
        System.out.println("[TC15] PASS");
    }

    @Test(priority = 16)
    public void TC16() throws InterruptedException {
        System.out.println("\n[TC16] Kiem tra shopping cart icon");
        
        WebElement cartIcon = driver.findElement(By.className("shopping_cart_link"));
        assertTrue(cartIcon.isDisplayed());
        
        cartIcon.click();
        Thread.sleep(1500);
        
        assertTrue(driver.getCurrentUrl().contains("cart.html"));
        
        System.out.println("[TC16] PASS");
    }
    
    @Test(priority = 17)
    public void TC17() throws InterruptedException {
        System.out.println("\n[TC17] Kiem tra so luong san pham sai - FAIL");
        
        List<WebElement> products = driver.findElements(By.className("inventory_item"));
        
        assertEquals(products.size(), 10);
        
        System.out.println("[TC17] FAIL");
    }
    
    @Test(priority = 18)
    public void TC18() throws InterruptedException {
        System.out.println("\n[TC18] Kiem tra gia san pham dau tien - FAIL");
        
        List<WebElement> prices = driver.findElements(By.className("inventory_item_price"));
        String firstPrice = prices.get(0).getText();
        
        assertEquals(firstPrice, "$99.99");
        
        System.out.println("[TC18] FAIL");
    }
    
    @Test(priority = 19)
    public void TC19() throws InterruptedException {
        System.out.println("\n[TC19] Kiem tra badge gio hang sai - FAIL");
        
        driver.findElement(By.id("add-to-cart-sauce-labs-backpack")).click();
        Thread.sleep(500);
        driver.findElement(By.id("add-to-cart-sauce-labs-bike-light")).click();
        Thread.sleep(1000);
        
        WebElement cartBadge = driver.findElement(By.className("shopping_cart_badge"));
        
        assertEquals(cartBadge.getText(), "5");
        
        System.out.println("[TC19] FAIL");
    }
    
    @Test(priority = 20)
    public void TC20() throws InterruptedException {
        System.out.println("\n[TC20] Kiem tra tieu de trang sai - FAIL");
        
        WebElement title = driver.findElement(By.className("title"));
        
        assertEquals(title.getText(), "All Products");
        
        System.out.println("[TC20] FAIL");
    }
    
    @Test
    public void testMain() {
        System.out.println("main");
        String[] args = null;
        Nhom08.main(args);
    }
}