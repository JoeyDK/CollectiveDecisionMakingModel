package collectiveDecisionMakingModel.ContextBuilding;
import java.util.ArrayList;
import java.util.List;

import collectiveDecisionMakingModel.EnumTypes.Decision;
import collectiveDecisionMakingModel.EnumTypes.Mood;
import collectiveDecisionMakingModel.Models.Agent;
import repast.simphony.context.Context;
import repast.simphony.context.space.continuous.ContinuousSpaceFactory;
import repast.simphony.context.space.continuous.ContinuousSpaceFactoryFinder;
import repast.simphony.context.space.grid.GridFactory;
import repast.simphony.context.space.grid.GridFactoryFinder;
import repast.simphony.dataLoader.ContextBuilder;
import repast.simphony.random.RandomHelper;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.continuous.NdPoint;
import repast.simphony.space.continuous.RandomCartesianAdder;
import repast.simphony.space.continuous.WrapAroundBorders;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridBuilderParameters;
import repast.simphony.space.grid.SimpleGridAdder;

public class SimulationBuilder implements ContextBuilder<Agent> {

	@Override
	public Context<Agent> build(Context<Agent> context) {
		context.setId("CollectiveDecisionMakingModel");
		
		ContinuousSpaceFactory spaceFactory = ContinuousSpaceFactoryFinder.createContinuousSpaceFactory(null);
		ContinuousSpace<Agent> space = spaceFactory.createContinuousSpace("space", context, 
				new RandomCartesianAdder<Agent>(), 
				new WrapAroundBorders(), 50, 50);
		GridFactory gridFactory = GridFactoryFinder.createGridFactory(null);
		Grid<Agent> grid = gridFactory.createGrid("grid", context,
				new GridBuilderParameters<Agent>(
						new repast.simphony.space.grid.WrapAroundBorders(),
						new SimpleGridAdder<Agent>(),
						true, 50, 50));
		
		List<Decision> decisions = new ArrayList<>();
				
		int agentCountA = 150;
		for (int i=0; i<agentCountA; i++) {
			decisions.add(Decision.A);
		}
		
		int agentCountB = 150;
		for (int i=0; i<agentCountB; i++) {
			decisions.add(Decision.B);
		}
		
		shuffle(1, context, space, grid, decisions);
		
		for (Agent agent : context) {
			NdPoint pt = space.getLocation(agent);
			grid.moveTo(agent, (int)pt.getX(), (int)pt.getY());
		}
		
		return context;
	}
	
	private void shuffle(int iterations, Context<Agent> context, ContinuousSpace<Agent> space, Grid<Agent> grid, List<Decision> decisions) {
		for(int i=0; i<iterations; i++) {
			for(int j=decisions.size()-1; j>=0; j--) {
				Decision d = decisions.get(RandomHelper.nextIntFromTo(0, j));
				context.add(new Agent(space, grid, Mood.RATIONAL, d));
				decisions.remove(d);
			}
		}
	}
	
}
