let user = "${user.name}";
let allUsers = ${all.users};

document.querySelector("#search-form").addEventListener("submit", event => {
    event.preventDefault();
    searchMovies();
});

for (let i = 0; i < allUsers.length; i++) {
    let option = document.createElement("option");
    option.value = allUsers[i];
    option.innerHTML = allUsers[i];
    document.querySelector("#user-list").append(option);
}
document.querySelector("#user-list").value = user;

function addMovie(data) {
    let movieTitle = data["title"];

    let ele = document.createElement("div");
    ele.classList.add("movie");
    ele.style.backgroundImage = "url(https://image.tmdb.org/t/p/w500" + data["poster_path"] + ")";

    let clickIcon = "";
    if (pageId === "search") {
        clickIcon = "<a class='fav material-icons' onclick='addToFavorites(\x22" + data["id"] + "\x22)'>star_outline</a>";
    }

    let desc = document.createElement("div");
    desc.classList.add("movie-description");
    desc.innerHTML = movieTitle + "<br>" + clickIcon;
    ele.append(desc);

    document.querySelector(".movie-list").append(ele);
}

function newUser() {
    let newUserName = prompt("Enter name for the new user!");
    if (/[a-zA-Z]/.test(newUser)) {
        let blockRequest = false;

        let Http = new XMLHttpRequest();
        let url = ${application.url} + "/user/create?user=" + newUserName;
        Http.open("GET", url);
        Http.send();

        Http.onreadystatechange = (e) => {
            user = newUserName;
            goToHomePage();
        }
    }
}

function changeUser() {
    user = document.querySelector("#user-list").value;
    goToHomePage();
}

function searchMovies() {
    let query = document.querySelector(".search-bar").value.split(' ').join('+');
    window.location.replace(${application.url} + "/search?query=" + query + "&user=" + user);
}

function goToHomePage() {
    window.location.replace(${application.url} + "/?user=" + user);
}
