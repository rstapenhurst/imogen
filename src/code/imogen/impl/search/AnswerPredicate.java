package code.imogen.impl.search;

public interface AnswerPredicate {

	boolean apply(Question question, Answer answer);
	
}
