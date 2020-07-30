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
import com.google.gson.Gson;
import com.google.sps.data.ViewsEntity;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet for accumulating page view data */
@WebServlet("/page-view-stats")
public class PageViewStatsServlet extends HttpServlet {

  /** Loads views from the datastore database and returns data from the last week. */
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Date date = new Date();
    LocalDate currentDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

    LinkedHashMap<String, Long> views = new LinkedHashMap<>();
    for (int i = 6; i >= 0; i--) {
      LocalDate dateOfInterest = currentDate.minusDays(i);
      String formattedDate = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT).format(dateOfInterest);
      views.put(formattedDate, getCount(dateOfInterest));
    }

    response.setContentType("application/json;");
    response.setCharacterEncoding("UTF-8");
    Gson gson = new Gson();
    response.getWriter().println(gson.toJson(views));
  }

  /** Returns number of page views on given date. */
  private long getCount(LocalDate date) {
    int year = date.getYear();
    int month = date.getMonthValue();
    int day = date.getDayOfMonth();

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
      return 0;
    } else {
      return (long) (viewsEntity.getProperty(ViewsEntity.COUNT_PROPERTY.getLabel()));
    }
  }
}
