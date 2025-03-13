package za.co.bbd.grad.fupboard.api.dbobjects;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "f_ups")
public class FUp {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Integer fUpId;
    private String fUpName;
    private String description;
    @ManyToOne()
    @JoinColumn(name = "project_id")
    @JsonBackReference
    private Project project;

    public FUp() {}

    public FUp(Project project, String name, String description) {
        this.fUpName = name;
        this.description = description;
        this.project = project;
    }

    public Integer getfUpId() {
        return fUpId;
    }

    public void setfUpId(Integer fUpId) {
        this.fUpId = fUpId;
    }

    public String getfUpName() {
        return fUpName;
    }

    public void setfUpName(String fUpName) {
        this.fUpName = fUpName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }
}
