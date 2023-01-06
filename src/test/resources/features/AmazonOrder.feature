Feature: Amazon order

  @Project1
  Scenario Outline: TC001_OrderProduct_Amazon
    Given Web Application launched - Application - "Amazon" : with Browser : "<Browser>", Environment : "<Environment>"
    Then Click on the hamburger menu in the top left corner
    And Scroll down and then Click on the TV, Appliances and Electronics link under Shop by Department section
    And  click on Televisions under the Tv, Audio & Cameras sub section
    Then Scroll down and filter the results by Brand ‘Samsung’
    Then Sort the Samsung results with price High to Low
    Then Click on the second highest priced item
    Then Assert that “About this item” section is present and log this section text to console or report


    Examples:
      | Browser | Environment |
      | Chrome  | QA          |