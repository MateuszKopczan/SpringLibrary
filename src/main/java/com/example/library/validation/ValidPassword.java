package com.example.library.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Constraint(validatedBy = PasswordValidator.class)
@Target({ElementType.TYPE, ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ValidPassword {

    String message() default "Password must contain: at least one uppercase character one lowercase character one number and one special character";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
