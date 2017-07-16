package code.imogen.impl;

public interface CommandListener {

	void quit(SessionData session);
	void print(SessionData session, String text);
	
}
