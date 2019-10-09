## Money transfer application

This is simple Web application that demonstrate transfer of money between accounts.
It is implemented with minimal usage of frameworks.
There is no authentication and information about accounts is stored in memory.

### Prerequisites

Set `JAVA_HOME` to Java 11 runtime. 

### 1. How to build

    ./gradlew clean build

Unit and integration (http) tests are run during the build. Tests include concurrency testing and demonstrate correctness of application in real world scenarios.

Test report can be found in:

     <project-dir>/build/reports/tests/test/index.html

To run tests with code coverage:

    ./gradlew clean coverage
    
Code coverage report can be found in:

    <project-dir>/build/reports/jacoco/test/html/index.html

### 2. How to run

    ./gradlew run

Application will start listening for http requests on port `7777` on all IPs (`0.0.0.0`).
For example: `http://localhost:7777/v1/accounts`

To specify another port pass it as command line argument:

    ./gradlew run --args 8080

### 3. How to use

Account can be in one of two states: `OPEN` or `CLOSED`.
Initially account is open and permits top up and transfer money operations.
Account can be closed if it has 0 money on it and after that no operations are possible on this account except getting info.

Supported account currencies: `RUB`, `USD`, `EURO`.

Amounts of money are represented as `long` in terms of minimal units, in kopeks for rubles and in cents for dollars and euros (to avoid rounding errors of floating point numbers). 100 euro and 52 cents = 10052.

#### Create new account

    POST /v1/accounts
    Content-Type: application/json; charset=UTF-8

    {"currencyType": "USD"}

Response

    {
        "id": "cw82AeOITdCLMKHxyiH9Cg",
        "currencyType": "RUB",
        "amount": 0,
        "state": "OPEN"
    }

#### Add money (top up)

Replace `${id}` with account id. 

    PUT /v1/accounts/${id}
    Content-Type: application/json; charset=UTF-8

    {"amount":10000}

Response

    {
        "id": "cw82AeOITdCLMKHxyiH9Cg",
        "currencyType": "RUB",
        "amount": 10000,
        "state": "OPEN"
    }

### Get account info

Replace `${id}` with account id.

    GET /v1/accounts/${id}

Response

    {
        "id": "cw82AeOITdCLMKHxyiH9Cg",
        "currencyType": "RUB",
        "amount": 10000,
        "state": "OPEN"
    }

### Transfer money between accounts

Currently transfers supported only between accounts having same currency types.

Replace `${idFrom}` with account id of sender and `${idTo}` with account id of receiver.

    PUT /v1/accounts/${idFrom}/${idTo}
    Content-Type: application/json; charset=UTF-8
 
    {"amount":7000}

Response returns state of sender's account (`$idFrom`)

    {
        "id": "cw82AeOITdCLMKHxyiH9Cg",
        "currencyType": "RUB",
        "amount": 3000,
        "state": "OPEN"
    }

### Close account

Attempt to close account that is already closed results in error.

Replace `${id}` with account id.

    DELETE /v1/accounts/${id}

Response

    {
        "id": "cw82AeOITdCLMKHxyiH9Cg",
        "currencyType": "RUB",
        "amount": 0,
        "state": "CLOSED"
    } 