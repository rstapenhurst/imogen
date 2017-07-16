package code.imogen.impl.substitution;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import code.imogen.impl.search.Condition;
import code.imogen.impl.search.FullState;
import code.imogen.impl.search.Question;
import code.imogen.impl.search.SimpleQuestion;
import code.imogen.impl.search.SimpleQuestion.SimpleAnswer;

public class ClauseTemplate {

	public Section section;
	public boolean preformatted = false;
	private final Condition condition;
	private final List<Element> template;
	private final String subclauseIntro;
	private final String subclauseTemplate;
	private final boolean hasSubclause;
	
	private final int index;
	
	public ClauseTemplate(
			int index,
			Condition condition,
			List<Element> template) {
		this(index, condition, template, null, null);
	}
	
	public ClauseTemplate(int index, Element ... template) {
		this(index, Condition.always(), Arrays.asList(template));
	}
	
	public ClauseTemplate(
			int index,
			Condition condition,
			List<Element> template,
			String subclauseIntro,
			String subclauseTemplate) {
				this.index = index;
				this.condition = condition;
				this.template = template;
				this.subclauseIntro = subclauseIntro;
				this.subclauseTemplate = subclauseTemplate;
				hasSubclause = subclauseIntro != null;
	}
	
	public boolean canResolve(FullState state) {
		if (!condition.test(state)) {
			return false;
		}
		for (Element e : template) {
			if (!e.canResolve(state)) {
				return false;
			}
		}
		if (hasSubclause) {
			int place = 1;
			while (true) {
				SimpleQuestion q = generateSubclauseQuestion(place);
				if (!state.answers.containsKey(q)) {
					return false;
				}
				if (((SimpleAnswer)state.answers.get(q)).getAnswerText().equals("done")) {
					break;
				}
				place++;
			}
		}
		return true;
	}
	
	private Map<Integer, SimpleQuestion> cache = new HashMap<>();
	
	private SimpleQuestion generateSubclauseQuestion(int place) {
		if (!cache.containsKey(place)) {
			String suffix = "th";
			if (place % 10 == 1 && place % 100 != 11) {
				suffix = "st";
			}
			if (place % 10 == 2 && place % 100 != 12) {
				suffix = "nd";
			}
			if (place % 10 == 3 && place % 100 != 13) {
				suffix = "th";
			}
			if (place == 1) {
				cache.put(place, new SimpleQuestion(subclauseIntro + "\n" +
						subclauseTemplate.replace("<N>", place + suffix)));
			} else {
				cache.put(place, new SimpleQuestion(subclauseTemplate.replace("<N>", place + suffix)));
			}
		}
		return cache.get(place);
	}
	
	/**
	 * Only return a non-void question if the clause is otherwise resolvable.
	 */
	public Question getPendingQuestion(FullState state) {
		Question result = Question.VOID; 
		if (condition.test(state)) {
			for (Element e : template) {
				Question elementQuestion = e.getPendingQuestion(state);
				if (elementQuestion == Question.VOID) {
					if (!e.canResolve(state)) {
						return Question.VOID;
					}
				} else {
					result = elementQuestion;
				}
			}
			if (result == Question.VOID && hasSubclause) {
				int place = 1;
				while (true) {
					SimpleQuestion q = generateSubclauseQuestion(place);
					if (!state.answers.containsKey(q)) {
						return q;
					}
					if (((SimpleAnswer)state.answers.get(q)).getAnswerText().equals("done")) {
						break;
					}
					place++;
				}
			}
		}
		return result;
	}
	
	public Clause evaluate(FullState state) {
		StringBuilder fullText = new StringBuilder();
		for (Element e : template) {
			fullText.append(e.evaluate(state));
		}
		List<String> subclauses = new ArrayList<>();
		if (hasSubclause) {
			int place = 1;
			while (true) {
				SimpleQuestion q = generateSubclauseQuestion(place);
				if (!state.answers.containsKey(q)) {
					throw new RuntimeException("Can't evaluate this");
				}
				SimpleAnswer a = (SimpleAnswer)state.answers.get(q);
				if (a.getAnswerText().equals("done")) {
					break;
				}
				subclauses.add(a.getAnswerText());
				place++;
			}
		}
		if (preformatted) {
			return new PreformattedClause(section, index, fullText.toString());
		} else {
			return new Clause(section, index, fullText.toString(), subclauses);
		}
	}
	
}
