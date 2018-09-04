Feature: Some sample feature file

  Scenario:
    Given I go to login page
    When I enter "admin" in username field
    And I enter "admin" in password field
    And I click on submit button
    Then I should be logged in
    When I can click on logout
    Then I should be on login page