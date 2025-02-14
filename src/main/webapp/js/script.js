document.getElementById('type').addEventListener('change', function() {
    const volunteerFields = document.getElementById('volunteerFields');
    const termsText = document.getElementById('termsText');
    const heightField = document.getElementById('height'); // Get the height field
    const weightField = document.getElementById('weight'); // Get the weight field

    if (this.value === 'volunteer') {
        volunteerFields.style.display = 'block';

        heightField.setAttribute('required', 'required');
        weightField.setAttribute('required', 'required');

        termsText.innerHTML = `Unnecessary use of the application is prohibited. I agree that unnecessary use will be prosecuted. I hereby declare that I am an active member of the volunteer firefighters.`;
    } else {
        volunteerFields.style.display = 'none';

        heightField.removeAttribute('required');
        weightField.removeAttribute('required');

        termsText.innerHTML = `
            Απαγορεύεται η άσκοπη χρήση της εφαρμογής. Συμφωνώ πως η άσκοπη χρήση της θα διώκεται ποινικά.
        `;
    }
});

// Password visibility toggle
document.getElementById('togglePassword').addEventListener('click', function() {
    const passwordInput = document.getElementById('password');
    const confirmPasswordInput = document.getElementById('confirm-password');
    const type = passwordInput.type === 'password' ? 'text' : 'password';
    passwordInput.type = type;
    confirmPasswordInput.type = type;
    this.textContent = type === 'password' ? 'Show' : 'Hide';
});


// Check password strength when button is clicked
document.getElementById('checkStrength').addEventListener('click', function() {
    const password = document.getElementById('password').value;
    const strengthMessage = document.getElementById('password-strength-message');

    const strength = checkPasswordStrength(password);
    strengthMessage.textContent = strength.charAt(0).toUpperCase() + strength.slice(1) + ' password';
});

