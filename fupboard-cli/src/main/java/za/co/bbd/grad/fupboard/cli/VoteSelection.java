package za.co.bbd.grad.fupboard.cli;

import java.util.Map;

public class VoteSelection {
    private final int projectId;
    private final int fUpId;
    private final Map<Integer, Integer> voteIndexMap;

    public VoteSelection(int projectId, int fUpId, Map<Integer, Integer> voteIndexMap) {
        this.projectId = projectId;
        this.fUpId = fUpId;
        this.voteIndexMap = voteIndexMap;
    }

    public int getProjectId() { return projectId; }
    public int getFUpId() { return fUpId; }
    public Map<Integer, Integer> getVoteIndexMap() { return voteIndexMap; }
}
