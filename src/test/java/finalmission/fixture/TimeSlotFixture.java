package finalmission.fixture;

import finalmission.domain.TimeSlot;
import java.time.LocalTime;
import net.bytebuddy.asm.Advice.Local;
import org.springframework.test.util.ReflectionTestUtils;

public class TimeSlotFixture {

    public static TimeSlot CREATE_TIME_SLOT_1(){
        LocalTime time = LocalTime.of(10, 0);
        TimeSlot register = TimeSlot.register(time);
        ReflectionTestUtils.setField(register, "id", 1L);

        return register;
    }

    public static TimeSlot CREATE_TIME_SLOT_2(){
        LocalTime time = LocalTime.of(11, 0);
        TimeSlot register = TimeSlot.register(time);
        ReflectionTestUtils.setField(register, "id", 2L);

        return register;
    }

    public static TimeSlot CREATE_TIME_SLOT_3(){
        LocalTime time = LocalTime.of(12, 0);
        TimeSlot register = TimeSlot.register(time);
        ReflectionTestUtils.setField(register, "id", 3L);

        return register;
    }

    public static TimeSlot CREATE_TIME_SLOT_4(){
        LocalTime time = LocalTime.of(13, 0);
        TimeSlot register = TimeSlot.register(time);
        ReflectionTestUtils.setField(register, "id", 4L);

        return register;
    }
}
