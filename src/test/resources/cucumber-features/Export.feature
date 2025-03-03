@Expense-Export
Feature: Export expenses

  Scenario: a non authenticated user exports his/her expenses
    Given a non authenticated user
    When he downloads his expenses
    Then he gets a response with status 401

  Scenario: an authenticated user with insufficient permissions exports his/her expenses
    Given an authenticated user, "Sarah", with role "EXPENSE-TRACKER-TEST-USER"
    When she downloads her expenses
    Then he gets a response with status 403

  Scenario: an authenticated user with sufficient permissions exports his/her expenses while he/she hasn't registered any
    Given an authenticated user, "Talia", with role "EXPENSE-TRACKER-USER"
    When she downloads her expenses
    Then she gets a response with status 200
    Then she receives an empty export file

  Scenario: an authenticated user with sufficient permissions exports his/her expenses
    Given an authenticated user, "Tara", with role "EXPENSE-TRACKER-USER"
    And she sends a request to register an expense with 01/05/2021, -3.50, frais_bancaire;Belfius, frais de gestion compte a vue, false and false
    When she downloads her expenses
    Then she gets a response with status 200
    Then she receives an export file that contains "01/05/2021;-3.50;Belfius, frais_bancaire;frais de gestion compte a vue;false;false"