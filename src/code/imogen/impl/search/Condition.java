package code.imogen.impl.search;

import java.util.Arrays;
import java.util.function.Predicate;

@FunctionalInterface
public interface Condition extends Predicate<FullState> {
	
	public static Condition always() {
		return (state) -> true;
	}
	
	public static Condition labels(StateLabel ... labels) {
		return (state) -> state.labels.containsAll(Arrays.asList(labels)); 
	}
	
	public static Condition and(Condition ... conditions) {
		return (state) -> {
			for (Condition c : conditions) {
				if (!c.test(state)) {
					return false;
				}
			}
			return true;
		};
	}
	
	public static Condition or(Condition ... conditions) {
		return (state) -> {
			for (Condition c : conditions) {
				if (c.test(state)) {
					return true;
				}
			}
			return false;
		};
	}
}
