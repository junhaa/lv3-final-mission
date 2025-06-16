package finalmission.fixture;

import finalmission.domain.Member;
import finalmission.domain.MemberRole;
import org.springframework.test.util.ReflectionTestUtils;

public class MemberFixture {

    public static Member CREATE_USER_1(){
        Member register = Member.register("member1@email.com", "member1", "member1");
        ReflectionTestUtils.setField(register, "id", 1L);
        return register;
    }

    public static Member CREATE_USER_2(){
        Member register = Member.register("member2@email.com", "member2", "member2");
        ReflectionTestUtils.setField(register, "id", 2L);
        return register;
    }

    public static Member CREATE_ADMIN_1(){
        Member register = Member.register("member3@email.com", "member3", "member3");
        ReflectionTestUtils.setField(register, "id", 3L);
        ReflectionTestUtils.setField(register, "role", MemberRole.ADMIN);
        return register;
    }
}
