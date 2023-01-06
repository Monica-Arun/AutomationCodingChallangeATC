


import org.junit.runner.RunWith;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;

//@RunWith(Cucumber.class)
//@CucumberOptions(features = "src/test/resources/features/", glue = { "StepDefinitions" }, plugin = { "pretty",
//		"html:target/cucumber-reports/Html.html", "io.qameta.allure.cucumber6jvm.AllureCucumber6Jvm",
//
//		"json:target/cucumber-reports/cucumber.json" }, tags = "")

@io.cucumber.junit.platform.engine.Cucumber
public class TestRunner {
}
