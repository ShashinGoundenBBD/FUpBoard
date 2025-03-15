package za.co.bbd.grad.fupboard.api.dbobjects;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "projects")
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer projectId;

    private String projectName;

    @ManyToOne()
    @JoinColumn(name = "owner")
    @JsonBackReference
    private User owner;
    
    @OneToMany(mappedBy = "project")
    @JsonManagedReference
    private List<FUp> fUps;
    
    @OneToMany(mappedBy = "project")
    @JsonManagedReference
    private List<ProjectInvite> invites;

    public Project() {}

    public Project(String name, User owner) {
        this.projectName = name;
        this.owner = owner;
    }

    public Integer getProjectId() {
        return projectId;
    }

    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public List<FUp> getfUps() {
        return fUps;
    }

    public void setfUps(List<FUp> fUps) {
        this.fUps = fUps;
    }

    public List<ProjectInvite> getInvites() {
        return invites;
    }

    public void setInvites(List<ProjectInvite> invites) {
        this.invites = invites;
    }
}
