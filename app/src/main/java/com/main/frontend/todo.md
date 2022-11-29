signup process
[x] User enters cellphone and selects acc type
[x] firebase sends back an otp
[x] user enters otp and firebase authorizes user (sends back a token and verificaiton code)
[ ] app sends user type and firebase user details to expressjs endpoint
[ ] expressjs stores this data in a postgres table

sign in process
[ ] User enters cellphone
[ ] Firebase sends otp
[ ] user enters otp and firebase authorizes
[ ] send auth token and user id to nodejs server
[ ] server verifies and sends back user type
[ ] display activities based on user type

General todos:
[ ] Implement warnings / errors when fields left blank or wrong input
[ ] Use resource strings rather than hardcoded strings