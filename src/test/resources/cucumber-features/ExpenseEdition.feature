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
    And he receives the persisted expense with 01/01/2010, 12.34, update_tags, "updated description", true, true and null

  Scenario: a non authenticated user updates the "checked" status of some expenses
    Given a non authenticated user
    When he updates the "checked" status to true of expenses with ids=c35b6629-c516-42e5-a74f-5f8c5a65b1e9
    Then he gets a response with status 302
    And response contains redirect URL "/sso/login"

  Scenario: an authenticated user with insufficient permission updates the "checked" status of some expenses
    Given an authenticated user, "Bob", with role "EXPENSE-TRACKER-TEST-USER"
    When he updates the "checked" status to true of expenses with ids=c35b6629-c516-42e5-a74f-5f8c5a65b1e9
    Then he gets a response with status 403

  Scenario: an authenticated user with sufficient permission updates the "checked" status of some expenses that don't exist
    Given an authenticated user, "Bob", with role "EXPENSE-TRACKER-USER"
    When he updates the "checked" status to true of expenses with ids=54b3c5dd-6677-410b-aa36-84f4a329b324;a19c110d-9ed9-4d7f-93bd-d1f2d3909738
    Then he gets a response with status 400

  Scenario: an authenticated user with sufficient permission updates the "checked" status of some expenses from another user
    Given an authenticated user, "Tony", with role "EXPENSE-TRACKER-USER"
    Given he sends a request to register an expense with 21/04/2022, -7.50, lunch, "lunch @ work", false and false
    Given an authenticated user, "Katie", with role "EXPENSE-TRACKER-USER"
    When she updates the "checked" status of the last expense created by "Tony", to true
    Then she gets a response with status 403

  Scenario: an authenticated user with sufficient permission updates the "checked" status of some expenses
    Given an authenticated user, "Tony", with role "EXPENSE-TRACKER-USER"
    Given he sends a request to register an expense with 21/04/2022, -7.50, lunch, "lunch @ work", false and false
    When he sends a request to retrieve the last expense created by "Tony"
    Then he receives the persisted expense with 21/04/2022, -7.50, lunch, "lunch @ work", false, false and null
    When he updates the "checked" status of the last expense created by "Tony", to true
    Then he gets a response with status 200
    When he sends a request to retrieve the last expense created by "Tony"
    Then he receives the persisted expense with 21/04/2022, -7.50, lunch, "lunch @ work", false, false and true