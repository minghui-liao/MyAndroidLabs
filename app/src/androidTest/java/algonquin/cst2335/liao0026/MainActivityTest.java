package algonquin.cst2335.liao0026;


import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.test.espresso.ViewInteraction;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withParent;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void mainActivityTest() {
        ViewInteraction appCompatEditText = onView(
                allOf(withId(R.id.cityTextField),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                1),
                        isDisplayed()));
        appCompatEditText.perform(click());

        ViewInteraction appCompatEditText2 = onView(
                allOf(withId(R.id.cityTextField),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                1),
                        isDisplayed()));
        appCompatEditText2.perform(replaceText("12345"), closeSoftKeyboard());

//        pressBack();

        ViewInteraction materialButton = onView(
                allOf(withId(R.id.forecastButton), withText("LOGIN"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                2),
                        isDisplayed()));
        materialButton.perform(click());

        ViewInteraction textView = onView(
                allOf(withId(R.id.textView), withText("You shall not pass!"),
                        withParent(withParent(withId(android.R.id.content))),
                        isDisplayed()));
        textView.check(matches(withText("You shall not pass!")));
    }

    /** This function tests a password that is only missing an upper case letter
     *
     */
    @Test
    public void testFindMissingUpperCase() {
        //find the view
        ViewInteraction appCompatEditText = onView( withId(R.id.cityTextField) );
        //type in password123#$*
        appCompatEditText.perform(replaceText(("password123#$*")));

        //find the button
        ViewInteraction materialButton = onView( withId(R.id.forecastButton) );
        //click the button
        materialButton.perform(click());

        //find the text view
        ViewInteraction textView = onView( withId(R.id.textView) );
        //check the text
        textView.check(matches(withText("You shall not pass!")));
    }

    /** This function tests a password that is only missing a lower case letter
     *
     */
    @Test
    public void testFindMissingLowerCase() {
        //find the view
        ViewInteraction appCompatEditText = onView( withId(R.id.cityTextField) );
        //type in PASSWORD123#$*
        appCompatEditText.perform(replaceText(("PASSWORD123#$*")));

        //find the button
        ViewInteraction materialButton = onView( withId(R.id.forecastButton) );
        //click the button
        materialButton.perform(click());

        //find the text view
        ViewInteraction textView = onView( withId(R.id.textView) );
        //check the text
        textView.check(matches(withText("You shall not pass!")));
    }

    /** This function tests a password that is only missing a digit
     *
     */
    @Test
    public void testFindMissingDigit() {
        //find the view
        ViewInteraction appCompatEditText = onView( withId(R.id.cityTextField) );
        //type in Password#$*
        appCompatEditText.perform(replaceText(("Password#$*")));

        //find the button
        ViewInteraction materialButton = onView( withId(R.id.forecastButton) );
        //click the button
        materialButton.perform(click());

        //find the text view
        ViewInteraction textView = onView( withId(R.id.textView) );
        //check the text
        textView.check(matches(withText("You shall not pass!")));
    }

    /** This function tests a password that is only missing a special character
     *
     */
    @Test
    public void testFindMissingSpecialCharacter() {
        //find the view
        ViewInteraction appCompatEditText = onView( withId(R.id.cityTextField) );
        //type in Password123
        appCompatEditText.perform(replaceText(("Password123")));

        //find the button
        ViewInteraction materialButton = onView( withId(R.id.forecastButton) );
        //click the button
        materialButton.perform(click());

        //find the text view
        ViewInteraction textView = onView( withId(R.id.textView) );
        //check the text
        textView.check(matches(withText("You shall not pass!")));
    }

    /** This function tests a password that has a digit, an upper case, a lower case and a special character.
     *
     */
    @Test
    public void testHasAllRequirements() {
        //find the view
        ViewInteraction appCompatEditText = onView( withId(R.id.cityTextField) );
        //type in Password123#$*
        appCompatEditText.perform(replaceText(("Password123#$*")));

        //find the button
        ViewInteraction materialButton = onView( withId(R.id.forecastButton) );
        //click the button
        materialButton.perform(click());

        //find the text view
        ViewInteraction textView = onView( withId(R.id.textView) );
        //check the text
        textView.check(matches(withText("Your password meets the requirements")));
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
