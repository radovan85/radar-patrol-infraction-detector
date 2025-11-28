package com.radovan.play.validation;

import play.data.validation.Constraints;

import java.lang.annotation.*;

@Constraints.ValidateWith(YearNotInFutureValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface YearNotInFuture {
    String message() default "Manufacture year cannot be in the future";
}

