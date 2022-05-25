package com.example.zooseeker;


import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withParent;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;

import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.test.espresso.ViewInteraction;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class RoutePlanSummaryTest {

    @Rule
    public ActivityScenarioRule<MainActivity> mActivityScenarioRule =
            new ActivityScenarioRule<>(MainActivity.class);

    @Test
    public void routePlanSummaryTest() {
        // Click "Clear" button to reset selected exhibits
        onView(allOf(withId(R.id.clearExhibitsBtn), withText("Clear"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                7),
                        isDisplayed())).perform(click());


        // Click directions button without exhibits selected
        onView(allOf(withId(R.id.getDirectionsButton), withText("Get Directions"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                8),
                        isDisplayed())).perform(click());

        // Assert if alert appears
        onView(withText("Alert!")).check(matches(isDisplayed()));

        // Go back to summary screen
        ViewInteraction materialButton2 = onView(
                allOf(withId(android.R.id.button1), withText("Ok"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.ScrollView")),
                                        0),
                                3)));
        materialButton2.perform(scrollTo(), click());

        // Select Gorillas and Alligators, Click "Get Directions" button
        ViewInteraction materialCheckBox = onView(
                allOf(withId(R.id.added),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.rvExhibits),
                                        0),
                                0),
                        isDisplayed()));
        materialCheckBox.perform(click());

        ViewInteraction materialCheckBox2 = onView(
                allOf(withId(R.id.added),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.rvExhibits),
                                        3),
                                0),
                        isDisplayed()));
        materialCheckBox2.perform(click());

        ViewInteraction materialButton3 = onView(
                allOf(withId(R.id.getDirectionsButton), withText("Get Directions"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                8),
                        isDisplayed()));
        materialButton3.perform(click());

        // Assert "Gate to Alligators" on Summary RecyclerView
        onView(allOf(withId(R.id.summary_item_text), withText("Gate to Alligators (110 meters)"),
                        withParent(withParent(withId(R.id.direction_items))),
                        isDisplayed())).check(matches(withText("Gate to Alligators (110 meters)")));

        //Assert "Alligators to Gorillas" on Summary RecyclerView
        onView(allOf(withId(R.id.summary_item_text), withText("Alligators to Gorillas (300 meters)"),
                        withParent(withParent(withId(R.id.direction_items))),
                        isDisplayed())).check(matches(withText("Alligators to Gorillas (300 meters)")));

        //Assert "Gorillas to Gate" on Summary RecyclerView
        onView(allOf(withId(R.id.summary_item_text), withText("Gorillas to Gate (210 meters)"),
                        withParent(withParent(withId(R.id.direction_items))),
                        isDisplayed())).check(matches(withText("Gorillas to Gate (210 meters)")));

        // Assert that "Back" button is on summary screen
        onView(allOf(withId(R.id.back_btn), withText("BACK"),
                        withParent(withParent(withId(android.R.id.content))),
                        isDisplayed())).check(matches(isDisplayed()));

        // Assert that "Go" button is on summary screen
        onView(allOf(withId(R.id.go_btn), withText("GO"),
                        withParent(withParent(withId(android.R.id.content))),
                        isDisplayed())).check(matches(isDisplayed()));

        // Click "Back" to test persistence of checked exhibits still on search screen
        onView(allOf(withId(R.id.back_btn), withText("BACK"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                1),
                        isDisplayed())).perform(click());

        // TO DO: Check checkboxes checkled, use the checkbox test on main to see how to find the ViewInteraction
        //Assert "Alligators to Gorillas" on Summary RecyclerView
        onView(allOf(withId(R.id.added), withText("Alligators"),
                withParent(withParent(withId(R.id.rvExhibits))),
                isDisplayed())).check(matches(withText("Alligators to Gorillas (300 meters)")));
        ViewInteraction checkBox = onView(
                allOf(withId(R.id.added),
                        withParent(withParent(withId(R.id.rvExhibits))),
                        isDisplayed()));
        checkBox.check(matches(isDisplayed()));

        ViewInteraction checkBox2 = onView(
                allOf(withId(R.id.added),
                        withParent(withParent(withId(R.id.rvExhibits))),
                        isDisplayed()));
        checkBox2.check(matches(isDisplayed()));

        ViewInteraction materialButton5 = onView(
                allOf(withId(R.id.getDirectionsButton), withText("Get Directions"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                8),
                        isDisplayed()));
        materialButton5.perform(click());

        ViewInteraction materialButton6 = onView(
                allOf(withId(R.id.go_btn), withText("GO"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                2),
                        isDisplayed()));
        materialButton6.perform(click());

        ViewInteraction textView5 = onView(
                allOf(withId(R.id.header_txt), withText("Gate to Alligators"),
                        withParent(withParent(withId(android.R.id.content))),
                        isDisplayed()));
        textView5.check(matches(withText("Gate to Alligators")));

        ViewInteraction button3 = onView(
                allOf(withId(R.id.previous_btn), withText("PREVIOUS"),
                        withParent(withParent(withId(android.R.id.content))),
                        isDisplayed()));
        button3.check(matches(isDisplayed()));

        ViewInteraction button4 = onView(
                allOf(withId(R.id.exit_btn), withText("EXIT"),
                        withParent(withParent(withId(android.R.id.content))),
                        isDisplayed()));
        button4.check(matches(isDisplayed()));

        ViewInteraction button5 = onView(
                allOf(withId(R.id.next_btn), withText("NEXT"),
                        withParent(withParent(withId(android.R.id.content))),
                        isDisplayed()));
        button5.check(matches(isDisplayed()));
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
