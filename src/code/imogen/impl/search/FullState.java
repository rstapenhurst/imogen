package code.imogen.impl.search;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import code.imogen.impl.substitution.Clause;

public class FullState {
	
	public final Set<StateLabel> labels = new HashSet<>();
	public final Set<Clause> clauses = new HashSet<>();
	public final Map<Question, Answer> answers = new HashMap<>();

	public FullState() {
		
	}
	
}
