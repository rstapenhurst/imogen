package code.imogen.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import code.imogen.debug.TextConverters;
import code.imogen.impl.Main;
import code.imogen.impl.SessionData;
import code.imogen.impl.search.Question;

public class FullTest {

	@Test
	public void test1() {
		(new ImogenTest()).ans("Use /start to begin!", "/start")
				.ans("Which of the following best describes your employment situation at the time of incident? {Employed,Unemployed,Self-employed,None of the above}",
						"Employed")
				.ans("Are you bringing a claim in your capacity as an individual or a company? {Individual,Company}",
						"Individual")
				.ans("Do you want to bring a claim against a company or individual? {Individual,Company}", "Company")
				.ans("What is your occupation?", "Builder").ans("What is the name of your employer?", "Doors Inc")
				.ans("What is your full name?", "Richard John Stapenhurst")
				.clause("The Claimant was at all material times Richard John Stapenhurst, and is employed by Doors Inc as a Builder.")
				.build();
	}

	@Test
	public void test2() {

		(new ImogenTest()).ans("Use /start to begin!", "/start")
				.ans("Which of the following best describes your employment situation at the time of incident? {Employed,Unemployed,Self-employed,None of the above}",
						"Empoyed")
				.ans("Which of the following best describes your employment situation at the time of incident? {Employed,Unemployed,Self-employed,None of the above}",
						"Employed")
				.ans("Are you bringing a claim in your capacity as an individual or a company? {Individual,Company}",
						"Company")
				.ans("Do you want to bring a claim against a company or individual? {Individual,Company}", "Individual")
				.ans("What is your occupation?", "Builder").ans("What is the name of your employer?", "Bricksman")
				.ans("What is your full name?", "Richard Stapenhurst")
				.clause("The Claimant was at all material times Richard Stapenhurst, and is employed by Bricksman as a Builder.")
				.build();
	}
	
	@Test
	public void test3() {
		(new ImogenTest())
		.ans("Use /start to begin!", "/start")
		.ans("Which of the following best describes your employment situation at the time of incident? {Employed,Unemployed,Self-employed,None of the above}", "Self-employed")
		.ans("Are you bringing a claim in your capacity as an individual or a company? {Individual,Company}", "Company")
		.ans("Do you want to bring a claim against a company or individual? {Individual,Company}", "Individual")
		.ans("What is your occupation?", "Man")
		.ans("What is your full name?", "Noobface")
		.clause("The Claimant was at all material times Noobface, and their self-employed occuptation is Man.")
		.build();
	}

	static class ImogenTest {

		private final Map<String, String> answers = new HashMap<>();
		private final List<String> clauses = new ArrayList<>();

		private ImogenTest ans(String q, String a) {
			answers.put(q, a);
			return this;
		}

		private ImogenTest clause(String c) {
			clauses.add(c);
			return this;
		}

		private void build() {
			boolean success = true;
			SessionData sessionData = new SessionData(1);
			Main main = new Main();
			while (true) {
				Question q = main.getNextQuestion(sessionData);
				String qText = TextConverters.questionToText(q);
				if (!answers.containsKey(qText)) {
					if (!qText.equals("IMOGEN interrogation is complete. /reset or /quit.")) {
						success = false;
						System.out.println("Unexpected question: ");
						System.out.println(String.format(".ans(\"%s\", \"\")", qText));
					}
					break;
				}
				main.acceptAnswer(sessionData, q, TextConverters.textToAnswer(answers.get(qText), q));
			}
			/*
			List<String> results = main.getResult(sessionData);
			for (String s : results) {
				if (!clauses.contains(s)) {
					success = false;
					System.out.println(String.format(".clause(\"%s\")", s));
				}
			}
			*/
			if (!success) {
				throw new RuntimeException("Failed");
			}
		}
	}
}
