package za.co.bbd.grad.fupboard.api.dbobjects;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import za.co.bbd.grad.fupboard.api.models.Leaderboard;
import za.co.bbd.grad.fupboard.api.models.LeaderboardEntry;

@Entity
@Table(name = "f_ups")
public class FUp {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer fUpId;
    private String fUpName;
    private String description;
    @ManyToOne()
    @JoinColumn(name = "project_id")
    @JsonBackReference
    private Project project;
    
    @OneToMany(mappedBy = "fUp")
    @JsonIgnore
    private List<Vote> votes;

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

    public List<Vote> getVotes() {
        return votes;
    }

    public void setVotes(List<Vote> votes) {
        this.votes = votes;
    }

    @JsonIgnore
    public Leaderboard getLeaderboard() {
        var leaderboard = new Leaderboard();

        var votesList = votes;
        if (votes == null) votesList = List.of();

        var usernames = votesList.stream().map(v -> v.getAccusedUsername()).distinct().toList();

        for (String name : usernames) {
            var avgScore = votesList.stream().filter(v -> v.getAccusedUsername().equals(name)).collect(Collectors.averagingDouble(v -> v.getScore()));
            leaderboard.getEntries().add(new LeaderboardEntry(name, avgScore));
        }

        leaderboard.sortEntries();

        return leaderboard;
    }
}
