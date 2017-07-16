package code.imogen.impl.search;

public class SimpleQuestion implements Question {
	
	private String text;

	public SimpleQuestion(String text) {
		this.text = text;
	}

	@Override
	public String getText() {
		return text;
	}

	@Override
	public Answer makeAnswer(String answer) {
		return new SimpleAnswer(answer);
	}

	@Override
	public String getAnswerText(Answer answer) {
		return ((SimpleAnswer)answer).getAnswerText();
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((text == null) ? 0 : text.hashCode());
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
		SimpleQuestion other = (SimpleQuestion) obj;
		if (text == null) {
			if (other.text != null)
				return false;
		} else if (!text.equals(other.text))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return text;
	}

	public static class SimpleAnswer extends Answer {
	
		private final String answerText;

		public SimpleAnswer(String answerText) {
			this.answerText = answerText;
		}

		public String getAnswerText() {
			return answerText;
		}
	}

}
