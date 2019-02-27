package collectiveDecisionMakingModel.Visualisation;

import java.awt.Color;

import collectiveDecisionMakingModel.EnumTypes.Decision;
import collectiveDecisionMakingModel.Models.Agent;
import repast.simphony.visualizationOGL2D.DefaultStyleOGL2D;

public class AgentStyle2D extends DefaultStyleOGL2D {

	@Override
	public Color getColor(Object object) {
		if(object instanceof Agent) {
			Agent agent = (Agent) object;
			if(agent.getDecision().equals(Decision.A)) {
				return Color.RED;
			}
			else {
				return Color.BLUE;
			}
		}
		else {
			return null;
		}
	}

}
