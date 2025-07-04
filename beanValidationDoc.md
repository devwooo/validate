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