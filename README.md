# Recipe-Shop-App-Version

This is the App version of Recipe Shop, the link to the web version repo can be found [here](https://github.com/njzfjiang/Recipe-Shop/tree/main)
# Recipe-Shop
COMP 4350-A01 Group 9
### Team Members

| Name | Username in Github  |   email |
|------|---------------------|---------|
| Meixuan Chen | njzfjiang   | chenm7@myumanitoba.ca  |
| Densson Zhu  | zhude2 | zhud2@myumanitoba.ca |
| Troy Thomas | TroyT21 | thomast9@myumanitoba.ca |
| Ifeanyi Ochiagha | Francis518 |ochiaghi@myumanitoba.ca|

### Overview

Recipe Shop is an online platform built for anyone who loves to cook or needs a hand in preparing meals. Recipe shop aims to provide convenience in the process of preparing meals and grocery shopping; and allows people of similar interests to find and share their favorite recipes.

### Vision Statement
Recipe Shop is a mobile and web based application that allows users to find recipes based on their specialized needs. The application relieves the user from the pain of having to google for recipes before each meal; and frustration when they find a recipe but did not have enough ingredients at home. With “Recipe Shop” they can simply enter the ingredients they have and have a list of recipes matching the ingredients. The main goal of Recipe Shop is to make the process of preparing meals and grocery shopping easier, and also provides a platform for recipe sharing and collecting.

### Getting Started with the project
# Option 1 Android Studio
1. Clone the repository to your local machine
2. Open the repository in Android Studio
3. Wait for Android Studio to sync project files
4. Go to Device Manager and launch a device (recommend Android version 14.0)
5. Once device is running, run the app by pressing the play button at the top.
6. In the app, set ip to the ip address the server is running on then login or register

# Option 2 Android Device
1. Clone the repository to your local machine
2. Get the .apk file from Recipe-Shop-App-Version/app/build/outputs/apk/debug
3. Copy .apk file to an android device (recommend Android version 14.0)
4. Install the apk on the device
5. Launch the app on the device
6. In the app, set ip to the ip address the server is running on then login or register

### Documentation
* [API Documentation](https://github.com/njzfjiang/Recipe-Shop/blob/main/Documentation/API%20Documentation.md)
* [Architecture and Flow Diagrams](https://github.com/njzfjiang/Recipe-Shop/blob/main/Documentation/Architecture%20and%20Flow%20Diagrams.md)
* [Project proposal](https://github.com/njzfjiang/Recipe-Shop/blob/7f3a1a3495a17daec8fcd5658245c182e3fdc76b/Documentation/Project%20proposal.md)
* [Test Plan](https://github.com/njzfjiang/Recipe-Shop/blob/main/Documentation/Recipe%20Shop%20Test%20Plan.pdf)
* [Meeting Notes](https://github.com/njzfjiang/Recipe-Shop/blob/main/Documentation/Meeting%20Logs.md)
* [Sprint 1 Worksheet](https://github.com/njzfjiang/Recipe-Shop/blob/main/Documentation/Sprint%201%20worksheet.md)
### Key features
1.  Users can create account to view and manage recipes
    
2.  The app suggest recipes based on user's input of ingredients and cooking methods
    
3.  Users can generate grocery list with their chose of recipes
    
4.  Users can search for recipes
    
5.  Registered users can upload original recipes to forum

**Non Functional**: The application can handle 20 Users sending 100 requests per minute.


### Initial Architecture
![Architecture Diagram](https://github.com/njzfjiang/Recipe-Shop/blob/dev/Documentation/images/RenewedArchitecture.JPG)

### Technology
**Front End**: React, Android Studio 

**API**: Express.js, Edamam

**Database**: MongoDB

**Server**: Python, Node.js

**Code Management**: Github

**Android App**: Android Studio
