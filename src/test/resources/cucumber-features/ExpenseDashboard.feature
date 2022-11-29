@Expense-Dashboard
Feature: Expense dashboard
  Scenario: a non authenticated user retrieves his expenses
    Given a non authenticated user
    When he sends a request to retrieve his expenses with page number=1, page size=1, sortBy=DATE, sortDirection=DESC, tag filters=null, description filters=null, date lower bound=null, date upper bound=null, checked status=null, paid with credit card=null, credit card statement issued=null, amount lower bound=null and amount upper bound=null
    Then he gets a response with status 302
    And response contains redirect URL "/sso/login"

  Scenario: an authenticated user with insufficient permissions retrieves his expenses
    Given an authenticated user, "Bob", with role "EXPENSE-TRACKER-TEST-USER"
    When he sends a request to retrieve his expenses with page number=1, page size=1, sortBy=DATE, sortDirection=DESC, tag filters=null, description filters=null, date lower bound=null, date upper bound=null, checked status=null, paid with credit card=null, credit card statement issued=null, amount lower bound=null and amount upper bound=null
    Then he gets a response with status 403

  Scenario: an authenticated user with sufficient permissions retrieves his expenses while he hasn't registered any
    Given an authenticated user, "Will", with role "EXPENSE-TRACKER-USER"
    When he sends a request to retrieve his expenses with page number=1, page size=1, sortBy=DATE, sortDirection=DESC, tag filters=null, description filters=null, date lower bound=null, date upper bound=null, checked status=null, paid with credit card=null, credit card statement issued=null, amount lower bound=null and amount upper bound=null
    Then he gets a response with status 200
    And he receives a list of 0 expense

  Scenario: an authenticated user with sufficient permissions retrieves his expenses
    Given an authenticated user, "Alice", with role "EXPENSE-TRACKER-USER"
    And she sends a request to register an expense with 01/09/2021, 649.00, electromenager, aspirateur Dyson v11, true and false
    Given an authenticated user, "Will", with role "EXPENSE-TRACKER-USER"
    And he sends a request to register an expense with 01/05/2021, 3.50, frais_bancaire;Belfius, frais de gestion compte a vue, false and false
    And he sends a request to register an expense with 01/04/2021, 3.50, frais_bancaire;Belfius, frais de gestion compte a vue, false and false
    And he sends a request to register an expense with 15/04/2021, 27.50, courses, Courses chez Carrefour, true and false
    And he sends a request to register an expense with 15/04/2021, 52.50, achat_sport, Decathlon, true and true
    And he sends a request to register an expense with 01/09/2021, 250.00, Cotisation_sport, null, false and false
    When he sends a request to retrieve his expenses with page number=1, page size=2, sortBy=AMOUNT, sortDirection=DESC, tag filters=null, description filters=null, date lower bound=null, date upper bound=null, checked status=null, paid with credit card=null, credit card statement issued=null, amount lower bound=null and amount upper bound=null
    Then he gets a response with status 200
    And he receives a list of 2 expenses
    And he receives a list of expenses containing at index 0 an expense with 01/09/2021, 250.00, Cotisation_sport, null, false, false and null
    And he receives a list of expenses containing at index 1 an expense with 15/04/2021, 52.50, achat_sport, Decathlon, true, true and null
    When he sends a request to retrieve his expenses with page number=2, page size=2, sortBy=AMOUNT, sortDirection=DESC, tag filters=null, description filters=null, date lower bound=null, date upper bound=null, checked status=null, paid with credit card=null, credit card statement issued=null, amount lower bound=null and amount upper bound=null
    Then he gets a response with status 200
    And he receives a list of 2 expenses
    And he receives a list of expenses containing at index 0 an expense with 15/04/2021, 27.50, courses, Courses chez Carrefour, true, false and null
    And he receives a list of expenses containing at index 1 an expense with 01/04/2021, 3.50, Belfius;frais_bancaire, frais de gestion compte a vue, false, false and null

  Scenario: an authenticated user with sufficient permissions retrieves expenses using filters
    Given an authenticated user, "Ada", with role "EXPENSE-TRACKER-USER"
    And she sends a request to register an expense with 01/05/2022, -50.99, meubles, commode Pax Ikea, true and true
    And she sends a request to register an expense with 01/04/2022, -15.99, meubles, accessoires Pax Ikea, true and false
    And she sends a request to register an expense with 20/04/2022, -5, alimentaire, Hot dog et glace Ikea, false and false
    And she sends a request to register an expense with 01/03/2022, -5, alimentaire, Glace gaston, true and true
    And she updates the "checked" status of the last expense created by "Ada", to true
    When she sends a request to retrieve his expenses with page number=1, page size=10, sortBy=DATE, sortDirection=DESC, tag filters=alimentaire, description filters=glace;ston, date lower bound=01/01/1999, date upper bound=30/03/2022, checked status=true, paid with credit card=true, credit card statement issued=true, amount lower bound=null and amount upper bound=null
    Then she receives a list of expenses containing at index 0 an expense with 01/03/2022, -5.00, alimentaire, Glace gaston, true, true and true

  Scenario: an authenticated user with sufficient permissions retrieves expenses using filters with amount
    Given an authenticated user, "Jessica", with role "EXPENSE-TRACKER-USER"
    And she sends a request to register an expense with 01/02/2022, -50.00, sport, maillot basket, false and false
    And she sends a request to register an expense with 12/02/2022, -15.60, transport, stib, false and false
    And she sends a request to register an expense with 12/02/2022, 500, versement, versement liquide, false and false
    When she sends a request to retrieve his expenses with page number=1, page size=10, sortBy=DATE, sortDirection=DESC, tag filters=null, description filters=null, date lower bound=null, date upper bound=null, checked status=null, paid with credit card=null, credit card statement issued=null, amount lower bound=0 and amount upper bound=1000
    Then she receives a list of expenses containing at index 0 an expense with 12/02/2022, 500, versement, versement liquide, null, null and null