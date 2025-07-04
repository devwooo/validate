## V1. 검증 직접처리
 - Map을 만들어 error 사항을 담아 Model에 넣고
 - 입력폼으로 다시 return 처리 해준다.
  - 입력 폼으로 다시 돌아왔는데 입력했던 값이 남아있네? >> ModleAttribute에 값이 남아있다. 그래서 남아있는것
 - ?. >> errors 가 null 인경우  NullPointerException 이 발생하는 대신 null을 반환하는 문법이다.



## V2 BindingResult1 - 스프링이 제공하는 검증 오류처리 방법 > java.hello.web.validation.ValidationItemControllerV1 >> addItemV1
 - Controller로 들어와서 Map에 에러코드를 담았던 것을 BindingResult bindingResult 를 파라미터로 받음으로써
 -  ** BindingResult bindingResult 위치는 @ModelAttribute 객체 다음에 와야 한다.
 - errros 대신 bindingResult 에 담으면 된다. Item Object에 담긴건 addError() 에 파라미터로 new FieldError() 를 통해서 담지만
 - global 에러는 addError() 에 파라미터로 new ObjectError()에 담아준다.
 - 파라미터 중 ObjectName에들어가는 값은 @ModelAttribute 에 바인딩된 객체의 이름이다. 

 - BindingResult 에 바인딩 된 Error 들이 view 단에서 사용되는데
 - th:errors, th:errorclass 로 사용된다.
   - th:errors="*{...}" >> 다음과 같은 형식으로 사용되며, "..." 안에는 Controller에서 BindingResult에 담긴 field name 값과 맵핑이 된다. 
   - th:errorclass="..." >> th:field="*{...}" 의 "..." 에 맵핑된 name의 에러가 BindingResult에 있을경우 errorclass에 명시된 클래스가 class="..." 여기에 append된다.

## V2 BindingResult2 >> java.hello.web.validation.ValidationItemControllerV2 >> addItemV1
 - BindingResult 가 있으면 @ModelAttribute 에 데이터 바인딩 시 오류가 발생해도 컨트롤러가 호출된다.
   - BindingResult 가 없으면 400 에러
   - BindingREsult 가 있으면 > Field Error 를 BindingResult에 담아서 컨트롤러를 정상 호출한다.
 - BindingResult 는 인터페이스고, Errors도 인터페이스인데 이를 상속하고 있다. 즉 BindingResult 대신에 Errors 를 사용해도 되나 기능이 조금 제한되게 제공한다.
 - 하지만 이전의 Map 을 사용했을때와 달리 오류 발생시 사용자가 입력한 데이터가 유지되고 있지 않다.
 - 유지 하려면 어떻게 해야할지 FieldError, ObjectError에 대해서 자세히 알아보자

## FieldError, ObjectError >> java.hello.web.validation.ValidationItemControllerV2 >> addItemV2
 - 사용자 입력오류 메시지가 사용자 화면에 남아있도록 처리하자
 - new FieldError() 함수의 매개변수중 rejectedValue 값에 넘어온 데이터를 넣어주면 값이 유지된다.
 - 타임리프의 th:field 값은 정상 상황에서는 모델 객체의 값을 사용하지만, 오류가 발생하면 FieldError 에서 보관한 값을 사용해서 출력한다.

## 오류코드와 메시지 처리1 >> java.hello.web.validation.ValidationItemControllerV2 >> addItemV3
 - FieldError(...) 에서 사용되는 에러 메시지를 관리하고 싶다.
 - FieldError(), ObjectError 생성자를 보면 codes[메시지 코드], arguments[메시지에서 사용하는 인자]를 사용하면 된다.
 - application.properties >> 에서 메시지 파일을 읽게 하기 위해 basename을 추가 등록한다. >> spring.messages.basename=messages, errors
 - 해당 errors.properties 파일에서 Key값을 읽어 노출시켜준다.
 - codes 는 String[] 을 받으며 해당 배열에 key 값들을 나열하면되고, 0번쨰 부터 일치하는지 확인하고 없으면 다음 순서의 키를 매칭하고, 모두 매칭되지 않으면 error 발생한다.

