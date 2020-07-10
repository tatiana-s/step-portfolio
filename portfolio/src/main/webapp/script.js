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
 * Helper functions for naviagtion via arrows and indicator dots.
 */
function changeSlides(n) {
  showSlides(slideIndex += n);
}

function currentSlide(n) {
  showSlides(slideIndex = n);
}

/**
 * Displays photo at index n by changing its display style and setting the corresponding indicator.
 */
function showSlides(n) {
  let slides = document.getElementsByClassName("slide");
  let dots = document.getElementsByClassName("dot");
  // handle edge cases
  if (n > slides.length - 1) {
    slideIndex = 0;
  } else if (n < 0) {
    slideIndex = slides.length - 1;
  } else {
    slideIndex = n;
  }
  let i;
  // hide all photos
  for (i = 0; i < slides.length; i++) {
      slides[i].style.display = "none";
  }
  // deactivate all indicators
  for (i = 0; i < dots.length; i++) {
      dots[i].className = dots[i].className.replace("active", "");
  }
  // display photo
  slides[slideIndex].style.display = "block";
  dots[slideIndex].className += " active";
}
