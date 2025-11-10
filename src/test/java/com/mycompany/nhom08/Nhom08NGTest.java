/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/EmptyTestNGTest.java to edit this template
 */
package com.mycompany.nhom08;

import static org.testng.Assert.*;
import org.testng.annotations.*;
import org.testng.ITestResult;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author miyam
 * 10 TEST CASES: 8 PASS + 2 FAIL = 20% FAIL RATE
 
 */
public class Nhom08NGTest {
    
    private WebDriver driver;
    private WebDriverWait wait;
    private static final String BASE_URL = "https://www.saucedemo.com/";
    private long startTime;
    
    // Thong tin dang nhap
    private static final String VALID_USERNAME = "standard_user";
    private static final String VALID_PASSWORD = "secret_sauce";
    private static final String LOCKED_USERNAME = "locked_out_user";
    
    public Nhom08NGTest() {
    }
    
    @BeforeSuite
    public void setupSuite() throws InterruptedException {
        Nhom08.CSVReporter.clear();
        System.out.println("=".repeat(60));
        System.out.println("BAT DAU CHAY 10 TEST CASES - SAUCEDEMO.COM");
        System.out.println("(8 PASS + 2 FAIL = 20% FAIL RATE)");
        System.out.println("=".repeat(60));
        
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.addArguments("--disable-save-password-bubble");
        options.addArguments("--disable-features=PasswordManager,PasswordCheck,PasswordLeakDetection");
        options.addArguments("--disable-password-manager-reauthentication");
        options.addArguments("--no-service-autorun");
        options.addArguments("--password-store=basic");
        options.addArguments("--incognito");
      
        Map<String, Object> prefs = new HashMap<>();
        prefs.put("credentials_enable_service", false);
        prefs.put("profile.password_manager_enabled", false);
        prefs.put("profile.default_content_setting_values.notifications", 2);
        prefs.put("autofill.profile_enabled", false);
        prefs.put("password_manager_enabled", false);
        prefs.put("profile.password_manager_leak_detection", false);
        prefs.put("safebrowsing.enabled", false);
        options.setExperimentalOption("prefs", prefs);
        options.setExperimentalOption("excludeSwitches", new String[]{"enable-automation", "enable-logging"});
        driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        
        // LOGIN 1 LAN DUY NHAT CHO TOAN BO TEST SUITE
        System.out.println("\n[SETUP] Dang nhap 1 lan cho toan bo test suite...");
        login(VALID_USERNAME, VALID_PASSWORD);
        System.out.println("[SETUP] Da dang nhap thanh cong ");
    }
    
    @BeforeMethod
    public void setUpMethod() throws Exception {
        startTime = System.currentTimeMillis();
        Thread.sleep(1500);
    }
    
    @AfterMethod
    public void tearDownMethod(ITestResult result) throws Exception {
        long duration = System.currentTimeMillis() - startTime;
        String testName = result.getMethod().getMethodName();
        String status = result.isSuccess() ? "PASS" : "FAIL";
        
        Nhom08.CSVReporter.addResult(testName, status, duration);
        System.out.println(">> " + testName + ": " + status + " (" + duration + "ms)");
        
        // Chi reset ve trang inventory, KHONG LOGOUT
        try {
            if (!driver.getCurrentUrl().contains("inventory.html")) {
                driver.get(BASE_URL + "inventory.html");
                Thread.sleep(1000);
            }
        } catch (Exception e) {
            System.out.println("  Khong the reset ve inventory");
        }
    }
    
    @AfterSuite
    public void finish() throws InterruptedException {
        System.out.println("=".repeat(60));
        Nhom08.CSVReporter.writeToCSV();
        System.out.println("=".repeat(60));
        
        // Logout truoc khi dong browser
        try {
            System.out.println("\n[CLEANUP] Dang logout...");
            safeLogout();
            Thread.sleep(1000);
        } catch (Exception e) {
            System.out.println("[CLEANUP] Khong the logout");
        }
        
        if (driver != null) {
            driver.quit();
            System.out.println("[CLEANUP] Da dong browser");
        }
    }
    
    // ========== HELPER METHODS ==========
    
    private void login(String username, String password) throws InterruptedException {
        driver.get(BASE_URL);
        Thread.sleep(2000);
        
        driver.findElement(By.id("user-name")).clear();
        driver.findElement(By.id("user-name")).sendKeys(username);
        Thread.sleep(500);
        
        driver.findElement(By.id("password")).clear();
        driver.findElement(By.id("password")).sendKeys(password);
        Thread.sleep(500);
        
        driver.findElement(By.id("login-button")).click();
        Thread.sleep(2000);
        
        // Tu dong dong popup password neu co
        try {
            WebElement okButton = driver.findElement(By.xpath("//button[contains(text(), 'OK') or contains(text(), 'Ok')]"));
            if (okButton.isDisplayed()) {
                okButton.click();
                Thread.sleep(500);
                System.out.println("  Da dong popup password");
            }
        } catch (Exception e) {
            // Khong co popup - OK
        }
    }
    
