@Expenser-CRUD
Feature: Expense retrieval

  Scenario: a non authenticated user retrieves a certain expense
    Given a non authenticated user
    When he sends a request to retrieve any expense
    Then he gets a response with status 302
    And response contains redirect URL "/sso/login"

  Scenario: an authenticated user with insufficient permission retrieves any expense
    Given an authenticated user, "Bob", with role "EXPENSE-TRACKER-TEST-USER"
    When he sends a request to retrieve any expense
    Then he gets a response with status 403