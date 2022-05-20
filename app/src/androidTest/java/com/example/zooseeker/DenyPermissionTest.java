//package com.example.zooseeker;
//
//
//import static androidx.test.espresso.Espresso.onView;
//import static androidx.test.espresso.action.ViewActions.click;
//import static androidx.test.espresso.action.ViewActions.scrollTo;
//import static androidx.test.espresso.assertion.ViewAssertions.matches;
//import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
//import static androidx.test.espresso.matcher.ViewMatchers.withId;
//import static androidx.test.espresso.matcher.ViewMatchers.withParent;
//import static androidx.test.espresso.matcher.ViewMatchers.withText;
//import static org.hamcrest.Matchers.allOf;
//
//import android.view.View;
//import android.view.ViewGroup;
//import android.view.ViewParent;
//
//import androidx.test.espresso.ViewInteraction;
//import androidx.test.ext.junit.rules.ActivityScenarioRule;
//import androidx.test.ext.junit.runners.AndroidJUnit4;
//import androidx.test.filters.LargeTest;
//import androidx.test.rule.GrantPermissionRule;
//
//import org.hamcrest.Description;
//import org.hamcrest.Matcher;
//import org.hamcrest.TypeSafeMatcher;
//import org.junit.Rule;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//
//@LargeTest
//@RunWith(AndroidJUnit4.class)
//public class DenyPermissionTest {
//
//    @Rule
//    public ActivityScenarioRule<MainActivity> mActivityScenarioRule =
//            new ActivityScenarioRule<>(MainActivity.class);
//
//    @Rule
//    public GrantPermissionRule mGrantPermissionRule = GrantPermissionRule.grant(
//            "android.permission.ACCESS_FINE_LOCATION", "android.permission.ACCESS_COARSE_LOCATION");
//
//    @Test
//    public void denyPermissionTest() {
//        ViewInteraction textView =
//                onView(allOf(withId(com.android.permissioncontroller.R.id.permission_message), withText("Allow ZooSeeker to access this device’s location?"), withParent(withParent(withId(com.android.permissioncontroller.R.id.content_container))), isDisplayed()));
//        textView.check(matches(withText("Allow ZooSeeker to access this device’s " +
//                                        "location?")));
// // TODO: Espresso cannot record response to permissions; need to use UIAutomator

//        ViewInteraction textView2 = onView(allOf(withId(android.R.id.message),
//                                                 withText("Please go to System Settings" +
//                                                          " to enable Precise Location " +
//                                                          "for ZooSeeker."),
//                                                 withParent(withParent(withId(androidx.appcompat.R.id.scrollView))), isDisplayed()));
//        textView2.check(matches(withText("Please go to System Settings to enable " +
//                                         "Precise Location for ZooSeeker.")));
//
//        ViewInteraction button = onView(allOf(withId(android.R.id.button1), withText(
//                "OK"), withParent(withParent(withId(androidx.appcompat.R.id.buttonPanel))), isDisplayed()));
//        button.check(matches(isDisplayed()));
//
//        ViewInteraction materialButton = onView(allOf(withId(android.R.id.button1),
//                                                      withText("Ok"),
//                                                      childAtPosition(childAtPosition(withId(androidx.appcompat.R.id.buttonPanel), 0), 3)));
//        materialButton.perform(scrollTo(), click());
//    }
//
//    private static Matcher<View> childAtPosition(final Matcher<View> parentMatcher,
//                                                 final int position) {
//
//        return new TypeSafeMatcher<View>() {
//            @Override
//            public void describeTo(Description description) {
//                description.appendText("Child at position " + position + " in parent ");
//                parentMatcher.describeTo(description);
//            }
//
//            @Override
//            public boolean matchesSafely(View view) {
//                ViewParent parent = view.getParent();
//                return parent instanceof ViewGroup && parentMatcher.matches(parent) && view.equals(((ViewGroup) parent).getChildAt(position));
//            }
//        };
//    }
//
//    private void allowPermissionsIfNeeded()  {
//        if (Build.VERSION.SDK_INT >= 23) {
//            UiObject allowPermissions = mDevice.findObject(new UiSelector().text("DENY"));
//            if (allowPermissions.exists()) {
//                try {
//                    allowPermissions.click();
//                } catch (UiObjectNotFoundException e) {
//                    Timber.e(e, "There is no permissions dialog to interact with ");
//                }
//            }
//        }
//    }
//}
