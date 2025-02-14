document.addEventListener("DOMContentLoaded", () => {
    // 1. Fetch all incidents
    fetch("/A3_4739/admin/incidents", {
        method: "GET",
        headers: {
            "Content-Type": "application/json"
        }
    })
        .then(response => response.json())
        .then(incidents => {
            console.log("Incidents:", incidents);
            renderIncidents(incidents);
        })
        .catch(err => {
            console.error("Error fetching incidents:", err);
        });
});

// 2. Render the list of incidents
function renderIncidents(incidents) {
    const container = document.getElementById("incidents-container");
    container.innerHTML = ""; // Clear existing

    incidents.forEach(incident => {
        const incidentDiv = document.createElement("div");

        // Note the use of `incident.incident_id`
        incidentDiv.innerHTML = `
            <p><strong>ID:</strong> ${incident.incident_id}</p>
            <p><strong>Description:</strong> ${incident.description}</p>
            <p><strong>Status:</strong> <span id="status-${incident.incident_id}">${incident.status}</span></p>
            <p><strong>Danger:</strong> <span id="danger-${incident.incident_id}">${incident.danger || "N/A"}</span></p>
        `;

        // Buttons to update status
        const fakeButton = document.createElement("button");
        fakeButton.textContent = "Mark as Fake";
        // Pass the incident.incident_id
        fakeButton.addEventListener("click", () => updateIncident(incident.incident_id, "fake"));

        const runningButton = document.createElement("button");
        runningButton.textContent = "Mark as Running";
        runningButton.addEventListener("click", () => updateIncident(incident.incident_id, "running"));

        const finishedButton = document.createElement("button");
        finishedButton.textContent = "Mark as Finished";
        finishedButton.addEventListener("click", () => updateIncident(incident.incident_id, "finished"));

        // Danger input
        const dangerLabel = document.createElement("label");
        dangerLabel.textContent = "Danger: ";
        const dangerInput = document.createElement("input");
        dangerInput.type = "text";
        dangerInput.placeholder = "low, medium, high...";

        const dangerBtn = document.createElement("button");
        dangerBtn.textContent = "Update Danger";
        // Pass the incident.incident_id
        dangerBtn.addEventListener("click", () => updateIncidentDanger(incident.incident_id, dangerInput.value));

        // Append all
        incidentDiv.appendChild(fakeButton);
        incidentDiv.appendChild(runningButton);
        incidentDiv.appendChild(finishedButton);
        incidentDiv.appendChild(document.createElement("br"));
        incidentDiv.appendChild(dangerLabel);
        incidentDiv.appendChild(dangerInput);
        incidentDiv.appendChild(dangerBtn);

        container.appendChild(incidentDiv);
        container.appendChild(document.createElement("hr"));
    });
}

// 3. Update status
function updateIncident(incidentId, newStatus) {
    fetch("/A3_4739/admin/incidents/update", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({
            // Must match your servlet's expected JSON key
            incident_id: incidentId,
            status: newStatus
        })
    })
        .then(res => res.json())
        .then(data => {
            console.log("Update response:", data);
            if (data.success) {
                // Update the UI in-place
                document.getElementById(`status-${incidentId}`).textContent = newStatus;
            } else {
                alert("Failed to update incident status: " + data.message);
            }
        })
        .catch(err => {
            console.error("Error updating incident:", err);
        });
}

// 4. Update danger
function updateIncidentDanger(incidentId, dangerValue) {
    fetch("/A3_4739/admin/incidents/update", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({
            incident_id: incidentId,
            danger: dangerValue
        })
    })
        .then(res => res.json())
        .then(data => {
            console.log("Update response:", data);
            if (data.success) {
                document.getElementById(`danger-${incidentId}`).textContent = dangerValue;
            } else {
                alert("Failed to update danger level: " + data.message);
            }
        })
        .catch(err => {
            console.error("Error updating incident:", err);
        });
}

document.addEventListener('DOMContentLoaded', () => {
    const adminAddIncidentForm = document.getElementById('adminAddIncidentForm');
    if (!adminAddIncidentForm) return; // If form not found, do nothing

    adminAddIncidentForm.addEventListener('submit', async (e) => {
        e.preventDefault();

        // Gather form data
        const formData = new FormData(adminAddIncidentForm);
        const incidentData = {};

        formData.forEach((value, key) => {
            // Trim all incoming values
            incidentData[key] = value.trim();
        });

        // If you have numeric fields, parse them:
        if (incidentData.lat) {
            incidentData.lat = parseFloat(incidentData.lat);
        }
        if (incidentData.lon) {
            incidentData.lon = parseFloat(incidentData.lon);
        }
        if (incidentData.vehicles) {
            incidentData.vehicles = parseInt(incidentData.vehicles, 10);
        }
        if (incidentData.firemen) {
            incidentData.firemen = parseInt(incidentData.firemen, 10);
        }

        try {
            // POST to your servlet endpoint
            const response = await fetch("/A3_4739/admin/incidents/new", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify(incidentData)
            });

            const result = await response.json();

            // Check success
            if (response.ok && result.success) {
                document.getElementById('adminAddIncidentResult').textContent =
                    "Το συμβάν καταχωρήθηκε επιτυχώς!";

                // (Optional) Re-fetch incidents if you have a function for that:
                // fetchIncidents();
                window.location.reload();
            } else {
                // Show error returned by server
                document.getElementById('adminAddIncidentResult').textContent =
                    "Σφάλμα: " + (result.message || "Αποτυχία καταχώρησης συμβάντος.");
            }
        } catch (err) {
            console.error("Error adding incident:", err);
            document.getElementById('adminAddIncidentResult').textContent =
                "Σφάλμα σύνδεσης ή σφάλμα διακομιστή.";
        }
    });
});


