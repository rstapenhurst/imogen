package code.imogen.impl.search.tree;

import java.util.HashSet;
import java.util.Set;

import code.imogen.impl.search.FullState;
import code.imogen.impl.search.Question;
import code.imogen.impl.search.StateLabel;

public class LabelTreeNode implements TreeNode {
	
	private final Set<StateLabel> labels;
	
	public LabelTreeNode(
			Set<StateLabel> labels) {
		this.labels = labels;
	}

	@Override
	public Set<Question> getQuestions(FullState state) {
		return new HashSet<>();
	}
	
	@Override
	public TreeNode next(FullState state) {
		return TreeNode.INVALID;
	}

	@Override
	public Set<StateLabel> getLabels() {
		return labels;
	}

	@Override
	public boolean shouldAdvance(FullState state) {
		return false;
	}
	
}
