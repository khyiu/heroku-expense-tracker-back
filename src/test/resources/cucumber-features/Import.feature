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