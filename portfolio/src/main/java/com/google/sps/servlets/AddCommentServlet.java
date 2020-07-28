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
import com.google.sps.data.CommentEntity;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet for handling comments data */
@WebServlet("/add-comment")
public class AddCommentServlet extends HttpServlet {

  private static final String COMMENT_FORM_NAME = "comment-input";
  private static final String EMAIL_FORM_NAME = "email-input";
  private static final String USER_FORM_NAME = "username-input";
  private static final String MOOD_FORM_NAME = "select-mood";
  private static final String REDIRECT_URL = "/index.html";
  private static final String DEFAULT_USERNAME = "Anonymous";
  private static final int MAX_COMMENT_LENGHT = 500;

  /** Saves comments entered in the comment form in the datastore database. */
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String comment = getParameter(request, COMMENT_FORM_NAME, "");
    String email = getParameter(request, EMAIL_FORM_NAME, "");
    String user = getParameter(request, USER_FORM_NAME, DEFAULT_USERNAME);
    String mood = getParameter(request, MOOD_FORM_NAME, "");
    long time = System.currentTimeMillis();

    if (!comment.isEmpty() && !(comment.length() > MAX_COMMENT_LENGHT) && !email.isEmpty()) {
      Entity commentEntity = new Entity(CommentEntity.KIND.getLabel());
      commentEntity.setProperty(CommentEntity.CONTENT_PROPERTY.getLabel(), comment);
      commentEntity.setProperty(CommentEntity.EMAIL_PROPERTY.getLabel(), email);
      commentEntity.setProperty(CommentEntity.USERNAME_PROPERTY.getLabel(), user);
      commentEntity.setProperty(CommentEntity.TIME_PROPERTY.getLabel(), time);
      commentEntity.setProperty(CommentEntity.MOOD_PROPERTY.getLabel(), mood);
      DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
      datastore.put(commentEntity);
    }

    response.sendRedirect(REDIRECT_URL);
  }

  /** Checks if the request parameter is specified in the client and returns default if not. */
  private static String getParameter(HttpServletRequest request, String name, String defaultValue) {
    String value = request.getParameter(name);
    if (value == null || value.isEmpty()) {
      return defaultValue;
    }
    return value;
  }
}
