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

/** Servlet for deleting comments */
@WebServlet("/delete-comments")
public class DeleteCommentServlet extends HttpServlet {

  /* Datastore data is represented by entities which have a kind and certain properties,
   * and the constants below define the kind and property names for comment entities.
   * Changing them would mean comments saved with a previous definition would not be displayed anymore. */
  private static final String COMMENT_KIND = "Comment";
  private static final String CONTENT_PROPERTY = "content";

  /* This specifies what URL the client is redirected to after a POST request. */
  private static final String REDIRECT_URL = "/index.html";

  /**
   * Deletes all comments in the datastore database. 
   */
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Query query = new Query(COMMENT_KIND);
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery results = datastore.prepare(query);
    List<String> comments = new ArrayList<>();
    for (Entity entity : results.asIterable()) {
      datastore.delete(entity.getKey());
    }
    response.sendRedirect(REDIRECT_URL);
  }
}