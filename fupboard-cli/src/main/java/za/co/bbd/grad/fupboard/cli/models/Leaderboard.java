package za.co.bbd.grad.fupboard.cli.models;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import za.co.bbd.grad.fupboard.cli.common.Constants;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Leaderboard {
    private final NumberFormat SCORE_FORMAT = new DecimalFormat("#0.0"); 
    private final int COL_GAP = 2;

    private List<LeaderboardEntry> entries;

    public List<LeaderboardEntry> getEntries() {
        return entries;
    }

    @Override
    public String toString() {
        String out = "";

        var colRankWidth = "Rank".length();
        var colUsernameWidth = "Username".length();
        var colScoreWidth = "Score".length();
        
        for (int i = 0; i < entries.size(); i++) {
            var entry = entries.get(i);
            var rankLength = ((i+1)+"").length();
            if (rankLength > colRankWidth) {
                colRankWidth = rankLength;
            }
            var usernameLength = entry.getUsername().length();
            if (usernameLength > colUsernameWidth) {
                colUsernameWidth = usernameLength;
            }
            var scoreLength = (entry.getScore()+"").length();
            if (scoreLength > colScoreWidth) {
                colScoreWidth = scoreLength;
            }
        }

        colRankWidth += COL_GAP;
        colUsernameWidth += COL_GAP;
        
        out += Constants.YELLOW +
            padRight("Rank", colRankWidth) +
            padRight("Username", colUsernameWidth) +
            padRight("Score", colScoreWidth) +
            Constants.RESET + "\n";

        for (int i = 0; i < entries.size(); i++) {
            var entry = entries.get(i);
            out += padRight((i+1)+"", colRankWidth) +
                padRight(entry.getUsername(), colUsernameWidth) +
                padRight(SCORE_FORMAT.format(entry.getScore()), colScoreWidth);
            if (i < entries.size() - 1) {
                out += "\n";
            }
        }
        
        return out;
    }

    private static String padRight(String s, int n) {
        return String.format("%-" + n + "s", s);
    }
}
