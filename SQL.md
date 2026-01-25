# SQL for PostgreSQL
### Useful PostgreSQL commands

```bash
psql -U (username = postgres)  -- open PostgreSQL shell
```

```SQL
\l  \l+  \list         -- list databases
\c spring   \connect    -- connect to database
\q 						-- exit psql 
\c postgres				-- exit to base database
\d              		-- list tables
\d engineers    		-- show table structure
```

## Creating DATABASE
```SQL
CREATE DATABASE university_sql
    WITH
    OWNER = postgres
    TEMPLATE = template1
    ENCODING = 'UTF8'
    LOCALE_PROVIDER = 'libc'
    CONNECTION LIMIT = -1
    IS_TEMPLATE = False;
```
**TEMPLATE**
* `CREATE DATABASE` actually works by copying an existing database.
* `template1` -- standard system database is copied by default.
* `template0`
    * contains the same data as `template1`, but only with the standard objects predefined (“pristine” user database).
    * new encoding and locale settings can be specified

**LOCALE_PROVIDER**
* Specifies the provider to use for the default collation in this database.
    * libc – klasyczny mechanizm systemowy (glibc)
    * icu – ICU (International Components for Unicode)
    * builtin – wbudowany, bardzo ograniczony

**CONNECTION LIMIT**
* How many concurrent(jednoczesnych) connections can be made to this database.
    * -1 - (the default) no limit.
    * 0 - no one can connect
    * 10 - max 10 sessions

**IS_TEMPLATE**
* If true, then this database can be cloned by any user with CREATEDB privileges.
* if false (the default), then only superusers or the owner of the database can clone it.

### Deleting database
* can't rollback operation

```SQL
DROP DATABASE university_sql;
DROP DATABASE university_sql WITH (FORCE);
```

### Creating backup
```SQL
BACKUP DATABASE university_sql
TO DISK = 'D:\backups\testDB.bak'
WITH DIFFERENTIAL;				-- backs up the parts of the DB that have changed since the last full DB backup.

CREATE DATABASE university_backup TEMPLATE university_sql;
```

* PostgreSQL robi backupy narzędziami zewnętrznymi, nie SQL-em.
* Zalecany sposób: **pg_dump**
    * eksportuje (dumpuje) bazę danych
    * generuje tekstowy SQL potrzebny do jej odtworzenia

```bash
pg_dump -U postgres university_sql > testDB.sql
```

## Creating Tables
* Data types referenc: https://www.w3schools.com/sql/sql_datatypes.asp
* PostgreSQL: https://www.geeksforgeeks.org/postgresql/postgresql-data-types/

```SQL
CREATE TABLE Persons (
    person_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    last_name varchar(255) NOT NULL,
    firs_name varchar(255),
    address varchar(255) UNIQUE,
    city varchar(255) DEFAULT 'Warszawa',
	age INTEGER CHECK (age >= 18),
);

CREATE TABLE Orders (
    order_id BIGINT PRIMARY KEY,
    order_number INTEGER,
	order_date DATE DEFAULT GETDATE(),
    person_id BIGINT REFERENCES Persons(person_id)
);
```

### Constraints
* NOT NULL
* UNIQUE
* PRIMARY KEY - A combination of a `NOT NULL` and `UNIQUE`
* FOREIGN KEY - Prevents actions that would destroy links between tables
    * The table with the **FK** is called the **child** table
    * The table with the **PK** is called the referenced or **parent** table.
* CHECK - Ensures that the values in a column satisfies a specific condition
* DEFAULT - Sets a default value for a column if no value is specified
* CREATE INDEX - Used to create and retrieve data from the database very quickly

**Set contraint on multiple columns**
```SQL
CREATE TABLE Persons (
    ID int,
    LastName varchar(255),
    CONSTRAINT UC_Person UNIQUE (ID, LastName)
);
```

### PostgreSQL Foreign Keys

```sql
CREATE TABLE cities (
        name     varchar(80) primary key,
        location point
);

CREATE TABLE weather (
        city      varchar(80) references cities(name),
        temp_lo   int,
        temp_hi   int,
        prcp      real,
        date      date
);

INSERT INTO weather VALUES ('Berkeley', 45, 53, 0.0, '1994-11-28');

ERROR:  insert or update on table "weather" violates foreign key constraint "weather_city_fkey"
DETAIL:  Key (city)=(Berkeley) is not present in table "cities".
```

