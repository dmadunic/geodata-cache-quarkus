# geodataRestQuarkus

This application was generated using JHipster 7.9.1 and JHipster Quarkus 2.0.0-beta.2, you can find documentation and help at [https://www.jhipster.tech/documentation-archive/v7.9.1](https://www.jhipster.tech/documentation-archive/v7.9.1).

### 1. Database setup (optional)
This step needs to be performed only if you have not already done it while preparing [geodataApp][].

If you already do not have it, create a database named: **ag04**.
Connect to database with user that has sufficient privileges and execute:

```sql
CREATE DATABASE ag04;
```

The next step is to create **geodata** user and his corresponding schema.
To do so execute the following sql commands:

```sql
CREATE ROLE geodata NOSUPERUSER NOCREATEDB NOCREATEROLE NOINHERIT LOGIN PASSWORD 'geodatapwd';
GRANT ALL PRIVILEGES ON DATABASE ag04 TO geodata;
```

Disconnect from "default" database, and connect to ag04 database using the same user as in the previous steps.

```sql
CREATE SCHEMA IF NOT EXISTS AUTHORIZATION "geodata";
```

## Development

For **geodata-test-quarkus** application to be run you need to specify one runtime env variable **GR_LIQUIBASE_ENABLED** with value set to `true` or `false`.
This variable controls if **geodata-test-quarkus** liquibase is enabled or not. 

**It should not be enabled if you have already created all database objects and populated them (for example while running [geodataApp][]).**


To start your application first run infinispan server by running (inside root application folder):
```sh
docker-compose -f src/main/docker/infinispan/infinispan.yml
```

Now start the application in the dev profile, run:

```sh
    ./mvnw
```

For further instructions on how to develop with JHipster, have a look at [Using JHipster in development][].

### Using Angular CLI

You can also use [Angular CLI][] to generate some custom client code.

For example, the following command:

    ng generate component my-component

will generate few files:

    create src/main/webapp/app/my-component/my-component.component.html
    create src/main/webapp/app/my-component/my-component.component.ts
    update src/main/webapp/app/app.module.ts

## Building for production

### Packaging as thin jar

To build the final jar and optimize the geodataRestQuarkus application for production, run:

```
./mvnw -Pprod clean package
```

To ensure everything worked, run:

    java -jar target/quarkus-app/*.jar

Refer to [Using JHipster in production][] for more details.

### Packaging as native executable

_Targeting your Operation System_
In order to build a native image locally, your need to have [GraalVM](https://www.graalvm.org/) installed and `GRAALVM_HOME` defined.
You can use the `native` profile as follow to build native executable.

```
./mvnw package -Pnative
```

Keep in mind that the generated native executable is dependent on your Operating System.

_Targeting a container environment_
If you plan to run your application in a container, run:

```
./mvnw package -Dnative -Dquarkus.native.container-build=true -DskipTests
```

It will use a Docker container with GraalVM installed and produce an 64 bit Linux executable.

### Building a docker image

Once you have container executable ready execute:

```
docker build -f src/main/docker/Dockerfile.native -t ag04/geodata-test-quarkus:latest .
```

or (for a smaller image):

```
docker build -f src/main/docker/Dockerfile.native-distroless -t ag04/geodata-test-quarkus:latest .
```



## Testing

To launch your application's tests, run:

    ./mvnw verify

For more information, refer to the [Running tests page][].

## Using Docker to simplify development (optional)

You can use Docker to improve your JHipster development experience. A number of docker-compose configuration are available in the [src/main/docker](src/main/docker) folder to launch required third party services.

For example, to start a postgresql database in a docker container, run:

    docker-compose -f src/main/docker/postgresql.yml up -d

To stop it and remove the container, run:

    docker-compose -f src/main/docker/postgresql.yml down

[jhipster homepage and latest documentation]: https://www.jhipster.tech
[jhipster 7.9.1 archive]: https://www.jhipster.tech/documentation-archive/v7.9.1
[using jhipster in development]: https://www.jhipster.tech/documentation-archive/v7.9.1/development/
[using docker and docker-compose]: https://www.jhipster.tech/documentation-archive/v7.9.1/docker-compose
[using jhipster in production]: https://www.jhipster.tech/documentation-archive/v7.9.1/production/
[running tests page]: https://www.jhipster.tech/documentation-archive/v7.9.1/running-tests/
[code quality page]: https://www.jhipster.tech/documentation-archive/v7.9.1/code-quality/
[setting up continuous integration]: https://www.jhipster.tech/documentation-archive/v7.9.1/setting-up-ci/
[geodataApp]: https://github.com/dmadunic/geodata-app