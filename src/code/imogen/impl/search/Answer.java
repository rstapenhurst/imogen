package code.imogen.impl.search;

public abstract class Answer {

	public boolean isCommand() {
		return false;
	}
	
	public Command asCommand() {
		throw new RuntimeException("This answer is not a command");
	}
	
	public static Answer VOID = new Answer() {};
	
}
