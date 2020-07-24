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
import com.google.gson.GsonBuilder;
import com.google.sps.data.CommentEntity;
import java.io.IOException;
import java.util.HashMap;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet for accumulating comments data */
@WebServlet("/analyse-comments")
public class AnalyseCommentServlet extends HttpServlet {

  /** Loads comments from the datastore database and accumulates some data about them. */
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Query query = new Query(CommentEntity.KIND.getLabel());
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery results = datastore.prepare(query);

    HashMap<String, Integer> data = new HashMap<>();
    int numberOfComments = 0;
    int numberOfAnonymousComments = 0;
    for (Entity entity : results.asIterable()) {
      numberOfComments++;
      String username = (String) entity.getProperty(CommentEntity.USERNAME_PROPERTY.getLabel());
      if (username.equals("Anonymous")) {
        numberOfAnonymousComments++;
      }
      String mood = (String) entity.getProperty(CommentEntity.MOOD_PROPERTY.getLabel());
      data.put(mood, data.getOrDefault(mood, 0) + 1);
    }
    data.put("comments", numberOfComments);
    data.put("anonymous", numberOfAnonymousComments);

    response.setContentType("application/json;");
    response.setCharacterEncoding("UTF-8");
    response.getWriter().println(convertToJson(data));
  }

  /** Converts a list of comments into a JSON string using the Gson library. */
  private static String convertToJson(HashMap<String, Integer> data) {
    Gson gson = new GsonBuilder().disableHtmlEscaping().create();
    return gson.toJson(data);
  }
}
