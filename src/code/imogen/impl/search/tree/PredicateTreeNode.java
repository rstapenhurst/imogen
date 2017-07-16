package code.imogen.impl.search.tree;

import java.util.Map;
import java.util.Set;

import code.imogen.impl.search.Answer;
import code.imogen.impl.search.AnswerPredicate;
import code.imogen.impl.search.FullState;
import code.imogen.impl.search.Question;
import code.imogen.impl.search.StateLabel;

public class PredicateTreeNode implements TreeNode {
	
	public final Question question;
	public final Map<? extends AnswerPredicate, ? extends TreeNode> predicates;
	public final Set<StateLabel> labels;

	public PredicateTreeNode(
			Question question,
			Map<? extends AnswerPredicate, TreeNode> predicates,
		    Set<StateLabel> labels) {
		this.question = question;
		this.predicates = predicates;
		this.labels = labels;
	}

	@Override
	public Question getQuestion(FullState state) {
		return question;
	}
	
	@Override
	public TreeNode next(FullState state) {
		Answer answer = state.answers.get(question);
		for (AnswerPredicate predicate : predicates.keySet()) {
			if (predicate.apply(question, answer)) {
				return predicates.get(predicate);
			}
		}
		return this;
	}

	@Override
	public Set<StateLabel> getLabels() {
		return labels;
	}

	@Override
	public boolean shouldAdvance(FullState state) {
		return state.answers.containsKey(question);
	}
	
}
