package com.twister.cineworld.exception;

import com.twister.cineworld.R;
import com.twister.cineworld.ui.Translated;

public class ExternalException extends ApplicationException {

	private static final long	serialVersionUID	= 6036021854062972178L;

	public enum System implements Translated {
		CINEWORLD(R.string.system_cineworld);

		private int	m_resId;

		private System(final int resId) {
			m_resId = resId;
		}

		public int getResId() {
			return m_resId;
		}

		public Object[] getParams() {
			return null;
		}
	}

	public ExternalException(final System system) {
		super(ErrorType.EXTERNAL, new Object[] { system });
	}

	public ExternalException(final String message, final System system) {
		super(ErrorType.EXTERNAL, message, new Object[] { system });
	}

	public ExternalException(final String message, final Exception cause, final System system) {
		super(ErrorType.EXTERNAL, message, cause, new Object[] { system });
	}

}
