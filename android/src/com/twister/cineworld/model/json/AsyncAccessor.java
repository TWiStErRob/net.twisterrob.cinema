package com.twister.cineworld.model.json;

import java.util.List;

public interface AsyncAccessor<RawResponse, UIResponse> {
	public List<RawResponse> getList();

	public List<UIResponse> postProcess(final List<RawResponse> list);

	public void updateUI(final List<UIResponse> result);
}
