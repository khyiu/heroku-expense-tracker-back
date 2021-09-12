@Expense-CRUD
Feature: Expense registration

  Scenario: a non authenticated user registers an expense
    Given a non authenticated user
    When he sends a request to register any expense
    Then he gets a response with status 302
    And response contains redirect URL "/sso/login"
