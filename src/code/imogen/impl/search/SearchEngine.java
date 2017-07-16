package code.imogen.impl.search;

public interface SearchEngine {

	boolean isResolved();
	Question getPendingQuestion();
	void acceptAnswer(Question question, Answer answer);
	FullState getResult();
	
}
