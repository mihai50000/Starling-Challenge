# Starling-tech-challenge

## Build

Run: _gradle build_

## Run

With gradle: _gradle bootRun_

With Intellij: run project with class StarlingRoundUpApplication

## Authorization token and Account Holder UID

The authorization token can be found in resources directory inside access_token.properties file

The account holder uid can be found in resources inside application.properties file

## Application API:

* GET /accounts 
    - Returns the accounts owned by the user
* GET /transactions
    - Parameters:
      - accountUid (mandatory)
      - minTransactionTimestamp (optional)
      - maxTransactionTimestamp (optional)
    - Returns the settled transactions from the time window determined by minTimestamp and maxTimestamp
    - The default length of the time window is 1 week. If none of the time bounds is given the request will return all settled transactions from the past week
* GET /roundup
    - Parameters:
      - accountUid (mandatory)
    - Returns the roundUp amount of settled transactions for 1 week
* GET /roundup/savings-goal
    - Parameters:
        - accountUid (mandatory)
    - Returns the roundup savings goal associated with given account
* PUT /roundup
    - Parameters:
      - accountUid (mandatory)
    - Moves the roundUp amount calculated for the past week to the savings goal

## STARLING API:
* https://developer.starlingbank.com/docs