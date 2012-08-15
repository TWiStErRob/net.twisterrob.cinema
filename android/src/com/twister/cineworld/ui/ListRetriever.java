package com.twister.cineworld.ui;

import java.util.List;

/**
 * Abstraction to handle retrieving a list of items, then doing some possible transformative processing on them, and in
 * the end updating the display with the final results.
 * 
 * @author papp.robert.s
 * @param <RawItem> The type of items returned by the lower data handling layers
 * @param <UIItem> The type of items handled on the UI
 */
public interface ListRetriever<RawItem, UIItem> extends Retriever<List<RawItem>, List<UIItem>> {
}
