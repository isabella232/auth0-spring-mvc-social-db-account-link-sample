<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="content-type" content="text/html; charset=utf-8"/>
    <title>Login</title>
    <script src="http://code.jquery.com/jquery.js"></script>
    <script src="http://cdn.auth0.com/js/lock/11.0.0-beta.1/lock.min.js"></script>
    <script src="/js/jquery.growl.js" type="text/javascript"></script>
    <link rel="stylesheet" type="text/css" href="/css/bootstrap.css"/>
    <link rel="stylesheet" type="text/css" href="/css/jquery.growl.css"/>
</head>
<body>

<div class="container">

    <script type="text/javascript">

        $(function () {
            var error = ${error};
            if (error) {
                $.growl.error({message: "An error was detected. Please log in"});
            } else {
                $.growl({title: "Welcome!", message: "Please log in"});
            }
        });

        $(function () {
            var lock = new Auth0Lock('${clientId}', '${domain}', {
                auth: {
                    redirectUrl: '${fn:replace(pageContext.request.requestURL, pageContext.request.requestURI, '')}' + '/callback',
                    responseType: 'code',
                    params: {
                      scope: 'openid name email picture',
                      state: '${state}'
                    }
                }
            });
            setTimeout(function () {
                lock.show({allowedConnections: ['${socialConnection}', '${passwordConnection}']});
            }, 1500);
        });

    </script>

</div>
</body>
</html>
