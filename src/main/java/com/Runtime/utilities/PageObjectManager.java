package com.Runtime.utilities;


import com.Base.Utilities.GeneralReusableFunctions;
import test.pages.ElectronicsPage;
import test.pages.HomePage;
//import test.pages.*;

public class PageObjectManager {

	private final RuntimeEnvironment runtime;
	private GeneralReusableFunctions generalReusableFunctions;

	public PageObjectManager(RuntimeEnvironment runtime) {
		this.runtime = runtime;
	}

	HomePage homePage;
	ElectronicsPage electronicsPage;

	public HomePage get_HomePage()
	{
		if(homePage==null) {homePage = new HomePage(runtime);return homePage;}
		return homePage;
	}
	public ElectronicsPage get_ElectronicsPage()
	{
		if(electronicsPage==null) {electronicsPage = new ElectronicsPage(runtime);return electronicsPage;}
		return electronicsPage;
	}

}
