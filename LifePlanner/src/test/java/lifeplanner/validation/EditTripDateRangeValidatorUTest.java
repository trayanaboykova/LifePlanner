package lifeplanner.validation;

import lifeplanner.web.dto.EditTripRequest;
import org.junit.jupiter.api.Test;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.ConstraintValidatorContext.ConstraintViolationBuilder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EditTripDateRangeValidatorUTest {

    @InjectMocks
    private EditTripDateRangeValidator validator;

    @Mock
    private ConstraintValidatorContext context;

    @Mock
    private ConstraintViolationBuilder builder;

    @Mock
    private ConstraintViolationBuilder.NodeBuilderCustomizableContext nodeBuilder;

    @Test
    void isValid_nullDates_returnsTrue() {
        EditTripRequest request = EditTripRequest.builder()
                .startDate(null)
                .endDate(null)
                .build();
        assertTrue(validator.isValid(request, context));
    }

    @Test
    void isValid_validDates_returnsTrue() {
        EditTripRequest request = EditTripRequest.builder()
                .startDate(LocalDate.of(2023, 7, 1))
                .endDate(LocalDate.of(2023, 7, 2))
                .build();
        assertTrue(validator.isValid(request, context));
    }

    @Test
    void isValid_invalidDates_returnsFalse_andAddsConstraintViolation() {
        when(context.getDefaultConstraintMessageTemplate()).thenReturn("message");
        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(builder);
        when(builder.addPropertyNode(anyString())).thenReturn(nodeBuilder);

        EditTripRequest request = EditTripRequest.builder()
                .startDate(LocalDate.of(2023, 7, 3))
                .endDate(LocalDate.of(2023, 7, 2))
                .build();

        assertFalse(validator.isValid(request, context));

        verify(context).disableDefaultConstraintViolation();
        verify(context).buildConstraintViolationWithTemplate("message");
        verify(builder).addPropertyNode("endDate");
        verify(nodeBuilder).addConstraintViolation();
    }
}