package com.Base.Utilities;

import java.io.*;
import java.nio.charset.Charset;
import java.util.Properties;

import org.assertj.core.api.Assertions;
import org.junit.Assert;
import org.openqa.selenium.WebDriver;

public class Configurations {

	WebDriver driver;
	Properties testProperties = null;
	Properties GlobaltestProperties = null;
	String propertyFile;
	String Country;
	String Environment;
	
	public static final String ConfigPath = System.getProperty("user.dir") + File.separator + "src" + File.separator
			+ "test" + File.separator + "resources" + File.separator + "Configuration" + File.separator;
	
	public Configurations(WebDriver driver, String Environment) {
		this.driver = driver;
		this.Environment = Environment;
	}
	public Configurations(WebDriver driver) {
		this.driver = driver;
		this.Environment = Browser.get_environment();
	}

	public Properties loadProperties()
	{
		if(testProperties!= null)
		{
			return testProperties;
		}
		else
		{
			testProperties = new Properties();
		}
		propertyFile = ConfigPath + Environment + ".properties";
		
		try {
			FileInputStream testPropertiesFile = new FileInputStream(new File(propertyFile));		
			testProperties.load(new InputStreamReader(testPropertiesFile, Charset.forName("UTF-8")));
			testPropertiesFile.close();
		} catch (FileNotFoundException e) {
			throw new RuntimeException("Check if file exists " + propertyFile);
		} catch (NullPointerException e) {
			throw new RuntimeException("Check if file exists " + propertyFile);
		} catch (IOException e) {
			throw new RuntimeException("Error reading property file " + propertyFile);
		}

		return testProperties;
	}

	public void Save_Property_Env(String propertyName, String PropertyVaue)
	{
		Properties prop = loadProperties();
		prop.setProperty(propertyName,PropertyVaue);
		try {
			prop.store(new FileOutputStream(ConfigPath + Environment + ".properties"),
					"Store properties");
		} catch (IOException ex) {
			Assert.fail("saving to properties file has failed...!");
		}
	}
	
	public Properties loadGlobalData()
	{
		
		propertyFile = ConfigPath + "GlobalData.properties";

		if(GlobaltestProperties!= null)
		{
			return GlobaltestProperties;
		}
		else
		{
			GlobaltestProperties = new Properties();
		}
		try {
			FileInputStream testPropertiesFile = new FileInputStream(propertyFile);		
			GlobaltestProperties.load(new InputStreamReader(testPropertiesFile, Charset.forName("UTF-8")));
			testPropertiesFile.close();
		} catch (FileNotFoundException e) {
			throw new RuntimeException("Check if file exists " + propertyFile);
		} catch (NullPointerException e) {
			throw new RuntimeException("Check if file exists " + propertyFile);
		} catch (IOException e) {
			throw new RuntimeException("Error reading property file " + propertyFile);
		}

		return GlobaltestProperties;
	}
	
	public String GLOBAL_get_Data(String KeyText)
	{
		Browser.getLogger().info("Retrieved : " + KeyText + " Value : " + loadGlobalData().getProperty(KeyText));
		return loadGlobalData().getProperty(KeyText);
	}
	
	public String ENV_get_Data(String KeyText)
	{
		try {
			return loadProperties().getProperty(KeyText);
		} catch (NullPointerException e) {
			Assertions.fail("Could not retrieve field :" + KeyText + ". The field not found.!");
		}
		return null;
	}
	
}
