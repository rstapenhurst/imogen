package code.imogen.debug;

import static code.imogen.impl.search.Condition.and;
import static code.imogen.impl.search.Condition.or;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import code.imogen.impl.search.AnswerPredicate;
import code.imogen.impl.search.Condition;
import code.imogen.impl.search.OneOfQuestion;
import code.imogen.impl.search.OneOfQuestion.OneOfPredicate;
import code.imogen.impl.search.Question;
import code.imogen.impl.search.SimpleQuestion;
import code.imogen.impl.search.StateLabel;
import code.imogen.impl.search.tree.ConditionalNode;
import code.imogen.impl.search.tree.LabelTreeNode;
import code.imogen.impl.search.tree.PredicateTreeNode;
import code.imogen.impl.search.tree.TreeNode;
import code.imogen.impl.search.tree.TreeSearchEngine;
import code.imogen.impl.substitution.ClauseTemplate;
import code.imogen.impl.substitution.Element;
import code.imogen.impl.substitution.Element.AnswerElement;
import code.imogen.impl.substitution.Element.ConditionalElement;
import code.imogen.impl.substitution.Element.LabelMap;
import code.imogen.impl.substitution.Element.Literal;
import code.imogen.impl.substitution.Section;

public class TreeSearchEngineBuilder {
	
	public static int nextIndex = 1;

