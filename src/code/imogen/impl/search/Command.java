package code.imogen.impl.search;

import java.util.Scanner;

import code.imogen.impl.search.Question.VoidQuestion;

public class Command extends Answer {
	
	public static boolean isCommand(String text) {
		if (text.startsWith("/")) {
			if (CommandType.fromText(text.substring(1)) != null) {
				return true;
			}
		}
		return false;
	}
	
	public static Command makeCommand(String text) {
		if (!isCommand(text)) {
			throw new RuntimeException("Not a valid command: " + text);
		}
		try (Scanner sc = new Scanner(text.substring(1))) {
			String cmd = sc.next();
			CommandType type = CommandType.fromText(cmd);
			return new Command(type);
		}
	}
	
	private final CommandType type;
	
	public Command(CommandType type) {
		this.type = type;
	}
	
	public CommandType getType() {
		return type;
	}

	@Override
	public boolean isCommand() {
		return true;
	}
	
	@Override
	public Command asCommand() {
		return this;
	}
	
	public enum CommandType {
		START_ENGINE("start"),
		RESET("reset"),
		UPLOAD_DOCUMENT("upload_document"),
		QUIT("quit");
		
		public final String text;
		
		private CommandType(String text) {
			this.text = text;
		}
		
		public static CommandType fromText(String text) {
			for (CommandType type : CommandType.values()) {
				if (type.text.equals(text)) {
					return type;
				}
			}
			return null;
		}
	}
	
}