document.addEventListener('DOMContentLoaded', function () {
    const createVolunteerPositionForm = document.getElementById('createVolunteerPositionForm');

    if (createVolunteerPositionForm) {
        createVolunteerPositionForm.addEventListener('submit', async function (e) {
            e.preventDefault();

            // Collect form data
            const formData = new FormData(createVolunteerPositionForm);
            const data = {};
            formData.forEach((value, key) => {
                data[key] = value;
            });

            try {
                // Display loading message
                document.getElementById('createVolunteerPositionResult').textContent =
                    'Δημιουργία θέσης...';

                // Send POST request to the server
                const response = await fetch('/A3_4739/admin/volunteerPositions/new', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                    },
                    body: JSON.stringify(data),
                });

                const result = await response.json();

                if (response.ok && result.success) {
                    // Success message
                    document.getElementById('createVolunteerPositionResult').textContent =
                        'Θέση εθελοντών δημιουργήθηκε επιτυχώς!';

                    // Clear form fields
                    createVolunteerPositionForm.reset();

                    // Optionally fetch updated volunteer positions
                    // Uncomment the following if you have a fetchVolunteerPositions function
                    // fetchVolunteerPositions();
                } else {
                    // Error message from the server
                    document.getElementById('createVolunteerPositionResult').textContent =
                        'Σφάλμα: ' + (result.message || 'Αποτυχία δημιουργίας θέσης.');
                }
            } catch (error) {
                // Connection or server error
                console.error('Error:', error);
                document.getElementById('createVolunteerPositionResult').textContent =
                    'Σφάλμα διακομιστή. Παρακαλώ προσπαθήστε ξανά.';
            }
        });
    }
});



async function fetchVolunteerRequests() {
    try {
        // Fetch the list of volunteer requests from the backend
        const response = await fetch('/A3_4739/admin/volunteerRequests');
        const requests = await response.json();

        // Get the container where requests will be displayed
        const container = document.getElementById('volunteerRequestsContainer');
        container.innerHTML = ''; // Clear the container

        // Loop through the requests and render each one
        requests.forEach((req) => {
            const div = document.createElement('div');
            div.innerHTML = `
                <p>Incident ID: ${req.incident_id}</p>
                <p>Volunteer ID: ${req.volunteer_id}</p>
                <p>Position: ${req.position_type}</p>
                <p>Status: ${req.status}</p>
                ${
                req.status === 'pending'
                    ? `<button onclick="updateRequest(${req.id}, 'approved')">Approve</button>
                           <button onclick="updateRequest(${req.id}, 'rejected')">Reject</button>`
                    : ''
            }
            `;
            container.appendChild(div);
        });
    } catch (error) {
        console.error('Error fetching volunteer requests:', error);
        alert('Failed to fetch volunteer requests. Please try again later.');
    }
}


async function updateRequest(requestId, newStatus) {
    try {
        const response = await fetch('/A3_4739/admin/volunteerRequests/update', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ id:requestId, status: newStatus }),
        });

        const result = await response.json();
        if (result.success) {
            alert('Request updated successfully!');
            fetchVolunteerRequests(); // Reload requests
        } else {
            alert(`Error: ${result.message}`);
        }
    } catch (error) {
        console.error('Error updating request:', error);
        alert('Server error. Please try again.');
    }
}
fetchVolunteerRequests();


async function populateIncidentTable() {
    try {
        const response = await fetch('/A3_4739/incidents/ids');
        if (!response.ok) throw new Error('Failed to fetch incident IDs.');
        const incidentIds = await response.json();

        const tableBody = document.getElementById('incidentTable').querySelector('tbody');
        tableBody.innerHTML = ''; // Clear existing rows

        incidentIds.forEach((id) => {
            const row = document.createElement('tr');

            const idCell = document.createElement('td');
            idCell.textContent = id;

            const actionCell = document.createElement('td');
            const selectButton = document.createElement('button');
            selectButton.textContent = 'Select';
            selectButton.addEventListener('click', () => {
                document.getElementById('selectedIncidentId').value = id;
                fetchMessages(); // Fetch messages for the selected incident
            });

            actionCell.appendChild(selectButton);
            row.appendChild(idCell);
            row.appendChild(actionCell);

            tableBody.appendChild(row);
        });
    } catch (error) {
        console.error('Error fetching incident IDs:', error);
        alert('Failed to load incident IDs. Please try again later.');
    }
}


