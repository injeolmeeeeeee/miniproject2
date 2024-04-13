package vttp.nus.miniproject2.models;

public class Question {
	private Long id;
	private int QuestionNumber;
	private String questionText;
	private String category;
	private String edition;
	private int level;
	private int score;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getQuestionText() {
		return questionText;
	}

	public void setQuestionText(String questionText) {
		this.questionText = questionText;
	}

	public String getEdition() {
		return edition;
	}

	public void setEdition(String edition) {
		this.edition = edition;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public int getQuestionNumber() {
		return QuestionNumber;
	}

	public void setQuestionNumber(int questionNumber) {
		QuestionNumber = questionNumber;
	}

	
}