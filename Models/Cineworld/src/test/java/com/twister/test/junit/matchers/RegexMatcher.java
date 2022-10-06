package com.twister.test.junit.matchers;

import java.util.regex.Pattern;

import org.hamcrest.*;

public class RegexMatcher extends BaseMatcher<String> {
	private final Pattern pattern;

	public RegexMatcher(String regex) {
		this(Pattern.compile(regex));
	}

	public RegexMatcher(Pattern pattern) {
		this.pattern = pattern;
	}

	public boolean matches(Object string) {
		return pattern.matcher((CharSequence)string).matches();
	}

	public void describeTo(Description description) {
		description.appendText("string matching ").appendValue(pattern);
	}

	public static RegexMatcher matchesPattern(String regex) {
		return new RegexMatcher(regex);
	}

	public static RegexMatcher matchesPattern(Pattern pattern) {
		return new RegexMatcher(pattern);
	}
}
