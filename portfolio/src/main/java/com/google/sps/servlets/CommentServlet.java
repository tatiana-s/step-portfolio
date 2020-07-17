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
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.gson.Gson;
import com.google.sps.data.Comment;
import com.google.sps.data.CommentEntity;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet for handling comments data */
@WebServlet("/comments")
public class CommentServlet extends HttpServlet {

  private static final String COMMENT_FORM_NAME = "comment-input";
  private static final String USER_FORM_NAME = "username-input";
  private static final String MOOD_FORM_NAME = "select-mood";
  private static final String REDIRECT_URL = "/index.html";
  private static final String COMMENT_NUMBER_QUERY_PARAM = "number";
  private static final String DEFAULT_USERNAME = "Anonymous";

  /** Loads and returns comments from the datastore database. */
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    int commentNumber = 0;
    try {
      Integer.parseInt(request.getParameter(COMMENT_NUMBER_QUERY_PARAM));
    } catch (NumberFormatException e) {
      e.printStackTrace();
    }
    Query query = new Query(CommentEntity.KIND.getLabel());
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery results = datastore.prepare(query);

    FetchOptions limitComments = FetchOptions.Builder.withLimit(commentNumber);
    List<Comment> comments = new ArrayList<>();
    for (Entity entity : results.asIterable(limitComments)) {
      long id = entity.getKey().getId();
      String content = (String) entity.getProperty(CommentEntity.CONTENT_PROPERTY.getLabel());
      String user = (String) entity.getProperty(CommentEntity.CONTENT_PROPERTY.getLabel());
      long time = (long) entity.getProperty(CommentEntity.CONTENT_PROPERTY.getLabel());
      String mood = (String) entity.getProperty(CommentEntity.CONTENT_PROPERTY.getLabel());
      comments.add(new Comment(id, content, user, time, mood));
    }

    response.setContentType("application/json;");
    response.getWriter().println(convertToJson(comments));
  }

  /** Saves comments entered in the comment form in the datastore database. */
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String comment = getParameter(request, COMMENT_FORM_NAME, "");
    String user = getParameter(request, USER_FORM_NAME, DEFAULT_USERNAME);
    String mood = getParameter(request, MOOD_FORM_NAME, "");
    long time = System.currentTimeMillis();

    if (!comment.isEmpty()) {
      Entity commentEntity = new Entity(CommentEntity.KIND.getLabel());
      commentEntity.setProperty(CommentEntity.CONTENT_PROPERTY.getLabel(), comment);
      commentEntity.setProperty(CommentEntity.USER_PROPERTY.getLabel(), user);
      commentEntity.setProperty(CommentEntity.TIME_PROPERTY.getLabel(), time);
      commentEntity.setProperty(CommentEntity.MOOD_PROPERTY.getLabel(), mood);
      DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
      datastore.put(commentEntity);
    }

    response.sendRedirect(REDIRECT_URL);
  }

  /** Converts a list of comments into a JSON string using the Gson library. */
  private static String convertToJson(List<Comment> commentList) {
    Gson gson = new Gson();
    return gson.toJson(commentList);
  }

  /** Checks if the request parameter is specified in the client and returns default if not. */
  private static String getParameter(HttpServletRequest request, String name, String defaultValue) {
    String value = request.getParameter(name);
    if(value == null || value.isEmpty()) {
      return defaultValue;
    }
    return value;
  }
}
