/**
 * Comment form for wissel.net.
 *
 * Talks to the blog-comments Cloudflare Worker and gates submission behind a
 * Cloudflare Turnstile challenge. Replaces the prior script-tag-library +
 * reCAPTCHA version; Markdown.Editor and mustache.js have no such dependency,
 * so nothing else needs it.
 */
/* global Mustache, Markdown, turnstile */

/**
 * /why: wissel.net, notessensei.com and their www forms are all served by the
 * same Worker, so a relative URL is same-origin for every one of them. Only the
 * stwissel.github.io fallback, served by GitHub Pages, is cross-origin. Branching
 * on the fallback rather than listing the aliases means a future alias needs no
 * change here.
 */
var COMMENT_ENDPOINT = location.hostname.endsWith(".github.io")
  ? "https://wissel.net/blogcomments"
  : "/blogcomments";

var TEMPLATE_URL = "/blog/js2/comment.mustache";

var turnstileWidgetId = null;

function byId(id) {
  return document.getElementById(id);
}

function setVisible(id, visible) {
  var element = byId(id);
  if (element) {
    element.style.display = visible ? "" : "none";
  }
}

function showAlert(text, isError) {
  var container = byId("alertContainer");
  if (!container) {
    return;
  }
  container.textContent = "";
  var pre = document.createElement("pre");
  pre.textContent = text;
  container.appendChild(pre);
  container.classList.toggle("alert-error", Boolean(isError));
  container.style.display = "";
}

/** Renders the form, wires the markdown editor, and mounts the Turnstile widget. */
function renderComment(siteKey, parentId) {
  var host = byId("commentform_" + parentId);
  if (!host) {
    return Promise.resolve();
  }

  return fetch(TEMPLATE_URL, { cache: "no-cache" })
    .then(function (response) {
      if (!response.ok) {
        throw new Error("comment form template unavailable");
      }
      return response.text();
    })
    .then(function (template) {
      host.innerHTML = Mustache.render(template, {
        sitekey: siteKey,
        parentId: parentId
      });

      // Hidden until Turnstile reports a solved challenge.
      setVisible("commentsubmit", false);

      var editor = new Markdown.Editor(Markdown.getSanitizingConverter());
      editor.run();

      turnstileWidgetId = turnstile.render("#captchadiv", {
        sitekey: siteKey,
        theme: "auto",
        callback: function () {
          setVisible("commentsubmit", true);
        },
        "expired-callback": function () {
          setVisible("commentsubmit", false);
        },
        "error-callback": function () {
          setVisible("commentsubmit", false);
          showAlert("The challenge could not load. Please reload the page.", true);
        }
      });
    })
    .catch(function (error) {
      showAlert("The comment form could not be loaded: " + error.message, true);
    });
}

/** Restores the form so a failed submission can be corrected and retried. */
function resetCommentForm() {
  setVisible("commentcontrol", true);
  setVisible("captchadiv", true);
  setVisible("commentsubmit", false);
  if (turnstileWidgetId !== null) {
    // Tokens are single-use: a retry needs a fresh challenge.
    turnstile.reset(turnstileWidgetId);
  }
}

/** Called from the form's onSubmit. Always returns false — submission is via fetch. */
function addComment(form, siteKey, parentId) {
  setVisible("commentsubmit", false);
  setVisible("commentcontrol", false);
  setVisible("captchadiv", false);
  showAlert("One moment please, submitting comment...", false);

  var payload = {
    Commentor: byId("Commentor").value,
    eMail: byId("Email").value,
    webSite: byId("webSite").value,
    Body: byId("wmd-input").value,
    parentId: parentId,
    turnstileToken: turnstile.getResponse(turnstileWidgetId)
  };

  fetch(COMMENT_ENDPOINT, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(payload)
  })
    .then(function (response) {
      return response
        .json()
        .catch(function () {
          return { message: "Something went wrong, we are sooo sorry" };
        })
        .then(function (result) {
          return { ok: response.ok, message: result.message };
        });
    })
    .then(function (result) {
      showAlert(result.message, !result.ok);
      if (result.ok) {
        // Give the reader time to read the confirmation, then offer a fresh form.
        window.setTimeout(function () {
          renderComment(siteKey, parentId);
        }, 5000);
      } else {
        resetCommentForm();
      }
    })
    .catch(function (error) {
      showAlert("Something went wrong: " + error.message, true);
      resetCommentForm();
    });

  return false;
}
