package com.Base.Utilities;



import java.awt.image.BufferedImage;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DateTimeException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.imageio.IIOException;
import javax.imageio.ImageIO;
import com.Runtime.utilities.RuntimeEnvironment;
import org.apache.commons.io.FileUtils;
import org.json.JSONObject;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.ElementNotInteractableException;
import org.openqa.selenium.InvalidElementStateException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchCookieException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.NoSuchFrameException;
import org.openqa.selenium.NoSuchWindowException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.assertthat.selenium_shutterbug.core.Capture;
import com.assertthat.selenium_shutterbug.core.Shutterbug;

import io.cucumber.java.Scenario;
import io.qameta.allure.Attachment;
import io.qameta.allure.Step;


import static org.junit.jupiter.api.Assertions.*;

public class GeneralReusableFunctions{
	RuntimeEnvironment runtime;
public GeneralReusableFunctions(RuntimeEnvironment runtime)
{
	this.runtime = runtime;
}

	private byte[] TakeScreenSnap(WebDriver driver) {
		BufferedImage screenshot = Shutterbug.shootPage(driver, Capture.VIEWPORT).getImage();
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

		try {
			ImageIO.write(screenshot, "png", outputStream);
		} catch (IOException e) {
			Browser.getLogger().info("IO exception while taking screenshot");
		}
		catch (WebDriverException e) {
			Browser.getLogger().info("WebDriver exception while taking screenshot");
		}
	
		return outputStream.toByteArray();

	}
	
	private byte[] TakeScreenSnap_FullScroll(WebDriver driver) throws javax.imageio.IIOException{
		BufferedImage screenshot;
		screenshot = Shutterbug.shootPage(driver, Capture.FULL_SCROLL).getImage();

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

		try {
			ImageIO.write(screenshot, "png", outputStream);
		} catch (IOException e) {
			Browser.getLogger().info("IO exception while taking screenshot");
		}
		catch (WebDriverException e) {
			Browser.getLogger().info("WebDriver exception while taking screenshot");
		}
	
		return outputStream.toByteArray();

	}


	public  void TakeScreenSnap(String imgDescription, Scenario scn, WebDriver driver) {
		scn.attach(TakeScreenSnap(driver), "image/png", imgDescription);

	}	
	
	public void TakeScreenSnap_FullScroll(String imgDescription, Scenario scn, WebDriver driver) {
		try {
			scn.attach(TakeScreenSnap_FullScroll(driver), "image/png", imgDescription);
		} catch (IIOException e) {
			wait_ForSeconds(5);
			TakeScreenSnap_FullScroll(imgDescription, scn, driver);
			return;
		}
		wait_ForSeconds(2);
		Scrollto_TopOfPage(driver);
		wait_ForSeconds(4);

	}
	public void mouseOver(WebElement element, WebDriver driver) {
		Actions action = new Actions(driver);	
		action.moveToElement(element).perform();	
	}	


