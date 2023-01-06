package com.Runtime.utilities;

import com.Base.Utilities.*;
import org.openqa.selenium.WebDriver;

import io.cucumber.java.Scenario;

public class RuntimeEnvironment {

	public WebDriver driver;
	
	
	public Scenario scn;
	public PageObjectManager pages;
	public String browser;
	public String Environment;
	public String Country;
	public String OS;
	public String OSVersion;
	public String device;
	public String TestCaseName;
	public String runningPlatform;
	public String user;
	public Configurations configurations;
	
	//To store values and to use it across steps

	public RuntimeEnvironment(Scenario scn, String browser, String Environment) {
		// The class is used to package all the needed details on the run time environment


		this.browser = browser;
		this.Environment = Environment;
		this.Country = Country;
		this.driver = Browser.InitialiseWebDriver(browser);
		this.runningPlatform = "Browser : " + browser;
		this.scn = scn;
		this.TestCaseName = Browser.get_ScenarioName(scn);

		//Set the attributes

		this.OS = null;
		this.OSVersion = null;
		this.device = null;
		this.configurations  = new Configurations(driver,Environment);

	}

	public PageObjectManager get_Page(RuntimeEnvironment runtime)
	{
		if(pages == null)
		{
			pages = new PageObjectManager(runtime);
		}
		return pages;
	}

}
