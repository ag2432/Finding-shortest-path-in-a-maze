package submit;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import game.FindState;
import game.Finder;
import game.Node;
import game.NodeStatus;
import game.ScramState;

/** Student solution for two methods. */
public class Pollack extends Finder {

    /** Get to the orb in as few steps as possible. <br>
     * Once you get there, you must return from the function in order to pick it up. <br>
     * If you continue to move after finding the orb rather than returning, it will not count.<br>
     * If you return from this function while not standing on top of the orb, it will count as <br>
     * a failure.
     *
     * There is no limit to how many steps you can take, but you will receive<br>
     * a score bonus multiplier for finding the orb in fewer steps.
     *
     * At every step, you know only your current tile's ID and the ID of all<br>
     * open neighbor tiles, as well as the distance to the orb at each of <br>
     * these tiles (ignoring walls and obstacles).
     *
     * In order to get information about the current state, use functions<br>
     * state.currentLoc(), state.neighbors(), and state.distanceToOrb() in FindState.<br>
     * You know you are standing on the orb when distanceToOrb() is 0.
     *
     * Use function state.moveTo(long id) in FindState to move to a neighboring<br>
     * tile by its ID. Doing this will change state to reflect your new position.
     *
     * A suggested first implementation that will always find the orb, but <br>
     * likely won't receive a large bonus multiplier, is a depth-first walk. <br>
     * Some modification is necessary to make the search better, in general. */
    @Override
    public void findOrb(FindState state) {

        ArrayList<Long> visited= new ArrayList<Long>();
        optimizeddfswalk(state, visited);

    }

    /** Pres Pollack is standing on a Node u (say) given by State state. Visit every node reachable
     * along paths of unvisited nodes from node u according to it's priority (determined by distance
     * to target) for optimized result End with walker standing on Node u. */
    public void optimizeddfswalk(FindState state, ArrayList<Long> visited) {

        long u= state.currentLoc();
        if (state.distanceToOrb() == 0) return;

        visited.add(u);

        Heap<NodeStatus> sortedneighbor= sort(state.neighbors());

        while (sortedneighbor.size() != 0) {
            NodeStatus w= sortedneighbor.poll();
            long v= w.getId();
            if (visited.contains(v) != true) {
                state.moveTo(v);
                optimizeddfswalk(state, visited);
                if (state.distanceToOrb() == 0) return;
                state.moveTo(u);
            }
        }
    }

    public Heap<NodeStatus> sort(Collection<NodeStatus> neigh) {
        Heap<NodeStatus> sortedneigh= new Heap<>(false);
        for (NodeStatus w : neigh) {
            sortedneigh.add(w, w.getDistanceToTarget());
        }
        return sortedneigh;

    }

    /** Pres Pollack is standing at a node given by parameter state.<br>
     *
     * Get out of the cavern before the ceiling collapses, trying to collect as <br>
     * much gold as possible along the way. Your solution must ALWAYS get out <br>
     * before time runs out, and this should be prioritized above collecting gold.
     *
     * You now have access to the entire underlying graph, which can be accessed <br>
     * through parameter state. <br>
     * state.currentNode() and state.getExit() will return Node objects of interest, and <br>
     * state.allNodes() will return a collection of all nodes on the graph.
     *
     * The cavern will collapse in the number of steps given by <br>
     * state.stepsLeft(), and for each step this number is decremented by the <br>
     * weight of the edge taken. <br>
     * Use state.stepsLeft() to get the time still remaining, <br>
     * Use state.moveTo() to move to a destination node adjacent to your current node.<br>
     * Do not call state.grabGold(). Gold on a node is automatically picked up <br>
     * when the node is reached.<br>
     *
     * The method must return from this function while standing at the exit. <br>
     * Failing to do so before time runs out or returning from the wrong <br>
     * location will be considered a failed run.
     *
     * You will always have enough time to scram using the shortest path from the <br>
     * starting position to the exit, although this will not collect much gold. <br>
     * For this reason, using the shortest path method to calculate the shortest <br>
     * path to the exit is a good starting solution */
    @Override
    public void scram(ScramState state) {
        // TODO 2: scram

        gold(state);

        List<Node> shorty= Path.shortest(state.currentNode(), state.getExit());
        run(state, shorty);

    }

    /** Helper function for the Scram State. <br>
     * Runs the the path represented by the the list parameter shorty. <br>
     * Returns the node Martha is standing on when the function ends. <br>
     * In other words the last node in the list shorty */
    public Node run(ScramState state, List<Node> shorty) {
        Object[] u= shorty.toArray();
        for (int i= 0; i < u.length - 1; i++ ) {
            Node n= (Node) u[i];
            for (Node y : n.getNeighbors()) {
                if (y.equals(u[i + 1])) state.moveTo(y);

            }
        }
        return state.currentNode();
    }

    /** Collects gold by running the shortest path to each node with gold <br>
     * greater than 200 until the number of steps left is almost less than <br>
     * the shortest path to the exit from the current node */
    public void gold(ScramState state) {
        for (Node u : state.allNodes()) {
            if (u.getTile().gold() > 200) {
                List<Node> shorty= Path.shortest(state.currentNode(), u);
                if (state.stepsLeft() - Path.pathSum(shorty) <= Path
                    .pathSum(Path.shortest(u, state.getExit()))) {
                    break;
                }
                run(state, shorty);
            }
        }

    }
}