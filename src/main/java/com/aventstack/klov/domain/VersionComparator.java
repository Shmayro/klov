package com.aventstack.klov.domain;

import java.util.Arrays;
import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

public class VersionComparator implements Comparator<String> {

    @Override
    public int compare(final String v1, final String v2) {
        return Long.compare(createVersionID(v1), createVersionID(v2));
    }
    
    private static String[] getSplittedVersion(final String version) {
        return version.replaceAll("[a-zA-Z-]", "").split("\\.");
    }
    
    public static long createVersionID(final String version) {
		try {
			final Pattern pattern = Pattern.compile("^([0-9]+\\.){3}([0-9]+){1}");
			final Matcher matcher = pattern.matcher(version);
			final String fomatedVersion = matcher.find() ? matcher.group() : version;
			final String[] values = getSplittedVersion(fomatedVersion);
			final String paddedVersion = Arrays.stream(values).map(str -> StringUtils.leftPad(str, 4, '0'))
					.collect(Collectors.joining());
			return Long.parseLong(paddedVersion);
		} catch (final NumberFormatException e) {
			return 0;
		}
	}

}