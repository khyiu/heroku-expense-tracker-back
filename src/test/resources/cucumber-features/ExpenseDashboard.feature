@Expense-Dashboard
Feature: Expense dashboard
  Scenario: a non authenticated user retrieves his expenses
    Given a non authenticated user
    When he sends a request to retrieve his expenses with page number=1, page size=1, sortBy=DATE, sortDirection=DESC, tag filters=null and description filter=null
    Then he gets a response with status 302
    And response contains redirect URL "/sso/login"

  Scenario: an authenticated user with insufficient permissions retrieves his expenses
    Given an authenticated user, "Bob", with role "EXPENSE-TRACKER-TEST-USER"
    When he sends a request to retrieve his expenses with page number=1, page size=1, sortBy=DATE, sortDirection=DESC, tag filters=null and description filter=null
    Then he gets a response with status 403

  Scenario: an authenticated user with sufficient permissions retrieves his expenses while he hasn't registered any
    Given an authenticated user, "Will", with role "EXPENSE-TRACKER-USER"
    When he sends a request to retrieve his expenses with page number=1, page size=1, sortBy=DATE, sortDirection=DESC, tag filters=null and description filter=null
    Then he gets a response with status 200
    And he receives a list of 0 expense

  Scenario: an authenticated user with sufficient permissions retrieves his expenses
    Given an authenticated user, "Alice", with role "EXPENSE-TRACKER-USER"
    And he sends a request to register an expense with 01/09/2021, 649.00, electromenager, "aspirateur Dyson v11", true and false
    Given an authenticated user, "Will", with role "EXPENSE-TRACKER-USER"
    And he sends a request to register an expense with 01/05/2021, 3.50, frais_bancaire;Belfius, "frais de gestion compte à vue", false and false
    And he sends a request to register an expense with 01/04/2021, 3.50, frais_bancaire;Belfius, "frais de gestion compte à vue", false and false
    And he sends a request to register an expense with 15/04/2021, 27.50, courses, "Courses @ Carrefour", true and false
    And he sends a request to register an expense with 15/04/2021, 52.50, achat_sport, "Decathlon", true and true
    And he sends a request to register an expense with 01/09/2021, 250.00, Cotisation_sport, "null", false and false
    When he sends a request to retrieve his expenses with page number=1, page size=2, sortBy=AMOUNT, sortDirection=DESC, tag filters=null and description filter=null
    Then he gets a response with status 200
    And he receives a list of 2 expenses
    And he receives a list of expenses containing at index 0 an expense with 01/09/2021, 250.00, Cotisation_sport, "null", false and false
    And he receives a list of expenses containing at index 1 an expense with 15/04/2021, 52.50, achat_sport, "Decathlon", true and true
    When he sends a request to retrieve his expenses with page number=2, page size=2, sortBy=AMOUNT, sortDirection=DESC, tag filters=null and description filter=null
    Then he gets a response with status 200
    And he receives a list of 2 expenses
    And he receives a list of expenses containing at index 0 an expense with 15/04/2021, 27.50, courses, "Courses @ Carrefour", true and false
    And he receives a list of expenses containing at index 1 an expense with 01/04/2021, 3.50, Belfius;frais_bancaire, "frais de gestion compte a vue", false and false