# ScoreApp
An application for managing a sports tournament. Allows you to enter teams,
then automatically draws groups and pairs teams. 
After all results have been entered, it automatically creates a tournament ladder.
* [Database](#database)
* [User manual](#user-manual)
* [Comments](#comments)
* [Technologies](#technologies)
## Database
he application works correctly under one of the two databases: PostgreSQL or H2. 
If you want to connect to the selected base, you need to uncomment right section in <b>application.properties</b>.
By default, H2 has a blank user and password. PostgresSQL must be configured according to your own database and set up your data.
### User manual
To run the application, first set the appropriate options related to the database, then build the project: <b>ScoreAppApplication.java</b>.
By default, the program runs on Tomcat (http: // localhost: 8080). The application consists of a home page and 3 subpages where we can view tournament statistics and make changes.
1. Home page - information about author.
2. Teams - here you will find information about the teams (names, photos and short descriptions). It also allows you to add your teams, fill in with default values, and also draw groups.
3. Matchups - contains information about all matches, divided into groups and tournament phases. Here you can enter results for prepared matchups. There is also a function that fills the results with random values.
4. Groups - all the results of each group. 
5. Error page - there are implementation of error handling on this page. We have a case here, for example, when a user tries to draw teams, but not all of them have been entered yet.
### Comments
1. I was not able to fully implement the function that would check head to head results in the event of a tie. In the event that the rest of the rules do not determine a winner, the winner is assigned randomly.
2. There is a problem with changing team photos. The picture file is created in the folder, however ... does not want to be displayed. There is probably a problem with the path, 
but I can't find it, and I've spent too much time on this feature.
3. The application has been tested by me, all other functions work as they should. Errors are handled, I do not see any serious problems with the app.
4. All used dependencies can be found in <b>pom.xml</b> file.
### Technologies
1. Java
2. Spring
3. Hibernate
4. Thymeleaf
5. HTML, CSS
6. Bootstrap
