package code.imogen.impl.search;

public class SystemQuestion implements Question {

	private String text;

	public SystemQuestion(String text) {
		this.text = text;
	}
	
	@Override
	public String getText() {
		return text;
	}

	@Override
	public Answer makeAnswer(String answer) {
		return Answer.VOID;
	}

	@Override
	public String getAnswerText(Answer answer) {
		return "";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((text == null) ? 0 : text.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SystemQuestion other = (SystemQuestion) obj;
		if (text == null) {
			if (other.text != null)
				return false;
		} else if (!text.equals(other.text))
			return false;
		return true;
	}

	@Override
	public int getOrder() {
		return -1;
	}
	
	@Override
	public String getExample() {
		return null;
	}

}
