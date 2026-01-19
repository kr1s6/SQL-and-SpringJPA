# Spring Data JPA
Spring Data JPA removes a lot of boilerplate code by providing abstractions on top of JPA and Hibernate.

---

### JPA Annotations
It is recommended to annotate all tables and columns with names for better readability and maintainability.

```java
@Column(name = "example")
@Table(name = "example")
```

## Database-specific strategies for auto-generated values

* **PostgreSQL** → `SEQUENCE` + `@SequenceGenerator`
* **MySQL / MariaDB** → `IDENTITY`


### Identity Strategy (`GenerationType.IDENTITY`)

* Very similar to the `AUTO` strategy
* Generated values are unique only within a given type hierarchy (simpy table)

```java
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;
```

❌ No batch inserts (Worse performance with many inserts)

**Batch insert**:
A database technique for inserting multiple rows using fewer, larger operations instead of individual inserts.  
This significantly improves performance by reducing network overhead and transaction costs.

---

### Sequence Strategy (`GenerationType.SEQUENCE`)

* Has batch insert - IDs are allocated in blocks to reduce database round trips
* Requires the sequence to exist in the database
* UUIDs cannot be used with sequences

> ⚠️ Using a single shared sequence is acceptable only for small projects with no long-term growth expectations.  
> ✅ For scalable systems, **create a separate sequence per entity**.

```java
@Entity
@SequenceGenerator(
    name = "student_seq",          // Alias used by @GeneratedValue
    sequenceName = "student_seq",  // Actual database sequence name
    allocationSize = 50              // Fetch IDs in blocks of 50
)
public class Student {

    @Id
    @GeneratedValue(
        strategy = GenerationType.SEQUENCE,
        generator = "student_seq"
    )
    private long id;
}
```

✔️ Best performance on PostgreSQL  ✔️ Highly scalable

#### Recommended `allocationSize` values

| Environment | allocationSize |
| ----------- | -------------- |
| Dev         | 1              |
| Test        | 10             |
| Prod        | 50–100         |
| High-load   | 100–1000       |

---

## Unique Constraints

Using `@Table(uniqueConstraints = ...)` improves readability and maintainability.

```java
@Table(
    name = "student",
    uniqueConstraints = @UniqueConstraint(
        columnNames = {"email_address", "phone_number"}
    )
)
```

This is equivalent to:

```java
@Column(unique = true)
```

---

## `@Embeddable` and `@Embedded`

Use embeddables when you want to group fields into a reusable component **without creating a separate table or entity**.

```java
@Embeddable
public class Guardian {
    private String name;
    private String email;
    private String mobile;
}
```

```java
@Entity
public class Student {

    @Embedded
    private Guardian guardian;
}
```

* Fields from the embedded class are stored in the same table as the owning entity
* Improves domain modeling and code reuse

---

## JPA `@OneToOne`

### Key characteristics

* The **owning side** contains the foreign key (FK) and `@JoinColumn` annotation
* The other side is only a **mirror** of the relationship (`mappedBy`) and **not** define the FK
* Relationships are **optional by default**, which means you can create a `Course` without a `CourseMaterial`

To make the relationship mandatory:

```java
@OneToOne(optional = false)
```

### Owning side example

```java
@Entity
@Table(name = "course_material")
public class CourseMaterial {

    @Id
    @SequenceGenerator(...)
    @GeneratedValue(...)
    private Long id;

    @OneToOne(cascade = CascadeType.ALL)    // Cascading
    @JoinColumn(
        name = "course_id",              // FK column name
        referencedColumnName = "courseId"       // Target column (by default targets PK)
    )
    private Course course;
}
```

```java
@OneToOne(cascade = CascadeType.ALL)
```

Cascading allows you to save multiple entities with a single save operation.

#### When is `referencedColumnName` required?

Use it when the foreign key references a column **other than the primary key**.

```java
@ManyToOne
@JoinColumn(
    name = "user_email",
    referencedColumnName = "email"
)
private User user;
```

