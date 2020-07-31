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

/** Current filter settings in comment section. */
let commentLimit = 4;
let sortOrder = 'new';

/** Moods that can be selected in the comment form. */
const moods = ['😀', '🤔', '🤠', '☹️', '👽'];
export default moods;

/** Called when website is loaded. */
window.init = function init() {
  showCommentForm();
  showComments();
  updatePageViews();
};

/**
 * Displays a login status message
 * and if the user is authenticated the comment form.
 */
async function showCommentForm() {
  const status = await fetchLoginStatus();
  const message = document.getElementById('login-message');
  message.innerHTML = '';
  message.append(createLoginMessage(status));
  document.getElementById('email-input').value = status.userEmail;
  if (status.loggedIn) {
    document.getElementById('comments-form').style.display = 'block';
  } else {
    document.getElementById('comments-form').style.display = 'none';
  }
  const moodForm = document.getElementById('select-mood');
  for (let i = 0; i < moods.length; i++) {
    const option = document.createElement('option');
    option.appendChild(document.createTextNode(moods[i]));
    option.value = moods[i];
    moodForm.appendChild(option);
  }
}

/**
 * Returns login status including current email adress and login/logout link.
 * Asynchronous function so await should be used when calling it.
 * @return {Promise} The a resolved promise containing the status object.
 */
function fetchLoginStatus() {
  const status = fetch('/user')
      .then((response) => response.json()).then((status) => {
        return status;
      });
  return status;
}

/**
 * Depending on whether user is authenticated or not
 * displays either a login or logout link.
 * @param {Object} status The status object.
 * @return {HTMLElement} The resulting html element.
 */
function createLoginMessage(status) {
  const element = document.createElement('span');
  if (status.loggedIn) {
    element.innerText = 'You are currently logged in as ' + status.userEmail +
      ', log out ';
  } else {
    element.innerText = 'You need to log in to leave comments, log in ';
  }
  const link = document.createElement('a');
  link.href = status.link;
  link.innerHTML = 'here.';
  element.append(link);
  return element;
}

/** Called when comment number limit selector changes: changes limit. */
window.changeLimit = function changeLimit() {
  commentLimit = document.getElementById('select-comment-number').value;
  showComments();
};

/** Called when sorting order selector changes: changes sorting order. */
window.changeSort = function changeSort() {
  sortOrder = document.getElementById('select-comment-sort').value;
  showComments();
};

/** Displays list of comments returned by the server if user is logged in.*/
async function showComments() {
  const url = new URL(window.location.origin + '/comments');
  const params = {limit: commentLimit, sort: sortOrder};
  url.search = new URLSearchParams(params).toString();
  fetch(url)
      .then((response) => response.json()).then(async (comments) => {
        const commentsList = document.getElementById('text-container');
        const status = await fetchLoginStatus();
        const currEmail = status.userEmail;
        commentsList.innerHTML = '';
        for (let i = 0; i < comments.length; i++) {
          const commentElement = createCommentElement(comments[i], currEmail);
          commentsList.appendChild(commentElement);
        }
      });
}

/**
 * Creates an <p> comment element containing text.
 * @param {Object} comment The comment object.
 * @param {Object} currentEmail The current user email.
 * @return {HTMLElement} The html element.
 */
function createCommentElement(comment, currentEmail) {
  const element = document.createElement('div');
  element.className = 'comment';
  const username = document.createElement('span');
  username.innerText = comment.username;
  username.className = 'comment-username';
  const mood = document.createElement('span');
  mood.innerText = comment.mood;
  mood.className = 'comment-mood';
  const content = document.createElement('p');
  content.innerText = comment.content;
  content.className = 'comment-content';

  element.appendChild(mood);
  element.appendChild(username);

  if (currentEmail === comment.email) {
    const deleteButton = document.createElement('button');
    deleteButton.className = 'delete-button';
    deleteButton.innerText = 'Delete';
    deleteButton.addEventListener('click', () => {
      element.remove();
      deleteComment(comment);
    });
    element.appendChild(deleteButton);
  }

  element.appendChild(content);
  return element;
}

/**
 * Deletes all comments on by the server.
 * @param {Object} comment The comment object.
 */
async function deleteComment(comment) {
  const params = new URLSearchParams();
  params.append('id', comment.id);
  await fetch('/delete-comment', {
    method: 'POST',
    body: params,
  });
  showComments();
}

/** Adds a comment when the comment form is submitted. */
window.addComment = async function addComment() {
  const form = document.getElementById('comments-form');
  const params = new URLSearchParams();
  const formData = new FormData(form);
  for (const pair of formData.entries()) {
    params.append(pair[0], pair[1]);
  }
  await fetch('/add-comment', {
    method: 'POST',
    body: params,
  });
  form.reset();
  showCommentForm();
  showComments();
};

/** Increments daily page view count. */
function updatePageViews() {
  fetch('/add-page-view', {
    method: 'POST',
  });
}
