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