	public static TreeSearchEngine build() {
		
		Set<TreeNode> trees = new HashSet<>();
		Set<ClauseTemplate> clauses = new HashSet<>();
		List<Section> sections = new ArrayList<>();
/*
		StringBuilder sb = new StringBuilder();		
		try (Scanner sc = new Scanner(TreeSearchEngineBuilder.class.getResourceAsStream("/resources/template"))) {
			while (sc.hasNext()) {
				sb.append(sc.nextLine()).append("\n");
			}
		}
		ClauseTemplate header = ClauseBuilder.newBuilder()
				.then(sb.toString(), q("asd", "qwe"))
				.build();
		header.preformatted = true;
		clauses.add(header);
		sections.add(new Section(0, new HashSet<>(), clauses));
		clauses = new HashSet<>();/**/
		
		trees.add(QuestionNode.of(
				"Are you bringing a claim in your capacity as an individual or a company?")
				.option("Individual", "claimant_is_individual")
				.option("Company", "claimant_is_company")
				.build());
		trees.add(QuestionNode.of(
				"Do you want to bring a claim against a company or individual?")
				.option("Individual", "defendant_is_individual")
				.option("Company", "defendant_is_company")
				.build());
		trees.add(QuestionNode.of(
				"Are you claiming against your employer?")
				.option("Yes", "defendant_is_employer", "claimant_employed")
				.option("No", "defendant_is_not_employer")
				.build(is("claimant_is_individual", "defendant_is_company")));
		trees.add(LabelNode.of("ask_employer")
				.build(and(
						is("claimant_is_individual"),
						or(is("defendant_is_individual"), is("defendant_is_not_employer")))));
		trees.add(QuestionNode.of(
				"Which of the following best describes your employment situation at the time of incident?")
				.option("Employed", "claimant_employed")
				.option("Unemployed", "claimant_unemployed")
				.option("Self-employed", "claimant_self_employed")
				.build(is("ask_employer")));
		
		
		Element claimantName = cond(
				is("claimant_is_individual"), q("What is your full name?", "John Smith"),
				is("claimant_is_company"), q("What is the claiming company's name?", "Business Corp Ltd"));
		Element claimantOccupation = q("What is your occupation?", "builder");
		Element claimantAddress = q("What is your address?", "10 High Road, London, N1 2AB");
		Element claimantEmployer = q("What is the name of your employer?", "Southern Plumbers");
		Element defendantName = cond(
				is("defendant_is_employer"), claimantEmployer,
				or(is("claimant_is_company"), is("defendant_is_individual"), is("defendant_is_not_employer")), cond(
				is("defendant_is_individual"), q("What is the name of the person you want to bring the claim against?", "Fraser Donald"),
				is("defendant_is_company"), q("What is the name of the company you want to bring the claim against?", "Great Taxis")));
		Element defendantAddress = cond(
				is("defendant_is_individual"), q("What is address of the person you want to bring the claim against?", "25 Mill Lane, Manchester, M12 3DE"),
				is("defendant_is_company"), q("What is address of the company you want to bring the claim against?", "40 Mill Lane, Manchester, M12 3DE"));
		Element defendantBusiness = cond(
				is("defendant_is_individual"), q("What the nature of the business of the person you want to bring a claim against?", "textiles"),
				is("defendant_is_company"), q("What the nature of the business of the company you want to bring a claim against?", "taxi services"));
		
		clauses.add(ClauseBuilder.newBuilder()
				.then("The Claimant was at all material times ", claimantName, " of ", claimantAddress,
						", and their self-employed occuptation is ", claimantOccupation, ".")
				.build(is("claimant_self_employed")));
		clauses.add(ClauseBuilder.newBuilder()
				.then("The Claimant was at all material times ", claimantName, " of ", claimantAddress,
						", and is employed by ", claimantEmployer, " as a ", claimantOccupation, ".")
				.build(is("claimant_employed")));
		clauses.add(ClauseBuilder.newBuilder()
				.then("The Claimant was at all material times ", claimantName, " of ", claimantAddress, ", and is unemployed.")
				.build(is("claimant_unemployed")));
		
		clauses.add(ClauseBuilder.newBuilder()
				.then("The Defendant was at all material times ", defendantName, " of ", defendantAddress,
						", the employer of the Claimant, operating in the business of ", defendantBusiness, ".")
				.build(is("defendant_is_employer")));
		clauses.add(ClauseBuilder.newBuilder()
				.then("The Defendant was at all material times ", defendantName, " of ", defendantAddress,
						", operating in the business of ", defendantBusiness, ".")
				.build(is("defendant_is_company", "defendant_is_not_employer")));
		clauses.add(ClauseBuilder.newBuilder()
				.then("The Defendant was at all material times ", defendantName, " of ", defendantAddress, ".")
				.build(is("defendant_is_individual")));
		sections.add(new Section("Parties", 1, trees, clauses));
		trees = new HashSet<>();
		clauses = new HashSet<>();
		
		trees.add(QuestionNode.of(
				"What is the nature of the agreement within which you are making the claim?")
				.option("Written agreement", "contract")
				.option("Verbal agreement", "oral")
				.build());
		trees.add(QuestionNode.of(
				"With whom did you make the agreement?")
				.option("The company", "agreement_with_company")
				.option("An employee of the company", "agreement_with_employee")
				.build(is("contract", "defendant_is_company")));
		
		Element agreementDate = q("When did you make the agreement?", "10 June 2001");
		Element agreementLocation = q("Where was the agreement made?", "6 Union Street, London, SW1 9AB");
		Element agreementPurpose = q("What was the purpose of the agreement?", "arranging a routine car repair");
		Element claimantObligations = q("What did you promise to do as your part in the agreement?", "repair a car");
		Element defendantObligations = q("What did the Defendant promise to do as their part in the agreement?", "pay parts & labour for the repair");
		Element companyRepresentative = q("What is the name of the company employee you made the agreement with?", "Craig Summers");
		
		clauses.add(ClauseBuilder.newBuilder()
				.then("There was a written contract between the Claimant and the Defendant made on ", agreementDate, " at ",
						agreementLocation, " for the purpose of ", agreementPurpose, ". The Claimant agreed ", claimantObligations,
						" and the Defendant agreed ", defendantObligations, ".")
				.build(and(is("contract"), or(is("defendant_is_individual"), is("agreement_with_company")))));
		clauses.add(ClauseBuilder.newBuilder()
				.then("There was a written contract between the Claimant and the ", companyRepresentative,
						" (acting on behalf of the Defendant) made on ", agreementDate, " at ", agreementLocation, " for the purpose of ",
						agreementPurpose, ". The Claimant agreed ", claimantObligations, " and the Defendant agreed ", defendantObligations, ".")
				.build(is("contract", "defendant_is_company", "agreement_with_employee")));
		clauses.add(ClauseBuilder.newBuilder()
				.then("There was an oral agreement between the Claimant and the Defendant made on ", agreementDate, " at ",
						agreementLocation, " for the purpose of ", agreementPurpose, ". The Claimant agreed ", claimantObligations,
						" and the Defendant agreed ", defendantObligations, ".")
				.build(is("oral", "defendant_is_individual")));
		clauses.add(ClauseBuilder.newBuilder()
				.then("There was an oral agreement between the Claimant and the ", companyRepresentative,
						" (acting on behalf of the Defendant) made on ", agreementDate, " at ", agreementLocation, " for the purpose of ",
						agreementPurpose, ". The Claimant agreed ", claimantObligations, " and the Defendant agreed ", defendantObligations, ".")
				.build(is("oral", "defendant_is_company")));/**/
		sections.add(new Section("The Contract", 2, trees, clauses));
		trees = new HashSet<>();
		clauses = new HashSet<>();
		
		trees.add(QuestionNode.of(
				"Were there any terms in the contract that related directly to the incident?")
				.option("Yes", "relevant_express_terms")
				.option("No", "no_relevant_express_terms")
				.build(is("contract")));
		trees.add(LabelNode.of("no_relevant_express_terms")
				.build(is("oral")));
		trees.add(QuestionNode.of(
				"Were there any terms that were not written down but related to the incident (customs, industry norms, etc.)?")
				.option("Yes", "relevant_implied_terms")
				.option("No", "no_relevant_implied_terms")
				.build(is("contract")));
		trees.add(LabelNode.of("relevant_implied_terms")
				.build(is("oral")));
		trees.add(QuestionNode.of(
				"Why do you believe that the unwritten terms were part of the agreement?")
				.option("Business efficacy", "implied_for_business_efficacy")
				.option("Custom", "implied_as_customary")
				.build(is("relevant_implied_terms")));
		
		clauses.add(ClauseBuilder.newBuilder()
				.then("The contract contained the following express terms:")
				.build(is("relevant_express_terms"),
						"Please list, exactly, the terms from your agreement that relate to the incident.",
						"What is the <N> term from the agreement? (or \"done\" if finished)",
						"The Client agrees to pay £500 for services."));
		clauses.add(ClauseBuilder.newBuilder()
				.then("The following terms were necessarily implied into the agreement to give business efficacy:")
				.build(is("relevant_implied_terms", "implied_for_busines_efficacy"),
						"Please list anything relevant to the incident that was implied in your agreement but not written down.",
						"What is the <N> relevant implied term? (or \"done\" if finished)",
						"The Client shall remove their car from the garage promptly."));
		clauses.add(ClauseBuilder.newBuilder()
				.then("The following terms were necessarily implied into the agreement as a matter of custom:")
				.build(is("relevant_implied_terms", "implied_as_customary"),
						"Please list anything relevant to the incident that was implied in your agreement but not written down.",
						"What is the <N> relevant implied term? (or \"done\" if finished)",
						"The garage shall return the keys to the Client's vehicle upon payment."));	
		sections.add(new Section("Terms of the Contract", 3, trees, clauses));
		trees = new HashSet<>();
		clauses = new HashSet<>();
		
		trees.add(QuestionNode.of(
				"Did you issue an invoice to the Defendant?")
				.option("Yes", "claimant_issued_invoice")
				.option("No", "claimant_did_not_issue_invoice")
				.build());
		Element claimantPerformance = q("What did you do to fulfill your side of the agreement?", "fully repaired the car");
		Element claimantPerformanceDate = q("On what date did you fulfill your side of the agreement?", "8 July 2005");
		Element claimantInvoiceNumber = q("What was the invoice number for the invoice that you issued to the Defendant?", "1234");
		Element claimantInvoiceDate = q("What was the date for the invoice that you issued to the Defendant?", "8 July 2005");
		clauses.add(ClauseBuilder.newBuilder()
				.then("The Claimant duly ", claimantPerformance, " on ", claimantPerformanceDate,
						". The Claimant issued an invoice ", claimantInvoiceNumber,
						" to the Defendant dated ", claimantInvoiceDate, ".")
				.build(is("claimant_issued_invoice")));
		clauses.add(ClauseBuilder.newBuilder()
				.then("The Claimant duly ", claimantPerformance, " on ", claimantPerformanceDate,
						".")
				.build(is("claimant_did_not_issue_invoice")));		
		
		sections.add(new Section("Claimant's Performance of the Contract", 4, trees, clauses));
		trees = new HashSet<>();
		clauses = new HashSet<>();
		
		Element paymentAllowedDays = q("How many days after the invoice date was payment due?", "14");
		Element paymentDueDate = q("On what date was payment due?", "22 July 2005");

		clauses.add(ClauseBuilder.newBuilder()
				.then("Payment was due from the Defendant ", paymentAllowedDays,
						" days from the date of the invoice, namely by ", paymentDueDate, ".")
				.build(is("claimant_issued_invoice")));
		clauses.add(ClauseBuilder.newBuilder()
				.then("Payment was due from the Defendant on ", paymentDueDate, ".")
				.build(is("claimant_did_not_issue_invoice")));
		
		sections.add(new Section("Payment Due Date", 5, trees, clauses));
		trees = new HashSet<>();
		clauses = new HashSet<>();
		Element dueAmount = q("How much was the Defendant supposed to pay you?", "£400");
		
		clauses.add(ClauseBuilder.newBuilder()
				.then("In breach of contract, the Defendant failed to pay the sum of ",
						dueAmount, " or any part of that sum by the due date or at all.")
				.build(is("contract")));
		clauses.add(ClauseBuilder.newBuilder()
				.then("The Defendant failed to pay the sum of ", dueAmount,
						" or any part of that sum by the due date or at all.")
				.build(is("oral")));
		clauses.add(ClauseBuilder.newBuilder()
				.then("The Claimant made the following requests for payment:")
				.build(Condition.always(),
						"Please list any requests (oral or written) that you made for payment.",
						"What was the <N> request for payment? (or \"done\" if finished)",
						"On 8 August 2005, requested payment over email."));			
		clauses.add(ClauseBuilder.newBuilder()
				.then("The Defendant therefore owes the Claimant the sum of ", dueAmount)
				.build());
		
		sections.add(new Section("Defendant's Failure to Pay", 6, trees, clauses));
		trees = new HashSet<>();
		clauses = new HashSet<>();
		
		trees.add(QuestionNode.of(
				"In addition to owing payment, did the Defendant also fail to fulfil their part of the agreement?")
				.option("Yes", "breach_of_contract")
				.option("No", "no_breach_of_contract")
				.build());
		Element breachNature = q("In what way did the Defendant fail to satisfy the contract?", "did not pick the vehicle up on the agreed day");
		Element reasonForObligations = q("Why do you require the Defendent to satisfy the contract?", "the vehicle is occupying important parking space");
		
		clauses.add(ClauseBuilder.newBuilder()
				.then("In breach of contract, the Defendant additionally ", breachNature,
						" which is required by the Claimant because ", reasonForObligations)
				.build(is("breach_of_contract")));
		clauses.add(ClauseBuilder.newBuilder()
				.then("The Claimant made the following requests regarding this breach:")
				.build(is("breach_of_contract"),
						"Please list any requests (oral or written) that you made for the Defendant to satisfy the contract.",
						"What was the <N> request? (or \"done\" if finished)",
						"On 7 August 2005, requested that the Defendant remove their vehicle from the property."));			
		clauses.add(ClauseBuilder.newBuilder()
				.then("If the Claimant does not obtain an order for specific performance ",
						"of the contract, it will suffer the following additional losses ",
						"as a result of the Defendant's breach of contract:")
				.build(is("breach_of_contract"),
						"Please list any losses, financial or otherwise, that will occur if the Defendant does not fulfill the contract.",
						"What is the <N> loss? (or \"done\" if finished)",
						"Loss of reputation due to low car park capacity."));
		
		sections.add(new Section("Breach of Contract", 7, trees, clauses));
		trees = new HashSet<>();
		clauses = new HashSet<>();
		
		clauses.add(ClauseBuilder.newBuilder()
				.then("The sum of ", dueAmount)
				.build());
		clauses.add(ClauseBuilder.newBuilder()
				.then("Specific performance of the contract")
				.build(is("breach_of_contract")));
		clauses.add(ClauseBuilder.newBuilder()
				.then("Costs")
				.build());
		
		sections.add(new Section("Claims", 8, trees, clauses));
		
 		return new TreeSearchEngine(sections);
	}

