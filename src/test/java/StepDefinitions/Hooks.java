package StepDefinitions;


import static org.junit.jupiter.api.Assertions.fail;

import java.awt.AWTException;
import java.io.IOException;

import io.cucumber.java.en.And;
import org.apache.xmlgraphics.image.loader.impl.imageio.ImageIOUtil;
import org.openqa.selenium.JavascriptExecutor;
import com.Base.Utilities.Browser;
import com.Base.Utilities.GeneralReusableFunctions;
import com.Runtime.utilities.BaseSteps;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;


public class Hooks extends BaseSteps{
	private final BaseSteps baseSteps;
	public Hooks(BaseSteps baseSteps) {
		this.baseSteps = baseSteps;
	}


	@Given("Web Application launched - Application - {string} : with Browser : {string}, Environment : {string}")
	public void WebApplication_Launch(String ApplicationName, String browser,
									  String Environment) {
		//Browser.getLogger().info("Token ID: " +runtime.configurations.ENV_get_Data(Country + "_URL"));
		baseSteps.initialise_Runtime(browser, Environment);
		baseSteps.runtime.driver.get(baseSteps.runtime.configurations.ENV_get_Data(ApplicationName + "_URL"));


	}

	@Before()
	public void setup(Scenario sh) {
		baseSteps.scn = sh;
		String timeStamp = GeneralReusableFunctions.generateTimeStamp();
		GeneralReusableFunctions.Record_TextData("Test Execution Began", timeStamp,baseSteps.scn);
	}
	@After()
	public void closeBrowser() throws IOException, AWTException {

		JavascriptExecutor jse = null;
		try {
			jse = (JavascriptExecutor)baseSteps.runtime.driver;
		} catch (NullPointerException e2) {
			Browser.getLogger().info("runtime/driver is null");
		}
		
		if(baseSteps.runtime == null)
		{
			Browser.getLogger().info("runtime is null");
			return;
		}
		if(baseSteps.runtime.driver == null)
		{
			Browser.getLogger().info("driver is null");
			return;
		}
		if (baseSteps.scn.isFailed()) {
			baseSteps.generalReusableFunctions.TakeScreenSnap("Failed", baseSteps.scn, baseSteps.runtime.driver);
			String timeStamp = GeneralReusableFunctions.generateTimeStamp();
			GeneralReusableFunctions.Record_TextData("Test Execution Ended", timeStamp,baseSteps.scn);
			if(baseSteps.runtime.runningPlatform.equalsIgnoreCase("BrowserStack"))
			jse.executeScript("browserstack_executor: {\"action\": \"setSessionStatus\", \"arguments\": {\"status\": \"Failed\", \"reason\": \"" + baseSteps.runtime.TestCaseName +  "\"}}");
		}
		else
		{
			baseSteps.generalReusableFunctions.TakeScreenSnap("Passed", baseSteps.scn,  baseSteps.runtime.driver);
			String timeStamp = GeneralReusableFunctions.generateTimeStamp();
			GeneralReusableFunctions.Record_TextData("Test Execution Ended", timeStamp,baseSteps.scn);
			if(baseSteps.runtime.runningPlatform.equalsIgnoreCase("BrowserStack"))
				jse.executeScript("browserstack_executor: {\"action\": \"setSessionStatus\", \"arguments\": {\"status\": \"Passed\", \"reason\": \"" + baseSteps.runtime.TestCaseName +  "\"}}");
		}

		try {
			baseSteps.runtime.driver.quit();			
		} catch (NullPointerException e) {
			Browser.getLogger().info("driver is null.....!");
			if(baseSteps.runtime.runningPlatform.equalsIgnoreCase("BrowserStack"))
				jse.executeScript("browserstack_executor: {\"action\": \"setSessionStatus\", \"arguments\": {\"status\": \"Broken\", \"reason\": \"" + baseSteps.runtime.TestCaseName +  "\"}}");
		}
	}

}
