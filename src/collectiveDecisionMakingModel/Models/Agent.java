package collectiveDecisionMakingModel.Models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import collectiveDecisionMakingModel.EnumTypes.AgentState;
import collectiveDecisionMakingModel.EnumTypes.Decision;
import collectiveDecisionMakingModel.EnumTypes.Mood;
import repast.simphony.engine.schedule.ScheduledMethod;
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
	
	static final int MAX_MESSAGES = 5;
	
	private ContinuousSpace<Agent> space;
	private Grid<Agent> grid;
	
	private AgentState state;
	private Mood mood;
	private Decision decision;
	private Message message;
	
	private int transmittingChance;
	static final int MAX_TRANSMITTING_CHANCE = 70;
	static final int MIN_TRANSMITTING_CHANCE = 30;
	
	private HashMap<Mood, Double> moodWeights;
	static final double MAX_MOOD_WEIGHT = 1.5;
	static final double MIN_MOOD_WEIGHT = 0.5;
	
	private List<Message> receivedMessages;
	
	public Agent(ContinuousSpace<Agent> space, Grid<Agent> grid, Mood mood, Decision decision) {
		this.space = space;
		this.grid = grid;
		
		this.state = AgentState.MOVING;
		this.mood = mood;
		this.decision = decision;
		
		this.transmittingChance = 50;
		this.moodWeights = new HashMap<>();
		
		this.receivedMessages = new ArrayList<>();
	}
	
	@ScheduledMethod(start = 1, interval = 1)
	public void step() {
		GridPoint pt, newpt;
		GridCellNgh<Agent> nghCreator;
		List<GridCell<Agent>> gridCells;
		switch(this.state) {
		case MOVING:
			pt = grid.getLocation(this);
			nghCreator = new GridCellNgh<Agent>(grid, pt, Agent.class, 1, 1);
			gridCells = nghCreator.getNeighborhood(true);
			newpt = gridCells.get(RandomHelper.nextIntFromTo(0, gridCells.size()-1)).getPoint();
			this.move(newpt);
			break;
		case TRANSMITTING:
			pt = grid.getLocation(this);
			nghCreator = new GridCellNgh<Agent>(grid, pt, Agent.class, 2, 2);
			gridCells = nghCreator.getNeighborhood(false);
			for(GridCell<Agent> cell : gridCells) {
				for(Agent agent : cell.items()) {
					agent.receiveMessage(this.message);
				}
			}
			break;
		}
		this.updateState();
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
	
	//Update state of the agent (moving or sending)
	private void updateState() {
		if(RandomHelper.nextIntFromTo(0, 99)<this.transmittingChance) {
			this.message = new Message(this.mood, this.decision);
			this.state = AgentState.TRANSMITTING;
		}
		else {
			this.state = AgentState.MOVING;
		}
	}
	
	//Called by other agents when they send a message that is receivable by you
	public void receiveMessage(Message m) {
		boolean alreadyReceived = false;
		int count = 0;
		while(!alreadyReceived && count < this.receivedMessages.size()) {
			if(this.receivedMessages.get(count++).getID()==m.getID()) alreadyReceived = true;
		}
		if(!alreadyReceived) {
			this.receivedMessages.add(m);
			if(this.receivedMessages.size() == MAX_MESSAGES) this.processMessages();
		}
	}
	
	//Majority Rule => THIS IS IMPORTANT FOR THE THESIS
	private void processMessages() {
		//Read all the received messages
		HashMap<Decision,Double> majorityMap = new HashMap<>();
		for(Message m : this.receivedMessages) {
			Mood mood = (Mood)m.getMessage().get(0);
			Decision decision = (Decision)m.getMessage().get(1);
			if(!majorityMap.containsKey(decision)) majorityMap.put(decision, 0.0);
			if(!this.moodWeights.containsKey(mood)) this.moodWeights.put(mood, 1.0);
			majorityMap.put(decision, majorityMap.get(decision)+this.moodWeights.get(mood));
		}
		//Find the winning vote
		Decision winner = this.decision;
		Double winningCount = -1.0;
		for(Map.Entry<Decision, Double> entry : majorityMap.entrySet()) {
			if(entry.getValue() > winningCount) winner = entry.getKey();
		}
		/*
		//Update mood weights
		for(Message m : this.receivedMessages) {
			Mood mood = (Mood)m.getMessage().get(0);
			Decision decision = (Decision)m.getMessage().get(1);
			if(decision.equals(winner)) {
				if (this.moodWeights.get(mood) < Agent.MAX_MOOD_WEIGHT) this.moodWeights.put(mood, this.moodWeights.get(mood)+0.01);
			}
			else {
				if (this.moodWeights.get(mood) > Agent.MIN_MOOD_WEIGHT) this.moodWeights.put(mood, this.moodWeights.get(mood)-0.01);
			}
		}
		*/
		//Adjust transmitting chance to promote correct decisions
		if(this.decision.equals(winner)) {
			if (this.transmittingChance < Agent.MAX_TRANSMITTING_CHANCE) this.transmittingChance++;
		}
		else {
			if(this.transmittingChance > Agent.MIN_TRANSMITTING_CHANCE) this.transmittingChance--;
		}
		this.decision = winner;
		this.receivedMessages.clear();
	}
	
	public Decision getDecision() {
		return this.decision;
	}

}
