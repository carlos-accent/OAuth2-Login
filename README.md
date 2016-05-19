# OAuth2-Login

This is a sample appilication that uses Spring security, using Oauth authentifification.

For developing this it has been followed Spring Oauth tutorial: https://spring.io/guides/tutorials/spring-boot-oauth2/ (for Github and Facebook authentification)

It has been aded as weell a Google Oauth athentification, it's important to know that not every platfom supports the same approach and
the ones used in this app are completly different( google, Facebook, github).

In order to adapt Spring tutorial to our case it's only necesary to create a Console Project providign with it the Clients Id and Secret. In google authtication it been followed the same approach but in this case it's been necessary to specify all properties following a basic google authentication.

<-- Next step is to create nes web pages with the secutiry provided by Spring>
