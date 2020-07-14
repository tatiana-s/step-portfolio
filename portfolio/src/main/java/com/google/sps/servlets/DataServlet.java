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
import java.io.IOException;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet for handling comments data */
@WebServlet("/comments")
public class DataServlet extends HttpServlet {

  /* Datastore data is represented by entities which have a kind and certain properties,
   * and the constants below define the kind and property names for comment entities.
   * Changing them would mean comments saved with a previous definition would not be displayed anymore. */
  private static final String COMMENT_KIND = "Comment";
  private static final String CONTENT_PROPERTY = "content";

  /* This is the name of the input form which is defined in the html of the main page */
  private static final String INPUT_FORM = "comment-input";

  /* This specifies what URL the client is redirected to after a POST request. */
  private static final String REDIRECT_URL = "/index.html";

  /* This is the query string parameter for the number of comments to be displayed as defined in the script. */
  private static final String COMMENT_NUMBER_PARAM = "number";

  /**
   * Loads and returns comments from the datastore database.
   */
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    int commentNumber = Integer.parseInt(request.getParameter(COMMENT_NUMBER_PARAM));
    Query query = new Query(COMMENT_KIND);
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery results = datastore.prepare(query);
    List<String> comments = new ArrayList<>();
    int count = 0;
    for (Entity entity : results.asIterable()) {
      Object content = entity.getProperty(CONTENT_PROPERTY);
      if(content instanceof String) {
        comments.add((String) content);
      }
      count++;
      if (count == commentNumber) {
        break;
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
    String comment = request.getParameter(INPUT_FORM);
    Entity commentEntity = new Entity(COMMENT_KIND);
    commentEntity.setProperty(CONTENT_PROPERTY, comment);
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
