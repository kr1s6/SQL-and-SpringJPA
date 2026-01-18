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
