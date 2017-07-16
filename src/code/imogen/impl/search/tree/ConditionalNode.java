package code.imogen.impl.search.tree;

import java.util.HashSet;
import java.util.Set;

import code.imogen.impl.search.Condition;
import code.imogen.impl.search.FullState;
import code.imogen.impl.search.Question;
import code.imogen.impl.search.StateLabel;

public class ConditionalNode implements TreeNode {

	private Condition condition;
	TreeNode child;

	public ConditionalNode(Condition condition, TreeNode child) {
		this.condition = condition;
		this.child = child;
		
	}

	@Override
	public Set<Question> getQuestions(FullState state) {
		return new HashSet<>();
	}
	
	@Override
	public boolean shouldAdvance(FullState state) {
		return condition.test(state);
	}

	@Override
	public TreeNode next(FullState state) {
		return child;
	}

	@Override
	public Set<StateLabel> getLabels() {
		return new HashSet<>();
	}
	
}
