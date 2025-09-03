package finalmission.domain;

import finalmission.exception.InvalidInputException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Table;
import java.util.Objects;

@Entity
@Table(name = "members")
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MemberRole role;

    private Member(Long id, String email, String password, String name, MemberRole role) {
        validateEmail(email);
        validatePassword(password);
        validateName(name);
        validateRole(role);
        this.id = id;
        this.email = email;
        this.password = password;
        this.name = name;
        this.role = role;
    }

    public static Member register(String email, String password, String name){
        return new Member(null, email, password, name, MemberRole.USER);
    }

    public boolean canDeleteBy(Long memberId){
        return role == MemberRole.ADMIN || (this.role == MemberRole.USER && memberId == this.id);
    }

    public boolean isPasswordMatch(String password){
        return this.password.equals(password);
    }

    private void validateEmail(String email) {
        if(email == null || email.isBlank()) {
            throw new InvalidInputException("이메일은 null이거나 공백일 수 없습니다.");
        }
    }

    private void validatePassword(String password) {
        if(password == null || password.isBlank()) {
            throw new InvalidInputException("비밀번호는 null이거나 공백일 수 없습니다.");
        }
    }

    private void validateName(String name) {
        if(name == null || name.isBlank()) {
            throw new InvalidInputException("이름은 null이거나 공백일 수 없습니다.");
        }
    }

    private void validateRole(MemberRole role) {
        if(role == null){
            throw new InvalidInputException("권한은 null일 수 없습니다.");
        }
    }

    protected Member() {}

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getName() {
        return name;
    }

    public MemberRole getRole() {
        return role;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Member member)) {
            return false;
        }
        return Objects.equals(id, member.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
