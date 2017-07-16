package code.imogen.impl.search.tree;

import java.util.HashSet;
import java.util.Set;

import code.imogen.impl.search.Answer;
import code.imogen.impl.search.FullState;
import code.imogen.impl.search.Question;
import code.imogen.impl.search.StateLabel;
import code.imogen.impl.search.Question.VoidQuestion;

public interface TreeNode {

	Set<Question> getQuestions(FullState state);
	TreeNode next(FullState state);
	Set<StateLabel> getLabels();
	boolean shouldAdvance(FullState state);
		
	public static final TreeNode INVALID = new TreeNode() {
			@Override
			public TreeNode next(FullState state) {
				return this;
			}

			@Override
			public Set<Question> getQuestions(FullState state) {
				return new HashSet<>();
			}

			@Override
			public Set<StateLabel> getLabels() {
				return new HashSet<>();
			}

			@Override
			public boolean shouldAdvance(FullState state) {
				return false;
			}
		};


}