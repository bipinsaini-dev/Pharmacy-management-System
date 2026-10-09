<c:set var="pageTitle" value="My Profile" />
<c:set var="activeNav" value="profile" />
<%@ include file="/WEB-INF/views/common/header.jsp" %>
<div class="row g-3">
  <div class="col-lg-5"><div class="card h-100">
    <div class="card-header">Account details</div>
    <div class="card-body">
      <dl class="row detail-list mb-0">
        <dt class="col-5">Full name</dt><dd class="col-7"><c:out value="${sessionScope.user.fullName}" /></dd>
        <dt class="col-5">Username</dt><dd class="col-7"><c:out value="${sessionScope.user.username}" /></dd>
        <dt class="col-5">Email</dt><dd class="col-7"><c:out value="${sessionScope.user.email}" /></dd>
        <dt class="col-5">Phone</dt><dd class="col-7"><c:out value="${sessionScope.user.phone}" /></dd>
        <dt class="col-5">Role</dt><dd class="col-7"><span class="badge text-bg-primary"><c:out value="${sessionScope.user.role}" /></span></dd>
      </dl>
    </div>
  </div></div>
  <div class="col-lg-7"><div class="card h-100">
    <div class="card-header">Change password</div>
    <div class="card-body">
      <form method="post" action="${ctx}/${base}/profile" class="needs-validation" novalidate>
        <div class="mb-3"><label class="form-label" for="currentPassword">Current password</label>
          <input type="password" class="form-control" id="currentPassword" name="currentPassword" required autocomplete="current-password">
          <div class="invalid-feedback">Enter your current password.</div></div>
        <div class="mb-3"><label class="form-label" for="password">New password</label>
          <input type="password" class="form-control" id="password" name="newPassword" required minlength="8" autocomplete="new-password">
          <div class="invalid-feedback">At least 8 characters.</div></div>
        <div class="mb-3"><label class="form-label" for="confirmPassword">Confirm new password</label>
          <input type="password" class="form-control" id="confirmPassword" name="confirmPassword" required autocomplete="new-password">
          <div class="invalid-feedback">Passwords must match.</div></div>
        <button class="btn btn-primary" type="submit"><i class="bi bi-key me-1"></i>Update password</button>
      </form>
    </div>
  </div></div>
</div>
<%@ include file="/WEB-INF/views/common/footer.jsp" %>
