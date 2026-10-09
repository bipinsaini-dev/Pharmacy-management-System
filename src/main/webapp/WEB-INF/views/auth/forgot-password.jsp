<c:set var="pageTitle" value="Forgot Password" />
<%@ include file="/WEB-INF/views/common/auth-header.jsp" %>
<h2 class="h5 mb-1">Reset your password</h2>
<p class="text-muted small">Enter your username and the email registered with it, then choose a new password.</p>
<form method="post" action="${ctx}/forgot-password" class="needs-validation" novalidate>
  <div class="mb-3">
    <label class="form-label" for="username">Username</label>
    <input type="text" class="form-control" id="username" name="username" value="<c:out value='${username}'/>" required>
    <div class="invalid-feedback">Username is required.</div>
  </div>
  <div class="mb-3">
    <label class="form-label" for="email">Registered email</label>
    <input type="email" class="form-control" id="email" name="email" value="<c:out value='${email}'/>" required>
    <div class="invalid-feedback">Enter your registered email.</div>
  </div>
  <div class="mb-3">
    <label class="form-label" for="password">New password</label>
    <input type="password" class="form-control" id="password" name="newPassword" required minlength="8" autocomplete="new-password">
    <div class="invalid-feedback">At least 8 characters.</div>
  </div>
  <div class="mb-3">
    <label class="form-label" for="confirmPassword">Confirm new password</label>
    <input type="password" class="form-control" id="confirmPassword" name="confirmPassword" required autocomplete="new-password">
    <div class="invalid-feedback">Passwords must match.</div>
  </div>
  <button type="submit" class="btn btn-primary w-100"><i class="bi bi-arrow-repeat me-1"></i>Reset password</button>
</form>
<p class="text-center mt-3 mb-0"><a href="${ctx}/login">Back to login</a></p>
<%@ include file="/WEB-INF/views/common/auth-footer.jsp" %>
