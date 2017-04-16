package app.student;

import java.io.IOException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnCancelListener;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.text.Html;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Активность для просмотра результата прохождения теста и отправки его на
 * электронную почту.
 * 
 * @autor Кирилл Малышев
 * @version 1.0
 */
public class ActivityResult extends Activity {

	/** Текстовое окно для отображения результата. */
	TextView text;

	/** Всплывающее диалоговое окно для ввода e-mail. */
	AlertDialog.Builder ad;

	/** Резульататп прохождения в формате html-разметки. */
	String result;

	/** Номер результата прохождения. */
	String uuid;

	/** Имя файла для хранения информации об ученике. */
	final String DATA_FOR_AUTH = "DataAboutStudent";

	/**
	 * Электронный адрес, на который следует отправить результат прохождения
	 * теста.
	 */
	String email;

	/**
	 * Объект для запуска потока, отправляющего запрос на сервер.
	 * 
	 * @see ActivityResult.Task
	 */
	RequestTask task;

	/**
	 * Устанавливает разметку и выводит результат прохождения теста в текстовое
	 * поле {@link ActivityResult#text}
	 */
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.result);
		text = (TextView) findViewById(R.id.result);

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			result = extras.getString("result");
			uuid = extras.getString("uuid");
		}
		text.setText(Html.fromHtml(result));
	}

	/**
	 * Открывает диалоговое окно для ввода адреса электронной почты, на который
	 * нужно отправить результат. Делает запрос на сервер для отправки
	 * результата на него {@link ActivityResult#email}.
	 * 
	 * @param v
	 *            Кнопка "E-mail".
	 */
	public void toMail(View v) {
		ad = new AlertDialog.Builder(this);
		final EditText editText = new EditText(this);
		editText.setSingleLine(true);
		final SharedPreferences sharedPref = getSharedPreferences(DATA_FOR_AUTH, Context.MODE_PRIVATE);
		email = sharedPref.getString("email", "");
		editText.setText(email);
		ad.setTitle(R.string.mailresult);
		ad.setMessage(R.string.noemail);
		ad.setView(editText);
		ad.setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				email = editText.getText().toString().trim();
				if (email.equals("")) {
					Utils.showToast(ActivityResult.this, getString(R.string.noname));
					return;
				} else if (!Utils.isValidEmail(email)) {
					Utils.showToast(ActivityResult.this, getString(R.string.erremail));
					return;
				} else
					try {
						if (Utils.isConnected()) {
							Editor editor = sharedPref.edit();
							editor.putString("email", email);
							editor.apply();

							task = new Task();
							task.setContext(ActivityResult.this);
							task.addParam("email", email);
							task.addParam("result", result);
							task.addParam("uuid", uuid);
							task.execute(RequestTask.DOMAIN + "/api/scripts/mailresult.php");

						} else
							Utils.showToast(ActivityResult.this, getString(R.string.noconnect));
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
		ad.setOnCancelListener(new OnCancelListener() {
			public void onCancel(DialogInterface dialog) {
			}
		});

		ad.show();
	}

	/**
	 * Переводит пользователя в стартовое окно приложения.
	 * 
	 * @see FirstActivity
	 * @param v
	 *            Кнопка "Назад".
	 */
	public void back(View v) {
		Intent intent = new Intent(this, FirstActivity.class);
		startActivity(intent);
		finish();
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
		 * Обрабатывает ответ от сервера. Выводит сообщения об удачной или
		 * неудачной отпкавке результата прохождения на e-mail.
		 * 
		 * @param result
		 *            Ответ от сервера. Если "true": успешная отправка. Иначе
		 *            произошла ошибка.
		 * 
		 */
		@Override
		protected void onPostExecute(String result) {

			super.dialog.dismiss();

			try {
				if (Boolean.valueOf(result))
					Utils.showToast(ActivityResult.this, getString(R.string.succesfulmail));
				else
					Utils.showToast(ActivityResult.this, getString(R.string.errormail));

			} catch (Exception e) {
				e.printStackTrace();
			}

			super.onPostExecute(result);

		}
	}
}