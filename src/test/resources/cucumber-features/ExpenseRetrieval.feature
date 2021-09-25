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
    
  Scenario: an authenticated user with sufficient permission retrieves a non existing expense
    Given an authenticated user, "John", with role "EXPENSE-TRACKER-USER"
    When he sends a request to retrieve any expense
    Then he gets a response with status 404

  Scenario: an authenticated user with sufficient permission retrieves an expense that belongs to another user
    Given an authenticated user, "John", with role "EXPENSE-TRACKER-USER"
    Given he sends a request to register any expense
    Given an authenticated user, "Dwight", with role "EXPENSE-TRACKER-USER"
    When he sends a request to retrieve the last expense created by "John"
    Then he gets a response with status 403

  Scenario Outline: an authenticated user with sufficient permission retrieves his own expense
    Given an authenticated user, "John", with role "EXPENSE-TRACKER-USER"
    When he sends a request to register an expense with <date>, <amount>, <tags>, <description>, <paidWithCreditCard> and <creditCardStatementIssued>
    And he sends a request to retrieve the last expense created by "John"
    Then he gets a response with status 200
    And he receives the persisted expense with <date>, <amount>, <tags>, <description>, <paidWithCreditCard> and <creditCardStatementIssued>
    Examples:
      | date       | amount | tags            | description                    | paidWithCreditCard | creditCardStatementIssued |
      | 02/01/2021 | -15.00 | transport       | "Tickets STIB 10x"             | true               | false                     |
      | 02/01/2021 | -10.00 | bouffe          | "Glace"                        | false              | false                     |
      | 07/02/2021 | -3.50  | frais_bancaires | "Frais bancaires compte a vue" | false              | false                     |
      | 07/02/2021 | 50000  | lotto           | "Gains lotto"                  | false              | false                     |