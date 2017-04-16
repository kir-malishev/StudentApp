package app.student;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

/**
 * Активность для выполнения теста.
 * 
 * @author  Кирилл Малышев
 * @version 1.0
 */
public class RunTestActivity extends Activity implements OnItemClickListener {

	/** Количество вопросов теста. */
	int testSize;

	/** Название теста. */
	String testName;

	/** Логин учителя (e-mail). */
	String teacherName;

	/** Имя, фамилия и класс/курс ученика. */
	String studentName;

	/** Строка с html-разметкой результата ученика. */
	String studentResult;

	/** Массив с информацией по тесту. */
	public static ArrayList<HashMap<String, String>> data_for_test;

	/** Массив с элементами ListView {@link RunTestActivity@list}. */
	public static ArrayList<Item> qq = new ArrayList<Item>();

	/** Массив с ответами ученика на вопросы теста. */
	public static ArrayList<ArrayList<String>> userAns;

	/** Массив с баллами за верный ответ на вопросы. */
	public static int[] pointsArray;

	/** Список вопросов теста. */
	ListView list;

	/** Массив с перемешанными вариантами ответов на вопросы. */
	public static ArrayList<String[]> answers;

	/** Всплывающее диалоговое окно. */
	AlertDialog.Builder ad;

	/**
	 * Объект для запуска потока, отправляющего запрос на сервер.
	 * 
	 * @see RunTestActivity.Task
	 */
	RequestTask task;

	/** Номер результата. */
	String uuid;

