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

let slideIndex = 0;

/**
 * Helper function for naviagtion via arrows.
 */
function changeSlides(offset) {
  showSlides(slideIndex + offset);
}

/**
 * Displays photo at new index by changing its display style and setting the corresponding indicator.
 */
function showSlides(newIndex) {
  let slides = document.getElementsByClassName("slide");
  let dots = document.getElementsByClassName("dot");
  // Hide the previous photo and indicator.
  slides[slideIndex].style.display = "none";
  dots[slideIndex].className = dots[slideIndex].className.replace("active", "");
  // Handle edge cases and set the new slide index.
  if (newIndex > slides.length - 1) {
    slideIndex = 0;
  } else if (newIndex < 0) {
    slideIndex = slides.length - 1;
  } else {
    slideIndex = newIndex;
  }
  // Display the photo.
  slides[slideIndex].style.display = "block";
  dots[slideIndex].className += " active";
}

/** Displays list of comments returned by the server.*/
function showComments() {
  const limit = document.getElementById("select-comment-number").value;
  fetch('/comments?number=' + limit).then(response => response.json()).then(comments => {
    const commentsList = document.getElementById('text-container');
    commentsList.innerHTML = '';
    for(let i = 0; i < comments.length; i++) {
      commentsList.appendChild(createCommentElement(comments[i]));
    }
  });
}

/** Creates an <p> comment element containing text. */
function createCommentElement(text) {
  const element = document.createElement('p');
  element.innerText = text;
  element.className = 'comment';
  return element;
}

/** Deletes all comments on by the server. */
function deleteComments() {
  fetch('/delete-comments', {
    method: 'POST'
  }).then(showComments());
}
