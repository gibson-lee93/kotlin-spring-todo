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
  
  
### Model Mapper dependency 추가 및 설정
사용하는 이유: Todo 생성시 필요한 field들을 dto로 받고 알맞게 dto에서 todo entity 형식으로 바꾸기 위해
`build.gradle.kts'에 Model Mapper dependency 추가
```
dependencies {
  // ...(전략)...
	implementation("org.modelmapper:modelmapper:2.4.4")
  // ...
}
```
@Bean으로 등록하기 위해 config 파일 작성
``` kotlin
@Configuration
class ModelMapperConfig {

    @Bean
    fun modelMapper(): ModelMapper {
        val modelMapper = ModelMapper()
        // 필드 이름이 같은 것끼리 매칭
        modelMapper.configuration.isFieldMatchingEnabled = true

        // private 필드여도 접근 가능
        modelMapper.configuration.fieldAccessLevel = org.modelmapper.config.Configuration.AccessLevel.PRIVATE

        return modelMapper
    }
}
```
사용법은 TodoService에서 다룸

---

## `Todo` Entity 작성
``` kotlin
@Entity
@Table(name = "todos")
class Todo(
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    val id: Long,

    @Column(nullable = false)
    var title: String,

    var description: String? = null
)
```
간단하게 title하고 description만 attribute으로 갖는다. Description만 null 값을 허용

---

## `Todo` Dto 작성
``` kotlin
data class TodoDto(
    val id: Long,

    @field:Size(min = 4, message = "Todo title should have at least 4 characters")
    var title: String? = null,

    val description: String? = null
) {}
```
Todo를 update 할 때 description만 수정할 경우가 있으니 title에 null 값을 허용  
하지만 null이 아닐경우 문자열 최소 길이가 4 이여야 하며 아닐경우 위와 같은 메세지 출력

---

## `Todo` Controller & Service 구현
### `Todo` Controller
``` kotlin
@RestController
@RequestMapping("/todos")
class TodoController(private val todoService: TodoService) {

    //Dto에서 validation 사용하려면 @Validated 필수
    @PostMapping
    fun createTodo(@RequestBody @Validated todoDto: TodoDto): Todo {
        return todoService.createTodo(todoDto)
    }

    @GetMapping("/{id}")
    fun getTodoById(@PathVariable(name = "id") id: Long): Todo {
        return todoService.getTodoById(id)
    }

    @GetMapping
    fun getAllTodos(): List<Todo> {
        return todoService.getAllTodos()
    }

    @PutMapping("/{id}")
    fun updateTodo(
        @PathVariable(name = "id") id: Long,
        @RequestBody @Validated todoDto: TodoDto
    ): Todo {
        return todoService.updateTodo(id, todoDto)
    }

    @DeleteMapping("/{id}")
    fun deleteTodo(@PathVariable(name = "id") id: Long): String {
        return todoService.deleteTodo(id)
    }
}
```
기본 CRUD REST API

### `Todo` Service
``` kotlin
@Service
class TodoService(
    private val todoRepository: TodoRepository,
    private val mapper: ModelMapper
) {
    fun createTodo(todoDto: TodoDto): Todo {
        return try {
            todoRepository.save(mapToEntity(todoDto))
        } catch(e: MethodArgumentNotValidException) {
            throw APIException(HttpStatus.BAD_REQUEST, e.message)
        }
    }

    fun getTodoById(id: Long): Todo {
        return try {
            todoRepository.findById(id).get()
        } catch(e: NoSuchElementException) {
            throw APIException(HttpStatus.NOT_FOUND, "Can't find todo with Id: $id")
        }
    }

    fun getAllTodos(): List<Todo> {
        return todoRepository.findAll().toList()
    }

    fun updateTodo(id: Long, todoDto: TodoDto): Todo {
        val todo = getTodoById(id)
        todo.title = todoDto.title ?: todo.title
        todo.description = todoDto.description ?: todo.description

        return try {
            todoRepository.save(todo)
        } catch (e: Exception) {
            throw APIException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error")
        }
    }

    fun deleteTodo(id: Long): String {
        val todo = getTodoById(id)
        try {
            todoRepository.delete(todo)
            return "Successfully deleted"
        } catch (e: Exception) {
            throw APIException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error")
        }
    }

    private fun mapToEntity(todoDto: TodoDto): Todo {
        return mapper.map(todoDto, Todo::class.java)
    }
}
```
`createTodo`에서 `todoRepository.save(mapToEntity(todoDto))`통해 데이터베이스에 저장할 때 `.save()`는 Todo의 entity 타입을 매게변수로 받기 때문에 `mapToEntity` 함수에서 `model mapper`를 사용하여 `TodoDto`를 `Todo` entity로 매핑하여 `.save()`를 실행  
<br/>
`Todo`의 update나 delete는 먼저 parameter로 받은 id로 `getTodoById`를 사용하여 조회를 하고, 조회가 되지 않을 경우 `getTodoById`에서 에러 throw 하도록 구현
<br/>

---

## Custom Error Response 구현
### ErrorResponse
``` kotlin
class ErrorResponse(
    //에러 코드
    val status : Int? = null,
    
    //에러 메세지
    val message : String? = null
)
```

### APIException
``` kotlin
class APIException(status: HttpStatus, message : String) : RuntimeException(message) {
    val status = status
}
```

### GlobalExceptionHandler
``` kotlin
@ControllerAdvice
class GlobalExceptionHandler {
    @ExceptionHandler
    fun handleApiException(exc: APIException) : ResponseEntity<ErrorResponse> {
        val errorResponse = ErrorResponse(exc.status.value(), exc.message)
        return ResponseEntity(errorResponse, exc.status)
    }
}
```
에러가 발생했을 경우 `ErrorResponse`를 통해 에러의 상태 코드와 간단한 메시지를 return
``` kotlin
return try {
  todoRepository.findById(id).get()
} catch(e: NoSuchElementException) {
  throw APIException(HttpStatus.NOT_FOUND, "Can't find todo with Id: $id")
}
```
위와 같이 에러가 발생했을 경우 `todo service`에서 `throw APIException`에 첫번째 매개변수로 에러에 알맞은 HTTP status와 두번째 매게변수로 메시지 입력
