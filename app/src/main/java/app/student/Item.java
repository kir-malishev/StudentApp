package app.student;

/**
 * Элемент ListView
 */
public class Item {
	/**
	 * Заголовок
	 */
	String header;

	/**
	 * Подзаголовок
	 */
	String subHeader;

	/** Тип */
	String type;

	/** Вопрос с выбором одного ответа */
	final static String CHOICE = "choice";

	/** Вопрос с вводом ответа */
	final static String INPUT = "input";

	/** Вопрос смножественным выбором ответа */
	final static String MULTIPLE = "multiple";

	/**
	 * Конструктор создает новый элемент в соответствии с передаваемыми
	 * параметрами:
	 * 
	 * @param h
	 *            - заголовок элемента
	 * @param s
	 *            - подзаголовок
	 * @param t
	 *            - тип
	 */
	Item(String h, String s, String t) {
		this.type = t;
		this.header = h;
		this.subHeader = s;
	}

	public String getType() {
		return type;
	}

	public String getHeader() {
		return header;
	}

	public void setHeader(String header) {
		this.header = header;
	}

	public String getSubHeader() {
		return subHeader;
	}

	public void setSubHeader(String subHeader) {
		this.subHeader = subHeader;
	}

}