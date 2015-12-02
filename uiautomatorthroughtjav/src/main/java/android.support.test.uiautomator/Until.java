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

import android.view.accessibility.AccessibilityEvent;

import java.util.List;
import java.util.regex.Pattern;

/**
 * The {@link Until} class provides factory methods for constructing common conditions.
 */
public class Until {

    // Search conditions

    /**
     * Returns a {@link SearchCondition} that is satisfied when no elements matching the selector
     * can be found.
     */
    public static SearchCondition<Boolean> gone(final BySelector selector) {
        return new SearchCondition<Boolean>() {
            @Override
            Boolean apply(Searchable container) {
                return !container.hasObject(selector);
            }
        };
    }

    /**
     * Returns a {@link SearchCondition} that is satisfied when at least one element matching the
     * selector can be found.
     */
    public static SearchCondition<Boolean> hasObject(final BySelector selector) {
        return new SearchCondition<Boolean>() {
            @Override
            Boolean apply(Searchable container) {
                return container.hasObject(selector);
            }
        };
    }

    /**
     * Returns a {@link SearchCondition} that is satisfied when at least one element matching the
     * selector can be found. The condition will return the first matching element.
     */
    public static SearchCondition<UiObject2> findObject(final BySelector selector) {
        return new SearchCondition<UiObject2>() {
            @Override
            UiObject2 apply(Searchable container) {
                return container.findObject(selector);
            }
        };
    }

    /**
     * Returns a {@link SearchCondition} that is satisfied when at least one element matching the
     * selector can be found. The condition will return all matching elements.
     */
    public static SearchCondition<List<UiObject2>> findObjects(final BySelector selector) {
        return new SearchCondition<List<UiObject2>>() {
            @Override
            List<UiObject2> apply(Searchable container) {
                List<UiObject2> ret = container.findObjects(selector);
                return ret.isEmpty() ? null : ret;
            }
        };
    }


    // UiObject2 conditions

    /**
     * Returns a condition that depends on a {@link UiObject2}'s checkable state.
     *
     * @param isCheckable Whether the object should be checkable to satisfy this condition.
     */
    public static UiObject2Condition<Boolean> checkable(final boolean isCheckable) {
        return new UiObject2Condition<Boolean>() {
            @Override
            Boolean apply(UiObject2 object) {
                return object.isCheckable() == isCheckable;
            }
        };
    }

    /**
     * Returns a condition that depends on a {@link UiObject2}'s checked state.
     *
     * @param isChecked Whether the object should be checked to satisfy this condition.
     */
    public static UiObject2Condition<Boolean> checked(final boolean isChecked) {
        return new UiObject2Condition<Boolean>() {
            @Override
            Boolean apply(UiObject2 object) {
                return object.isChecked() == isChecked;
            }
        };
    }

    /**
     * Returns a condition that depends on a {@link UiObject2}'s clickable state.
     *
     * @param isClickable Whether the object should be clickable to satisfy this condition.
     */
    public static UiObject2Condition<Boolean> clickable(final boolean isClickable) {
        return new UiObject2Condition<Boolean>() {
            @Override
            Boolean apply(UiObject2 object) {
                return object.isClickable() == isClickable;
            }
        };
    }

    /**
     * Returns a condition that depends on a {@link UiObject2}'s enabled state.
     *
     * @param isEnabled Whether the object should be enabled to satisfy this condition.
     */
    public static UiObject2Condition<Boolean> enabled(final boolean isEnabled) {
        return new UiObject2Condition<Boolean>() {
            @Override
            Boolean apply(UiObject2 object) {
                return object.isEnabled() == isEnabled;
            }
        };
    }

    /**
     * Returns a condition that depends on a {@link UiObject2}'s focusable state.
     *
     * @param isFocusable Whether the object should be focusable to satisfy this condition.
     */
    public static UiObject2Condition<Boolean> focusable(final boolean isFocusable) {
        return new UiObject2Condition<Boolean>() {
            @Override
            Boolean apply(UiObject2 object) {
                return object.isFocusable() == isFocusable;
            }
        };
    }