    private void safeLogout() throws InterruptedException {
        try {
            // Neu dang o trang khac, ve trang inventory truoc
            if (!driver.getCurrentUrl().contains("inventory.html")) {
                driver.get(BASE_URL + "inventory.html");
                Thread.sleep(1000);
            }
            
            WebElement menuButton = driver.findElement(By.id("react-burger-menu-btn"));
            menuButton.click();
            Thread.sleep(1000);
            
            WebElement logoutLink = driver.findElement(By.id("logout_sidebar_link"));
            logoutLink.click();
            Thread.sleep(1000);
        } catch (Exception e) {
            System.out.println("  Logout gap van de, reset ve trang chu");
            driver.get(BASE_URL);
            Thread.sleep(1000);
        }
    }
    
    // ========== 8 TEST CASES PASS ==========
    
    @Test(priority = 1)
    public void test01_Login_Success() throws InterruptedException {
        System.out.println("\n[TEST 1] Dang test dang nhap thanh cong...");
        
        // Logout truoc de test login
        safeLogout();
        Thread.sleep(1000);
        
        login(VALID_USERNAME, VALID_PASSWORD);
        
        assertTrue(driver.getCurrentUrl().contains("inventory.html"));
        WebElement title = wait.until(ExpectedConditions.presenceOfElementLocated(By.className("title")));
        assertEquals(title.getText(), "Products");
        
        System.out.println("[OK] Test 1 hoan thanh");
    }

    @Test(priority = 2)
    public void test02_Login_LockedUser() throws InterruptedException {
        System.out.println("\n[TEST 2] Dang test locked user...");
        
        // Logout truoc de test login
        safeLogout();
        Thread.sleep(1000);
        
        driver.get(BASE_URL);
        Thread.sleep(2000);
        
        driver.findElement(By.id("user-name")).sendKeys(LOCKED_USERNAME);
        Thread.sleep(500);
        
        driver.findElement(By.id("password")).sendKeys(VALID_PASSWORD);
        Thread.sleep(500);
        
        driver.findElement(By.id("login-button")).click();
        Thread.sleep(2000);
        
        WebElement errorMsg = driver.findElement(By.cssSelector("[data-test='error']"));
        assertTrue(errorMsg.getText().contains("locked out"));
        
        // Login lai sau khi test
        login(VALID_USERNAME, VALID_PASSWORD);
        
        System.out.println("[OK] Test 2 hoan thanh");
    }

    @Test(priority = 3)
    public void test03_Login_InvalidPassword() throws InterruptedException {
        System.out.println("\n[TEST 3] Dang test mat khau sai...");
        
        // Logout truoc de test login
        safeLogout();
        Thread.sleep(1000);
        
        driver.get(BASE_URL);
        Thread.sleep(2000);
        
        driver.findElement(By.id("user-name")).sendKeys(VALID_USERNAME);
        Thread.sleep(500);
        
        driver.findElement(By.id("password")).sendKeys("wrong_password");
        Thread.sleep(500);
        
        driver.findElement(By.id("login-button")).click();
        Thread.sleep(2000);
        
        WebElement errorMsg = driver.findElement(By.cssSelector("[data-test='error']"));
        assertTrue(errorMsg.getText().contains("do not match"));
        
        // Login lai sau khi test
        login(VALID_USERNAME, VALID_PASSWORD);
        
        System.out.println("[OK] Test 3 hoan thanh");
    }

    @Test(priority = 4)
    public void test04_AddProductToCart() throws InterruptedException {
        System.out.println("\n[TEST 4] Dang test them san pham vao gio hang...");
        // DA LOGIN ROI, KHONG CAN LOGIN NUA
        
        WebElement addButton = driver.findElement(By.id("add-to-cart-sauce-labs-backpack"));
        addButton.click();
        Thread.sleep(1000);
        
        WebElement cartBadge = driver.findElement(By.className("shopping_cart_badge"));
        assertEquals(cartBadge.getText(), "1");
        
        WebElement removeButton = driver.findElement(By.id("remove-sauce-labs-backpack"));
        assertTrue(removeButton.isDisplayed());
        
        // Xoa san pham khoi gio hang de cleanup
        removeButton.click();
        Thread.sleep(500);
        
        System.out.println("[OK] Test 4 hoan thanh");
    }