## Auto increment
* `GENERATED ALWAYS AS IDENTITY`
    * nie ma batch insert
```SQL
CREATE TABLE students (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name TEXT
);
```
### Sequence Strategy
* has batch insert

```SQL
CREATE SEQUENCE [ IF NOT EXISTS ] name
    [ AS data_type ]					-- BIGINT by default. (Valid types are SMALLINT, INTEGER, and BIGINT)
    [ INCREMENT [ BY ] increment ]			-- 1 by default
    [ MINVALUE minvalue | NO MINVALUE ] 		-- minimum value of the data type by deafult
	[ MAXVALUE maxvalue | NO MAXVALUE ]		-- maximum value of the data type by deafult
    [ [ NO ] CYCLE ]					-- NO CYCLE is the default (If NO CYCLE, any calls to nextval after the sequence has reached its max value will return an error)
    [ START [ WITH ] start ]				-- The default starting value is minvalue for ascending sequences and maxvalue for descending ones.
    [ CACHE cache ]					-- 1 by default (specifies how many sequence numbers are to be preallocated and stored in memory for faster access)
    [ OWNED BY { table_name.column_name | NONE } ]	-- OWNED BY NONE by default

-- Example:
CREATE SEQUENCE students_id_seq
	CACHE 50;
	
CREATE TABLE students (
    id BIGINT PRIMARY KEY DEFAULT nextval('students_id_seq'),
    name TEXT NOT NULL,
);
```

## Altering table
* The `ALTER TABLE` statement is used to add, delete, or modify columns in an existing table.

```SQL
ALTER TABLE table_name

ADD column_name datatype;			-- To add a column
DROP COLUMN column_name;			-- To delete a column
RENAME COLUMN old_name TO new_name;		-- To rename a column
ALTER COLUMN column_name datatype;		-- To change the data type of a column
ADD UNIQUE (ID);				-- To create a UNIQUE constraint on the "ID" column
ADD FOREIGN KEY (PersonID) REFERENCES Persons(PersonID);   -- To create a FK constraint on the "PersonID" column 
ADD CHECK (Age>=18);
ALTER COLUMN City SET DEFAULT 'Sandnes';
```

## INSERT INTO

```sql
INSERT INTO weather VALUES ('Berkeley', 45, 53, 0.0, '1994-11-28');

INSERT INTO employee (
    employee_id,
    first_name,
    last_name,
    manager_id
)
VALUES
    (1, 'Sandeep', 'Jain', NULL),
    (7, 'Virat', 'Kohli', 3),
    (8, 'Rohit', 'Sharma', 3);
```

## SELECT

```SQL
SELECT column1, column2, ...  FROM table_name;
SELECT DISTINCT Country FROM Customers;
```
* SELECT DISTINCT	- return only different values
* TOP
* MIN() - returns the smallest value within the selected column
* MAX() - returns the largest value within the selected column
* COUNT() - returns the number of rows in a set
* SUM() - returns the total sum of a numerical column
* AVG() - returns the average value of a numerical column

```sql
SELECT TOP 3 * FROM Customers;
SELECT MIN(Price) FROM Products;
SELECT COUNT(ProductID) FROM Products WHERE Price > 20;
SELECT SUM(Quantity) FROM OrderDetails;
SELECT AVG(Price) FROM Products;
```

**Aliases**  
You can skip `AS` statement
```sql
SELECT CustomerID ID, CustomerName AS Customer
FROM Customers;

SELECT CustomerName, Address + ', ' + PostalCode + ' ' + City + ', ' + Country AS Address
FROM Customers;
```

**Case**  
The `CASE` expression goes through conditions and returns a value when the first condition is met (like an if-then-else statement).
If there is no `ELSE` part and no conditions are true, it returns `NULL`.

```SQL
CASE
    WHEN condition1 THEN result1
    WHEN condition2 THEN result2
    WHEN conditionN THEN resultN
    ELSE result
END;

SELECT OrderID, Quantity,
CASE
    WHEN Quantity > 30 THEN 'The quantity is greater than 30'
    WHEN Quantity = 30 THEN 'The quantity is 30'
    ELSE 'The quantity is under 30'
END AS QuantityText
FROM OrderDetails;

SELECT CustomerName, City, Country
FROM Customers
ORDER BY
(CASE
    WHEN City IS NULL THEN Country
    ELSE City
END);
```

