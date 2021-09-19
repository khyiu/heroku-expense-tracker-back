@Expenser-CRUD
Feature: Expense retrieval

  Scenario: a non authenticated user retrieves a certain expense
    Given a non authenticated user
    When he sends a request to retrieve any expense
    Then he gets a response with status 302
    And response contains redirect URL "/sso/login"