package com.Runtime.utilities;

import static org.junit.jupiter.api.Assertions.assertTrue;

import com.Base.Utilities.Browser;
import com.Base.Utilities.GeneralReusableFunctions;
import io.cucumber.java.Scenario;


public class BaseSteps {
	
	public Scenario scn;
	public PageObjectManager pages;
	public RuntimeEnvironment runtime;
	public GeneralReusableFunctions generalReusableFunctions;


	public void initialise_Runtime(String browser,
								   String Environment) {

		if(!Browser.get_environment_fromCommandline().equalsIgnoreCase("NOT FOUND"))
		{
			Environment =Browser.get_environment_fromCommandline();
		}
		runtime = new RuntimeEnvironment(scn, browser, Environment);
		this.pages = runtime.get_Page(runtime);
		this.generalReusableFunctions = new GeneralReusableFunctions(runtime);

	}

	public RuntimeEnvironment get_runtime() {
		return runtime;
	}
	public Scenario get_Scenario() {
		return scn;
	}
	
	public PageObjectManager get_Pages() {
		return pages;
	}

	public GeneralReusableFunctions get_GeneralReusableFunction()
	{
		return generalReusableFunctions;
	}


}