// Function to check password strength
function checkPasswordStrength(password) {
    const prohibitedSequences = ["fire", "fotia", "ethelontis", "volunteer"];
    const lowercasePassword = password.toLowerCase();

    for (const sequence of prohibitedSequences) {
        if (lowercasePassword.includes(sequence)) {
            return "weak";
        }
    }

    let numCount = 0;
    let charCount = {};
    for (let i = 0; i < password.length; i++) {
        const char = password[i];
        if (!isNaN(char)) {
            numCount++;
        }
        charCount[char] = (charCount[char] || 0) + 1;
    }

    if (numCount / password.length >= 0.5) {
        return "weak";
    }

    for (const count of Object.values(charCount)) {
        if (count / password.length >= 0.5) {
            return "weak";
        }
    }

    const hasUppercase = /[A-Z]/.test(password);
    const hasLowercase = /[a-z]/.test(password);
    const hasNumber = /\d/.test(password);
    const hasSymbol = /[!@#$%^&*(),.?":{}|<>]/.test(password);

    if (hasUppercase && hasLowercase && hasNumber && hasSymbol) {
        return "strong";
    }

    return "medium";
}

let isAddressVerified = false;
let latitude = '';
let longitude = '';

//VERIFY ADDRESS FUNCTION
document.getElementById('verifyAddress').addEventListener('click', function() {
    isAddressVerified = false;
    latitude = '';
    longitude = '';
    document.getElementById('latitude').value = '';
    document.getElementById('longitude').value = '';
    document.getElementById('displayMapButton').style.display = 'none';
    document.getElementById('mapContainer').style.display = 'none';

    const countrySelect = document.getElementById('country');
    const country = countrySelect.options[countrySelect.selectedIndex].text.trim();
    const municipality = document.getElementById('municipality').value.trim();
    const address = document.getElementById('address').value.trim();

    document.getElementById('address-error').style.color = 'blue';
    document.getElementById('address-error').textContent = 'Επαλήθευση διεύθυνσης...';

    const fullAddress = `${address}, ${municipality}, ${country}`;
    console.log("Checking address:", fullAddress);

    const xhr = new XMLHttpRequest();
    xhr.withCredentials = true;

    xhr.open('GET', "https://forward-reverse-geocoding.p.rapidapi.com/v1/search?q=" + fullAddress + "&accept-language=en&limit=5");
    xhr.setRequestHeader('x-rapidapi-key', '53988821camshb9974f9da3a8129p177f1ajsna387cc530843'); // Replace with your actual API key
    xhr.setRequestHeader('x-rapidapi-host', 'forward-reverse-geocoding.p.rapidapi.com');

    xhr.addEventListener("readystatechange", function () {
        if (this.readyState === this.DONE) {
            console.log("Response received:", this.responseText);
            try {
                const data = JSON.parse(this.responseText);

                if (data && data.length > 0) {
                    const result = data[0];
                    const displayName = result.display_name;
                    console.log("Parsed Display Name:", displayName);

                    const cretePrefectures = /Chania|Χανιά|Heraklion|Ηράκλειο|Rethymno|Ρέθυμνο|Lasithi|Λασίθι|Crete|Κρήτη/;
                    if (cretePrefectures.test(displayName)) {
                        isAddressVerified = true;
                        latitude = result.lat;
                        longitude = result.lon;
                        document.getElementById('latitude').value = latitude;
                        document.getElementById('longitude').value = longitude;

                        document.getElementById('address-error').style.color = 'green';
                        document.getElementById('address-error').textContent = 'Η διεύθυνση επαληθεύτηκε επιτυχώς!';
                        document.getElementById('displayMapButton').style.display = 'inline-block'; // Show the "Display Map" button
                    } else {
                        document.getElementById('address-error').style.color = 'red';
                        document.getElementById('address-error').textContent = 'Η υπηρεσία είναι διαθέσιμη μόνο στην Κρήτη αυτή τη στιγμή.';
                    }
                } else {
                    document.getElementById('address-error').style.color = 'red';
                    document.getElementById('address-error').textContent = 'Μη έγκυρη διεύθυνση. Παρακαλώ ελέγξτε τα στοιχεία και δοκιμάστε ξανά.';
                }
            } catch (error) {
                console.error('Error parsing response:', error);
                document.getElementById('address-error').style.color = 'red';
                document.getElementById('address-error').textContent = 'Σφάλμα κατά την επαλήθευση της διεύθυνσης.';
            }
        }
    });

    xhr.onerror = function () {
        document.getElementById('address-error').style.color = 'red';
        document.getElementById('address-error').textContent = 'Σφάλμα δικτύου κατά την επαλήθευση της διεύθυνσης.';
        console.error('Network error occurred.');
    };

    xhr.send();
});


document.getElementById('displayMapButton').addEventListener('click', function() {
    console.log("Display Map button clicked");
    displayMap(latitude, longitude);
});

let map;
//FUNCTION TO DISPLAY THE MAP
function displayMap(lat, lon) {
    console.log("Displaying map with coordinates:", lat, lon);

    const mapContainer = document.getElementById('mapContainer');
    mapContainer.style.display = 'block';

    if (!map) {
        map = new ol.Map({
            target: 'mapContainer',
            layers: [
                new ol.layer.Tile({
                    source: new ol.source.OSM()
                })
            ],
            view: new ol.View({
                center: ol.proj.fromLonLat([parseFloat(lon), parseFloat(lat)]),
                zoom: 14
            })
        });
    } else {
        map.getView().setCenter(ol.proj.fromLonLat([parseFloat(lon), parseFloat(lat)]));
        map.getView().setZoom(14);
    }

    map.getOverlays().clear();

    const markerElement = document.createElement('div');
    markerElement.className = 'map-marker';

    const marker = new ol.Overlay({
        position: ol.proj.fromLonLat([parseFloat(lon), parseFloat(lat)]),
        positioning: 'center-center',
        element: markerElement,
        stopEvent: false
    });

    map.addOverlay(marker);

    console.log("Map initialized at:", lat, lon);
}

// Show or hide the map button based on address verification
document.getElementById('verifyAddress').addEventListener('click', function() {
    if (isAddressVerified) {
        document.getElementById('showMap').style.display = 'inline-block';
    } else {
        document.getElementById('showMap').style.display = 'none';
        document.getElementById('mapContainer').style.display = 'none';
    }
});

document.getElementById('registrationForm').addEventListener('submit', async function (event) {
    event.preventDefault();

    const formData = new FormData(this);
    const userType = formData.get('type');
    const targetURL = userType === 'volunteer' ? '/A3_4739/saveVolunteer' : '/A3_4739/saveUser';

    const jsonData = {};
    formData.forEach((value, key) => {
        jsonData[key] = value;
    });

    //AJAX REQUEST
    try {
        const response = await fetch(targetURL, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(jsonData),
        });

        let data;
        try {
            data = await response.json();
        } catch (err) {
            console.error('Response is not JSON:', err);
            throw new Error('The server response is not valid JSON.');
        }

        if (response.ok && data.success) {
            window.location.href = "/A3_4739/success.html";
        } else {
            window.location.href = "/A3_4739/error.html";
        }
    } catch (error) {
        console.error('Error:', error);
        window.location.href = "/A3_4739/error.html";
    }

});





