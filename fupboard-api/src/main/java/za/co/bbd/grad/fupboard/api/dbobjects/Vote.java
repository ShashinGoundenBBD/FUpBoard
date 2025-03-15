package za.co.bbd.grad.fupboard.api.dbobjects;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;


@Entity
@Table(name = "votes")
public class Vote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore // Hide ID from API responses for security
    private Integer voteId;

    @ManyToOne
    @JoinColumn(name = "reporter_id", nullable = false)
    @JsonBackReference // Prevents infinite recursion in JSON serialization
    private User reporter;

    @ManyToOne
    @JoinColumn(name = "accused_id", nullable = false)
    @JsonBackReference // Prevents infinite recursion in JSON serialization
    private User accused;

    @ManyToOne
    @JoinColumn(name = "f_up_id", nullable = false)
    @JsonBackReference // Prevents infinite recursion in JSON serialization
    private FUp fUp;

    @Column(nullable = false)

    private Integer score;

    public Vote() {}

    public Vote(User reporter, User accused, FUp fUp, Integer score) {
        this.reporter = reporter;
        this.accused = accused;
        this.fUp = fUp;
        this.score = score;
    }

    public Integer getVoteId() {
        return voteId;
    }

    public void setVoteId(Integer voteId) {
        this.voteId = voteId;
    }

    public User getReporter() {
        return reporter;
    }

    public void setReporter(User reporter) {
        this.reporter = reporter;
    }

    public User getAccused() {
        return accused;
    }

    public void setAccused(User accused) {
        this.accused = accused;
    }

    public FUp getFUp() {
        return fUp;
    }

    public void setFUp(FUp fUp) {
        this.fUp = fUp;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        if (score < 1 || score > 5) {
            throw new IllegalArgumentException("Score must be between 1 and 5");
        }
        this.score = score;
    }
}
