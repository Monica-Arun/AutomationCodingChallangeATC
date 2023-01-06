package com.Base.Utilities;

import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.cucumber.java.Scenario;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.ie.InternetExplorerOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import io.github.bonigarcia.wdm.WebDriverManager;

public class Browser {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = LogManager.getLogger();

	/** The Constant osName. */
	public static final String osName = System.getProperty("os.name");

	/** The Constant targetBrowser. */
	public static String targetBrowser = System.getProperty("browser");

	private static String countryNameFromSystemProperty = System.getProperty("country");

	private static String envNameFromSystemProperty = System.getProperty("env");

	public static final String downloadPath = System.getProperty("user.dir") + File.separator + "src" + File.separator
			+ "test" + File.separator + "resources" + File.separator + "downloads" + File.separator;
	public static final String APIFiles_Path = System.getProperty("user.dir") + File.separator + "src" + File.separator
			+ "test" + File.separator + "resources" + File.separator + "API_Files" + File.separator;
	
	public static final String uploadPath = System.getProperty("user.dir") + File.separator + "src" + File.separator
			+ "test" + File.separator + "resources" + File.separator + "uploads" + File.separator;
	
	private static final String seleniumGrid = System.getProperty("grid");
	
	private static final String gridUrl = "http://ec2-18-236-179-11.us-west-2.compute.amazonaws.com:4444/wd/hub";


	public static Logger getLogger() {
		return LOGGER;
	}

	public static String get_environment()
	{
		if(envNameFromSystemProperty ==null || envNameFromSystemProperty.equalsIgnoreCase(""))
		{
			Browser.getLogger().info("Environment not specified...! Defaulting to QA");
			envNameFromSystemProperty = "QA";
		}
		return envNameFromSystemProperty;
	}

	public static String get_environment_fromCommandline()
	{
		if(envNameFromSystemProperty ==null || envNameFromSystemProperty.equalsIgnoreCase(""))
		{
			Browser.getLogger().info("Environment not specified...! returning 'NOT FOUND'");
			envNameFromSystemProperty = "NOT FOUND";
		}
		else {
			Browser.getLogger().info("Environment passed from the commandline : " + envNameFromSystemProperty);
		}
		return envNameFromSystemProperty;
	}

	public static void set_environment(String environment)
	{
		if(get_environment_fromCommandline().equalsIgnoreCase("NOT FOUND")) {
			envNameFromSystemProperty = environment;
		}
	}

	public static String get_Browser()
	{
		if(targetBrowser == null || targetBrowser.equalsIgnoreCase(""))
		{
			Browser.getLogger().info("Browser not specified...! Defaulting to 'Chrome'");
			targetBrowser = "Chrome";
		}
		return targetBrowser;
	}
	
