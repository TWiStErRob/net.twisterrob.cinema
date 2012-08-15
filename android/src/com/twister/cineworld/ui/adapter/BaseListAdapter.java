package com.twister.cineworld.ui.adapter;

import java.util.*;

import android.content.Context;
import android.view.*;
import android.widget.BaseAdapter;

public abstract class BaseListAdapter<T, VH> extends BaseAdapter {
	protected List<T>	           m_items;
	protected final Context	       m_context;
	protected final LayoutInflater	m_inflater;

	public BaseListAdapter(final Context context, final Collection<T> items) {
		this(context, items instanceof List? (List<T>) items : new ArrayList<T>(items));
	}

	public BaseListAdapter(final Context context, final List<T> items) {
		this.m_context = context;
		this.m_inflater = LayoutInflater.from(m_context);
		setItems(items);
	}

	public int getCount() {
		return m_items.size();
	}

	public T getItem(final int position) {
		return m_items.get(position);
	}

	public long getItemId(final int position) {
		return position;
	}

	public List<T> getItems() {
		return m_items;
	}

	public void setItems(List<T> items) {
		if (items == null) {
			items = Collections.<T> emptyList();
		}
		m_items = items;
	}

	@SuppressWarnings("unchecked")
	public View getView(final int position, View convertView, final ViewGroup parent) {
		T currentItem = m_items.get(position);
		VH holder;
		if (convertView == null) {
			convertView = m_inflater.inflate(getItemLayoutId(), null);

			holder = createHolder(convertView);
			bindModel(holder, currentItem);

			convertView.setTag(holder);
		} else {
			holder = (VH) convertView.getTag();
		}

		bindView(holder, currentItem, convertView);

		return convertView;
	}

	@SuppressWarnings("unchecked")
	@Override
	public View getDropDownView(final int position, View convertView, final ViewGroup parent) {
		T currentItem = m_items.get(position);
		VH holder;
		if (convertView == null) {
			convertView = m_inflater.inflate(getDropDownItemLayoutId(), null);

			holder = createDropDownHolder(convertView);
			bindDropDownModel(holder, currentItem);

			convertView.setTag(holder);
		} else {
			holder = (VH) convertView.getTag();
		}

		bindDropDownView(holder, currentItem, convertView);

		return convertView;
	}

	protected abstract int getItemLayoutId();

	protected abstract VH createHolder(View convertView);

	protected void bindModel(final VH holder, final T currentItem) {
	}

	protected abstract void bindView(VH holder, T currentItem, View convertView);

	protected int getDropDownItemLayoutId() {
		return getItemLayoutId();
	}

	protected VH createDropDownHolder(final View convertView) {
		return createHolder(convertView);
	}

	protected void bindDropDownView(final VH holder, final T currentItem, final View convertView) {
		bindView(holder, currentItem, convertView);
	}

	protected void bindDropDownModel(final VH holder, final T currentItem) {
		bindModel(holder, currentItem);
	}
}
