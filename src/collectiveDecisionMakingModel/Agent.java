package collectiveDecisionMakingModel;

import java.util.HashMap;
import java.util.List;

import repast.simphony.query.space.grid.GridCell;
import repast.simphony.query.space.grid.GridCellNgh;
import repast.simphony.random.RandomHelper;
import repast.simphony.space.SpatialMath;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.continuous.NdPoint;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridPoint;

/**
 * @author Joey De Keyser
 *
 */
public class Agent {
	
	private ContinuousSpace<Agent> space;
	private Grid<Agent> grid;
	private AgentState state;
	private Mood mood;
	private int transmittingChance;
	private HashMap<Mood, Integer> moodWeights;
	
	public Agent(ContinuousSpace<Agent> space, Grid<Agent> grid) {
		this.space = space;
		this.grid = grid;
		this.state = AgentState.MOVING;
		this.mood = Mood.RATIONAL;
		this.transmittingChance = 50;
		this.moodWeights = new HashMap<Mood, Integer>();
	}
	
	public void step() {
		GridPoint pt, newpt;
		GridCellNgh<Agent> nghCreator;
		List<GridCell<Agent>> gridCells;
		switch(this.state) {
		case MOVING:
			pt = grid.getLocation(this);
			nghCreator = new GridCellNgh<Agent>(grid, pt, Agent.class, 1, 1);
			gridCells = nghCreator.getNeighborhood(true);
			newpt = gridCells.get(RandomHelper.nextIntFromTo(0, gridCells.size())).getPoint();
			this.move(newpt);
			break;
		case TRANSMITTING:
			pt = grid.getLocation(this);
			nghCreator = new GridCellNgh<Agent>(grid, pt, Agent.class, 2, 2);
			gridCells = nghCreator.getNeighborhood(false);
			for(GridCell<Agent> agent : gridCells) {
				//Communicate with agent
			}
			break;
		}
		if(RandomHelper.nextIntFromTo(0, 100)>this.transmittingChance) this.state = AgentState.TRANSMITTING;
		else this.state = AgentState.MOVING;
	}
	
	private void move(GridPoint pt) {
		if (!pt.equals(grid.getLocation(this))) {
			NdPoint myPoint = space.getLocation(this);
			NdPoint otherPoint = new NdPoint(pt.getX(), pt.getY());
			double angle = SpatialMath.calcAngleFor2DMovement(space, myPoint, otherPoint);
			space.moveByVector(this, 1, angle, 0);
			myPoint = space.getLocation(this);
			grid.moveTo(this, (int)myPoint.getX(), (int)myPoint.getY());
		}
	}
	
	public void receiveMessage() {
		
	}
	
	

}
