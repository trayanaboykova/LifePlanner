package lifeplanner.schedulers;

import lifeplanner.scheduler.DateAndTimeScheduler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

class DateAndTimeSchedulerUTest {

    private DateAndTimeScheduler scheduler;

    @BeforeEach
    void setup() {
        scheduler = new DateAndTimeScheduler();
    }

    @Test
    void updateCurrentDateTime_updatesCurrentDateTime() {
        scheduler.updateCurrentDateTime();
        LocalDateTime updatedTime = scheduler.getCurrentDateTime();
        assertNotNull(updatedTime);
        // Optionally, verify that the updated time is close to the current system time.
        LocalDateTime now = LocalDateTime.now();
        assertTrue(Math.abs(now.getSecond() - updatedTime.getSecond()) < 5);
    }

    @Test
    void getCurrentDateTime_beforeUpdate_returnsCurrentDateTime() {
        // When updateCurrentDateTime hasn't been called, getCurrentDateTime returns a non-null value.
        LocalDateTime time = scheduler.getCurrentDateTime();
        assertNotNull(time);
    }
}