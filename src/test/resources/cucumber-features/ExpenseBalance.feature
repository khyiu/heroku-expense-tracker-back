@Expense-Balance
Feature: Expenses balance
  Scenario: a non authenticated user retrieves his expenses balance
    Given a non authenticated user
    When he sends a request to retrieve his expenses balance
    Then he gets a response with status 302
    And response contains redirect URL "/sso/login"

  Scenario: an authenticated user with insufficient permissions retrieves his expenses balance
    Given an authenticated user, "Bob", with role "EXPENSE-TRACKER-TEST-USER"
    When he sends a request to retrieve his expenses balance
    Then he gets a response with status 403

  Scenario: an authenticated user with sufficient permissions retrieves his expenses balance while he hasn't registered any expense
    Given an authenticated user, "Will", with role "EXPENSE-TRACKER-USER"
    When he sends a request to retrieve his expenses balance
    Then he gets a response with status 200
    And he receives a balance equal to 0

  Scenario: an authenticated user with sufficient permissions retrieves his expenses balance
    Given an authenticated user, "Will", with role "EXPENSE-TRACKER-USER"
    And he sends a request to register an expense with 01/05/2021, -3.50, frais_bancaire;Belfius, "frais de gestion compte à vue", false and false
    And he sends a request to register an expense with 01/04/2021, -3.50, frais_bancaire;Belfius, "frais de gestion compte à vue", false and false
    When he sends a request to retrieve his expenses balance
    Then he gets a response with status 200
    And he receives a balance equal to -7.00

  Scenario: an authenticated user with sufficient permissions retrieves his expenses balance while he has registered future expenses
    Given an authenticated user, "Will", with role "EXPENSE-TRACKER-USER"
    And he sends a request to register an expense with 01/05/2021, -3.50, frais_bancaire;Belfius, "frais de gestion compte à vue", false and false
    And he sends a request to register an expense with 01/05/2023, -3.50, frais_bancaire;Belfius, "frais de gestion compte à vue", false and false
    And he sends a request to register an expense with 31/03/2031, 1000, salaire, "salaire mensuel", false and false
    When he sends a request to retrieve his expenses balance
    Then he gets a response with status 200
    And he receives a balance equal to 993.00

