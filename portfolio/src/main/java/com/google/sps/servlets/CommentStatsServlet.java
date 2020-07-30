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

package com.google.sps.servlets;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.gson.Gson;
import com.google.sps.data.CommentEntity;
import java.io.IOException;
import java.util.HashMap;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet for accumulating comments data */
@WebServlet("/comment-stats")
public class CommentStatsServlet extends HttpServlet {

  private static final String COMMENT_COUNT_DATA_LABEL = "commentCount";
  private static final String MOOD_COUNT_DATA_LABEL = "moodCount";
  private static final String DEFAULT_USER = "Anonymous";

  /** Loads comments from the datastore database and accumulates some data about them. */
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    HashMap<String, Integer> commentCount = new HashMap<>();
    HashMap<String, Integer> moodCount = new HashMap<>();

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    Query query = new Query(CommentEntity.KIND.getLabel());
    PreparedQuery results = datastore.prepare(query);

    int numberOfComments = 0;
    int numberOfAnonymousComments = 0;
    for (Entity entity : results.asIterable()) {
      numberOfComments++;
      String username = (String) entity.getProperty(CommentEntity.USERNAME_PROPERTY.getLabel());
      if (username.equals(DEFAULT_USER)) {
        numberOfAnonymousComments++;
      }
      String mood = (String) entity.getProperty(CommentEntity.MOOD_PROPERTY.getLabel());
      moodCount.put(mood, moodCount.getOrDefault(mood, 0) + 1);
    }
    commentCount.put("total", numberOfComments);
    commentCount.put("anonymousTotal", numberOfAnonymousComments);

    HashMap<String, HashMap<String, Integer>> data = new HashMap<>();
    data.put(COMMENT_COUNT_DATA_LABEL, commentCount);
    data.put(MOOD_COUNT_DATA_LABEL, moodCount);
    response.setContentType("application/json;");
    response.setCharacterEncoding("UTF-8");
    Gson gson = new Gson();
    response.getWriter().println(gson.toJson(data));
  }
}
