Feature: Some sample feature file

  Scenario:
    Given I go to login page
    When I enter "admin" in username field
    And I enter "admin" in password field
    And I click on submit button
    Then I should be logged in
    When I can click on logout
    Then I should be on login page

  Scenario:
    Given I go to login page
    When I enter "support" in username field
    And I enter "support" in password field
    And I click on submit button
    Then I should be logged in
    When I can click on logout
    Then I should be on login page

  Scenario:
    Given I go to login page
    When I enter "user" in username field
    And I enter "user" in password field
    And I click on submit button
    Then I should be logged in
    When I can click on logout
    Then I should be on login page