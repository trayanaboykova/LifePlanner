package lifeplanner.validation;

import lifeplanner.web.dto.AddTripRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.ConstraintValidatorContext.ConstraintViolationBuilder;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DateRangeValidatorUTest {

    @InjectMocks
    private DateRangeValidator validator;

    @Mock
    private ConstraintValidatorContext context;

    @Mock
    private ConstraintViolationBuilder builder;

    @Mock
    private ConstraintViolationBuilder.NodeBuilderCustomizableContext nodeBuilder;

    @Test
    void isValid_nullDates_returnsTrue() {
        AddTripRequest request = new AddTripRequest();
        request.setStartDate(null);
        request.setEndDate(null);
        assertTrue(validator.isValid(request, context));
    }

    @Test
    void isValid_validDates_returnsTrue() {
        AddTripRequest request = new AddTripRequest();
        request.setStartDate(LocalDate.of(2023, 5, 1));
        request.setEndDate(LocalDate.of(2023, 5, 2));
        assertTrue(validator.isValid(request, context));
    }

    @Test
    void isValid_invalidDates_returnsFalse_andAddsConstraintViolation() {
        // Setup mock behavior
        when(context.getDefaultConstraintMessageTemplate()).thenReturn("default message");
        when(context.buildConstraintViolationWithTemplate("default message")).thenReturn(builder);
        when(builder.addPropertyNode("endDate")).thenReturn(nodeBuilder);

        AddTripRequest request = new AddTripRequest();
        request.setStartDate(LocalDate.of(2023, 5, 3));
        request.setEndDate(LocalDate.of(2023, 5, 2));

        assertFalse(validator.isValid(request, context));

        verify(context).disableDefaultConstraintViolation();
        verify(context).buildConstraintViolationWithTemplate("default message");
        verify(builder).addPropertyNode("endDate");
        verify(nodeBuilder).addConstraintViolation();
    }
}