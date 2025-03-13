package za.co.bbd.grad.fupboard.api.dbobjects;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;

@Entity
@Table(name = "commits", uniqueConstraints = {
    @UniqueConstraint(columnNames = "commit_hash") // Ensures each commit hash is unique
})
public class Commit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore // Hide ID from API responses for security
    private Integer commitId;

    @ManyToOne
    @JoinColumn(name = "repository_id", nullable = false)
    @JsonBackReference // Prevents infinite recursion in JSON serialization
    private Repository repository;

    @ManyToOne
    @JoinColumn(name = "f_up_id", nullable = false)
    @JsonBackReference // Prevents infinite recursion in JSON serialization
    private FUp fUp;

    @Column(nullable = false, length = 128, unique = true)
    private String commitHash; // Unique commit identifier

    public Commit() {}

    public Commit(Repository repository, FUp fUp, String commitHash) {
        this.repository = repository;
        this.fUp = fUp;
        this.commitHash = commitHash;
    }

    public Integer getCommitId() {
        return commitId;
    }

    public void setCommitId(Integer commitId) {
        this.commitId = commitId;
    }

    public Repository getRepository() {
        return repository;
    }

    public void setRepository(Repository repository) {
        this.repository = repository;
    }

    public FUp getFUp() {
        return fUp;
    }

    public void setFUp(FUp fUp) {
        this.fUp = fUp;
    }

    public String getCommitHash() {
        return commitHash;
    }

    public void setCommitHash(String commitHash) {
        this.commitHash = commitHash;
    }
}
