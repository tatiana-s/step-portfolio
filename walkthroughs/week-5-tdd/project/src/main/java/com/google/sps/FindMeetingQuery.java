// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps;

import java.util.ArrayList;
import java.util.Collection;
import java.util.PriorityQueue;

public final class FindMeetingQuery {
  public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
    Collection<String> attendees = request.getAttendees();
    int initialQueueCapacity = 1;
    if (events.size() > 0) {
      initialQueueCapacity = events.size();
    }
    PriorityQueue<TimeRange> blockingIntervals =
        new PriorityQueue<>(initialQueueCapacity, TimeRange.ORDER_BY_START);
    for (Event event : events) {
      if (eventContainsAttendee(event, attendees)) {
        blockingIntervals.add(event.getWhen());
      }
    }
    ArrayList<TimeRange> freeSlots = new ArrayList<>();
    long duration = request.getDuration();
    if (duration < TimeRange.WHOLE_DAY.duration()) {
      if (!blockingIntervals.isEmpty()) {
        int previousEnd = TimeRange.START_OF_DAY;
        while (!blockingIntervals.isEmpty()) {
          TimeRange timeRange = blockingIntervals.remove();
          int intervalDifference = previousEnd - timeRange.start();
          if (intervalDifference < 0 && Math.abs(intervalDifference) >= duration) {
            freeSlots.add(TimeRange.fromStartEnd(previousEnd, timeRange.start(), false));
          }
          if (timeRange.end() > previousEnd) {
            previousEnd = timeRange.end();
          }
        }
        int intervalDifference = previousEnd - TimeRange.END_OF_DAY;
        if (intervalDifference < 0 && Math.abs(intervalDifference) >= duration) {
          freeSlots.add(TimeRange.fromStartEnd(previousEnd, TimeRange.END_OF_DAY, true));
        }
      } else {
        freeSlots.add(TimeRange.WHOLE_DAY);
      }
    }
    return freeSlots;
  }

  private static boolean eventContainsAttendee(Event event, Collection<String> requestAttendees) {
    Collection<String> eventAttendees = event.getAttendees();
    for (String attendee : requestAttendees) {
      if (eventAttendees.contains(attendee)) {
        return true;
      }
    }
    return false;
  }
}