async function fetchMessages() {
    const incidentId = document.getElementById('selectedIncidentId').value;
    if (!incidentId) {
        document.getElementById('messagesForIncident').innerHTML = '<p>Select an incident to view messages.</p>';
        return;
    }

    try {
        const response = await fetch(`/A3_4739/messages/get?incident_id=${incidentId}`);
        if (!response.ok) throw new Error('Failed to fetch messages.');

        const messages = await response.json();
        displayMessages(messages);
    } catch (error) {
        console.error('Error fetching messages:', error);
        alert('Failed to load messages. Please try again later.');
    }
}


function displayMessages(messages) {
    const container = document.getElementById('messagesForIncident');
    container.innerHTML = ''; // Clear previous messages

    if (messages.length === 0) {
        container.innerHTML = '<p>No messages for this incident.</p>';
        return;
    }

    messages.forEach((msg) => {
        const div = document.createElement('div');
        div.textContent = `[${msg.date_time}] ${msg.sender} -> ${msg.recipient}: ${msg.message}`;
        container.appendChild(div);
    });
}

async function sendMessage() {
    const incidentId = document.getElementById('selectedIncidentId').value;
    const messageText = document.getElementById('messageText').value.trim();
    const recipient = document.getElementById('recipient').value;

    if (!incidentId || !messageText) {
        alert('Incident and message text are required.');
        return;
    }

    try {
        const response = await fetch('/A3_4739/messages/send', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                incident_id: parseInt(incidentId),
                sender: 'admin', // Adjust as needed
                recipient,
                message: messageText,
            }),
        });

        const result = await response.json();
        if (result.success) {
            alert('Message sent successfully!');
            fetchMessages(); // Refresh messages
            document.getElementById('messageText').value = ''; // Clear the text area
        } else {
            alert('Error sending message: ' + result.message);
        }
    } catch (error) {
        console.error('Error sending message:', error);
        alert('Server error. Please try again.');
    }
}


window.onload = function () {
    populateIncidentTable();
};


google.charts.load('current', { packages: ['corechart', 'bar'] });
google.charts.setOnLoadCallback(drawCharts);

async function drawCharts() {
    await drawIncidentsByTypeChart();
    await drawVehiclesFiremenChart();
}

// 1. Incidents by Type
async function drawIncidentsByTypeChart() {
    try {
        const response = await fetch('/A3_4739/statistics/incidentsByType');
        if (!response.ok) throw new Error('Failed to fetch incidents by type.');
        const data = await response.json();

        const chartData = [['Incident Type', 'Count']];
        data.forEach(row => {
            chartData.push([row.incident_type, row.count]);
        });

        const googleData = google.visualization.arrayToDataTable(chartData);
        const options = { title: 'Incidents by Type', is3D: true };
        const chart = new google.visualization.PieChart(document.getElementById('piechart'));
        chart.draw(googleData, options);
    } catch (error) {
        console.error('Error drawing incidents by type chart:', error);
    }
}

// 2. Users and Volunteers
async function drawUsersVolunteersChart() {
    try {
        // Fetch data from the servlet
        const response = await fetch('/A3_4739/statistics/usersVolunteersCount');
        if (!response.ok) throw new Error('Failed to fetch user and volunteer counts.');
        const data = await response.json();

        // Format data for Google Charts
        const chartData = google.visualization.arrayToDataTable([
            ['Type', 'Count'],
            ['Users', data.total_users],
            ['Volunteers', data.total_volunteers],
        ]);

        // Define chart options
        const options = {
            title: 'Users and Volunteers Count',
            is3D: true, // Optional, can use is3D or flat pie chart
        };

        // Render the chart
        const chart = new google.visualization.PieChart(document.getElementById('usersVolunteersChart'));
        chart.draw(chartData, options);

    } catch (error) {
        console.error('Error fetching chart data:', error);
        alert('Failed to load chart. Please try again later.');
    }
}

// Load the Google Charts library and draw the chart
google.charts.load('current', { packages: ['corechart'] });
google.charts.setOnLoadCallback(drawUsersVolunteersChart);

// 3. Vehicles and Firemen
async function drawVehiclesFiremenChart() {
    try {
        const response = await fetch('/A3_4739/statistics/vehiclesFiremenCount');
        if (!response.ok) throw new Error('Failed to fetch vehicles and firemen count.');
        const data = await response.json();

        const chartData = google.visualization.arrayToDataTable([
            ['Category', 'Count'],
            ['Vehicles', data.total_vehicles],
            ['Firemen', data.total_firemen]
        ]);

        const options = { title: 'Vehicles and Firemen Participation', chartArea: { width: '50%' }, hAxis: { title: 'Count' } };
        const chart = new google.visualization.BarChart(document.getElementById('barchart_vehicles_firemen'));
        chart.draw(chartData, options);
    } catch (error) {
        console.error('Error drawing vehicles and firemen chart:', error);
    }
}


