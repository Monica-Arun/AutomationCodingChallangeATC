package test.pages;

import com.Base.Utilities.Browser;
import com.Base.Utilities.GeneralReusableFunctions;
import com.Runtime.utilities.PageObjectManager;
import com.Runtime.utilities.RuntimeEnvironment;
import io.cucumber.java.Scenario;
import io.qameta.allure.Step;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;
import org.openqa.selenium.support.PageFactory;

import java.util.ArrayList;
import java.util.Set;

public class ElectronicsPage {
    private WebDriver driver;
    private PageObjectManager pages;
    private GeneralReusableFunctions generalReusableFunctions;
    Scenario scn;
    RuntimeEnvironment runtime;

    @FindBy(how= How.XPATH, using ="//span[contains(@class,'dropdown-prompt')]")
    public WebElement SortBy_Dropdown;
    @FindBy(how= How.XPATH, using ="//a[contains(text(),'Price: High to Low')]")
    public WebElement Price_HighToLow;
    @FindBy(how= How.XPATH, using ="(//span[contains(@class,'a-price-whole')])[2]")
    public WebElement SecondHighest_Item;
    @FindBy(how= How.XPATH, using ="//h1[contains(text(),' About this item ')]")
    public WebElement SecondHighestItem_AboutThisItem;

    @Step("Sort the Samsung results with price High to Low")
    public void Sort_By_HighToLow()
    {
        generalReusableFunctions.Verify_Object_IsDisplayed(SortBy_Dropdown,driver,"Electronics Page -> SortBy dropdown");
        generalReusableFunctions.Click_Element(SortBy_Dropdown,driver,"Electronics Page -> SortBy dropdown");
        generalReusableFunctions.Verify_Object_IsDisplayed(Price_HighToLow,driver,"Electronics Page -> SortBy dropdown -> Price: High to Low");
        generalReusableFunctions.Click_Element(Price_HighToLow,driver,"Electronics Page -> -> Price: High to Low");
        generalReusableFunctions.TakeScreenSnap("SortedByPrice",scn,driver);
    }

    @Step("Click on the second highest priced item")
    public void Click_SecondHighest_PricedItem()
    {
        generalReusableFunctions.Verify_Object_IsDisplayed(SecondHighest_Item,driver,"Electronics Page -> -> Price: High to Low -> Second highest priced item");
        Actions newwin = new Actions(driver);
        newwin.keyDown(Keys.SHIFT).click(SecondHighest_Item).keyUp(Keys.SHIFT).build().perform();
        String winHandleBefore = driver.getWindowHandle();
        for(String winHandle : driver.getWindowHandles()){
            driver.switchTo().window(winHandle);
        }
        generalReusableFunctions.TakeScreenSnap("SecondHighest_PricedItem",scn,driver);
    }

    @Step("Assert that “About this item” section is present and log this section text to console or report")
    public void Validate_TextPresent_AboutThisItem()
    {
        generalReusableFunctions.scroll_to_view_WebElement(driver,SecondHighestItem_AboutThisItem,"Second highest priced item -> About this item");
        generalReusableFunctions.Verify_Object_IsDisplayed(SecondHighestItem_AboutThisItem,driver,"Second highest priced item -> About this item");
        GeneralReusableFunctions.Record_TextData("AboutThisItem",SecondHighestItem_AboutThisItem.getText(),scn);
        Browser.getLogger().info(SecondHighestItem_AboutThisItem.getText());

    }


    public void intitialiseObjects() {

        PageFactory.initElements(driver, this);
    }


    public ElectronicsPage(RuntimeEnvironment runtime) {
        this.driver = runtime.driver;
        this.scn = runtime.scn;
        this.pages = runtime.get_Page(runtime);
        this.runtime = runtime;
        this.generalReusableFunctions = new GeneralReusableFunctions(runtime);
        intitialiseObjects();
    }
}
