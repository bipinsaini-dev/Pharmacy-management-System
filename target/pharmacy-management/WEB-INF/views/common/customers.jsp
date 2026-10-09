<c:set var="pageTitle" value="Customers" />
<c:set var="activeNav" value="customers" />
<%@ include file="/WEB-INF/views/common/header.jsp" %>
<div class="card mb-3"><div class="card-body">
  <form method="get" action="${ctx}/${base}/customers" class="row g-2">
    <div class="col-12 col-md-6"><input type="search" class="form-control" name="q" value="<c:out value='${q}'/>" placeholder="Search by name, phone, email or address"></div>
    <div class="col-12 col-md-6 d-flex gap-2">
      <button class="btn btn-primary" type="submit"><i class="bi bi-search me-1"></i>Search</button>
      <a class="btn btn-light" href="${ctx}/${base}/customers">Reset</a>
      <button type="button" class="btn btn-success ms-auto" data-bs-toggle="modal" data-bs-target="#customerModal" data-modal-title="Add customer"><i class="bi bi-plus-lg me-1"></i>Add customer</button>
    </div>
  </form>
</div></div>

<div class="card">
  <div class="table-responsive">
    <table class="table table-hover align-middle mb-0">
      <thead><tr><th>ID</th><th>Name</th><th>Phone</th><th>Email</th><th>Address</th><th class="text-end">Actions</th></tr></thead>
      <tbody>
        <c:forEach var="c" items="${customers}">
          <tr>
            <td>${c.id}</td>
            <td class="fw-semibold"><c:out value="${c.customerName}" /></td>
            <td><c:out value="${c.phone}" default="-" /></td>
            <td><c:out value="${c.email}" default="-" /></td>
            <td><c:out value="${c.address}" default="-" /></td>
            <td class="text-end text-nowrap">
              <a class="btn btn-sm btn-outline-secondary" title="View history" href="${ctx}/${base}/customers?action=view&id=${c.id}"><i class="bi bi-clock-history"></i></a>
              <c:if test="${c.id != walkInId}">
                <button type="button" class="btn btn-sm btn-outline-primary" title="Edit" data-bs-toggle="modal" data-bs-target="#customerModal"
                        data-modal-title="Edit customer" data-id="${c.id}" data-customer-name="<c:out value='${c.customerName}'/>"
                        data-phone="<c:out value='${c.phone}'/>" data-email="<c:out value='${c.email}'/>" data-address="<c:out value='${c.address}'/>"><i class="bi bi-pencil"></i></button>
                <button type="button" class="btn btn-sm btn-outline-danger" title="Delete" data-delete-url="${ctx}/${base}/customers"
                        data-delete-id="${c.id}" data-delete-name="<c:out value='${c.customerName}'/>"><i class="bi bi-trash"></i></button>
              </c:if>
            </td>
          </tr>
        </c:forEach>
        <c:if test="${empty customers}"><tr><td colspan="6" class="text-center text-muted py-4">No customers found.</td></tr></c:if>
      </tbody>
    </table>
  </div>
</div>

<div class="modal fade crud-modal" id="customerModal" tabindex="-1" aria-hidden="true">
  <div class="modal-dialog modal-dialog-centered">
    <form class="modal-content needs-validation" method="post" action="${ctx}/${base}/customers" novalidate>
      <input type="hidden" name="action" value="save">
      <input type="hidden" name="id" value="0">
      <div class="modal-header"><h5 class="modal-title">Customer</h5><button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button></div>
      <div class="modal-body">
        <div class="mb-3"><label class="form-label">Name *</label><input class="form-control" name="customerName" required maxlength="100"><div class="invalid-feedback">Name is required.</div></div>
        <div class="mb-3"><label class="form-label">Phone</label><input class="form-control" name="phone" pattern="\+?[0-9]{10,13}" placeholder="9876543210"><div class="invalid-feedback">Enter 10 to 13 digits.</div></div>
        <div class="mb-3"><label class="form-label">Email</label><input type="email" class="form-control" name="email" maxlength="120"><div class="invalid-feedback">Enter a valid email.</div></div>
        <div class="mb-0"><label class="form-label">Address</label><textarea class="form-control" name="address" rows="2" maxlength="255"></textarea></div>
      </div>
      <div class="modal-footer"><button type="button" class="btn btn-light" data-bs-dismiss="modal">Cancel</button><button type="submit" class="btn btn-primary">Save</button></div>
    </form>
  </div>
</div>
<%@ include file="/WEB-INF/views/common/footer.jsp" %>
