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
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.CompositeFilter;
import com.google.appengine.api.datastore.Query.CompositeFilterOperator;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.sps.data.ViewsEntity;
import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet for incrementing page views every time the page is loaded. */
@WebServlet("/add-page-view")
public class AddPageViewServlet extends HttpServlet {

  private static final String REDIRECT_URL = "/index.html";

  /** Stores page view count in views entity for current day. */
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Date date = new Date();
    LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    int year = localDate.getYear();
    int month = localDate.getMonthValue();
    int day = localDate.getDayOfMonth();
    long lastUpdated = System.currentTimeMillis();

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    Query query = new Query(ViewsEntity.KIND.getLabel());
    query.setFilter(
        new CompositeFilter(
            CompositeFilterOperator.AND,
            Arrays.asList(
                new FilterPredicate(
                    ViewsEntity.YEAR_PROPERTY.getLabel(), FilterOperator.EQUAL, year),
                new FilterPredicate(
                    ViewsEntity.MONTH_PROPERTY.getLabel(), FilterOperator.EQUAL, month),
                new FilterPredicate(
                    ViewsEntity.DAY_PROPERTY.getLabel(), FilterOperator.EQUAL, day))));
    Entity viewsEntity = datastore.prepare(query).asSingleEntity();

    if (viewsEntity == null) {
      viewsEntity = new Entity(ViewsEntity.KIND.getLabel());
      viewsEntity.setProperty(ViewsEntity.YEAR_PROPERTY.getLabel(), year);
      viewsEntity.setProperty(ViewsEntity.MONTH_PROPERTY.getLabel(), month);
      viewsEntity.setProperty(ViewsEntity.DAY_PROPERTY.getLabel(), day);
      viewsEntity.setProperty(ViewsEntity.COUNT_PROPERTY.getLabel(), 1);
    } else {
      long count = (long) (viewsEntity.getProperty(ViewsEntity.COUNT_PROPERTY.getLabel()));
      viewsEntity.setProperty(ViewsEntity.COUNT_PROPERTY.getLabel(), count + 1);
    }
    viewsEntity.setProperty(ViewsEntity.UPDATED_PROPERTY.getLabel(), lastUpdated);
    datastore.put(viewsEntity);

    response.sendRedirect(REDIRECT_URL);
  }
}
