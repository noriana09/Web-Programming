function logout(){
    var xhr = new XMLHttpRequest();
    xhr.onload = function () {
        if(xhr.readyState === 4 && xhr.status === 200){
            console.log("Successful Logout");
            window.location.replace("loginALL.html");
        }else if(xhr.status !== 200){
            console.log('Request failed. Returned status of ' + xhr.status);
        }
    };

    xhr.open('POST', 'Logout');
    xhr.setRequestHeader('Content-type','application/x-www-form-urlencoded');
    xhr.send();
}