### WHERE
* To filter records

```SQL
SELECT * FROM Customers
WHERE Country = 'Mexico';
WHERE Price BETWEEN 50 AND 60;
WHERE City LIKE 's%';
WHERE City IN ('Paris','London');

WHERE column_name IS [NOT] NULL;
WHERE NOT Country = 'Spain';   /   WHERE CustomerName NOT LIKE 'A%';
```
* `[=, >, <, >=, <=]`
* `<>`   - Not equal. (!=)
* `BETWEEN`	- Between a certain range
* `LIKE`		- Search for a pattern
* `IN`		- To specify multiple possible values for a column
* `NOT`     - The negative result
* `IS NULL` / `IS NOT NULL`

#### HAVING
* The `HAVING` clause was added to SQL because the `WHERE` keyword cannot be used with aggregate functions.

```SQL
SELECT column_name(s) 
FROM table_name
WHERE condition
GROUP BY column_name(s)
HAVING condition
ORDER BY column_name(s);


SELECT COUNT(CustomerID), Country
FROM Customers
GROUP BY Country
HAVING COUNT(CustomerID) > 5;
```

#### AND & OR
* The `WHERE` clause can contain one or many `AND` / `OR` operators.

```sql
SELECT * FROM Customers
WHERE Country = 'Spain' AND CustomerName LIKE 'G%';
```

**Select all Spanish customers that starts with either "G" or "R":**
```sql
SELECT * FROM Customers
WHERE Country = 'Spain' AND (CustomerName LIKE 'G%' OR CustomerName LIKE 'R%');    
```

**Select all customers that either:**
* are from Spain and starts with either "G", or starts with the letter "R":
```sql
SELECT * FROM Customers
WHERE Country = 'Spain' AND CustomerName LIKE 'G%' OR CustomerName LIKE 'R%';
```

##### IN
* Allows you to specify multiple values in a `WHERE` clause, it's shorthand for multiple OR conditions.

**Return all customers from 'Germany', 'France', or 'UK'**  
```sql
SELECT * FROM Customers
WHERE Country IN ('Germany', 'France', 'UK');
```

#### LIKE
* Used in a `WHERE` clause to search for a specified pattern in a column.
    * The percent sign % represents zero, one, or multiple characters
    * The underscore sign _ represents one, single character

```sql
 -- Select all customers that starts with the letter "a":
SELECT * FROM Customers
WHERE CustomerName LIKE 'a%';

 -- Return all customers from a city that starts with 'L' followed by one wildcard character, then 'nd' and then two wildcard characters:
SELECT * FROM Customers
WHERE city LIKE 'L_nd__';
```

#### GROUP by
* Groups rows that have the same values into summary rows, like "find the number of customers in each country".
* Often used with aggregate functions (COUNT(), MAX(), MIN(), SUM(), AVG()) to group the result-set by one or more columns.

```SQL
SELECT COUNT(CustomerID), Country FROM Customers
GROUP BY Country;
```

### ORDER BY
* To sort the result-set in ascending (default) or descending order

```sql
SELECT * FROM table_name
ORDER BY column1, column2, ... ASC|DESC;

ORDER BY Price;
ORDER BY Price DESC;
ORDER BY Country, CustomerName;
ORDER BY Country ASC, CustomerName DESC;
```

### JOIN
**(INNER) `JOIN`: Returns records that have matching values in both tables**
**`LEFT (OUTER) JOIN`: Returns all records from the left table, and the matched records from the right table**
**`RIGHT (OUTER) JOIN`: Returns all records from the right table, and the matched records from the left table**
**`FULL (OUTER) JOIN`: Returns all records when there is a match in either left or right table**

#### INNER JOIN
* `JOIN` and `INNER JOIN` will return the same result.
* Returns only rows with a match in both tables.
  Which means that if you have a product with no CategoryID, or with a CategoryID that is not present in the Categories table,
  that record would not be returned in the result.

