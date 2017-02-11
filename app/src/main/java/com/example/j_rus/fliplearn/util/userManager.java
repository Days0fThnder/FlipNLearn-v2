package com.example.j_rus.fliplearn.util;

import android.text.TextUtils;

/**
 * Created by j_rus on 2/11/2017.
 */

public class UserManager {

    public static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}
