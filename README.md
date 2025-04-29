## 🌱 1차시 미션

### 1️⃣ spring-tutorial를 완료하자!

- 사진 첨부
![image](https://github.com/user-attachments/assets/ecd4ae52-200b-46ed-9c4a-40f4ec08de7d)

### 2️⃣ spring이 지원하는 기술들(IoC/DI, AOP, PSA 등)을 자유롭게 조사해요

세션 자료에 있는 내용에 추가적으로 본인만의 언어로 기술해주세요

예제 코드까지 추가해주시면 👍

---

- **Spring 삼각형이란?**
    - POJO 기반의 스프링 3대 프로그래밍 모델을 의미함
    
    ![img.png](attachment:494041eb-b6b9-49da-b495-f4fe4d62bf8b:img.png)
    
    - **IoC/DI(Inversion of Control / Dependency Injection)**
        - IoC는 제어의 역전이라는 개념으로, 객체의 생성과 의존관계 설정을 개발자가 아닌 프레임워크가 담당하는 것
        - . DI는 IoC의 구현 방식으로, 객체가 필요로 하는 의존성을 외부에서 주입해주는 패턴
            - **의존성을 주입 받아 사용**하므로 **의존성이 변경되더라도** **의존하는 객체는 변경될 필요가 없음**
        - 예제 코드
            
            ```java
            // 1. 인터페이스 정의
            public interface UserRepository {
                User findById(Long id);
            }
            
            // 2. 구현체
            @Repository
            public class UserRepositoryImpl implements UserRepository {
                @Override
                public User findById(Long id) {
                    // DB에서 사용자 조회 로직
                    return new User(id, "사용자" + id);
                }
            }
            
            // 3. 서비스 클래스 (의존성 주입을 받음)
            @Service
            public class UserService {
                private final UserRepository userRepository;
                
                // 생성자 주입 방식
                @Autowired
                public UserService(UserRepository userRepository) {
                    this.userRepository = userRepository;
                }
                
                public User getUser(Long id) {
                    return userRepository.findById(id);
                }
            }
            
            // 4. 컨트롤러
            @RestController
            @RequestMapping("/api/users")
            public class UserController {
                private final UserService userService;
                
                @Autowired
                public UserController(UserService userService) {
                    this.userService = userService;
                }
                
                @GetMapping("/{id}")
                public User getUserById(@PathVariable Long id) {
                    return userService.getUser(id);
                }
            }
            ```
            
    - **AOP (Aspect Oriented Programming)**
        - AOP는 관점 지향 프로그래밍으로, 핵심 비즈니스 로직과 공통 기능(로깅, 트랜잭션, 보안 등)을 분리해 코드의 중복을 줄이고 모듈화를 증진
            - **비즈니스 로직**에서 **종단 관심사 코드**를 **분리/모듈화**하고 **필요한 곳에 적용**한다.
            - **핵심 관심사에 집중**
        - 예제 코드
            
            ```java
            // 1. 공통 관심사를 정의하는 Aspect 클래스
            @Aspect
            @Component
            public class LoggingAspect {
                private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);
                
                // UserService의 모든 메서드 실행 전후에 로깅
                @Around("execution(* com.example.service.UserService.*(..))")
                public Object logServiceMethods(ProceedingJoinPoint joinPoint) throws Throwable {
                    String methodName = joinPoint.getSignature().getName();
                    logger.info("시작: {} 메서드 실행", methodName);
                    
                    long startTime = System.currentTimeMillis();
                    Object result = joinPoint.proceed(); // 실제 메서드 실행
                    long endTime = System.currentTimeMillis();
                    
                    logger.info("종료: {} 메서드 실행 시간 {}ms", methodName, (endTime - startTime));
                    return result;
                }
                
                // 예외 발생 시 로깅
                @AfterThrowing(pointcut = "execution(* com.example.service.*.*(..))", throwing = "ex")
                public void logException(JoinPoint joinPoint, Exception ex) {
                    logger.error("예외 발생: {} - {}", joinPoint.getSignature().getName(), ex.getMessage());
                }
            }
            ```
            
    - **PSA (Portable Service Abstraction: 이식 가능한 서비스 추상화)**
        - 환경과 세부 기술에 종속되지 않은 일관된 서비스 추상화 계층을 제공
            - 예: Spring의 Transaction, Cache, ORM 등
        - 어렵고 복잡한 개념을 **특정 환경에 종속되지 않고 쉽게 사용할 수 있게 추상화된 레이어 제공**
            - 간단히 말하면 모듈화다
                - 뒤에선 어떻게 돌아가는지는 모르겠지만 내가 쓰려는 목적에 맞다면 사용 가능(재활용)
        - 예제코드
            
            ```java
            // PSA를 활용한 트랜잭션 관리
            @Service
            public class OrderService {
                private final OrderRepository orderRepository;
                private final PaymentService paymentService;
                
                @Autowired
                public OrderService(OrderRepository orderRepository, PaymentService paymentService) {
                    this.orderRepository = orderRepository;
                    this.paymentService = paymentService;
                }
                
                @Transactional  // 선언적 트랜잭션 관리 (PSA)
                public Order createOrder(OrderRequest request) {
                    // 주문 생성
                    Order order = new Order();
                    order.setItems(request.getItems());
                    order.setTotalAmount(calculateTotal(request.getItems()));
                    
                    // 결제 처리
                    Payment payment = paymentService.processPayment(order.getTotalAmount());
                    order.setPaymentId(payment.getId());
                    
                    // 주문 저장 - 결제나 저장 중 예외가 발생하면 전체 트랜잭션이 롤백됨
                    return orderRepository.save(order);
                }
                
                private BigDecimal calculateTotal(List<OrderItem> items) {
                    return items.stream()
                        .map(item -> item.getPrice().multiply(new BigDecimal(item.getQuantity())))
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                }
            }
            ```
            
    - **POJO (Plain Old Java Object)**
        - 오래된 방식의 순수한 자바 객체
        - 특정 프레임워크에 종속되지 않은 일반 자바 객체
            - **DI, AOP, PSA를 하기 위해** 이제까지 하던 대로 **그냥 평범한 객체를 만들면 된다.**
            - 특정 프레임워크, 라이브러리에 **종속되지 않음**
            - ⇒ 이런건 이제 스프링 같은 프레임 워크가 다 해줄 거임(비즈니스 로직만 잘 짜면 된다.)
        - **POJO로 만들면 가독성과 유지보수성이 뛰어나고 단위테스트가 용이**
        - 예제코드
            
            ```java
            // POJO 기반 도메인 모델
            public class User {
                private Long id;
                private String name;
                private String email;
                private LocalDate birthDate;
                
                // 생성자
                public User() {}
                
                public User(Long id, String name) {
                    this.id = id;
                    this.name = name;
                }
                
                // Getter, Setter
                public Long getId() {
                    return id;
                }
                
                public void setId(Long id) {
                    this.id = id;
                }
                
                public String getName() {
                    return name;
                }
                
                public void setName(String name) {
                    this.name = name;
                }
                
                public String getEmail() {
                    return email;
                }
                
                public void setEmail(String email) {
                    this.email = email;
                }
                
                public LocalDate getBirthDate() {
                    return birthDate;
                }
                
                public void setBirthDate(LocalDate birthDate) {
                    this.birthDate = birthDate;
                }
                
                // 비즈니스 메서드
                public boolean isAdult() {
                    return birthDate != null && 
                           ChronoUnit.YEARS.between(birthDate, LocalDate.now()) >= 18;
                }
                
                @Override
                public String toString() {
                    return "User{id=" + id + ", name='" + name + "', email='" + email + "'}";
                }
            }
            ```
            

### 3️⃣ Spring Bean 이 무엇이고, Bean 의 라이프사이클은 어떻게 되는지 조사해요

---

- Spring Bean이란?
    - **스프링 프레임워크**에 의해 **생성되고 관리되는** 자바 객체
    - 일반 자바 객체(POJO)와 기술적으로는 동일하지만, Spring 컨테이너에 의해 생성, 관리, 소멸되는 특별한 지위를 가짐
- Bean의 주요 특징
    - Spring 컨테이너에 의해 인스턴스화, 조립, 관리됨
    - 의존성 주입(DI)을 통해 다른 Bean과 연결됨
    - 싱글톤(기본), 프로토타입, 요청, 세션 등 다양한 범위(Scope)로 생성 가능
    - 생명주기 콜백을 통해 초기화 및 소멸 과정을 제어할 수 있음
- Bean 등록방법
    - **컴포넌트 스캔**: `@Component`, `@Service`, `@Repository`, `@Controller` 등의 어노테이션을 사용
    - **자바 설정 클래스**: `@Configuration` 클래스 내에서 `@Bean` 메서드로 등록
    - **XML 설정**: XML 파일에 bean 정의
    - **명시적 vs 묵시적**:
        - **명시적** : 아키텍처 입장에서는 좋다, 외부 객체를 사용하기 위해서 필요
        - **묵시적** : 개발이 편하다
- Bean의 라이프사이클
    
    > 스프링 컨테이너 생성 -> Bean 생성 -> 의존성 주입 -> **초기화 콜백** -> Bean 사용 -> **소멸 전 콜백** -> 스프링 종료
    > 
    - 초기화 콜백(init) : Bean이 생성되고, Bean의 **의존성 주입이 완료된 뒤 호출**된다.
    - 소멸 전 콜백(destroy) : 스프링이 종료되기 전, **Bean이 소멸되기 직전에 호출**된다.
    
    ### **@PostConstruct / @PreDestroy**
    
    - 스프링에서 권장하는 방법으로, 어노테이션을 초기화, 소멸 전 콜백 메서드에 붙여주면 된다.
    - Spring에 종속적인 기술도 아니고, Java 표준 기술이다.
    - @Component를 이용하여 Bean을 등록 하는 경우에는 하나의 클래스에서 확인할 수 있다는 장점도 있다.
        
        ```java
        public class TestBean {
        	//...
        	@PostConstruct
            public void init() {
                System.out.println("빈 초기화");
                start();
            }
        
        	@PreDestroy
            public void close() {
                System.out.println("빈 소멸 직전");
                finish();
            }
        }
        ```
        
        이 방법은 **내부 코드를 수정할 수 없는 경우**에는 사용하지 못한다.
        
        외부 라이브러리에 대한 초기화, 종료 설정을 하려면 아래와 같은 @Bean 설정을 이용하자.
        
    
    ---
    
    ### **@Bean(initMethod = "...", destroyMethod = "...")**
    
    - 이 방법을 이용하면, @Bean 어노테이션에 추가 설정으로 메서드 명을 명시하는 방식으로 초기화, 소멸 전 콜백 메서드를 등록할 수 있다.
    - 클래스 내부 코드가 아닌, 설정하는 곳(Configuration)에서 지정하기 때문에 **내부 코드를 고칠 수 없는 경우에 유용**하다.
        
        ```java
        @Configuration
        class TestLifeCycleConfig {
        
            @Bean(initMethod = "init", destroyMethod = "close") //메서드 이름
            public TestBean testBean() {
                return new TestBean();
            }
        }
        ```
        

### 4️⃣ 스프링 어노테이션을 심층 분석해요

- 어노테이션이란 무엇이며, Java에서 어떻게 구현될까요?
- 스프링에서 어노테이션을 통해 Bean을 등록할 때, 어떤 일련의 과정이 일어나는지 탐구해보세요.
- `@ComponentScan` 과 같은 어노테이션을 사용하여 스프링이 컴포넌트를 어떻게 탐색하고 찾는지의 과정을 깊게 파헤쳐보세요.
- 어떻게 어노테이션을 내가 원하는 기능으로 커스텀할 수 있는 지 알아봐요.

---

### 어노테이션이란?

- 메타데이터
- 코드에 대한 추가 정보를 제공
- 그 자체로는 기능을 수행하지 않지만, 컴파일러나 런타임 환경에서 코드를 어떻게 처리할지에 대한 지시사항을 제공
- 구현 방법
    
    ```java
    // 기본적인 어노테이션 정의 방법
    // @interface 키워드를 사용하여 정의
    public @interface MyAnnotation {
        // 어노테이션 요소(elements) 정의
        String value() default "기본값";
        int priority() default 0;
        boolean enabled() default true;
    }
    ```
    

### 어노테이션을 통한 Bean 등록 과정

1. 어노테이션 기반 Bean 등록의 시작점

- 스프링에서 어노테이션 기반 Bean 등록은 주로 `@Component`, `@Service`, `@Repository`, `@Controller` 어노테이션과 같은 스테레오타입 어노테이션으로 시작됩니다.

2. 어노테이션 탐지 및 처리 과정

1. **스프링 컨테이너 초기화**:
    - `ApplicationContext` 구현체 생성 
    (ex: `AnnotationConfigApplicationContext`)
2. **Bean 정의 스캐너 활성화**:
    - `ClassPathBeanDefinitionScanner`가 클래스패스에서 어노테이션이 달린 클래스 검색
    - `ConfigurationClassPostProcessor`가 `@Configuration` 클래스 처리
3. **어노테이션 메타데이터 처리**:
    - 스프링은 `AnnotationMetadata`를 통해 클래스의 어노테이션 정보 추출
    - 이 메타데이터를 기반으로 `BeanDefinition` 객체 생성
4. **Bean 정의 등록**:
    - 생성된 `BeanDefinition`은 `BeanDefinitionRegistry`에 등록
    - 이 때 Bean의 이름, 범위(Scope), 의존성 등의 정보 포함
5. **Bean 인스턴스 생성 및 초기화**:
    - `BeanFactory`가 등록된 정의를 바탕으로 실제 Bean 인스턴스 생성
    - 의존성 주입 및 초기화 메서드 호출

3. 어노테이션 처리 핵심 컴포넌트

- **`BeanFactoryPostProcessor`**: Bean 정의를 처리하고 수정
- **`BeanPostProcessor`**: Bean 인스턴스의 초기화 전후에 추가 처리
- **`AnnotationConfigUtils`**: 어노테이션 처리를 위한 유틸리티 클래스

### `@ComponentScan`을 통한 컴포넌트 탐색 과정

1. ComponentScan의 역할

`@ComponentScan`은 지정된 패키지와 그 하위 패키지에서 컴포넌트 어노테이션(`@Component`, `@Service` 등)이 부착된 클래스를 찾아 Bean으로 등록합니다.

```java

java
@Configuration
@ComponentScan(basePackages = "com.example.myapp")
public class AppConfig {
// 구성 내용
}

```

2. ComponentScan의 상세 처리 과정

1. **스캔 대상 설정**:
    - `basePackages`: 문자열로 패키지 지정
    - `basePackageClasses`: 패키지를 대표하는 클래스 지정
    - 지정하지 않으면 `@ComponentScan`이 선언된 클래스의 패키지가 기본값
2. **스캐너 초기화**:
    - `ClassPathBeanDefinitionScanner` 객체 생성
    - 필터 설정: 포함 필터(`includeFilters`)와 제외 필터(`excludeFilters`) 적용
3. **클래스패스 스캐닝**:
    - ASM 라이브러리를 사용하여 클래스 파일 바이트코드 분석
    - 이때 실제 클래스를 로딩하지 않고 메타데이터만 추출하여 성능 최적화
4. **타입 필터링**:
    - 스테레오타입 어노테이션 확인
    - 사용자 정의 필터 적용
5. **Bean 정의 생성**:
    - 검출된 각 컴포넌트 클래스에 대해 `BeanDefinition` 생성
    - 범위(Scope), 지연 초기화(Lazy), 주입 방식 등 설정
6. **Bean 이름 생성**:
    - `BeanNameGenerator`를 통해 자동으로 이름 생성
    - `@Component("customName")`처럼 명시적으로 지정된 이름 우선 적용
7. **Bean 등록**:
    - 생성된 Bean 정의를 `BeanDefinitionRegistry`에 등록

3. ComponentScan의 고급 기능

- **필터링 기능**:
    
    ```java
    
    java
    @ComponentScan(
        basePackages = "com.example",
        includeFilters = @ComponentScan.Filter(type = FilterType.REGEX, pattern = ".*Repository"),
        excludeFilters = @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = Deprecated.class)
    )
    
    ```
    
- **ScopedProxyMode**: 프록시 생성 방식 지정
    
    ```java
    
    java
    @ComponentScan(
        basePackages = "com.example",
        scopedProxy = ScopedProxyMode.INTERFACES
    )
    
    ```
    
- **lazyInit**: 지연 초기화 설정
    
    ```java
    
    java
    @ComponentScan(
        basePackages = "com.example",
        lazyInit = true
    )
    
    ```
    

4. 스캐닝 내부 동작 메커니즘

스프링의 컴포넌트 스캔은 다음과 같은 내부 클래스들의 협력으로 이루어집니다:

1. **`ClassPathScanningCandidateComponentProvider`**:
    - 클래스패스에서 컴포넌트 후보를 식별하는 기본 클래스
    - 리소스 패턴을 사용해 클래스패스에서 모든 클래스 검색
2. **`ClassPathBeanDefinitionScanner`**:
    - `ClassPathScanningCandidateComponentProvider`를 확장
    - 발견된 컴포넌트를 Bean 정의로 변환하고 등록
3. **`ConfigurationClassParser`**:
    - `@Configuration` 클래스의 `@ComponentScan` 어노테이션 처리
    - 중첩된 구성과 임포트 처리
4. **`ComponentScanAnnotationParser`**:
    - `@ComponentScan` 어노테이션을 파싱하여 스캐너 구성

### 커스텀 어노테이션 만들기

- @Target의 값을 ({ElementType.TYPE}) 으로 사용하는 예시
    
    > PersonInfo.java
    > 
    > 
    > 클래스 생성시 mention이라는 인삿말을 넣어주기 위한 어노테이션 생성
    > 
    
    ```java
    import java.lang.annotation.ElementType;
    import java.lang.annotation.Retention;
    import java.lang.annotation.RetentionPolicy;
    import java.lang.annotation.Target;
    
    @Target({ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    public @interface PersonInfo {
    
    	String mention() default "안녕하세용 ㅎ";
    }
    ```
    
    > Person.java
    > 
    > 
    > 어노테이션 값에 예의바르게 인사하는 인삿말을 전달
    > 
    
    ```java
    @PersonInfo(mention = "반가워요.")
    public class Person {
    	private String name;
    	private int age;
    
    	public Person(String name, int age) {
    		super();
    		this.name = name;
    		this.age = age;
    	}
    	public String getName() {
    		return name;
    	}
    	public void setName(String name) {
    		this.name = name;
    	}
    	public int getAge() {
    		return age;
    	}
    	public void setAge(int age) {
    		this.age = age;
    	}
    }
    ```
    
    > Trash.java
    > 
    > 
    > 어노테이션 값에 예의없게 대하는 인삿말을 전달
    > 
    
    ```java
    @PersonInfo(mention = "뭘 봐")
    public class Trash {
    	private String name;
    	private int age;
    
    	public Trash(String name, int age) {
    		super();
    		this.name = name;
    		this.age = age;
    	}
    	public String getName() {
    		return name;
    	}
    	public void setName(String name) {
    		this.name = name;
    	}
    	public int getAge() {
    		return age;
    	}
    	public void setAge(int age) {
    		this.age = age;
    	}
    }
    ```
    
    > PersonService.java
    > 
    > 
    > Person과 Trash 인스턴스를 생성하여 런타임 시 어노테이션을 읽어 mention을 출력
    > 
    
    ```java
    import java.lang.annotation.Annotation;
    
    public class PersonService {
    
    	public static void main(String[] args) {
    		PersonService p = new PersonService();
    		p.printPerson(new Person("kim", 20));
    		p.printTrash(new Trash("park", 28));
    	}
    
    	public void printPerson(Person p) {
    		Annotation[] annotations = Person.class.getDeclaredAnnotations();
    		for(Annotation annotation : annotations) {
    			if (annotation instanceof PersonInfo) {
    				PersonInfo personInfo = (PersonInfo) annotation;
    				System.out.println(p.getName() + "(" + p.getAge() + ") 가 말합니다 : " + personInfo.mention());
    			}
    		}
    	}
    	public void printTrash(Trash p) {
    		Annotation[] annotations = Trash.class.getDeclaredAnnotations();
    		for(Annotation annotation : annotations) {
    			if (annotation instanceof PersonInfo) {
    				PersonInfo personInfo = (PersonInfo) annotation;
    				System.out.println(p.getName() + "(" + p.getAge() + ") 가 말합니다 : " + personInfo.mention());
    			}
    		}
    	}
    }
    ```
    
    - 출처
        - https://velog.io/@potato_song/Java-%EC%96%B4%EB%85%B8%ED%85%8C%EC%9D%B4%EC%85%98-%EC%BB%A4%EC%8A%A4%ED%85%80-%EC%96%B4%EB%85%B8%ED%85%8C%EC%9D%B4%EC%85%98-%EB%A7%8C%EB%93%A4%EA%B8%B0

### 5️⃣ **단위 테스트와 통합 테스트 탐구**

- 단위 테스트와 통합 테스트의 의미를 알아봅시다!

---

### ✅ 단위 테스트 (Unit Test)

- **정의**: 시스템의 가장 작은 단위(보통 클래스나 메서드 단위)를 독립적으로 테스트하는 것.
- **목적**: 특정 메서드 또는 클래스의 **로직이 올바르게 작동하는지** 검증.
- **특징**:
    - 외부 의존성(DB, 파일, 네트워크 등)을 **Mocking**으로 대체.
    - 테스트 속도가 매우 빠름.
    - 일반적으로 `@WebMvcTest`, `@MockBean`, `Mockito` 등을 사용.

```java
java
코드 복사
@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    public void testGetUserById() {
        // given
        User user = new User(1L, "홍길동");
        given(userRepository.findById(1L)).willReturn(Optional.of(user));

        // when
        User result = userService.getUserById(1L);

        // then
        assertEquals("홍길동", result.getName());
    }
}

```

---

### ✅ 통합 테스트 (Integration Test)

- **정의**: 여러 컴포넌트들이 함께 작동하는지를 테스트하는 것. 실제 환경에 가까운 상태에서 실행.
- **목적**: **전체 시스템이 제대로 통합되어 작동하는지** 확인.
- **특징**:
    - 실제 DB(H2 등 임시 DB 사용)나 Spring Context를 로딩함.
    - 느리지만 실제 운영 환경과 유사.
    - `@SpringBootTest`, `@AutoConfigureMockMvc` 등을 사용.

```java
java
코드 복사
@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testGetUser() throws Exception {
        mockMvc.perform(get("/users/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("홍길동"));
    }
}

```

---

### 🔍 차이점 요약

| 항목 | 단위 테스트 (Unit Test) | 통합 테스트 (Integration Test) |
| --- | --- | --- |
| 테스트 범위 | 개별 클래스/메서드 | 시스템 전체 또는 여러 계층 |
| Spring Context | 로딩하지 않음 | 로딩함 |
| 속도 | 빠름 | 느림 |
| 의존성 처리 | Mock 사용 | 실제 환경 또는 Test용 Bean 사용 |
