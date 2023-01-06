package StepDefinitions;

import com.Runtime.utilities.BaseSteps;
import io.cucumber.java.en.Then;

public class ElectronicsPage_Steps extends BaseSteps {

    private final BaseSteps baseSteps;

    public ElectronicsPage_Steps(BaseSteps baseSteps) {
        this.baseSteps = baseSteps;

    }
    @Then("Sort the Samsung results with price High to Low")
    public void SortBy_Price_HighToLow() {
        baseSteps.pages.get_ElectronicsPage().Sort_By_HighToLow();
    }
    @Then("Click on the second highest priced item")
    public void clickOnTheSecondHighestPricedItem() {
        baseSteps.pages.get_ElectronicsPage().Click_SecondHighest_PricedItem();
    }
    @Then("Assert that “About this item” section is present and log this section text to console or report")
    public void assertThatAboutThisItemSectionIsPresentAndLogThisSectionTextToConsoleReport() {
        baseSteps.pages.get_ElectronicsPage().Validate_TextPresent_AboutThisItem();
    }
}