	private static Condition is(String... labels) {
		return Condition.labels(convert(labels));
	}

	private static StateLabel[] convert(String... in) {
		StateLabel[] result = new StateLabel[in.length];
		for (int i = 0; i < in.length; i++) {
			result[i] = s(in[i]);
		}
		return result;
	}

	private static ConditionalElement cond(Object... vals) {
		LinkedHashMap<Condition, Element> elements = new LinkedHashMap<>();
		for (int i = 0; i < vals.length; i += 2) {
			elements.put((Condition) vals[i], (Element) vals[i + 1]);
		}
		return new ConditionalElement(elements);
	}

	private static StateLabel s(String state) {
		return new StateLabel(state);
	}

	private static Element q(String text, String example) {
		return new AnswerElement(new SimpleQuestion(nextIndex++, text, example));
	}

	private static class ClauseBuilder {

		private Map<StateLabel, String> pendingLabelMap;
		private final List<Element> elements = new ArrayList<>();
		private int index;

		private ClauseBuilder() {
		}

		public static ClauseBuilder newBuilder() {
			return new ClauseBuilder().n(nextIndex++);
		}

		public ClauseTemplate build(Condition condition) {
			return new ClauseTemplate(index, condition, elements);
		}

		public ClauseTemplate build(Condition condition, String subclauseIntro, String subclauseTemplate, String subclauseExample) {
			return new ClauseTemplate(index, condition, elements, subclauseIntro, subclauseTemplate, subclauseExample);
		}