    @Test(priority = 5)
    public void test05_RemoveProductFromCart() throws InterruptedException {
        System.out.println("\n[TEST 5] Dang test xoa san pham khoi gio hang...");
        // DA LOGIN ROI, KHONG CAN LOGIN NUA
        
        driver.findElement(By.id("add-to-cart-sauce-labs-backpack")).click();
        Thread.sleep(1000);
        
        driver.findElement(By.id("remove-sauce-labs-backpack")).click();
        Thread.sleep(1000);
        
        List<WebElement> badges = driver.findElements(By.className("shopping_cart_badge"));
        assertEquals(badges.size(), 0);
        
        System.out.println("[OK] Test 5 hoan thanh");
    }

   @Test(priority = 6)
    public void test06_SortProductsByName() throws InterruptedException {
        System.out.println("\n[TEST 6] Dang test sap xep san pham theo ten...");
        // DA LOGIN ROI, KHONG CAN LOGIN NUA
        
        Select sortDropdown = new Select(driver.findElement(By.className("product_sort_container")));
        sortDropdown.selectByValue("az");
        Thread.sleep(1000);
        
        List<WebElement> productNames = driver.findElements(By.className("inventory_item_name"));
        String firstName = productNames.get(0).getText();
        assertTrue(firstName.startsWith("Sauce Labs"));
        
        sortDropdown.selectByValue("za");
        Thread.sleep(1000);
        
        productNames = driver.findElements(By.className("inventory_item_name"));
        String lastFirst = productNames.get(0).getText();
        assertTrue(lastFirst.contains("T-Shirt") || lastFirst.contains("Test"));
        
        // Reset ve sap xep mac dinh
        sortDropdown.selectByValue("az");
        Thread.sleep(500);
        
        System.out.println("[OK] Test 6 hoan thanh");
    }

    @Test(priority = 7)
    public void test07_SortProductsByPrice() throws InterruptedException {
        System.out.println("\n[TEST 7] Dang test sap xep san pham theo gia...");
        // DA LOGIN ROI, KHONG CAN LOGIN NUA
        
        Select sortDropdown = new Select(driver.findElement(By.className("product_sort_container")));
        sortDropdown.selectByValue("lohi");
        Thread.sleep(1000);
        
        List<WebElement> prices = driver.findElements(By.className("inventory_item_price"));
        String firstPrice = prices.get(0).getText().replace("$", "");
        double price1 = Double.parseDouble(firstPrice);
        
        String secondPrice = prices.get(1).getText().replace("$", "");
        double price2 = Double.parseDouble(secondPrice);
        
        assertTrue(price1 <= price2);
        
        // Reset ve sap xep mac dinh
        sortDropdown.selectByValue("az");
        Thread.sleep(500);
        
        System.out.println("[OK] Test 7 hoan thanh");
    }

    @Test(priority = 8)
    public void test08_VerifyProductCount() throws InterruptedException {
        System.out.println("\n[TEST 8] Dang test so luong san pham...");
        // DA LOGIN ROI, KHONG CAN LOGIN NUA
        
        List<WebElement> products = driver.findElements(By.className("inventory_item"));
        assertEquals(products.size(), 6);
        
        System.out.println("[OK] Test 8 hoan thanh");
    }

    @Test(priority = 9)
    public void test09_ViewProductDetails() throws InterruptedException {
        System.out.println("\n[TEST 9] Dang test xem chi tiet san pham...");
        // DA LOGIN ROI, KHONG CAN LOGIN NUA
        
        // Click vao san pham dau tien
        List<WebElement> productLinks = driver.findElements(By.className("inventory_item_name"));
        productLinks.get(0).click();
        Thread.sleep(2000);
        
        assertTrue(driver.getCurrentUrl().contains("inventory-item.html"));
        
        WebElement productName = driver.findElement(By.cssSelector("[data-test='inventory-item-name']"));
        assertNotNull(productName.getText());
        
        WebElement backButton = driver.findElement(By.id("back-to-products"));
        backButton.click();
        Thread.sleep(1000);
        
        System.out.println("[OK] Test 9 hoan thanh");
    }

    @Test(priority = 10)
    public void test10_NavigateToCart() throws InterruptedException {
        System.out.println("\n[TEST 10] Dang test di chuyen toi gio hang...");
        // DA LOGIN ROI, KHONG CAN LOGIN NUA
        
        driver.findElement(By.className("shopping_cart_link")).click();
        Thread.sleep(2000);
        
        assertTrue(driver.getCurrentUrl().contains("cart.html"));
        WebElement title = driver.findElement(By.className("title"));
        assertEquals(title.getText(), "Your Cart");
        
        System.out.println("[OK] Test 10 hoan thanh");
    }    
    @Test
    public void testMain() {
        System.out.println("main");
        String[] args = null;
        Nhom08.main(args);
    }
}