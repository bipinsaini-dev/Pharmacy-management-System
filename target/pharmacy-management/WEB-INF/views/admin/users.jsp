<c:set var="pageTitle" value="Users" />
<c:set var="activeNav" value="users" />
<%@ include file="/WEB-INF/views/common/header.jsp" %>
<div class="card mb-3"><div class="card-body">
  <form method="get" action="${ctx}/admin/users" class="row g-2">
    <div class="col-12 col-md-6"><input type="search" class="form-control" name="q" value="<c:out value='${q}'/>" placeholder="Search by name, username, email, phone or role"></div>
    <div class="col-12 col-md-6 d-flex gap-2">
      <button class="btn btn-primary" type="submit"><i class="bi bi-search me-1"></i>Search</button>
      <a class="btn btn-light" href="${ctx}/admin/users">Reset</a>
      <button type="button" class="btn btn-success ms-auto" data-bs-toggle="modal" data-bs-target="#userModal" data-modal-title="Add user"><i class="bi bi-person-plus me-1"></i>Add user</button>
    </div>
  </form>
</div></div>
<div class="card"><div class="table-responsive">
  <table class="table table-hover align-middle mb-0">
    <thead><tr><th>ID</th><th>Name</th><th>Username</th><th>Email</th><th>Phone</th><th>Role</th><th>Status</th><th>Joined</th><th class="text-end">Actions</th></tr></thead>
    <tbody>
      <c:forEach var="u" items="${users}">
        <tr>
          <td>${u.id}</td>
          <td class="fw-semibold"><c:out value="${u.fullName}" /></td>
          <td><c:out value="${u.username}" /></td>
          <td><c:out value="${u.email}" /></td>
          <td><c:out value="${u.phone}" /></td>
          <td><span class="badge ${u.admin ? 'text-bg-primary' : 'text-bg-info'}"><c:out value="${u.role}" /></span></td>
          <td><span class="badge ${u.active ? 'bg-success' : 'bg-secondary'}"><c:out value="${u.status}" /></span></td>
          <td><fmt:formatDate value="${u.createdAt}" pattern="dd MMM yyyy" /></td>
          <td class="text-end text-nowrap">
            <button type="button" class="btn btn-sm btn-outline-primary" title="Edit" data-bs-toggle="modal" data-bs-target="#userModal"
                    data-modal-title="Edit user (leave password blank to keep it)" data-id="${u.id}" data-full-name="<c:out value='${u.fullName}'/>"
                    data-username="<c:out value='${u.username}'/>" data-email="<c:out value='${u.email}'/>" data-phone="<c:out value='${u.phone}'/>"
                    data-role="${u.role}" data-status="${u.status}"><i class="bi bi-pencil"></i></button>
            <c:if test="${u.id != sessionScope.user.id}">
              <button type="button" class="btn btn-sm btn-outline-danger" title="Delete" data-delete-url="${ctx}/admin/users"
                      data-delete-id="${u.id}" data-delete-name="<c:out value='${u.fullName}'/>"><i class="bi bi-trash"></i></button>
            </c:if>
          </td>
        </tr>
      </c:forEach>
      <c:if test="${empty users}"><tr><td colspan="9" class="text-center text-muted py-4">No users found.</td></tr></c:if>
    </tbody>
  </table>
</div></div>

<div class="modal fade crud-modal" id="userModal" tabindex="-1" aria-hidden="true">
  <div class="modal-dialog modal-dialog-centered modal-lg">
    <form class="modal-content needs-validation" method="post" action="${ctx}/admin/users" novalidate>
      <input type="hidden" name="action" value="save">
      <input type="hidden" name="id" value="0">
      <div class="modal-header"><h5 class="modal-title">User</h5><button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button></div>
      <div class="modal-body">
        <div class="row g-3">
          <div class="col-md-6"><label class="form-label">Full name *</label><input class="form-control" name="fullName" required minlength="3" maxlength="100"><div class="invalid-feedback">Required (min 3 characters).</div></div>
          <div class="col-md-6"><label class="form-label">Username *</label><input class="form-control" name="username" required pattern="[A-Za-z0-9_.]{3,30}"><div class="invalid-feedback">3-30 letters, digits, _ or .</div></div>
          <div class="col-md-6"><label class="form-label">Email *</label><input type="email" class="form-control" name="email" required maxlength="120"><div class="invalid-feedback">Enter a valid email.</div></div>
          <div class="col-md-6"><label class="form-label">Phone *</label><input class="form-control" name="phone" required pattern="\+?[0-9]{10,13}"><div class="invalid-feedback">Enter 10 to 13 digits.</div></div>
          <div class="col-md-6"><label class="form-label">Role</label><select class="form-select" name="role"><option value="PHARMACIST">Pharmacist</option><option value="ADMIN">Admin</option></select></div>
          <div class="col-md-6"><label class="form-label">Status</label><select class="form-select" name="status"><option value="ACTIVE">Active</option><option value="INACTIVE">Inactive</option></select></div>
          <div class="col-md-6"><label class="form-label">Password</label><input type="password" class="form-control" name="password" id="password" minlength="8" autocomplete="new-password"><div class="form-text">Required for new users, min 8 characters.</div></div>
          <div class="col-md-6"><label class="form-label">Confirm password</label><input type="password" class="form-control" name="confirmPassword" id="confirmPassword" autocomplete="new-password"></div>
        </div>
      </div>
      <div class="modal-footer"><button type="button" class="btn btn-light" data-bs-dismiss="modal">Cancel</button><button type="submit" class="btn btn-primary">Save</button></div>
    </form>
  </div>
</div>
<%@ include file="/WEB-INF/views/common/footer.jsp" %>
