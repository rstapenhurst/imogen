package code.imogen.impl.search;

import java.util.List;

public class OneOfQuestion implements Question {

	private final int order;
	public final String question;
	public final List<String> options;

	public OneOfQuestion(
			int order,
			String question,
			List<String> options) {
		this.order = order;
		this.question = question;
		this.options = options;
	}

	@Override
	public int getOrder() {
		return order;
	}
	
	@Override
	public String getText() {
		return question;
	}

	@Override
	public Answer makeAnswer(String answer) {
		try {
			return new OneOfAnswer(Integer.parseInt(answer));
		} catch (NumberFormatException e) {
			for (int i = 0; i < options.size(); i++) {
				if (options.get(i).equals(answer)) {
					return new OneOfAnswer(i);
				}
				if (makeQuestionText(i).equals(answer)) {
					return new OneOfAnswer(i);
				}
			}
			return Answer.VOID;
		}
	}

	@Override
	public String getAnswerText(Answer answer) {
		return options.get(((OneOfAnswer)answer).selectedOption);
	}
	
	@Override
	public String getExample() {
		return null;
	}
	
	public String makeQuestionText(int index) {
		return String.format("[%d] %s", index+1, options.get(index));
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((question == null) ? 0 : question.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		OneOfQuestion other = (OneOfQuestion) obj;
		if (question == null) {
			if (other.question != null)
				return false;
		} else if (!question.equals(other.question))
			return false;
		return true;
	}



	public static class OneOfAnswer extends Answer {
		
		public final int selectedOption;
		
		public OneOfAnswer(int selectedOption) {
			this.selectedOption = selectedOption;
		}
	}
	
	public static class OneOfPredicate implements AnswerPredicate {
		
		private int requiredOption;

		public OneOfPredicate(int requiredOption) {
			this.requiredOption = requiredOption;
		}

		@Override
		public boolean apply(Question question, Answer answer) {
			return ((OneOfAnswer)answer).selectedOption == requiredOption;
		}
		
	}
	
}
