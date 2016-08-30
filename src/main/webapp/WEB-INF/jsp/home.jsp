<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html lang="en">
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Home Page</title>
    <link rel="stylesheet" type="text/css" href="/css/bootstrap.css">
    <link rel="stylesheet" type="text/css" href="/css/jumbotron-narrow.css">
    <link rel="stylesheet" type="text/css" href="/css/jquery.growl.css"/>
    <link rel="stylesheet" type="text/css" href="/css/home.css">
    <script src="http://code.jquery.com/jquery.js"></script>
    <script src="/js/jquery.growl.js" type="text/javascript"></script>
</head>

<body>

<div class="container">
    <div class="header clearfix">
        <nav>
            <ul class="nav nav-pills pull-right">
                <li class="active" id="home"><a href="#">Home</a></li>
                <c:if test="${passwordLogin}">
                    <li id="password-login"><a href="#">Password Login</a></li>
                </c:if>
                <li id="logout"><a href="#">Logout</a></li>
            </ul>
        </nav>
        <h3 class="text-muted">App.com</h3>
    </div>
    <c:if test="${passwordLogin}">
    <div class="alert alert-danger">
        <strong>Password Login</strong><br/><br/>You are requested to perform a one-off password verification of your current
        <strong>${email}</strong> email address against your existing <strong>${passwordConnection}</strong> database account.
        You do not need to logout and shall be automatically redirected back to this page once completed.
        Please click <a href="${fn:replace(pageContext.request.requestURL, pageContext.request.requestURI, '')}/portal/password">Password Login</a>
        to begin. We thank you for your co-operation.
    </div>
    </c:if>
    <div class="jumbotron">
        <h3>Hello ${user.name}!</h3>
        <p class="lead">Your nickname is: ${user.nickname}</p>
        <p class="lead">Your user id is: ${user.userId}</p>
        <p><img class="avatar" src="${user.picture}"/></p>
    </div>
    <c:choose>
    <c:when test="${passwordLogin}">
    <div class="row marketing">
        <div class="col-lg-6">
            <h4>Subheading</h4>
            <p>Morbi leo risus, porta ac consectetur ac, vestibulum at eros. Cras mattis consectetur purus sit amet
                fermentum.</p>

        </div>

        <div class="col-lg-6">
            <h4>Subheading</h4>
            <p>Donec id elit non mi porta gravida at eget metus. Maecenas faucibus mollis interdum.</p>

        </div>
    </div>
    </c:when>
    <c:otherwise>
    <div class="row marketing">
        <div class="col-lg-6">
            <h4>Subheading</h4>
            <p>Donec id elit non mi porta gravida at eget metus. Maecenas faucibus mollis interdum.</p>

            <h4>Subheading</h4>
            <p>Morbi leo risus, porta ac consectetur ac, vestibulum at eros. Cras mattis consectetur purus sit amet
                fermentum.</p>
        </div>

        <div class="col-lg-6">
            <h4>Subheading</h4>
            <p>Donec id elit non mi porta gravida at eget metus. Maecenas faucibus mollis interdum.</p>

            <h4>Subheading</h4>
            <p>Morbi leo risus, porta ac consectetur ac, vestibulum at eros. Cras mattis consectetur purus sit amet
                fermentum.</p>
        </div>
    </div>
    </c:otherwise>
    </c:choose>
    <footer class="footer">
        <p> &copy; 2016 Company Inc</p>
    </footer>
</div>

<script type="text/javascript">
    <c:if test="${passwordLogin}">
        $('#password-login').click(function () {
            $("#home").removeClass("active");
            $("#logout").removeClass("active");
            $("#password-login").addClass("active");
            window.location = "${fn:replace(pageContext.request.requestURL, pageContext.request.requestURI, '')}/portal/password";
        });
    </c:if>
    $("#logout").click(function(e) {
        e.preventDefault();
        $("#home").removeClass("active");
        $("#password-login").removeClass("active");
        $("#logout").addClass("active");
        // assumes we are not part of SSO so just logout of local session
        window.location = "${fn:replace(pageContext.request.requestURL, pageContext.request.requestURI, '')}/logout";
    });
</script>

</body>
</html>