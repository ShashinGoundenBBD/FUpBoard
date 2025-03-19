package za.co.bbd.grad.fupboard.api.models;

import java.util.ArrayList;
import java.util.List;

public class Leaderboard {
    private List<LeaderboardEntry> entries;

    public Leaderboard() {
        this.entries = new ArrayList<>();
    }

    public List<LeaderboardEntry> getEntries() {
        return entries;
    }

    public void setEntries(List<LeaderboardEntry> entries) {
        this.entries = entries;
    }

    public void sortEntries() {
        this.entries.sort((a, b) -> -((Double)a.getScore()).compareTo(b.getScore()));
    }
}