	/**
	 * Устанавливает разметку. Обраватывает информацию по тесту и записывает её
	 * в массивы.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.run_test);

		if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

		data_for_test = new ArrayList<HashMap<String, String>>();

		qq = new ArrayList<Item>();
		list = (ListView) findViewById(R.id.answers);

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			String testJSON = extras.getString("testJSON");
			getTest(testJSON);
			getqq();
			getRandomAnswers();
			setTitle(testName);
		}
		studentName = FirstActivity.whatName + " " + FirstActivity.whatLastName + " " + FirstActivity.whatClass + " "
				+ FirstActivity.whatLetter;
	}

	/**
	 * Обрабатывает json-объект с информацией по тесту и записывает её в
	 * массивы.
	 * 
	 * @param testJSON
	 *            Json-объект с информацией по тесту.
	 */
	public void getTest(String testJSON) {
		JSONObject json;
		try {
			data_for_test = new ArrayList<HashMap<String, String>>();
			userAns = new ArrayList<ArrayList<String>>();
			answers = new ArrayList<String[]>();
			json = new JSONObject(testJSON);
			JSONArray data = json.getJSONArray("test");
			String type;
			String points;
			String question;
			String answer;
			String isRight;
			String size;
			testSize = data.length();
			testName = json.getString("testname");
			teacherName = json.getString("username");
			pointsArray = new int[testSize];
			for (int position = 0; position < testSize; position++) {
				type = data.getJSONObject(position).getString("type").toString();
				question = data.getJSONObject(position).getString("question").toString();
				points = data.getJSONObject(position).getString("points");
				size = data.getJSONObject(position).getString("size").toString();
				userAns.add(new ArrayList<String>());
				answers.add(new String[Integer.parseInt(size)]);
				pointsArray[position] = Integer.parseInt(points);
				data_for_test.add(new HashMap<String, String>());
				data_for_test.get(position).put("size", size);
				data_for_test.get(position).put("type", type);
				data_for_test.get(position).put("qq", question);
				data_for_test.get(position).put("points", points);
				if (type.equals("input") || type.equals("choice")) {
					if (type.equals("input"))
						userAns.get(position).add("");
					for (int i = 0; i < Integer.parseInt(size); i++) {
						answer = data.getJSONObject(position).getString("answer" + (i + 1)).toString();
						data_for_test.get(position).put("ans_" + i, answer);
					}
				} else if (type.equals("multiple")) {
					for (int i = 0; i < Integer.parseInt(size); i++) {
						answer = data.getJSONObject(position).getString("answer" + (i + 1)).toString();
						isRight = data.getJSONObject(position).getString("isright" + (i + 1)).toString();
						data_for_test.get(position).put("ans_" + i, answer);
						data_for_test.get(position).put("isright_" + i, isRight);
					}
				}

			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Открывает окно для выполнения задания в зависимости от типа выбранного
	 * вопроса.
	 * 
	 * @param parent
	 *            Адаптер для ListView {@link RunTestActivity#list}
	 * @param view
	 *            Нажатый элемент списка.
	 * @param position
	 *            Индекс вопроса (номер вопроса - 1).
	 * @param id
	 *            Идентификатор элемента.
	 */
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		String type = qq.get(position).getType();

		Intent intent = new Intent(this, RunTestActivity.class);
		if (type.equals("choice")) {
			intent = new Intent(this, ChoiceActivity.class);
		} else if (type.equals("multiple")) {
			intent = new Intent(this, MultipleActivity.class);
		} else if (type.equals("input")) {
			intent = new Intent(this, InputActivity.class);
		}
		intent.putExtra("position", position);
		intent.putExtra("type", qq.get(position).getType());
		startActivity(intent);
	}

	/**
	 * Добавляет в ListView {@link RunTestActivity#list} новый элемент
	 * 
	 * @param header
	 *            Заголовок большего размера.
	 * @param subHeader
	 *            Заголовок меньшего размера.
	 * @param type
	 *            Тип вопроса теста.
	 */
	public void addQuestion(String header, String subHeader, String type) {
		Item item = new Item(header, subHeader, type);
		qq.add(item);
		list.setAdapter(new MyAdapter(this, qq));
		list.setOnItemClickListener(this);
		data_for_test.add(new HashMap<String, String>());
	}

	/**
	 * Создаёт ListView {@link RunTestActivity#list} со списком вопросов теста.
	 */
	public void getqq() {
		String type;
		for (int position = 0; position < testSize; position++) {
			if (data_for_test.get(position).containsKey("type"))
				type = data_for_test.get(position).get("type");
			else
				type = "choice";
			if (type.equals("choice"))
				addQuestion(getString(R.string.numberqq) + (qq.size() + 1), data_for_test.get(position).get("qq"),
						"choice");
			else if (type.equals("multiple"))
				addQuestion(getString(R.string.numberqq) + (qq.size() + 1), data_for_test.get(position).get("qq"),
						"multiple");
			else if (type.equals("input"))
				addQuestion(getString(R.string.numberqq) + (qq.size() + 1), data_for_test.get(position).get("qq"),
						"input");
		}

	}

	/**
	 * Перемешивает массив
	 * 
	 * @param ar
	 *            Массив, который нужно перемешать.
	 * @return Перемешанный массив.
	 */
	static String[] shuffleArray(String[] ar) {
		Random rnd = new Random();
		for (int i = ar.length - 1; i > 0; i--) {
			int index = rnd.nextInt(i + 1);
			String a = ar[index];
			ar[index] = ar[i];
			ar[i] = a;
		}
		return ar;
	}

	/** Перемешивает варианты ответов на тест. */
	public void getRandomAnswers() {
		int size = 0;
		for (int position = 0; position < testSize; position++) {
			if (data_for_test.get(position).containsKey("size"))
				size = Integer.parseInt(data_for_test.get(position).get("size"));
			for (int i = 0; i < size; i++) {
				if (data_for_test.get(position).containsKey("ans_" + i)
						&& data_for_test.get(position).containsKey("type"))
					if (data_for_test.get(position).get("type").equals("input"))
						answers.get(position)[i] = data_for_test.get(position).get("ans_" + i).toUpperCase();
					else {
						answers.get(position)[i] = data_for_test.get(position).get("ans_" + i);
					}
			}
			System.arraycopy(shuffleArray(answers.get(position)), 0, answers.get(position), 0, size);
		}
	}

	/**
	 * Собирает из данных об ответах ученика html-разметку с информацией о
	 * результате выполнения теста.
	 * 
	 * @return Html-разметка результата.
	 */
	public String getResult() {
		String type;
		boolean isMistake;
		boolean isRight;
		int allPoints = 0;
		int scorePoints = 0;
		String message = "";
		message += "<font >" + testName + "<br><br></font>";
		int testSize = qq.size();
		for (int position = 0; position < testSize; position++) {
			isMistake = false;
			isRight = false;
			if (data_for_test.get(position).containsKey("qq"))
				message += "<font >" + (position + 1) + "." + data_for_test.get(position).get("qq") + "<br></font>";
			type = qq.get(position).getType();
			if (type.equals("choice")) {
				message += "<font >" + getString(R.string.choice) + "<br></font>";
				if (data_for_test.get(position).containsKey("size")
						&& data_for_test.get(position).containsKey("ans_0")) {
					int size = Integer.parseInt(data_for_test.get(position).get("size"));
					for (int j = 0; j < size; j++) {
						if (userAns.get(position).size() > 0) {
							if (userAns.get(position).contains(answers.get(position)[j]))
								if (answers.get(position)[j].equals(data_for_test.get(position).get("ans_" + 0))) {
									message += "<font>\t&#9679; </font><font color=#00ff00>" + answers.get(position)[j]
											+ " &#9989;<br></font>";
									isRight = true;
								} else {
									message += "<font>\t&#9679; </font><font color=#ff0000>" + answers.get(position)[j]
											+ "<br></font>";
								}
							else if (answers.get(position)[j].equals(data_for_test.get(position).get("ans_" + 0)))
								message += "<font>\t&#9675; " + answers.get(position)[j] + " &#9989;<br></font>";
							else
								message += "<font>\t&#9675; " + answers.get(position)[j] + "<br></font>";
						} else if (answers.get(position)[j].equals(data_for_test.get(position).get("ans_" + 0)))
							message += "<font>\t&#9675; " + answers.get(position)[j] + " &#9989;<br></font>";
						else
							message += "<font>\t&#9675; " + answers.get(position)[j] + "<br></font>";
					}
					if (data_for_test.get(position).containsKey("points"))
						message += "<font>" + getString(R.string.points) + data_for_test.get(position).get("points")
								+ "<br><br></font>";
					if (userAns.get(position).size() == 0)
						message += "<font color=#ff0000>Ответа нет<br><br></font>";
				}
			} else if (type.equals("multiple")) {
				message += "<font>" + getString(R.string.multiple) + "<br></font>";
				if (data_for_test.get(position).containsKey("size")) {
					int size = Integer.parseInt(data_for_test.get(position).get("size"));
					for (int j = 0; j < size; j++) {

						for (int i = 0; i < size; i++) {
							if (answers.get(position)[j].equals(data_for_test.get(position).get("ans_" + i)))
								if (userAns.get(position).size() > 0) {
									if (userAns.get(position).contains(data_for_test.get(position).get("ans_" + i)))
										if (Boolean.valueOf(data_for_test.get(position).get("isright_" + i))) {
											message += "<font>\t&#9632; </font><font color=#00ff00>"
													+ answers.get(position)[j] + " &#9989;<br></font>";
											isRight = true;
										} else {
											message += "<font>\t&#9632; </font><font color=#ff0000>"
													+ answers.get(position)[j] + "<br></font>";
											isRight = false;
											isMistake = true;
										}
									else if (Boolean.valueOf(data_for_test.get(position).get("isright_" + i))) {
										message += "<font>\t&#9633; " + answers.get(position)[j]
												+ " &#9989;<br></font>";
										isRight = false;
										isMistake = true;
									} else
										message += "<font>\t&#9633; " + answers.get(position)[j] + "<br></font>";
								} else if (Boolean.valueOf(data_for_test.get(position).get("isright_" + i))) {
									message += "<font>\t&#9633; " + answers.get(position)[j] + " &#9989;<br></font>";
									isRight = false;
									isMistake = true;
								} else
									message += "<font>\t&#9633; " + answers.get(position)[j] + "<br></font>";
						}
					}
					if (data_for_test.get(position).containsKey("points"))
						message += "<font>" + getString(R.string.points) + data_for_test.get(position).get("points")
								+ "<br><br></font>";
					if (userAns.get(position).size() == 0)
						message += "<font color=#ff0000>Ответа нет<br><br></font>";
				}
			} else if (type.equals("input")) {
				message += "<font>" + getString(R.string.input) + "<br></font>";
				if (userAns.get(position).size() > 0)
					if (Arrays.asList(answers.get(position)).contains(userAns.get(position).get(0).toUpperCase())) {
						message += "<font color=#00ff00>\t " + userAns.get(position).get(0) + "<br></font>";
						isRight = true;
					} else {
						message += "<font color=#ff0000>\t " + userAns.get(position).get(0) + "<br></font>";
						isMistake = true;
					}
				if (data_for_test.get(position).containsKey("points"))
					message += "<font>" + getString(R.string.points) + data_for_test.get(position).get("points")
							+ "<br><br></font>";
				if (userAns.get(position).get(0).equals("")) {
					message += "<font color=#ff0000>Ответа нет<br><br></font>";
					isMistake = true;
				}
				if (isMistake) {
					message += "<font>" + getString(R.string.variants) + "<br></font>";
					for (int j = 0; j < answers.get(position).length; j++) {
						message += "<font>\t<i>" + answers.get(position)[j] + "</i><br></font>";
					}
				}
			}
			if (data_for_test.get(position).containsKey("points")) {
				int points = Integer.parseInt(data_for_test.get(position).get("points"));
				allPoints += points;
				if (isRight && (isMistake == false)) {
					message += "<font color=#00ff00>" + getString(R.string.right) + "<br><br></font><font>"
							+ getString(R.string.getpoints) + " " + points + "<br><br></font>";
					scorePoints += points;
				} else
					message += "<font color=#ff0000>" + getString(R.string.noright) + "<br><br></font><font>"
							+ getString(R.string.getpoints) + " 0<br><br></font>";
			}
		}
		double pct = Math.round(((float) scorePoints / (float) allPoints) * 10000) / 100.0;
		message = "<b><font >" + studentName + "<br><br>" + getString(R.string.score) + " " + scorePoints + " "
				+ getString(R.string.from) + " " + allPoints + " (" + pct + "%)<br><br>" + getString(R.string.mark)
				+ " " + getMark(pct) + "<br><br></font>" + message + "<font>&#9989; – "
				+ getString(R.string.rightvariant) + "<br></font></b>";
		return message;
	}

	/**
	 * Возвращает предварительную оценку по пятибалльной шкале в зависимости от
	 * процента выполнения теста.
	 * 
	 * @param pct
	 *            Процент выполнения теста.
	 * @return Предварительная оценка.
	 */
	public int getMark(double pct) {
		if (pct < 50)
			return 2;
		else if (pct < 75)
			return 3;
		else if (pct < 90)
			return 4;
		else
			return 5;
	}

	/**
	 * Выводит сообщения о подтверждении досрочного выхода из теста и при
	 * положительном ответе переводит пользователя в стартовое окно приложения.
	 * 
	 * @param v
	 *            Кнопка "Выйти".
	 */
	public void exit(View v) {
		ad = new AlertDialog.Builder(this);
		ad.setTitle(R.string.exit);
		ad.setMessage(R.string.warning);
		final Intent intent = new Intent(getApplicationContext(), FirstActivity.class);
		ad.setPositiveButton(R.string.exit, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				startActivity(intent);
				finish();
			}
		});

		ad.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
			}
		});
		ad.setCancelable(true);
		ad.show();
	}

	/**
	 * Выводит сообщение о вопросах, на которые ответа не было дано, если такие
	 * есть. При подтверждении действия подсчитывает результат и отправляет
	 * запрос на сервер для его сохранения.
	 * 
	 * @param v
	 *            Кнопка "Отправить".
	 */
	public void finishTest(View v) {
		String forget = getString(R.string.forget);
		boolean err = false;
		for (int position = 0; position < testSize; position++) {
			if (qq.get(position).getType().equals("input") && userAns.get(position).get(0).trim().equals("")
					|| userAns.get(position).size() == 0) {
				if (err)
					forget += ", " + (position + 1);
				else
					forget += " " + (position + 1);
				err = true;
			}
		}

		ad = new AlertDialog.Builder(this);
		ad.setTitle(R.string.finishtest);
		if (err)
			ad.setMessage(getString(R.string.finish) + "\n" + forget);
		else
			ad.setMessage(getString(R.string.finish));
		ad.setPositiveButton(R.string.finishtest, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {

				try {
					if (Utils.isConnected()) {
						studentResult = getResult();
						uuid = UUID.randomUUID().toString().toUpperCase();
						task = new Task();
						task.addParam("result", studentResult);
						task.addParam("id", SetIdActivity.id.toUpperCase());
						task.addParam("studentname", studentName);
						task.addParam("teachername", teacherName);
						task.addParam("uuid", uuid);
						task.setContext(RunTestActivity.this);
						task.execute(RequestTask.DOMAIN + "/api/scripts/saveresult.php");
					} else
						Utils.showToast(RunTestActivity.this, getString(R.string.noconnect));
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});

		ad.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
			}
		});
		ad.setCancelable(true);
		ad.show();
	}

	private void hideKeyboard() {
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
	}

	/** Закрывает клавиатуру, если произедено касание какого-либо из полей. */
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		if (ev.getAction() == MotionEvent.ACTION_DOWN)
			hideKeyboard();
		return super.dispatchTouchEvent(ev);
	}

	/**
	 * Класс для обработки ответа от сервера. Отправка запроса происходит в
	 * классе {@link RequestTask}.
	 */
	class Task extends RequestTask {

		/**
		 * Переводит пользователя в окно просмотра результата выполнения теста.
		 * 
		 * @param result
		 *            Отвтет сервера.
		 */
		@Override
		protected void onPostExecute(String result) {

			super.dialog.dismiss();

			try {
				Intent intent = new Intent(getApplicationContext(), ActivityResult.class);
				intent.putExtra("result", studentResult);
				intent.putExtra("uuid", uuid);
				startActivity(intent);
				finish();

			} catch (Exception e) {
				e.printStackTrace();
			}

			super.onPostExecute(result);

		}
	}
}
