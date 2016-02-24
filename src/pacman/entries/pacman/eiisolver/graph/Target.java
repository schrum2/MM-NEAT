package pacman.entries.pacman.eiisolver.graph;

import pacman.game.Constants.MOVE;

/**
 * A target node for a ghost during extended search.
 *
 * @author louis
 *
 */
public class Target {

    /**
     * the junction that is closer to the ghosts
     */
    public Node ghostJunction;
    /**
     * First move from ghostJunction towards pacman
     */
    public MOVE firstMoveFromGhost;
    public BigEdge edge;
    public boolean reached;
    public boolean abandoned;
    public int currDist;
    private Target[] backups = new Target[60];
    int nrBackups = 0;

    public void init() {
        for (int i = 0; i < backups.length; ++i) {
            backups[i] = new Target();
        }
    }

    public void set(BorderEdge edge, int ghostNr) {
        ghostJunction = edge.ghostJunction;
        firstMoveFromGhost = edge.firstMoveFromGhost;
        this.edge = edge.edge;
        currDist = edge.ghostDist[ghostNr];
        abandoned = false;
        reached = false;
    }

    public void backup() {
        Target backup = backups[nrBackups];
        backup.reached = reached;
        backup.abandoned = abandoned;
        backup.currDist = currDist;
        ++nrBackups;
    }

    public void restore() {
        --nrBackups;
        Target backup = backups[nrBackups];
        reached = backup.reached;
        abandoned = backup.abandoned;
        currDist = backup.currDist;
    }
}
