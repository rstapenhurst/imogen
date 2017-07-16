package code.imogen.impl.substitution;

import java.util.ArrayList;
import java.util.List;

public class Clause {

	public final Section section;
	public final int index;
	public final String text;
	public final List<String> subclauses;
	
	public Clause(Section section, int index, String text, List<String> subclauses) {
		this.section = section;
		this.index = index;
		this.text = text;
		this.subclauses = subclauses;
	}
	
	public Clause(Section section, int index, String text) {
		this(section, index, text, new ArrayList<>());
	}
	
	public int getOrder() {
		return index;
	}
	
	@Override
	public String toString() {
		return toText(index);
	}
	
	public String toText(int clause) {
		StringBuilder sb = new StringBuilder();
		sb.append(clause).append(". ").append(text);
		for (int i = 0; i < subclauses.size(); i++) {
			sb.append("\n").append(clause).append(".").append(i+1).append(". ").append(subclauses.get(i));
		}
		return sb.toString();
	}
	
	public String toHtml(int clause) {
		StringBuilder sb = new StringBuilder();
		sb.append(clause).append(". ").append(text);
		for (int i = 0; i < subclauses.size(); i++) {
			sb.append("<br />").append(clause).append(".").append(i+1).append(". ").append(subclauses.get(i));
		}
		return sb.toString();		
	}
}
