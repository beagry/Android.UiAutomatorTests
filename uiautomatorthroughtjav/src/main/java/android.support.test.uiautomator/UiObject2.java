/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package android.support.test.uiautomator;

import android.graphics.Point;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.ViewConfiguration;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * A {@link UiObject2} represents a UI element. Unlike {@link UiObject}, it is bound to a particular
 * view instance and can become stale if the underlying view object is destroyed. As a result, it
 * may be necessary to call {@link UiDevice#findObject(BySelector)} to obtain a new
 * {@link UiObject2} instance if the UI changes significantly.
 */
public class UiObject2 implements Searchable {

    private static final String TAG = UiObject2.class.getSimpleName();

    private UiDevice mDevice;
    private Gestures mGestures;
    private GestureController mGestureController;
    private BySelector mSelector;  // Hold this mainly for debugging
    private AccessibilityNodeInfo mCachedNode;
    private DisplayMetrics mDisplayMetrics;

    // Margins
    private int mMarginLeft   = 5;
    private int mMarginTop    = 5;
    private int mMarginRight  = 5;
    private int mMarginBottom = 5;

    // Default gesture speeds
    private static final int DEFAULT_SWIPE_SPEED  = 5000;
    private static final int DEFAULT_SCROLL_SPEED = 5000;
    private static final int DEFAULT_FLING_SPEED = 7500;
    private static final int DEFAULT_DRAG_SPEED = 2500;
    private static final int DEFAULT_PINCH_SPEED = 2500;
    // Short, since we should stop scrolling after the gesture completes.
    private final long SCROLL_TIMEOUT = 1000;
    // Longer, since we may continue to scroll after the gesture completes.
    private final long FLING_TIMEOUT = 5000;

    // Get wait functionality from a mixin
    private WaitMixin<UiObject2> mWaitMixin = new WaitMixin<UiObject2>(this);


    /** Package-private constructor. Used by {@link UiDevice#findObject(BySelector)}. */
    UiObject2(UiDevice device, BySelector selector, AccessibilityNodeInfo cachedNode) {
        mDevice = device;
        mSelector = selector;
        mCachedNode = cachedNode;
        mGestures = Gestures.getInstance(device);
        mGestureController = GestureController.getInstance(device);
        mDisplayMetrics = mDevice.getAutomatorBridge().getContext().getResources()
                .getDisplayMetrics();
    }

    /** {@inheritDoc} */
    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null) {
            return false;
        }
        if (getClass() != object.getClass()) {
            return false;
        }
        try {
            UiObject2 other = (UiObject2)object;
            return getAccessibilityNodeInfo().equals(other.getAccessibilityNodeInfo());
        } catch (StaleObjectException e) {
            return false;
        }
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode() {
        return getAccessibilityNodeInfo().hashCode();
    }

    /** Recycle this object. */
    public void recycle() {
        mCachedNode.recycle();
        mCachedNode = null;
    }


    // Settings

    /** Sets the margins used for gestures in pixels. */
    public void setGestureMargin(int margin) {
        setGestureMargins(margin, margin, margin, margin);
    }

    /** Sets the margins used for gestures in pixels. */
    public void setGestureMargins(int left, int top, int right, int bottom) {
        mMarginLeft = left;
        mMarginTop = top;
        mMarginRight = right;
        mMarginBottom = bottom;
    }


    // Wait functions

    /**
     * Waits for given the {@code condition} to be met.
     *
     * @param condition The {@link UiObject2Condition} to evaluate.
     * @param timeout Maximum amount of time to wait in milliseconds.
     * @return The final result returned by the condition.
     */
    public <R> R wait(UiObject2Condition<R> condition, long timeout) {
        return mWaitMixin.wait(condition, timeout);
    }

    /**
     * Waits for given the {@code condition} to be met.
     *
     * @param condition The {@link SearchCondition} to evaluate.
     * @param timeout Maximum amount of time to wait in milliseconds.
     * @return The final result returned by the condition.
     */
    public <R> R wait(SearchCondition<R> condition, long timeout) {
        return mWaitMixin.wait(condition, timeout);
    }

    // Search functions

    /** Returns this object's parent. */
    public UiObject2 getParent() {
        AccessibilityNodeInfo parent = getAccessibilityNodeInfo().getParent();
        return parent != null ? new UiObject2(mDevice, mSelector, parent) : null;
    }

    /** Returns the number of child elements directly under this object. */
    public int getChildCount() {
        return getAccessibilityNodeInfo().getChildCount();
    }

    /** Returns a collection of the child elements directly under this object. */
    public List<UiObject2> getChildren() {
        return findObjects(By.depth(1));
    }

    /** Returns whether there is a match for the given criteria under this object. */
    public boolean hasObject(BySelector selector) {
        AccessibilityNodeInfo node =
                ByMatcher.findMatch(mDevice, selector, getAccessibilityNodeInfo());
        if (node != null) {
            node.recycle();
            return true;
        }
        return false;
    }

    /**
     * Searches all elements under this object and returns the first object to match the criteria.
     */
    public UiObject2 findObject(BySelector selector) {
        AccessibilityNodeInfo node =
                ByMatcher.findMatch(mDevice, selector, getAccessibilityNodeInfo());
        return node != null ? new UiObject2(mDevice, selector, node) : null;
    }

    /** Searches all elements under this object and returns all objects that match the criteria. */
    public List<UiObject2> findObjects(BySelector selector) {
        List<UiObject2> ret = new ArrayList<UiObject2>();
        for (AccessibilityNodeInfo node :
                ByMatcher.findMatches(mDevice, selector, getAccessibilityNodeInfo())) {

            ret.add(new UiObject2(mDevice, selector, node));
        }

        return ret;
    }


    // Attribute accessors

    /** Returns the visible bounds of this object in screen coordinates. */
    public Rect getVisibleBounds() {
        return getVisibleBounds(getAccessibilityNodeInfo());
    }

    /** Returns the visible bounds of this object with the margins removed. */
    private Rect getVisibleBoundsForGestures() {
        Rect ret = getVisibleBounds();
        ret.left = ret.left + mMarginLeft;
        ret.top = ret.top + mMarginTop;
        ret.right = ret.right - mMarginRight;
        ret.bottom = ret.bottom - mMarginBottom;
        return ret;
    }

    /** Returns the visible bounds of {@code node} in screen coordinates. */
    private Rect getVisibleBounds(AccessibilityNodeInfo node) {
        // Get the object bounds in screen coordinates
        Rect ret = new Rect();
        node.getBoundsInScreen(ret);

        // Trim any portion of the bounds that are not on the screen
        Rect screen = new Rect(0, 0, mDevice.getDisplayWidth(), mDevice.getDisplayHeight());
        ret.intersect(screen);

        // Find the visible bounds of our first scrollable ancestor
        AccessibilityNodeInfo ancestor = null;
        for (ancestor = node.getParent(); ancestor != null; ancestor = ancestor.getParent()) {
            // If this ancestor is scrollable
            if (ancestor.isScrollable()) {
                // Trim any portion of the bounds that are hidden by the non-visible portion of our
                // ancestor
                Rect ancestorRect = getVisibleBounds(ancestor);
                ret.intersect(ancestorRect);
                break;
            }
        }

        return ret;
    }

    /** Returns a point in the center of the visible bounds of this object. */
    public Point getVisibleCenter() {
        Rect bounds = getVisibleBounds();
        return new Point(bounds.centerX(), bounds.centerY());
    }

    /**
     * Returns the class name of the underlying {@link android.view.View} represented by this
     * object.
     */
    public String getClassName() {
        CharSequence chars = getAccessibilityNodeInfo().getClassName();
        return chars != null ? chars.toString() : null;
    }

    /** Returns the content description for this object. */
    public String getContentDescription() {
        CharSequence chars = getAccessibilityNodeInfo().getContentDescription();
        return chars != null ? chars.toString() : null;
    }

    /** Returns the package name of the app that this object belongs to. */
    public String getApplicationPackage() {
        CharSequence chars = getAccessibilityNodeInfo().getPackageName();
        return chars != null ? chars.toString() : null;
    }

    /** Returns the fully qualified resource name for this object's id. */
    public String getResourceName() {
        CharSequence chars = getAccessibilityNodeInfo().getViewIdResourceName();
        return chars != null ? chars.toString() : null;
    }

    /** Returns the text value for this object. */
    public String getText() {
        CharSequence chars = getAccessibilityNodeInfo().getText();
        return chars != null ? chars.toString() : null;
    }

    /** Returns whether this object is checkable. */
    public boolean isCheckable() {
        return getAccessibilityNodeInfo().isCheckable();
    }

    /** Returns whether this object is checked. */
    public boolean isChecked() {
        return getAccessibilityNodeInfo().isChecked();
    }

    /** Returns whether this object is clickable. */
    public boolean isClickable() {
        return getAccessibilityNodeInfo().isClickable();
    }

    /** Returns whether this object is enabled. */
    public boolean isEnabled() {
        return getAccessibilityNodeInfo().isEnabled();
    }

    /** Returns whether this object is focusable. */
    public boolean isFocusable() {
        return getAccessibilityNodeInfo().isFocusable();
    }

    /** Returns whether this object is focused. */
    public boolean isFocused() {
        return getAccessibilityNodeInfo().isFocused();
    }

    /** Returns whether this object is long clickable. */
    public boolean isLongClickable() {
        return getAccessibilityNodeInfo().isLongClickable();
    }

    /** Returns whether this object is scrollable. */
    public boolean isScrollable() {
        return getAccessibilityNodeInfo().isScrollable();
    }

    /** Returns whether this object is selected. */
    public boolean isSelected() {
        return getAccessibilityNodeInfo().isSelected();
    }


    // Actions

    /** Clears the text content if this object is an editable field. */
    public void clear() {
        setText("");
    }

    /** Clicks on this object. */
    public void click() {
        mGestureController.performGesture(mGestures.click(getVisibleCenter()));
    }

    /** Clicks on this object, and waits for the given condition to become true. */
    public <R> R clickAndWait(EventCondition<R> condition, long timeout) {
        return mGestureController.performGestureAndWait(condition, timeout,
                mGestures.click(getVisibleCenter()));
    }

    /**
     * Drags this object to the specified location.
     *
     * @param dest The end point that this object should be dragged to.
     */
    public void drag(Point dest) {
        drag(dest, (int)(DEFAULT_DRAG_SPEED * mDisplayMetrics.density));
    }

    /**
     * Drags this object to the specified location.
     *
     * @param dest The end point that this object should be dragged to.
     * @param speed The speed at which to perform this gesture in pixels per second.
     */
    public void drag(Point dest, int speed) {
        if (speed < 0) {
            throw new IllegalArgumentException("Speed cannot be negative");
        }
        mGestureController.performGesture(mGestures.drag(getVisibleCenter(), dest, speed));
    }

    /** Performs a long click on this object. */
    public void longClick() {
        mGestureController.performGesture(mGestures.longClick(getVisibleCenter()));
    }

    /**
     * Performs a pinch close gesture on this object.
     *
     * @param percent The size of the pinch as a percentage of this object's size.
     */
    public void pinchClose(float percent) {
        pinchClose(percent, (int)(DEFAULT_PINCH_SPEED * mDisplayMetrics.density));
    }

    /**
     * Performs a pinch close gesture on this object.
     *
     * @param percent The size of the pinch as a percentage of this object's size.
     * @param speed The speed at which to perform this gesture in pixels per second.
     */
    public void pinchClose(float percent, int speed) {
        if (percent < 0.0f || percent > 1.0f) {
            throw new IllegalArgumentException("Percent must be between 0.0f and 1.0f");
        }
        if (speed < 0) {
            throw new IllegalArgumentException("Speed cannot be negative");
        }
        mGestureController.performGesture(
                mGestures.pinchClose(getVisibleBoundsForGestures(), percent, speed));
    }

    /**
     * Performs a pinch open gesture on this object.
     *
     * @param percent The size of the pinch as a percentage of this object's size.
     */
    public void pinchOpen(float percent) {
        pinchOpen(percent, (int)(DEFAULT_PINCH_SPEED * mDisplayMetrics.density));
    }

    /**
     * Performs a pinch open gesture on this object.
     *
     * @param percent The size of the pinch as a percentage of this object's size.
     * @param speed The speed at which to perform this gesture in pixels per second.
     */
    public void pinchOpen(float percent, int speed) {
        if (percent < 0.0f || percent > 1.0f) {
            throw new IllegalArgumentException("Percent must be between 0.0f and 1.0f");
        }
        if (speed < 0) {
            throw new IllegalArgumentException("Speed cannot be negative");
        }
        mGestureController.performGesture(
                mGestures.pinchOpen(getVisibleBoundsForGestures(), percent, speed));
    }

    /**
     * Performs a swipe gesture on this object.
     *
     * @param direction The direction in which to swipe.
     * @param percent The length of the swipe as a percentage of this object's size.
     */
    public void swipe(Direction direction, float percent) {
        swipe(direction, percent, (int)(DEFAULT_SWIPE_SPEED * mDisplayMetrics.density));
    }

    /**
     * Performs a swipe gesture on this object.
     *
     * @param direction The direction in which to swipe.
     * @param percent The length of the swipe as a percentage of this object's size.
     * @param speed The speed at which to perform this gesture in pixels per second.
     */
    public void swipe(Direction direction, float percent, int speed) {
        if (percent < 0.0f || percent > 1.0f) {
            throw new IllegalArgumentException("Percent must be between 0.0f and 1.0f");
        }
        if (speed < 0) {
            throw new IllegalArgumentException("Speed cannot be negative");
        }
        Rect bounds = getVisibleBoundsForGestures();
        mGestureController.performGesture(mGestures.swipeRect(bounds, direction, percent, speed));
    }

    /**
     * Performs a scroll gesture on this object.
     *
     * @param direction The direction in which to scroll.
     * @param percent The distance to scroll as a percentage of this object's visible size.
     * @return Whether the object can still scroll in the given direction.
     */
    public boolean scroll(final Direction direction, final float percent) {
        return scroll(direction, percent, (int)(DEFAULT_SCROLL_SPEED * mDisplayMetrics.density));
    }

    /**
     * Performs a scroll gesture on this object.
     *
     * @param direction The direction in which to scroll.
     * @param percent The distance to scroll as a percentage of this object's visible size.
     * @param speed The speed at which to perform this gesture in pixels per second.
     * @return Whether the object can still scroll in the given direction.
     */
    public boolean scroll(Direction direction, float percent, final int speed) {
        if (percent < 0.0f) {
            throw new IllegalArgumentException("Percent must be greater than 0.0f");
        }
        if (speed < 0) {
            throw new IllegalArgumentException("Speed cannot be negative");
        }

        // To scroll, we swipe in the opposite direction
        final Direction swipeDirection = Direction.reverse(direction);

        // Scroll by performing repeated swipes
        Rect bounds = getVisibleBoundsForGestures();
        for (; percent > 0.0f; percent -= 1.0f) {
            float segment = percent > 1.0f ? 1.0f : percent;
            PointerGesture swipe =
                    mGestures.swipeRect(bounds, swipeDirection, segment, speed).pause(250);

            // Perform the gesture and return early if we reached the end
            if (mGestureController.performGestureAndWait(
                    Until.scrollFinished(direction), SCROLL_TIMEOUT, swipe)) {
                return false;
            }
        }
        // We never reached the end
        return true;
    }

    /**
     * Performs a fling gesture on this object.
     *
     * @param direction The direction in which to fling.
     * @return Whether the object can still scroll in the given direction.
     */
    public boolean fling(final Direction direction) {
        return fling(direction, (int)(DEFAULT_FLING_SPEED * mDisplayMetrics.density));
    }

    /**
     * Performs a fling gesture on this object.
     *
     * @param direction The direction in which to fling.
     * @param speed The speed at which to perform this gesture in pixels per second.
     * @return Whether the object can still scroll in the given direction.
     */
    public boolean fling(final Direction direction, final int speed) {
        ViewConfiguration vc = ViewConfiguration.get(mDevice.getAutomatorBridge().getContext());
        if (speed < vc.getScaledMinimumFlingVelocity()) {
            throw new IllegalArgumentException("Speed is less than the minimum fling velocity");
        }

        // To fling, we swipe in the opposite direction
        final Direction swipeDirection = Direction.reverse(direction);

        Rect bounds = getVisibleBoundsForGestures();
        PointerGesture swipe = mGestures.swipeRect(bounds, swipeDirection, 1.0f, speed);

        // Perform the gesture and return true if we did not reach the end
        return !mGestureController.performGestureAndWait(
                Until.scrollFinished(direction), FLING_TIMEOUT, swipe);
    }

    /**
     * Set the text content by sending individual key codes.
     * @hide
     */
    public void legacySetText(String text) {
        AccessibilityNodeInfo node = getAccessibilityNodeInfo();

        // Per framework convention, setText(null) means clearing it
        if (text == null) {
            text = "";
        }

        CharSequence currentText = node.getText();
        if (!text.equals(currentText)) {
            InteractionController ic = mDevice.getAutomatorBridge().getInteractionController();

            // Long click left + center
            Rect rect = getVisibleBounds();
            ic.longTapNoSync(rect.left + 20, rect.centerY());

            // Select existing text
            mDevice.wait(Until.findObject(By.descContains("Select all")), 50).click();
            // Wait for the selection
            SystemClock.sleep(250);
            // Delete it
            ic.sendKey(KeyEvent.KEYCODE_DEL, 0);

            // Send new text
            ic.sendText(text);
        }
    }

    /** Sets the text content if this object is an editable field. */
    public void setText(String text) {
        AccessibilityNodeInfo node = getAccessibilityNodeInfo();

        // Per framework convention, setText(null) means clearing it
        if (text == null) {
            text = "";
        }

        if (UiDevice.API_LEVEL_ACTUAL > Build.VERSION_CODES.KITKAT) {
            // do this for API Level above 19 (exclusive)
            Bundle args = new Bundle();
            args.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, text);
            if (!node.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, args)) {
                // TODO: Decide if we should throw here
                Log.w(TAG, "AccessibilityNodeInfo#performAction(ACTION_SET_TEXT) failed");
            }
        } else {
            CharSequence currentText = node.getText();
            if (!text.equals(currentText)) {
                // Give focus to the object. Expect this to fail if the object already has focus.
                if (!node.performAction(AccessibilityNodeInfo.ACTION_FOCUS) && !node.isFocused()) {
                    // TODO: Decide if we should throw here
                    Log.w(TAG, "AccessibilityNodeInfo#performAction(ACTION_FOCUS) failed");
                }
                // Select the existing text. Expect this to fail if there is no existing text.
                Bundle args = new Bundle();
                args.putInt(AccessibilityNodeInfo.ACTION_ARGUMENT_SELECTION_START_INT, 0);
                args.putInt(AccessibilityNodeInfo.ACTION_ARGUMENT_SELECTION_END_INT, text.length());
                if (!node.performAction(AccessibilityNodeInfo.ACTION_SET_SELECTION, args) &&
                        currentText.length() > 0) {
                    // TODO: Decide if we should throw here
                    Log.w(TAG, "AccessibilityNodeInfo#performAction(ACTION_SET_SELECTION) failed");
                }
                // Send the delete key to clear the existing text, then send the new text
                InteractionController ic = mDevice.getAutomatorBridge().getInteractionController();
                ic.sendKey(KeyEvent.KEYCODE_DEL, 0);
                ic.sendText(text);
            }
        }
    }


    /**
     * Returns an up-to-date {@link AccessibilityNodeInfo} corresponding to the {@link android.view.View} that
     * this object represents.
     */
    private AccessibilityNodeInfo getAccessibilityNodeInfo() {
        if (mCachedNode == null) {
            throw new IllegalStateException("This object has already been recycled");
        }

        mDevice.waitForIdle();
        if (!mCachedNode.refresh()) {
            mDevice.runWatchers();

            if (!mCachedNode.refresh()) {
                throw new StaleObjectException();
            }
        }
        return mCachedNode;
    }
}
