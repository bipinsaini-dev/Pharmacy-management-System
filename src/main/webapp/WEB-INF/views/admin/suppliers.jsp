<c:set var="pageTitle" value="Suppliers" />
<c:set var="activeNav" value="suppliers" />
<%@ include file="/WEB-INF/views/common/header.jsp" %>
<div class="card mb-3"><div class="card-body">
  <form method="get" action="${ctx}/admin/suppliers" class="row g-2">
    <div class="col-12 col-md-6"><input type="search" class="form-control" name="q" value="<c:out value='${q}'/>" placeholder="Search suppliers"></div>
    <div class="col-12 col-md-6 d-flex gap-2">
      <button class="btn btn-primary" type="submit"><i class="bi bi-search me-1"></i>Search</button>
      <a class="btn btn-light" href="${ctx}/admin/suppliers">Reset</a>
      <button type="button" class="btn btn-success ms-auto" data-bs-toggle="modal" data-bs-target="#supplierModal" data-modal-title="Add supplier"><i class="bi bi-plus-lg me-1"></i>Add supplier</button>
    </div>
  </form>
</div></div>
<div class="card"><div class="table-responsive">
  <table class="table table-hover align-middle mb-0">
    <thead><tr><th>ID</th><th>Supplier</th><th>Contact person</th><th>Phone</th><th>Email</th><th>Address</th><th class="text-end">Actions</th></tr></thead>
    <tbody>
      <c:forEach var="s" items="${suppliers}">
        <tr>
          <td>${s.id}</td>
          <td class="fw-semibold"><c:out value="${s.supplierName}" /></td>
          <td><c:out value="${s.contactPerson}" default="-" /></td>
          <td><c:out value="${s.phone}" default="-" /></td>
          <td><c:out value="${s.email}" default="-" /></td>
          <td><c:out value="${s.address}" default="-" /></td>
          <td class="text-end text-nowrap">
            <button type="button" class="btn btn-sm btn-outline-primary" title="Edit" data-bs-toggle="modal" data-bs-target="#supplierModal"
                    data-modal-title="Edit supplier" data-id="${s.id}" data-supplier-name="<c:out value='${s.supplierName}'/>"
                    data-contact-person="<c:out value='${s.contactPerson}'/>" data-phone="<c:out value='${s.phone}'/>"
                    data-email="<c:out value='${s.email}'/>" data-address="<c:out value='${s.address}'/>"><i class="bi bi-pencil"></i></button>
            <button type="button" class="btn btn-sm btn-outline-danger" title="Delete" data-delete-url="${ctx}/admin/suppliers"
                    data-delete-id="${s.id}" data-delete-name="<c:out value='${s.supplierName}'/>"><i class="bi bi-trash"></i></button>
          </td>
        </tr>
      </c:forEach>
      <c:if test="${empty suppliers}"><tr><td colspan="7" class="text-center text-muted py-4">No suppliers found.</td></tr></c:if>
    </tbody>
  </table>
</div></div>

<div class="modal fade crud-modal" id="supplierModal" tabindex="-1" aria-hidden="true">
  <div class="modal-dialog modal-dialog-centered">
    <form class="modal-content needs-validation" method="post" action="${ctx}/admin/suppliers" novalidate>
      <input type="hidden" name="action" value="save">
      <input type="hidden" name="id" value="0">
      <div class="modal-header"><h5 class="modal-title">Supplier</h5><button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button></div>
      <div class="modal-body">
        <div class="mb-3"><label class="form-label">Supplier name *</label><input class="form-control" name="supplierName" required maxlength="120"><div class="invalid-feedback">Supplier name is required.</div></div>
        <div class="mb-3"><label class="form-label">Contact person</label><input class="form-control" name="contactPerson" maxlength="100"></div>
        <div class="row g-3 mb-3">
          <div class="col-md-6"><label class="form-label">Phone</label><input class="form-control" name="phone" pattern="\+?[0-9]{10,13}"><div class="invalid-feedback">Enter 10 to 13 digits.</div></div>
          <div class="col-md-6"><label class="form-label">Email</label><input type="email" class="form-control" name="email" maxlength="120"><div class="invalid-feedback">Enter a valid email.</div></div>
        </div>
        <div class="mb-0"><label class="form-label">Address</label><textarea class="form-control" name="address" rows="2" maxlength="255"></textarea></div>
      </div>
      <div class="modal-footer"><button type="button" class="btn btn-light" data-bs-dismiss="modal">Cancel</button><button type="submit" class="btn btn-primary">Save</button></div>
    </form>
  </div>
</div>
<%@ include file="/WEB-INF/views/common/footer.jsp" %>
