package com.twister.cineworld.ui;

import java.util.List;

/**
 * Implementations should execute the {@link Retriever} provided based on their willings. However some basic principles should hold.
 * 
 * @author papp.robert.s
 * @param <RawItem> The type of items returned by the lower data handling layers
 * @param <UIItem> The type of items handled on the UI
 * @see #execute(Retriever)
 */
public interface RetrieverExecutor<RawItem, UIItem> {
	/**
	 * Executes the given retriever with the chosen logic. It can perform caching, logging, asynchronicity, etc.<br>
	 * The logic should follow these rules:
	 * <ul>
	 * <li>{@link Retriever#retrieve()}'s result need to be passed to {@link Retriever#process(List)}</li>
	 * <li>{@link Retriever#process(List)}'s result need to be passed to {@link Retriever#update(List)}</li>
	 * <li>{@link Retriever#retrieve()}'s result should not be modified</li>
	 * <li>{@link Retriever#process(List)}'s result should not be modified</li>
	 * </ul>
	 * 
	 * So a trivial implentation looks something like this:
	 * 
	 * <pre>
	 * retriever.update(retriever.process(retriever.retrieve()));
	 * </pre>
	 * 
	 * @param retriever to execute
	 */
	void execute(Retriever<RawItem, UIItem> retriever);
}