    /**
     * Returns a condition that depends on a {@link UiObject2}'s focused state.
     *
     * @param isFocused Whether the object should be focused to satisfy this condition.
     */
    public static UiObject2Condition<Boolean> focused(final boolean isFocused) {
        return new UiObject2Condition<Boolean>() {
            @Override
            Boolean apply(UiObject2 object) {
                return object.isFocused() == isFocused;
            }
        };
    }

    /**
     * Returns a condition that depends on a {@link UiObject2}'s long clickable state.
     *
     * @param isLongClickable Whether the object should be long clickable to satisfy this condition.
     */
    public static UiObject2Condition<Boolean> longClickable(final boolean isLongClickable) {
        return new UiObject2Condition<Boolean>() {
            @Override
            Boolean apply(UiObject2 object) {
                return object.isLongClickable() == isLongClickable;
            }
        };
    }

    /**
     * Returns a condition that depends on a {@link UiObject2}'s scrollable state.
     *
     * @param isScrollable Whether the object should be scrollable to satisfy this condition.
     */
    public static UiObject2Condition<Boolean> scrollable(final boolean isScrollable) {
        return new UiObject2Condition<Boolean>() {
            @Override
            Boolean apply(UiObject2 object) {
                return object.isScrollable() == isScrollable;
            }
        };
    }

    /**
     * Returns a condition that depends on a {@link UiObject2}'s selected state.
     *
     * @param isSelected Whether the object should be selected to satisfy this condition.
     */
    public static UiObject2Condition<Boolean> selected(final boolean isSelected) {
        return new UiObject2Condition<Boolean>() {
            @Override
            Boolean apply(UiObject2 object) {
                return object.isSelected() == isSelected;
            }
        };
    }

    /**
     * Returns a condition that is satisfied when the object's content description matches the given
     * regex.
     */
    public static UiObject2Condition<Boolean> descMatches(final Pattern regex) {
        return new UiObject2Condition<Boolean>() {
            @Override
            Boolean apply(UiObject2 object) {
                String desc = object.getContentDescription();
                return regex.matcher(desc != null ? desc : "").matches();
            }
        };
    }

    /**
     * Returns a condition that is satisfied when the object's content description matches the given
     * regex.
     */
    public static UiObject2Condition<Boolean> descMatches(String regex) {
        return descMatches(Pattern.compile(regex));
    }

    /**
     * Returns a condition that is satisfied when the object's content description exactly matches
     * the given string.
     */
    public static UiObject2Condition<Boolean> descEquals(String contentDescription) {
        return descMatches(Pattern.compile(Pattern.quote(contentDescription)));
    }

    /**
     * Returns a condition that is satisfied when the object's content description contains the
     * given string.
     */
    public static UiObject2Condition<Boolean> descContains(String substring) {
        return descMatches(Pattern.compile(String.format("^.*%s.*$", Pattern.quote(substring))));
    }

    /**
     * Returns a condition that is satisfied when the object's content description starts with the
     * given string.
     */
    public static UiObject2Condition<Boolean> descStartsWith(String substring) {
        return descMatches(Pattern.compile(String.format("^%s.*$", Pattern.quote(substring))));
    }

    /**
     * Returns a condition that is satisfied when the object's content description ends with the
     * given string.
     */
    public static UiObject2Condition<Boolean> descEndsWith(String substring) {
        return descMatches(Pattern.compile(String.format("^.*%s$", Pattern.quote(substring))));
    }

    /**
     * Returns a condition that is satisfied when the object's text value matches the given regex.
     */
    public static UiObject2Condition<Boolean> textMatches(final Pattern regex) {
        return new UiObject2Condition<Boolean>() {
            @Override
            Boolean apply(UiObject2 object) {
                String text = object.getText();
                return regex.matcher(text != null ? text : "").matches();
            }
        };
    }

