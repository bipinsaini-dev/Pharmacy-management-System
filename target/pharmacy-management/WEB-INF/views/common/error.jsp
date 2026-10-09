<c:set var="ctx" value="${pageContext.request.contextPath}" />
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Error | PharmaCare</title>
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
  <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css" rel="stylesheet">
  <link href="${ctx}/assets/css/style.css" rel="stylesheet">
</head>
<body class="auth-body">
<div class="auth-wrap">
  <div class="auth-card text-center">
    <div class="error-icon"><i class="bi bi-exclamation-circle"></i></div>
    <h2 class="h4">Oops, something went wrong</h2>
    <p class="text-muted">
      <c:choose>
        <c:when test="${not empty errorMessage}"><c:out value="${errorMessage}" /></c:when>
        <c:otherwise>We could not complete your request. Please go back and try again.</c:otherwise>
      </c:choose>
    </p>
    <a class="btn btn-primary" href="${ctx}/login">Back to PharmaCare</a>
  </div>
</div>
</body>
</html>
