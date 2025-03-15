package za.co.bbd.grad.fupboard.api.dbobjects;

import jakarta.persistence.*;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "user_roles")
public class UserRole {

    @Id
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false, referencedColumnName = "user_id",
                foreignKey = @ForeignKey(name = "fk_user_role_user"))
    @OnDelete(action = OnDeleteAction.CASCADE) // Correct way to apply cascading delete
    private User user;

    @Id
    @ManyToOne
    @JoinColumn(name = "role_id", nullable = false, referencedColumnName = "role_id",
                foreignKey = @ForeignKey(name = "fk_user_role_role"))
    @OnDelete(action = OnDeleteAction.CASCADE) // Ensures deletion when parent is deleted
    private Role role;

    public UserRole() {}

    public UserRole(User user, Role role) {
        this.user = user;
        this.role = role;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}
