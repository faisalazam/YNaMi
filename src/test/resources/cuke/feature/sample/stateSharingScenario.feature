Feature: Some sample feature file

  Scenario:
    Given I am about to perform an operation
    When I enter "4" as first operand
    And I enter "2" as second operand
    Then The result of addition operation is "6"

  Scenario:
    Given I am about to perform an operation
    When I enter "5" as first operand
    And I enter "3" as second operand
    Then The result of subtraction operation is "2"

  Scenario:
    Given I am about to perform an operation
    When I enter "6" as first operand
    And I enter "4" as second operand
    Then The result of multiplication operation is "24"