# kotlin-spring-todo
코틀린과 스프링부트 프레임워크를 사용한 간단한 Todo CRUD REST API 프로젝트 입니다.

---

## 기본 코드 생성
Spring Initializr 사용: https://start.spring.io/
- Project: Gradle Project
- Language: Kotlin
- Dependencies:
  - Spring Boot DevTools
  - Spring Web
  - Validation
  - Spring Data JPA
  - PostgreSQL Driver

---

## 기초 설정

### PostgreSQL 연결 설정
`application.properties` 에서 local postgres 정보에 맞게 작성

```
## Spring DATASOURCE (DataSourceAutoConfiguration & DataSourceProperties)
spring.datasource.url = jdbc:postgresql://localhost:5432/todo
spring.datasource.username = annotation
spring.datasource.password = password

# The SQL dialect makes Hibernate generate better SQL for the chosen database
spring.jpa.properties.hibernate.dialect = org.hibernate.dialect.PostgreSQLDialect

# Hibernate ddl auto (create, create-drop, validate, update)
spring.jpa.hibernate.ddl-auto = update
```
`spring.datasource.url`에는 `localhost` 뒤에 postgres의 post와 `/` 뒤에 database 이름을 작성  
각 `spring.datasource.username`와 `spring.datasource.password`에는 유저네임과 비밀번호를 작성

---

## `Todo` Entity 작성
``` kotlin
@Entity
@Table(name = "todos")
data class Todo(
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    val id: Long? = null,

    @Column(nullable = false)
    var title: String? = null,

    var description: String? = null
) {
    fun set(todo: Todo) {
        this.title = todo.title ?: this.title
        this.description = todo.description ?: this.description
    }
}
```
간단하게 title하고 description만 attribute으로 갖는다. 처음 todo를 생성할 때 title을 null로 가져선 안 되기 때문에 service에서 exception을 내보내고  
만들어진 todo를 update할 때에는 description 또는 title만 update 할 가능성이 있게 때문에 title과 description을 nullabe로 설정했다.

---

## `Todo` Controller & Service 구현
### `Todo` Controller
``` kotlin
@RestController
@RequestMapping("/todos")
class TodoController(val service: TodoService) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@RequestBody todo: Todo): Todo {
        return service.create(todo)
    }

    @GetMapping("/{id}")
    fun detail(@PathVariable(name = "id") id: Long): Todo {
        return service.detail(id)
    }

    @GetMapping
    fun list(): List<Todo> {
        return service.list()
    }

    @PutMapping("/{id}")
    fun update(
        @PathVariable(name = "id") id: Long,
        @RequestBody todo: Todo
    ): Todo {
        return service.update(id, todo)
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable(name = "id") id: Long): String {
        return service.delete(id)
    }

    @ExceptionHandler(value = [NoSuchElementException::class])
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun noSuchElementException(e: NoSuchElementException) =
        ExceptionResponse(
            code = "NOT_FOUND",
            message = e.message.toString(),
            trace = e.stackTraceToString()
        )

    @ExceptionHandler(value = [IllegalArgumentException::class])
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun illegalArgumentException(e: IllegalArgumentException) =
        ExceptionResponse(
            code = "BAD_REQUEST",
            message = e.message.toString(),
            trace = e.stackTraceToString()
        )
}
```
기본 CRUD REST API

### `Todo` Service
``` kotlin
@Service
class TodoService(
    private val repository: TodoRepository
) {
    fun create(todo: Todo): Todo {
        if (todo.title == null) {
            throw IllegalArgumentException("Title should not be empty")
        }
        return repository.save(todo)
    }

    fun detail(id: Long): Todo {
        return try {
            repository.findById(id).get()
        } catch (e: NoSuchElementException) {
            throw NoSuchElementException("Todo does not exist")
        }
    }

    fun list(): List<Todo> {
        return repository.findAll()
    }

    fun update(id: Long, todo: Todo): Todo {
        val foundTodo = detail(id)
        foundTodo.set(todo)
        return repository.save(foundTodo)
    }

    fun delete(id: Long): String {
        val todo = detail(id)
        repository.delete(todo)
        return "Todo successfully deleted"
    }
}
```
`Todo`의 update나 delete는 먼저 parameter로 받은 id로 `deatil`를 사용하여 조회를 하고, 조회가 되지 않을 경우 `detail`에서 `NoSuchElementException`을 throw 하도록 구현
<br/>

---

## Custom Exception Response 구현
### ExcetpionResponse
``` kotlin
data class ExceptionResponse(
    val code: String?,
    val message: String?,
    val trace: String?
)
```