	public WebDriver get_Chrome_WebDriver()
	{
		WebDriver driver;
			Browser.getLogger().info("Creating chrome driver..!");
			driver =  newChromeDriver();

		if(driver ==null)
		{
			Browser.getLogger().info("Driver is null..!");
			fail("Driver is null..!");
		}
		driver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);
		driver.manage().window().maximize();
		return driver;
	}

	public WebDriver get_firefox_Driver()
	{
		WebDriver driver;
		Browser.getLogger().info("Creating firefox driver..!");
		driver = get_firefox_Driver();
		if(driver ==null)
		{
			Browser.getLogger().info("Driver is null..!");
			fail("Driver is null..!");
		}
		driver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);
		driver.manage().window().maximize();
		return driver;
	}
	public WebDriver get_IE_Driver()
	{
		WebDriver driver;
		Browser.getLogger().info("Creating IE driver..!");
		driver = get_IE_Driver();
		if(driver ==null)
		{
			Browser.getLogger().info("Driver is null..!");
			fail("Driver is null..!");
		}
		driver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);
		driver.manage().window().maximize();
		return driver;
	}

	public static WebDriver InitialiseWebDriver(String browser)
	{
		WebDriver driver = null;
		if(browser.trim().equalsIgnoreCase("Chrome"))
		{
			Browser.getLogger().info("Creating chrome driver..!");
			driver =  newChromeDriver();
		}
		else if(browser.trim().equalsIgnoreCase("Edge"))
		{
			Browser.getLogger().info("Creating Edge driver..!");
			driver =  newEdgeDriver();
		}
		else if(browser.trim().equalsIgnoreCase("Firefox"))
		{
			Browser.getLogger().info("Creating Firefox driver..!");
			driver =  newFirefoxDriver();
		}
		else {
			fail("Browser not defined : " + browser);
		}
		if(driver ==null)
		{
			Browser.getLogger().info("Driver is null..!");
			fail("Driver is null..! Browser : " + browser);
		}
		driver.manage().window().maximize();
		driver.manage().timeouts().implicitlyWait(20,TimeUnit.SECONDS);
		driver.manage().timeouts().pageLoadTimeout(50,TimeUnit.SECONDS);
		driver.manage().timeouts().setScriptTimeout(50,TimeUnit.SECONDS);
		return driver;
		
	}

	private static WebDriver newFirefoxDriver()
	{
		Path path = Paths.get(downloadPath);
		FirefoxOptions options = new FirefoxOptions();
		Map<String, Object> prefs = new HashMap<String, Object>();
		prefs.put("profile.default_content_settings.popups", 0);

		if (Files.exists(path)) {
			prefs.put("download.default_directory", downloadPath);
			getLogger().info("Chrome default download path is set to {}", downloadPath);
		} else {
			getLogger().error("Default download path {} does not exists", downloadPath);
			return null;
		}
		options.addArguments("--no-sandbox");
		options.addArguments("--disable-dev-shm-usage");
		options.addArguments("--ignore-ssl-errors=yes"); //Added to handle insecure content
		options.addArguments("--ignore-certificate-errors");//Added to handle insecure content
		options.addArguments("--disable-popup-blocking");
		options.addArguments("--disable-backgrounding-occluded-windows");//To handle multiple wndows

		WebDriverManager.firefoxdriver().setup();

		if (getSeleniumgrid().equalsIgnoreCase("true")) {
			try {
//				return new RemoteWebDriver(new URL("http://localhost:4444/wd/hub"), options);
				return new RemoteWebDriver(new URL(gridUrl), options);
			} catch (MalformedURLException e) {
				Browser.getLogger().info("Error while creating remote driver : " + e);
				fail("Error while creating remote driver (MalformedURLException)");
			}

		}
		return new FirefoxDriver(options);
	}

	private static WebDriver newEdgeDriver()
	{
		Path path = Paths.get(downloadPath);
		EdgeOptions options =new EdgeOptions();
		Map<String, Object> prefs = new HashMap<String, Object>();
		prefs.put("profile.default_content_settings.popups", 0);

		if (Files.exists(path)) {
			prefs.put("download.default_directory", downloadPath);
			getLogger().info("default download path is set to {}", downloadPath);
		} else {
			getLogger().error("Default download path {} does not exists", downloadPath);
			return null;
		}
//		options.setPageLoadStrategy("--no-sandbox");
//		options.setPageLoadStrategy("--disable-dev-shm-usage");
//		options.setPageLoadStrategy("--ignore-ssl-errors=yes"); //Added to handle insecure content
//		options.setPageLoadStrategy("--ignore-certificate-errors");//Added to handle insecure content
//		options.setPageLoadStrategy("--disable-popup-blocking");
//		options.setPageLoadStrategy("--disable-backgrounding-occluded-windows");//To handle multiple wndows

		WebDriverManager.edgedriver().setup();

		if (getSeleniumgrid().equalsIgnoreCase("true")) {
			try {
//				return new RemoteWebDriver(new URL("http://localhost:4444/wd/hub"), options);
				return new RemoteWebDriver(new URL(gridUrl), options);
			} catch (MalformedURLException e) {
				Browser.getLogger().info("Error while creating remote driver : " + e);
				fail("Error while creating remote driver (MalformedURLException)");
			}

		}
		return new EdgeDriver(options);
	}

	private static WebDriver newChromeDriver()
	{
		Path path = Paths.get(downloadPath);
		ChromeOptions options = new ChromeOptions();
		Map<String, Object> prefs = new HashMap<String, Object>();
		prefs.put("profile.default_content_settings.popups", 0);

		if (Files.exists(path)) {
			prefs.put("download.default_directory", downloadPath);
			getLogger().info("Chrome default download path is set to {}", downloadPath);
		} else {
			getLogger().error("Default download path {} does not exists", downloadPath);
			return null;
		}
		options.setExperimentalOption("prefs", prefs);
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--ignore-ssl-errors=yes"); //Added to handle insecure content
        options.addArguments("--ignore-certificate-errors");//Added to handle insecure content
        options.addArguments("--disable-popup-blocking");
        options.addArguments("--disable-backgrounding-occluded-windows");//To handle multiple wndows
        
        WebDriverManager.chromedriver().setup();
        
		if (getSeleniumgrid().equalsIgnoreCase("true")) {
			try { 
//				return new RemoteWebDriver(new URL("http://localhost:4444/wd/hub"), options);
				return new RemoteWebDriver(new URL(gridUrl), options);
			} catch (MalformedURLException e) {
				Browser.getLogger().info("Error while creating remote driver : " + e);
				fail("Error while creating remote driver (MalformedURLException)");
			}

		}
		return new ChromeDriver(options);
	}
	
	public static String getSeleniumgrid() {
		if(seleniumGrid == null)
		{
			Browser.getLogger().info("Grid environment variable not provided.. defaulting to FALSE");
			return "false";
		}
		return seleniumGrid;
	}

	public static String get_ScenarioName(Scenario scn)
	{
		Browser.getLogger().info("Scenario name for data sheet : " + (scn.getName().split(":"))[0]);
		return (scn.getName().split(":"))[0].trim();
	}
	
}
