## Bean Validation
- 검증기능을 매번 코드로 직접 작성하는 것은 힘들다.
- 따라서 이런 검증 로직을 모든 프로젝트에 적용 할 수 있게 공통화하고 표준화 한게 Bean Validation이다.
- 이를 잘 사용하면 애노테이션 하나로 검증 로직을 매우 편리하게 적용 할 수 있다.
- 공식 홈페이지
    - 공식 사이트 : http://hibernate.org/validator/
    - 공식 메뉴얼 : http://docs.jboss.org/hibernate/validator/6.2/reference/en-US/html_single/
    - 검증 애노테이션 모음 : http://docs.jboss.org/hibernate/validator/6.2/reference/en-US/html_single/#validator-defineconstraints-spec

## 검증 애노테이션
- @NotBlank : 빈값 + 공백만 있는경우 허용X
- @NotNull : 'null'을 허용하지 않는다
- @Range(min = '' max = '') 범위안에 있어야 한다.
- @Max(...) 최대 '...'까지 허용한다.
- javax.validation 으로 시작되면 표준 인터페이스, org.hibernate.validator는 하이버네이트 validator 구현체를 사용할때만 제공되는 기능

- 검증기 생성
    - ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    - Validator validator = factory.getValidator();

- 스프링 MVC는 어떻게 Bean Validator를 사용?
  - spring-boot-starter-validation 라이브러리 의존성을 추가하면 자동으로 Bean Validator를 인지하고 스프링에 통합
  - LocalValidatorFactoryBean을 Global Validator를 등록해준다.
    - LocalValidatorFactoryBean는 @NotNull 같은 애노테이션을 보고 검증을 수행한다.
    - 때문에 @Valid, @Validated 만 적용하면된다.

- 검증의 순서
  - @ModelAttribute가 있으면 각각의 필드에 타입 변환 시도
    - 성공하면 다음으로
    - 실패하면  typeMismatch 로 FieldError 추가
    - * 바인딩에 성공한 필드만 Bean Validation 적용
  - Validator 적용


## Bean Validation - 에러코드
 - Bean Validation 이 제공하는 오류 메시지를 변경하기
 - 애노테이션을 기준으로 error code를 생성해준다.(NotBlank.item.itemName, NotBlank.itemName, NotBlank.java.lang.String, NotBlank ... )
 - 생성된 메시지 코드 순서대로 messageSource에서 메시지 찾고 > 애노테이션의 message 속성 사용  >> 라이브러리가 제공하는 기본 값 사용

## Bean Validation - 오브젝트 오류
 - Bean Validation에서 특정 필드(FieldError)가 아닌 오브젝트(ObjectError) 관련 오류는 해당 도메인에 @ScriptAssert() 를 사용하면된다.
   - ex) @ScriptAssert(lang = "javascript", script = "_this.price * _this.quantity >= 10000", message = "총합이 10,000원 넘게 입력해 주세요")
   - 실제로 사용해보면 제약이 많고 복잡하다. 따라서 오브젝트 오류의 경우는 직접 자바 코드로 작성하는걸 권장한다.

## Bean Validation - 한계
 - 데이터 등록시, 수정시 요구사항이 다를수 있다. 동일한 모델 객체 각각 다르게 검증하는 방법을 알아보자
 - Bean Validation의 groups 기능 >> 실제로 잘 사용되지 않는다.
   - 해당 어노테이션에서 적용되야할 클래스를 넣어준다 >> @Validated(SaveCheck.class) || @Valid 에서는 해당 기능을 사용할 수 없다.
   - SaveCheck, UpdateCheck 인터페이스를 생성한 후 해당 도메인에 다음과 같이 표현한다.
```
    
    @NotNull(groups = UpdateCheck.class)
    private Long id;

    @NotBlank(groups = {UpdateCheck.class, SaveCheck.class})
    private String itemName;

    @NotNull(groups = {UpdateCheck.class, SaveCheck.class})
    @Range(min = 1000, max = 1000000)
    private Integer price;

    @NotNull(groups = {UpdateCheck.class, SaveCheck.class})
    @Max(value = 9999, groups = SaveCheck.class)
    private Integer quantity;

```
 - item을 직접 사용하지 않고 itemSaveForm, itemUpdateForm 같은 폼전송을 위한 별도의 모델 객체를 만들어서 사용한다.
   - 등록시 폼에서 전달하는 데이터가 해당 도메인 객체와 딱 맞지 않기 때문이다.
   - 실무에서는 회원 등록시 회원과 관련된 데이터만 전달 받는게 아니라 여러가지 데이터가 넘어온다.
   - HTML form > Item > Controller > Item > Repository
     - 간단한 경우에만 적용할 수 있다.
   - HTML form > ItemSaveForm> Controller > Item > Repository
     - 등록용과 수정용 별도의 폼 객체를 만들기 때문에 검증이 중복되지 않는다.


## Bean Validation - HTTP 메시지 컨버터
** @ModelAttribute 는 HTTP 요청 파라미터(URL 쿼리 스트링, POST Form)을 다룰 때 사용
** @RequestBody는 HTTP Body의 데이터를 객체로 변환할 때 사용한다, 주로 API JSON 요청을 다룰때 사용한다.
 - @Valid, @Validated 는 HttpMessageConverter 에도 적용할 수 있다.
## API  //POST raw{"itemName":"hello", "price":1000, "quantity":100}
 - 성공요청 : 성공
 - 실패 요청 : JSON을 객체로 생성하는것 자체가 실패함
 - 검증 오류 요청 : JSON을 객체로 생성하는 것은 성공했고, 검증에서 실패함

## @ModelAttribute vs @RequestBody
 - @ModelAttribute 는 각각의 필드 단위로 세밀하게 적용, 그래서 특정 필드에 타입이 맞지 않는 오류가 발생해도 나머지는 정상 처리 가능
 - @RequestBody  >> HttpMessageConverter는 위와 다르게 전체 객체 단위로 적용된다. 따라서 메시지 컨버터의 작동이 성공해서 ITEM을 만들어야 @Valid, @Validated 가 적용된다.
   - 즉 JSON데이터를 객체로 변경이 된 후 Validation이 적용된다. 그렇지 않으면 컨트롤러 조차 호출되지 않는다.
   - HttpMessageConverter 단계에서 실패하면 예외가 발생하고 이는 예외처리를 통해 다룰수 있다.


