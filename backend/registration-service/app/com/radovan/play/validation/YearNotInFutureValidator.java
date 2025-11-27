package com.radovan.play.validation;

import play.data.validation.Constraints;
import play.libs.F;

import java.time.Year;

public class YearNotInFutureValidator extends Constraints.Validator<Integer> {

    @Override
    public boolean isValid(Integer value) {
        if (value == null) {
            return true; // Required će već hvatati null
        }
        int currentYear = Year.now().getValue();
        return value <= currentYear;
    }

    @Override
    public F.Tuple<String, Object[]> getErrorMessageKey() {
        return F.Tuple("error.year.future", new Object[]{});
    }

}
