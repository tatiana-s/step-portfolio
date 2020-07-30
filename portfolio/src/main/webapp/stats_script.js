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

/* eslint-disable no-unused-vars */
/* eslint-disable require-jsdoc*/

google.charts.load('current', {'packages': ['corechart']});
google.charts.setOnLoadCallback(displayPageViewsCharts);
google.charts.setOnLoadCallback(displayGeneralCommentChart);
google.charts.setOnLoadCallback(displayMoodCommentChart);

/** Display settings for all the charts. */
const options = {
  hAxis: {
    titleTextStyle: {color: '#607d8b'},
    gridlines: {count: 0},
    textStyle: {
      color: '#b0bec5',
      fontSize: '14',
      bold: true,
    },
  },
  vAxis: {
    minValue: 0,
    gridlines: {
      color: '#37474f',
    },
    baselineColor: 'transparent',
    textStyle: {
      color: '#b0bec5',
      fontSize: '14',
      bold: true,
    },
  },
  animation: {
    startup: true,
    duration: 1000,
    easing: 'out',
  },
  backgroundColor: 'transparent',
  chartArea: {backgroundColor: 'transparent'},
  colors: ['#9acd32', '#22e0da', '#2287e0', '#dceb0c', '#0eebb7'],
  legend: {
    position: 'top',
    alignment: 'center',
    textStyle: {color: 'white', fontSize: '14'},
  },
};

function displayPageViewsCharts() {
  fetch('/page-view-stats')
      .then((response) => response.json()).then((data) => {
        const dataTable = new google.visualization.DataTable();
        dataTable.addColumn('string', 'Date');
        dataTable.addColumn('number', 'Views');
        for (const entry of Object.entries(data)) {
          dataTable.addRow(entry);
        }
        const chart = new google.visualization.ColumnChart(
            document.getElementById('views-columnchart'));
        chart.draw(dataTable, options);
      });
}

function displayGeneralCommentChart() {
  fetch('/comment-stats')
      .then((response) => response.json()).then((data) => {
        const dataTable = new google.visualization.DataTable();
        dataTable.addColumn('string', 'Username' );
        dataTable.addColumn('number', 'Number of Comments');
        anonymousTotal = data['commentCount']['anonymousTotal'];
        total = data['commentCount']['total'] - anonymousTotal;
        dataTable.addRows([
          ['Anonymous', anonymousTotal],
          ['Custom Username', total],
        ]);
        const chart = new google.visualization.PieChart(
            document.getElementById('comment-piechart'));
        chart.draw(dataTable, options);
      });
}

function displayMoodCommentChart() {
  fetch('/comment-stats')
      .then((response) => response.json()).then((data) => {
        const dataTable = new google.visualization.DataTable();
        dataTable.addColumn('string', 'Mood' );
        dataTable.addColumn('number', 'Number of Comments');
        dataTable.addRows([
          ['😀', data['moodCount']['😀']],
          ['🤔', data['moodCount']['🤔']],
          ['🤠', data['moodCount']['🤠']],
          ['☹️', data['moodCount']['☹️']],
          ['👽', data['moodCount']['👽']],
        ]);
        const chart = new google.visualization.PieChart(
            document.getElementById('mood-piechart'));
        chart.draw(dataTable, options);
      });
}
