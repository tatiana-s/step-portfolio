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

/** Current slide index in photo gallery. */
let slideIndex = 0;

/** Current filter settings in comment section. */
let commentLimit = 4;
let sortOrder = 'new';

/**
 * Helper function for naviagtion via arrows.
 * @param {number} offset The number of slides to change by
 * (negative numbers indicate going backwards).
 */
function changeSlides(offset) {
  showSlides(slideIndex + offset);
}

/**
 * Displays photo at new index by changing its display style
 * and setting the corresponding indicator.
 * @param {number} newIndex Index of photo to be displayed.
 */
function showSlides(newIndex) {
  const slides = document.getElementsByClassName('slide');
  const dots = document.getElementsByClassName('dot');
  // Hide the previous photo and indicator.
  slides[slideIndex].style.display = 'none';
  dots[slideIndex].className = dots[slideIndex].className.replace('active', '');
  // Handle edge cases and set the new slide index.
  if (newIndex > slides.length - 1) {
    slideIndex = 0;
  } else if (newIndex < 0) {
    slideIndex = slides.length - 1;
  } else {
    slideIndex = newIndex;
  }
  // Display the photo.
  slides[slideIndex].style.display = 'block';
  dots[slideIndex].className += ' active';
}

/** Called when website is loaded. */
function init() {
  showCommentForm();
  showComments();
}

async function showCommentForm() {
  const status = await checkLoginStatus();
  const message = document.getElementById('login-message');
  message.innerHTML = '';
  message.append(createLoginMessage(status));
  document.getElementById('email-input').value = status.userEmail;
  if (status.loggedIn) {
    document.getElementById('comments-form').style.display = 'block';
  } else {
    document.getElementById('comments-form').style.display = 'none';
  }
}

async function checkLoginStatus() {
  const response = await fetch('/user');
  const status = await response.json();
  return status;
}

function createLoginMessage(status) {
  const element = document.createElement('span');
  if (status.loggedIn) {
    element.innerText = 'You are currently logged in as ' + status.userEmail + ', log out ';
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
function changeLimit() {
  commentLimit = document.getElementById('select-comment-number').value;
  showComments();
}

/** Called when sorting order selector changes: changes sorting order. */
function changeSort() {
  sortOrder = document.getElementById('select-comment-sort').value;
  showComments();
}

/** Displays list of comments returned by the server if user is logged in.*/
function showComments() {
  const url = new URL(window.location.origin + '/comments');
  const params = {limit: commentLimit, sort: sortOrder};
  url.search = new URLSearchParams(params).toString();
  fetch(url)
    .then((response) => response.json()).then((comments) => {
      const commentsList = document.getElementById('text-container');
      commentsList.innerHTML = '';
      for (let i = 0; i < comments.length; i++) {
        commentsList.appendChild(createCommentElement(comments[i]));
      }
    });
}

/**
 * Creates an <p> comment element containing text.
 * @param {Object} comment The comment object.
 * @return {HTMLElement} The html element.
 */
function createCommentElement(comment) {
  const element = document.createElement('div');
  element.className = 'comment';
  const username = document.createElement('span');
  username.innerText = comment.user;
  username.className = 'comment-username';
  const mood = document.createElement('span');
  mood.innerText = comment.mood;
  mood.className = 'comment-mood';
  const content = document.createElement('p');
  content.innerText = comment.content;
  content.className = 'comment-content';

  const deleteButton = document.createElement('button');
  deleteButton.className = 'delete-button';
  deleteButton.innerText = 'Delete';
  deleteButton.addEventListener('click', () => {
    element.remove();
    deleteComment(comment);
  });

  element.appendChild(mood);
  element.appendChild(username);
  element.appendChild(deleteButton);
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
  await fetch('/delete-comments', {
    method: 'POST',
    body: params,
  });
  showComments();
}
