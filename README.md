## STKlov.

### STKlov Installation

1. Install MongoDB 3.2 (other versions may not work correctly) (skip if you have this already)
2. Install Redis-Server (skip if you do not plan to use Redis, see section "Using Klov Without Redis")
3. Run Klov:

```java
java -jar STKlov.jar
```

#### MongoDB Settings

You can configure your MongoDB environment settings from `application.properties`:

```java
# data.mongodb
spring.data.mongodb.host=localhost
spring.data.mongodb.port=27017
spring.data.mongodb.database=st-klov
```

#### Redis settings

You can configure your Redis server settings from `application.properties`:

```java
# redis, session
spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.session.SessionAutoConfiguration
spring.session.store-type=redis
server.session.timeout=-1
spring.redis.host=localhost
spring.redis.port=6379
```

#### Using Klov without Redis

To use Klov without Redis, simply uncomment this line in `application.properties`:

```
spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.session.SessionAutoConfiguration
```


#### Default admin

```
user:  root
password:  password
```

### Setting up KlovReporter with ExtentReports API

```java
KlovReporter klov = new KlovReporter();

// specify mongoDb connection
klov.initMongoDbConnection("localhost", 27017);

// specify project
// ! you must specify a project, other a "Default project will be used"
klov.setProjectName("Java");

// you must specify a reportName otherwise a default timestamp will be used
klov.setReportName("AppBuild");

// URL of the KLOV server
// you must specify the server URL to ensure all your runtime media is uploaded
// to the server
klov.setKlovUrl("http://localhost");

// finally, attach the reporter:
extent.attachReporter(klov);
```
