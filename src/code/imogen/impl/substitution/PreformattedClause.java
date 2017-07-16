package code.imogen.impl.substitution;

public class PreformattedClause extends Clause {

	public PreformattedClause(Section section, int index, String text) {
		super(section, index, text);
	}

	public int getOrder() {
		return index;
	}
	
	@Override
	public String toString() {
		return toText(index);
	}
	
	public String toText(int clause) {
		return text;
	}
	
	public String toHtml(int clause) {
		return text;
	}

	
}
