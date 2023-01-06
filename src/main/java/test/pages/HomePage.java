package test.pages;

import com.Base.Utilities.GeneralReusableFunctions;
import com.Runtime.utilities.PageObjectManager;
import com.Runtime.utilities.RuntimeEnvironment;
import io.cucumber.java.Scenario;
import io.qameta.allure.Step;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;
import org.openqa.selenium.support.PageFactory;

public class HomePage {
    private WebDriver driver;
    private PageObjectManager pages;
    private GeneralReusableFunctions generalReusableFunctions;
    Scenario scn;
    RuntimeEnvironment runtime;

    @FindBy(how= How.XPATH, using ="//a[contains(@id,'hamburger-menu')]")
    public WebElement HamburgerIcon;
    @FindBy(how= How.XPATH, using ="//div[contains(text(),'TV, Appliances, Electronics')]")
    public WebElement ShopByTVElectronics_section;
    @FindBy(how= How.XPATH, using ="//a[contains(text(),'Televisions')]")
    public WebElement TelevisionsSection;
    @FindBy(how= How.XPATH, using ="//div[contains(@class,'checkbox')]//following::span[contains(text(),'Samsung')]")
    public WebElement SamsungBrand;

    @Step("Click on the hamburger menu in the top left corner")
    public void Click_HamburgerMenu()
    {
        generalReusableFunctions.Verify_Object_IsDisplayed(HamburgerIcon,driver,"Home Page -> Hamburger menu");
        generalReusableFunctions.Click_Element(HamburgerIcon,driver,"Home Page -> Hamburger menu");
        generalReusableFunctions.TakeScreenSnap("HamburgerMenu_Displayed",scn,driver);
    }

    @Step("Scroll down and then Click on the TV, Appliances and Electronics link under Shop by Department section")
    public void Click_ShopBy_TvAndElectronics()
    {
        generalReusableFunctions.scroll_to_view_WebElement(driver,ShopByTVElectronics_section,"Home Page -> Hamburger menu -> Shop by TV and Electronics section");
        generalReusableFunctions.Click_Element(ShopByTVElectronics_section,driver,"Home Page -> Hamburger menu -> Shop by TV and Electronics section");
        generalReusableFunctions.TakeScreenSnap("ShopByTvSection_Displayed",scn,driver);
    }

    @Step("click on Televisions under the Tv, Audio & Cameras sub section")
    public void Click_TelevisionsSection()
    {
        generalReusableFunctions.Verify_Object_IsDisplayed(TelevisionsSection,driver,"Tv, Audio & Cameras -> Televisions");
        generalReusableFunctions.Click_Element(TelevisionsSection,driver,"Tv, Audio & Cameras -> Televisions");
        generalReusableFunctions.TakeScreenSnap("TelevisionsSection_Displayed",scn,driver);
    }

    @Step("Scroll down and filter the results by Brand ‘Samsung’")
    public void FilterBySamsungBrand()
    {
        generalReusableFunctions.scroll_to_view_WebElement(driver,SamsungBrand,"Tv, Audio & Cameras -> Televisions -> Samsung");
        generalReusableFunctions.Click_Element(SamsungBrand,driver,"Tv, Audio & Cameras -> Televisions -> Samsung");
        generalReusableFunctions.waitForPageToLoad(driver);
        generalReusableFunctions.TakeScreenSnap("FilteredBySamsung",scn,driver);
    }
    public void intitialiseObjects() {

        PageFactory.initElements(driver, this);
    }


    public HomePage(RuntimeEnvironment runtime) {
        this.driver = runtime.driver;
        this.scn = runtime.scn;
        this.pages = runtime.get_Page(runtime);
        this.runtime = runtime;
        this.generalReusableFunctions = new GeneralReusableFunctions(runtime);
        intitialiseObjects();
    }
}
