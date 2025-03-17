package za.co.bbd.grad.fupboard.api.dbobjects;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.*;

@Entity
@Table(name = "project_invites")
public class ProjectInvite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int projectInviteId;

    @ManyToOne
    @JoinColumn(name = "project_id", nullable = false)
    @JsonBackReference // Prevents infinite recursion
    private Project project;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonBackReference // Prevents infinite recursion
    private User user;

    @Column(nullable = false)
    private Boolean accepted = false; // Default value is false (not accepted)

    public ProjectInvite() {}

    public ProjectInvite(Project project, User user, Boolean accepted) {
        this.project = project;
        this.user = user;
        this.accepted = accepted;
    }

    public int getProjectId() {
        return project.getProjectId();
    }

    public int getProjectInviteId() {
        return projectInviteId;
    }

    public void setProjectInviteId(int projectInviteId) {
        this.projectInviteId = projectInviteId;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Boolean getAccepted() {
        return accepted;
    }

    public void setAccepted(Boolean accepted) {
        this.accepted = accepted;
    }
}