    /**
     * Returns a condition that is satisfied when the object's text value matches the given regex.
     */
    public static UiObject2Condition<Boolean> textMatches(String regex) {
        return textMatches(Pattern.compile(regex));
    }

    public static UiObject2Condition<Boolean> textNotEquals(final String text) {
        return new UiObject2Condition<Boolean>() {
            @Override
            Boolean apply(UiObject2 object) {
                return !text.equals(object.getText());
            }
        };
    }

    /**
     * Returns a condition that is satisfied when the object's text value exactly matches the given
     * string.
     */
    public static UiObject2Condition<Boolean> textEquals(String text) {
        return textMatches(Pattern.compile(Pattern.quote(text)));
    }

    /**
     * Returns a condition that is satisfied when the object's text value contains the given string.
     */
    public static UiObject2Condition<Boolean> textContains(String substring) {
        return textMatches(Pattern.compile(String.format("^.*%s.*$", Pattern.quote(substring))));
    }

    /**
     * Returns a condition that is satisfied when the object's text value starts with the given
     * string.
     */
    public static UiObject2Condition<Boolean> textStartsWith(String substring) {
        return textMatches(Pattern.compile(String.format("^%s.*$", Pattern.quote(substring))));
    }

    /**
     * Returns a condition that is satisfied when the object's text value ends with the given
     * string.
     */
    public static UiObject2Condition<Boolean> textEndsWith(String substring) {
        return textMatches(Pattern.compile(String.format("^.*%s$", Pattern.quote(substring))));
    }


    // Event conditions

    /** Returns a condition that depends on a new window having appeared. */
    public static EventCondition<Boolean> newWindow() {
        return new EventCondition<Boolean>() {
            private int mMask = AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED |
                    AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED;

            @Override
            Boolean apply(AccessibilityEvent event) {
                mMask &= ~event.getEventType();
                return mMask == 0;
            }

            @Override
            Boolean getResult() {
                return mMask == 0;
            }
        };
    }

    /**
     * Returns a condition that depends on a scroll having reached the end in the given
     * {@code direction}.
     *
     * @param direction The direction of the scroll.
     */
    public static EventCondition<Boolean> scrollFinished(final Direction direction) {
        return new EventCondition<Boolean>() {
            private Direction mDirection = direction;
            private Boolean mResult = null;

            @Override
            Boolean apply(AccessibilityEvent event) {
                if (event.getFromIndex() != -1 && event.getToIndex() != -1 &&
                        event.getItemCount() != -1) {

                    switch (mDirection) {
                        case UP:
                            mResult = (event.getFromIndex() == 0);
                            break;
                        case DOWN:
                            mResult = (event.getToIndex() == event.getItemCount() - 1);
                            break;
                        case LEFT:
                            mResult = (event.getFromIndex() == 0);
                            break;
                        case RIGHT:
                            mResult = (event.getToIndex() == event.getItemCount() - 1);
                            break;
                        default:
                            throw new IllegalArgumentException("Invalid Direction");
                    }
                } else if (event.getScrollX() != -1 && event.getScrollY() != -1) {
                    switch (mDirection) {
                        case UP:
                            mResult = (event.getScrollY() == 0);
                            break;
                        case DOWN:
                            mResult = (event.getScrollY() == event.getMaxScrollY());
                            break;
                        case LEFT:
                            mResult = (event.getScrollX() == 0);
                            break;
                        case RIGHT:
                            mResult = (event.getScrollX() == event.getMaxScrollX());
                            break;
                        default:
                            throw new IllegalArgumentException("Invalid Direction");
                    }
                }

                // Keep listening for events until the result is set to true (we reached the end)
                return Boolean.TRUE.equals(mResult);
            }

            @Override
            Boolean getResult() {
                // If we didn't recieve any scroll events (mResult == null), assume we're already at
                // the end and return true.
                return mResult == null || mResult;
            }
        };
    }
}
