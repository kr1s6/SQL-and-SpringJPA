# Spring Data JPA

Spring Data JPA removes a lot of boilerplate code by providing abstractions on top of JPA and Hibernate.

---

## JPA Annotations

It is recommended to explicitly annotate tables and columns for better readability and maintainability.

```java
@Column(name = "example")
@Table(name = "example")
```

---

## Auto-Generated Values

### Database-specific strategies

* **PostgreSQL** → `SEQUENCE` + `@SequenceGenerator`
* **MySQL / MariaDB** → `IDENTITY`

---

## Identity Strategy (`GenerationType.IDENTITY`)

⚠️ **Not recommended for Hibernate + PostgreSQL**

* Very similar to the `AUTO` strategy
* Difference: a separate identity generator is managed per type hierarchy
* Generated values are unique only within a given type hierarchy

```java
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;
```

### Drawbacks

❌ No batch inserts
❌ Worse performance with many inserts
❌ Hibernate loses control over the entity lifecycle

**Batch insert**:
A database technique for inserting multiple rows using fewer, larger operations instead of individual inserts. This significantly improves performance by reducing network overhead and transaction costs.

---

## Sequence Strategy (`GenerationType.SEQUENCE`)

* Supports `allocationSize`, which can significantly improve performance
* One sequence can be shared across multiple tables (architectural or business decision)
* Requires the sequence to exist in the database
* IDs are allocated in blocks to reduce database round trips
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

### Advantages

✔️ Best performance on PostgreSQL
✔️ Full Hibernate control over entity lifecycle
✔️ Highly scalable

### Disadvantages

❌ Requires manual sequence creation in the database

### Recommended `allocationSize` values

| Environment | allocationSize |
| ----------- | -------------- |
| Dev         | 1              |
| Test        | 10             |
| Prod        | 50–100         |
| High-load   | 100–1000       |

---

## Table Strategy (`GenerationType.TABLE`)

* Very similar to the `SEQUENCE` strategy
* Uses a database table to simulate a sequence

```java
@Entity
@TableGenerator(name = "tab", initialValue = 0, allocationSize = 50)
public class EntityWithTableId {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "tab")
    private long id;
}
```

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

* The **owning side** contains the foreign key (FK)
* The owning side **always owns the relationship**
* The entity that owns the FK column uses the `@JoinColumn` annotation
* The other side is only a **mirror** of the relationship (`mappedBy`)
* The non-owning side does **not** define the foreign key
* Relationships are **optional by default**, which means you can create a `Course` without a `CourseMaterial`

To make the relationship mandatory:

```java
@OneToOne(optional = false)
```

Entities containing a foreign key can be fetched using:

* **Fetch Join**
* **DTO projections**

---

### Owning side example

```java
@Entity
@Table(name = "course_material")
public class CourseMaterial {

    @Id
    @SequenceGenerator(...)
    @GeneratedValue(...)
    private Long id;

    @OneToOne
    @JoinColumn(
        name = "course_id",              // FK column name
        referencedColumnName = "id"       // Target column (default = PK)
    )
    private Course course;
}
```

* If `referencedColumnName` is not provided, JPA automatically uses the **primary key** of the target entity

---

### When is `referencedColumnName` required?

Use it when the foreign key references a column **other than the primary key**.

```java
@ManyToOne
@JoinColumn(
    name = "user_email",
    referencedColumnName = "email"
)
private User user;
```

---

### Inverse side (non-owning side)

```java
@Entity
@Table(name = "course")
public class Course {

    @Id
    @SequenceGenerator(...)
    @GeneratedValue(...)
    private Long id;

    @OneToOne(mappedBy = "course")
    private CourseMaterial courseMaterial;
}
```

`mappedBy = "course"` tells JPA:

> "This side is NOT the owner of the relationship.
> The relationship is managed by the `course` field in `CourseMaterial`."

---

## Cascading

```java
@OneToOne(cascade = CascadeType.ALL)
```

Cascading allows you to persist multiple entities with a single operation.

```java
courseMaterialRepository.save(
    new CourseMaterial("URL:SDSD", new Course("Course 1", 1000))
);
```

In this example:

* `CourseMaterial` is saved
* `Course` is automatically saved as well

---

## Fetch Types

* `FetchType.EAGER` (**default**) – related entity is fetched immediately using a join
* `FetchType.LAZY` – related entity is loaded only when accessed

```java
@OneToOne(fetch = FetchType.LAZY)
```

⚠️ Be careful with `EAGER` fetching — it may cause performance issues and unexpected joins.

---

## Unidirectional vs Bidirectional Relationships

### Unidirectional `@OneToOne`

* Navigation is possible only in one direction
* `Course` will **not** expose `CourseMaterial`

### Bidirectional `@OneToOne`

* Both entities are aware of each other
* `Course` can access `CourseMaterial`

