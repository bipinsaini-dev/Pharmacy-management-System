<%-- Frame for public pages (login, register, forgot password). Set pageTitle first. --%>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title><c:out value="${pageTitle}" /> | PharmaCare</title>
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
  <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css" rel="stylesheet">
  <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">
  <link href="${ctx}/assets/css/style.css" rel="stylesheet">
</head>
<body class="auth-body">
<div class="auth-wrap">
  <div class="auth-card">
    <div class="auth-brand"><i class="bi bi-capsule-pill"></i> PharmaCare</div>
    <p class="text-center text-muted mb-4">Pharmacy Management System</p>
    <%@ include file="messages.jsp" %>