## 오류코드와 메시지 처리2
 - 컨트롤러에서 BindingResult는 검증할 객체가 무엇인지 알고 있다(왜냐하면 @ModelAttribute 객체 바로 다음에 오니까), 따라서 target에 대한 정보는 없어도 된다.
 - BindingResult가 제공하는 rejectValue(), reject() 를 사용하면 더 간편해진다.
   - rejectValue(field, errorCode), rejectValue(field, errorCode, errorArgs, defulaMessage)
 - 오류 메시지를 잘찾아 출력하는데 이 규칙은 MessageCodesResolver를 이해해야 한다. 이를 자세히 알아보자


## 오류코드와 메시지 처리3
 - 오류 코드를 자세히 만들수도 있고, 범용성있게 간단하게 만들수 있다.
 - 오류코드는 더 자세한것을 찾고 없으면 더 단계가 낮은 순서로 찾는다
   - #Level1 >> required.item.itemName
     -  #Level2 >> required
 - 스프링에서 'MessageCodesResolver' 를 통해서 이러한 기능을 지원한다

## 오류코드와 메시지 처리4
 - MessageCodesResolver는 인터페이스고 이를 구현한 DefaultMessageCodesResolver 를 통해 사용한다
 - 동작 방식
   - rejectValue(), reject() 의 내부에서 MessageCodesResolver 를 사용한다.
   - FieldError, ObjectError 의 생성자를 보면 오류 코드 하나가 아니라 여러 오류코드를 가질수 있다
   - MessageCodesResolver 를 통해서 생성된 순서대로 오류코드를 보관한다.
 - 기본 메시지 생성 규칙
 - 객체오류인 경우
   - ex) 오류코드 :  required, object name : item
   - 1. code + "." + object name >>  required.item
   - 2. code        >> required
 - 필드 오류인 경우
   - ex) 오류코드 :  typeMismatch, object name : "user", field : "age", field type : int
     - 1. typeMismatch.user.age
     - 2. typeMismatch.age
     - 3. typeMismatch.int
     - 4. typeMismatch


## 오류 코드와 메시지 처리
 - 핵심은 구체적인 것에서 >> 덜 구체적인 것으로
    

## 스프링이 직접만든 오류 메시지 처리
 - 개발자가 직접 설정한 오류코드 >> rejectValue()를 직접 호출
 - 스프링이 직접 검증오류에 추가한 경우(주로 타입 정보가 틀린경우)
 - ex) typeMismatch.item.price,typeMismatch.price,typeMismatch.java.lang.Integer,typeMismatch 는 스프링이 만들어서 넣은 codes 이고 해당 내용을
 - errors.properties에 재정의할 수 있다.

## Validator1
 - 컨트롤러에서 검증 로직이 차지 하는 부분이 매우 큰데, 이런경우 별도 클래스로 분리하는게 좋다. 
 - 이때 구현할 클래스는 Validator 인터페이스를 상속한다.

## Validator2
 - 스프링이 Validator 인터페이스를 별도로 제공하는 이유는 체계적으로 검증 기능을 도입하기 위해서 이다.
 - Validator1에서는 검증기를 직접 불러서 사용했지만, Validator 인터페이스를 사용해서 검증기를 만들면 스프링의 추가적인 도움을 받을 수 있다.
 - 'WebDataBind'는 스프링의 파라미터 바인딩 역할을 해주고 검증 기능도 내부에 포함한다.
   - WebDataBind를 사용하기 위해서는 @InitBinder 어노테이션을 사용한다.
   - 글로벌 설정은 별도록 해야한다.
     - WebMvcConfigurer를 상속하고, getValidator()를 @Overide 하여 사용할 검증기를 리턴시켜주면된다.
   - 검증할 객체에 @Validated 어노테이션을 붙여 검증을 하라고 시킨다.
     - 즉 WebDataBind에 등록한 검증기를 찾아서 실행한다. 만약 여러개의 검증기를 등록한다면 그중 어느 검증기가 실행되야 할지 구분이 필요하다
     - 이때 spports()가 사용된다. 
 - 검증시 @Validated, @Valid 를 사용가능 
 - @Valid 하려면 build.gradle에 의존성 주입되야하고, 자바 표준 검증 애노테이션이고
 - @Validated는 스프링 전용 애노테이션 이다.

