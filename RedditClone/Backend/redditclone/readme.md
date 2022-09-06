# Reddit clone application

---

## Post entity

* `postId` (id of the post `Long` identity)
* `postName` (`String` and  `notBlank` 'Post name cannot be empty or Null')
* `url` (`String` and  `Nullable`)
* `description` (`String` and  `Nullable` and `Lob`)
* `voteCount` `integer` )

A következő végpontokon érjük el az entitást:

| HTTP metódus | Végpont                    | Leírás                                              |  HTTP Response státusz  |
|--------------|----------------------------|-----------------------------------------------------|:-----------------------:|
| GET          | `"/api/trains"`            | lekérdezi az összes vonatot                         |           202           |       
| GET          | `"/api/trains/{id}"`       | lekérdez egy vonatot `id` alapján                   |           202           |       
| POST         | `"/api/trains"`            | létrehoz egy vonatot                                |           201           |       
| POST         | `"/api/trains/periodical"` | létrehoz megadott számú periódikusan induló vonatot |           201           |
| PUT          | `"/api/trains/{id}"`       | módosít egy vonatot `id` alapján                    |           202           |       
| DELETE       | `"/api/trains/{id}"`       | `id` alapján kitöröl egy vonatot                    |           204           |       
| DELETE       | `"/api/trains/"`           | az összes vonatot törli                             |           204           |       

---
## Technológiai részletek

Ez egy klasszikus háromrétegű webes alkalmazás, controller, service és repository
réteggel, entitásonként a rétegeknek megfelelően elnevezett osztályokkal. A megvalósítás
Java programnyelven, Spring Boot használatával történt. Az alkalmazás HTTP kéréseket
képes fogadni, ezt a RESTful webszolgáltatások segítségével valósítja meg.
Adattárolásra SQL alapú MariaDB adatbázist használ, melyben a táblákat Flyway hozza létre.
Az adatbáziskezelés Spring Data JPA technológiával történik. A beérkező adatok validálását a
Spring Boot `spring-boot-starter-validation` modulja végzi, az általános hibakezelést pedig
a `problem-spring-web-starter` projekt.
Az alkalmazás tesztelésére WebClient-tel implementált integrációs
tesztek állnak rendelkezésre, a kipróbálásához pedig az `src/test/java` könyvtáron belül
HTTP fájlok, valamint egy részletesen feliratozott Swagger felület. A mellékelt `Dockerfile`
segítségével az alkalmazásból Docker image készíthető.

---

## Swagger felület és Open API link

[Swagger UI](http://localhost:8080/swagger-ui.html)

[Open API](http://localhost:8080/v3/api-docs)

---

## Virtuális hálózat létrehozás
`docker network create --driver bridge redditclone-net`

## MariaDb indítása Dockerben
`docker run -d -e MYSQL_DATABASE=redditclone -e MYSQL_USER=redditclone -e MYSQL_PASSWORD=redditclone -e MYSQL_ALLOW_EMPTY_PASSWORD=yes --network redditclone-net -p 3306:3306 --name redditclone-mariadb mariadb`

## Az alkalmazás buildelése
`mvnw clean package`

`docker build -t redditcloneb .` 

## Az alkalmazás futtatása dockerben MariaDB-vel
`docker run -d -e SPRING_DATASOURCE_URL=jdbc:mariadb://redditclone-mariadb/redditclone --network redditclone-net -p 8080:8080 --name redditcloneb redditcloneb`

openssl genpkey -out springblog.key -algorithm RSA -pkeyopt rsa_keygen_bits:2048
openssl rsa -pubout -in springblog.key -out springblog.crt