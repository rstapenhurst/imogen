package code.imogen.impl.substitution;

import java.util.Set;

import code.imogen.impl.search.tree.TreeNode;

public class Section {

	public final String name;
	public final int index;
	public final Set<TreeNode> nodes;
	public final Set<ClauseTemplate> clauses;
	
	public Section(String name, int index, Set<TreeNode> nodes, Set<ClauseTemplate> clauses) {
		this.name = name;
		this.index = index;
		this.nodes = nodes;
		this.clauses = clauses;
		for (ClauseTemplate clause : clauses) {
			clause.section = this;
		}
	}
	
}
