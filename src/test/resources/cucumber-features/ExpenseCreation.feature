@Expense-CRUD
Feature: Expense registration

  Scenario: a non authenticated user registers an expense
    Given a non authenticated user
    When he sends a request to register any expense
    Then he gets a response with status 302
    And response contains redirect URL "/sso/login"

  Scenario: an authenticated user with insufficient permission registers an expense
    Given an authenticated user, "Bob", with role "EXPENSE-TRACKER-TEST-USER"
    When he sends a request to register any expense
    Then he gets a response with status 403

  Scenario Outline: an authenticated user with sufficient permission registers an invalid expense
    Given an authenticated user, "John", with role "EXPENSE-TRACKER-USER"
    When he sends a request to register an expense with <date>, <amount>, <tags> and <randomDescriptionLength>
    Then he gets a response with status 400
    Examples:
      | date       | amount   | tags      | randomDescriptionLength |
      | null       | -5.23    | tag1;tag2 | 0                       |
      | 14/12/2014 | null     | tag1;tag2 | 1                       |
      | 14/12/2014 | 1000000  | tag1;tag2 | 10                      |
      | 14/12/2014 | -1000000 | tag1;tag2 | 100                     |
      | 14/12/2014 | -78.00   | null      | 1000                    |
      | 14/12/2014 | -78.00   | ;         | 1024                    |
      | 14/12/2014 | -78.00   | tag1      | 1025                    |

  Scenario Outline: an authenticated user with sufficient permission registers a valid expense
    Given an authenticated user, "John", with role "EXPENSE-TRACKER-USER"
    When he sends a request to register an expense with <date>, <amount>, <tags>, <description>, <paidWithCreditCard> and <creditCardStatementIssued>
    Then he gets a response with status 201
    And he receives the persisted expense with <date>, <amount>, <tags>, <description>, <paidWithCreditCard> and <creditCardStatementIssued>
    Examples:
      | date       | amount | tags            | description                    | paidWithCreditCard | creditCardStatementIssued |
      | 02/01/2021 | -15.00 | transport       | "Tickets STIB 10x"             | true               | false                     |
      | 02/01/2021 | -10.00 | bouffe          | "Glace"                        | false              | false                     |
      | 07/02/2021 | -3.50  | frais_bancaires | "Frais bancaires compte a vue" | false              | false                     |
      | 07/02/2021 | 50000  | lotto           | "Gains lotto"                  | false              | false                     |