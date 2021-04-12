# GCash Mini

## Features
- Create Account
- Verify Account
- Authenticate Account
- Add Product
- Purchase a product (Validates product existence and balance of the account buying)
- Transfer money (Validate balance of the account sending the money)
- Get all activities
- Get account
- Get product

## NOTE
1. All request from client (postman) must go through the `api-gateway`.

### Create Account
```json
POST /account
{
    "firstName": "John",
    "lastName": "Doe",
    "email": "jdoe@apper.ph",
    "password": "@pp3r"
}

200 OK
{
  "verificationCode": "123qwe"
}
```

### Verify Account
```json
POST /account/verify
{
  "verificationCode": "qwe123",
  "email": "jdoe@apper.ph"
}

200 OK
```

### Authenticate Account
```json
POST /account/authenticate
{
  "email": "jdoe@apper.ph",
  "password": "@pp3r"
}

200 OK
{
  "accountId": "ACCT123"
}
```

### Get Account
```json
GET /account/ACCT123

200 OK
{
  "firstName": "John",
  "lastName": "Doe",
  "email": "jdoe@apper.ph",
  "balance": 12500.45,
  "loggedIn": True 
}

404 NOT FOUND
```

### Update Account
Balance and password fields are optional.
```json
PATCH /account/ACCT123

200 OK
{
  "balance": 1000.00,
  "password": "New_PassW0rd" 
}

404 NOT FOUND
```

### Add Product
```json
POST /product
{
  "name": "UnliData to the Max",
  "price": 1000.00
}

200 OK
{
  "product_id": "123443re"
}
```

### Purchase Product
```json
POST /purchase
{
  "productId": "1231",
  "accountId": "ACCT123"
}

200 OK
```

### Transfer Money
```json
POST /transfer
{
  "fromAccountId": "ACCT123",
  "toAccountId": "ACCT456",
  "amount": 500.00
}

200 OK
```


### Get Product
```json
GET /product/1234123

200 OK
{
  "name": "UnliData to the Max",
  "price": 1000
}

404 NOT FOUND
```
