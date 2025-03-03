@Expense-Tag
Feature: Tag handling

  Scenario: a non authenticated user retrieves his tags with no query
    Given a non authenticated user
    When he sends a request to retrieve his tags with query null
    Then he gets a response with status 401

  Scenario: an authenticated user with insufficient permission retrieves his tags
    Given an authenticated user, "Ricky", with role "EXPENSE-TRACKER-TEST-USER"
    When he sends a request to retrieve his tags with query someTag
    Then he gets a response with status 403

  Scenario: an authenticated user with sufficient permission retrieves his tags while he has no registered expense
    Given an authenticated user, "Ricky", with role "EXPENSE-TRACKER-USER"
    When he sends a request to retrieve his tags with query null
    Then he gets a response with status 200
    And he receives a list of tags that contains ;

  Scenario: an authenticated user with sufficient permission retrieves his tags without query
    Given an authenticated user, "Gabriel", with role "EXPENSE-TRACKER-USER"
    When he sends a request to register an expense with 01/03/2022, -5.00, tag1 and 2
    When he sends a request to register an expense with 02/03/2022, -5.00, tag2 and 2
    When he sends a request to register an expense with 03/03/2022, -5.00, tag3 and 2
    When he sends a request to register an expense with 04/03/2022, -5.00, tag4 and 2
    When he sends a request to delete the last expense created by "Gabriel"
    When he sends a request to retrieve his tags with query null
    Then he gets a response with status 200
    And he receives a list of tags that contains tag1;tag2;tag3

  Scenario: an authenticated user with sufficient permission retrieves his tags with a query
    Given an authenticated user, "Gabriel", with role "EXPENSE-TRACKER-USER"
    When he sends a request to register an expense with 01/03/2022, -5.00, tag20 and 2
    When he sends a request to register an expense with 02/03/2022, -5.00, tag212 and 2
    When he sends a request to register an expense with 03/03/2022, -5.00, tag120 and 2
    When he sends a request to register an expense with 04/03/2022, -5.00, tag4 and 2
    When he sends a request to delete the last expense created by "Gabriel"
    When he sends a request to retrieve his tags with query 12
    Then he gets a response with status 200
    And he receives a list of tags that contains tag120;tag212