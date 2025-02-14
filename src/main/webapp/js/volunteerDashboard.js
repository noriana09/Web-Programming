document.addEventListener("DOMContentLoaded", async function () {
    try {
        const response = await fetch("/A3_4739/getVolunteerDetails");
        const data = await response.json();

        if (data.success) {
            const fields = [
                "username", "email", "firstname", "lastname", "birthdate", "gender",
                "country", "address", "municipality", "prefecture", "job",
                "telephone", "afm", "latitude", "longitude","volunteer_type","height","weight"
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
    "email", "firstname", "lastname", "birthdate", "gender",
    "country", "address", "municipality", "prefecture", "job",
    "latitude", "longitude","volunteer_type","height","weight"
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
        const response = await fetch("/A3_4739/updateVolunteerDetails", {
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

let loggedInVolunteerId = null; // Variable to store the logged-in volunteer's ID
let volunteerType = null; // Store volunteer type for filtering positions
let pos_type = null;

// Fetch logged-in volunteer's details
async function fetchVolunteerDetails() {
    try {
        const response = await fetch("/A3_4739/getVolunteerDetails", { method: "GET" });
        const data = await response.json();

        if (data.success) {
            // Store the volunteer ID and type
            loggedInVolunteerId = data.volunteer_id; // Ensure this is the correct unique ID
            volunteerType = data.volunteer_type;
            pos_type = data.volunteer_type;

            // Debugging log
            console.log("Volunteer ID:", loggedInVolunteerId, "Type:", volunteerType);

            // Fetch available positions
            fetchAvailablePositions();
        } else {
            console.error("Failed to fetch volunteer details:", data.message);
            alert("Unable to fetch volunteer details. Please log in.");
        }
    } catch (error) {
        console.error("Error fetching volunteer details:", error);
        alert("An error occurred while fetching your details.");
    }
}

// Fetch available positions
async function fetchAvailablePositions() {
    try {
        if (!volunteerType) {
            console.error("Volunteer type is undefined.");
            return;
        }

        const response = await fetch(`/A3_4739/volunteer/positions?volunteer_type=${volunteerType}`, { method: "GET" });
        const positions = await response.json();
        console.log("Fetched Positions:", positions);

        const positionsTableBody = document.getElementById("positionsTable").querySelector("tbody");
        positionsTableBody.innerHTML = ""; // Clear any existing rows

        if (positions.length === 0) {
            positionsTableBody.innerHTML = "<tr><td colspan='5'>No positions available for your type.</td></tr>";
            return;
        }

        positions.forEach((position) => {
            const row = document.createElement("tr");

            row.innerHTML = `
                <td><input type="radio" name="positionSelect" value="${position.id}" onclick="selectPosition(${position.id})" /></td>
                <td>${position.incident_id}</td>
                <td>${position.position_type}</td>
                <td>${position.slots_open}</td>
                <td>${position.slots_filled}</td>
            `;

            positionsTableBody.appendChild(row);
        });
    } catch (error) {
        console.error("Error fetching positions:", error);
    }
}

// Function to handle position selection
function selectPosition(positionId) {
    document.getElementById("selectedPositionId").value = positionId;
}

// Submit a volunteer request
async function submitVolunteerRequest(event) {
    event.preventDefault(); // Prevent the form from refreshing the page

    const positionId = document.getElementById("selectedPositionId").value;

    if (!positionId) {
        alert("Please select a position!");
        return;
    }

    if (!loggedInVolunteerId) {
        alert("Unable to identify the logged-in volunteer.");
        return;
    }

    const requestData = {
        incident_id: parseInt(positionId), // Ensure correct mapping for position
        volunteer_id: loggedInVolunteerId,
        position_type:pos_type,
        status: "pending",
        request_date: new Date().toISOString() // Current timestamp in ISO 8601 format
    };

    console.log("Submitting request data:", requestData);

    try {
        const response = await fetch("/A3_4739/volunteer/request", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(requestData)
        });

        const result = await response.json();

        // Handle the response
        if (result.success) {
            document.getElementById("responseMessage").textContent = "Request submitted successfully!";
            fetchAvailablePositions(); // Refresh positions to update slots
        } else {
            document.getElementById("responseMessage").textContent = `Error: ${result.message}`;
        }
    } catch (error) {
        console.error("Error submitting volunteer request:", error);
        document.getElementById("responseMessage").textContent = "Server error.";
    }
}

// Initialize the dashboard
document.addEventListener("DOMContentLoaded", fetchVolunteerDetails);


document.addEventListener("DOMContentLoaded", async function () {
    async function fetchAvailableIncidents() {
        try {
            const response = await fetch("/A3_4739/volunteer/incidents", { method: "GET" });
            const incidents = await response.json();
            console.log("Fetched Incidents for Volunteer:", incidents);

            const incidentSelect = document.getElementById("incidentSelect");
            incidentSelect.innerHTML = "<option value=''>-- Select Incident --</option>";

            if (!incidents || incidents.length === 0) {
                incidentSelect.innerHTML = "<option value=''>No incidents available</option>";
                return;
            }

            incidents.forEach((incident) => {
                const option = document.createElement("option");
                option.value = incident.incident_id;
                option.textContent = `Incident ${incident.incident_id}`;
                incidentSelect.appendChild(option);
            });
        } catch (error) {
            console.error("Error fetching incidents for volunteer:", error);
        }
    }

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

    // Populate incidents dropdown
    await fetchAvailableIncidents();
});


document.addEventListener("DOMContentLoaded", async function () {
    // Fetch messages for the volunteer
    async function fetchMessages() {
        try {
            const response = await fetch("/A3_4739/volunteer/messages", { method: "GET" });
            const messages = await response.json();
            console.log("Fetched Messages:", messages);

            const messagesList = document.getElementById("messagesList");
            messagesList.innerHTML = ""; // Clear previous messages

            if (!messages || messages.length === 0) {
                messagesList.innerHTML = "<p>No messages available for your incidents.</p>";
                return;
            }

            messages.forEach((message) => {
                const messageDiv = document.createElement("div");
                messageDiv.className = "message-item";
                messageDiv.innerHTML = `
                    <p><strong>Incident ID:</strong> ${message.incident_id}</p>
                    <p><strong>Sender:</strong> ${message.sender}</p>
                    <p><strong>Recipient:</strong> ${message.recipient}</p>
                    <p><strong>Message:</strong> ${message.content}</p>
                    <p><strong>Timestamp:</strong> ${message.timestamp}</p>
                `;
                messagesList.appendChild(messageDiv);
            });
        } catch (error) {
            console.error("Error fetching messages:", error);
            document.getElementById("messagesList").innerHTML = "<p>Error loading messages.</p>";
        }
    }

    // Fetch messages on page load
    await fetchMessages();
});


document.addEventListener("DOMContentLoaded", async function () {
    // Fetch the volunteer's history and display it
    async function fetchVolunteerHistory() {
        try {
            const response = await fetch("/A3_4739/volunteer/history", { method: "GET" });
            const history = await response.json();
            console.log("Fetched Volunteer History:", history);

            const historyTableBody = document.getElementById("historyTableBody");
            historyTableBody.innerHTML = ""; // Clear any existing rows

            if (history.length === 0) {
                historyTableBody.innerHTML = "<tr><td colspan='5'>No history available</td></tr>";
                return;
            }

            history.forEach((incident) => {
                const row = document.createElement("tr");

                row.innerHTML = `
                    <td>${incident.incident_id}</td>
                    <td>${incident.description}</td>
                    <td>${incident.start_datetime}</td>
                    <td>${incident.end_datetime || "Ongoing"}</td>
                    <td>${incident.status}</td>
                `;

                historyTableBody.appendChild(row);
            });
        } catch (error) {
            console.error("Error fetching volunteer history:", error);
        }
    }

    // Call fetchVolunteerHistory on page load
    await fetchVolunteerHistory();
});