### Inverse side (non-owning side)

```java
@Entity
@Table(name = "course")
public class Course {

    @Id
    @SequenceGenerator(...)
    @GeneratedValue(...)
    private Long courseId;

    @OneToOne(mappedBy = "course")
    private CourseMaterial courseMaterial;
}
```

`mappedBy = "course"` tells JPA:

> "This side is NOT the owner of the relationship. 
> The relationship is managed by the `course` field in `CourseMaterial`."

---

## JPA `@OneToMany` `@ManyToOne` `ManyToMany`
Apparently it's **always better** to make `@ManyToOne` instead `@OneToMany` when possible
* Simpler mapping
* Better performance

### `@ManyToOne`

```java
@Entity
@Table(name = "course")
public class Course {

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(
        name = "teacher_id",
        referencedColumnName = "teacher_id"
    )
    private Teacher teacher;
}
```

### `@OneToMany`

* Using `@OneToMany` on `Teacher` results in a **FK column in the `Course` table**
* You will **not** see courses stored in the `Teacher` table, instead, the `Course` table contains a reference to `Teacher`

```java
@Entity
@Table(name = "teacher")
public class Teacher {

    @Id
    @Column(name = "teacher_id")
    private Long teacherId;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(
        name = "teacher_id_name_for_course", // FK column name in Course table
        referencedColumnName = "teacher_id"  // PK column in Teacher table
    )
    private List<Course> courseList;
}
```

### `@ManyToMany`

* Requires a **join table** to store the relationship
* The join table maps IDs from both sides of the association

```java
@ManyToMany(cascade = CascadeType.ALL)
@JoinTable(
    name = "student_course_map",
    joinColumns = @JoinColumn(
        name = "course_id",            // FK for this entity
        referencedColumnName = "courseId"
    ),
    inverseJoinColumns = @JoinColumn(
        name = "student_id",           // FK for the related entity (Student)
        referencedColumnName = "studentId"
    )
)
private List<Student> students;
```

#### Helper method

```java
public void addStudent(Student student) {
    if (studentList == null) {
        studentList = new ArrayList<>();
    }
    studentList.add(student);
}
```

---

## Fetch Types

* `FetchType.EAGER` – related entity is fetched immediately using a join
* `FetchType.LAZY` – related entity is loaded only when accessed

```java
@OneToOne(fetch = FetchType.LAZY)
```

⚠️ Be careful with `EAGER` fetching — it may cause performance issues and unexpected joins.

---

## Paging & Sorting

### Paging

```
Pageable firstPageWithThreeRecords = PageRequest.of(pageNumber: 0, pageSize: 3);

List<Course> courseList = courseRepository
        .findAll(firstPageWithThreeRecords)
        .getContent();

System.out.println("Courses = " + courseList);

long totalElements = courseRepository
        .findAll(firstPageWithThreeRecords)
        .getTotalElements();

System.out.println("totalElements = " + totalElements);

long totalPages = courseRepository
        .findAll(firstPageWithThreeRecords)
        .getTotalPages();

// totalPages depends on page size and total record count
System.out.println("totalPages = " + totalPages);
```


### Sorting

```java
Pageable sortByTitle = PageRequest.of(0, 2, Sort.by("title"));

Pageable sortByCreditDesc = PageRequest.of(
    0, 2, Sort.by("credit").descending()
);

Pageable sortByTitleAndCreditDesc = PageRequest.of(
    0, 2,
    Sort.by("title").descending().and(Sort.by("credit"))
);

List<Course> courses = courseRepository
        .findAll(sortByTitleAndCreditDesc)
        .getContent();

System.out.println(courses);
```


### Paging + Sorting with Custom Query Method

```java
Page<Course> findByTitleContaining(String title, Pageable pageable);
```

```java
Pageable sortByTitle = PageRequest.of(0, 2, Sort.by("title"));

System.out.println(
    courseRepository
        .findByTitleContaining("kurs", sortByTitle)
        .getContent()
);
```

---

