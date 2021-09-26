@Expense-CRUD
Feature: Expense edition

  Scenario: a non authenticated user edits an expense
    Given a non authenticated user
    When he sends a request to edit something in the expense with id="c35b6629-c516-42e5-a74f-5f8c5a65b1e9"
    Then he gets a response with status 302
    And response contains redirect URL "/sso/login"

  Scenario: an authenticated user with insufficient permission edits an expense
    Given an authenticated user, "Bob", with role "EXPENSE-TRACKER-TEST-USER"
    When he sends a request to edit something in the expense with id="c35b6629-c516-42e5-a74f-5f8c5a65b1e9"
    Then he gets a response with status 403

  Scenario: an authenticated user with sufficient permission edits a non existing expense
    Given an authenticated user, "John", with role "EXPENSE-TRACKER-USER"
    When he sends a request to edit something in the expense with id="c35b6629-c516-42e5-a74f-5f8c5a65b1e9"
    Then he gets a response with status 404

  Scenario: an authenticated user with sufficient permission edits an expense that belongs to another user
    Given an authenticated user, "John", with role "EXPENSE-TRACKER-USER"
    Given he sends a request to register any expense
    Given an authenticated user, "Bill", with role "EXPENSE-TRACKER-USER"
    When he sends a request to edit the last expense created by "John" with 01/01/2010, 12.34, update_tags, "updated description", true and true
    Then he gets a response with status 403

  Scenario: an authenticated user with sufficient permission edits one of his expense
    Given an authenticated user, "John", with role "EXPENSE-TRACKER-USER"
    Given he sends a request to register any expense
    Given an authenticated user, "John", with role "EXPENSE-TRACKER-USER"
    When he sends a request to edit the last expense created by "John" with 01/01/2010, 12.34, update_tags, "updated description", true and true
    Then he gets a response with status 200
    And he receives the persisted expense with 01/01/2010, 12.34, update_tags, "updated description", true and true

