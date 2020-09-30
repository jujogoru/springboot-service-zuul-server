# springboot-service-zuul-server

Gateway service that provides dynamic routing, monitoring, resiliency, security, and more.

### Setup

Follow the next commands to generate the server image:

### Generate jar

```bash
$ ./mvnw clean package -DskipTests
```

### Create network (ignore if the network was already created)

```bash
$ docker network create springcloud
```

### Generate image

```bash
$ docker build -t service-zuul-server:v1 .
```

### Start this project without docker-compose

```bash
$ docker run -d -p 8090:8090 --name service-zuul-server --network springcloud service-zuul-server:v1
```
### How to start the whole project

To start the complete project, please take a look at **docker-compose** project and follow the instructions from its README file.