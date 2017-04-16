package app.student;

import java.util.ArrayList;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * Адаптер для ListView.
 * 
 * @autor Кирилл Малышев
 * @version 1.0
 */
public class MyAdapter extends BaseAdapter {

	ArrayList<Item> data = new ArrayList<Item>();
	Context context;

	public MyAdapter(Context context, ArrayList<Item> arr) {
		if (arr != null) {
			data = arr;
		}
		this.context = context;
	}

	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public Object getItem(int num) {
		return data.get(num);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	/** Создаёт элемент ListView */
	@Override
	public View getView(int i, View someView, ViewGroup arg2) {
		LayoutInflater inflater = LayoutInflater.from(context);
		if (someView == null) {
			someView = inflater.inflate(R.layout.list_view_item, arg2, false);
		}
		TextView header = (TextView) someView.findViewById(R.id.item_headerText);
		TextView subHeader = (TextView) someView.findViewById(R.id.item_subHeaderText);
		if ("choice".equals(data.get(i).type)) {
			header.setTextColor(context.getResources().getColor(R.color.orange_color));
		} else if ("multiple".equals(data.get(i).type)) {
			header.setTextColor(context.getResources().getColor(R.color.white_color));
		} else if ("input".equals(data.get(i).type)) {
			header.setTextColor(context.getResources().getColor(R.color.yellow_color));
		}
		header.setText(data.get(i).header);
		subHeader.setText(data.get(i).subHeader);
		return someView;
	}

}