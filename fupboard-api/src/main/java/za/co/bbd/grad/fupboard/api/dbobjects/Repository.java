package za.co.bbd.grad.fupboard.api.dbobjects;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;

@Entity
@Table(name = "repositories", uniqueConstraints = {
    @UniqueConstraint(columnNames = "repository_url") // Ensures unique repository URLs
})
public class Repository {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore // Hides ID from API responses for security
    private Integer repositoryId;

    @ManyToOne
    @JoinColumn(name = "project_id", nullable = false)
    @JsonBackReference // Prevents infinite JSON recursion
    private Project project;

    @Column(nullable = false, length = 64)
    private String repositoryName;

    @Column(nullable = false, length = 64, unique = true)
    private String repositoryUrl; // Unique to prevent duplicates

    public Repository() {}

    public Repository(Project project, String repositoryName, String repositoryUrl) {
        this.project = project;
        this.repositoryName = repositoryName;
        this.repositoryUrl = repositoryUrl;
    }

    public Integer getRepositoryId() {
        return repositoryId;
    }

    public void setRepositoryId(Integer repositoryId) {
        this.repositoryId = repositoryId;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public String getRepositoryName() {
        return repositoryName;
    }

    public void setRepositoryName(String repositoryName) {
        this.repositoryName = repositoryName;
    }

    public String getRepositoryUrl() {
        return repositoryUrl;
    }

    public void setRepositoryUrl(String repositoryUrl) {
        this.repositoryUrl = repositoryUrl;
    }
}

