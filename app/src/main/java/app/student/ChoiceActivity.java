package app.student;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;

/**
 * Активность для ответа на вопрос с выбором одного ответа из нескольких.
 *
 * @autor Кирилл Малышев
 * @version 1.0
 */
public class ChoiceActivity extends Activity {

	/** Индекс вопроса (номер вопроса - 1). */
	int position;

	/** Группа радиокнопок для выбора ответа. */
	RadioGroup answers;

	/** Устанавливает разметку. Выводит на экран информацию о вопросе
	 * и вариантах ответа.
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

		setContentView(R.layout.choice);
		if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

		TextView questionLabel = (TextView) findViewById(R.id.questionlabel);
		questionLabel.setText(getString(R.string.numberqq) + (position + 1) + "\n" + getString(R.string.choiceans));

		TextView question = (TextView) findViewById(R.id.question);
		question.setText(RunTestActivity.data_for_test.get(position).get("qq"));

		answers = (RadioGroup) findViewById(R.id.answers);
		addAns();

		answers.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				RunTestActivity.userAns.get(position).clear();
				RunTestActivity.userAns.get(position).add(RunTestActivity.answers.get(position)[checkedId]);
			}
		});

		TextView viewPoints = (TextView) findViewById(R.id.points);
		viewPoints.setText(getString(R.string.points) + RunTestActivity.pointsArray[position]);

		ScrollView view = (ScrollView) findViewById(R.id.scrollView3);
		view.setOnTouchListener(new OnSwipeTouchListener(getApplicationContext()) {

			public void onSwipeLeft() {
				if (position + 1 < RunTestActivity.answers.size())
					swipe(position + 1);
			}

			public void onSwipeRight() {
				if (position > 0)
					swipe(position - 1);
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
		intent.putExtra("type", type);
		startActivity(intent);
		finish();
	}

	/** Добавляет на экран варианты ответа. */
	@SuppressLint("ResourceAsColor")
	public void addAns() {
		for (int i = 0; i < RunTestActivity.answers.get(position).length; i++) {

			RadioButton answer = new RadioButton(this);
			answer.setTextColor(getResources().getColor(R.color.light_color));
			answer.setText(RunTestActivity.answers.get(position)[i]);
			answer.setId(i);
			if (RunTestActivity.userAns.get(position).contains(RunTestActivity.answers.get(position)[i]))
				answer.setChecked(true);

			answers.addView(answer);
		}
	}

	/**Закрывает окно и возвращает пользователя к списку вопросов.
	 *
	 *  @see RunTestActivity
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
		if (ev.getAction() == MotionEvent.ACTION_DOWN)
			hideKeyboard();
		return super.dispatchTouchEvent(ev);
	}
}
