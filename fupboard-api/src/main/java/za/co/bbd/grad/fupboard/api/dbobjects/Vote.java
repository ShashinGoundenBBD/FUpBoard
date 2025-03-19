package za.co.bbd.grad.fupboard.api.dbobjects;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.*;


@Entity
@Table(name = "votes")
public class Vote {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int voteId;

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
    @JsonBackReference
    private FUp fUp;

    @Column(nullable = false)

    private int score;

    public Vote() {}

    public Vote(User reporter, User accused, FUp fUp, int score) {
        this.reporter = reporter;
        this.accused = accused;
        this.fUp = fUp;
        this.score = score;
    }

    public int getVoteId() {
        return voteId;
    }

    public void setVoteId(int voteId) {
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

    public FUp getfUp() {
        return fUp;
    }

    public void setfUp(FUp fUp) {
        this.fUp = fUp;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        if (score < 1 || score > 5) throw new IllegalArgumentException();
        this.score = score;
    }

    public String getReporterUsername() {
        return reporter.getUsername();
    }

    public String getAccusedUsername() {
        return accused.getUsername();
    }
}
