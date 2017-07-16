package code.imogen.telegram;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import code.imogen.debug.TextConverters;
import code.imogen.impl.CommandListener;
import code.imogen.impl.Main;
import code.imogen.impl.SessionData;
import code.imogen.impl.search.Answer;
import code.imogen.impl.search.OneOfQuestion;
import code.imogen.impl.search.Question;

public class ImogenBot extends TelegramLongPollingBot implements CommandListener {

	public static final Random rand = new Random();
	Main main;
	SessionData session = new SessionData(1);
	Question question;
	String questionText;
	String toPrint;

	public ImogenBot() {
		this.
		main = new Main();
		init();
	}
	
	private void init() {
		main.registerListener(this);
		question = main.getNextQuestion(session);
		questionText = question.getText();
	}

	@Override
	public void onUpdateReceived(Update update) {
		String answerText = update.getMessage().getText();

		if (answerText.equals(".")) {
			if (question instanceof OneOfQuestion) {
				answerText = "" + rand.nextInt(((OneOfQuestion)question).options.size());
			} else {
				answerText = "((" + questionText + "))";
			}
		}
		if (question instanceof OneOfQuestion) {
			try {
				int optionIndex = Integer.parseInt(answerText);
				answerText = "" + (optionIndex - 1);
			} catch (NumberFormatException e) {}
		}
		Answer answer = TextConverters.textToAnswer(answerText, question);
		main.acceptAnswer(session, question, answer);
		
		question = main.getNextQuestion(session);
		questionText = question.getText();
		SendMessage s = new SendMessage();
		StringBuilder text = new StringBuilder();
		s.setChatId(update.getMessage().getChatId());
		if (toPrint != null) {
			text.append(toPrint).append("\n");
			toPrint = null;
		}
		if (question == Question.VOID) {
			text.append("No further questions. /quit or /upload_document.");
		} else {
			text.append(questionText);
		}
		if (question instanceof OneOfQuestion) {
			OneOfQuestion q = (OneOfQuestion)question;
			text.append("\n");

			ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup();
			markup.setSelective(true).setResizeKeyboard(true).setOneTimeKeyboard(true);
			List<KeyboardRow> rows = new ArrayList<>();
			for (int i = 0; i < q.options.size(); i++) {
				KeyboardRow row = new KeyboardRow();
				row.add(q.makeQuestionText(i));
				rows.add(row);
			}
			markup.setKeyboard(rows);
			s.setReplyMarkup(markup);
			s.setParseMode("Markdown");
		} else {
		}
		
		s.setText(text.toString());
		System.out.println("Sending message: " + s);
		try {
			sendMessage(s);
		} catch (TelegramApiException e) {
			e.printStackTrace();
		}
	}

	@Override
	public String getBotUsername() {
		return TelegramLauncher.BOT_NAME;
	}

	@Override
	public String getBotToken() {
		return TelegramLauncher.BOT_TOKEN;
	}

	@Override
	public void quit(SessionData session) {
	}

	@Override
	public void print(SessionData session, String text) {
		toPrint = text;
	}

}
