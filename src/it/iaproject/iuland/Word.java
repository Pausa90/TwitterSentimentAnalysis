package it.iaproject.iuland;

public class Word {

	private String text;
	private String partOfSpeech; //es: nome, verbo, ...
	
	public Word(String text, String partOfSpeech){
		this.setText(text);
		this.setPartOfSpeech(partOfSpeech);
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getPartOfSpeech() {
		return partOfSpeech;
	}

	public void setPartOfSpeech(String partOfSpeech) {
		this.partOfSpeech = partOfSpeech;
	}

}
