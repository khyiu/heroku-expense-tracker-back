# heroku-expense-tracker-back
## Description
This is a personal project to explore usage of Heroku. I've previously created a similar project
to explore AWS features but after the free-tier period expired, I figured the cost of simply letting 
the deployed application idle were quite high. Hence, the switch to Heroku with a free hobby plan.

## Journey logs
### 1 Deploying Spring Boot application to Heroku
As deployment would likely be part of a CI/CD process, I've chosen to handle deployment to Heroku using `heroku-maven-plugin`.

Here are the steps I've followed:
1. Sign up on Heroku
2. Create an application named `heroku-expense-tracker-back`
3. Create a Spring Boot application
4. In `application.properties`, inject the `PORT` environment variable to the `server.port` property:
`server.property=${PORT}`. Heroku serves applications behind a reverse-proxy, and at startup of an 
   application, will assign a port to it. While the user is always using the same URL to access a Heroku 
   app, behind the scenes, the port assign to it will likely change every time it is put to sleep and awaken.
5. For local deployment, define an environment variable named `PORT` and gives it the Heroku API key as value.
This key can be found in the user settings in Heroku
6. Add `heroku-maven-plugin` plugin to the `pom.xml` and add the following configuration:
```xml
<configuration>
    <appName>heroku-expense-tracker-back</appName>
    <jdkVersion>13</jdkVersion>
    <processTypes>
    <web>java $JAVA_OPTS -cp target/classes:target/dependency/* be.kuritsu.hetb.HerokuExpenseTrackerBackApplication</web>
    </processTypes>
</configuration>
```