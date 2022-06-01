//package com.example.zooseeker;
//
//import static org.junit.Assert.assertFalse;
//import static org.junit.Assert.assertNotNull;
//import static org.junit.Assert.assertTrue;
//
//import android.widget.CheckBox;
//
//import androidx.lifecycle.Lifecycle;
//import androidx.recyclerview.widget.RecyclerView;
//import androidx.test.core.app.ActivityScenario;
//import androidx.test.ext.junit.runners.AndroidJUnit4;
//
//import org.junit.Test;
//import org.junit.runner.RunWith;
//
//@RunWith(AndroidJUnit4.class)
//public class CheckBoxTest {
//    /**
//     * Name:     testCheckBox
//     * Behavior: Verify that the checkBox returns True after the first checkbox is set to true.
//     *           Then, set the checkBox to False and verify that the first checkbox is set to false.
//     *
//     */
//    @Test
//    public void testCheckBox() {
//        // Start an instance of main activity for testing.
//        ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class);
//        scenario.moveToState(Lifecycle.State.CREATED);
//        scenario.moveToState(Lifecycle.State.STARTED);
//        scenario.moveToState(Lifecycle.State.RESUMED);
//
//        scenario.onActivity(activity -> {
//            // Create a recyclerView and set it to the first checkbox on the screen.
//            RecyclerView recyclerView = activity.recyclerView;
//            RecyclerView.ViewHolder firstVH = recyclerView.findViewHolderForAdapterPosition(0);
//            assertNotNull(firstVH);
//            CheckBox checkBox = firstVH.itemView.findViewById(R.id.exhibit_item_checkBox);
//
//            // Set the first checkbox to True and verify that it is true.
//            checkBox.setChecked(true);
//            assertTrue(checkBox.isChecked());
//
//            // Set the second checkbox to False and verify that it is false.
//            checkBox.setChecked(false);
//            assertFalse(checkBox.isChecked());
//        });
//    }
//
//}
