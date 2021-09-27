@Expense-CRUD
Feature: Expense deletion

  Scenario: a non authenticated user deletes an expense
    Given a non authenticated user
    When he sends a request to delete the expense with id="c35b6629-c516-42e5-a74f-5f8c5a65b1e9"
    Then he gets a response with status 302
    And response contains redirect URL "/sso/login"

  Scenario: an authenticated user with insufficient permission deletes an expense
    Given an authenticated user, "Bob", with role "EXPENSE-TRACKER-TEST-USER"
    When he sends a request to delete the expense with id="c35b6629-c516-42e5-a74f-5f8c5a65b1e9"
    Then he gets a response with status 403

  Scenario: an authenticated user with sufficient permission deletes a non existing expense
    Given an authenticated user, "John", with role "EXPENSE-TRACKER-USER"
    When he sends a request to delete the expense with id="c35b6629-c516-42e5-a74f-5f8c5a65b1e9"
    Then he gets a response with status 404
    
  Scenario: an authenticated user with sufficient permission deletes an expense that belongs to an other user
    Given an authenticated user, "Will", with role "EXPENSE-TRACKER-USER"
    Given he sends a request to register an expense with 09/09/2021, 1.20, tag1, "description", false and false
    Given an authenticated user, "Jim", with role "EXPENSE-TRACKER-USER"
    When he sends a request to delete the last expense created by "Will"
    Then he gets a response with status 403

  Scenario: an authenticated user with sufficient permission deletes one of its own expenses
    Given an authenticated user, "Millie", with role "EXPENSE-TRACKER-USER"
    Given he sends a request to register an expense with 09/09/2021, 1.20, tag1, "description", false and false
    When he sends a request to delete the last expense created by "Millie"
    Then he gets a response with status 200
    When he sends a request to retrieve the last expense created by "Millie"
    Then he gets a response with status 404