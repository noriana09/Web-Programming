document.addEventListener("DOMContentLoaded", async function () {
    try {
        const response = await fetch("/A3_4739/getUserDetails");
        const data = await response.json();

        if (data.success) {
            const fields = [
                "username", "email", "firstname", "lastname", "birthdate", "gender",
                "country", "address", "municipality", "prefecture", "job",
                "telephone", "afm", "latitude", "longitude"
            ];

            fields.forEach(field => {
                document.getElementById(`${field}Display`).innerText = data[field] || "";
                if (document.getElementById(field)) {
                    document.getElementById(field).value = data[field] || "";
                }
            });
        } else {
            console.error("Failed to fetch user details:", data.message);
        }
    } catch (error) {
        console.error("Error fetching user details:", error);
    }
});

function enableEditing(fieldId) {
    const displayElement = document.getElementById(`${fieldId}Display`);
    const inputElement = document.getElementById(fieldId);

    displayElement.style.display = "none";
    inputElement.style.display = "inline-block";
    document.getElementById("saveChangesButton").style.display = "inline-block";
}

const editableFields = [
    "firstname", "lastname", "birthdate", "gender",
    "country", "address", "municipality", "prefecture", "job",
    "latitude", "longitude"
];

editableFields.forEach(field => {
    const buttonId = `edit${field.charAt(0).toUpperCase() + field.slice(1)}`;
    console.log(`Looking for button with ID: ${buttonId}`);
    const button = document.getElementById(buttonId);
    if (button) {
        button.addEventListener("click", () => enableEditing(field));
    } else {
        console.error(`Button with ID "${buttonId}" not found.`);
    }
});

// Save Changes
document.getElementById("saveChangesButton").addEventListener("click", async () => {
    const updatedDetails = {};

    editableFields.forEach(field => {
        const inputElement = document.getElementById(field);
        if (inputElement && inputElement.style.display !== "none") {
            updatedDetails[field] = inputElement.value;
        }
    });

    //AJAX REQUEST
    try {
        const response = await fetch("/A3_4739/updateUserDetails", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(updatedDetails),
        });

        const data = await response.json();

        if (data.success) {
            alert("Profile updated successfully!");
            location.reload();
        } else {
            alert(`Error updating profile: ${data.message}`);
        }
    } catch (error) {
        console.error("Error updating profile:", error);
    }
});

//add incident user
// Wait until the DOM is fully loaded
document.addEventListener("DOMContentLoaded", () => {
    // Select the form element
    const form = document.querySelector("#add-incident-form");

    if (form) {
        console.log("Form found:", form);

        // Attach the submit event listener to the form
        form.addEventListener("submit", async function (event) {
            event.preventDefault(); // Prevent default form submission behavior

            // Collect form data
            const data = {
                incident_type: document.getElementById("incident_type_input").value.trim(),
                description: document.getElementById("description_input").value.trim(),
                user_type: document.getElementById("user_type_input").value.trim(),
                user_phone: document.getElementById("user_phone_input").value.trim(),
                address: document.getElementById("address_input").value.trim(),
                lat: parseFloat(document.getElementById("lat_input").value) || null,
                lon: parseFloat(document.getElementById("lon_input").value) || null,
                prefecture: document.getElementById("prefecture_input").value.trim(),
                municipality: document.getElementById("municipality_input").value.trim()
            };
            // Validate required fields
            if (!data.incident_type || !data.description || !data.user_type ||
                !data.user_phone || !data.address || !data.prefecture || !data.municipality) {
                document.querySelector("#add-incident-result").textContent =
                    "Error: All fields are required!";
                return;
            }

            // Submit the data via a POST request
            try {
                const response = await fetch("/A3_4739/incident", {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/json",
                    },
                    body: JSON.stringify(data),
                });

                // If the response is not OK, handle the error
                if (!response.ok) {
                    const err = await response.json();
                    throw new Error(err.message || "Server error");
                }

                const json = await response.json();
                document.querySelector("#add-incident-result").textContent = json.message;
            } catch (error) {
                console.error("Error submitting incident:", error);
                document.querySelector("#add-incident-result").textContent = error.message;
            }
        });
    } else {
        console.error("Form not found!");
    }
});


