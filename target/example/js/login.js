function loginUser() {
    const username = document.getElementById("username").value;
    const password = document.getElementById("password").value;
    const userType = document.getElementById("userType").value;

    const xhr = new XMLHttpRequest();

    // Determine the appropriate servlet
    let endpoint;
    if (userType === "admin") {
        endpoint = "LoginAdmin";
    } else if (userType === "volunteer") {
        endpoint = "LoginVolunteer";
    } else {
        endpoint = "LoginUser";
    }

    xhr.open("POST", endpoint, true);
    xhr.setRequestHeader("Content-type", "application/x-www-form-urlencoded");

    xhr.onload = function () {
        if (xhr.readyState === 4 && xhr.status === 200) {
            console.log("Login successful.");
            if (userType === "admin") {
                window.location.replace("adminDashboard.html");
            } else if (userType === "volunteer") {
                window.location.replace("volunteerDashboard.html");
            } else {
                window.location.replace("userDashboard.html");
            }
        } else if (xhr.status === 403) {
            console.log("Invalid credentials.");
        } else {
            console.log("An error occurred during login.");
        }
    };

    // Encode the data to send in the request body
    const requestData = `username=${encodeURIComponent(username)}&password=${encodeURIComponent(password)}&userType=${encodeURIComponent(userType)}`;

    xhr.send(requestData);
}
