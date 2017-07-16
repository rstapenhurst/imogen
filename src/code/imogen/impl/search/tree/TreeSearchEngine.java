package code.imogen.impl.search.tree;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import code.imogen.debug.TextConverters;
import code.imogen.impl.search.Answer;
import code.imogen.impl.search.FullState;
import code.imogen.impl.search.Question;
import code.imogen.impl.search.SearchEngine;
import code.imogen.impl.substitution.Clause;
import code.imogen.impl.substitution.ClauseTemplate;
import code.imogen.impl.substitution.Section;

public class TreeSearchEngine implements SearchEngine {
	
	private final FullState state = new FullState();
	private Set<TreeNode> pendingNodes = new HashSet<>();
	private Set<Question> pendingClauseQuestions = new HashSet<>();
	private Set<ClauseTemplate> clauseTemplates;
	private final List<Section> sections;
	private int currentSection = -1;

	public TreeSearchEngine(List<Section> sections) {
		this.sections = sections;
		expandSection();
	}
	
	private void expandSection() {
		currentSection++;
		System.out.println("Starting section " + currentSection);
		Section current = sections.get(currentSection);
		this.pendingNodes = current.nodes;
		this.clauseTemplates = current.clauses;
		while (evaluateTrees());
		evaluateClauses();
	}
	
	private void evaluateClauses() {
		Set<ClauseTemplate> resolvable = new HashSet<>();
		for (ClauseTemplate template : clauseTemplates) {
			if (template.canResolve(state)) {
				resolvable.add(template);
			} else {
				Question nextQuestion = template.getPendingQuestion(state);
				if (nextQuestion != Question.VOID) {
					pendingClauseQuestions.add(nextQuestion);
				}
			}
		}
		for (ClauseTemplate template : resolvable) {
			clauseTemplates.remove(template);
			state.clauses.add(template.evaluate(state));
		}
	}
	
	public boolean sectionIsResolved() {
		return getPendingQuestion() == Question.VOID;
	}

	@Override
	public boolean isResolved() {
		return currentSection == sections.size()-1 && sectionIsResolved();
	}

	@Override
	public FullState getResult() {
		return state;
	}
	
	@Override
	public Question getPendingQuestion() {
		if (!pendingNodes.isEmpty()) {
			for (TreeNode node : pendingNodes) {
				if (node.getQuestion(state) != Question.VOID) {
					return node.getQuestion(state);
				}
			}
		}
		if (!pendingClauseQuestions.isEmpty()) {
			return pendingClauseQuestions.iterator().next();
		}
		return Question.VOID;
	}

	@Override
	public void acceptAnswer(Question question, Answer answer) {
		state.answers.put(question, answer);
		while (evaluateTrees()); // Repeat!
		acceptAnswerInClauses(question, answer);
		evaluateClauses();
		if (sectionIsResolved() && !isResolved()) {
			expandSection();
		}
		printState();
	}
	
	private void acceptAnswerInClauses(Question question, Answer answer) {
		pendingClauseQuestions.remove(question);
	}
	
	private boolean evaluateTrees() {
		Set<TreeNode> satisfiedNodes = new HashSet<>();
		for (TreeNode node : pendingNodes) {
			if (node.shouldAdvance(state)) {
				satisfiedNodes.add(node);
			}
			if (node instanceof LabelTreeNode) {
				satisfiedNodes.add(node);
			}
		}
		for (TreeNode node : satisfiedNodes) {
			pendingNodes.remove(node);
			if (node.shouldAdvance(state)) {
				TreeNode next = node.next(state);
				pendingNodes.add(next);
				state.labels.addAll(next.getLabels());
			}
		}
		return !satisfiedNodes.isEmpty();
	}
	
	public void printState() {
		System.out.println("Active labels:" + state.labels);
		System.out.println("Resolved clauses:\n" + TextConverters.printClauses(state.clauses));
	}
	
}