document.addEventListener("DOMContentLoaded", async function () {
    const incidentListContainer = document.getElementById("incidentList");
    let userLat, userLon;

    try {
        // Step 1: Fetch user details
        const userResponse = await fetch("/A3_4739/getUserDetails");
        const userData = await userResponse.json();

        if (userData.success) {
            // Extract latitude and longitude from the response
            userLat = parseFloat(userData.latitude);
            userLon = parseFloat(userData.longitude);

            if (isNaN(userLat) || isNaN(userLon)) {
                throw new Error("Invalid latitude or longitude from user details.");
            }
        } else {
            console.error("Failed to fetch user details:", userData.message);
            incidentListContainer.innerHTML = "<p>Error loading user details.</p>";
            return;
        }

        // Step 2: Fetch active incidents
        const response = await fetch("/A3_4739/incident/all/running");
        const incidents = await response.json();

        if (incidents.length > 0) {
            // Step 3: Calculate distances and durations using TrueWay API
            const { distances, durations } = await calculateDistancesAndDurations(userLat, userLon, incidents);

            // Step 4: Display incidents with distances and durations
            incidentListContainer.innerHTML = ""; // Clear loading message
            incidents.forEach((incident, index) => {
                const distance = distances[index] / 1000; // Convert meters to kilometers
                const duration = durations[index] / 60; // Convert seconds to minutes
                const incidentElement = document.createElement("div");
                incidentElement.className = "incident-item";
                incidentElement.innerHTML = `
                    <p><strong>Type:</strong> ${incident.incident_type}</p>
                    <p><strong>Description:</strong> ${incident.description}</p>
                    <p><strong>Address:</strong> ${incident.address}</p>
                    <p><strong>Prefecture:</strong> ${incident.prefecture}</p>
                    <p><strong>Municipality:</strong> ${incident.municipality}</p>
                    <p><strong>Distance from You:</strong> ${distance.toFixed(2)} km</p>
                    <p><strong>Duration by Car:</strong> ${duration.toFixed(1)} minutes</p>
                `;
                incidentListContainer.appendChild(incidentElement);
            });
        } else {
            incidentListContainer.innerHTML = "<p>No active incidents found.</p>";
        }
    } catch (error) {
        console.error("Error fetching incidents:", error);
        incidentListContainer.innerHTML = "<p>Error loading incidents.</p>";
    }
});

// Function to calculate distances and durations using TrueWay API
async function calculateDistancesAndDurations(userLat, userLon, incidents) {
    const origins = `${userLat},${userLon}`;
    const destinations = incidents
        .map(incident => `${incident.lat},${incident.lon}`)
        .join(";");

    const apiUrl = `https://trueway-matrix.p.rapidapi.com/CalculateDrivingMatrix?origins=${origins}&destinations=${destinations}`;
    const apiKey = "53988821camshb9974f9da3a8129p177f1ajsna387cc530843"; // Replace with your actual RapidAPI key

    try {
        const response = await fetch(apiUrl, {
            method: "GET",
            headers: {
                "x-rapidapi-host": "trueway-matrix.p.rapidapi.com",
                "x-rapidapi-key": apiKey,
            },
        });

        const data = await response.json();

        if (!data.distances || !data.durations) {
            throw new Error("Invalid distances or durations from API.");
        }

        return {
            distances: data.distances[0], // Array of distances in meters
            durations: data.durations[0], // Array of durations in seconds
        };
    } catch (error) {
        console.error("Error fetching distances and durations from TrueWay API:", error);
        return { distances: [], durations: [] };
    }
}


document.addEventListener("DOMContentLoaded", async function () {
    const publicMessagesContainer = document.getElementById("publicMessagesContainer");

    try {
        const response = await fetch(`/A3_4739/user/messages/public`);
        const messages = await response.json();

        if (messages.length > 0) {
            publicMessagesContainer.innerHTML = ""; // Clear loading message
            messages.forEach(msg => {
                const messageElement = document.createElement("div");
                messageElement.className = "message-item";
                messageElement.innerHTML = `
                    <p><strong>Sender:</strong> ${msg.sender}</p>
                    <p><strong>Message:</strong> ${msg.message}</p>
                    <p><strong>Incident ID:</strong> ${msg.incident_id}</p>
                    <p><strong>Date:</strong> ${new Date(msg.date_time).toLocaleString()}</p>
                `;
                publicMessagesContainer.appendChild(messageElement);
            });
        } else {
            publicMessagesContainer.innerHTML = "<p>No public messages found for active incidents.</p>";
        }
    } catch (error) {
        console.error("Error fetching public messages:", error);
        publicMessagesContainer.innerHTML = "<p>Error loading messages.</p>";
    }
});


async function populateIncidentDropdown() {
    const incidentSelect = document.getElementById("incidentSelect");

    try {
        const response = await fetch("/A3_4739/incident/all/running");
        const incidents = await response.json();

        if (incidents.length > 0) {
            incidents.forEach(incident => {
                const option = document.createElement("option");
                option.value = incident.incident_id;
                option.textContent = `Incident ${incident.incident_id}: ${incident.description}`;
                incidentSelect.appendChild(option);
            });
        } else {
            const option = document.createElement("option");
            option.value = "";
            option.textContent = "No running incidents available";
            incidentSelect.appendChild(option);
        }
    } catch (error) {
        console.error("Error fetching incidents:", error);
    }
}

// Populate the dropdown when the DOM is loaded
document.addEventListener("DOMContentLoaded", populateIncidentDropdown);


document.getElementById("sendMessageForm").addEventListener("submit", async function (event) {
    event.preventDefault();
    const incidentId = document.getElementById("incidentSelect").value;
    const recipient = document.getElementById("recipient").value;
    const messageContent = document.getElementById("messageContent").value.trim();
    const resultElement = document.getElementById("sendMessageResult");

    if (!incidentId || !recipient || !messageContent) {
        resultElement.textContent = "All fields are required!";
        return;
    }

    try {
        const response = await fetch("/A3_4739/user/messages/send", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({
                incident_id: parseInt(incidentId),
                recipient,
                message: messageContent,
                sender: "user" // Replace with dynamic user information if available
            })
        });

        const data = await response.json();
        if (data.success) {
            resultElement.textContent = "Message sent successfully!";
        } else {
            resultElement.textContent = `Error sending message: ${data.message}`;
        }
    } catch (error) {
        console.error("Error sending message:", error);
        resultElement.textContent = "An error occurred while sending the message.";
    }
});

