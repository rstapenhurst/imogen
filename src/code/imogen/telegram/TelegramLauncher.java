package code.imogen.telegram;

import java.io.IOException;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;

import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.logging.BotLogger;
import org.telegram.telegrambots.logging.BotsFileHandler;

import code.imogen.debug.Debug;
import code.imogen.impl.Main;

public class TelegramLauncher {
	
	public static String BOT_NAME;
	public static String BOT_TOKEN;
	
	public static void main(String[] args) throws Exception {

		Debug.AWS_KEY = args[0];
		Debug.AWS_SECRET = args[1];
		BOT_NAME = args[2];
		BOT_TOKEN = args[3];
		
		BotLogger.setLevel(Level.ALL);
        BotLogger.registerLogger(new ConsoleHandler());
        try {
            BotLogger.registerLogger(new BotsFileHandler());
        } catch (IOException e) {
            BotLogger.severe("TAG:", e);
        }
        ApiContextInitializer.init();
		TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
		telegramBotsApi.registerBot(new ImogenBot());
	}

}
