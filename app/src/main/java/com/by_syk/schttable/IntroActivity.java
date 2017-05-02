package com.by_syk.schttable;

import com.stephentuso.welcome.BasicPage;
import com.stephentuso.welcome.WelcomeActivity;
import com.stephentuso.welcome.WelcomeConfiguration;

/**
 * Created by By_syk on 2017-05-02.
 */

public class IntroActivity extends WelcomeActivity {
    @Override
    protected WelcomeConfiguration configuration() {
        return new WelcomeConfiguration.Builder(this)
                .defaultBackgroundColor(R.color.intro_1)
                .page(new BasicPage(R.drawable.ic_intro_1,
                        getString(R.string.intro_title_1),
                        getString(R.string.intro_desc_1))
                        .background(R.color.intro_1))
                .page(new BasicPage(R.drawable.ic_intro_2,
                        getString(R.string.intro_title_2),
                        getString(R.string.intro_desc_2))
                        .background(R.color.intro_2))
                .page(new BasicPage(R.drawable.intro_course,
                        getString(R.string.intro_title_3),
                        getString(R.string.intro_desc_3))
                        .background(R.color.intro_3))
                .page(new BasicPage(R.drawable.intro_appwidget,
                        getString(R.string.intro_title_4),
                        getString(R.string.intro_desc_4))
                        .background(R.color.intro_4))
                .bottomLayout(WelcomeConfiguration.BottomLayout.STANDARD_DONE_IMAGE)
                .canSkip(false)
//                .swipeToDismiss(true)
                .exitAnimation(android.R.anim.fade_out)
                .build();
    }

//    public static String welcomeKey() {
//        return "welcome";
//    }
}
