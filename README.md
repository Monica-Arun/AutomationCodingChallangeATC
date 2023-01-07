# AutomationCodingChallangeATC
**selenium-cucumber-java**
This repository contains a collection of sample selenium-cucumber-java projects and libraries that demonstrate how to use the tool and develop automation script using the Cucumber (v 3.0.0) BDD framework with Java as programming language. It generate Allure, HTML and JSON reporters as well. 
It also generate screen shots for your tests if you enable it and also generate error shots for your failed test cases as well.
Tests are written in the Cucumber framework using the Gherkin Syntax. 
**
The Page Object Design Pattern**
Within your web app's UI there are areas that your tests interact with.
A Page Object simply models these as objects within the test code. 
This reduces the amount of duplicated code and means that if the UI changes, the fix need only be applied in one place. 
In other wards one of the challenges of writing test automation is keeping your [selectors] ( classes, id's, or xpath' etc.) up to date with the latest version of your code. 
The next challenge is to keep the code you write nice and DRY (Don't Repeat Yourself). 
The page object pattern helps us accomplish this in one solution.
Instead of including our selectors in our step definitions(in cucumber) we instead place them in a .java file where we can manage all these selectors and methods together. 
Your test file should only call the test methods.

You can also place reusable methods or logic inside of these pages and call them from your step java files. 
The page object serves as a layer of abstraction between tests and code. When A test fails, it fails on a individual step. 
That step may call a selector that is no longer valid, but that selector may be used by many other steps.
By having a single source of truth of what the selector is supposed to be, fixing one selector on the page object could repair a number of failing tests that were affected by the same selector.

**Installation (pre-requisites)**
JDK 1.8+ (make sure Java class path is set)
Gradle
IntellliJ
IntelliJ Plugins for
Gradle
Cucumber
Browser driver (make sure you have your desired browser driver and class path is set)
**Framework set up**
Fork / Clone repository from here or download zip and set it up in your local workspace.

**Run Some Sample Tests**
Navigate to the project location in your workspace and open cmd prompt from there,

To run the feature, Use the following command:
gradlew.bat clean test generateReport -Ptest.parallel=false -Dcucumber.filter.tags="@Project1" -Penv=QA

Or it can be run from the IntelliJ also, Run the feature file from the IntelliJ - This will work.

**Reporters**
Once you ran your tests you can generate the various types of reports. This framework selenium-cucumber-java uses several different types of test reporters to communicate pass/failure.
**ALLURE REPORT**
You can open this from project location, project -> allure-report -> index (open this in a firefox)
**CUCUMBER REPORT**
You can open this from the project location, project -> target -> cucumber-reports -> Html
**JSON REPORT**
You can open this from the project location, project -> target -> cucumber-reports -> cucumber.json
