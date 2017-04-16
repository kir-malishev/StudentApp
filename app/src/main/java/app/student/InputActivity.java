package app.student;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

/**
 * Активность для ответа на вопрос с вводом ответа.
 *
 * @autor Кирилл Малышев
 * @version 1.0
 */
public class InputActivity extends Activity {

	/** Индекс вопроса (номер вопроса - 1). */
	int position;

	/** Текстовое поле для ввода ответа на вопрос. */
	EditText editText;

	/** Устанавливает разметку. Выводит на экран информацию о вопросе.
	 *
	 * @param savedInstanceState
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			position = extras.getInt("position");
		}

		setContentView(R.layout.input);
		if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

		TextView questionLabel = (TextView) findViewById(R.id.questionlabel);
		questionLabel.setText(getString(R.string.numberqq) + (position + 1) + "\n" + getString(R.string.inputans));

		TextView question = (TextView) findViewById(R.id.question);
		question.setText(RunTestActivity.data_for_test.get(position).get("qq"));

		editText = (EditText) findViewById(R.id.answer);

		if (RunTestActivity.userAns.get(position).size() > 0)
			editText.setText(RunTestActivity.userAns.get(position).get(0));

		editText.addTextChangedListener(new TextWatcher() {
			public void afterTextChanged(Editable s) {
			}

			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			public void onTextChanged(CharSequence s, int start, int before, int count) {

				RunTestActivity.userAns.get(position).set(0, s.toString().trim());
			}
		});
		
		TextView viewPoints = (TextView) findViewById(R.id.points);
		viewPoints.setText(getString(R.string.points) + RunTestActivity.pointsArray[position]);

		ScrollView view = (ScrollView) findViewById(R.id.scrollView3);
		view.setOnTouchListener(new OnSwipeTouchListener(getApplicationContext()) {

			public void onSwipeLeft() {
				if (position + 1 < RunTestActivity.answers.size()) {
					swipe(position + 1);
				}
			}

			public void onSwipeRight() {
				if (position > 0) {
					swipe(position - 1);
				}
			}
		});

	}

	/**Переводит пользователя к следующему вопросу.
	 *
	 * @param v Кнопка "Вперёд".
	 */
	public void next(View v) {
		if (position + 1 < RunTestActivity.answers.size())
			swipe(position + 1);
	}

	/**Переводит пользователя к предыдущему вопросу.
	 *
	 * @param v Кнопка "Назад".
	 */
	public void back(View v) {
		if (position > 0)
			swipe(position - 1);
	}

	/** Переводит пользователя к другому вопросу.
	 *
	 * @param position Индекс вопроса (номер вопроса - 1).
	 */
	public void swipe(int position) {
		String type = RunTestActivity.qq.get(position).getType();

		Intent intent = new Intent(this, RunTestActivity.class);
		if (type.equals("choice")) {
			intent = new Intent(this, ChoiceActivity.class);
		} else if (type.equals("multiple")) {
			intent = new Intent(this, MultipleActivity.class);
		} else if (type.equals("input")) {
			intent = new Intent(this, InputActivity.class);
		}
		intent.putExtra("position", position);
		intent.putExtra("type", RunTestActivity.qq.get(position).getType());
		startActivity(intent);
		finish();
	}

	/**Закрывает окно и возвращает пользователя к списку вопросов.
	 *
	 * @see RunTestActivity
	 *
	 * @param v Кнопка "К списку вопросов".
	 */
	public void toList(View v) {
		finish();
	}

	 private void hideKeyboard() {
		 InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		 imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
	 }

	/** Закрывает клавиатуру, если произедено касание какого-либо из полей. */
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		if (ev.getAction() ==  MotionEvent.ACTION_DOWN) hideKeyboard();
		return super.dispatchTouchEvent(ev);
	}

}
