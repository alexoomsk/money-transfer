money-transfer is a test application that implements RESTful API for money transfers between accounts.

### Start
```sh
java -jar money-transfer.jar [port]
```
_port_ is an optional parameter. By default the application will start on 4567.

### API
*GET*  _/api/account/:number_  get information about account with _:number_

*POST* _/api/transfer_ submit a transfer request like
```json
{
  "fromAccountNumber": 1, 
  "toAccountNumber": 2, 
  "amount": 0.4
}
```

### Default data
There are 3 registered accounts for tests (accountNumber, balance): (1, 100), (2, 200), (3, 300). 

### Testing
Some test cases you can find [here](src/test/java/test/money/ApplicationTest.java)