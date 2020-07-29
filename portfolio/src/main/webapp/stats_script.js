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

google.charts.load('current', {'packages':['corechart']});
google.charts.setOnLoadCallback(displayGeneralCommentChart);
google.charts.setOnLoadCallback(displayMoodCommentChart);

function displayGeneralCommentChart() {
   fetch('/comment-stats')
      .then((response) => response.json()).then((data) => {
        const generalData = new google.visualization.arrayToDataTable([
          ['Username', 'Number of Comments'],
          ['Anonymous', data['anonymous']],
          ['Custom', data['comments'] - data['anonymous']]
        ]);
        const generalOptions = {
          title: 'Number of Comments by Username',
              'width':500,
              'height':300
        };
        const generalChart = new google.visualization.PieChart(document.getElementById('comment-piechart'));
        generalChart.draw(generalData, generalOptions);
      });
}

function displayMoodCommentChart() {
   fetch('/comment-stats')
      .then((response) => response.json()).then((data) => {
          const moodData = new google.visualization.arrayToDataTable([
            ['Mood', 'Number of Comments'],
            ['😀', data['😀']],
            ['🤔', data['🤔']],
            ['🤠', data['🤠']],
            ['☹️', data['☹️']],
            ['👽', data['👽']],
          ]);
          const moodOptions = {
            title: 'Number of Comments by Mood',
              'width':500,
              'height':300
          };
        const moodChart = new google.visualization.PieChart(document.getElementById('mood-piechart'));
        moodChart.draw(moodData, moodOptions);
      });
}
