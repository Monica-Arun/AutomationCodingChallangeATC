package StepDefinitions;

import com.Runtime.utilities.BaseSteps;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;

public class HomePage_Steps extends BaseSteps {

    private final BaseSteps baseSteps;

    public HomePage_Steps(BaseSteps baseSteps) {
        this.baseSteps = baseSteps;
    }

    @Then("Click on the hamburger menu in the top left corner")
    public void Click_HamburgerMenu_TopLeftCorner()
    {
         baseSteps.pages.get_HomePage().Click_HamburgerMenu();
    }
    @And("Scroll down and then Click on the TV, Appliances and Electronics link under Shop by Department section")
    public void scrollDownAndThenClickOnTheTVAppliancesAndElectronicsLinkUnderShopByDepartmentSection() {
        baseSteps.pages.get_HomePage().Click_ShopBy_TvAndElectronics();
    }

    @And("click on Televisions under the Tv, Audio & Cameras sub section")
    public void thenClickOnTelevisionsUnderTheTvAudioCamerasSubSection() {
        baseSteps.pages.get_HomePage().Click_TelevisionsSection();
    }

    @Then("Scroll down and filter the results by Brand ‘Samsung’")
    public void scrollDownAndFilterTheResultsByBrandSamsung() {
        baseSteps.pages.get_HomePage().FilterBySamsungBrand();
    }
}
