# PSS
PSS is a web application that allows employees to manage their business trips.

## Technologies
- Spring Boot 
- Spring Security
- OAuth
- JPA
- H2
- Swagger
- Vaadin 

## Features
- registering new accounts
- logging into an account (also by social media: Google and Facebook)
- adding, editing and deleting delegations
- exporting reports of delegations to PDF format
- accepting or rejecting requests (Admin)
- managing users accounts (Admin)
- editing profile and changing settings

The front-end isn't in any way fancy, because I was more focused on back-end (that's also why I chose Vaadin).

## Demo
The application is available here: https://pss1.herokuapp.com
It's used the free deployment option on [Heroku](https://www.heroku.com), so if the application wasn't used for a longer time, rebuilding will be necessary after a new request was sent. This process takes about **20** seconds.

The database contains some generated data. You can use these initiated users to try and test the application:
| Username | Password | Role |
|:--------:|:--------:|:--------:|
| hubigabi19@gmail.com | AdamJohnson | Admin |
| JohnSmith@gmail.com | JohnSmith | User |


## Usage
Firstly clone this repository and go to the project directory:
```shell
$ git clone https://github.com/hubigabi/pss.git
$ cd pss
```

Run the application using Maven:
```shell
$ mvn spring-boot:run
```
The application will be working on: http://localhost:8080

## Created by
[Hubert Gabryszewski](https://github.com/hubigabi)
