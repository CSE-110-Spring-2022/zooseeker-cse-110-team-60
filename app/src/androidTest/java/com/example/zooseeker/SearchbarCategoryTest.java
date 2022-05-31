package com.example.zooseeker;


import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withParent;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.test.espresso.ViewInteraction;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import androidx.test.rule.GrantPermissionRule;
import androidx.test.runner.AndroidJUnit4;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class SearchbarCategoryTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Rule
    public GrantPermissionRule mGrantPermissionRule = GrantPermissionRule.grant(
            "android.permission.ACCESS_FINE_LOCATION", "android.permission.ACCESS_COARSE_LOCATION");

    @Test
    public void searchbarCategoryTest() {
        ViewInteraction materialAutoCompleteTextView = onView(
                allOf(withId(R.id.main_searchBar),
                      childAtPosition(
                              childAtPosition(
                                      withId(android.R.id.content),
                                      0),
                              0),
                      isDisplayed()));
        materialAutoCompleteTextView.perform(click());

        ViewInteraction materialAutoCompleteTextView2 = onView(
                allOf(withId(R.id.main_searchBar),
                      childAtPosition(
                              childAtPosition(
                                      withId(android.R.id.content),
                                      0),
                              0),
                      isDisplayed()));
        materialAutoCompleteTextView2.perform(replaceText("reptile"), closeSoftKeyboard());

        ViewInteraction textView = onView(
                allOf(withId(R.id.node_name), withText("Crocodiles"),
                      withParent(withParent(withId(R.id.main_exhibitsRecyclerView))),
                      isDisplayed()));
        textView.check(matches(withText("Crocodiles")));
    }

    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                       && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }
}