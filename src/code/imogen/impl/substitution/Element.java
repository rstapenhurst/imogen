package code.imogen.impl.substitution;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import code.imogen.impl.search.Condition;
import code.imogen.impl.search.FullState;
import code.imogen.impl.search.Question;
import code.imogen.impl.search.SimpleQuestion;
import code.imogen.impl.search.SimpleQuestion.SimpleAnswer;
import code.imogen.impl.search.StateLabel;

public interface Element {
	
	boolean canResolve(FullState state);
	String evaluate(FullState state);
	Question getPendingQuestion(FullState state);

	public class ConditionalElement implements Element {
		
		private LinkedHashMap<Condition, Element> elements;
		
		public ConditionalElement(LinkedHashMap<Condition, Element> elements) {
			this.elements = elements;
		}

		@Override
		public boolean canResolve(FullState state) {
			for (Condition c : elements.keySet()) {
				if (c.test(state) && elements.get(c).canResolve(state)) {
					return true;
				}
			}
			return false;
		}

		@Override
		public String evaluate(FullState state) {
			for (Condition c : elements.keySet()) {
				if (c.test(state) && elements.get(c).canResolve(state)) {
					return elements.get(c).evaluate(state);
				}
			}
			return "";
		}

		@Override
		public Question getPendingQuestion(FullState state) {
			for (Condition c : elements.keySet()) {
				if (c.test(state)) {
					Element e = elements.get(c);
					if (e.canResolve(state)) {
						break;
					}
					Question q = e.getPendingQuestion(state);
					if (!state.answers.containsKey(q) && q != Question.VOID) {
						return q;
					}
				}
			}
			return Question.VOID;
		}
	}
	
	public class Literal implements Element {
		
		private String value;

		public Literal(String value) {
			this.value = value;
		}
		
		@Override
		public boolean canResolve(FullState state) {
			return true;
		}

		@Override
		public String evaluate(FullState state) {
			return value;
		}

		@Override
		public Question getPendingQuestion(FullState state) {
			return Question.VOID;
		}
		
	}
	
	public class LabelMap implements Element {
		
		private Map<StateLabel, String> map;
		
		public LabelMap(Map<StateLabel, String> map) {
			this.map = map;
		}

		@Override
		public boolean canResolve(FullState state) {
			for (StateLabel label : map.keySet()) {
				if (state.labels.contains(label)) {
					return true;
				}
			}
			return false;
		}

		@Override
		public String evaluate(FullState state) {
			for (StateLabel label : map.keySet()) {
				if (state.labels.contains(label)) {
					return map.get(label);
				}
			}
			throw new RuntimeException("This should never happen");
		}

		@Override
		public Question getPendingQuestion(FullState state) {
			return Question.VOID;
		}
		
	}
	
	public class AnswerElement implements Element {
		private final Question question;
		
		public AnswerElement(Question question) {
			this.question = question;
		}

		@Override
		public boolean canResolve(FullState state) {
			return state.answers.containsKey(question);
		}

		@Override
		public String evaluate(FullState state) {
			return question.getAnswerText(state.answers.get(question));
		}

		@Override
		public Question getPendingQuestion(FullState state) {
			if (!state.answers.containsKey(question)) {
				return question;
			}
			return Question.VOID;
		}
		
	}
	
}
