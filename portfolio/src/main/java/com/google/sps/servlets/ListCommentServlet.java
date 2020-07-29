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
import com.google.appengine.api.datastore.Query.SortDirection;
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
public class ListCommentServlet extends HttpServlet {

  private static final String COMMENT_NUMBER_QUERY_PARAM = "limit";
  private static final String SORT_ORDER_QUERY_PARAM = "sort";

  /** Loads and returns comments from the datastore database. */
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Query query = new Query(CommentEntity.KIND.getLabel());
    String sortOrder = request.getParameter(SORT_ORDER_QUERY_PARAM);
    switch (sortOrder) {
      case "old":
        query.addSort(CommentEntity.TIME_PROPERTY.getLabel(), SortDirection.ASCENDING);
        break;
      case "new":
      default:
        query.addSort(CommentEntity.TIME_PROPERTY.getLabel(), SortDirection.DESCENDING);
    }

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery results = datastore.prepare(query);

    int commentNumber = Integer.parseInt(request.getParameter(COMMENT_NUMBER_QUERY_PARAM));
    FetchOptions limitComments = FetchOptions.Builder.withLimit(commentNumber);
    List<Comment> comments = new ArrayList<>();
    for (Entity entity : results.asIterable(limitComments)) {
      long id = entity.getKey().getId();
      String content = (String) entity.getProperty(CommentEntity.CONTENT_PROPERTY.getLabel());
      String email = (String) entity.getProperty(CommentEntity.EMAIL_PROPERTY.getLabel());
      String username = (String) entity.getProperty(CommentEntity.USERNAME_PROPERTY.getLabel());
      long time = (long) entity.getProperty(CommentEntity.TIME_PROPERTY.getLabel());
      String mood = (String) entity.getProperty(CommentEntity.MOOD_PROPERTY.getLabel());
      comments.add(new Comment(id, content, email, username, time, mood));
    }

    response.setContentType("application/json;");
    response.setCharacterEncoding("UTF-8");
    Gson gson = new Gson();
    response.getWriter().println(gson.toJson(comments));
  }
}
