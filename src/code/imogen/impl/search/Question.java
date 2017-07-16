package code.imogen.impl.search;

public interface Question {

	String getText();
	Answer makeAnswer(String answer);
	String getAnswerText(Answer answer);
	
	public static final VoidQuestion VOID = new VoidQuestion();
	
	public static class VoidQuestion implements Question {

		@Override
		public String getText() {
			return "";
		}

		@Override
		public Answer makeAnswer(String answer) {
			return Answer.voidAnswer();
		}

		@Override
		public String getAnswerText(Answer answer) {
			return "";
		}
		
	};
}
