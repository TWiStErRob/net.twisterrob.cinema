package com.twister.cineworld.ui;

import java.util.List;

public interface Retriever<RawItem, UIItem> {
	public List<RawItem> retrieve();

	public List<UIItem> process(final List<RawItem> list);

	public void update(final List<UIItem> result);
}
