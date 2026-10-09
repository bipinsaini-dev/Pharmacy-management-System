<c:set var="pageTitle" value="Register" />
<%@ include file="/WEB-INF/views/common/auth-header.jsp" %>
<h2 class="h5 mb-3">Create a pharmacist account</h2>
<form method="post" action="${ctx}/register" class="needs-validation" novalidate>
  <div class="mb-3">
    <label class="form-label" for="fullName">Full name</label>
    <input type="text" class="form-control" id="fullName" name="fullName" value="<c:out value='${form.fullName}'/>" required minlength="3" maxlength="100">
    <div class="invalid-feedback">Full name is required (min 3 characters).</div>
  </div>
  <div class="row g-3 mb-3">
    <div class="col-md-6">
      <label class="form-label" for="email">Email</label>
      <input type="email" class="form-control" id="email" name="email" value="<c:out value='${form.email}'/>" required maxlength="120">
      <div class="invalid-feedback">Enter a valid email address.</div>
    </div>
    <div class="col-md-6">
      <label class="form-label" for="phone">Phone</label>
      <input type="tel" class="form-control" id="phone" name="phone" value="<c:out value='${form.phone}'/>" required pattern="\+?[0-9]{10,13}" placeholder="9876543210">
      <div class="invalid-feedback">Enter 10 to 13 digits.</div>
    </div>
  </div>
  <div class="row g-3 mb-3">
    <div class="col-md-6">
      <label class="form-label" for="username">Username</label>
      <input type="text" class="form-control" id="username" name="username" value="<c:out value='${form.username}'/>" required pattern="[A-Za-z0-9_.]{3,30}" autocomplete="username">
      <div class="invalid-feedback">3-30 letters, digits, _ or .</div>
    </div>
    <div class="col-md-6">
      <label class="form-label" for="role">Role</label>
      <select class="form-select" id="role" disabled>
        <option selected>Pharmacist</option>
      </select>
      <div class="form-text">Admin accounts are created by an administrator.</div>
    </div>
  </div>
  <div class="row g-3 mb-3">
    <div class="col-md-6">
      <label class="form-label" for="password">Password</label>
      <input type="password" class="form-control" id="password" name="password" required minlength="8" autocomplete="new-password">
      <div class="invalid-feedback">At least 8 characters.</div>
    </div>
    <div class="col-md-6">
      <label class="form-label" for="confirmPassword">Confirm password</label>
      <input type="password" class="form-control" id="confirmPassword" name="confirmPassword" required autocomplete="new-password">
      <div class="invalid-feedback">Passwords must match.</div>
    </div>
  </div>
  <button type="submit" class="btn btn-primary w-100"><i class="bi bi-person-plus me-1"></i>Register</button>
</form>
<p class="text-center mt-3 mb-0">Already registered? <a href="${ctx}/login">Login</a></p>
<%@ include file="/WEB-INF/views/common/auth-footer.jsp" %>
