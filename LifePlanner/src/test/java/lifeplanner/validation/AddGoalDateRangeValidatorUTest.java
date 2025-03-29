package lifeplanner.validation;

import lifeplanner.web.dto.AddGoalRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.ConstraintValidatorContext.ConstraintViolationBuilder;
import jakarta.validation.ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderCustomizableContext;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AddGoalDateRangeValidatorUTest {

    @InjectMocks
    private AddGoalDateRangeValidator validator;

    @Mock
    private ConstraintValidatorContext context;

    @Mock
    private ConstraintViolationBuilder builder;

    @Mock
    private NodeBuilderCustomizableContext nodeBuilder;

    @Test
    void isValid_nullDates_returnsTrue() {
        AddGoalRequest request = new AddGoalRequest();
        request.setStartDate(null);
        request.setEndDate(null);
        assertTrue(validator.isValid(request, context));
    }

    @Test
    void isValid_validDates_returnsTrue() {
        AddGoalRequest request = new AddGoalRequest();
        request.setStartDate(LocalDate.of(2023, 1, 1));
        request.setEndDate(LocalDate.of(2023, 1, 2));
        assertTrue(validator.isValid(request, context));
    }

    @Test
    void isValid_invalidDates_returnsFalse_andAddsConstraintViolation() {
        when(context.getDefaultConstraintMessageTemplate()).thenReturn("default message");
        when(context.buildConstraintViolationWithTemplate("default message")).thenReturn(builder);
        when(builder.addPropertyNode(anyString())).thenReturn(nodeBuilder);

        AddGoalRequest request = new AddGoalRequest();
        request.setStartDate(LocalDate.of(2023, 1, 3));
        request.setEndDate(LocalDate.of(2023, 1, 2));

        assertFalse(validator.isValid(request, context));

        verify(context).disableDefaultConstraintViolation();
        verify(context).buildConstraintViolationWithTemplate("default message");
        verify(builder).addPropertyNode("endDate");
        verify(nodeBuilder).addConstraintViolation();
    }
}