	public void wait_ForSeconds(int secondsWait) {
		secondsWait = secondsWait * 1000;
		try {
			Thread.sleep(secondsWait);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void waitForPageToLoad(WebDriver driver) {
		WebDriverWait wait = new WebDriverWait(driver, 100);
		try {
			wait.until((ExpectedCondition<Boolean>) wd -> ((JavascriptExecutor) wd)
					.executeScript("return document.readyState").equals("complete"));
		} catch (TimeoutException e) {
			fail("Page load failure.");
		}

	}

	public void waitUntilPageLoads(WebDriver driver) {
		JavascriptExecutor js = (JavascriptExecutor) driver;
		String siteready = (String) js.executeScript("return document.readyState;");
		int itterator = 0;
		while (!siteready.equalsIgnoreCase("COMPLETE") && itterator < 50) {
			wait_ForSeconds(5);
			Browser.getLogger().info("WEB PAGE NOT READY - Wait.... ");
			siteready = (String) js.executeScript("return document.readyState;");
			itterator++;
			wait_ForSeconds(5);
		}
		wait_ForSeconds(5);
	}

	public void Click_FirstInteractableElement(String eElementxpath, WebDriver driver, String ElementInfo)
	{
		List<WebElement> elements = driver.findElements(By.xpath(eElementxpath));
		Browser.getLogger().info("No Of elements matching xpath :" + elements.size());
		for(WebElement element : elements)
		{
			try {
				if(Verify_Object_IsDisplayed(element,driver))
				{
					element.click();
				Browser.getLogger().info("Clicked on  : " + element);
				return;
				}
				
			} catch (ElementNotInteractableException e) {
				Browser.getLogger().info("Hiden element : " + element + " : Checking next..");
			}
			catch (StaleElementReferenceException e) {
				Browser.getLogger().info("Hiden element : " + element + " : Checking next..");
			}
		}
		
		fail( "Unable to click element : " + ElementInfo);
	}

	/*
	 * @Purpose: To Verify Object Displayed by WebElement
	 */
	public boolean Verify_Object_IsDisplayed(WebElement wElement, WebDriver driver) {
		int noOfAttempts = 0;
		try {
			if (wElement.isDisplayed()) {
				wait_ForSeconds(3);
				return true;
			} else {

				while (noOfAttempts < 3) {
					Mobile_ScrollToViewElement(driver, wElement,wElement.toString());
					Browser.getLogger().info("Element found but hidden...!   : " +wElement);
					wait_ForSeconds(3);
					try {
						if (wElement.isDisplayed()) {
							wait_ForSeconds(3);
							return true;
						}
					} catch (NoSuchElementException e) {
						Browser.getLogger().info("Element not found(No Such Element)");
						return false;
					}
					noOfAttempts++;
				}

				return false;
			}
		} catch (NoSuchElementException e) {
			Browser.getLogger().info("Element not found(No Such Element)");
			return false;
		}
		catch (WebDriverException e) {
			Browser.getLogger().info("Element not found(WebDriver Exception)");
			return false;
		}

	}

	public boolean Verify_Object_IsDisplayed_byDianamicXpath(String xpathString, WebDriver driver ,String ElementInfo) {
		int noOfAttempts = 0;
		try {
			if (driver.findElement(By.xpath(xpathString)).isDisplayed()) {
				wait_ForSeconds(3);
				return true;
			} else {

				while (noOfAttempts < 3) {
					//Mobile_ScrollToViewElement(driver, wElement,wElement.toString());
					ScrollToViewElement(driver, xpathString, ElementInfo);
					Browser.getLogger().info("Element found but hidden...!   : " +xpathString);
					wait_ForSeconds(3);
					try {
						if (driver.findElement(By.xpath(xpathString)).isDisplayed()) {
							wait_ForSeconds(3);
							return true;

						}
					} catch (NoSuchElementException e) {
						Browser.getLogger().info("Element not found(No Such Element) : " + ElementInfo);
						return false;
					}
					noOfAttempts++;
				}
				Browser.getLogger().info("Element found in the DOM but not displayed...!  : " + ElementInfo);
				return false;
			}
		} catch (NoSuchElementException e) {
			Browser.getLogger().info("Element not found(No Such Element) - first attempt : " + ElementInfo);
			return false;
		}
		catch (WebDriverException e) {
			Browser.getLogger().info("Element not found(WebDriver Exception) - first attempt : "+ ElementInfo);
			return false;
		}

	}

	
	public boolean Verify_Object_IsDisplayed(WebElement wElement, WebDriver driver, String ElementInfo) {
		int noOfAttempts = 0;
		try {
			if (wElement.isDisplayed()) {
				Browser.getLogger().info("Web Element is present & displayed on screen : " + ElementInfo);
			} else {

				while (noOfAttempts < 3) {
					Browser.getLogger()
							.info("Web Element is present but not displayed on screen : " + ElementInfo);
					scroll_to_view_WebElement(driver, ElementInfo);

					try {
						if (wElement.isDisplayed()) {
							Browser.getLogger().info("Web Element is present & displayed on screen : " + ElementInfo);
							return false;
						}
					} catch (NoSuchElementException e) {
						// Do nothing.
					}
					noOfAttempts++;
					wait_ForSeconds(4);
				}
				Browser.getLogger().fatal("Web Element not displayed : " + ElementInfo);
				fail("Element Not found : " + ElementInfo);
			}
		} catch (NoSuchElementException e) {
			Browser.getLogger().fatal("Web Element not displayed : " + ElementInfo);
			fail("Element Not found : " + ElementInfo);
		} catch (NullPointerException e) {
			Browser.getLogger().fatal("Web Element not displayed(Nullpointer exception) : " + ElementInfo);
			fail("Element Not found : " + ElementInfo);
			// return Verify_Object_IsDisplayed(wElement,driver);
		} catch (WebDriverException e) {
			Browser.getLogger().fatal("Web Element not displayed(WebDriverException) : " + ElementInfo);
			fail("Element Not found : " + ElementInfo);
			// return Verify_Object_IsDisplayed(wElement,driver);
		}
		return false;
    }

	public void Verify_Object_IsNotDisplayed(WebElement wElement, WebDriver driver, String ElementInfo) {
			int noOfAttempts = 0;
			try {
				if (wElement.isDisplayed()) {
					Browser.getLogger().info("Web Element is present & displayed on screen : " + ElementInfo);
					fail("WebElement is present : " +ElementInfo);
				} else {

					while (noOfAttempts < 3) {
						Browser.getLogger()
								.info("Web Element is present but not displayed on screen : " + ElementInfo);
						scroll_to_view_WebElement(driver, wElement,ElementInfo);

						try {
							if (wElement.isDisplayed()) {

								fail("WebElement is present : " +ElementInfo);
							}
						} catch (NoSuchElementException e) {
							Browser.getLogger().info("Web Element is NOT present : " + ElementInfo);
							return;
						}
						noOfAttempts++;
					}

					Browser.getLogger().info("Web Element is NOT present : " + ElementInfo);
				}
			} catch (NoSuchElementException e) {
				Browser.getLogger().info("Web Element is NOT present : " + ElementInfo);
			} catch (WebDriverException e) {
				Browser.getLogger().info("Web Element not displayed(WebDriverException) : " + ElementInfo);
			}

		}
		
		
		
		public void Verify_Object_IsNotDisplayed(String wElementXpath, WebDriver driver, String ElementInfo) {
			WebElement wElement = null;
			try {
				wElement = driver.findElement(By.xpath(wElementXpath));
				if(wElement.isDisplayed())
				{
					fail("Element found (Expected : element not displayed) : " + ElementInfo);
				}
				else
				{
					return;
				}
			} catch (NoSuchElementException e) {
				Browser.getLogger().info("Web Element is NOT present : " + ElementInfo);
			} 
			catch (WebDriverException e) {
				Browser.getLogger().info("Web Element not displayed(WebDriverException) : " + ElementInfo);
			}

		}
		
		public boolean Verify_Object_IsNotDisplayed(String wElementXpath, WebDriver driver) {
			WebElement wElement = null;
			try {
				wElement = driver.findElement(By.xpath(wElementXpath));
				return !wElement.isDisplayed();
			
			} catch (NoSuchElementException e) {
				Browser.getLogger().info("Web Element is NOT present : " + wElementXpath);
				return true;
			} 
			catch (WebDriverException e) {
				Browser.getLogger().info("Web Element not displayed(WebDriverException) : " + wElementXpath);
				return true;
			}

		}
		
		
	
		public boolean Verify_Object_IsNotDisplayed(WebElement wElement, WebDriver driver) {
			int noOfAttempts = 0;
			try {
				if (wElement.isDisplayed()) {
					Browser.getLogger().info("WebElement present, returning false..!");
					return false;
				} else {

					while (noOfAttempts < 3) {
						scroll_to_view_WebElement(driver, wElement,"Test Element");

						try {
							if (wElement.isDisplayed()) {

								return false;
							}
						} catch (NoSuchElementException e) {
							return true;
						}
						noOfAttempts++;
					}

					return true;
				}
			} catch (NoSuchElementException e) {
				return true;
			}
			catch (WebDriverException e) {
				return true;
			}

		}
	/*
	 * @Purpose: To Verify Object enabled by WebElement
	 */
	// @Step("Verify {0} : is displayed")
	public boolean Verify_Object_IsEnabled(WebElement wElement, WebDriver driver, String ElementInfo) {
		try {
			if (wElement.isEnabled()) {
				System.out.println("Web Element is present & displayed on screen : " + ElementInfo);
				Browser.getLogger().info("Web Element is present & displayed on screen : " + ElementInfo);
				return true;
			} else {
				return false;
			}
		} catch (NoSuchElementException e) {
			Browser.getLogger().info("Web Element not displayed : " + ElementInfo);
			return false;
		} catch (NullPointerException e) {

			return false;
			// return Verify_Object_IsDisplayed(wElement,driver);
		}

	}


	/*
	 * @Purpose: To Scroll into WebElement View
	 */
	public void scroll_to_view_WebElement(WebDriver driver, WebElement wElement,
			String elementInfo) {
		try {
			JavascriptExecutor js = (JavascriptExecutor) driver;
			js.executeScript("arguments[0].scrollIntoView();", wElement);
			Browser.getLogger().info("Scolled to weblement : " + elementInfo);
		} catch (WebDriverException e) {
			Browser.getLogger().info("Element not found : " + elementInfo);
			fail("Element not found : " + elementInfo);
		}
	}

	/*
	 * @Purpose: To Scroll down by desired Pixel
	 */
	public void scrollWindowByPixel(WebDriver driver, int pixel) {
		JavascriptExecutor js = (JavascriptExecutor) driver;
		js.executeScript("window.scrollBy(0," + pixel + ");");
		Browser.getLogger().info("Scrolled by pixel : " + pixel);
	}
	
	/*
	 * @Purpose: To Verify Page Title
	 * 
	 */

	public boolean Verify_WebPage_Title(WebDriver driver, String textToVerify) {
		try {
			if (driver.getTitle().contains(textToVerify)) {
				System.out.println("\n" + "The Page is loaded with title: " + driver.getTitle());
				Browser.getLogger().info("The Page is loaded with title: " + driver.getTitle());
				return true;
			} else {
				System.out.println("\n" + "WebPage Title Mismatch: " + " Expected :" + textToVerify + " Actual :"
						+ driver.getTitle());
				Browser.getLogger().info(
						"WebPage Title Mismatch: " + " Expected :" + textToVerify + " Actual :" + driver.getTitle());

				return false;
			}
		} catch (NoSuchElementException e) {
			Browser.getLogger().info("Web Element not available/displayed : ");
			e.printStackTrace();
			fail( "Web Element not available/displayed : ");
			return false;
		} catch (ElementNotInteractableException e) {
			Browser.getLogger().info("Element hidden. ");
			return false;
		} catch (InvalidElementStateException e) {
			Browser.getLogger().info("Element hidden ");
			e.printStackTrace();
			fail( "Element  hidden");
			return false;
		} catch (org.openqa.selenium.StaleElementReferenceException e1) {
			fail( "Page unstable");
			return false;
		}

	}

	/*
	 * Description : To Reset Implicit wait time.
	 * 
	 */

	public void Reset_ImplicitWaitTime(WebDriver driver, int i) {
		driver.manage().timeouts().implicitlyWait(i, TimeUnit.SECONDS);

	}

	/*
	 * @Purpose: To Verify HyperLink by WebElement
	 */
	public boolean Verify_HyperLink(WebElement wElement, WebDriver driver, String textToVerify, String ElementInfo) {
		int noOfAttempts = 0;
		try {

			if (wElement.getAttribute("href").contains(textToVerify)) {
				Browser.getLogger().info("Hyper link is successfully verified" + wElement);
				return true;
			} else {
				Reset_ImplicitWaitTime(driver, 5);
				while (noOfAttempts < 3) {
					Browser.getLogger()
							.info("Web Element is present but not displayed on screen : " + wElement);
					scroll_to_view_WebElement(driver, wElement,ElementInfo);

					try {
						if (wElement.getAttribute("href").contains(textToVerify)) {
							Reset_ImplicitWaitTime(driver, noOfAttempts);
							return true;
						}
					} catch (NoSuchElementException e) {
						Browser.getLogger().info("Web Element not displayed : " + wElement);
						return false;
					}
					noOfAttempts++;
				}
				Reset_ImplicitWaitTime(driver, noOfAttempts);
				Browser.getLogger().info("Hyper link is different" + wElement);
				return false;
			}
		} catch (NoSuchElementException e) {
			Browser.getLogger().info("Web Element not displayed : " + wElement);
			return false;
		} catch (StaleElementReferenceException e) {

			Browser.getLogger()
					.info("Element is stale(May have been closed / hidden/ not loaded)" + " : " + wElement);
			return false;

		}

	}

	/*
	 * @Purpose: click on element by Actions Class and WebElement
	 */

	private void Actions_Click(WebElement wElement, WebDriver driver, String ElementInfo) throws InvocationTargetException
	{
			try {
				wait_ForSeconds(5);
				Actions action = new Actions(driver);
				action.moveToElement(wElement).click().build().perform();
			} catch (WebDriverException e) {
				Click_Element(driver, wElement, ElementInfo);
			}
	}
	public void Click_Element_byActions(WebElement wElement, WebDriver driver, String ElementInfo){

		try {
			Actions_Click(wElement, driver,ElementInfo);
			Browser.getLogger().info("Clicked the element(Actions) : " + ElementInfo);
		} catch (NoSuchElementException e) {
			Browser.getLogger().info("Web Element not displayed : " + ElementInfo);
			fail( "The element to click doesn't exist : " + ElementInfo);
		} catch (ElementClickInterceptedException e) {
			Browser.getLogger().info("Element hidden. Unable to click, trying to Click using JavaScript Executor "
					+ wElement.toString());
			Click_Element_JSript(driver, wElement,ElementInfo);

		} catch (ElementNotInteractableException e) {
			Browser.getLogger().info("Element hidden. Unable to click, trying to Click using JavaScript Executor "
					+ wElement.toString());
			Click_Element_JSript(driver, wElement,ElementInfo);

		} catch (InvalidElementStateException e) {
			Browser.getLogger().info("Element hidden. Unable to click, trying to Click using JavaScript Executor "
					+ wElement.toString());
			Click_Element_JSript(driver, wElement,ElementInfo);

		} catch (org.openqa.selenium.StaleElementReferenceException e1) {

			Browser.getLogger().info("Element is stale..! Reinitialising..!" + " : " + wElement.toString());
			WebElement tempElement = reinitialise_WebElement(wElement, driver);
			if (Verify_Object_IsDisplayed(tempElement, driver)) {
				Click_Element_JSript(driver, tempElement,ElementInfo);
			}

		} catch (InvocationTargetException e)
		{
			Browser.getLogger().fatal("Element to click unavailable (InvocationTargetException) : " + ElementInfo);
			fail( "Element to click unavailable (InvocationTargetException) : " + ElementInfo);
		}

	}
	public void Click_Element_byActions( WebDriver driver,WebElement wElement, String ElementInfo) {

			assertTrue(Verify_Object_IsDisplayed(wElement, driver), "Element not displayed : " + ElementInfo);
			Click_Element_byActions(wElement, driver,ElementInfo);

	}
	
	public void Click_Element_byActions( WebDriver driver,String wElementXpath, String ElementInfo) {

		WebElement TempElement = null;
		try {
			TempElement = driver.findElement(By.xpath(wElementXpath));
		} catch (NoSuchElementException e) {
			Browser.getLogger().info("Element not found : " + wElementXpath);
			fail( "Element not found : " + wElementXpath);
		}
		Click_Element_byActions(TempElement, driver,ElementInfo);

}

	/*
	 * @Purpose: To Verify text in WebPage by WebElement
	 */

	public boolean Verify_WebPage_TextDisplayed(WebDriver driver, WebElement wElement, String textToVerify
			,String ElementInfo) {
		try {
			if (wElement.getText().trim().equalsIgnoreCase(textToVerify.trim())) {
				Browser.getLogger().info("Text comparison successfull for : " + ElementInfo);
				return true;
			} else {
				Browser.getLogger().info("Text comparison failed for : " + ElementInfo + " Expected : "
						+ textToVerify + " Actual : " + wElement.getText());
				fail("Text comparison failed for : " + ElementInfo + " Expected : "
						+ textToVerify + " Actual : " + wElement.getText());
				return false;
			}
		} catch (NoSuchElementException e) {
			Browser.getLogger().info("Web Element not available/displayed : " + ElementInfo);
			fail("Web Element not available/displayed : " + ElementInfo);
			return false;
		} catch (ElementNotInteractableException e) {
			Browser.getLogger().info("Element hidden. " + ElementInfo);
			// Set_Text_JSript(driver, wElement, textToVerify);
			return false;
		} catch (InvalidElementStateException e) {
			Browser.getLogger().info("Element hidden " + ElementInfo);
			e.printStackTrace();
			fail( "Element  hidden");
			return false;
		} catch (org.openqa.selenium.StaleElementReferenceException e1) {
			WebElement tempElement = reinitialise_WebElement(wElement,driver);
			Verify_WebPage_TextDisplayed(driver,tempElement,textToVerify
					,ElementInfo);
		}
	return true;
	}

	/*
	 * @Purpose: click on element by Java Script Executor and WebElement
	 */

	public void Click_Element_JSript(WebDriver driver, WebElement myelement, String ElementInfo) {
		JavascriptExecutor js = (JavascriptExecutor) driver;
		try {
			js.executeScript("arguments[0].scrollIntoView()", myelement);
			js.executeScript("arguments[0].click();", myelement);
			Browser.getLogger().info("Clicked using jScript : " + ElementInfo);
		} catch (NoSuchElementException e) {
			Browser.getLogger().info("Element to click doesn't exist : " + ElementInfo);
			fail("Element to click doesn't exist : " + ElementInfo);
		} catch (ElementClickInterceptedException e) {
			Browser.getLogger().info("Element hidden. Unable to click " + ElementInfo);
			fail("Element to click - hidden");
		} catch (ElementNotInteractableException e) {
			Browser.getLogger().info("Element hidden. " + ElementInfo);
			fail("Element to click - hidden");
		} catch (InvalidElementStateException e) {
			Browser.getLogger().info("Element hidden. Unable to click " + ElementInfo);
			fail("Element to click - hidden");
		} catch (org.openqa.selenium.StaleElementReferenceException e) {

			Browser.getLogger().info("Element is stale..! Reinitialising..!" + " : " + ElementInfo);
			WebElement tempElement = reinitialise_WebElement(myelement, driver);
			if (Verify_Object_IsDisplayed(tempElement, driver)) {
				Click_Element_JSript(driver, tempElement,ElementInfo);
			}

		}

	}

	/*
	 * @Purpose: click on element by WebElement
	 */

	public void Click_Element(WebDriver driver, WebElement wElement, String ElementInfo) {

		try {
			wElement.click();
			Browser.getLogger().info("Clicked the element : " + ElementInfo);
		} catch (NoSuchElementException e) {
			Browser.getLogger().info("Web Element not displayed : " + ElementInfo);
			fail( "The element to click doesn't exist : " + ElementInfo);

		} catch (ElementClickInterceptedException e) {
			Browser.getLogger().info(
					"Element hidden. Scrolling...!" + ElementInfo);
			scroll_to_view_WebElement(driver, wElement, ElementInfo);
			if(Verify_Object_IsDisplayed(wElement, driver))
			{
				wait_ForSeconds(5);
				try {
					wElement.click();
				} catch (ElementClickInterceptedException e1) {
					
					Click_Element_byActions(wElement, driver, ElementInfo);
				}
			}
			
		} catch (ElementNotInteractableException e) {
			Browser.getLogger().info(
					"Element hidden. Unable to click, trying to Click using Actions Class " + ElementInfo);
			Click_Element_byActions(wElement, driver, ElementInfo);
//			assertTrue(false, "Element click failed : " + ElementInfo);

		} catch (InvalidElementStateException e) {
			Browser.getLogger().info(
					"Element click failed...!" + ElementInfo);

			fail("Element click failed : " + ElementInfo);

		}
		catch (WebDriverException e) {

			Browser.getLogger().info("Element is not found. click failed..!" + " : " + ElementInfo);
			fail("Element click failed (WebDriverException): " + ElementInfo);

		}


	}

	public boolean validate_CheckBox_Checked(WebDriver driver, WebElement wElement, String ElementInfo)
	{
		try {
			if(wElement.isSelected())
			{
				Browser.getLogger().info("Check box is selected.. : " + ElementInfo);
				return true;
			}
			else
			{
				return  false;
			}
		} catch (NoSuchElementException e) {
			Browser.getLogger().info("Web Element not displayed : " + ElementInfo);
			fail( "The element check does not exist : " + ElementInfo);

		} catch (ElementClickInterceptedException e) {
			Browser.getLogger().info(
					"Element hidden. Scrolling...!" + ElementInfo);
			scroll_to_view_WebElement(driver, wElement, ElementInfo);
			if(Verify_Object_IsDisplayed(wElement, driver))
			{
				wait_ForSeconds(5);
				try {
					wElement.click();
				} catch (ElementClickInterceptedException e1) {

					Click_Element_byActions(wElement, driver, ElementInfo);
				}
			}

		} catch (ElementNotInteractableException e) {
			Browser.getLogger().info(
					"Element hidden. Unable to click, trying to Click using Actions Class " + ElementInfo);
		fail(" element is not interactable : " + ElementInfo);

		} catch (InvalidElementStateException e) {
			Browser.getLogger().info(
					"Element click failed...!" + ElementInfo);

			fail("Element check failed : " + ElementInfo);

		}
		catch (WebDriverException e) {

			Browser.getLogger().info("Element is not found. click failed..!" + " : " + ElementInfo);
			fail("Element check failed (WebDriverException): " + ElementInfo);

		}
		return false;
	}
	// checkbox element should be of type INPUT
	int reccursiveCallIndex =1;
	public void Select_CheckBox(WebDriver driver, WebElement wElement,String OnOrOff, String ElementInfo) {

		try {
			if(OnOrOff.equalsIgnoreCase("ON"))
			{
				if( wElement.isSelected())
				{
					Browser.getLogger().info("Checkbox already ticked..!");
					return;
				}
				else
				{
					Browser.getLogger().info("Attemting to select checkbox...!");
					wElement.click();
					wait_ForSeconds(2);
					if( wElement.isSelected())
					{
						Browser.getLogger().info("Checkbox ticked successfully..!");
					}
					else
					{
						if(reccursiveCallIndex < 5) {
							reccursiveCallIndex++;
							Select_CheckBox(driver, wElement, OnOrOff, ElementInfo);
						}
						else
						{
							reccursiveCallIndex=1;
							fail("Could not select the checkbox  : " + ElementInfo);
						}
						return;
					}
				}
			}
			else if(OnOrOff.equalsIgnoreCase("OFF"))
			{
				if(!wElement.isSelected())
				{
					Browser.getLogger().info("Checkbox is already un-ticked..!");
					return;
				}
				else
				{
					Browser.getLogger().info("Attemting to un-select checkbox...!");
					Click_Element(driver,wElement,"");
					wait_ForSeconds(2);
					if( !wElement.isSelected())
					{
						Browser.getLogger().info("Checkbox un-ticked successfully..!");
					}
					else
					{
						if(reccursiveCallIndex < 5) {
							reccursiveCallIndex++;
							Select_CheckBox(driver, wElement, OnOrOff, ElementInfo);
						}
						else
						{
							reccursiveCallIndex=1;
							fail("Could not un-select the checkbox  : " + ElementInfo);
						}

						return;
					}

				}
			}
			else
			{
				fail("invalid parameter for the checkbox state : " + OnOrOff);
			}
			reccursiveCallIndex=1;
		} catch (NoSuchElementException e) {
			Browser.getLogger().info("Web Element not displayed : " + ElementInfo);
			fail( "The element check does not exist : " + ElementInfo);

		} catch (ElementClickInterceptedException e) {
			Browser.getLogger().info(
					"Element hidden. Scrolling...!" + ElementInfo);
			scroll_to_view_WebElement(driver, wElement, ElementInfo);
			if(Verify_Object_IsDisplayed(wElement, driver))
			{
				wait_ForSeconds(5);
				try {
					wElement.click();
				} catch (ElementClickInterceptedException e1) {

					Click_Element_byActions(wElement, driver, ElementInfo);
				}
			}

		} catch (ElementNotInteractableException e) {
			Browser.getLogger().info(
					"Element hidden. Unable to click, trying to Click using Actions Class " + ElementInfo);
			Click_Element_byActions(wElement, driver, ElementInfo);
			if(reccursiveCallIndex < 5) {
				reccursiveCallIndex++;
				Select_CheckBox(driver, wElement, OnOrOff, ElementInfo);
			}
			else
			{
				fail("Could not un-select the checkbox  : " + ElementInfo);
			}

		} catch (InvalidElementStateException e) {
			Browser.getLogger().info(
					"Element click failed...!" + ElementInfo);

			fail("Element check failed : " + ElementInfo);

		}
		catch (WebDriverException e) {

			Browser.getLogger().info("Element is not found. click failed..!" + " : " + ElementInfo);
			fail("Element check failed (WebDriverException): " + ElementInfo);

		}
	}

	
	public void Mobile_ScrollToViewElement(WebDriver driver, WebElement wElement, String ElementInfo) {
		((JavascriptExecutor)driver).executeScript("arguments[0].scrollIntoView();", wElement);
	}
	
	public  void Scrollto_TopOfPage(WebDriver driver) {

		((JavascriptExecutor) driver).executeScript("window.scrollTo(document.body.scrollHeight, 0)");
		wait_ForSeconds(3);
		}
	
	public  void ScrolltillEndOfPage(WebDriver driver) {

		((JavascriptExecutor) driver).executeScript("window.scrollTo(0, document.body.scrollHeight)");
		wait_ForSeconds(3);
		Browser.getLogger().info("Page scrolled till the end..!");
		}
	
	public  void ScrollToViewElement(WebDriver driver, String wElementXpath, String ElementInfo) {

		WebElement wElement = driver.findElement(By.xpath(wElementXpath));
		((JavascriptExecutor) driver).executeScript("window.scrollTo(document.body.scrollHeight, 0)");
		((JavascriptExecutor)driver).executeScript("arguments[0].scrollIntoView();", wElement);
	}
	
	
	
	public void Click_Element_IfPresent(WebDriver driver, WebElement wElement, String ElementInfo) {

		try {
			wElement.click();
			Browser.getLogger().info("Clicked the element : " + ElementInfo);
		} catch (NoSuchElementException e) {
			Browser.getLogger().info("Web Element not present-- continuing without click : " + ElementInfo);

		} catch (ElementClickInterceptedException e) {
			Browser.getLogger().info(
					"Element hidden. Unable to click, trying to Click using Actions Class " + ElementInfo);
			Click_Element_byActions(wElement, driver,ElementInfo);
		} catch (ElementNotInteractableException e) {
			Browser.getLogger().info(
					"Element hidden. Unable to click, trying to Click using Actions Class " + ElementInfo);
			Click_Element_byActions(wElement, driver,ElementInfo);

		} catch (InvalidElementStateException e) {
			Browser.getLogger().info(
					"Element hidden. Unable to click, trying to Click using Actions Class " + ElementInfo);
			Click_Element_byActions(wElement, driver,ElementInfo);

		} catch (StaleElementReferenceException e) {

			Browser.getLogger().info("Element is stale..! Reinitialising..!" + " : " + ElementInfo);
			WebElement tempelement = reinitialise_WebElement(wElement,driver);
			Click_Element_IfPresent(driver, wElement,ElementInfo);

		} catch(WebDriverException e)
		{
			Browser.getLogger().info("Web Element not present (WebDriverException)-- continuing without click : " + ElementInfo);
		}

	}
	
	
	/*
	 * @Purpose: To verify Webpage URL
	 */

	public boolean Verify_WebPage_URL(WebDriver driver, String textToVerify) {
		try {
			if (driver.getCurrentUrl().trim().contains(textToVerify.trim())&&!textToVerify.isEmpty()) {
				Browser.getLogger().info("WebPage URL comparison successfull for : " + driver.getCurrentUrl());
				return true;
			} else {
				Browser.getLogger().info("Text comparison failed for the WebPage" + " Expected :" + textToVerify
						+ " Actual :" + driver.getCurrentUrl());

				return false;
			}
		} catch (NullPointerException e) {
			fail("Driver is null");
			return false;
		}

	}


public void Select_List_byVisibleText(WebElement wElement,
		String valueToSelect, WebDriver driver, String ElementInfo) {
	try {
	Select selectElement = new Select(wElement);
	Click_Element( driver,wElement,ElementInfo);
	wait_ForSeconds(5);
	selectElement.selectByVisibleText(valueToSelect);
	Browser.getLogger().info("Selected : " + valueToSelect + " from : "
			+ ElementInfo);
	} catch (NoSuchElementException e) {
		Browser.getLogger()
		.info("Select not displayed : " + ElementInfo);
		fail("The select object doesn't exist : " + ElementInfo);
	} catch (org.openqa.selenium.StaleElementReferenceException e) {
	WebElement tempElement = reinitialise_WebElement(wElement,driver);
	Select_List_byVisibleText(tempElement,valueToSelect,driver,ElementInfo);
	}
}



/*
 * @Purpose: To Verify Object is Enabled by Xpath
 */
// @Step("Verify {0} : is enabled")
public boolean Verify_Object_IsEnabled(String xpathString, WebDriver driver) {
	try {
		if (driver.findElement(By.xpath(xpathString)).isEnabled()) {
			Browser.getLogger().info("Web Element is Enabled : " + xpathString);
			return true;
		} else {
			Browser.getLogger().info("Web Element is disabled : " + xpathString);
			return false;
		}
	} catch (NoSuchElementException e) {
		Browser.getLogger().info("Web Element not displayed : " + xpathString);
		return false;
	}

}
	public boolean Verify_Object_IsEnabled(WebElement wElement, WebDriver driver) {
		try {
			if (wElement.isEnabled()) {
				Browser.getLogger().info("Web Element is Enabled : " + wElement);
				return true;
			} else {
				Browser.getLogger().info("Web Element is disabled : " + wElement);
				return false;
			}
		} catch (NoSuchElementException e) {
			Browser.getLogger().info("Web Element not displayed : " + wElement);
			return false;
		}

	}




public boolean switchToChildWindow(WebDriver driver,
		String urlContains) // Modified this as getWindowHandles do not
							// return browser instances in correct order
{
	int i = 1;
	boolean switchedWindow = false;
	do {
		String ChildWindowhandle;
		for (String iterator : driver.getWindowHandles()) {
			ChildWindowhandle = iterator;
			driver.switchTo().window(ChildWindowhandle);
			waitUntilPageLoads(driver);
			if (driver.getCurrentUrl().contains(urlContains)) {
				// driver.switchTo().window(ChildWindowhandle);
				driver.manage().window().maximize();
				i = 30;
				Browser.getLogger()
						.info("Switched the window successfully..");
				switchedWindow = true;
				break;
			}
			wait_ForSeconds(2);
		}
		wait_ForSeconds(2);
		i++;
	} while (i < 30);

	if (!switchedWindow) {
		Browser.getLogger().info("Unable to switch window");
		// assertTrue(false, "Unable to switch window");
		return false;
	}
	return true;
}


public boolean deleteCookie(WebDriver driver, String cookieName, String cookievalue,String domain,boolean... isRefreshRequired) {
	Cookie cookie = new Cookie(cookieName, cookievalue,domain,"/",null);
	driver.manage().deleteCookie(cookie);
	if(driver.manage().getCookieNamed(cookieName).getValue().compareTo(cookievalue)==0) {
		String baseURL = null;
		if(isRefreshRequired.length==1 && isRefreshRequired[0])
			driver.get(baseURL);
		return true;
	}else {
		return false;
	}
}

public boolean setCookie(WebDriver driver, String cookieName, String cookievalue,String domain,boolean... isRefreshRequired) {
	Cookie cookie = new Cookie(cookieName, cookievalue,domain,"/",null);
	driver.manage().addCookie(cookie);
	if(driver.manage().getCookieNamed(cookieName).getValue().compareTo(cookievalue)==0) {
		String baseURL = null;
		if(isRefreshRequired.length==1 && isRefreshRequired[0])
			driver.get(baseURL);
		return true;
	}else {
		return false;
	}
}

public void Set_Text(WebElement wElement, String valueToSet, WebDriver driver, String ElementInfo) {

	try {
	// WebBrowser.wait_ForSeconds(1);
		wElement.clear();
	wElement.sendKeys(valueToSet);
		Browser.getLogger().info("Entered Text : " + valueToSet
	+ " to element: " + ElementInfo);
	} 
	catch (NoSuchElementException e) {
		Browser.getLogger().info("Web Element not displayed (NoSuchElementException)" + ElementInfo);
		fail( "The element to set text doesn't exist : "
		+ ElementInfo);
		}
	catch (WebDriverException e) {
		Browser.getLogger().info("Web Element not displayed(WebDriverException) :" + ElementInfo);
		fail( "The element to set text doesn't exist  : " + ElementInfo);
		}
}

	public void Press_Key(String elXpath, String KeyPress, WebDriver driver, String ElementInfo)
	{
		try {
			WebElement el = driver.findElement(By.xpath(elXpath));
			switch (KeyPress) {
				case "ENTER" :
					el.sendKeys(Keys.ENTER);
					Browser.getLogger().info("Pressed ENTER against the element : " + ElementInfo);
					break;
				case "TAB" :
					el.sendKeys(Keys.TAB);
					Browser.getLogger().info("Pressed TAB against the element : " + ElementInfo);
					break;
				case "DOWN ARROW" :
					el.sendKeys(Keys.ARROW_DOWN);
					Browser.getLogger().info("Pressed TAB against the element : " + ElementInfo);
					break;
				default :
					Browser.getLogger().info("Key not defined");
					fail( "Key not defined");
			}
		} catch (NoSuchElementException e) {
			Browser.getLogger().info("Web Element not displayed");
			fail( "The element to set text doesn't exist : "
					+ ElementInfo);
		}
		catch (WebDriverException e) {
			Browser.getLogger().info("Web Element not displayed :" + ElementInfo);
			fail( "The element to press key doesn't exist : " + ElementInfo);
		}
	}

public void Press_Key(WebElement el, String KeyPress, WebDriver driver, String ElementInfo)
{	
	try {
		switch (KeyPress) {
			case "ENTER" :
				el.sendKeys(Keys.ENTER);
				Browser.getLogger().info("Pressed ENTER against the element : " + ElementInfo);
				break;
			case "TAB" : 
				el.sendKeys(Keys.TAB);
				Browser.getLogger().info("Pressed TAB against the element : " + ElementInfo);
				break;
			case "DOWN ARROW" : 
				el.sendKeys(Keys.ARROW_DOWN);
				Browser.getLogger().info("Pressed TAB against the element : " + ElementInfo);
				break;
			default :
				Browser.getLogger().info("Key not defined");
				fail( "Key not defined");
		}
	} catch (NoSuchElementException e) {
		Browser.getLogger().info("Web Element not displayed");
		fail( "The element to set text doesn't exist : "
		+ ElementInfo);
		}
	catch (WebDriverException e) {
		Browser.getLogger().info("Web Element not displayed :" + ElementInfo);
		fail( "The element to press key doesn't exist : " + ElementInfo);
		}
}

@Step("Find java script errors in the page loaded")
public boolean find_JavascriptErrors(WebDriver driver) {
	LogEntries logentries = driver.manage().logs().get(LogType.BROWSER);
	int i = 1;
	boolean jscriptErrorNotPresent = true;
	for (LogEntry entry : logentries) {
		EnterFailLogs(entry, i);
		i++;
		if (entry.getLevel().toString().trim()
				.equalsIgnoreCase("WARNING")) {
			// do nothing
		} else {
			jscriptErrorNotPresent = false;
		}

	}
	return jscriptErrorNotPresent;
}

@Attachment(value = "Error : {1}")
public String EnterFailLogs(LogEntry entry, int i) {
	String logStringMsg = "Issue :" + i + ":  " + entry.getLevel()
			+ "   :   " + entry.getMessage();

	return logStringMsg;
}

/*
 * @Purpose: To delete temp files from TEMP folder. Used after driver.quit.
 */
public synchronized void Delete_TempFiles() {
	String tempLocation = System.getProperty("user.home")
			+ "\\AppData\\Local\\Temp\\";
	// System.out.println(tempLocation);
	File dir = new File(tempLocation);
	deleteDir(dir);
	Browser.getLogger().info("TEMP files deleted...!");
	wait_ForSeconds(4);
}

/*
 * @Purpose : Used inside Delete_TempFiles
 */
private static boolean deleteDir(File dir) {
	File tempFile;
	final int TimeLimit = 30 * 60 * 1000;
	long tenAgo = System.currentTimeMillis() - TimeLimit;
	try {
		if (dir.isDirectory()) {
			String[] children = dir.list();

			for (int i = 0; i < children.length; i++) {
				tempFile = new File(dir, children[i]);
				if (tempFile.lastModified() < tenAgo)
					if (tempFile.delete()) {
						// System.out.println("Deleted : "+tempFile.getName());
					} else {
						deleteDir_allSub(tempFile);
					}
			}
		}
		dir.delete();
	} catch (NullPointerException e) {
		fail("Cannot delete file (null Pointer Exception)");
	}
	return true;
}

/*
 * @Purpose : Used inside Delete_TempFiles
 */
private static boolean deleteDir_allSub(File dir) {

	try {
		String[] children;
		if (dir.isDirectory()) {
			children = dir.list();

			for (int i = 0; i < children.length; i++) {
				boolean success = deleteDir_allSub(new File(dir, children[i]));
				if (!success) {
					Browser.getLogger().info("Deletion failed: " + children[i]);
					return true;
				}
			}
		}

		dir.delete();
		return true;
	} catch (NullPointerException e) {
		return true;
	}
}

/*
 * @Purpose : To attach any text required in the report.
 */
public static void Record_TextData(String Header, String logStringMsg, Scenario scn) {

	logStringMsg = Header + " :  " + logStringMsg;
	scn.attach(logStringMsg.getBytes(), "text/plain", Header);

}

/*
 * @Purpose : Click on the given element and select all (CTRL +A)
 */
public void Select_All_Elements(WebDriver driver,
		WebElement wElement, String ElementInfo) {
	try {
		Click_Element(wElement,driver,ElementInfo);
		wait_ForSeconds(10);
		Actions action = new Actions(driver);
		// '\u0061' is used for 'a'
		action.keyDown(Keys.CONTROL).sendKeys(String.valueOf('\u0061'))
			.perform();
		//wElement.sendKeys(Keys.CONTROL + "a" + Keys.CONTROL);
		
	} catch (NoSuchElementException e) {
		Browser.getLogger().info("Select All failed : " + ElementInfo);
	}

}

/*
 * @Purpose : Copy a file from one folder to another.
 */
public void CopyFile(String from, String to) {

	File fromFile = new File(from);
	File toFile = new File(to + fromFile.getName());

	if (toFile.exists()) {
		System.out.println("File already exists");

		try {
			FileUtils.copyFile(fromFile, toFile);
		} catch (IOException e) {
			Browser.getLogger().error(
					"Someone is using the File :" + toFile.getName());
			fail("Some other process is using the File : " + toFile.getName());
		}
	}
}

/*
 * @Purpose : Validate prefix formatting of a text
 */

public boolean Validate_PrefixSubText(String mainText, String prefix)
{
	return mainText.startsWith(prefix);
}

public void waitUntil_ObjectIsClickable(WebDriver driver,
		WebElement wElement) {
	WebDriverWait wait = new WebDriverWait(driver, 100);
	try {
		wait.until(ExpectedConditions.elementToBeClickable(wElement));
	} catch (TimeoutException e) {
		Browser.getLogger().info("Element not clickable:" + wElement.toString());
		// assertTrue(false, "Element not found:" + wElement.toString());
	} catch (NoSuchElementException e) {
		Browser.getLogger().info("Element not clickable:" + wElement.toString());
		// assertTrue(false, "Element not found:" + wElement.toString());
	}
	catch (StaleElementReferenceException e) {
		wait_ForSeconds(5);
		Browser.getLogger().info(
				"Element is stale..! Reinitialising..!"
						+ " : " + wElement.toString());
		WebElement tempElement = reinitialise_WebElement(wElement,driver);
		waitUntil_ObjectIsClickable(driver,tempElement);
	}
}

public void Click_Element(WebElement wElement, WebDriver driver, String ElementInfo)
		{
Click_Element(driver, wElement, ElementInfo);

}

public void ClickElement_JSript(WebDriver driver,
		WebElement myelement, String ElementInfo)  throws WebDriverException {
	JavascriptExecutor js = (JavascriptExecutor) driver;
	try {
		js.executeScript("arguments[0].scrollIntoView()", myelement);
		js.executeScript("arguments[0].click();", myelement);
		Browser.getLogger()
				.info("Clicked using jScript : " + ElementInfo);
	} catch (NoSuchElementException e) {
		Browser.getLogger().info(
				"Element to click doesn't exist : " + ElementInfo);
		fail("Element to click doesn't exist : " + ElementInfo);
	}
	catch (org.openqa.selenium.StaleElementReferenceException e1) {
		WebElement tempElement = reinitialise_WebElement(myelement,driver);
		ClickElement_JSript(driver,tempElement,ElementInfo);
	}
}

public WebElement reinitialise_WebElement(WebElement webEl, WebDriver webDriver)
{
	String elementInfo = webEl.toString();
	
	Browser.getLogger().info("Element reinitialise : " + elementInfo);
    try {
		elementInfo = elementInfo.substring(elementInfo.indexOf("->"));
	} catch (ArrayIndexOutOfBoundsException e) {
		return reinitialise_WebElement_withoutRef(webEl,webDriver);
	}
    String elementLocator = elementInfo.substring(elementInfo.indexOf(": "));
    elementLocator = elementLocator.substring(2, elementLocator.length() - 1);
    Browser.getLogger().info(elementInfo);

    WebElement retWebEl = null;
    if (elementInfo.contains("-> link text:")) {
        retWebEl = webDriver.findElement(By.linkText(elementLocator));
    } else if (elementInfo.contains("-> name:")) {
        retWebEl = webDriver.findElement(By.name(elementLocator));
    } else if (elementInfo.contains("-> id:")) {
        retWebEl = webDriver.findElement(By.id(elementLocator));
    } else if (elementInfo.contains("-> xpath:")) {
        retWebEl = webDriver.findElement(By.xpath(elementLocator));
    } else if (elementInfo.contains("-> class name:")) {
        retWebEl = webDriver.findElement(By.className(elementLocator));
    } else if (elementInfo.contains("-> css selector:")) {
        retWebEl = webDriver.findElement(By.cssSelector(elementLocator));
    } else if (elementInfo.contains("-> partial link text:")) {
        retWebEl = webDriver.findElement(By.partialLinkText(elementLocator));
    } else if (elementInfo.contains("-> tag name:")) {
        retWebEl = webDriver.findElement(By.tagName(elementLocator));
    } else {
    	Browser.getLogger().info("No valid locator found. COuld not refresh the stale element : " +
        		webEl );
		fail("No valid locator found. COuld not refresh the stale element : " +
        		webEl);
    }
    return retWebEl;
}

private WebElement reinitialise_WebElement_withoutRef(WebElement webEl,WebDriver webDriver)
{
String elementInfo = webEl.toString();
Browser.getLogger().info("Element reinitialise : " + elementInfo);
elementInfo = elementInfo.substring(elementInfo.indexOf("By."));
Browser.getLogger().info("elementInfo : " + elementInfo);
String elementLocator = elementInfo.substring(elementInfo.indexOf(": "));
elementLocator = elementLocator.substring(2, elementLocator.length() - 1);

WebElement retWebEl = null;
if (elementInfo.contains("link text:")) {
    retWebEl = webDriver.findElement(By.linkText(elementLocator));
} else if (elementInfo.contains("name:")) {
    retWebEl = webDriver.findElement(By.name(elementLocator));
} else if (elementInfo.contains("id:")) {
    retWebEl = webDriver.findElement(By.id(elementLocator));
} else if (elementInfo.contains("xpath:")) {
    retWebEl = webDriver.findElement(By.xpath(elementLocator));
} else if (elementInfo.contains("class name:")) {
    retWebEl = webDriver.findElement(By.className(elementLocator));
} else if (elementInfo.contains("css selector:")) {
    retWebEl = webDriver.findElement(By.cssSelector(elementLocator));
} else if (elementInfo.contains("partial link text:")) {
    retWebEl = webDriver.findElement(By.partialLinkText(elementLocator));
} else if (elementInfo.contains("tag name:")) {
    retWebEl = webDriver.findElement(By.tagName(elementLocator));
} else {
    Browser.getLogger().info("No valid locator found. COuld not refresh the stale element : " +
    		webEl);
	fail("No valid locator found. COuld not refresh the stale element : " +
    		webEl);
}
return retWebEl;
}


public WebElement validate_text_in_any_matchingWebElements(WebDriver driver, String wElementXpath, String textToCompare)
{
	WebElement elementTemp = null; // dummy element

	try {
		List<WebElement> AllElements = driver.findElements(By.xpath(wElementXpath));
		
		String AvailableTexts = "";
		
		for ( WebElement we: AllElements) 
		{
			if(we.getText().trim().contains(textToCompare))
			{
				Browser.getLogger().info("Text found : " + textToCompare + " under the xpath : " + wElementXpath);
				return we;
			}
			else
			{
				Browser.getLogger().info("Text comparison failed. checking next....! current text :  " + we.getText().trim());
				
				AvailableTexts = AvailableTexts + we.getText().trim() + "   |  ";
			}
		}
		
		Browser.getLogger().fatal("Text validation failed. Text : " + textToCompare + " Available texts on screen : " +  AvailableTexts);
		fail( "Text validation failed. Text : " + textToCompare + " Available texts on screen : " +  AvailableTexts);
	} catch (NoSuchElementException e) {
		Browser.getLogger().fatal("No Elements matching xpath : " + wElementXpath);
		fail( "No Elements matching xpath : " + wElementXpath);
		return elementTemp;
	} catch (StaleElementReferenceException e)
	{
		return validate_text_in_any_matchingWebElements(driver,wElementXpath,textToCompare);
	}
	return elementTemp;
}


public boolean Is_matching_text_Found(WebDriver driver, String wElementXpath, String textToCompare, Scenario scn)
{

	try {
		List<WebElement> AllElements = driver.findElements(By.xpath(wElementXpath));
		
		String AvailableTexts = "";
		
		for ( WebElement we: AllElements) 
		{
			if(we.getText().trim().contains(textToCompare))
			{
				Browser.getLogger().info("Text found : " + textToCompare + " under the xpath : " + wElementXpath);
				Record_TextData("Text_Comparison_Successfull", "Successfully found : " + textToCompare,scn);
				return true;
			}
			else
			{
				Browser.getLogger().info("Text comparison failed. checking next....! current text :  " + we.getText().trim());
				
				AvailableTexts = AvailableTexts + we.getText().trim() + "   |  ";
			}
		}
		
		Browser.getLogger().fatal("Text validation failed. Text : " + textToCompare + " Available texts on screen : " +  AvailableTexts);
		Record_TextData("Text_Comparison_Failed", "Available : " + AvailableTexts + "  Not found : " + textToCompare,scn);
		return false;
	} catch (NoSuchElementException e) {
		Browser.getLogger().fatal("No Elements matching xpath : " + wElementXpath);
		fail( "No Elements matching xpath : " + wElementXpath);
		return false;
	}
}



public void scroll_to_view_WebElement(WebDriver driver,
		String wElementXpath) {
	
	WebElement wElement = driver.findElement(By.xpath(wElementXpath));
	JavascriptExecutor js = (JavascriptExecutor) driver;
	js.executeScript("arguments[0].scrollIntoView()", wElement);
}


public WebElement return_first_DisplayedElement_MatchingXpath(String elementXpath, WebDriver driver)
{
	int size = driver.findElements(By.xpath(elementXpath)).size();
	List<WebElement> matchingElements = driver.findElements(By.xpath(elementXpath));
	int i = 0;
	while(i < size)
	{
		if(Verify_Object_IsDisplayed(matchingElements.get(i),driver))
		{
			return matchingElements.get(i);
		}
		i++;
	}
	
	return null;
}
	public boolean Verify_Object_IsDisplayed(
			String xpathString, WebDriver driver) {
		try {
			if (driver.findElement(By.xpath(xpathString)).isDisplayed()) {
				Browser.getLogger().info("WebElement is present : " + xpathString);
				return true;
			} else {
				Browser.getLogger().info(
						"Web Element is present but not displayed on screen : "
								+ xpathString);

				int noOfattempts = 0;
				Reset_ImplicitWaitTime(driver, 4);
				while (noOfattempts < 3) {
				ScrollToViewElement(driver, xpathString,xpathString);
					try {
						Browser.getLogger()
								.info("Re-attempting to find the element: "
										+ xpathString);
						if (driver.findElement(By.xpath(xpathString))
								.isDisplayed()) {
							return true;
						}
					} catch (NoSuchElementException e) {
						return false;
					}
					wait_ForSeconds(3);
					noOfattempts++;
				}

				Reset_ImplicitWaitTime(driver);
				return false;
			}
		} catch (NoSuchElementException e) {
			Browser.getLogger()
					.info("Web Element is unavailable : " + xpathString);
			return false;
		}
		catch(StaleElementReferenceException f)
		{
			Browser.getLogger().info("Stale Element exception - reloading the element reference : " + xpathString);
			wait_ForSeconds(5);
			return Verify_Object_IsDisplayed(xpathString,driver);
		}
		catch(WebDriverException f)
		{
			Browser.getLogger()
			.info("Web Element is unavailable : " + xpathString);
			wait_ForSeconds(15);
			return false;
		}

	}

	//@Step("Verify ({0}) : is displayed")
		public void Verify_Object_IsDisplayed(
				String xpathString, WebDriver driver, String FailureMessage) {
			try {
				if (driver.findElement(By.xpath(xpathString)).isDisplayed()) {
					scroll_to_view_WebElement(driver, xpathString);
					Browser.getLogger().info("WebElement is present : " + xpathString);
				} else {
					Browser.getLogger().info(
							"Web Element is present but not displayed on screen : "
									+ xpathString);

					int noOfattempts = 0;
					Reset_ImplicitWaitTime(driver, 4);
					while (noOfattempts < 6) {
					scroll_to_view_WebElement(driver, xpathString);
						try {
							Browser.getLogger()
									.info("Re-attempting to find the element: "
											+ xpathString);
							if (driver.findElement(By.xpath(xpathString))
									.isDisplayed()) {
								Reset_ImplicitWaitTime(driver);
							}
						} catch (NoSuchElementException e) {
							// Do Nothing. proceed to next iteration.
						}
						wait_ForSeconds(8);
						noOfattempts++;
					}

					Reset_ImplicitWaitTime(driver);
					fail( FailureMessage);
				}
			} catch (NoSuchElementException e) {
				Browser.getLogger()
						.info("Web Element is unavailable : " + xpathString);
				fail( FailureMessage);
			}
			catch(StaleElementReferenceException f)
			{
				Browser.getLogger().info("Stale Element exception - reloading the element reference : " + xpathString);
				wait_ForSeconds(5);
				Verify_Object_IsDisplayed(xpathString,driver);
			}
			catch(WebDriverException f)
			{
				Browser.getLogger()
				.info("Web Element is unavailable : " + xpathString);
				wait_ForSeconds(15);
				fail(FailureMessage);
			}

		}
	public boolean Verify_Object_IsDisabled(WebElement wElement,WebDriver driver, String ElementInfo) {
		try {
			if (!wElement.isEnabled()) {
				Browser.getLogger().info(
						"Web Element is disabled : " + ElementInfo);
				return true;
			} else {
				Browser.getLogger()
						.info("Web Element enabled (Expectation : disabled) : "
								+ ElementInfo);
				fail( "Element is enabled : " + ElementInfo);
				return false;
			}
		} catch (NoSuchElementException e) {
			Browser.getLogger().info("Web Element not available/displayed : "
					+ ElementInfo);
			fail("Element is not available : " + ElementInfo);
			return false;
		}

	}
	public boolean Verify_TextBox_Value(WebElement wElement,
			String Value, WebDriver driver) {
		try {
			if (wElement.getAttribute("value").equalsIgnoreCase(Value)) {
				Browser.getLogger().info("Textbox validated successfully...!");
				return true;
			} else {
				return false;
			}
		} catch (NoSuchElementException e) {
			Browser.getLogger()
					.info("Web Element not displayed");
			return false;
		} catch(WebDriverException e)
		{
			Browser.getLogger().info("Web Element not displayed (WebDriverException)");
			return false;
		}

	}
	
	public boolean Verify_TextBox_Value(WebElement wElement,
			String Value, WebDriver driver, String ElementInfo) {
		try {
			if (wElement.getAttribute("value").equalsIgnoreCase(Value)) {

				return true;
			} else {
				fail( "Text box value not matching. Expected : " + Value + "  Actual : " + wElement.getAttribute("value"));
				return false;
			}
		} catch (NoSuchElementException e) {
			Browser.getLogger()
					.info("Web Element not displayed : " + ElementInfo);
			fail( "Web Element not displayed : " + ElementInfo);
			return false;
		} catch(WebDriverException e)
		{
			Browser.getLogger().info("Web Element not displayed (WebDriverException) : " + ElementInfo);
			fail( "Web Element not displayed : " + ElementInfo);
			return false;
		}

	}
	
	public boolean Verify_TextBox_Value(String wElementXpath,
			String Value, WebDriver driver, String ElementInfo) {
		try {
			WebElement wElement = driver.findElement(By.xpath(wElementXpath));
			if (wElement.getAttribute("value").equalsIgnoreCase(Value)) {

				return true;
			} 
			else {
				Browser.getLogger()
				.info("Validation failed. Expected : " + Value 
						+ "  Actual : " + wElement.getAttribute("value"));
				fail( "Validation failed. Expected : " + Value
						+ "  Actual : " + wElement.getAttribute("value"));
				return false;
			}
		} catch (NoSuchElementException e) {
			Browser.getLogger()
					.info("Web Element not displayed : " + ElementInfo);
			fail( "Web Element not displayed : " + ElementInfo);
			return false;
		} catch(WebDriverException e)
		{
			Browser.getLogger().info("Web Element not displayed (WebDriverException) : " + ElementInfo);
			fail( "Web Element not displayed (WebDriverException) : " + ElementInfo);
			return false;
		}

	}
	public String Fetch_TextBox_Value(WebElement wElement, WebDriver driver, String ElementInfo) {
		try {
			return wElement.getAttribute("value");

		} catch (NoSuchElementException e) {
			Browser.getLogger()
					.info("Web Element not displayed : " + ElementInfo);
			fail("Web Element not displayed : " + ElementInfo);
			return "NOT FOUND";
		} catch(WebDriverException e)
		{
			Browser.getLogger().info("Web Element not displayed (WebDriverException) : " + ElementInfo);
			fail("Web Element not displayed (WebDriverException) : " + ElementInfo);
			return "NOT FOUND";
		}

	}
	
	public String Fetch_TextBox_Value(String wElementXpath, WebDriver driver, String ElementInfo) {
		try {
			return driver.findElement(By.xpath(wElementXpath)).getAttribute("value");

		} catch (NoSuchElementException e) {
			Browser.getLogger()
					.info("Web Element not displayed : " + ElementInfo);
			fail("Web Element not displayed : " + ElementInfo);
			return "NOT FOUND";
		} catch(WebDriverException e)
		{
			Browser.getLogger().info("Web Element not displayed (WebDriverException) : " + ElementInfo);
			fail("Web Element not displayed (WebDriverException) : " + ElementInfo);
			return "NOT FOUND";
		}

	}
		

		// @Step("Set text in {0} : value : {1}")
			public void Set_Text(String wElementXpath, String valueToSet, WebDriver driver) {
				waitUntilPageLoads(driver);
				WebElement wElement = driver.findElement(By.xpath(wElementXpath));
				try {
					// WebBrowser.wait_ForSeconds(1);
					wElement.sendKeys(valueToSet);
					Browser.getLogger().info("Entered Text : " + valueToSet
							+ " to element: " + wElement);
				} catch (NoSuchElementException e) {
					Browser.getLogger()
							.info("Web Element not displayed : " + wElement);
					fail( "The element to set text doesn't exist : "
							+ wElement);
				}
			}
			
		
		
		public void waitUntil_ObjectIsInteractable(WebDriver driver,
				WebElement wElement, String ElementInfo) {
			WebDriverWait wait = new WebDriverWait(driver, 100);
			try {
				wait.until(ExpectedConditions.elementToBeClickable(wElement));
			} catch (TimeoutException e1) {
				Browser.getLogger().info("Timed out waiting for the element: "
						+ wElement.toString());
			}
			try {
				if (wElement.isDisplayed()) {
					return;
				} else {
					Browser.getLogger().info(
							"Page not loaded in stipulated time. Test failed");
					fail("Page not loaded in stipulated time. Test failed");
				}
			} catch (NoSuchElementException e) {
				Browser.getLogger().info(
						"Element not found in stipulated time :" + e);
				fail("Element not found in stipulated time :" + e);
			}
			catch (StaleElementReferenceException e) {
				wait_ForSeconds(5);
				Browser.getLogger().info(
						"Element is stale..! Reinitialising..!"
								+ " : " + ElementInfo);
				WebElement tempElement = reinitialise_WebElement(wElement,driver);
				waitUntil_ObjectIsInteractable(driver,tempElement,ElementInfo);
			}
		}
		public void Clear_Text(WebElement wElement, WebDriver driver, String ElementInfo) {

			try {
				waitUntilPageLoads(driver);
				int noOfAttempts = 1;
				try {
					while(!wElement.getAttribute("value").equalsIgnoreCase("") && noOfAttempts < 5) // for text box
					{
						
						wElement.clear();
						wait_ForSeconds(4);
						Browser.getLogger().info("Clearing.. Attempt : " + noOfAttempts);
						Browser.getLogger().info("Current value : " + wElement.getAttribute("value").trim());
						noOfAttempts++;
					}
				} catch (NullPointerException e) { //when it is a text area, this part works
					while(!wElement.getText().equalsIgnoreCase("") && noOfAttempts < 5)
					{
						
						wElement.clear();
						wait_ForSeconds(4);
						noOfAttempts++;
					}
				}
				Browser.getLogger()
						.info("cleared the value from " + wElement);
			} catch (NoSuchElementException e) {
				Browser.getLogger()
						.info("Web Element not displayed : " + wElement);
				fail( "The element to set text doesn't exist : "
						+ wElement.toString());
			} catch (InvalidElementStateException e1) {
				waitUntil_ObjectIsInteractable(driver, wElement,ElementInfo);
				int noOfAttempts = 0;
				while (noOfAttempts < 30) {
					if (Verify_Object_IsEnabled(wElement,driver,ElementInfo)) {
						Browser.getLogger().info("TextBox/TextArea is enabled "
								+ wElement);
						break;
					}
					noOfAttempts++;
					wait_ForSeconds(2);

					try {
						wElement.clear();
						Browser.getLogger().info(
								"cleared the value from " + wElement);
					} catch (NoSuchElementException e) {
						Browser.getLogger().info("Web Element not displayed : "
								+ wElement);
						fail( "The element to set text doesn't exist : "
								+ wElement);
					} catch (InvalidElementStateException e2) {
						Browser.getLogger()
								.info("Web Element not loaded / is read-only : "
										+ wElement);
						fail( "Web Element not loaded / is read-only : "
								+ wElement);
					}
				}
			}
			catch (org.openqa.selenium.StaleElementReferenceException e1) {
				WebElement tempElement = reinitialise_WebElement(wElement,driver);
				Clear_Text(tempElement,driver,ElementInfo);
			}
			
			if(!wElement.getAttribute("value").equalsIgnoreCase(""))
			{
				wElement.sendKeys(Keys.chord(Keys.CONTROL,"a", Keys.DELETE));
				wait_ForSeconds(5);
				if(!wElement.getAttribute("value").equalsIgnoreCase(""))
				{
				Browser.getLogger().info("Unable to clear the text from : " + ElementInfo + "  Showing content : " + wElement.getAttribute("value"));
					fail( "Unable to clear the text from : " + ElementInfo + "  Showing content : " + wElement.getAttribute("value"));
				}
			}
		}
		
		public void Clear_Text(String wElementXpath, WebDriver driver, String ElementInfo) {
			WebElement wElement = null;
			try {
				wElement = driver.findElement(By.xpath(wElementXpath));
				waitUntilPageLoads(driver);
				int noOfAttempts = 1;
				try {
					while(!wElement.getAttribute("value").equalsIgnoreCase("") && noOfAttempts < 5) // for text box
					{
						
						wElement.clear();
						wait_ForSeconds(2);
						Browser.getLogger().info("Clearing.. Attempt : " + noOfAttempts);
						Browser.getLogger().info("Current value : " + wElement.getAttribute("value").trim());
						noOfAttempts++;
					}
				} catch (NullPointerException e) { //when it is a text area, this part works
					while(!wElement.getText().equalsIgnoreCase("") && noOfAttempts < 5)
					{
						
						wElement.clear();
						wait_ForSeconds(2);
						noOfAttempts++;
					}
				}
				Browser.getLogger()
						.info("cleared the value from " + wElement);
			} catch (NoSuchElementException e) {
				Browser.getLogger()
						.info("Web Element not displayed : " + wElement);
				fail("The element to set text doesn't exist : "
						+ wElement);
			} catch (InvalidElementStateException e1) {
				waitUntil_ObjectIsInteractable(driver, wElement,ElementInfo);
				int noOfAttempts = 0;
				while (noOfAttempts < 30) {
					if (Verify_Object_IsEnabled(wElement,driver,ElementInfo)) {
						Browser.getLogger().info("TextBox/TextArea is enabled "
								+ wElement);
						break;
					}
					noOfAttempts++;
					wait_ForSeconds(2);

					try {
						wElement.clear();
						Browser.getLogger().info(
								"cleared the value from " + wElement);
					} catch (NoSuchElementException e) {
						Browser.getLogger().info("Web Element not displayed : "
								+ wElement);
						fail("The element to set text doesn't exist : "
								+ wElement);
					} catch (InvalidElementStateException e2) {
						Browser.getLogger()
								.info("Web Element not loaded / is read-only : "
										+ wElement);
						fail("Web Element not loaded / is read-only : "
								+ wElement);
					}
				}
			}
			catch (org.openqa.selenium.StaleElementReferenceException e1) {
				WebElement tempElement = reinitialise_WebElement(wElement,driver);
				Clear_Text(tempElement,driver,ElementInfo);
			}
		}

		/*
		 * @Purpose: click on element by providing xpath
		 */
		public void Click_Element(String wElementString,
				WebDriver driver, String ElementInfo) {
			WebElement wElement = null;
			try {
				wElement = driver.findElement(By.xpath(wElementString));
				wElement.click();
				Browser.getLogger().info("Clicked the element : " + wElementString);
				waitUntilPageLoads(driver);
			} catch (NoSuchElementException e) {
				Browser.getLogger()
						.info("Web Element not displayed : " + wElementString);
				fail("The element to click doesn't exist : " + wElementString);
			} catch (ElementNotInteractableException e) {
				Browser.getLogger().info("Element hidden. " + wElementString);
				ClickElement_JSript(driver, wElement,ElementInfo);
			} catch (org.openqa.selenium.StaleElementReferenceException e1) {
				Click_Element(wElementString,driver,ElementInfo);
			}

		}

		
		public void Click_Element_IfPresent(WebElement wElement, WebDriver driver, String ElementInfo)
			{
			try {
				waitUntilPageLoads(driver);
				if(waitUntil_ObjectLoads(driver, wElement, 1, ElementInfo))
				{
					wait_ForSeconds(5);
				}
				else
				{
					Browser.getLogger()
					.info("Web Element not displayed : " + ElementInfo
					+ "   ..Continuing without click..!");
					return;
				}
				wElement.click();
				Browser.getLogger().info(
						"Clicked the element: " + ElementInfo);
				wait_ForSeconds(4);
				
			}

			catch (NoSuchElementException e) {
				Browser.getLogger()
						.info("Web Element not displayed : " + ElementInfo
						+ "   ..Continuing without click..!");
			}

			catch (ElementNotInteractableException e) {
				Browser.getLogger().info(
						"Element hidden. Unable to click " + ElementInfo);
				Click_Element(wElement, driver,ElementInfo);
			} catch (org.openqa.selenium.StaleElementReferenceException e1) {
				
				try {
					WebElement tempElement = reinitialise_WebElement(wElement,driver);
					Click_Element_IfPresent(tempElement,driver,ElementInfo);
				} catch (NoSuchWindowException e11) {
					Browser.getLogger().info("Window disappeared on clicking...!");
				}
			}
			catch(NoSuchWindowException e2)
			{
				Browser.getLogger().info("Window disappeared on clicking...!");
			}

		}
		
		public void Click_Element_IfPresent(String wElementXpath, WebDriver driver, String ElementInfo)
		{
			WebElement wElement;
		try {
			wElement = driver.findElement(By.xpath(wElementXpath));
			waitUntilPageLoads(driver);
			if(waitUntil_ObjectLoads(driver, wElement, 1, ElementInfo))
			{
				wait_ForSeconds(5);
			}
			else
			{
				Browser.getLogger()
				.info("Web Element not displayed : " + ElementInfo
				+ "   ..Continuing without click..!");
				return;
			}
			wElement.click();
			Browser.getLogger().info(
					"Clicked the element: " + ElementInfo);
			wait_ForSeconds(4);
			
		}

		catch (NoSuchElementException e) {
			Browser.getLogger()
					.info("Web Element not displayed : " + ElementInfo
					+ "   ..Continuing without click..!");
		}

		catch (ElementNotInteractableException e) {
			Browser.getLogger().info(
					"Element hidden. Unable to click " + ElementInfo);
			Click_Element(wElementXpath, driver,ElementInfo);
		} catch (org.openqa.selenium.StaleElementReferenceException e1) {
			
			try {
				Click_Element_IfPresent(wElementXpath,driver,ElementInfo);
			} catch (NoSuchWindowException e11) {
				Browser.getLogger().info("Window disappeared on clicking...!");
			}
		}
		catch(NoSuchWindowException e2)
		{
			Browser.getLogger().info("Window disappeared on clicking...!");
		}

	}
		
		
		
		public void Click_Element_byActions(String wElementXpath,
				WebDriver driver, String ElementInfo) {

			assertTrue(
					Verify_Object_IsDisplayed(
									wElementXpath, driver),
					"Element to click not found for the xpath : " + wElementXpath);
			WebElement wElement = driver.findElement(By.xpath(wElementXpath));
			try {
				waitUntil_ObjectIsClickable(driver, wElement);
				Click_Element_byActions(driver, wElement, ElementInfo);
				waitUntilPageLoads(driver);
			}

			catch (NoSuchElementException e) {
				Browser.getLogger()
						.info("Web Element not displayed : " + ElementInfo);
				fail("The element to click doesn't exist : "
						+ wElement.toString());
			}

			catch (ElementNotInteractableException e) {
				Browser.getLogger().info(
						"Element hidden. Unable to click " + ElementInfo);
				fail( "Element to click - hidden");
			} catch (org.openqa.selenium.StaleElementReferenceException e1) {
				Click_Element_byActions(wElementXpath,driver,ElementInfo);
			}

		}

		public void ClickElement_untilElementIsNotVisible(
				WebElement wElement, WebDriver driver, String ElementInfo) {
			int attempts = 0;
			Reset_ImplicitWaitTime(driver, 4);
			while (Verify_Object_IsDisplayed(wElement,
					driver) && attempts < 10) {
				Click_Element(wElement, driver,ElementInfo);
				wait_ForSeconds(10);
				attempts++;
			}
			wait_ForSeconds(10);
			Reset_ImplicitWaitTime(driver);
		}

		public void Select_List_byValue(WebElement wElement,
				String valueToSelect, WebDriver driver, String ElementInfo) {
			try {
				Select selectElement = new Select(wElement);
				selectElement.selectByValue(valueToSelect);
				Browser.getLogger().info("Selected : " + valueToSelect + "  from : "
						+ ElementInfo);
			} catch (NoSuchElementException e) {
				Browser.getLogger()
						.info("Select not displayed : " + ElementInfo);
				fail("The select object doesn't exist : " + ElementInfo);
			}
		}



		public void Select_List_byIndex(WebElement wElement,
				int indexToSelect, WebDriver driver, String ElementInfo) {
			try {
				Select selectElement = new Select(wElement);
				selectElement.selectByIndex(indexToSelect);
				Browser.getLogger().info("Selected : " + indexToSelect + "  from : "
						+ selectElement);
			} catch (NoSuchElementException e) {
				Browser.getLogger()
						.info("Select not displayed : " + ElementInfo);
				fail("The select object doesn't exist : " + ElementInfo);
			}
		}

		public void Verify_Element_Value_IsMatching(WebElement wElement,
				String Value, String attribute, String message
				,WebDriver driver, String ElementInfo) {

			try {
				String txtToCmpre = wElement.getAttribute(attribute);
				if (txtToCmpre.equalsIgnoreCase(Value)) {
					Browser.getLogger().info(message + " " + txtToCmpre
							+ " is matching with " + Value);
					return;
				} else {
					Browser.getLogger().info(message + " " + txtToCmpre
							+ " is not matching with " + Value);
					fail(message + " " + txtToCmpre
							+ " is not matching with " + Value);
				}
			} catch (NoSuchElementException e) {
				Browser.getLogger()
						.info("Web Element not displayed : " + ElementInfo);
				fail("Element not present : " + ElementInfo);
			}

		}
		
		public void Verify_String_insideWebElement(WebElement wElement,
				String Value, String message, WebDriver driver, String ElementInfo) {

			try {
				scroll_to_view_WebElement(driver, wElement, ElementInfo);
				String txtToCmpre = wElement.getText();
				if (txtToCmpre.toUpperCase().contains(Value.trim().toUpperCase())) {
					Browser.getLogger().info(message + " " + txtToCmpre
							+ " is matching with " + Value);
					return;
				} else {
					Browser.getLogger().info(message + ":  " + txtToCmpre
							+ " is not matching with : " + Value + " .Validation failed for : '" + ElementInfo + "'" );
					fail(message + ":  " + txtToCmpre
							+ " is not matching with : " + Value + " .Validation failed for : '" + ElementInfo + "'" );
				}
			} catch (NoSuchElementException e) {
				Browser.getLogger()
						.info("Web Element not displayed : " + ElementInfo);
				fail("Element not available : " + ElementInfo);
			}

		}
		
		public boolean Verify_String_insideWebElement(WebElement wElement,
				String Value, WebDriver driver) {

			try {
				scroll_to_view_WebElement(driver, wElement, Value);
				String txtToCmpre = wElement.getText();
				if (txtToCmpre.toUpperCase().contains(Value.trim().toUpperCase())) {
					Browser.getLogger().info(txtToCmpre
							+ " is matching with " + Value);
					 return true;
				} else {
					Browser.getLogger().info(txtToCmpre
							+ " is not matching with : " + Value);
					 return false;
				}
			} catch (NoSuchElementException e) {
				 return false;
			}

		}
		
		public boolean Verify_String_insideWebElement(String wElementXpath,
				String Value, WebDriver driver) {
			try {
				WebElement wElement = driver.findElement(By.xpath(wElementXpath));
				scroll_to_view_WebElement(driver, wElement, Value);
				String txtToCmpre = wElement.getText();
				if (txtToCmpre.toUpperCase().contains(Value.trim().toUpperCase())) {
					Browser.getLogger().info(txtToCmpre
							+ " is matching with " + Value);
					 return true;
				} else {
					Browser.getLogger().info(txtToCmpre
							+ " is not matching with : " + Value);
					 return false;
				}
			} catch (NoSuchElementException e) {
				 return false;
			}

		}
		
		
		public void Verify_PageUrl(String Validate_Text, WebDriver driver) {

			try {
				String txtToCmpre = driver.getCurrentUrl();
				if (txtToCmpre.toUpperCase().contains(Validate_Text.trim().toUpperCase())) {
					Browser.getLogger().info("URL validation successfull : " + txtToCmpre);

					 return;
				} 
				else {
					Browser.getLogger().info("URL validation failed : Actual :" + txtToCmpre
							+ " Expected : " + Validate_Text);
					fail( "URL validation failed : Actual :" + txtToCmpre
							+ " Expected : " + Validate_Text);
				}
			} catch (NoSuchElementException e) {
				Browser.getLogger().info("URL validation failed");
				fail("URL validation failed");
			}

		}
		
		

		@Step("Compare the dates : {0} and {1}")
		public boolean DateCompare(String Date1, String Date2,
				String DateFormat, String DateFormat2) {
			SimpleDateFormat sdformat;
			SimpleDateFormat sdformat2;
			switch (DateFormat) {
				case "yyyy-MM-dd" :
					sdformat = new SimpleDateFormat("yyyy-MM-dd");
					break;
				case "dd-MM-yyyy" :
					sdformat = new SimpleDateFormat("dd-MM-yyyy");
					break;
				case "MM-dd-yyyy" :
					sdformat = new SimpleDateFormat("MM-dd-yyyy");
					break;
				case "MM-dd-yy" :
					sdformat = new SimpleDateFormat("MM-dd-yy");
					break;
				case "dd-MM-yy" :
					sdformat = new SimpleDateFormat("dd-MM-yy");
					break;
				case "yyyy/MM/dd" :
					sdformat = new SimpleDateFormat("yyyy/MM/dd");
					break;
				case "dd/MM/yyyy" :
					sdformat = new SimpleDateFormat("dd/MM/yyyy");
					break;
				case "MM/dd/yyyy" :
					sdformat = new SimpleDateFormat("MM/dd/yyyy");
					break;
				case "MM/dd/yy" :
					sdformat = new SimpleDateFormat("MM/dd/yy");
					break;
				case "dd/MM/yy" :
					sdformat = new SimpleDateFormat("dd/MM/yy");
					break;
				default :
					Browser.getLogger().info("Date format undefined");
					return false;
			}
			
			switch (DateFormat2) {
				case "yyyy-MM-dd" :
					sdformat2 = new SimpleDateFormat("yyyy-MM-dd");
					break;
				case "dd-MM-yyyy" :
					sdformat2 = new SimpleDateFormat("dd-MM-yyyy");
					break;
				case "MM-dd-yyyy" :
					sdformat2 = new SimpleDateFormat("MM-dd-yyyy");
					break;
				case "MM-dd-yy" :
					sdformat2 = new SimpleDateFormat("MM-dd-yy");
					break;
				case "dd-MM-yy" :
					sdformat2 = new SimpleDateFormat("dd-MM-yy");
					break;
				case "yyyy/MM/dd" :
					sdformat2 = new SimpleDateFormat("yyyy/MM/dd");
					break;
				case "dd/MM/yyyy" :
					sdformat2 = new SimpleDateFormat("dd/MM/yyyy");
					break;
				case "MM/dd/yyyy" :
					sdformat2 = new SimpleDateFormat("MM/dd/yyyy");
					break;
				case "MM/dd/yy" :
					sdformat2 = new SimpleDateFormat("MM/dd/yy");
					break;
				case "dd/MM/yy" :
					sdformat2 = new SimpleDateFormat("dd/MM/yy");
					break;
				default :
					Browser.getLogger().info("Date format undefined");
					return false;
			}

			try {
				Date d1 = sdformat.parse(Date1);
				Date d2 = sdformat2.parse(Date2);
				if (d1.compareTo(d2) == 0) {
					Browser.getLogger().info("Dates matching : " + d1 + " and " + d2);
					return true;
				} else {
					Browser.getLogger()
							.info("Dates not matching : " + d1 + " and " + d2);
					fail( "Dates not matching : " + d1 + " and " + d2);
					return false;
				}

			} catch (ParseException e) {
				e.printStackTrace();
				Browser.getLogger().info("Date parse error");
				fail( "Date parse error...");
				return false;
			}

		}


		/*
		 * Function made for NIR dashboard validation
		 */
		public boolean Verify_WebPage_TextDisplayed(String wElementXpath,
				String textToVerify, WebDriver driver, String ElementInfo) {

			WebElement wElement = driver.findElement(By.xpath(wElementXpath));
			try {
				if (wElement.getText().trim()
						.equalsIgnoreCase(textToVerify.trim()) ||
						wElement.getText().trim()
						.equalsIgnoreCase("")) {
					Browser.getLogger().info("Text comparison successfull for : "
							+ ElementInfo);
					return true;
				} else {
					Browser.getLogger().info("Text comparison failed for : "
							+ ElementInfo + " Expected :" + textToVerify
							+ " Actual : (" + wElement.getText().trim() + ")");
					return false;
				}
			} catch (NoSuchElementException e) {
				Browser.getLogger().info("Web Element not available/displayed : "
						+ ElementInfo);
				e.printStackTrace();
				return false;
			}
			catch(StaleElementReferenceException g)
			{
				return Verify_WebPage_TextDisplayed(wElementXpath,textToVerify,driver,ElementInfo);
			}

		}

		// @Step("Add the date : {0} by {1}: {2}")
		public String AddDate(String Date1, String DayOrMonthOrYear,
				int NumberTOAdd, String DateFormat) {
			SimpleDateFormat sdformat;
			switch (DateFormat) {
				case "yyyy-MM-dd" :
					sdformat = new SimpleDateFormat("yyyy-MM-dd");
					break;
				case "dd-MM-yyyy" :
					sdformat = new SimpleDateFormat("dd-MM-yyyy");
					break;
				case "MM-dd-yyyy" :
					sdformat = new SimpleDateFormat("MM-dd-yyyy");
					break;
				case "MM-dd-yy" :
					sdformat = new SimpleDateFormat("MM-dd-yy");
					break;
				case "dd-MM-yy" :
					sdformat = new SimpleDateFormat("dd-MM-yy");
					break;
				case "yyyy/MM/dd" :
					sdformat = new SimpleDateFormat("yyyy/MM/dd");
					break;
				case "dd/MM/yyyy" :
					sdformat = new SimpleDateFormat("dd/MM/yyyy");
					break;
				case "MM/dd/yyyy" :
					sdformat = new SimpleDateFormat("MM/dd/yyyy");
					break;
				case "MM/dd/yy" :
					sdformat = new SimpleDateFormat("MM/dd/yy");
					break;
				case "dd/MM/yy" :
					sdformat = new SimpleDateFormat("dd/MM/yy");
					break;
				default :
					Browser.getLogger().info("Date format undefined");
					fail( "UnSupported Date Format");
					return "UnSupported Date Format";
			}

			try {
				Date d1 = sdformat.parse(Date1);
				Calendar c = Calendar.getInstance();
				c.setTime(d1);
				switch (DayOrMonthOrYear.toUpperCase()) {
					case "DAY" :
						c.add(Calendar.DAY_OF_MONTH, NumberTOAdd);
						Browser.getLogger()
								.info("Date addition successfull. Added : " + Date1
										+ " with : " + NumberTOAdd + " "
										+ DayOrMonthOrYear);
						return sdformat.format(c.getTime());

					case "YEAR" :
						c.add(Calendar.YEAR, NumberTOAdd);
						Browser.getLogger()
								.info("Date addition successfull. Added : " + Date1
										+ " with : " + NumberTOAdd + " "
										+ DayOrMonthOrYear);
						return sdformat.format(c.getTime());
					case "MONTH" :
						c.add(Calendar.MONTH, NumberTOAdd);
						Browser.getLogger()
								.info("Date addition successfull. Added : " + Date1
										+ " with : " + NumberTOAdd + " "
										+ DayOrMonthOrYear);
						return sdformat.format(c.getTime());
					default :
						Browser.getLogger().info("Issue with input : "
								+ DayOrMonthOrYear
								+ ". Date can only be added by Day/Month/Year");
						fail("\"Issue with input : \" + DayOrMonthOrYear + \". Date can only be added by Day/Month/Year\"");
				}

			} catch (ParseException e) {
				e.printStackTrace();
				Browser.getLogger().info("Date parse error");
				fail( "Date parse error...");
				return "Parse Exception";
			}
			return "Parse Exception";

		}

		/*
		 * Description : To retrieve today's date from system. Parameters : @String
		 * of expected value,@locator of dropdown
		 */
		public String Fetch_CurrentSystemDate(String DateFormat) {

			DateTimeFormatter datetimeformat;
			switch (DateFormat) {
				case "yyyy-MM-dd" :
					datetimeformat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
					break;
				case "dd-MM-yyyy" :
					datetimeformat = DateTimeFormatter.ofPattern("dd-MM-yyyy");
					break;
				case "MM-dd-yyyy" :
					datetimeformat = DateTimeFormatter.ofPattern("MM-dd-yyyy");
					break;
				case "MM-dd-yy" :
					datetimeformat = DateTimeFormatter.ofPattern("MM-dd-yy");
					break;
				case "dd-MM-yy" :
					datetimeformat = DateTimeFormatter.ofPattern("dd-MM-yy");
					break;
				case "yyyy/MM/dd" :
					datetimeformat = DateTimeFormatter.ofPattern("yyyy/MM/dd");
					break;
				case "dd/MM/yyyy" :
					datetimeformat = DateTimeFormatter.ofPattern("dd/MM/yyyy");
					break;
				case "MM/dd/yyyy" :
					datetimeformat = DateTimeFormatter.ofPattern("MM/dd/yyyy");
					break;
				case "MM/dd/yy" :
					datetimeformat = DateTimeFormatter.ofPattern("MM/dd/yy");
					break;
				case "dd/MM/yy" :
					datetimeformat = DateTimeFormatter.ofPattern("dd/MM/yy");
					break;
				case "dd-MMM-yyyy" :
					datetimeformat = DateTimeFormatter.ofPattern("dd-MMM-yyyy");
					break;
				default :
					Browser.getLogger().info("Date format undefined");
					fail("UnSupported Date Format");
					return "UnSupported Date Format";
			}

			try {
				LocalDateTime now = LocalDateTime.now();
				return datetimeformat.format(now);
			} catch (DateTimeException e) {
				fail( "Unable to fetch system date");

			}
			return "Error";

		}

		// @Filepath - the path from upload file path. if the file is stored in the
		// upload filepath,
		// give only the filename with extension
		@Step("Upload the file : {1} to the link : {0}")
		public void uploadFile(WebElement uploadLink, String filePath,
				WebDriver driver) {
			try {
				uploadLink.sendKeys(Browser.uploadPath + filePath);
				Browser.getLogger().info("File Upload successfull : " + filePath);
			} catch (Exception e1) {
				
					Browser.getLogger().fatal("Upload failed : " + filePath);
				fail( "Upload file - failed");
				
			}
		}

		// @author : Renjith Rajan
		// use this function to create a temperory copy of file (for file uploads)
		public String CreateCopy_ofFile(String MasterFileName) {
			// take a copy of the file
			File srcFile = new File(Browser.uploadPath + MasterFileName);
			String tempFileName = MasterFileName.split("\\.")[0] + "_Temp."
					+ MasterFileName.split("\\.")[1];
			File destFile = new File(Browser.uploadPath + tempFileName);
			try {
				copyFileUsingStream(srcFile, destFile);
				Browser.getLogger()
						.info("Created TEMP copy of the file : " + MasterFileName);
			} catch (IOException e1) {
				Browser.getLogger()
						.info("Error copying the file : " + MasterFileName);
				fail( "Error while creating copy of file template");
			}
			return tempFileName;
		}

		// @Author : Renjith Rajan
		// To replace text in a text file(csv or text file)
		// @tempFileName : just the file name and the file should be placed in the
		// upload path
		public void ReplaceText(String tempFileName, String SearchString,
				String replaceString) {
			String destinationFileName = Browser.uploadPath + tempFileName;
			try {
				FileReader fr = new FileReader(destinationFileName);
				String s;
				String totalStr = "";
				try (BufferedReader br = new BufferedReader(fr)) {

					while ((s = br.readLine()) != null) {
						totalStr += s + System.getProperty("line.separator");
					}
					totalStr = totalStr.replaceAll(SearchString, replaceString);
					FileWriter fw = new FileWriter(destinationFileName);
					fw.write(totalStr);
					fw.close();
				}
			} catch (Exception e) {
				Browser.getLogger()
						.info("Error while replacing text in the file template : "
								+ destinationFileName);
				fail( "Error while creating file template");
			}
		}

		
		@Attachment(value = "{0}")
		public String Attach_TextFileToResult(String tempFileName) {
			String destinationFileName = Browser.uploadPath + tempFileName;
			try {
				FileReader fr = new FileReader(destinationFileName);
				String s;
				String totalStr = "";
				try (BufferedReader br = new BufferedReader(fr)) {

					while ((s = br.readLine()) != null) {
						totalStr += s + System.getProperty("line.separator");
					}
				}
				return totalStr;
			} catch (Exception e) {
				Browser.getLogger()
						.info("Error while attaching the text file : "
								+ tempFileName);
				fail( "Error while attaching the text file");
				return "error";
			}
		}
		
		
		
		
		// author : Renjith Rajan
		// to be used to delete the temperory file
		// FileNameToDelete - full file path
		public void deleteFile(String FileNameToDelete) {
			File file = new File(FileNameToDelete);

			try {
				Files.deleteIfExists(Paths.get(FileNameToDelete));
			} catch (NoSuchFileException e) {
				Browser.getLogger().info("No Such File : " + FileNameToDelete);
				fail( "No Such File : " + FileNameToDelete);
			} catch (IOException e) {
				Browser.getLogger().info(
						"No permission to delete the file : " + FileNameToDelete);
				fail("No permission to delete the file : " + FileNameToDelete);
			}

			Browser.getLogger()
					.info("Successfully deleted the file : " + FileNameToDelete);

		}

		private void copyFileUsingStream(File source, File dest)
				throws IOException {
			InputStream is = null;
			OutputStream os = null;
			try {
				is = new FileInputStream(source);
				os = new FileOutputStream(dest);
				byte[] buffer = new byte[1024];
				int length;
				while ((length = is.read(buffer)) > 0) {
					os.write(buffer, 0, length);
				}
			} finally {
				is.close();
				os.close();
			}
		}

		// @Step("Download file and verify that the file is downloaded successfully.
		// Download link : {0}")
		public void downloadFile(String downloadedFileName) {
			wait_ForSeconds(30); // wait untill the file is downloaded
			File file = new File(Browser.downloadPath + downloadedFileName);
			if (file.exists()) {
				Browser.getLogger().info("File Downloaded successfully"
						+ Browser.downloadPath + downloadedFileName);
				//GeneralReusableFunctions.Record_TextData("File Downloaded Successfully", Browser.downloadPath + downloadedFileName);
			} else {
				Browser.getLogger().info("File not downloaded" + Browser.downloadPath
						+ downloadedFileName);
				fail( "The file download failed");
			}
		}

		public String generateRandomNumber_FromTimeStamp() {
			wait_ForSeconds(1);
			return (new SimpleDateFormat("yyMMddHHmmss")
					.format(new java.util.Date())); // 12 digits
		}

	public String generateRandomNumber_FromDate() {
		wait_ForSeconds(1);
		return (new SimpleDateFormat("yyMMdd")
				.format(new java.util.Date())); // 12 digits
	}

		public static String generateTimeStamp() {
			return (new SimpleDateFormat("yy/MM/dd/HH:mm:ss")
					.format(new java.util.Date())); // 12 digits
		}

		// @Step("Verify: selected value in the dropdown is :{1} for : {0}")
		public void Verify_Select_DefaultSelection(WebElement wElement,
				String textToVerify, WebDriver driver, String elementInfo) {
			try {
				Select sElement = new Select(wElement);
				if (sElement.getFirstSelectedOption().getText().trim().equalsIgnoreCase(textToVerify)) 
				{
					Browser.getLogger().info("Select validation success : " + sElement.getFirstSelectedOption().getText().trim());
					return;
				} 
				
				else {
					Browser.getLogger().info("Select comparison failed for : "
									+ wElement + " Expected value :"
									+ textToVerify + " Actual :"
									+ sElement.getFirstSelectedOption().getText());
					fail( "Select comparison failed for : "
									+ wElement + " Expected value :"
									+ textToVerify + " Actual :"
									+ sElement.getFirstSelectedOption().getText());
				}
			} catch (NoSuchElementException e) {
				Browser.getLogger().info("The Select not available/displayed : "
						+ elementInfo);
				fail( "The Select not available/displayed : " +elementInfo);
			}

		}
		
		public boolean Verify_Select_DefaultSelection(WebElement wElement,
				String textToVerify, WebDriver driver) {
			try {
				Select sElement = new Select(wElement);
				if (sElement.getFirstSelectedOption().getText().trim().equalsIgnoreCase(textToVerify)) 
				{
					Browser.getLogger().info("Select validation success : " + sElement.getFirstSelectedOption().getText().trim());
					return true;
				} 
				
				else {
					Browser.getLogger().info("Select comparison failed for : "
									+ wElement + " Expected value :"
									+ textToVerify + " Actual :"
									+ sElement.getFirstSelectedOption().getText());
					return false;
				}
			} catch (NoSuchElementException e) {
				Browser.getLogger().info("The Select not available/displayed");
				return false;
			}

		}
		
		
		
		public String  Fetch_Select_DefaultSelection(WebElement wElement,
				WebDriver driver, String elementInfo) {
			try {
				Select sElement = new Select(wElement);
				return sElement.getFirstSelectedOption().getText();
			} catch (NoSuchElementException e) {
				Browser.getLogger().info("The Select not available/displayed : "
						+ wElement);
				fail( "The Select not available/displayed : "
						+ wElement);
				return "Not Found";
			}

		}

		public void Reset_ImplicitWaitTime(WebDriver driver) {
			driver.manage().timeouts().implicitlyWait(Constants.default_Timeout,
					TimeUnit.SECONDS);
			Browser.getLogger().info("Implict wait reset back to : " + Constants.default_Timeout);
		}

		public void ClickElement_JSript(WebDriver driver,
				String myelementXpath) {
			WebElement myelement = driver.findElement(By.xpath(myelementXpath));
			JavascriptExecutor js = (JavascriptExecutor) driver;
			try {
				js.executeScript("arguments[0].scrollIntoView()", myelement);
				js.executeScript("arguments[0].click();", myelement);
				Browser.getLogger()
						.info("Clicked using jScript : " + myelement);
			} catch (NoSuchElementException e) {
				Browser.getLogger().info(
						"Element to click doesn't exist : " + myelement);
				fail("Element to click doesn't exist : " + myelement);
			}
			catch (org.openqa.selenium.StaleElementReferenceException e1) {
				ClickElement_JSript(driver,myelementXpath);
			}
		}
		
		public boolean waitUntil_ObjectLoads(WebDriver driver,
				WebElement wElement, int waitime, String elementInfo) {
			if(waitime > 20)
			{
				waitime = 5;
			}
			for(int i=0;i<waitime;i++)
			{
				if(waitUntil_ObjectLoads_Sub(driver,wElement,waitime,elementInfo))
				{
					return true;
				}
			}
			return false;
		}
		
		public boolean waitUntil_ObjectLoads_Sub(WebDriver driver,
				WebElement wElement, int waitime, String elementInfo) {
			WebDriverWait wait = new WebDriverWait(driver, waitime);
			try {
				wait.until(ExpectedConditions.visibilityOf(wElement));
				if(wElement.isDisplayed())
				{
					Browser.getLogger().info("Element found :" +elementInfo);
					return true;
				}
				else
				{
					Browser.getLogger().info("Element found but hidden. Scrolling....! :" +elementInfo);
					scroll_to_view_WebElement(driver, wElement, elementInfo);
				}
				return true;
			} catch (TimeoutException e) {
				Browser.getLogger().info("Element not found (Timeout):" + elementInfo);
				return false;
			} catch (NoSuchElementException e) {
				Browser.getLogger().info("Element not found (No Such Element):" +elementInfo);
				return false;
			}
			catch(StaleElementReferenceException d)
			{
				return false;
			}
			catch(WebDriverException f)
			{
				Browser.getLogger().info("Element not found (WebDriver Exception - Appium):"  + elementInfo + "... Wait...!");
				wait_ForSeconds(Constants.default_Timeout);
				return false;
			}
		}
		
		
		public void waitUntil_ObjectLoads(WebDriver driver,
				String wElementXpath, int waitime)
		{
			int i=0;
			while(i < waitime)
			{
				if(waitUntil_ObjectLoads_Sub(driver, wElementXpath, waitime))
				{
					Browser.getLogger().info("Element found/loaded : " + wElementXpath);
					return;
				}
				i++;
			}
			
			Browser.getLogger().info("Element not found/loaded : " + wElementXpath);
		}
		
		
		public boolean waitUntil_ObjectLoads_Sub(WebDriver driver,
				String wElementXpath, int waitime) {
			WebElement wElement = null;
			WebDriverWait wait = new WebDriverWait(driver, waitime);
			try {
				wElement = driver.findElement(By.xpath(wElementXpath));
				wait.until(ExpectedConditions.visibilityOf(wElement));
				return true;
			} catch (TimeoutException e) {
				Browser.getLogger().info("Element not found:" + wElementXpath);
				wait_ForSeconds(Constants.default_Timeout);
				return false;
			} catch (NoSuchElementException e) {
				Browser.getLogger().info("Element not found:" + wElementXpath);
				wait_ForSeconds(Constants.default_Timeout);
				return false;
			}
			catch(StaleElementReferenceException d)
			{
				Browser.getLogger().info(
						"Element is stale..! Reinitialising..!"
								+ " : " + wElementXpath);
				waitUntil_ObjectLoads_Sub(driver,wElementXpath,waitime);
				return false;
			}
			catch(WebDriverException f)
			{
				f.printStackTrace();
				Browser.getLogger().info("Element not found (WebDriver Exception - Appium):" + wElementXpath + "... Wait...!");
				wait_ForSeconds(Constants.default_Timeout);
				return false;
			}
		}
		
		public void waitUntil_FrameisAvailable_andSwitch(WebDriver driver,
				WebElement wElement, int waitime, String elementInfo) {
			try {
				waitUntil_ObjectLoads(driver, wElement, waitime,elementInfo);
				wait_ForSeconds(5);
				driver.switchTo().frame(wElement);
				Browser.getLogger().info("Switched frame successfully...");
				wait_ForSeconds(5);
				// wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(wElement));
			} catch (NoSuchFrameException e) {
				fail("Unable to switch to the frame : " + elementInfo);
			} catch (TimeoutException e) {
				fail("Unable to switch to the frame : " + elementInfo);
			}
			catch(StaleElementReferenceException e)
			{
				wait_ForSeconds(5);
				Browser.getLogger().info(
						"Element is stale..! Reinitialising..!"
								+ " : " + wElement.toString());
				driver.switchTo().defaultContent();
				WebElement tempElement = reinitialise_WebElement(wElement,driver);
				waitUntil_FrameisAvailable_andSwitch(driver,tempElement,waitime,elementInfo);
			}
		}
		
		public String get_Cookie_Value(WebDriver driver, String cookieName) {
			try {
				return driver.manage().getCookieNamed(cookieName).getValue();
			} catch (NoSuchCookieException e) {
				return "No Cookie available";
			}
		}
		
		/*
		 * @Purpose: To wait until an object is not present.
		 */
		public boolean waitUntil_ObjectDisappears(WebDriver driver,
				WebElement wElement, String ElementInfo) {
			
			int noOfAttempts = 0;
			
			try {
				while (wElement.isDisplayed() && noOfAttempts < 30)
				{
					Browser.getLogger().info("Waiting for element to disappear : " + ElementInfo + "   wait time : " + Constants.default_Timeout);
					wait_ForSeconds(Constants.default_Timeout);
					noOfAttempts++;
					
				}
				Browser.getLogger().info("Element not disappeared : " + ElementInfo);
				return false;
			} 
			
			catch (NoSuchElementException e) 
			{
				Browser.getLogger().info("Element disappeared : " + ElementInfo);
				return true;
			}
			catch (WebDriverException e) 
			{
				Browser.getLogger().info("Element disappeared : " + ElementInfo);
				return true;
			}
			
		}
		
		public boolean waitUntil_ObjectDisappears(WebDriver driver,
				String wElementXpath, String ElementInfo) 
		{
			int i=0;
			while(Verify_Object_IsDisplayed(wElementXpath, driver) && i < 2)
			{
				Browser.getLogger().info("Waiting for the element to disappear : " + ElementInfo + "  waititme : " + Constants.default_Timeout);
				i++;
				wait_ForSeconds(Constants.default_Timeout);
			}
			if(i>=2)
			{
				return false;
			}
			return true;
		}
		
		public  boolean Find_Element_andSwitchFrame(WebDriver driver, String ElementToCheck, int waitime, String elementInfo) {
			waitUntilPageLoads(driver);

				driver.switchTo().defaultContent();

				if (Verify_Object_IsDisplayed(ElementToCheck, driver))
				{
					Browser.getLogger().info("Element found in the default content. Not required to switch frame..   : " + ElementToCheck);
					return true;
				}
				
				

				if (!find_SubFrameAndSwtich(driver, ElementToCheck,elementInfo)) {
					Browser.getLogger().info("Element not found..Unable to switch window   :" + ElementToCheck);
					fail("Element not found..Unable to switch window    :" + ElementToCheck);
					return false;
				}
				return true;
			
		}
		
		public boolean Find_Element_andSwitchFrame(WebDriver driver, String ElementToCheck) {
			waitUntilPageLoads(driver);

				driver.switchTo().defaultContent();

				if (Verify_Object_IsDisplayed(ElementToCheck, driver))
				{
					Browser.getLogger().info("Element found in the default content. Not required to switch frame..   : " + ElementToCheck);
					return true;
				}
				if (!find_SubFrameAndSwtich(driver, ElementToCheck,"Welement")) {
					Browser.getLogger().info("Element not found..Unable to switch window   :" + ElementToCheck);
					return false;
				}
				return true;
			
		}
		
		public void switch_to_Default_Content(WebDriver driver)
		{
			driver.switchTo().defaultContent();
			Browser.getLogger().info("Switched to default content in the web page");
		}
		
		public boolean find_SubFrameAndSwtich(WebDriver driver, String ElementToCheck, String elementInfo)
		{
			WebElement tempElement = null;
			String tempId;
			String tempName;
			int itterator = 0;
			boolean staleElementFlag = false;
			List<WebElement> myFrames;
			boolean switchedFrame = false;

					myFrames = driver.findElements(By.tagName("iframe"));
					Browser.getLogger().info("No Of frames : " + myFrames.size());

					itterator=0;
					while(itterator < myFrames.size())
					{
						staleElementFlag = false;
						
						while(staleElementFlag == false)
							{
									try {
										tempId = myFrames.get(itterator).getAttribute("id");
										tempName = myFrames.get(itterator).getAttribute("name");
										
										if(tempId.equalsIgnoreCase("")|| tempName.equalsIgnoreCase(""))
										{
										tempElement = myFrames.get(itterator);
										}
										else
										{
											tempElement = driver.findElement(By.xpath("//iframe[@id='" + tempId + "' and @name='"
													+ tempName + "']"));
											Browser.getLogger().info("Manipulated xpath : " + "//iframe[@id='" + tempId + "' and @name='"
													+ tempName + "']");
										}
										
										staleElementFlag = true;
									} catch (StaleElementReferenceException e) {
										Browser.getLogger().info("Stale element exception");
										if (switchedFrame == true)
										{
											return true;
										}
										myFrames = driver.findElements(By.tagName("iframe"));
									}
									catch(IndexOutOfBoundsException g)
									{
										itterator = myFrames.size();
										continue;
									}
							}
						
						waitUntil_FrameisAvailable_andSwitch(driver, tempElement, 20,elementInfo);
						Reset_ImplicitWaitTime(driver,0);
									if (Verify_Object_IsDisplayed(ElementToCheck, driver)) {
					
										Browser.getLogger().info("Element found and Switched the frame successfully..");
										switchedFrame = true;
										Reset_ImplicitWaitTime(driver);
										return true;
									}
									else
									{
										switchedFrame = find_SubFrameAndSwtich(driver,ElementToCheck,elementInfo);
									}
									itterator++;
					}


					if (switchedFrame)
					{
						return true;
					}
					driver.switchTo().parentFrame();
					return false;
		}
		
		
	public String retrieve_all_Text_From_Matching_Xpath(String ElementXpath, WebDriver driver)
	{
		int noOfElements = 0;
		try {
			noOfElements = driver.findElements(By.xpath(ElementXpath)).size();
		} catch (NoSuchElementException e) {
			Browser.getLogger().info("No elements found mathching the xpath : " + ElementXpath);
			return "NOT FOUND";
		}
		int i = 1;
		String textToReturn = "";
		while(i <= noOfElements)
		{
			textToReturn = textToReturn + driver.findElement(By.xpath(ElementXpath + "[" + i + "]")).getText().trim();
			i++;
		}
		return textToReturn.trim();
	}

	public String fetch_Field_fromJSON(String fieldName, String JSONText)
	{
		JSONObject jSonResponse = new JSONObject(JSONText);
		if(jSonResponse.getString(fieldName).equalsIgnoreCase("")
				|| jSonResponse.getString(fieldName) == null)
		{
			Assert.fail("no data in the json file for the field : " + fieldName
					+ "    Actual Json text : " + JSONText);
		}
		return jSonResponse.getString(fieldName).trim();
	}

	public void validate_API_Response_Field_DataType(String JSONText,String fieldName, String DataType)
	{
		JSONObject jSonResponse = new JSONObject(JSONText);
		Object retrievedValue = jSonResponse.get(fieldName);
		if(DataType.equalsIgnoreCase("Decimal"))
		{
			DataType = "BigDecimal";
		}
		if(retrievedValue.getClass().getSimpleName().equalsIgnoreCase(DataType))
		{
			Browser.getLogger().info("Data type validated as " + DataType
			+ "  Value : " + retrievedValue.toString());
		}
		else
		{
			if(DataType.equalsIgnoreCase("Integer"))
			{
				if(retrievedValue.getClass().getSimpleName().equalsIgnoreCase("Long"))
				{
					Browser.getLogger().info("Data type validated as " + DataType
							+ "  Value : " + retrievedValue.toString());
					return;
				}
			}
			fail("Data type validation failed for the field : " + fieldName
			+ "  Expected : " + DataType + "  Actual : " + retrievedValue.getClass().getSimpleName());
		}

	}
	public String getFileChecksum(String filePath){

		MessageDigest digest = null;
		try {
			File file = new File(filePath);
			digest = MessageDigest.getInstance("MD5");
			MessageDigest shaDigest = MessageDigest.getInstance("SHA-256");
			//Get file input stream for reading the file content
			FileInputStream fis = new FileInputStream(file);

			//Create byte array to read data in chunks
			byte[] byteArray = new byte[1024];
			int bytesCount = 0;

			//Read file data and update in message digest
			while ((bytesCount = fis.read(byteArray)) != -1) {
				digest.update(byteArray, 0, bytesCount);
			}
			fis.close();
		} catch (NoSuchAlgorithmException e) {
			fail("Unable to find checksum of the file " + filePath
			+ "  (NoSuchAlgorithmException)");
		} catch (IOException e) {
			fail("Unable to find checksum of the file " + filePath
					+ "  (IOException)");
		}

		//Get the hash's bytes
		byte[] bytes = digest.digest();

		//This bytes[] has bytes in decimal format;
		//Convert it to hexadecimal format
		StringBuilder sb = new StringBuilder();
		for(int i=0; i< bytes.length ;i++)
		{
			sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
		}

		//return complete hash
		return sb.toString();
	}
	public String getFileSize(String filePath) {
		File f = new File(filePath);
		long fileSize = f.length();
		return Long.toString(fileSize);
	}
	public String getTimeStamp_yyyymmddThhhhmmssssZ()
	{
		//Date object
		Date date= new Date();
		//getTime() returns current time in milliseconds
		long time = date.getTime();
		//Passed the milliseconds to constructor of Timestamp class
		Timestamp ts = new Timestamp(time);
		// Convert Timestamp to Instant
		Instant instant = ts.toInstant();

		return instant.toString();
	}
	public String  check_file_exist(String file_with_location)
	{

		File file = new File(file_with_location);
		if (file.exists()) {
			Browser.getLogger().info("File Present...!");
			String result = "File Present";
			return result;
		} else {
			Browser.getLogger().info("File not Present...! : " + file_with_location);
			GeneralReusableFunctions.Record_TextData("FileCheck",
					"File not Present...! : " + file_with_location,runtime.scn);
			String result = "File not Present";
			String result1 = result;
			return result1;
		}
	}
	public String Fetch_CurrentSystemAddOrSubtract(String DateFormat,int Day) {

		DateTimeFormatter datetimeformat;
		switch (DateFormat) {
			case "yyyy-MM-dd" :
				datetimeformat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
				break;
			case "dd-MM-yyyy" :
				datetimeformat = DateTimeFormatter.ofPattern("dd-MM-yyyy");
				break;
			case "MM-dd-yyyy" :
				datetimeformat = DateTimeFormatter.ofPattern("MM-dd-yyyy");
				break;
			case "MM-dd-yy" :
				datetimeformat = DateTimeFormatter.ofPattern("MM-dd-yy");
				break;
			case "dd-MM-yy" :
				datetimeformat = DateTimeFormatter.ofPattern("dd-MM-yy");
				break;
			case "yyyy/MM/dd" :
				datetimeformat = DateTimeFormatter.ofPattern("yyyy/MM/dd");
				break;
			case "dd/MM/yyyy" :
				datetimeformat = DateTimeFormatter.ofPattern("dd/MM/yyyy");
				break;
			case "MM/dd/yyyy" :
				datetimeformat = DateTimeFormatter.ofPattern("MM/dd/yyyy");
				break;
			case "MM/dd/yy" :
				datetimeformat = DateTimeFormatter.ofPattern("MM/dd/yy");
				break;
			case "dd/MM/yy" :
				datetimeformat = DateTimeFormatter.ofPattern("dd/MM/yy");
				break;
			default :
				Browser.getLogger().info("Date format undefined");
				fail("UnSupported Date Format");
				return "UnSupported Date Format";
		}

		try {
			LocalDateTime now = LocalDateTime.now();
			//LocalDate today = LocalDate.now();
			return datetimeformat.format(now.plusDays(Day));
		} catch (DateTimeException e) {
			fail( "Unable to fetch system date");

		}
		return "Error";

	}
	public String timing() {
		String time = String.valueOf(java.time.LocalTime.now());
		char c0 = time.charAt(0);
		char c1 = time.charAt(1);
		char c2 = time.charAt(3);
		char c3 = time.charAt(4);
		String tt =String.valueOf(c0+ ""+""+c1+""+c2+""+c3);
		return tt;
	}

}
