package code.imogen.debug;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import code.imogen.impl.CommandListener;
import code.imogen.impl.Main;
import code.imogen.impl.SessionData;
import code.imogen.impl.search.Answer;
import code.imogen.impl.search.OneOfQuestion;
import code.imogen.impl.search.Question;

public class Debug implements CommandListener {
	
	public static final Random rand = new Random();
	public static String AWS_KEY;
	public static String AWS_SECRET;

	public static void main(String[] args) {
		AWS_KEY = args[0];
		AWS_SECRET = args[1];
		Main main = new Main();
		Debug db = new Debug(main);
		db.run();
	}
	
	private final Main main;
	private boolean quit = false;
	private final SessionData sessionData = new SessionData(1);
	private BufferedReader reader;
	private List<String> testLog = new ArrayList<>();
	
	public Debug(Main main) {
		this.main = main;
	}
	
	public void run() {
		System.out.println("Starting IMOGEN debug interface...");
		System.out.println("Commands: /start_engine /reset /quit");
		main.registerListener(this);
		reader = new BufferedReader(new InputStreamReader(System.in));
		
		while (!quit) {
			Question nextQuestion = main.getNextQuestion(sessionData);
			askQuestion(nextQuestion);
		}
		
		System.out.println("Thank you. Terminating IMOGEN session.");
		
	}
	
	private void askQuestion(Question question) {

		String questionText = TextConverters.questionToText(question);
		System.out.println(questionText);
		try {
			String text = reader.readLine();
			if (text.equals("")) {
				if (question instanceof OneOfQuestion) {
					text = "" + rand.nextInt(((OneOfQuestion)question).options.size());
				} else {
					text = "((" + questionText + "))";
				}
			}
			Answer answer = TextConverters.textToAnswer(text, question);
			main.acceptAnswer(sessionData, question, answer);
			testLog.add(String.format(".ans(\"%s\", \"%s\")", questionText, text));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	@Override
	public void quit(SessionData session) {
		for (String s : testLog) {
			System.out.println(s);
		}
		quit = true;
	}

	@Override
	public void print(SessionData session, String text) {
		System.out.println(text);
	}
	
}
