package com.laimiux.timeline;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.RelativeLayout;

import com.laimiux.timeline.internal.Constants;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.sephiroth.android.library.widget.AbsHListView;
import it.sephiroth.android.library.widget.HListView;

public class TimelineView extends RelativeLayout {
    private TimelineListView timelineListView;
    private TimelineControllerView timelineControllerView;

    private TimelineListAdapter timelineListAdapter;
    private ControllerAdapter<ControllerItem> timelineControllerAdapter;

    // Keeps track of heights. todo explain better
    final private Map<Integer, Integer> heightMap = new HashMap<>();

    private boolean isLastItemBiggerThanView;

    //  = (last item height - screen height)
    private int lastItemHeightExcess;

    // Width of timeline controller item
    final private int itemWidth;

    // Helper to keep track of list item heights
    private ListHeightUtil listHeightUtil;

    private AbsListView.OnScrollListener onScrollListener;

    public TimelineView(Context context, AttributeSet attrs) {
        super(context, attrs);

        // Get item width
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.TimelineView);
        itemWidth = typedArray.getDimensionPixelSize(R.styleable.TimelineView_item_width, -1);
        typedArray.recycle();

        if (itemWidth == -1) {
            throw new IllegalStateException("item_width is required");
        }

    }

    @Override protected void onFinishInflate() {
        super.onFinishInflate();

        // Set children views.
        timelineListView = (TimelineListView) findViewById(R.id.timeline_list);
        timelineControllerView = (TimelineControllerView) findViewById(R.id.timeline_controller);

        // Disable scroll to the bottom when new items are added.
        timelineListView.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_DISABLED);

        // Setup both scroll listeners
        setListScrollListener();
        setControllerScrollListener();
    }

    /**
     * Recalculates all list item heights.
     */
    private void recalculateItemHeights() {
        // Set up list heights
        if (listHeightUtil == null) {
            listHeightUtil = new ListHeightUtil(timelineListView);
        } else {
            listHeightUtil.recalculateHeights();
        }
    }

    private void initTimelineHeights() {
        heightMap.clear();

        int prevPosition = 0;
        for (int i = 0; i < timelineListAdapter.getCount(); i++) {

            int position = getTimelinePosition(i);

            if (prevPosition != position) {
                int calculatedHeight = listHeightUtil.getHeight(prevPosition, position);

                // Compensate for last item height
                if (position == timelineListAdapter.getCount() - 1 && getLastItemHeightExcess() > 0) {
                    calculatedHeight += getLastItemHeightExcess();
                }

                heightMap.put(prevPosition, calculatedHeight);
            }

            prevPosition = position;
        }
    }

    protected RefreshState saveState() {
        // Disable scroll to the bottom when new items are added.
        timelineListView.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_DISABLED);

        View topChild = timelineListView.getChildAt(0);

        if (topChild != null) {
            int top = topChild.getTop();
            int position = timelineListView.getPositionForView(topChild);
            int itemType = timelineListAdapter.getItemViewType(position);
            return new RefreshState(position, top, itemType);
        } else {
            return null;
        }
    }


    public void updateListItemHeight(int itemPosition, int newHeight) {
        listHeightUtil.updateHeight(itemPosition, newHeight);
        setListFillerFooterHeight();
        initTimelineHeights();

        adjustControllerPosition(timelineListView.getFirstVisiblePosition());
    }

    public void setListAdapter(TimelineListAdapter adapter) {
        timelineListAdapter = adapter;
        timelineListView.setAdapter(adapter);
    }

    public void setControllerAdapter(ControllerAdapter<ControllerItem> adapter) {
        timelineControllerAdapter = adapter;
        timelineControllerView.setAdapter(adapter);
    }

    /**
     * Moves the timeline to the last item
     */
    public void showLast() {
        timelineListView.setSelection(timelineListView.getCount());
        timelineControllerView.setSelection(timelineControllerView.getCount());
    }

    protected void adjustControllerPosition(int firstVisibleItem) {
        View childView = timelineListView.getChildAt(0);

        if (childView != null) {

            // Find closest top position that first visible item
            // is bigger than.
            Map.Entry<Integer, Integer> currentTopPos = null;
            for (Map.Entry<Integer, Integer> topPosEntry : heightMap.entrySet()) {
                Integer topPos = topPosEntry.getKey();
                if (firstVisibleItem >= topPos && (currentTopPos == null || topPos > currentTopPos.getKey())) {
                    currentTopPos = topPosEntry;
                }
            }

            // Find controller item position.
            int controllerPos = -1;

            // Why current top position can be null?
            if (currentTopPos != null) {
                for (int i = 0; i < timelineControllerAdapter.getCount() - 1; i++) {
                    ControllerItem controllerItem = timelineControllerAdapter.getItem(i);

                    if (controllerItem != null) {
                        if (controllerItem.getPosition() == currentTopPos.getKey()) {
                            controllerPos = i;
                            break;
                        }
                    }
                }
            }

            // Continue if controller position is found
            if (controllerPos >= 0) {
                // Total height
                int height = currentTopPos.getValue();
                int heightToVisibleItem = listHeightUtil.getHeight(currentTopPos.getKey(), firstVisibleItem);

                int firstVisibleItemOffset = childView.getTop();
                int totalHeightMoved = heightToVisibleItem + (-firstVisibleItemOffset);

                if (totalHeightMoved <= height) {
                    float ratio = totalHeightMoved / (float) height;
                    int offset = (int) (ratio * itemWidth);
                    final int x = -(controllerPos * itemWidth) - offset;

                    timelineControllerView.setSelectionFromLeft(0, x);
                } else {
                    if (Constants.DEBUG) {
                        Log.w(TimelineView.class.getSimpleName(), "Height moved is bigger than height, something is wrong");
                    }
                }
            }
        }
    }

    public void updateItems(List<Object> listItems, final List<? extends ControllerItem> controllerItems) {
        // Save old state
        RefreshState state = saveState();

        // We want to hold the position of header view
        if (state != null && state.itemType == TimelineListAdapter.ITEM_TYPE) {
            state = new RefreshState(state.position - 1, state.top, state.itemType);
        }

        final Object header;
        if (state == null) {
            header = null;
        } else {
            header = timelineListAdapter.getItem(state.position);
        }

        timelineListAdapter.setItems(listItems);

        final int newPosition;
        if (header == null) {
            newPosition = -1;
        } else {
            int itemPosition = listItems.indexOf(header);
            if (itemPosition != -1) {
                if (state.itemType == TimelineListAdapter.ITEM_TYPE) {
                    newPosition = 1 + itemPosition; //
                } else {
                    newPosition = itemPosition;
                }
            } else {
                newPosition = -1;
            }
        }

        final int top = state != null ? state.top : -1;

        timelineListView.post(new Runnable() {
            @Override
            public void run() {
                if (newPosition != -1) {
                    timelineListView.setSelectionFromTop(newPosition, top);
                } else {
                    timelineListView.setSelection(timelineListView.getCount());
                }

                // Make sure controller adapter has initialized
                timelineControllerAdapter.setItems(controllerItems);

                recalculateItemHeights();
                initTimelineHeights();
                setListFillerFooterHeight();


                if (newPosition == -1) {
                    adjustControllerPosition(timelineListView.getFirstVisiblePosition());
                } else {
                    adjustControllerPosition(newPosition);
                }
            }
        });
    }


    /**
     * Call only when list view is set
     */
    protected void setListFillerFooterHeight() {
        int adapterSize = timelineListAdapter.getCount();

        final int lastItemHeight;
        if (adapterSize < 2) {
            lastItemHeight = 0;
        } else {
            // Height of item + header
            lastItemHeight = listHeightUtil.getHeight(adapterSize - 2, adapterSize);
        }

        final int listViewHeight = timelineListView.getHeight();
        int footerHeight = listViewHeight - lastItemHeight;

        isLastItemBiggerThanView = footerHeight < 0;
        if (isLastItemBiggerThanView) {
            lastItemHeightExcess = listHeightUtil.getHeight(adapterSize - 1, adapterSize) - listViewHeight;
            footerHeight = 0;
        } else {
            lastItemHeightExcess = 0;
        }

        timelineListView.setFooterHeight(footerHeight);
    }

    /*
 * Sets timeline scroll listener that synchronizes with horizontal controller.
 */
    protected void setControllerScrollListener() {
        AbsHListView.OnScrollListener controllerScrollListener = new HListView.OnScrollListener() {
            private int mState;

            @Override
            public void onScrollStateChanged(AbsHListView view, int scrollState) {
                mState = scrollState;

                if (onScrollListener != null) {
                    onScrollListener.onScrollStateChanged(timelineListView, scrollState);
                }
            }

            @Override
            public void onScroll(AbsHListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                if (mState != SCROLL_STATE_IDLE) {
                    int centerX = view.getWidth() / 2;
                    int pointToPos = view.pointToPosition(centerX, (int) view.getY());

                    HListView listView = (HListView) view;

                    View childView = LayoutUtil.getViewAtPosition(listView, pointToPos);
                    if (childView != null) {
                        int adjustedX = (int) (childView.getX() + childView.getWidth() / 2);

                        // TODO temp
                        int controllerItemPos = pointToPos - listView.getHeaderViewsCount();

                        if (centerX == adjustedX) {
                            int position = getTimelinePosition(controllerItemPos);

                            // if last item
                            if (position == timelineListAdapter.getCount() - 1 && getLastItemHeightExcess() > 0) {
                                timelineListView.setSelectionFromTop(position, -getLastItemHeightExcess());
                            } else {
                                timelineListView.setSelection(position);
                            }
                        } else {

                            final int positionInTimeline;
                            final int offset;
                            if (adjustedX > centerX) {
                                // This is between the pointToPos and one on the right

                                positionInTimeline = getTimelinePosition(controllerItemPos - 1);

                                if (heightMap.containsKey(positionInTimeline)) {
                                    int height = heightMap.get(positionInTimeline);
                                    int positionFromPrevDate = childView.getWidth() + (centerX - adjustedX);
                                    offset = height * positionFromPrevDate / childView.getWidth();
                                } else {
                                    offset = 0;
                                }
                            } else {
                                positionInTimeline = getTimelinePosition(controllerItemPos);
                                // This is between the pointToPos and one to the right
                                if (heightMap.containsKey(positionInTimeline)) {
                                    int height = heightMap.get(positionInTimeline);
                                    int positionFromPrevDate = centerX - adjustedX;
                                    offset = height * positionFromPrevDate / childView.getWidth();
                                } else {
                                    offset = 0;
                                }
                            }

                            // Adjust the timeline list.
                            timelineListView.setSelectionFromTop(positionInTimeline, -offset);
                        }
                    }
                }


                if (onScrollListener != null) {
                    final int firstVisiblePosition = timelineListView.getFirstVisiblePosition();
                    int visibleItems = timelineListView.getLastVisiblePosition() - firstVisiblePosition;
                    int totalCount = timelineListView.getCount();

                    onScrollListener.onScroll(timelineListView, firstVisiblePosition, visibleItems, totalCount);
                }
            }
        };


        timelineControllerView.setOnScrollListener(controllerScrollListener);
    }

    private void setListScrollListener() {
        // Timeline list scroll listener
        AbsListView.OnScrollListener listScrollListener = new AbsListView.OnScrollListener() {
            private int mState;

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                mState = scrollState;

                if (onScrollListener != null) {
                    onScrollListener.onScrollStateChanged(view, scrollState);
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (mState != SCROLL_STATE_IDLE) {
                    adjustControllerPosition(firstVisibleItem);
                }

                if (onScrollListener != null) {
                    onScrollListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
                }
            }
        };

        timelineListView.setOnScrollListener(listScrollListener);
    }

    /**
     * Gets the position that the controller item points to in the list.
     *
     * @param controllerItemPos Position of controller item
     * @return Position of controller item representation in the list view.
     */
    protected int getTimelinePosition(int controllerItemPos) {
        ControllerItem controllerItem = timelineControllerAdapter.getItem(controllerItemPos);

        if (controllerItem == null) {
            return isLastItemBiggerThanView() ?
                    timelineListAdapter.getCount() - 1 : // Go to last item
                    timelineListAdapter.getCount() - 2; // Go to last header item
        } else {
            return controllerItem.getPosition();
        }
    }

    public void setOnScrollListener(AbsListView.OnScrollListener onScrollListener) {
        this.onScrollListener = onScrollListener;
    }

    public boolean isLastItemBiggerThanView() {
        return isLastItemBiggerThanView;
    }

    public int getLastItemHeightExcess() {
        return lastItemHeightExcess;
    }

    public boolean hasItems() {
        return timelineListAdapter.getCount() > 0;
    }


    /**
     * When refreshing, helps to keep track of list position
     */
    public static class RefreshState {
        final public int position;
        final public int top;
        final public int itemType;

        public RefreshState(int position, int top, int itemType) {
            this.position = position;
            this.top = top;
            this.itemType = itemType;
        }
    }
}