		public ClauseTemplate build() {
			return build(Condition.always());
		}

		public ClauseBuilder n(int index) {
			this.index = index;
			return this;
		}

		public ClauseBuilder element(Element e) {
			elements.add(e);
			return this;
		}

		public ClauseBuilder then(Object... elementsAndStrings) {
			for (Object o : elementsAndStrings) {
				if (o instanceof Element) {
					element((Element) o);
				} else if (o instanceof String) {
					element((String) o);
				} else if (o instanceof Question) {
					element((Question) o);
				}
			}
			return this;
		}

		public ClauseBuilder element(String literal) {
			elements.add(new Literal(literal));
			return this;
		}

		public ClauseBuilder element(Question question) {
			elements.add(new AnswerElement(question));
			return this;
		}

		public ClauseBuilder startLabelMap() {
			pendingLabelMap = new HashMap<>();
			return this;
		}

		public ClauseBuilder put(StateLabel label, String value) {
			pendingLabelMap.put(label, value);
			return this;
		}

		public ClauseBuilder finishLabelMap() {
			elements.add(new LabelMap(pendingLabelMap));
			return this;
		}

	}

	private static class LabelNode {
		final StateLabel[] labels;

		private LabelNode(String... labels) {
			this.labels = convert(labels);
		}

		public static LabelNode of(String... labels) {
			return new LabelNode(labels);
		}