## JPA Repository Interface

By extending Spring Data JPA repository interfaces, you get **CRUD operations** and pagination support out-of-the-box.  
You can also define **custom queries** using method names or the `@Query` annotation.  
[JPA Query Method Reference](https://docs.spring.io/spring-data/jpa/reference/jpa/query-methods.html)

### Automatic Custom Queries

* Spring Data generates queries based on method names automatically.

```java
Optional<User> findByUsername(String username);
Optional<List<Student>> findByFirstName(String name);          // Find records matching the exact name
List<Student> findByGuardianName(String name);                 // Find records in @Embedded class
boolean existsByUsername(String username);
```

### Manual Custom Queries with `@Query`

* Use **JPQL** (class-based query language)

```java
@Query("SELECT f FROM Foo f WHERE LOWER(f.name) = LOWER(:name)")
Foo retrieveByName(@Param("name") String name);

@Query("SELECT s.firstName FROM Student s WHERE s.emailId = ?1") // get only firstName
String getStudentFirstNameByEmailAddress(String emailId);
```

> Notes:
> * `Student` refers to the **entity class**, not the table
> * `?1`, `?2` refer to **method parameters by index**
> * `:name` refer to `@Param` variable

### Native Queries

* Use actual **database table and column names**

```java
@Query(
    value = "SELECT * FROM student s WHERE s.email_address = ?1",
    nativeQuery = true
)
Student getStudentByEmailAddressNative(String emailId);
```

#### Named Parameters in Native Queries

```java
@Query(
    value = "SELECT * FROM student s WHERE s.email_address = :emailId",
    nativeQuery = true
)
Student getStudentByEmailAddressNativeNamedParam(@Param("emailId") String emailId);
```

---

## `@Transactional` and `@Modifying`

* Transactions ensure **all-or-nothing** behavior (rollback on exception
* **Updates/deletes** must be transactional and annotated with `@Modifying`
* Repository methods **do not have transactions by default**


  Let's assume the exception is thrown after succeeding 1) and before executing 2).  
  Now we would have some kind of inconsistency because A lost 100$ while B got nothing.  
  Transactions means all or nothing.  
  If there is an exception thrown somewhere in the method, changes are not persisted in the database.  
  Rollback happens.

```java
@Transactional(readOnly = true)
interface UserRepository extends JpaRepository<User, Long> {

    List<User> findByLastname(String lastname);

    @Modifying
    @Transactional
    @Query("DELETE FROM User u WHERE u.active = false")
    void deleteInactiveUsers();

    @Modifying
    @Transactional  // by default readOnly = false
    @Query(
        value = "UPDATE student SET first_name = :firstName WHERE email_address = :emailId",
        nativeQuery = true
    )
    int updateStudentNameByEmailId(String firstName, String emailId);
}
```

* `readOnly = true` for queries that **only read data**
* `@Modifying` overrides this for update/delete operations

---

## JPA Testing with `@DataJpaTest`

* It's for repository testing which won't impact real database.
* Focused on the **repository layer** without loading the full application context
* Uses **in-memory database** (H2) by default

```java
@DataJpaTest
public class StudentRepositoryTest {

    @Autowired
    StudentRepository studentRepository;

    @Test
    @DisplayName("Find student by id")
    void givenStudentID_whenQuery_thenGetStudentObject() {
        Student student = studentRepository.getReferenceById(1L);
    }
}
```

**Dependencies:**

* `spring-boot-starter-data-jpa-test`
* `spring-boot-starter-test`
* `H2 database`

---

## `CommandLineRunner`

* Executes code **after application context is loaded**
* Useful for populating test/development data

```java
@Component
@RequiredArgsConstructor
public class StudentDataLoader implements CommandLineRunner {

    private final StudentRepository studentRepository;

    @Override
    public void run(String... args) throws Exception {
        studentRepository.save(new Student("Adam", "Grant", "Email"));
        studentRepository.save(new Student("Edward", "Grant", "EmailFajny"));
    }
}
```

---