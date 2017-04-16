package app.student;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * Класс для отправки POST-запроса на сервер.
 *
 * @version 1.0
 * @autor Кирилл Малышев
 */
public class RequestTask extends AsyncTask<String, String, String> {

	/**
	 * Адрес сервера, на который посылаются запросы.
	 */
	public static final String DOMAIN = "https://www.malishevkir.fvds.ru";
	final String USER_AGENT = "Project For Step In Future By Kirill Malyshev 1.0";
	/**
	 * Диалог закрузки
	 */
	protected ProgressDialog dialog;
	/**
	 * Список параметров
	 */
	HashMap<String, String> postDataParams = new HashMap<String, String>();
	/**
	 * Контекст активности
	 */
	Context context;

	/**
	 * Добавляет параметр в массив {@link RequestTask#postDataParams}
	 *
	 * @param key   Ключ.
	 * @param param Значение параметра.
	 */
	public void addParam(String key, String param) {
		postDataParams.put(key, param);
	}

	/**
	 * Задаёт контекст {@link RequestTask#context}
	 *
	 * @param context Контекст.
	 */
	public void setContext(Context context) {
		this.context = context;
	}

	private String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException {
		StringBuilder result = new StringBuilder();
		boolean first = true;
		for (Map.Entry<String, String> entry : params.entrySet()) {
			if (first)
				first = false;
			else
				result.append("&");

			result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
			result.append("=");
			result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
		}

		return result.toString();
	}

	/**
	 * Соединяется с сервером и получает ответ
	 *
	 * @param params Адрес, на который отправляется запрос.
	 */
	@Override
	protected String doInBackground(String... params) {
		try {
			URL url = new URL(params[0]);

			HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
			conn.setReadTimeout(15000);
			conn.setConnectTimeout(15000);
			conn.setRequestMethod("POST");
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setRequestProperty("User-Agent", USER_AGENT);


			OutputStream os = conn.getOutputStream();
			BufferedWriter writer = new BufferedWriter(
					new OutputStreamWriter(os, "UTF-8"));


			writer.write(getPostDataString(postDataParams));

			writer.flush();
			writer.close();
			os.close();
			int responseCode = conn.getResponseCode();
			String response = "";
			if (responseCode == HttpsURLConnection.HTTP_OK) {
				String line;
				BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
				while ((line = br.readLine()) != null) {
					response += line;
				}
			} else {
				response = "";

			}
			return response;

		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}


	/**
	 * Переопределяется в активности, где посылается запрос.
	 *
	 * @param res Ответ сервера.
	 */
	@Override
	protected void onPostExecute(String res) {
		super.onPostExecute(res);
	}

	/**
	 * Запускает прогресс-диалог {@link RequestTask#dialog}.
	 */
	@Override
	protected void onPreExecute() {
		dialog = new ProgressDialog(context);
		dialog.setMessage(context.getResources().getString(R.string.wait));
		dialog.setIndeterminate(true);
		dialog.setCancelable(false);
		dialog.show();
	}
}