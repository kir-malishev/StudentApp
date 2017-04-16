package app.student;

import java.io.IOException;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import app.student.R;

/**
 * Активность для ввода номера теста.
 * 
 * @autor Кирилл Малышев
 * @version 1.0
 */
public class SetIdActivity extends Activity {

	/** Номер теста. */
	public static String id;

	/** Текстовое поле для ввода номера теста. */
	EditText editId;

	/** Длина стандартного номера теста. */
	final int LENGTH = 6;

	/** Кнопка "Продолжить". */
	Button button;

	/**
	 * Объект для запуска потока, отправляющего запрос на сервер.
	 * 
	 * @see SetIdActivity.Task
	 */
	RequestTask task;

	/** Устанавливает разметку. */
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.set_id);

		editId = (EditText) findViewById(R.id.editid);

	}

	/**
	 * Отправляет пользователя в окно ввода информации об ученике.
	 * 
	 * @param v
	 *            Кнопка "Назад".
	 */
	public void back(View v) {
		Intent intent = new Intent(this, FirstActivity.class);
		startActivity(intent);
		finish();
	}

	/**
	 * Выполняет запрос с номером теста на сервер для получения информации по
	 * тесту для его дальнейшего выполнения и отправляет пользователя в окно
	 * прохождения теста {@link RunTestActivity}. Если теста с таким номером не
	 * существует, выводится соответствующее сообщение.
	 * 
	 * @param v
	 *            Кнопка "Продолжить".
	 */
	@SuppressLint("DefaultLocale")
	public void toID(View v) {
		id = editId.getText().toString();

		if (id.length() < LENGTH)
			Utils.showToast(this, getString(R.string.errformat));
		else
			try {
				if (Utils.isConnected()) {
					task = new Task();
					task.addParam("id", id.toUpperCase());
					task.setContext(this);
					button = (Button) findViewById(R.id.toID);
					button.setEnabled(false);
					task.execute(RequestTask.DOMAIN + "/api/scripts/gettest.php");
				} else
					Utils.showToast(this, getString(R.string.noconnect));
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
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
	 * классе {@link RequestTask}
	 */
	class Task extends RequestTask {

		/**
		 * Обрабатывает ответ от сервера. Если получен тест, переводит
		 * пользователя в окно прохождения теста {@link RunTestActivity}.
		 * 
		 * @param result
		 *            Ответ от сервера. Если "true", теста с введённым номером
		 *            не существует.
		 */
		@Override
		protected void onPostExecute(String result) {

			super.dialog.dismiss();

			try {
				button.setEnabled(true);
				if (Boolean.valueOf(result)) {
					Utils.showToast(SetIdActivity.this, getString(R.string.errid));
				} else {
					Intent intent = new Intent(getApplicationContext(), RunTestActivity.class);
					intent.putExtra("testJSON", result);
					startActivity(intent);
					finish();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			super.onPostExecute(result);

		}
	}
}