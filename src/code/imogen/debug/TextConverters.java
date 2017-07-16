package code.imogen.debug;

import java.util.Comparator;
import java.util.Set;
import java.util.stream.Collectors;

import code.imogen.impl.search.Answer;
import code.imogen.impl.search.Command;
import code.imogen.impl.search.OneOfQuestion;
import code.imogen.impl.search.Question;
import code.imogen.impl.substitution.Clause;
import code.imogen.impl.substitution.Section;

public class TextConverters {

	public static String questionToText(Question question) {
		if (question instanceof OneOfQuestion) {
			return String.format("%s {%s}",
					question.getText(),
					String.join(",", ((OneOfQuestion)question).options));
		} else if (question == Question.VOID) {
			return "IMOGEN interrogation is complete. /reset or /quit.";
		} else {
			return question.getText();
		}
	}
	
	public static Answer textToAnswer(String answer, Question question) {
		if (Command.isCommand(answer)) {
			return Command.makeCommand(answer);
		} else {
			return question.makeAnswer(answer);
		}
	}
	
	public static String toHtml(Set<Clause> clauses) {
		StringBuilder sb = new StringBuilder();
		sb.append("<html><body><h1>Particulars of Claim</h1>");
		int section = 0;
		int clauseNumber = 0;
		Section lastSection = null;
		String sep = "";
		for (Clause c : clauses.stream()
				.sorted(Comparator.comparing(Clause::getOrder)).collect(Collectors.toList())) {
			Section s = c.section;
			if (s != lastSection) {
				section++;
				lastSection = s;
				sb.append("<h2>").append(s.name).append("</h2>");
				sep = "";
			}
			sb.append(sep).append(c.toHtml(++clauseNumber));
			sep = "<br />";
		}
		sb.append("</body></html>");
		return sb.toString();		
	}
	
	public static String printClauses(Set<Clause> clauses) {
		StringBuilder sb = new StringBuilder();
		int section = 0;
		int clauseNumber = 0;
		Section lastSection = null;
		for (Clause c : clauses.stream()
				.sorted(Comparator.comparing(Clause::getOrder)).collect(Collectors.toList())) {
			Section s = c.section;
			if (s != lastSection) {
				section++;
				lastSection = s;
				sb.append(s.name).append("\n");
			}
			sb.append(c.toText(++clauseNumber))
			.append("\n");
		}
		return sb.toString();
	}
}
