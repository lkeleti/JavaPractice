### Author

Az 'Author' entitás a következő attribútumokkal rendelkezik:
* 'id' (Long): A szerző egyedi azonosítója (automatikus generált).
* 'name' (String): A szerző neve. Az API végponton keresztül (CreateAuthorCommand, UpdateAuthorCommand) megadva nem lehet üres (@NotBlank). Legfeljebb 255 karakter hosszú.
* 'birthYear' (Integer): A szerző születési éve. Az API végponton keresztül megadva kötelező (@NotNull), pozitív (@Positive) és nem lehet jövőbeli (@PastOrPresent). Az adatbázisban lehet NULL, ha nincs megadva.
* 'nationality' (String): A szerző nemzetisége. Az API végponton keresztül megadva nem lehet üres (@NotBlank). Legfeljebb 255 karakter hosszú. Az adatbázisban lehet NULL, ha nincs megadva.
* 'books' (List<Book>): A szerző könyveinek listája. OneToMany kétirányú kapcsolatban áll a Book entitással (a Book entitás author mezője által).

A következő végpontokon érheted el az entitáshoz tartozó műveleteket:

| HTTP metódus | Végpont             | Leírás                          | HTTP Response státusz|
|--------------|---------------------|---------------------------------|:--------------------:|
| GET	       | "/api/authors"	     | Lekérdezi az összes szerzőt     | 200 OK               |
| GET	       | "/api/authors/{id}" | Lekérdez egy szerzőt id alapján |  200 OK              |
| POST	       | "/api/authors"	     | Létrehoz egy új szerzőt         |  201 Created         |
| PUT	       | "/api/authors/{id}" | Módosít egy szerzőt id alapján  |  200 OK              |
| DELETE       | "/api/authors/{id}" | Töröl egy szerzőt id alapján    | 204 No Content       |

Az Author entitás adatai az adatbázisban az authors táblában tárolódnak.

### Book

A 'Book' entitás a következő attribútumokkal rendelkezik:

* 'id' (Long): A könyv egyedi azonosítója (automatikus generált).
* 'isbn' (String): A könyv ISBN száma. Kötelező (@NotBlank), egyedi az adatbázisban. Legfeljebb 255 karakter.
* 'title' (String): A könyv címe. Kötelező (@NotBlank). Legfeljebb 255 karakter.
* 'publicationYear' (Integer): A kiadási év. Csak pozitív szám lehet (@Positive). Az adatbázisban lehet NULL.
* 'author' (Author): A könyv szerzője. Kötelező kapcsolat (nullable = false). ManyToOne kapcsolat az Author entitással.

A következő végpontokon érheted el az entitáshoz tartozó műveleteket:

| HTTP metódus | Végpont              | Leírás                                             | HTTP Response státusz |
|--------------|----------------------|----------------------------------------------------|:---------------------:|
| GET	       | "/api/books"	      | Lekérdezi az összes könyvet	                        |        200 OK         |
| GET	       | "/api/books/{id}"	  | Lekérdez egy könyvet id alapján	                    |        200 OK         |
| POST	       | "/api/books"	      | Létrehoz egy új könyvet (szerző megadása kötelező)	|      201 Created      |
| PUT	       | "/api/books/{id}"	  | Módosít egy könyvet id alapján (szerzőt is)	        |        200 OK         |
| DELETE       | 	"/api/books/{id}  | Töröl egy könyvet id alapján	                    |    204 No Content     |         

A Book entitás adatai az adatbázisban a books táblában tárolódnak.

### Loan

A 'Loan' entitás egy könyv kölcsönzését reprezentálja a következő attribútumokkal:

* 'id' (Long): A kölcsönzés egyedi azonosítója (automatikus generált).
* 'book' (BookDto): A kölcsönzött könyv adatai (DTO formában). A kapcsolat a Book entitással kötelező (ManyToOne).
* 'borrowerName' (String): A kölcsönző neve. Megadása kötelező a kölcsönzéskor (@NotBlank). Legfeljebb 255 karakter.
* 'loanDate' (LocalDate): A kölcsönzés dátuma. Automatikusan beállítódik a létrehozáskor. Kötelező (@NotNull).
* 'dueDate' (LocalDate): A visszahozatali határidő. Automatikusan számítódik a létrehozáskor (pl. 14 nappal későbbre). Kötelező (@NotNull).
* 'returnDate' (LocalDate): A tényleges visszahozatal dátuma. NULL, amíg a könyv ki van kölcsönözve. Akkor állítódik be, amikor a könyvet visszahozzák.

A következő végpontokon érheted el a kölcsönzésekkel kapcsolatos műveleteket:

| HTTP metódus | Végpont                           | Leírás                                        | HTTP Response státusz |
|--------------|-----------------------------------|-----------------------------------------------|:---------------------:|
| POST         | "/api/loans"	                    | Új kölcsönzés indítása (könyv kikölcsönzése)	|     201 Created       |
| PUT	        | "/api/loans/book/{bookId}/return"	| Könyv visszahozatala (bookId alapján)	        |        200 OK         |
| GET	        | "/api/loans"	                    | Lekérdezi az összes kölcsönzést	            |        200 OK         |
| GET	        | "/api/loans/active"	            | Lekérdezi az összes aktív kölcsönzést	        |        200 OK         |
| GET	        | "/api/loans/overdue"	            | Lekérdezi az összes lejárt kölcsönzést	    |        200 OK         |
| GET	        | "/api/loans/history/{bookId}"	    | Lekérdezi egy könyv kölcsönzési előzményeit	|        200 OK         |

A Loan entitás adatai az adatbázisban a loans táblában tárolódnak.

## MariaDb indítása Dockerben (fejlesztéshez)
`docker run -d -e MYSQL_DATABASE=library -e MYSQL_USER=library -e MYSQL_PASSWORD=library -e MYSQL_ALLOW_EMPTY_PASSWORD=yes -p 3306:3306 --name library-mariadb mariadb`