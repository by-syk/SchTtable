package com.by_syk.schttable.util;

import android.text.InputFilter;
import android.text.Spanned;

import java.util.regex.Pattern;

/**
 * Created by By_syk on 2016-11-30.
 */

public class AccountInputFilter implements InputFilter {
    private Pattern pattern;

    public AccountInputFilter(String regex) {
        if (regex == null) {
            regex = "[0-9A-Za-z]+";
        }
        pattern = Pattern.compile(regex);
    }

    @Override
    public CharSequence filter(CharSequence charSequence, int i, int i1, Spanned spanned, int i2, int i3) {
        if (!pattern.matcher(charSequence.toString()).matches()) {
            return "";
        }
        return null;
    }
}
