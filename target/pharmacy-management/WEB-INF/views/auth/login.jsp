<c:set var="pageTitle" value="Login" />
<%@ include file="/WEB-INF/views/common/auth-header.jsp" %>
<h2 class="h5 mb-3">Sign in to your account</h2>
<form method="post" action="${ctx}/login" class="needs-validation" novalidate>
  <div class="mb-3">
    <label class="form-label" for="identifier">Username or email</label>
    <div class="input-group has-validation">
      <span class="input-group-text"><i class="bi bi-person"></i></span>
      <input type="text" class="form-control" id="identifier" name="identifier" value="<c:out value='${identifier}'/>" required autofocus autocomplete="username">
      <div class="invalid-feedback">Please enter your username or email.</div>
    </div>
  </div>
  <div class="mb-3">
    <label class="form-label" for="password">Password</label>
    <div class="input-group has-validation">
      <span class="input-group-text"><i class="bi bi-lock"></i></span>
      <input type="password" class="form-control" id="password" name="password" required autocomplete="current-password">
      <div class="invalid-feedback">Please enter your password.</div>
    </div>
  </div>
  <div class="d-flex justify-content-between align-items-center mb-3">
    <div class="form-check">
      <input class="form-check-input" type="checkbox" id="remember" name="remember" value="1" ${remember ? 'checked' : ''}>
      <label class="form-check-label" for="remember">Remember me</label>
    </div>
    <a href="${ctx}/forgot-password" class="small">Forgot password?</a>
  </div>
  <button type="submit" class="btn btn-primary w-100"><i class="bi bi-box-arrow-in-right me-1"></i>Login</button>
</form>
<p class="text-center mt-3 mb-0">New pharmacist? <a href="${ctx}/register">Create an account</a></p>
<%@ include file="/WEB-INF/views/common/auth-footer.jsp" %>
