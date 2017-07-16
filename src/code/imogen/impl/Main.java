package code.imogen.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import code.imogen.debug.TextConverters;
import code.imogen.debug.TreeSearchEngineBuilder;
import code.imogen.impl.search.Answer;
import code.imogen.impl.search.Command;
import code.imogen.impl.search.FullState;
import code.imogen.impl.search.Question;
import code.imogen.impl.search.SearchEngine;
import code.imogen.impl.search.SystemQuestion;
import code.imogen.upload.S3Uploader;

public class Main {
	
	private List<CommandListener> listeners = new ArrayList<>();
	private Map<Integer, SearchEngine> searchEngines = new HashMap<>();
	S3Uploader s3uploader = new S3Uploader();
	
	public Main() {
	}
	
	public void registerListener(CommandListener listener) {
		listeners.add(listener);
	}
	
	private void handleCommand(SessionData session, Command command) {
		switch (command.getType()) {
		case QUIT:
			for (CommandListener listener : listeners) {
				listener.quit(session);
			}
			break;
		case UPLOAD_DOCUMENT:
			FullState state = searchEngines.get(session.id).getResult();
			String html = TextConverters.toHtml(state.clauses);
			String url = s3uploader.upload("poc" + session.id, html);
			for (CommandListener listern : listeners) {
				listern.print(session, url);
			}
			break;
		case START_ENGINE:
		case RESET:
			searchEngines.put(session.id, TreeSearchEngineBuilder.build());
			break;
		}
	}
	
	public void acceptAnswer(
			SessionData session, Question question, Answer answer) {
		if (answer.isCommand()) {
			handleCommand(session, answer.asCommand());
		} else {
			if (searchEngines.containsKey(session.id)) {
				searchEngines.get(session.id).acceptAnswer(question, answer);
			} else {
				// No-op
			}
		}
	}

	public Question getNextQuestion(SessionData session) {
		if (searchEngines.containsKey(session.id)) {
			Question nextQuestion = searchEngines.get(session.id).getPendingQuestion();
		    return nextQuestion;
		} else {
			return new SystemQuestion("Use /start to begin!");
		}
	}
	
}
