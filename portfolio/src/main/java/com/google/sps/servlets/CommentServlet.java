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
import com.google.appengine.api.datastore.FetchOptions.Builder;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.gson.Gson;
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

  private static final String INPUT_FORM_NAME = "comment-input";
  private static final String REDIRECT_URL = "/index.html";
  private static final String COMMENT_NUMBER_QUERY_PARAM = "number";

  /**
   * Loads and returns comments from the datastore database.
   */
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    int commentNumber = Integer.parseInt(request.getParameter(COMMENT_NUMBER_QUERY_PARAM));
    Query query = new Query(CommentEntity.KIND.getLabel());
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery results = datastore.prepare(query);
    FetchOptions limitComments = FetchOptions.Builder.withLimit(commentNumber);
    List<String> comments = new ArrayList<>();
    for (Entity entity : results.asIterable(limitComments)) {
      Object content = entity.getProperty(CommentEntity.CONTENT_PROPERTY.getLabel());
      if(content instanceof String) {
        comments.add((String) content);
      }
    }
    response.setContentType("application/json;");
    response.getWriter().println(convertToJson(comments));
  }

  /**
   * Saves comments entered in the comment form in the datastore database. 
   */
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String comment = request.getParameter(INPUT_FORM_NAME);
    Entity commentEntity = new Entity(CommentEntity.KIND.getLabel());
    commentEntity.setProperty(CommentEntity.CONTENT_PROPERTY.getLabel(), comment);
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    datastore.put(commentEntity);
    response.sendRedirect(REDIRECT_URL);
  }

  /**
   * Converts a list of strings into a JSON string using the Gson library.
   */
  private static String convertToJson(List<String> commentList) {
    Gson gson = new Gson();
    return gson.toJson(commentList);
  }
}