```sql
SELECT O.OrderID, O.CustomerName, O.OrderDate FROM Orders O
JOIN Customers C 
ON O.CustomerID = C.CustomerID;

SELECT Orders.OrderID, Customers.CustomerName, Shippers.ShipperName
FROM ((Orders
JOIN Customers ON Orders.CustomerID = Customers.CustomerID)
JOIN Shippers ON Orders.ShipperID = Shippers.ShipperID);
```

* Zwraca tylko te wiersze, które istnieją we wszystkich joinowanych tabelach.
```sql
FROM Person p
JOIN Student s   ON s.id_person = p.id_person
JOIN Professor pr ON pr.id_person = p.id_person
```
**To oznacza:**  
Pokaż tylko osoby, które są JEDNOCZEŚNIE studentem i profesorem

## PostgreSQL implementing Database structure

```sql
CREATE TABLE Country(
	id_country BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
	name VARCHAR(255) NOT NULL
);

CREATE TABLE Address(
	id_address BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
	id_country BIGINT REFERENCES Country(id_country),
	street VARCHAR(255) NOT NULL,
	city VARCHAR(255) NOT NULL
);

CREATE TABLE Person ( 
	id_person BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
	id_address BIGINT REFERENCES Address(id_address),
	last_name VARCHAR(255),
	creation_date DATE DEFAULT CURRENT_DATE,
	modfification_date DATE DEFAULT CURRENT_DATE
);

CREATE TABLE Student(
	id_student BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
	id_person BIGINT REFERENCES Person(id_person),
	student_number INTEGER NOT NULL UNIQUE
);

CREATE TABLE Professor(
	id_perofessor BIGINT PRIMARY KEY GENERATED ALWAWYS AS IDENTITY,
	id_person BIGINT REFERENCES Person(id_person),
	professor_number INTEGER NOT NULL UNIQUE
);

INSERT INTO Country(name) VALUES ('Polska'), ('Anglia'), ('Włochy');

INSERT INTO Address(id_country, street, city) 
VALUES 
(1, 'Street1', 'Kraków'), 
(1, 'Street2', 'Warszawa'), 
(2, 'Street3', 'Londyn');

insert into person(id_address, last_name)
values(1, 'Nowak'), (1, 'Januszkiewicz'), (2, 'Duurubu'), (3, 'Lsfafaf');
```

### Selecting, grouping and filtering
Napisz apytanie które wyświetli liczbę studentów i profesorów grupując po kraju.
Ogranicz wynik do krajów które mają łącznie więcej niż 10 studentów i profesorów.

```sql
select count(s) as student_count, count(pr) as professor_count, c.name as country_name
from Person p 
left join Student s on s.id_person = p.id_person
left join Professor pr on pr.id_person = p.id_person
join Address a on p.id_address = a.id_address
join Country c on a.id_country = c.id_country
group by c.name
having (count(s) + count(pr)) > 10;
```

**Napisz zapytanie co wyświetli listę osób zmodyfikowanych pomiędzy datami:**  
**1 stycznia 2023 a 31 stycznia 2023.**  
**Wynik zapytania powinien zawierać następujące kolumny:**
* nazwisko osoby
* data ostatnie modyfikacji osoby
* numer studenta
* numer profesora
* adres w formacie "miasto, ulica"

```SQL
select p.last_name, p.modification_date, s.student_numebr, pr.professor_number,
 (a.city || ', ' || a.street) as adress
from Person p
left join Student s on p.id_person = s.id_person
left join Professor pr on p.id_person = pr.id_person
join Address a on p.id_address = a.id_address
where p.modification_date BETWEEN '2023-01-01' and '2023-01-31';
```

**Napisz zaptanie które wyświetli osoby dla wybranego kraju (np. "Polska").**  
**Wynik zapytania powinien zawierać:**
* nazwe kraju
* nazwisko osoby
* typ osoby(dopuszczalne wartości: STUDENT, PROFESSOR, UNKNOWN)

```SQL
select c.name, p.last_name,
case
	when s.id_person IS NOT NULL then 'STUDENT'
	when pr.id_person IS NOT NULL then 'PROFESSOR'
	else 'UNKNOWN'
end as person_type
from Person p
left join Student s on p.id_person = s.id_person
left join Professor pr on p.id_person = pr.id_person
join Address a on p.id_address = a.id_address
join Country c on c.id_country = a.id_country
where c.name = 'Polska';
```