<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Home Page</title>
    <link rel="stylesheet" type="text/css" href="/css/bootstrap.css">
    <link rel="stylesheet" type="text/css" href="/css/jumbotron-narrow.css">
    <link rel="stylesheet" type="text/css" href="/css/jquery.growl.css"/>
    <link rel="stylesheet" type="text/css" href="/css/password.css">
    <script src="http://code.jquery.com/jquery.js"></script>
    <script src="https://cdn.auth0.com/js/auth0/9.0.0-beta.1/auth0.min.js"></script>
    <script src="/js/jquery.growl.js" type="text/javascript"></script>
</head>
<body>

<script type="text/javascript">
    var auth0Instance = new auth0.WebAuth({
        domain: '${domain}',
        clientID: '${clientId}',
        redirectUri: '${fn:replace(pageContext.request.requestURL, pageContext.request.requestURI, '')}' + '/plcallback',
        responseType: 'code',
        audience: 'https://' + '${domain}' + '/userinfo',
        scope: 'openid profile'

    });
</script>

<div class="container">
    <div class="header clearfix">
        <nav>
            <ul class="nav nav-pills pull-right">
                <li id="home"><a href="#">Home</a></li>
                <li id="password-login" class="active"><a href="#">Password Login</a></li>
                <li id="logout"><a href="#">Logout</a></li>
            </ul>
        </nav>
        <h3 class="text-muted">App.com</h3>
    </div>
    <div class="alert alert-info">
        <strong>Password Login</strong><br/><br/>You are requested to perform a one-off password verification of your current
        <strong>${email}</strong> email address against your existing <strong>${passwordConnection}</strong> database account.
        You will automatically be redirected back to
        <a href="${fn:replace(pageContext.request.requestURL, pageContext.request.requestURI, '')}/portal/home">Home</a>
        once done. We thank you for your co-operation.
    </div>
    <div class="jumbotron">
        <h3>${email}</h3>
        <div class="input-group">
            <input type="password" id="password" placeholder="password" class="form-control">
              <span class="input-group-btn">
                <button id="password-btn" class="btn btn-lg btn-primary btn-block">Go</button>
              </span>
        </div>

    </div>
    <div class="row marketing">
        <div class="col-lg-6">
            <h4>Subheading</h4>
            <p>Donec id elit non mi porta gravida at eget metus. Maecenas faucibus mollis interdum.</p>

            <h4>Subheading</h4>
            <p>Morbi leo risus, porta ac consectetur ac, vestibulum at eros. Cras mattis consectetur purus sit amet
                fermentum.</p>

            <h4>Subheading</h4>
            <p>Donec id elit non mi porta gravida at eget metus. Maecenas faucibus mollis interdum.</p>

        </div>

        <div class="col-lg-6">
            <h4>Subheading</h4>
            <p>Donec id elit non mi porta gravida at eget metus. Maecenas faucibus mollis interdum.</p>

            <h4>Subheading</h4>
            <p>Morbi leo risus, porta ac consectetur ac, vestibulum at eros. Cras mattis consectetur purus sit amet
                fermentum.</p>

            <h4>Subheading</h4>
            <p>Donec id elit non mi porta gravida at eget metus. Maecenas faucibus mollis interdum.</p>

        </div>
    </div>

    <footer class="footer">
        <p> &copy; 2016 Company Inc</p>
    </footer>

</div>

<script type="text/javascript">

    $(function() {
        $.growl({ title: "Link your profiles", message: "${user.name} - please link with your database profile"  });
    });

    $('#home').click(function () {
        $("#logout").removeClass("active");
        $("#password-login").removeClass("active");
        $("#home").addClass("active");
        window.location = "${fn:replace(pageContext.request.requestURL, pageContext.request.requestURI, '')}/portal/home";
    });

    $('#password-btn').click(function () {
        var password = $('#password').val();
        auth0Instance.login({
          realm: '${passwordConnection}',
          state: '${state}',
          username: '${email}',
          password: password
        }, function (err) {
            if (err) {
                console.error("Error: " + err.message);
                $.growl.warning({message: "Login Failed. Please check and try again."});
            }
        });
    });

    $("#logout").click(function (e) {
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
