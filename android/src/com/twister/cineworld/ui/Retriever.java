package com.twister.cineworld.ui;

import java.util.List;

/**
 * Abstraction to handle retrieving an item, then doing some possible transformative processing on it, and in the end updating the display with the final
 * result.
 * 
 * @author papp.robert.s
 * @param <RawItem> The type of items returned by the lower data handling layers
 * @param <UIItem> The type of items handled on the UI
 * @see RetrieverExecutor
 */
public interface Retriever<RawItem, UIItem> {
	public List<RawItem> retrieve();

	public List<UIItem> process(final List<RawItem> list);

	public void update(final List<UIItem> result);
}
