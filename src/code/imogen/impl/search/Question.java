package code.imogen.impl.search;

import java.util.Set;

public interface Question {

	String getText();
	Answer makeAnswer(String answer);
	String getAnswerText(Answer answer);
	String getExample();
	int getOrder();
	
	public static Question first(Set<Question> questions) {
		Question best = VOID;
		for (Question q : questions) {
			if (q.getOrder() < best.getOrder()) {
				best = q;
			}
		}
		return best;
	}
	
	public static final VoidQuestion VOID = new VoidQuestion();
	
	public static class VoidQuestion implements Question {

		@Override
		public String getText() {
			return "";
		}

		@Override
		public Answer makeAnswer(String answer) {
			return Answer.VOID;
		}

		@Override
		public String getAnswerText(Answer answer) {
			return "";
		}

		@Override
		public int getOrder() {
			return Integer.MAX_VALUE;
		}

		@Override
		public String getExample() {
			return null;
		}
		
	};
}
