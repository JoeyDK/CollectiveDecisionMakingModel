package collectiveDecisionMakingModel.Models;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import collectiveDecisionMakingModel.EnumTypes.Decision;
import collectiveDecisionMakingModel.EnumTypes.Mood;

/**
 * @author Joey De Keyser
 *
 */
public class Message {
	
	private int ID;
	private static AtomicInteger ID_GENERATOR = new AtomicInteger(1);
	private Mood mood;
	private Decision decision;
	
	public Message(Mood mood, Decision decision) {
		this.ID = ID_GENERATOR.getAndIncrement();
		this.mood = mood;
		this.decision = decision;
	}
	
	public int getID() {
		return this.ID;
	}
	
	public List<Object> getMessage() {
		List<Object> result = new ArrayList<Object>();
		result.add(this.mood);
		result.add(this.decision);
		return result;
	}
	
	public void resetMessage(Mood mood, Decision decision) {
		this.mood = mood;
		this.decision = decision;
	}
	
	
	
}
