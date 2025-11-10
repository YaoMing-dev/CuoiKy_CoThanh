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
    
    // --- BIEN DA DUOC BO SUNG ---
    private static final String VALID_USERNAME = "standard_user";
    private static final String LOCKED_USERNAME = "locked_out_user"; // <-- DA THEM
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
      
        options.addArguments("--incognito");
      
        
        
        driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        
        System.out.println("\n[SETUP] Dang nhap 1 lan duy nhat...");
        driver.get(BASE_URL);
        Thread.sleep(1000); // Giam thoi gian cho
        
        // Su dung ham helper de dang nhap
        login(VALID_USERNAME, VALID_PASSWORD);
        
        wait.until(ExpectedConditions.urlContains("inventory.html"));
        System.out.println("[SETUP] Da dang nhap thanh cong - Bat dau chay test");
    }
    
    @BeforeMethod
    public void setUpMethod() throws Exception {
        startTime = System.currentTimeMillis();
        
        // Logic nay van dung de dam bao cac test 4-20 luon bat dau o trang inventory
        if (!driver.getCurrentUrl().contains("inventory.html")) {
            System.out.println("[WARNING] Phat hien trang thai da bi log-out. Dang thu vao lai trang inventory...");
            driver.get(BASE_URL + "inventory.html");
            Thread.sleep(1000);
            
            // Neu vao lai ma van bi day ve trang login thi tien hanh login lai
            if (!driver.getCurrentUrl().contains("inventory.html")) {
                 System.out.println("[RECOVERY] Bi day ve trang login. Dang dang nhap lai...");
                 login(VALID_USERNAME, VALID_PASSWORD);
                 wait.until(ExpectedConditions.urlContains("inventory.html"));
            }
        } else {
             // Neu da o trang inventory, chi can refresh de reset state (vi du: xoa item khoi cart)
            driver.navigate().refresh();
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("react-burger-menu-btn")));
            Thread.sleep(500);
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
    
     // --- TEST 1 (DA SUA) ---
     @Test(priority = 1)
    public void test01_Login_Success() throws InterruptedException {
        System.out.println("\n[TEST 1] Dang test dang nhap thanh cong...");
        
        // 1. Dang xuat khoi phien lam viec cua @BeforeSuite
        safeLogout();
        
        // 2. Thuc hien test dang nhap
        login(VALID_USERNAME, VALID_PASSWORD);
        
        // 3. Kiem tra ket qua
        assertTrue(driver.getCurrentUrl().contains("inventory.html"));
        WebElement title = wait.until(ExpectedConditions.presenceOfElementLocated(By.className("title")));
        assertEquals(title.getText(), "Products");
        
        // 4. KHONG dang xuat, de giu trang thai cho test tiep theo
        System.out.println("[OK] Test 1 hoan thanh (Da dang nhap lai)");
    }

    // --- TEST 2 (DA SUA) ---
    @Test(priority = 2)
    public void test02_Login_LockedUser() throws InterruptedException {
        System.out.println("\n[TEST 2] Dang test locked user...");
        
        // 1. Dang xuat
        safeLogout();
        
        // 2. Thuc hien test voi locked user
        login(LOCKED_USERNAME, VALID_PASSWORD);
        
        // 3. Kiem tra loi
        WebElement errorMsg = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("[data-test='error']")));
        assertTrue(errorMsg.getText().contains("locked out"));
        
        // 4. PHUC HOI TRANG THAI: Dang nhap lai bang user thuong
        System.out.println("[TEST 2] Dang log in lai (standard_user) de chuan bi cho test tiep theo...");
        login(VALID_USERNAME, VALID_PASSWORD);
        wait.until(ExpectedConditions.urlContains("inventory.html")); // Dam bao dang nhap thanh cong
        
        System.out.println("[OK] Test 2 hoan thanh (Da khoi phuc trang thai)");
    }

    // --- TEST 3 (DA SUA) ---
    @Test(priority = 3)
    public void test03_Login_InvalidPassword() throws InterruptedException {
        System.out.println("\n[TEST 3] Dang test mat khau sai...");
        
        // 1. Dang xuat
        safeLogout();
        
        // 2. Thuc hien test voi mat khau sai
        login(VALID_USERNAME, "wrong_password");
        
        // 3. Kiem tra loi
        WebElement errorMsg = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("[data-test='error']")));
        assertTrue(errorMsg.getText().contains("do not match"));
        
        // 4. PHUC HOI TRANG THAI: Dang nhap lai bang user thuong
        System.out.println("[TEST 3] Dang log in lai (standard_user) de chuan bi cho test tiep theo...");
        login(VALID_USERNAME, VALID_PASSWORD);
        wait.until(ExpectedConditions.urlContains("inventory.html")); // Dam bao dang nhap thanh cong
        
        System.out.println("[OK] Test 3 hoan thanh (Da khoi phuc trang thai)");
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

    // ... (Cac test case tu 5 den 20 giu nguyen) ...
    
    @Test(priority = 5)
    public void TC05() throws InterruptedException {
        System.out.println("\n[TC05] Kiem tra chuyen den trang gio hang");
        
        driver.findElement(By.className("shopping_cart_link")).click();
        Thread.sleep(1000); // Giam thoi gian cho
        
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
        Thread.sleep(1000); // Giam thoi gian cho
        
        assertTrue(driver.getCurrentUrl().contains("inventory.html"));
        
        System.out.println("[TC06] PASS");
    }

    @Test(priority = 7)
    public void TC07() throws InterruptedException {
        System.out.println("\n[TC07] Kiem tra xem chi tiet san pham");
        
        List<WebElement> productNames = driver.findElements(By.className("inventory_item_name"));
        productNames.get(0).click();
        Thread.sleep(1000); // Giam thoi gian cho
        
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
        Thread.sleep(1000); // Giam thoi gian cho
        
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
        Thread.sleep(1000); // Giam thoi gian cho
        
        WebElement menuWrap = driver.findElement(By.className("bm-menu-wrap"));
        assertTrue(menuWrap.isDisplayed());
        
        // Click nut "X" de dong menu lai, tranh anh huong test sau
        WebElement closeButton = driver.findElement(By.id("react-burger-cross-btn"));
        closeButton.click();
        Thread.sleep(500);

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
        Thread.sleep(1000); // Giam thoi gian cho
        
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
        Thread.sleep(500); // Giam thoi gian cho
        
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
    
    // --- CAC HAM HELPER DA DUOC BO SUNG ---
    
    /**
     * Ham helper de thuc hien dang nhap
     */
    private void login(String username, String password) throws InterruptedException {
        // Dam bao dang o trang login
        if (!driver.getCurrentUrl().equals(BASE_URL)) {
            driver.get(BASE_URL);
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("login-button")));
            Thread.sleep(200);
        }
        
        WebElement userField = driver.findElement(By.id("user-name"));
        userField.clear();
        userField.sendKeys(username);
        Thread.sleep(200);
        
        WebElement passField = driver.findElement(By.id("password"));
        passField.clear();
        passField.sendKeys(password);
        Thread.sleep(200);
        
        driver.findElement(By.id("login-button")).click();
        Thread.sleep(500); // Cho de trang bat dau load
    }

    /**
     * Ham helper de dang xuat an toan
     */
    private void safeLogout() throws InterruptedException {
        // Chi dang xuat neu dang o trong trang inventory
        if (driver.getCurrentUrl().contains("inventory.html")) {
            try {
                WebElement menuButton = driver.findElement(By.id("react-burger-menu-btn"));
                menuButton.click();
                Thread.sleep(500); // Cho menu mo

                WebElement logoutLink = wait.until(ExpectedConditions.elementToBeClickable(By.id("logout_sidebar_link")));
                logoutLink.click();

                wait.until(ExpectedConditions.urlToBe(BASE_URL)); // Cho de quay ve trang login
                Thread.sleep(200);
            } catch (Exception e) {
                System.err.println("Loi khi logout: " + e.getMessage());
                // Giai phap an toan: Neu loi thi cu dieu huong ve trang chu
                driver.get(BASE_URL);
                Thread.sleep(500);
            }
        } else {
             // Neu khong o trang inventory (vi du: trang cart), quay ve trang chu
             driver.get(BASE_URL);
             Thread.sleep(500);
        }
    }
}