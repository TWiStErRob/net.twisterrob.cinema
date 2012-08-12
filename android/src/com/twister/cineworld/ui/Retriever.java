package com.twister.cineworld.ui;

/**
 * Abstraction to handle retrieving an item, then doing some possible transformative processing on it, and in the end updating the display with the final
 * result.
 * 
 * @author papp.robert.s
 * @param <RawItem> The type of item returned by the lower data handling layers
 * @param <UIItem> The type of item handled on the UI
 * @see RetrieverExecutor
 */
public interface Retriever<RawItem, UIItem> {
	public RawItem retrieve();

	public UIItem process(final RawItem item);

	public void update(final UIItem result);
}
