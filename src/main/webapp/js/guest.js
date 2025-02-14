document.getElementById("chatgpt-form").addEventListener("submit", function (event) {
    event.preventDefault();

    const defaultQuestion = document.getElementById("chatgpt-default-questions").value;
    const customQuestion = document.getElementById("chatgpt-custom-question").value.trim();

    // Προτεραιότητα στην custom ερώτηση, αν υπάρχει
    const question = customQuestion || defaultQuestion;

    // Στέλνουμε το ερώτημα στο backend
    fetch("http://localhost:8081/chatgpt", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({ question: question })
    })
        .then(response => {
            if (!response.ok) {
                throw new Error("Σφάλμα κατά την επικοινωνία με το ChatGPT API.");
            }
            return response.json();
        })
        .then(data => {
            document.getElementById("chatgpt-response").textContent = data.response;
        })
        .catch(error => {
            document.getElementById("chatgpt-response").textContent = 'Σφαλμα: ${error.message}';
        });
});