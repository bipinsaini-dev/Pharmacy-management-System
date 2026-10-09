<c:set var="pageTitle" value="404 - Page not found" />
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>404 | PharmaCare</title>
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
  <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css" rel="stylesheet">
  <link href="${ctx}/assets/css/style.css" rel="stylesheet">
</head>
<body class="auth-body">
<div class="auth-wrap">
  <div class="auth-card text-center">
    <div class="error-icon"><i class="bi bi-search"></i></div>
    <h1 class="display-5 fw-bold">404</h1>
    <h2 class="h5">Page not found</h2>
    <p class="text-muted">The page you are looking for does not exist or has been moved.</p>
    <a class="btn btn-primary" href="${ctx}/login"><i class="bi bi-house me-1"></i>Back to PharmaCare</a>
  </div>
</div>
</body>
</html>
