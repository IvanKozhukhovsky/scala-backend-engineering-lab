(() => {
  const quizzes = document.querySelectorAll("[data-quiz]");
  if (quizzes.length === 0) return;

  const shuffle = (nodes) => {
    const items = Array.from(nodes);
    for (let i = items.length - 1; i > 0; i -= 1) {
      const j = Math.floor(Math.random() * (i + 1));
      [items[i], items[j]] = [items[j], items[i]];
    }
    return items;
  };

  quizzes.forEach((quiz) => {
    const items = quiz.querySelectorAll(".quiz-item");
    let answered = 0;
    let correct = 0;
    const score = quiz.querySelector(".quiz-score");

    items.forEach((item) => {
      const choices = item.querySelector(".quiz-choices");
      if (choices) {
        shuffle(choices.children).forEach((button) => choices.appendChild(button));
      }

      const buttons = item.querySelectorAll(".quiz-choices button");
      const feedback = item.querySelector(".quiz-feedback");

      buttons.forEach((button) => {
        button.addEventListener("click", () => {
          if (item.dataset.locked === "true") return;
          item.dataset.locked = "true";
          answered += 1;

          const isCorrect = button.dataset.correct === "true";
          buttons.forEach((candidate) => {
            candidate.disabled = true;
            if (candidate.dataset.correct === "true") candidate.classList.add("correct");
          });

          if (isCorrect) {
            correct += 1;
            button.classList.add("correct");
            if (feedback) feedback.textContent = feedback.dataset.ok || "Correct.";
          } else {
            button.classList.add("incorrect");
            if (feedback) feedback.textContent = feedback.dataset.bad || "Not quite. The highlighted answer is the one to remember.";
          }
          if (feedback) feedback.hidden = false;
          if (score) score.textContent = `Score: ${correct} / ${items.length}`;
        });
      });
    });
  });
})();