		public TreeNode build() {
			return new LabelTreeNode(new HashSet<>(Arrays.asList(labels)));
		}

		public TreeNode build(Condition c) {
			return new ConditionalNode(c, build());
		}
	}

	private static class QuestionNode {

		private final int order;
		private final String question;
		private final List<String> options = new ArrayList<>();
		private final Map<AnswerPredicate, TreeNode> predicates = new HashMap<>();
		private final Set<StateLabel> labels = new HashSet<>();

		public PredicateTreeNode build() {
			return new PredicateTreeNode(new OneOfQuestion(order, question, options), predicates, labels);
		}

		public ConditionalNode build(Condition c) {
			return new ConditionalNode(c, build());
		}

		private QuestionNode(int order, String question) {
			this.order = order;
			this.question = question;
		}

		public static QuestionNode of(String question) {
			return new QuestionNode(nextIndex++, question);
		}

		public QuestionNode labels(StateLabel... addLabels) {
			labels.addAll(Arrays.asList(addLabels));
			return this;
		}

		public QuestionNode option(String option, TreeNode result) {
			predicates.put(new OneOfPredicate(options.size()), result);
			options.add(option);
			return this;
		}

		public QuestionNode option(String option, String... labels) {
			return option(option, LabelNode.of(labels).build());
		}
	}
}
