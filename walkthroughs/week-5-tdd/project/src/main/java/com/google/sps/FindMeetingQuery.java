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
import java.util.Collections;
import java.util.PriorityQueue;

public final class FindMeetingQuery {
  public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
    Collection<String> attendees = request.getAttendees();
    Collection<String> optionalAttendees = request.getOptionalAttendees();
    PriorityQueue<TimeRange> blockingEvents = new PriorityQueue<>(TimeRange.ORDER_BY_START);
    PriorityQueue<TimeRange> blockingEventsNonOptional =
        new PriorityQueue<>(TimeRange.ORDER_BY_START);
    for (Event event : events) {
      if (!Collections.disjoint(event.getAttendees(), attendees)) {
        blockingEventsNonOptional.add(event.getWhen());
        blockingEvents.add(event.getWhen());
      } else if (!Collections.disjoint(event.getAttendees(), optionalAttendees)) {
          blockingEvents.add(event.getWhen());
      }
    }
    Collection<TimeRange> freeSlots = new ArrayList<>();
    long meetingDuration = request.getDuration();
    if (meetingDuration <= TimeRange.WHOLE_DAY.duration()) {
      if (blockingEvents.isEmpty()) {
        freeSlots.add(TimeRange.WHOLE_DAY);
      } else {
        findFreeSlots(blockingEvents, meetingDuration, freeSlots);
        if (freeSlots.isEmpty()) {
          if (blockingEventsNonOptional.isEmpty()) {
            freeSlots.add(TimeRange.WHOLE_DAY);
          } else {
            findFreeSlots(blockingEventsNonOptional, meetingDuration, freeSlots);
          }
        }
      } 
    }
    return freeSlots;
  }

  private static void findFreeSlots(
      PriorityQueue<TimeRange> blockingEvents,
      long meetingDuration,
      Collection<TimeRange> freeSlots) {
    int previousEventEnd = TimeRange.START_OF_DAY;
    while (!blockingEvents.isEmpty()) {
      TimeRange event = blockingEvents.remove();
      int gapDuration = event.start() - previousEventEnd;
      if (gapDuration >= meetingDuration) {
        freeSlots.add(TimeRange.fromStartEnd(previousEventEnd, event.start(), false));
      }
      if (event.end() > previousEventEnd) {
        previousEventEnd = event.end();
      }
    }
    int gapDuration = TimeRange.END_OF_DAY - previousEventEnd;
    if (gapDuration >= meetingDuration) {
      freeSlots.add(TimeRange.fromStartEnd(previousEventEnd, TimeRange.END_OF_DAY, true));
    }
  }
}
