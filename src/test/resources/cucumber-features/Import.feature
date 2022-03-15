@Expense-Import
Feature: Import expenses

  Scenario: a non authenticated user imports some expenses
    Given a non authenticated user
    When he uploads expense import file "import-01.csv"
    Then he gets a response with status 302

  Scenario: an authenticated user with insufficient permissions imports some expenses
    Given an authenticated user, "Bill", with role "EXPENSE-TRACKER-TEST-USER"
    When he uploads expense import file "import-01.csv"
    Then he gets a response with status 401

  Scenario: an authenticated user with sufficient permissions imports some expenses
    Given an authenticated user, "Aaron", with role "EXPENSE-TRACKER-USER"
    When he uploads expense import file "import-01.csv"
    Then he gets a response with status 200
    When he sends a request to retrieve his expenses balance
    Then he receives a balance equal to 757.40
    When he sends a request to retrieve his expenses with page number=1, page size=10, sortBy=DATE, sortDirection=DESC, tag filters=null and description filter=null
    Then he gets a response with status 200
    And he receives a list of 6 expenses
    And he receives a list of expenses containing at index 0 an expense with 01/03/2022, -152.00, alimentaire, "Restaurant japonais", true and true
    And he receives a list of expenses containing at index 1 an expense with 06/02/2022, -36.00, energie, "Facture Engie", false and false
    And he receives a list of expenses containing at index 2 an expense with 05/02/2022, -4.60, alimentaire, "Sandwich", true and false
    And he receives a list of expenses containing at index 3 an expense with 04/02/2022, 1000.00, salaire, "Salaire mensuel hors primes", false and false
    And he receives a list of expenses containing at index 4 an expense with 02/02/2022, -50.00, retrait, "ATM Bruxelles Centrale", false and false
    And he receives a list of expenses containing at index 5 an expense with 01/01/2022, 0.00, admin, "Initialisation", false and false