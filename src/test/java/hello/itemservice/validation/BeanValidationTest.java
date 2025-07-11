package hello.itemservice.validation;

import hello.itemservice.domain.item.Item;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

public class BeanValidationTest {

    @Test
    void beanValidation() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        Item item = new Item();
        item.setItemName("");
        item.setPrice(9);
        item.setQuantity(10000);

        Set<ConstraintViolation<Item>> validate = validator.validate(item);
        System.out.println(validate);
        for (ConstraintViolation<Item> itemConstraintViolation : validate) {
            System.out.println("itemConstraintViolation = " + itemConstraintViolation);
            System.out.println("itemConstraintViolation.getMessage() = " + itemConstraintViolation.getMessage());
        }
    }
}
