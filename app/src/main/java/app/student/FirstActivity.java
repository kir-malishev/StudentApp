package app.student;

import java.util.Arrays;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

/**
 * Активность для ввода информации об ученике.
 * 
 * @autor Кирилл Малышев
 * @version 1.0
 */
public class FirstActivity extends Activity {

	/** Поле для выбора класса/курса обучения */
	Spinner spinner;

	/** Поле для ввода имени ученика. */
	EditText editName;

	/** Поле для ввода фамилии ученика. */
	EditText editLastName;

	/** Полу для указания буквы класса/курса обучения. */
	EditText editLetter;

	/** Имя ученика */
	public static String whatName;

	/** Фамилия ученика */
	public static String whatLastName;

	/** Класс/курс */
	public static String whatClass;

	/** Буква класса/курса обучения */
	public static String whatLetter;

	/** Имя файла для хранения информации об ученике. */
	final String DATA_FOR_AUTH = "DataAboutStudent";

	/** Список возможных для выбора номеров класса/курса. */
	String[] classes;

	/**
	 * Устанавливает разметку. Выводит сохранённую информацию в поля, есть такая
	 * есть.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		
		 classes = getResources().getStringArray(R.array.classes);

		spinner = (Spinner) findViewById(R.id.classes);

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.style_for_spinner, R.id.spinnertext,
				classes);

		spinner.setAdapter(adapter);

		editName = (EditText) findViewById(R.id.editname);
		editLastName = (EditText) findViewById(R.id.editlastname);
		editLetter = (EditText) findViewById(R.id.letterEdit);

		getData();

	}

	/** Сохраняет в память устройства введённую информацию об ученике. */
	public void saveData() {
		SharedPreferences sharedPref = getSharedPreferences(DATA_FOR_AUTH, Context.MODE_PRIVATE);
		Editor editor = sharedPref.edit();
		whatName = editName.getText().toString();
		whatLastName = editLastName.getText().toString();
		whatClass = spinner.getSelectedItem().toString();
		whatLetter = editLetter.getText().toString();
		editor.putString("class", whatClass);
		editor.putString("name", whatName);
		editor.putString("lastname", whatLastName);
		editor.putString("letter", whatLetter);
		editor.apply();
	}

	/**
	 * Получает из памяти устройства сохранённую информацию об ученике и выводит
	 * её в соответствующие поля.
	 */
	public void getData() {
		SharedPreferences sharedPref = getSharedPreferences(DATA_FOR_AUTH, Context.MODE_PRIVATE);
		editName.setText(sharedPref.getString("name", ""));
		editLastName.setText(sharedPref.getString("lastname", ""));
		editLetter.setText(sharedPref.getString("letter", ""));
		spinner.setSelection(Arrays.asList(classes).indexOf(sharedPref.getString("class", "")));
	}

	/**
	 * Проверяет введённые данные и отправляет пользователя в окно ввода номера
	 * теста.
	 * 
	 * @see SetIdActivity
	 * 
	 * @param v
	 *            Кнопка "Продолжить".
	 */
	public void toID(View v) {
		if ("".equals(editName.getText().toString()))
			Utils.showToast(this, getString(R.string.noname));
		else if ("".equals(editLastName.getText().toString()))
			Utils.showToast(this, getString(R.string.nolastname));
		else {
			saveData();
			Intent intent = new Intent(this, SetIdActivity.class);
			startActivity(intent);
			finish();
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

}