---

### `toString()` and Bidirectional Relationships

When using bidirectional mappings, always override `toString()` carefully to avoid infinite recursion.

Example:

```java
@Override
public String toString() {
    return "Course{" +
            "id=" + id +
            ", title='" + title + '\'' +
            ", credit=" + credit +
            ", courseMaterial=" + (courseMaterial != null ? courseMaterial.getId() : null) +
            '}';
}
```

This approach:

* Prevents `StackOverflowError`
* Safely displays relationship information

---

## JPA `@OneToMany` & `@ManyToOne`

> Relationships are **optional by default**, which means you can create an entity without its related entities.
> To make a relationship mandatory:
>
> ```java
> @OneToOne(optional = false)
> ```

---

## `@OneToMany`

* Using `@OneToMany` on `Teacher` results in a **foreign key column in the `Course` table**
* You will **not** see courses stored in the `Teacher` table
* Instead, the `Course` table contains a reference to `Teacher`

```java
@Entity
@Table(name = "teacher")
public class Teacher {

    @Id
    @Column(name = "teacher_id")
    private Long teacherId;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(
        name = "teacher_id_name_for_course", // FK column in Course table
        referencedColumnName = "teacher_id"  // PK column in Teacher table
    )
    private List<Course> courseList;
}
```

📌 This is a **unidirectional** `@OneToMany` mapping.

---

## `@ManyToOne`

✅ **Preferred approach**: always favor `@ManyToOne` over `@OneToMany` when possible.

Reasons:

* Simpler mapping
* Better performance
* More natural database design

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

---

## `@ManyToMany`

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

### Helper method

```java
public void addStudent(Student student) {
    if (studentList == null) {
        studentList = new ArrayList<>();
    }
    studentList.add(student);
}
```

---

## Paging & Sorting

### Paging

```java
Pageable firstPageWithThreeRecords = PageRequest.of(0, 3);

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

---

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

---

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

```java
@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);

    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}
```

### Query Method Examples

```java
Optional<List<Student>> findByFirstName(String name);          // Find records matching the exact name
Optional<List<Student>> findByLastNameContaining(String name); // Find records containing the string
List<Student> findByGuardianName(String name);                 // Find records in @Embedded class
```

[JPA Query Method Reference](https://docs.spring.io/spring-data/jpa/reference/jpa/query-methods.html)

---

## Automatic Custom Queries

* Spring Data generates queries based on method names automatically.

```java
Optional<User> findByUsername(String username);
boolean existsByUsername(String username);
```

---

## Manual Custom Queries with `@Query`

* Use **JPQL** (class-based query language)

```java
@Query("SELECT f FROM Foo f WHERE LOWER(f.name) = LOWER(:name)")
Foo retrieveByName(@Param("name") String name);

@Query("SELECT DISTINCT e FROM SoftwareEngineer e LEFT JOIN FETCH e.techStack")
List<SoftwareEngineer> findAllWithTechStack();

@Query("SELECT s FROM Student s WHERE s.emailId = ?1")
Student getStudentByEmailAddress(String emailId);

@Query("SELECT s.firstName FROM Student s WHERE s.emailId = ?1") // get only firstName
String getStudentFirstNameByEmailAddress(String emailId);
```

> Notes:
>
> * `Student` refers to the **entity class**, not the table
> * `?1`, `?2` refer to **method parameters by index**

---

## Native Queries

* Use actual **database table and column names**

```java
@Query(
    value = "SELECT * FROM student s WHERE s.email_address = ?1",
    nativeQuery = true
)
Student getStudentByEmailAddressNative(String emailId);
```

### Named Parameters in Native Queries

```java
@Query(
    value = "SELECT * FROM student s WHERE s.email_address = :emailId",
    nativeQuery = true
)
Student getStudentByEmailAddressNativeNamedParam(@Param("emailId") String emailId);
```

---

## `@Transactional` and `@Modifying`

* Repository methods **do not have transactions by default**
* **Updates/deletes** must be transactional and annotated with `@Modifying`

```java
@Transactional(readOnly = true)
interface UserRepository extends JpaRepository<User, Long> {

    List<User> findByLastname(String lastname);

    @Modifying
    @Transactional
    @Query("DELETE FROM User u WHERE u.active = false")
    void deleteInactiveUsers();

    @Modifying
    @Transactional
    @Query(
        value = "UPDATE student SET first_name = :firstName WHERE email_address = :emailId",
        nativeQuery = true
    )
    int updateStudentNameByEmailId(String firstName, String emailId);
}
```

* `readOnly = true` for queries that **only read data**
* `@Modifying` overrides this for update/delete operations
* Transactions ensure **all-or-nothing** behavior (rollback on exception)

---

## JPA Testing with `@DataJpaTest`

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
* H2 database

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

